package se.spooky.downloader.url;

import java.net.MalformedURLException;
import java.net.URL;
import se.spooky.downloader.receive.FileReceiveException;

public class UrlHandler {
	private Direction mDirection = null;
	private String mFirstChunk;
	private String mSecondChunk;
	private String mValueBase;
	private String mValueNew;
	private String mTmpValue;

	public UrlHandler(URL url) {
		split(url.toString());
	}

	public UrlHandler(String url) {
		split(url);
	}

	public URL next() throws MalformedURLException {
		if (mDirection == null) {
			mDirection = Direction.NEXT;
		} else if (mDirection == Direction.PREV) {
			mValueNew = mValueBase;
			mDirection = Direction.NEXT;
		}
		int value = Integer.parseInt(mValueNew);
		++value;
		String valueTmp = Integer.toString(value);
		while (valueTmp.length() < mValueBase.length()) {
			valueTmp = "0" + valueTmp;
		}
		mValueNew = valueTmp;
		return assemble();
	}

	public URL prev() throws MalformedURLException, FileReceiveException {
		if (mDirection == null) {
			mDirection = Direction.PREV;
		} else if (mDirection == Direction.NEXT) {
			mValueNew = mValueBase;
			mDirection = Direction.PREV;
		}
		int value = Integer.parseInt(mValueNew);
		--value;
		if (value < 0) {
			throw new FileReceiveException("Done Previous", true);
		}
		String valueTmp = Integer.toString(value);
		int i = 0;
		while (valueTmp.length() < mValueBase.length() && mValueBase.charAt(i++) == '0') {
			valueTmp = "0" + valueTmp;
		}
		mValueNew = valueTmp;
		return assemble();
	}

	private static boolean isNumeric(char c) {
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
	}

	private URL assemble() throws MalformedURLException {
		return new URL(String.format("%s%s%s", mFirstChunk, mValueNew, mSecondChunk));
	}

	private void split(String url) {
		boolean foundLast = false;
		int first = -1;
		int last = -1;
		for (int i = url.length() - 1; i >= 0 && first == -1; --i) {
			if (isNumeric(url.charAt(i))) {
				if (!foundLast) {
					last = i;
					foundLast = true;
				}
			} else {
				if (foundLast) {
					first = i;
				}
			}
		}
		++first;
		++last;
		mFirstChunk = url.substring(0, first);
		mValueBase = url.substring(first, last);
		mValueNew = mValueBase;
		mSecondChunk = url.substring(last, url.length());
	}

	public URL correction(int v) throws Exception {
		switch (v) {
		case 0:// add 0
			mTmpValue = mValueNew;
		case 1:// add 0
		case 2:// add 0
		case 3:// add 0
		case 4:// add 0
		case 5:// add 0
		case 6:// add 0
			mValueNew = "0" + mValueNew;
			break;
		case 7:// shift
			mValueNew = mTmpValue;
		case 8:// shift
		case 9:// shift
		case 10:// shift
		case 11:// shift
		case 12:// shift
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
			if (mDirection == Direction.NEXT) {
				return next();
			} else {
				return prev();
			}
		default: {
			v = 0;
			mValueNew = mTmpValue;
			throw new Exception();
		}
		}
		return assemble();
	}
}
