package se.spooky.downloader.receive;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import se.spooky.downloader.interfce.GUIFrameInterface;
import se.spooky.downloader.url.Direction;

public class ThreadHandler implements ThreadHandlerInterface {
	private static ThreadHandler mInstance = null;
	private GUIFrameInterface mGuiFrameInterface;
	private FileReceiveThread[] mFileReceiveThread = null;
	private Set<URLStruct> mURLSet;

	public static synchronized ThreadHandler getInstance(GUIFrameInterface guiFrameInterface) {
		if (mInstance == null) {
			mInstance = new ThreadHandler(guiFrameInterface);
		}
		return mInstance;
	}

	public static ThreadHandlerInterface getThreadInstance() {
		return mInstance;
	}

	private ThreadHandler(GUIFrameInterface guiFrameInterface) {
		mFileReceiveThread = new FileReceiveThread[2];
		mGuiFrameInterface = guiFrameInterface;
		mURLSet = new HashSet<URLStruct>();
	}

	public boolean isDone() {
		return isThreadsRunning() && mURLSet.isEmpty();
	}

	public void handle(Direction direction, URL url, String root) {
		URLStruct urlStruct = new URLStruct(url);
		urlStruct.setRoot(root);
		if (mFileReceiveThread[direction.getId()] == null) {
			start(direction, urlStruct);
		} else if (mFileReceiveThread[direction.getId()].isAlive()) {
			pushSet(urlStruct);
		} else {
			start(direction, urlStruct);
		}
		pushSet(urlStruct);
	}

	public void killAll() {
		try {
			mFileReceiveThread[0].interrupt();
		} catch (Exception e) {
		}
		try {
			mFileReceiveThread[1].interrupt();
		} catch (Exception e) {
		}
	}

	private void start(Direction direction, URLStruct urlStruct) {
		new File(urlStruct.getRoot()).mkdir();
		System.out.println("start: " + direction.toString());
		mFileReceiveThread[direction.getId()] = new FileReceiveThread(mGuiFrameInterface, urlStruct.getURL(), direction, urlStruct.getRoot());
		mFileReceiveThread[direction.getId()].start();
		urlStruct.setDirectionDone(direction, true);
	}

	private void pushSet(URLStruct urlStruct) {
		mURLSet.remove(urlStruct);
		if (!urlStruct.isDone()) {
			mURLSet.add(urlStruct);
		}
	}

	private void next() {
		Iterator<URLStruct> iterator = mURLSet.iterator();
		URLStruct urlStruct = null;
		if (iterator.hasNext()) {
			urlStruct = iterator.next();
		}
		if (urlStruct != null) {
			start(urlStruct.getRemainingDirection(), urlStruct);
			pushSet(urlStruct);
		}
	}

	private boolean isThreadsRunning() {
		try {
			return mFileReceiveThread[Direction.NEXT.getId()].isAlive() && mFileReceiveThread[Direction.PREV.getId()].isAlive();
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public void setDone(Direction direction) {
		try {
			mFileReceiveThread[direction.getId()] = null;
		} catch (Exception e) {
		}
		next();
	}
}
