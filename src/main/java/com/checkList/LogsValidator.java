package com.checkList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
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
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class LogsValidator implements ActionListener {
	JFrame frame;
	JPanel panel;
	JFormattedTextField field1;
	JPasswordField field2;
	JLabel lab0, lab1, lab2;
	String userName;
	char[] passwordEntered;
	JButton button;
	String host[] = { "dc04plvbuc300", "dc04plvbuc301", "va10puvbas002" };
	int port = 22;
	String shift;
	String shiftStartTime, shiftEndTime;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	TimeZone zone = TimeZone.getTimeZone("America/New_York");
	JobNameIdentifier jobIdnt = new JobNameIdentifier();
	Map<String, String> logNmFndr = jobIdnt.jobAndLogNameFinder();
	boolean isCompleted = false;

	App app = new App();
	ExcelWriter xl = new ExcelWriter();
	SalesEventsFileChecker slsEvntChk = new SalesEventsFileChecker();

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
				ArrayList<String> logDates = findLogDate(shift);
				int logNameStartIndexNum;
				if (!host[i].equalsIgnoreCase("va10puvbas002")) {
					sftp.cd(batchFldrPath);
					System.out.println("Currently in location - " + sftp.pwd());
					logNameStartIndexNum = 60;
					// to get all the folder names
					Vector<LsEntry> fldrsList = sftp.ls(batchFldrPath);
					// should get the folders list from the Map
					for (LsEntry j : fldrsList) {
						/*
						 * Trimming the folder name in the 117th line as the received folder name string
						 * contains all other details like permissions, last updates date etc.
						 */
						String folderName = j.toString().substring(60);
						if (Pattern.matches("[a-zA-z]+", folderName)) {
							System.out.println("Checking the folder - " + folderName);
							String crntFldrPath = batchFldrPath + folderName + "/logs/";
							logChecker(sftp, crntFldrPath, logDates, logMapper, errorLogFile, logNameStartIndexNum);
						}
					}
				} else {
					logNameStartIndexNum = 56;
					String[] logLocations = { "/usr/app/blcs/data_process/logs/", "/usr/app/ascs/data_process/logs/" };
					for (String location : logLocations)
						logChecker(sftp, location, logDates, logMapper, errorLogFile, logNameStartIndexNum);
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
		System.out.println("***************Sales Event File Checker started***************");
		slsEvntChk.check002(userName, "va10puvbas002", password);
		System.out.println("***************Sales Event File Checker ended***************");
		frame.dispose();
//		Signature.main(null);
	}

	public void logChecker(ChannelSftp sftp, String crntFldrPath, ArrayList<String> logDates,
			Map<String, ArrayList<String>> logMapper, FileWriter errorLogFile, int logNameStartIndexNum) {
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
					// have to pass the list too
					ArrayList<String> errorList = null;
					logNameWtimeStamp = log.toString().substring(logNameStartIndexNum);
					if (logNameWtimeStamp.contains(logDates.get(x).toString())) {
						timeStamp = logNameWtimeStamp.substring(
								logNameWtimeStamp.indexOf(shiftStartTime.substring(0, 4)),
								logNameWtimeStamp.indexOf("."));
						System.out.println(log.toString());
						if (isLogCretdInGivnShift(timeStamp)) {
							logFileName = logNameWtimeStamp.substring(0, logNameWtimeStamp.indexOf(timeStamp));
							if (logFileName.contains("_")) {
								logFileName = logFileName.substring(0, logFileName.length() - 1);
							}
							stream = sftp.get(crntFldrPath + logNameWtimeStamp);

							br = new BufferedReader(new InputStreamReader(stream));
							errorLogFile.write("-------------Checking Logs for - " + logFileName + "-------------\n");
							errorLogFile.write("-->"+logNameWtimeStamp + "\n");
							boolean isErrExist = false;
							String line;
							String errorLine;
							if (logNmFndr.containsKey(logFileName))
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
									if (logFileName.equals("SBO_DAILY_CP_LETTER_LOAD_PROD"))
										maxLen = 196;
									else if (line.length() > 300)
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
				return;
			}
		}
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
			// System.out.println("yes, within time");
			return true;
		} else {
			// System.out.println("not within time");
			return false;
		}
	}

	public static void main(String args) {
		LogsValidator pt = new LogsValidator();
		/*
		 * Scanner scan = new Scanner(System.in); boolean corectInp = false;
		 * System.out.println("Enter the shift you're in: "); while (!corectInp) {
		 * pt.shift = scan.next().toUpperCase(); if (pt.shift.equals("S1") ||
		 * pt.shift.equals("S2") || pt.shift.equals("S3")) { corectInp = true;
		 * scan.close(); } else { System.out.println("Enter a correct input-"); } }
		 */
		pt.shift = args;
		pt.sdf.setTimeZone(pt.zone);
		Calendar cal = new GregorianCalendar();
		cal.setTimeZone(pt.zone);
		switch (pt.shift) {
		case "S1":
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 05:40";
			cal.add(Calendar.DATE, -1);
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 20:20";
			break;
		case "S2":
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 04:20";
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 13:40";
			break;
		case "S3":
			pt.shiftStartTime = pt.sdf.format(cal.getTime()) + " 11:50";
			pt.shiftEndTime = pt.sdf.format(cal.getTime()) + " 21:10";
			break;
		default:
			break;
		}
		System.out.println("Shift timings: " + pt.shiftStartTime + " to " + pt.shiftEndTime);
		pt.planWindow();
	}

}
