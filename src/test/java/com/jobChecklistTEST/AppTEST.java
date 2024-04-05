package com.jobChecklistTEST;

import java.util.Scanner;

public class AppTEST {
	String inpFileAddress = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\jobchecklist0404.xlsx";
	String outFileAddress = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\Daily Sheet2.xlsx";
	String detailedErrorsLoc = "C:\\Users\\AL04040\\OneDrive - Elevance Health\\Documents\\VA_SBE\\temp\\";
	static String shift = null;
	public static void main(String[] args) throws Exception {
		// AppTEST app = new AppTEST();
		Scanner scan = new Scanner(System.in);
		System.out.println("***************Started to fill the job timings***************");
		System.out.println("Enter the Order Date that you need to enter timings in format (MM/dd/yyyy):");
		String gvnOrdrDate = scan.next();
		System.out.println(gvnOrdrDate);
		JobTimingsFillerTEST.main(gvnOrdrDate);
		System.out.println("***************Completed to fill the job timings***************\n");
		System.out.println("\n***************Started to validate the job logs***************");
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
		ValidatingLogsTEST.main(shift);
		scan.close();
	}
}
