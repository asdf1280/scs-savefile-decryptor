package scsc.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.zip.InflaterInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

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
		System.out.println("\"" + filename + "\"");
		System.out.println(
				"If your Java is old and requires JCE(Java Cryptography Extension), This software won't work.");
		System.out.println("Source code is available on Github: https://github.com/dhkim0800/scs-savefile-decryptor");
		ScsMain instance = new ScsMain(filename);
		try {
			instance.initialize();
		} catch (IOException e) {
			System.out.println("An I/O Error occured while working. Error information: ");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException
				| InvalidKeyException e) {
			System.out.println("An unhandled cryptography error occured. Error information: ");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An unhandled error occured. Error information: ");
			e.printStackTrace();
		}
		in.close();
	}

	private static final byte[] aesKey = { (byte) 0x2a, (byte) 0x5f, (byte) 0xcb, (byte) 0x17, (byte) 0x91, (byte) 0xd2,
			(byte) 0x2f, (byte) 0xb6, (byte) 0x02, (byte) 0x45, (byte) 0xb3, (byte) 0xd8, (byte) 0x36, (byte) 0x9e,
			(byte) 0xd0, (byte) 0xb2, (byte) 0xc2, (byte) 0x73, (byte) 0x71, (byte) 0x56, (byte) 0x3f, (byte) 0xbf,
			(byte) 0x1f, (byte) 0x3c, (byte) 0x9e, (byte) 0xdf, (byte) 0x6b, (byte) 0x11, (byte) 0x82, (byte) 0x5a,
			(byte) 0x5d, (byte) 0x0a, };

	private String filename;

	public ScsMain(String filename) {
		if (filename == null) {
			System.out.println("Filename is null.");
		}
		this.filename = filename;
	}

	public void initialize() throws Exception {
		File out = decrypt(new File(filename));
		FileUtils.forceDelete(new File(filename));
		FileUtils.moveFile(out, new File(filename));
		System.out.println("Successfully decrypted your savefile!");
	}

	// SCSCOriginal start
	private File decrypt(File input) throws Exception {
		File out = File.createTempFile("scsc-", ".tmp");
		out.deleteOnExit();

		byte[] data = new byte[(int) (input.length())];
		FileInputStream fis = new FileInputStream(input);
		if (fis.read(data) != data.length) {
			fis.close();
			throw new RuntimeException("Could not read " + input + " into memory");
		}
		fis.close();

		byte[] cipherText = new byte[data.length - 0x38];
		byte[] iv = new byte[0x10];
		System.arraycopy(data, 0x38, cipherText, 0, cipherText.length);
		System.arraycopy(data, 0x24, iv, 0, iv.length);
		byte[] decrypted = decrypt(cipherText, aesKey, iv);

		ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
		InflaterInputStream iis = new InflaterInputStream(bis);
		InputStreamReader ir = new InputStreamReader(iis);
		BufferedReader br = new BufferedReader(ir);

		FileOutputStream fos = new FileOutputStream(out);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		PrintWriter pw = new PrintWriter(osw);

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			pw.println(line);
		}
		pw.close();
		br.close();

		return out;
	}
	
	private byte[] decrypt(byte[] cipherText, byte[] keyBytes, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherText);
    }
	// SCSCOriginal end
}
