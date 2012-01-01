package se.spooky.downloader.receive;

import java.net.MalformedURLException;
import java.net.URL;
import se.spooky.downloader.interfce.GUIFrameInterface;
import se.spooky.downloader.url.Direction;
import se.spooky.downloader.url.UrlHandler;

public class FileReceiveThread extends BaseFileReceiveThread implements Runnable {
	private URL mURL;
	private String mRootFolder;
	private GUIFrameInterface mGUIFrame;

	public FileReceiveThread(GUIFrameInterface frame, URL url, Direction direction, String rootFolder) {
		super(direction);
		mGUIFrame = frame;
		mURL = url;
		mRootFolder = rootFolder;
	}

	@Override
	protected void life() {
		boolean next = true;
		int v = 0;
		try {
			UrlHandler urlHandler = new UrlHandler(mURL);
			while (next && !isInterrupted()) {
				try {
					/*
					 * if (!Util.isOk(mURL)) {
					 * throw new FileReceiveException("", "URL is 404", true);
					 * }
					 */
					FileDownloader.handleImage(mURL, mRootFolder);
					mURL = getNext(urlHandler);
					v = 0;
					mGUIFrame.addLogMessage(String.format("Image %s Downloaded", FileDownloader.getFilename(mURL)));
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
		mGUIFrame.updateDoneButton();
	}

	public URL getNext(UrlHandler urlHandler) throws MalformedURLException, FileReceiveException {
		if (getDirection() == Direction.NEXT) {
			return urlHandler.next();
		}
		return urlHandler.prev();
	}
}
