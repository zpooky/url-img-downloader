package se.spooky.downloader.receive;

public class FileReceiveException extends Exception {
	private static final long serialVersionUID = 1L;
	private String mFileName;
	private String mMessage;
	private boolean mCorrect;
	private boolean mDone;

	public FileReceiveException(String fileName, String message, boolean correct) {
		mFileName = fileName;
		mMessage = message;
		mCorrect = correct;
	}

	public FileReceiveException(String message, boolean done) {
		mFileName = "";
		mMessage = message;
		mCorrect = false;
		mDone = true;
	}

	public boolean isCorrectNeeded() {
		return mCorrect;
	}

	public String getFileName() {
		return mFileName;
	}

	public String getMessage() {
		return mMessage;
	}

	public void printFileName() {
		System.out.println(String.format("Exception for file: %s", mFileName));
	}

	public boolean isDone() {
		return mDone;
	}
}
