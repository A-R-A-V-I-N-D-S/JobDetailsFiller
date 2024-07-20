package com.checkList;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SalesEventsFileChecker {

	public void check002(String userName, String host, String password) {
		String path = "/usr/app/blcs/inbound/salesevent";
		int port = 22;
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(userName, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			System.out.println("\nConnection successful - " + host);
			Channel chnl = session.openChannel("sftp");
			chnl.connect();
			ChannelSftp sftp = (ChannelSftp) chnl;
			sftp.cd(path);
			System.out.println("Change directory to -->" + sftp.pwd());
			Vector<LsEntry> fileList = sftp.ls(path);
			
			if (fileList.size() <= 3) {
				System.out.println("There are no files stuck in Inbound location");
			} else {
				System.out.println("These files are present in the Inbound location: ");
				for (LsEntry file : fileList)
					System.out.println("File name --> " + file.toString());
			}
			
			sftp.disconnect();
			session.disconnect();
			System.out.println("Disconnected from - " + host);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
