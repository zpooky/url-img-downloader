package se.spooky.downloader.receive;

import se.spooky.downloader.url.Direction;

public interface ThreadHandlerInterface {
	void setDone(Direction direction);
}
