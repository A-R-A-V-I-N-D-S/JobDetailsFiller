package com.checkList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {
	App app = new App();
	ExcelStyle styleClass = new ExcelStyle();

	public void writeLogs(Map<String, ArrayList<String>> logMapper) {

		try {
			FileInputStream fileIN = new FileInputStream(app.outFileAddress);
			XSSFWorkbook wb = new XSSFWorkbook(fileIN);
			XSSFSheet checkListSheet = wb.getSheet("Checklist");
			int totalJobs = checkListSheet.getLastRowNum();
			for (int i = 0; i < totalJobs; i++) {
				XSSFCell jobNameCell = checkListSheet.getRow(i).getCell(2);
				String jobName = jobNameCell.getStringCellValue();
				if (logMapper.containsKey(jobName)) {
					XSSFCell errorLogCell = checkListSheet.getRow(i).createCell(13);
					XSSFCell isValidatedCell = checkListSheet.getRow(i).createCell(12);
					ArrayList<String> errors = logMapper.get(jobName);
					String allErrors = App.shift + ":";
					if (errors.size() == 0) {
						allErrors += "\nNo errors";
					} else {
						for (int j = 0; j < errors.size(); j++) {
							allErrors += "\n";
							allErrors += errors.get(j);
						}
					}
					errorLogCell.setCellValue(allErrors);
					isValidatedCell.setCellValue("Y");
					styleClass.setBorder(wb, errorLogCell);
					styleClass.setBorder(wb, isValidatedCell);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(app.outFileAddress);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
