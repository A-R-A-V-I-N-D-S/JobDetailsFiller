package com.checkList;

import java.util.Scanner;

public class App {
	String inpFileAddress = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\Daily Checklist_IN.xlsx";
	String outFileAddress = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\Checklist.xlsx";
	String detailedErrorsLoc = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\";
	static String shift = null;

	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("***************Started to fill the job timings***************\n");
		System.out.println("Enter the Order Date that you need to enter timings in format (MM/dd/yyyy):");
		String gvnOrdrDate = scan.next();
		System.out.println(gvnOrdrDate);
		JobTimingsFiller.main(gvnOrdrDate);
		System.out.println("\n***************Completed to fill the job timings***************\n");
		System.out.println("\n***************Started to validate the job logs***************\n");
		boolean corectInp = false;
		System.out.println("Enter the shift you're in: ");
		while (!corectInp) {
			shift = scan.next().toUpperCase();
			if (shift.equals("S1") || shift.equals("S2") || shift.equals("S3")) {
				corectInp = true;
				scan.close();
			} else {
				System.out.println("Enter a correct input-");
			}
		}
		LogsValidator.main(shift);
		scan.close();
	}
}
