package se.spooky.downloader.interfce;

import se.spooky.downloader.url.Direction;

public interface GUIFrameInterface {
	public void addLogMessage(String message);

	public void setFileReceiveDone(Direction direction);
}
