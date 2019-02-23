package scsc.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

public class ScsMain {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		String filename = null;
		if (args.length < 1) {
			System.out.println("Please enter a file name to decrypt.");
			filename = in.nextLine();
		} else {
			filename = String.join(" ", args);
		}
		System.out.println("If your Java is old and requires JCE(Java Cryptography Extension), This software won't work.");
		System.out.println("Source code is available on Github.");
		ScsMain instance = new ScsMain(filename);
		try {
			instance.initialize();
		} catch (IOException e) {
			System.out.println("An I/O Error occured while working. Error information: ");
			e.printStackTrace();
		}
		in.close();
	}
	
	private String filename;
	public ScsMain(String filename) {
		if(filename == null) {
			System.out.println("Filename is null.");
		}
		this.filename = filename;
	}
	public void initialize() throws IOException {
		decrypt();
	}
	private void decrypt() throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		byte[] file = IOUtils.toByteArray(fis);
		
		
	}
}
