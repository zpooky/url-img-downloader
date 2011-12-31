package se.spooky.downloader.receive;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;

public class FileDownloader {
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0";

	public FileDownloader() {
	}

	// private static String getHTTPRequest(URL url) {
	// String request = "";
	// request += "GET " + url.getPath() + " http/1.0\n";
	// request += "Host: " + url.getHost() + "\n";
	// request +=
	// "User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:8.0) Gecko/20100101 Firefox/8.0\n";
	// request +=
	// "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n";
	// request += "Accept-Language: sv-se,sv;q=0.8,en-us;q=0.5,en;q=0.3\n";
	// request += "Accept-Encoding: gzip, deflate\n";
	// request += "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n";
	// request += "Connection: keep-alive\n";
	// return request;
	// }
	public void run() {
	}

	public static void handleImage(URL url, String rootFolder) throws FileReceiveException {
		File outputfile = null;
		try {
			// System.out.println(url);
			outputfile = new File(String.format("%s%s", rootFolder, getFilename(url)));
			// System.out.println(outputfile);
			if (outputfile.isFile()) {
				throw new FileReceiveException(outputfile.getName(), String.format("File %s allready exists.", outputfile.getName()), false);
			}
			URLConnection openConnection = url.openConnection();
			openConnection.setRequestProperty("User-Agent", USER_AGENT);
			BufferedImage bufferedImage = ImageIO.read(openConnection.getInputStream());
			if (bufferedImage == null) {
				throw new FileReceiveException(outputfile.getName(), String.format("Unknow Exception for file %s.", outputfile.getName()), false);
			}
			ImageIO.write(bufferedImage, getFiletype(url), outputfile);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new FileReceiveException(outputfile.getName(), String.format("File %s was not found.", outputfile.getName()), true);
		}
		/*
		 * URLConnection connection = url.openConnection();
		 * connection.setDoOutput(true); connection.setDoInput(true);
		 * sendRequest(connection, url); InputStream inputStream =
		 * connection.getInputStream(); // receiveResponse(inputStream); long
		 * start = System.currentTimeMillis();
		 */
		/*
		 * int bytesRead; int current = 0; byte[] mybytearray = new
		 * byte[130651]; FileOutputStream fileOutputStream = new
		 * FileOutputStream(getFilename(url)); BufferedOutputStream
		 * bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
		 * bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
		 * current = bytesRead; do { bytesRead = inputStream.read(mybytearray,
		 * current, (mybytearray.length - current)); if (bytesRead >= 0) current
		 * += bytesRead; } while (bytesRead > -1);
		 * bufferedOutputStream.write(mybytearray, 0, current);
		 * bufferedOutputStream.flush();
		 */
		/*
		 * long end = System.currentTimeMillis(); System.out.println(end -
		 * start);
		 */
		// bufferedOutputStream.close();
	}

	/*
	 * private static void sendRequest(URLConnection connection, URL url) throws
	 * IOException { OutputStream outputStream = connection.getOutputStream();
	 * OutputStreamWriter outputStreamWriter = new
	 * OutputStreamWriter(outputStream);
	 * outputStreamWriter.write(getHTTPRequest(url));
	 * outputStreamWriter.flush(); outputStream.close(); outputStream.flush(); }
	 * 
	 * private static void receiveResponse(InputStream inputStream) throws
	 * IOException { InputStreamReader inputStreamReader = new
	 * InputStreamReader(inputStream); char[] cbuf = new char[1024]; int read =
	 * inputStreamReader.read(cbuf, 0, 1024); for (int i = 0; i < read; ++i) {
	 * System.out.print(cbuf[i]); } }
	 */
	public static String getFilename(URL url) {
		int first = -1;
		for (int i = url.getPath().length() - 1; i >= 0 && first == -1; --i) {
			if (url.getPath().charAt(i) == '/') {
				first = i + 1;
			}
		}
		return url.getPath().substring(first);
	}

	private static String getFiletype(URL url) {
		int first = -1;
		for (int i = url.getPath().length() - 1; i >= 0 && first == -1; --i) {
			if (url.getPath().charAt(i) == '.') {
				first = i + 1;
			}
		}
		return url.getPath().substring(first);
	}
}
