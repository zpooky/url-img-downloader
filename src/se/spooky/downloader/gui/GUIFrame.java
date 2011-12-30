package se.spooky.downloader.gui;

import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import se.spooky.downloader.interfce.GUIFrameInterface;
import se.spooky.downloader.interfce.GUIFrameMainPanelInterface;
import se.spooky.downloader.receive.FileReceiveThread;
import se.spooky.downloader.url.Direction;
import se.spooky.downloader.util.Unique;
import se.spooky.downloader.util.Util;

/**
 * REQUIREMENTS
 * when prev from ex. 10 next is 9 if 9 is not right and 09 is set standard to 09 not 9
 * a message thread for receiving newly downloaded files and do a checksum check for that file directly
 * when done write 'done with URL' to label in main panel
 * improve correction()
 * do not automaticly redirect to mainPanel make a done button to let the user se the log
 * resizeable?
 * A queue of url to make it automaticle jump to next if it is still running
 * check 404
 */
public class GUIFrame extends JFrame implements GUIFrameInterface, GUIFrameMainPanelInterface {
	private static final long serialVersionUID = 1L;
	private static final String IMAGE_ROOT = "img/";
	private boolean[] mThreadDone;
	private SearchProgressPanel mSarchProgressPanel;
	private File mRootFolder;
	private URL mUrl;
	private JButton mDoneAbortButton;
	private FileReceiveThread[] mFileReceiveThread;

	private class MainPanel extends JPanel {
		private GUIFrameMainPanelInterface mGuiFrame;
		private static final long serialVersionUID = 1L;

		public MainPanel(GUIFrameMainPanelInterface guiFrame) {
			mGuiFrame = guiFrame;
			mFileReceiveThread = new FileReceiveThread[2];
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setLayout(new GridLayout(5, 4, 5, 5));
			init();
		}

		public void init() {
			final JTextField urlTextField = new JTextField();
			final JLabel status = new JLabel();
			add(urlTextField);
			JButton startButton = new JButton("Start");
			startButton.setBounds(200, 10, 50, 30);
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					onClick();
				}

				private void onClick() {
					if (urlTextField.getText().isEmpty()) {
						status.setText("URL can not be empty.");
					} else {
						try {
							mUrl = new URL(urlTextField.getText());
							status.setText("");
							mGuiFrame.onStartButton(mUrl);
						} catch (MalformedURLException e) {
							status.setText("Not a valid URL.");
						}
					}
				}
			});
			add(startButton);
			add(status);
		}
	}

	private class SearchProgressPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private TextArea mProgressLog;
		private JProgressBar mProgressBar;

		public SearchProgressPanel() {
			setLayout(new GridLayout(0, 1));
			init();
		}

		public void init() {
			mProgressBar = new JProgressBar();
			mProgressBar.setIndeterminate(true);
			add(mProgressBar);
			mDoneAbortButton = new JButton("Abort");
			mDoneAbortButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						mFileReceiveThread[0].interrupt();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						mFileReceiveThread[1].interrupt();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					getContentPane().removeAll();
					mSarchProgressPanel.revalidate();
					gotoMain();
				}
			});
			add(mDoneAbortButton);
			final JLabel status = new JLabel();
			status.setText("Dl from: " + mUrl.getHost());
			add(status);
			mProgressLog = new TextArea(20, 20);
			add(mProgressLog);
		}

		public void addLogMessage(String message) {
			String text = mProgressLog.getText().isEmpty() ? message : '\n' + message;
			mProgressLog.append(text);
		}
	}

	public GUIFrame() {
		setTitle("URL Image Downloader");
		setSize(300, 200);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
	}

	public void init() {
		File file = new File(IMAGE_ROOT);
		if (!file.isDirectory()) {
			file.mkdir();
		}
		mThreadDone = new boolean[2];
		mThreadDone[0] = false;
		mThreadDone[1] = false;
		MainPanel mainPanel = new MainPanel(this);
		getContentPane().add(mainPanel);
		mainPanel.revalidate();
	}

	public void done() {
		new Unique(mRootFolder).start();
		mDoneAbortButton.setText("Done");
		mDoneAbortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				init();
			}
		});
	}

	public void gotoMain() {
		getContentPane().removeAll();
		init();
	}

	@Override
	public void onStartButton(URL url) {
		getContentPane().removeAll();
		mSarchProgressPanel = new SearchProgressPanel();
		getContentPane().add(mSarchProgressPanel);
		mSarchProgressPanel.revalidate();
		String rootFolder = String.format("%s%s%s", IMAGE_ROOT, url.getHost(), File.separator);
		mRootFolder = new File(rootFolder);
		mRootFolder.mkdir();
		try {
			Util.makeDataFile(mRootFolder, url.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mFileReceiveThread[0] = new FileReceiveThread(this, url, Direction.NEXT, rootFolder);
		mFileReceiveThread[0].start();
		mFileReceiveThread[1] = new FileReceiveThread(this, url, Direction.PREV, rootFolder);
		mFileReceiveThread[1].start();
	}

	@Override
	public synchronized void setFileReceiveDone(Direction direction) {
		mThreadDone[direction.getId()] = true;
		if (mThreadDone[0] && mThreadDone[1]) {
			done();
		}
	}

	@Override
	public synchronized void addLogMessage(String message) {
		mSarchProgressPanel.addLogMessage(message);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GUIFrame guiFrame = new GUIFrame();
				guiFrame.setVisible(true);
			}
		});
	}
}