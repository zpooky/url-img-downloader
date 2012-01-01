package se.spooky.downloader.gui;

import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import se.spooky.downloader.receive.ThreadHandler;
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
	private static final Pattern FOLDER_URL_REPLACE = Pattern.compile("[\"*/:<>?\\|]");
	private static final String IMAGE_ROOT = "img" + File.separator;
	private SearchProgressPanel mSarchProgressPanel;
	private File mRootFolder;
	private URL mUrl;
	private JButton mDoneAbortButton;

	private class MainPanel extends JPanel {
		private GUIFrameMainPanelInterface mGuiFrame;
		private static final long serialVersionUID = 1L;

		public MainPanel(GUIFrameMainPanelInterface guiFrame) {
			mGuiFrame = guiFrame;
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
					ThreadHandler.getInstance(GUIFrame.this).killAll();
					getContentPane().removeAll();
					mSarchProgressPanel.revalidate();
					gotoMain();
				}
			});
			add(mDoneAbortButton);
			final JLabel status = new JLabel();
			status.setText("Dl from: " + mUrl.getHost());
			add(status);
			mProgressLog = new TextArea(2, 1);
			add(mProgressLog);
			JPanel jPanel = new JPanel();
			jPanel.setLayout(new GridLayout(0, 5));
			final JTextField addURLText = new JTextField(3);
			jPanel.add(addURLText);
			final JButton addURLButton = new JButton("+");
			addURLButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = addURLText.getText();
					addURLText.setText("");
					URL url;
					try {
						url = new URL(text);
						ThreadHandler.getInstance(GUIFrame.this).handle(Direction.NEXT, url, getRootFolder(url));
						ThreadHandler.getInstance(GUIFrame.this).handle(Direction.PREV, url, getRootFolder(url));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			});
			// addUrl.setPreferredSize(new Dimension(10, 10));
			jPanel.add(addURLButton);
			add(jPanel);
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
		String rootFolder = getRootFolder(url);
		mRootFolder = new File(rootFolder);
		mRootFolder.mkdir();
		try {
			Util.makeDataFile(mRootFolder, url.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ThreadHandler fileReceiveThreadHandler = ThreadHandler.getInstance(GUIFrame.this);
		fileReceiveThreadHandler.handle(Direction.NEXT, url, rootFolder);
		fileReceiveThreadHandler.handle(Direction.PREV, url, rootFolder);
	}

	@Override
	public synchronized void setFileReceiveDone(Direction direction) {
		if (ThreadHandler.getInstance(GUIFrame.this).isDone()) {
			done();
		}
	}

	@Override
	public synchronized void addLogMessage(String message) {
		mSarchProgressPanel.addLogMessage(message);
	}

	private static String getRootFolder(URL url) {
		String folderAppend = url.getPath();
		int lastIndexPath = folderAppend.lastIndexOf("/");
		if (lastIndexPath != -1) {
			folderAppend = folderAppend.substring(0, lastIndexPath);
		}
		Matcher matcher = FOLDER_URL_REPLACE.matcher(folderAppend);
		folderAppend = matcher.replaceAll(".");
		String host = url.getHost();
		if (host.substring(0, 4).equalsIgnoreCase("www.")) {
			host = host.substring(4);
		}
		int lastIndexHost = host.lastIndexOf(".");
		if (lastIndexHost != -1) {
			host = host.substring(0, lastIndexHost);
		}
		return String.format("%s%s%s%s", IMAGE_ROOT, host, folderAppend, File.separator);
	}

	public static void main(String[] args) {
		/*
		 * try {
		 * System.out.println(Util.isOk(new URL("http://localhost/test/img/02.bmp")) ? "true" : "false");
		 * } catch (MalformedURLException e) {
		 * e.printStackTrace();
		 * }
		 */
		/*
		 * System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:9.0) Gecko/20100101 Firefox/9.0");
		 * Properties properties2 = System.getProperties();
		 * Iterator<Entry<Object, Object>> iterator = properties2.entrySet().iterator();
		 * while (iterator.hasNext()) {
		 * Entry<Object, Object> next = iterator.next();
		 * System.out.println(((String) next.getKey()) + " " + ((String) next.getValue()));
		 * }
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GUIFrame guiFrame = new GUIFrame();
				guiFrame.setVisible(true);
			}
		});
	}
}