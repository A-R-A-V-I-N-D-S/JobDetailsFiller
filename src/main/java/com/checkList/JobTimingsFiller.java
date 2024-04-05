package com.checkList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JobTimingsFiller {
	App app = new App();
	String inpFileAddress = app.inpFileAddress;
	String outFileAddress = app.outFileAddress;
	static DataFormatter fm = new DataFormatter();
	static String century = "20";

	public static void main(String args) {
		JobTimingsFiller wXL = new JobTimingsFiller();
		try {
			// wXL.orderDateFormatter();
			FileInputStream fileIn = new FileInputStream(wXL.inpFileAddress);
			XSSFWorkbook wb = new XSSFWorkbook(fileIn);
			XSSFSheet sh1 = wb.getSheet("CTM Details");
			XSSFSheet sh2 = wb.getSheet("Checklist");

			DateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a");
			String gvnOrdrDate = args;
			int sh1Len = sh1.getLastRowNum();
			int sh2Len = sh2.getLastRowNum();
			System.out.printf("%d, %d\n", sh1Len, sh2Len);
			Cell scheduleCell = null;
			String schedule = "", splitter = null;
			int[] flag = new int[sh2Len + 1];
			String[] unmatchedJobs = new String[15];
			int cntr = 0;
			XSSFCellStyle style = wb.createCellStyle();
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			for (int i = 0; i <= sh1Len; i++) {
				// System.out.println("into the first for loop");
				String jobStatus = fm.formatCellValue(sh1.getRow(i).getCell(2));
				int matched = 0;
				String sh1JobName = fm.formatCellValue(sh1.getRow(i).getCell(1));
				String sh1FldrName = fm.formatCellValue(sh1.getRow(i).getCell(28));
				XSSFCell ordrDtCell = sh1.getRow(i).getCell(20);
				String ordrDate = "";
				// block which gets order date from the job details
				if (ordrDtCell != null) {// ignoring NullPointerException
					if (ordrDtCell.getCellType() != 3) {
						// System.out.println("into the order date formatter");
						ordrDate = fm.formatCellValue(ordrDtCell);
						if (ordrDate.contains("/")) {
							splitter = "/";
						} else if (ordrDate.contains("/")) {
							splitter = "-";
						} else
							continue;
						String[] newOrdrDte = ordrDate.split(splitter);
						ordrDate = (newOrdrDte[0].length() == 1 ? ("0" + newOrdrDte[0]) : newOrdrDte[0]) + "/"
								+ (newOrdrDte[1].length() == 1 ? ("0" + newOrdrDte[1]) : newOrdrDte[1]) + "/"
								+ newOrdrDte[2];
					}
				}
				// System.out.println(ordrDate);
				for (int j = 0; j <= sh2Len; j++) {
					String sh2JobName = fm.formatCellValue(sh2.getRow(j).getCell(2));
					String sh2FldrName = fm.formatCellValue(sh2.getRow(j).getCell(3));
					if (sh1JobName.equals(sh2JobName) && sh1FldrName.equals(sh2FldrName)) {
						matched = 1;
						if (ordrDate.equals(gvnOrdrDate)) {
							// System.out.println("order dates matched");
							XSSFCell cellStartDateINP = sh1.getRow(i).getCell(14);
							XSSFCell cellEndDateINP = sh1.getRow(i).getCell(15);
							String startDate = "";
							String endDate = "";
							Cell cellJobStatus = sh2.getRow(j).getCell(11);
							String sh2JobStatus = fm.formatCellValue(sh2.getRow(j).getCell(11));
							// condition to map for the only the jobs that are
							// not
							// in Ended OK and that are not mapped yet
							if ((!sh2JobStatus.equalsIgnoreCase("ended ok")) && flag[j] == 0) {
								flag[j] = 1;
								Cell cellStDtOUT = sh2.getRow(j).createCell(9);
								Cell cellEnDtOUT = sh2.getRow(j).createCell(10);
								cellJobStatus = sh2.getRow(j).createCell(11);
								if (cellStartDateINP != null) {// ignoringNullPointerException
									if (cellStartDateINP.getCellType() != 3) {
										if (cellStartDateINP.getCellType() != 1) {
											Date date1 = cellStartDateINP.getDateCellValue();
											startDate = timeFormatter.format(date1);
										} else {
											startDate = cellStartDateINP.getStringCellValue();
										}
									}
								}
								if (cellEndDateINP != null) {// ignoringNullPointerException
									if (cellEndDateINP.getCellType() != 3) {
										if (cellEndDateINP.getCellType() != 1) {
											Date date2 = cellEndDateINP.getDateCellValue();
											endDate = timeFormatter.format(date2);
										} else {
											endDate = cellEndDateINP.getStringCellValue();
										}
									}
								}
								cellStDtOUT.setCellValue(startDate);
								cellEnDtOUT.setCellValue(endDate);
								// switch block to set the exact job status
								switch (jobStatus) {
								case "Ended OK":
									cellJobStatus.setCellValue("Ended OK");
									break;
								case "Wait Condition":
									cellJobStatus.setCellValue("Executing");
									break;
								case "Executing":
									cellJobStatus.setCellValue("Executing");
									break;
								default:
									break;
								}
								String srvr = fm.formatCellValue(sh2.getRow(j).getCell(5));
								XSSFCell deletionStatusCell = sh1.getRow(i).getCell(26);
								XSSFCell holdStatusCell = sh1.getRow(i).getCell(10);
								String deletionStatus = fm.formatCellValue(deletionStatusCell);
								String holdStatus = fm.formatCellValue(holdStatusCell);
								if (startDate.equals("") && endDate.equals("") && deletionStatus.equals("Checked"))
									cellJobStatus.setCellValue("Cancelled");
								else if (startDate.equals("") && endDate.equals("") && holdStatus.equals("Checked"))
									cellJobStatus.setCellValue("Hold");
								else if (startDate.equals("") && endDate.equals(""))
									cellJobStatus.setCellValue("Yet to start");
								System.out.printf("%d) %s - %s%n", (i + 1), sh2JobName, sh2FldrName);
								/*if (!isJobInScan(srvr, ordrDate,
								 * gvnOrdrDate)) {
								 * cellJobStatus.setCellValue("NA");
								 * cellStDtOUT.setCellValue("");
								 * cellEnDtOUT.setCellValue(""); }
								 */
								cellStDtOUT.setCellStyle(style);
								cellEnDtOUT.setCellStyle(style);
								cellJobStatus.setCellStyle(style);
								break;
							}
						}
					}
				}
				if (matched == 0)
					unmatchedJobs[cntr++] = sh1JobName;
			}
			// block for filling NA for jobs that are not in scan
			for (int k = 1; k <= sh2Len; k++) {
				String jobStatusNA = fm.formatCellValue(sh2.getRow(k).getCell(11));
				if (flag[k] == 0 && jobStatusNA.equals("")) {
					sh2.getRow(k).createCell(9).setCellStyle(style);
					sh2.getRow(k).createCell(10).setCellStyle(style);
					Cell cellNAjobStatus = sh2.getRow(k).createCell(11);
					cellNAjobStatus.setCellValue("NA");
					cellNAjobStatus.setCellStyle(style);
				}
			}
			sh2.autoSizeColumn(9);
			sh2.autoSizeColumn(10);
			sh2.autoSizeColumn(11);
			if (cntr == 1) {
				System.out.println("All jobs are present in the Checklist");
			} else {
				System.out.println("These jobs are not present in Job Checklist:");
				for (int i = 0; i < cntr; i++) {
					if (!unmatchedJobs[i].equalsIgnoreCase("name") && cntr > 1)
						System.out.printf("%d) %s%n", (i + 1), unmatchedJobs[i]);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(wXL.outFileAddress);
			wb.write(fileOut);
			fileOut.close();
			System.out.println("File successfully written and timings marked for completed jobs.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// function to know if the server had new scan or not
	public static boolean isJobInScan(String srvr, String ordrDate, String gvnOrdrDate) throws ParseException {
		DateFormat dateNTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		dateNTimeFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		Date curDate = new Date();
		Date curOrdrDate = dateFormat.parse(ordrDate);
		String tdyOdrDate = dateFormat.format(curOrdrDate);
		String tmrDate = dateFormat.format(cal.getTime());
		Date tdy3pmEST = null, tmr3pmEST = null;
		Date tdy6amEST = null, tmr6amEST = null;
		tdy3pmEST = dateNTimeFormat.parse(tdyOdrDate + " 15:00:00");
		tdy6amEST = dateNTimeFormat.parse(tdyOdrDate + " 06:00:00");
		tmr3pmEST = dateNTimeFormat.parse(tmrDate + " 15:00:00");
		tmr6amEST = dateNTimeFormat.parse(tmrDate + " 06:00:00");
		if (tdyOdrDate.equals(gvnOrdrDate)) {
			if (srvr.equals("CTM200")) {
				System.out.println(dateNTimeFormat.format(curDate));
				if (curDate.after(tdy3pmEST) && curDate.before(tmr3pmEST)) {
					System.out.println("yes");
					return true;
				} else
					return false;
			} else {
				if (curDate.after(tdy6amEST) && curDate.before(tmr6amEST)) {
					System.out.println("yes");
					return true;
				} else
					return false;
			}
		} else
			return false;
	}

	// function to give confirmation of job availability accorting to the day
	public static boolean isJobTodayScan(String ordrDate, String schedule, String srvr, String gvnOrdrDate)
			throws ParseException {
		DateFormat dayFrmt = new SimpleDateFormat("EE");
		DateFormat dateFrmt = new SimpleDateFormat("MM/dd/yyyy");
		Date date = dateFrmt.parse(ordrDate);
		System.out.println(dayFrmt.format(date).toUpperCase());
		if (schedule.contains(dayFrmt.format(date).toUpperCase()) && isJobInScan(srvr, ordrDate, gvnOrdrDate))
			return true;
		return false;
	}
}
