package se.spooky.downloader.receive;

import java.net.MalformedURLException;
import java.net.URL;
import se.spooky.downloader.interfce.GUIFrameInterface;
import se.spooky.downloader.url.Direction;
import se.spooky.downloader.url.UrlHandler;

public class FileReceiveThread extends Thread implements Runnable {
	private URL mURL;
	private Direction mDirection;
	private String mRootFolder;
	private GUIFrameInterface mGUIFrame;

	public FileReceiveThread(GUIFrameInterface frame, URL url, Direction direction, String rootFolder) {
		mGUIFrame = frame;
		mURL = url;
		mDirection = direction;
		mRootFolder = rootFolder;
	}

	@Override
	public void run() {
		boolean next = true;
		int v = 0;
		try {
			UrlHandler urlHandler = new UrlHandler(mURL);
			while (next && !isInterrupted()) {
				try {
					FileReceiver.handleImage(mURL, mRootFolder);
					mURL = getNext(urlHandler);
					v = 0;
					mGUIFrame.addLogMessage(String.format("Image %s Downloaded", FileReceiver.getFilename(mURL)));
				} catch (FileReceiveException e) {
					mGUIFrame.addLogMessage(e.getMessage());
					if (e.isCorrectNeeded()) {
						try {
							mURL = urlHandler.correction(v);
							++v;
						} catch (Exception e1) {
							next = false;
						}
					} else if (e.isDone()) {
						next = false;
					}
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		mGUIFrame.setFileReceiveDone(mDirection);
	}

	public URL getNext(UrlHandler urlHandler) throws MalformedURLException, FileReceiveException {
		if (mDirection == Direction.NEXT) {
			return urlHandler.next();
		}
		return urlHandler.prev();
	}
}
