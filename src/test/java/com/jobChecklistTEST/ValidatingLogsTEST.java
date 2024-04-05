package com.jobChecklistTEST;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ValidatingLogsTEST implements ActionListener {

	JFrame frame;
	JPanel panel;
	JFormattedTextField field1;
	JPasswordField field2;
	JLabel lab0, lab1, lab2;
	String userName;
	char[] passwordEntered;
	JButton button;
	String host[] = { "dc04plvbuc300", "dc04plvbuc301" };
	int port = 22;
	String shift;
	String shiftStartTime, shiftEndTime;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	TimeZone zone = TimeZone.getTimeZone("America/New_York");
	JobNameIdentifierTEST jobIdnt = new JobNameIdentifierTEST();
	Map<String, String> logNmFndr = jobIdnt.jobAndLogNameFinder();
	boolean isCompleted = false;
	
	AppTEST app = new AppTEST();
	ExcelWriterTEST xl = new ExcelWriterTEST();
	
	public void planWindow() {
		frame = new JFrame("Enter the password");
		panel = new JPanel(null);
		frame.add(panel);
		field1 = new JFormattedTextField();
		field2 = new JPasswordField();
		lab0 = new JLabel("Domain ID");
		lab1 = new JLabel("Password:");
		lab2 = new JLabel(host[0].toUpperCase() + "/" + host[1].substring(10));
		button = new JButton("Submit");
		lab2.setBounds(48, 28, 200, 30);
		lab1.setBounds(48, 88, 110, 30);
		lab0.setBounds(48, 55, 200, 30);
		field1.setBounds(120, 55, 200, 30);
		field2.setBounds(120, 88, 200, 30);
		button.setBounds(110, 130, 150, 30);
		panel.add(lab0);
		panel.add(lab1);
		panel.add(lab2);
		panel.add(field1);
		panel.add(field2);
		panel.add(button);

		button.addActionListener(this);
		frame.setSize(400, 250);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e) {
		userName = field1.getText().toUpperCase();
		passwordEntered = field2.getPassword();
		String password = new String(passwordEntered);
		String batchFldrPath = "/apps/sclc/batch/";
		// int i = 0;
		Map<String, ArrayList<String>> logMapper = new HashMap<>();
		for (int i = 0; i < host.length; i++) {
			try {
				FileWriter errorLogFile = new FileWriter(app.detailedErrorsLoc + host[i].toUpperCase() + ".txt");
				JSch jsch = new JSch();
				Session session = jsch.getSession(userName, host[i], port);
				session.setPassword(password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
				System.out.println("Connection successful - " + host[i]);
				Channel channel = session.openChannel("sftp");
				channel.connect();
				ChannelSftp sftp = (ChannelSftp) channel;
				sftp.cd(batchFldrPath);
				System.out.println("Currently in location - " + sftp.pwd());
				// to get all the folder names
				Vector<LsEntry> fldrsList = sftp.ls(batchFldrPath);
				ArrayList<String> logDates = findLogDate(shift);
				for (LsEntry j : fldrsList) {
					String folderName = j.toString().substring(60);
					if (Pattern.matches("[a-zA-z]+", folderName)) {
						System.out.println("Checking the folder - " + folderName);
						String crntFldrPath = batchFldrPath + folderName + "/logs/";
						try {
							sftp.cd(crntFldrPath);
							System.out.println("Change directory to -->" + sftp.pwd());
							Vector<LsEntry> logsList = sftp.ls(crntFldrPath);
							InputStream stream = null;
							BufferedReader br = null;
							String logFileName = null, logNameWtimeStamp = null, timeStamp = null;
							int maxLen;
							for (int x = 0; x < logDates.size(); x++) {
								for (LsEntry log : logsList) {
									ArrayList<String> errorList = null;
									logNameWtimeStamp = log.toString().substring(60);
									if (logNameWtimeStamp.contains(logDates.get(x).toString())) {
										timeStamp = logNameWtimeStamp.substring(
												logNameWtimeStamp.indexOf(shiftStartTime.substring(0, 4)),
												logNameWtimeStamp.indexOf("."));
										if (isLogCretdInGivnShift(timeStamp)) {
											logFileName = logNameWtimeStamp.substring(0,
													logNameWtimeStamp.indexOf(timeStamp));
											if (logFileName.contains("_")) {
												logFileName = logFileName.substring(0, logFileName.length() - 1);
											}
											System.out.println(log.toString());
											stream = sftp.get(crntFldrPath + logNameWtimeStamp);
											br = new BufferedReader(new InputStreamReader(stream));
											errorLogFile.write("-------------Checking Logs for - " + logFileName
													+ "-------------\n");
											boolean isErrExist = false;
											String line;
											String errorLine;
											if(logNmFndr.containsKey(logFileName))
												logFileName = logNmFndr.get(logFileName);
											if (logMapper.containsKey(logFileName))
												errorList = logMapper.get(logFileName);
											else
												errorList = new ArrayList<>();
												logMapper.put(logFileName, errorList);
											
											while ((line = br.readLine()) != null) {
												if (line.contains("ERROR")) {
													// System.out.println(line);
													// if condition to limit total characters to 300 per line of error
													if (line.length() > 300)
														maxLen = 300;
													else
														maxLen = line.length();
													errorLine = line.substring(line.indexOf("ERROR"), maxLen);
													if (!errorList.contains(errorLine)) {
														errorList.add(errorLine);
														logMapper.put(logFileName, errorList);
													}
													errorLogFile.write(errorLine + "\n");
													isErrExist = true;
												}
											}
											if (!isErrExist)
												errorLogFile.write("No errors present");
											errorLogFile.write("\n");
											stream.close();
										}
									}
								}
							}
						} catch (Exception exc) {
							if (exc.getMessage().contains("No such file")) {
								System.out.println("**No log folder found inside this folder**");
								continue;
							}
						}
					}
				}
				sftp.disconnect();
				channel.disconnect();
				session.disconnect();
				System.out.println("Disconnected from server - " + host[i]);
				errorLogFile.flush();
				errorLogFile.close();
				System.out.println("File written successfully");
			} catch (Exception ex) {
				System.out.println("An error occured");
				ex.printStackTrace();
			}
		}
		logMapper.forEach((key, value) -> System.out.println(key.toString() + " : " + value));
		xl.writeLogs(logMapper);
		System.out.println("Error logs written in file successfully");
		System.out.println("***************Completed to fill the job logs***************");
		frame.dispose();
	}


	public static ArrayList<String> findLogDate(String shift) {
		Date dateNtime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		// Set TimeZone is working
		String timeStampTdy, timeStampYest;
		Calendar cal = new GregorianCalendar(Integer.parseInt(formatter.format(dateNtime).substring(0, 4)),
				Integer.parseInt(formatter.format(dateNtime).substring(4, 6)) - 1,
				Integer.parseInt(formatter.format(dateNtime).substring(6)));
		cal.add(Calendar.DATE, -1);
		ArrayList<String> TS = new ArrayList<>();
		timeStampTdy = formatter.format(dateNtime);
		timeStampYest = formatter.format(cal.getTime());
		if (shift.equalsIgnoreCase("s1")) {
			TS.add(timeStampYest);
			TS.add(timeStampTdy);
		} else {
			TS.add(timeStampTdy);
		}
		return TS;
	}// function working perfectly returning expected dates

	public boolean isLogCretdInGivnShift(String logTimeStamp) throws ParseException {
		String year = logTimeStamp.substring(0, 4);
		String month = logTimeStamp.substring(4, 6);
		String day = logTimeStamp.substring(6, 8);
		String hour = logTimeStamp.substring(8, 10);
		String min = logTimeStamp.substring(10, 12);
		String gvnLogCrtdTime = year + "-" + month + "-" + day + " " + hour + ":" + min;
		SimpleDateFormat sfd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sfd2.setTimeZone(zone);
		if (sfd2.parse(gvnLogCrtdTime).after(sfd2.parse(shiftStartTime))
				&& sfd2.parse(gvnLogCrtdTime).before(sfd2.parse(shiftEndTime))) {
			System.out.println("yes, within time");
			return true;
		} else {
			System.out.println("not within time");
			return false;
		}
	}

	public static void main(String args) {
		ValidatingLogsTEST pt = new ValidatingLogsTEST();
		/*Scanner scan = new Scanner(System.in);
		boolean corectInp = false;
		System.out.println("Enter the shift you're in: ");
		while (!corectInp) {
			pt.shift = scan.next().toUpperCase();
			if (pt.shift.equals("S1") || pt.shift.equals("S2") || pt.shift.equals("S3")) {
				corectInp = true;
				scan.close();
			} else {
				System.out.println("Enter a correct input-");
			}
		}*/
		pt.shift = args;
		pt.sdf.setTimeZone(pt.zone);
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(pt.zone);
		switch (pt.shift) {
		case "S1":
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 05:30";
			cal.add(Calendar.DATE, -1);
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 20:30";
			break;
		case "S2":
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 04:30";
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 13:30";
			break;
		case "S3":
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 12:00";
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 21:00";
			break;
		default:
			break;
		}
		System.out.println("Shift timings: " + pt.shiftStartTime + " to " + pt.shiftEndTime);
		pt.planWindow();
	}

}
