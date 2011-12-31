package se.spooky.downloader.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {
	public static void makeDataFile(File root, String content) throws IOException {
		File file = new File(String.format("%s%s%s", root.toString(), File.separator, "data.txt"));
		file.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.flush();
		out.close();
	}

	public static boolean isOk(URL url) {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("HEAD");
			httpURLConnection.connect();
			boolean ret = httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
			httpURLConnection.disconnect();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
