package se.spooky.downloader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.List;

public class Unique extends Thread implements Runnable {
	private File mRootFolder;

	public Unique(File rootFolder) {
		mRootFolder = rootFolder;
	}

	public File getRootFolder() {
		return mRootFolder;
	}

	public void setRootFolder(File rootFolder) {
		mRootFolder = rootFolder;
	}

	@Override
	public void run() {
		List<String> hashList = new LinkedList<String>();
		File[] listFiles = mRootFolder.listFiles();
		for (int i = 0; i < listFiles.length && !isInterrupted(); ++i) {
			try {
				String hash = getMD5Checksum(listFiles[i].toString());
				if (hashList.contains(hash)) {
					listFiles[i].delete();
				} else {
					hashList.add(hash);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
