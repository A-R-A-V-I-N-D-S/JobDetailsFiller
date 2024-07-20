package com.checkList;

public class Signature {
	public static void main(String[] args) {
		String[] str = new String[6];
		System.out.println("Code by:");

		str[0] = "            ____                                _____\n";
		str[1] = "    /\\     |    |     /\\  \\        / | |\\    | |     \\\n";
		str[2] = "   /  \\    |____|    /  \\  \\      /  | | \\   | |      |\n";
		str[3] = "  /____\\   |  \\     /____\\  \\    /   | |  \\  | |      |\n";
		str[4] = " /      \\  |   \\   /      \\  \\  /    | |   \\ | |      |\n";
		str[5] = "/        \\ |    \\ /        \\  \\/     | |    \\| |_____/\n\n";
		for(int i=0;i<str.length;i++){
			System.out.print(str[i]);
		}
	}
}
