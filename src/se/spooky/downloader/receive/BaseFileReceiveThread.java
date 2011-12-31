package se.spooky.downloader.receive;

import se.spooky.downloader.url.Direction;

public abstract class BaseFileReceiveThread extends Thread {
	private Direction mDirection;

	public BaseFileReceiveThread(Direction direction) {
		mDirection = direction;
	}

	@Override
	public void run() {
		life();
		System.out.println("done: " + mDirection.toString());
		ThreadHandler.getThreadInstance().setDone(mDirection);
	}

	protected abstract void life();

	public Direction getDirection() {
		return mDirection;
	}

	public void setDirection(Direction direction) {
		mDirection = direction;
	}
}
