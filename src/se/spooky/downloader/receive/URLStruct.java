package se.spooky.downloader.receive;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import se.spooky.downloader.url.Direction;

public class URLStruct {
	private URL mURL;
	private Map<Direction, Boolean> mDirectionDone;
	private String mRoot;

	public URLStruct(URL uRL) {
		mURL = uRL;
		mDirectionDone = new HashMap<Direction, Boolean>();
		mDirectionDone.put(Direction.NEXT, false);
		mDirectionDone.put(Direction.PREV, false);
	}

	public URL getURL() {
		return mURL;
	}

	public void setURL(URL uRL) {
		mURL = uRL;
	}

	public void setRoot(String root) {
		mRoot = root;
	}

	public String getRoot() {
		return mRoot;
	}

	public void setDirectionDone(Direction direction, Boolean done) {
		mDirectionDone.put(direction, done);
	}

	public boolean isDone() {
		return mDirectionDone.get(Direction.NEXT) && mDirectionDone.get(Direction.PREV);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof URLStruct) {
			URLStruct urlStruct = (URLStruct) obj;
			return mURL.equals(urlStruct.mURL);
		} else if (obj instanceof URL) {
			URL url = (URL) obj;
			return mURL.equals(url);
		}
		return false;
	}

	public Direction getRemainingDirection() {
		Direction direction = null;
		if (!mDirectionDone.get(Direction.NEXT)) {
			direction = Direction.NEXT;
		} else {
			direction = Direction.PREV;
		}
		return direction;
	}
}
