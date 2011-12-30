package se.spooky.downloader.url;

public enum Direction {
	NEXT(0), //
	PREV(1);
	private int mId;

	private Direction(int id) {
		mId = id;
	}

	public int getId() {
		return mId;
	}
}
