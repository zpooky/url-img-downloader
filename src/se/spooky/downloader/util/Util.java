package se.spooky.downloader.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	public static void makeDataFile(File root, String content) throws IOException {
		File file = new File(root.toString() + File.separator + "data.txt");
		file.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(content);
		out.flush();
		out.close();
	}
}
