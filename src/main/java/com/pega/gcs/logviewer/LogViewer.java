/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.RecentFileJMenu;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.fringecommon.utilities.kyro.KryoSerializer;
import com.pega.gcs.logviewer.alert.AlertCheatSheetFrame;
import com.pega.gcs.logviewer.logfile.LogPattern;
import com.pega.gcs.logviewer.model.LogViewerSetting;

import gnu.getopt.Getopt;

public class LogViewer extends BaseFrame {

	private static final long serialVersionUID = 7083567192063249944L;

	private static final Log4j2Helper LOG = new Log4j2Helper(LogViewer.class);

	private static final String APPNAME = "Pega-LogViewer v3.0b3";

	private static final String FILE_CHOOSER_FILTER_DESC = "Log Files";

	protected static final String[] FILE_CHOOSER_FILTER_EXT = { "log", "" };

	private static final String FILE_CHOOSER_DIALOG_TITLE_PEGARULES = "Select PegaRULES log file";

	private LogViewerSetting logViewerSetting;

	private ArrayList<String> openFileList;

	private File selectedFile;

	private RecentFileJMenu recentFileJMenu;

	private RecentFileContainer recentFileContainer;

	private LogTabbedPane logTabbedPane;

	private AlertCheatSheetFrame alertCheatSheetFrame;

	/**
	 * @throws Exception
	 */
	public LogViewer() throws Exception {
		super();

		// setPreferredSize(new Dimension(1200, 700));

		setFocusTraversalKeysEnabled(false);

		pack();

		// setLocationRelativeTo(null);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		setVisible(true);

		// openFileList is loaded in initialize method
		if ((openFileList != null) && (openFileList.size() > 0)) {
			loadFile(openFileList);
		}

		LOG.info("LogViewer - Started");

	}

	protected LogViewerSetting getLogViewerSetting() {
		return logViewerSetting;
	}

	protected File getSelectedFile() {
		return selectedFile;
	}

	protected RecentFileContainer getRecentFileContainer() {
		return recentFileContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.fringe.common.gui.BaseFrame#initialize()
	 */
	@Override
	protected void initialize() throws Exception {

		byte[] byteArray;

		// get LogViewerSetting
		byteArray = GeneralUtilities.getPreferenceByteArray(LogViewer.class, PREF_SETTINGS);

		if (byteArray != null) {
			try {
				logViewerSetting = (LogViewerSetting) KryoSerializer.decompress(byteArray, LogViewerSetting.class);
			} catch (Exception e) {
				LOG.error("Error decompressing logViewerSetting.", e);
			}
		}

		if (logViewerSetting == null) {
			logViewerSetting = new LogViewerSetting();
			byteArray = KryoSerializer.compress(logViewerSetting);
			GeneralUtilities.setPreferenceByteArray(LogViewer.class, PREF_SETTINGS, byteArray);
		}

		// get OpenFileList
		byteArray = GeneralUtilities.getPreferenceByteArray(LogViewer.class, PREF_OPEN_FILE_LIST);

		if (byteArray != null) {
			try {
				openFileList = (ArrayList<String>) KryoSerializer.decompress(byteArray, ArrayList.class);
			} catch (Exception e) {
				LOG.error("Error decompressing open file list.", e);
			}
		}

		if (openFileList == null) {
			openFileList = new ArrayList<>();
			byteArray = KryoSerializer.compress(openFileList);
			GeneralUtilities.setPreferenceByteArray(LogViewer.class, PREF_OPEN_FILE_LIST, byteArray);
		}

		Set<LogPattern> pegaRuleslog4jPatternSet = logViewerSetting.getPegaRuleslog4jPatternSet();

		if (pegaRuleslog4jPatternSet == null) {
			pegaRuleslog4jPatternSet = LogPattern.getDefaultPegaRulesLog4jPatternSet();
			logViewerSetting.setPegaRuleslog4jPatternSet(pegaRuleslog4jPatternSet);
		}

		int capacity = logViewerSetting.getRecentItemsCount();

		recentFileContainer = new RecentFileContainer(getClass(), capacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.fringe.common.gui.BaseFrame#getMenuJMenuBar()
	 */
	@Override
	protected JMenuBar getMenuJMenuBar() {

		// ---- FILE ----
		JMenu fileJMenu = new JMenu("   File   ");

		fileJMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem pegaRULESJMenuItem = new JMenuItem("Load Pega Log File");

		pegaRULESJMenuItem.setMnemonic(KeyEvent.VK_L);
		pegaRULESJMenuItem.setToolTipText("Load PegaRULES or Alert Log File");

		ImageIcon ii = FileUtilities.getImageIcon(this.getClass(), "open.png");

		pegaRULESJMenuItem.setIcon(ii);

		pegaRULESJMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				File selectedFile = getSelectedFile();

				File aFile = openFileChooser(LogViewer.this, LogViewer.class, FILE_CHOOSER_DIALOG_TITLE_PEGARULES,
						Arrays.asList(FILE_CHOOSER_FILTER_EXT), FILE_CHOOSER_FILTER_DESC, selectedFile);

				if (aFile != null) {
					loadFile(aFile);
				}
			}
		});

		RecentFileJMenu recentFileJMenu = getRecentFileJMenu();

		JMenuItem clearRecentJMenuItem = new JMenuItem("Clear Recent");
		clearRecentJMenuItem.setMnemonic(KeyEvent.VK_C);
		clearRecentJMenuItem.setToolTipText("Clear Recent");

		clearRecentJMenuItem.setIcon(ii);

		clearRecentJMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				clearRecentPreferences();
				savePreferences();
			}
		});

		JMenuItem exitJMenuItem = new JMenuItem("Exit");
		exitJMenuItem.setMnemonic(KeyEvent.VK_X);
		exitJMenuItem.setToolTipText("Exit application");

		ii = FileUtilities.getImageIcon(this.getClass(), "exit.png");

		exitJMenuItem.setIcon(ii);

		exitJMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				exit(0);
			}
		});

		// fileJMenu.addSeparator();
		fileJMenu.add(pegaRULESJMenuItem);
		fileJMenu.add(recentFileJMenu);
		fileJMenu.add(clearRecentJMenuItem);
		fileJMenu.add(exitJMenuItem);

		// ---- EDIT ----
		JMenu editJMenu = new JMenu("   Edit   ");
		editJMenu.setMnemonic(KeyEvent.VK_E);

		JMenuItem alertCheatSheetJMenuItem = new JMenuItem("Alert Cheat Sheet");
		alertCheatSheetJMenuItem.setToolTipText("Alert Cheat Sheet");

		// ii = FileUtilities.getImageIcon(this.getClass(), "settings.png");
		// settingsJMenuItem.setIcon(ii);

		alertCheatSheetJMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (alertCheatSheetFrame == null) {
					alertCheatSheetFrame = new AlertCheatSheetFrame(APPNAME, getAppIcon(), LogViewer.this);
					alertCheatSheetFrame.addWindowListener(new WindowAdapter() {

						@Override
						public void windowClosed(WindowEvent e) {
							super.windowClosed(e);
							alertCheatSheetFrame = null;
						}
					});
					alertCheatSheetFrame.setVisible(true);
				} else {
					alertCheatSheetFrame.toFront();
				}
			}
		});

		editJMenu.add(alertCheatSheetJMenuItem);

		JMenuItem settingsJMenuItem = new JMenuItem("Settings");
		settingsJMenuItem.setToolTipText("Settings");

		ii = FileUtilities.getImageIcon(this.getClass(), "settings.png");
		settingsJMenuItem.setIcon(ii);

		settingsJMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				LogViewerSettingsDialog logViewerSettingsDialog = null;
				logViewerSettingsDialog = new LogViewerSettingsDialog(getLogViewerSetting(), getAppIcon(),
						LogViewer.this);

				logViewerSettingsDialog.setVisible(true);

				if (logViewerSettingsDialog.isSettingUpdated()) {

					int recentItemsCount = logViewerSettingsDialog.getRecentItemsCount();
					String charset = logViewerSettingsDialog.getSelectedCharset();
					boolean tailLogFile = logViewerSettingsDialog.isTailLogFile();
					boolean reloadPreviousFiles = logViewerSettingsDialog.isReloadPreviousFiles();

					LogViewerSetting logViewerSetting = getLogViewerSetting();

					logViewerSetting.setRecentItemsCount(recentItemsCount);
					logViewerSetting.setCharset(charset);
					logViewerSetting.setTailLogFile(tailLogFile);
					logViewerSetting.setReloadPreviousFiles(reloadPreviousFiles);
				}
			}
		});

		editJMenu.add(settingsJMenuItem);

		// ---- HELP ----
		JMenu helpJMenu = getHelpAboutJMenu();

		JMenuBar jMenuBar = new JMenuBar();
		jMenuBar.add(fileJMenu);
		jMenuBar.add(editJMenu);
		jMenuBar.add(helpJMenu);

		return jMenuBar;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.fringe.common.gui.BaseFrame#getMainJPanel()
	 */
	@Override
	protected JComponent getMainJPanel() {

		JPanel mainJPanel = new JPanel();

		LogTabbedPane logTabbedPane = getLogTabbedPane();

		mainJPanel.setLayout(new BorderLayout());

		mainJPanel.add(logTabbedPane, BorderLayout.CENTER);

		return mainJPanel;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.fringe.common.gui.BaseFrame#getAppName()
	 */
	@Override
	protected String getAppName() {
		return APPNAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.fringe.common.gui.BaseFrame#release()
	 */
	@Override
	protected void release() {

		savePreferences();

		LOG.info("LogViewer - Stopped");
	}

	protected void savePreferences() {
		// save recent file container
		recentFileContainer.saveRecentFilesPreferrence();

		// save logViewerSetting
		try {
			byte[] byteArray = KryoSerializer.compress(logViewerSetting);

			LOG.info("LogViewerSetting ByteSize: " + byteArray.length);

			GeneralUtilities.setPreferenceByteArray(this.getClass(), PREF_SETTINGS, byteArray);
		} catch (Exception e) {
			LOG.error("Error compressing logViewerSetting.", e);
		}

		// save openFileList
		saveOpenFileList();

	}

	private void saveOpenFileList() {

		try {

			boolean reloadPreviousFiles = logViewerSetting.isReloadPreviousFiles();

			if (reloadPreviousFiles) {
				LogTabbedPane logTabbedPane = getLogTabbedPane();
				openFileList = logTabbedPane.getOpenFileList();
			} else {
				openFileList = new ArrayList<>();
			}

			byte[] byteArray = KryoSerializer.compress(openFileList);

			LOG.info("Open File List ByteSize: " + byteArray.length);

			GeneralUtilities.setPreferenceByteArray(this.getClass(), PREF_OPEN_FILE_LIST, byteArray);

		} catch (Exception e) {
			LOG.error("Error compressing open file list.", e);
		}
	}

	public RecentFileJMenu getRecentFileJMenu() {

		if (recentFileJMenu == null) {

			recentFileJMenu = new RecentFileJMenu(recentFileContainer) {

				private static final long serialVersionUID = -5159590911380579230L;

				@Override
				public void onSelect(RecentFile recentFile) {

					String file = (String) recentFile.getAttribute(RecentFile.KEY_FILE);

					File aFile = new File(file);

					if (aFile.exists() && aFile.isFile() && aFile.canRead()) {

						loadFile(aFile);

					} else {

						JOptionPane.showMessageDialog(this, "File: " + aFile + " cannot be read.", "File not found",
								JOptionPane.ERROR_MESSAGE);

						getRecentFileContainer().deleteRecentFile(recentFile);
					}
				}
			};
		}

		return recentFileJMenu;
	}

	/**
	 * @return the logTabbedPane
	 */
	private LogTabbedPane getLogTabbedPane() {

		if (logTabbedPane == null) {
			logTabbedPane = new LogTabbedPane(logViewerSetting, recentFileContainer);
			logTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

			// logTabbedPane.setBorder(BorderFactory.createLineBorder(
			// MyColor.GRAY, 1));
		}
		return logTabbedPane;
	}

	protected void clearRecentPreferences() {
		recentFileContainer.clearRecentFilesPreferrence();
		logViewerSetting = new LogViewerSetting();
		openFileList = new ArrayList<>();
	}

	protected void loadFile(List<String> fileNameList) {

		for (String filename : fileNameList) {

			LOG.info("Processing file: " + filename);

			File aFile = new File(filename);

			if (aFile.exists() && aFile.isFile() && aFile.canRead()) {
				loadFile(aFile);
			} else {
				LOG.info("\"" + filename + "\" is not a file.");
			}
		}
	}

	protected void loadFile(File aFile) {

		this.selectedFile = aFile;

		LogTabbedPane logTabbedPane = getLogTabbedPane();

		try {

			logTabbedPane.loadFile(selectedFile);

			saveOpenFileList();

		} catch (Exception e) {
			LOG.error("Error loading file: " + selectedFile, e);

			JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile), "Error loading file: ",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void generateReport(List<String> fileNameList) {

		if (fileNameList.size() == 0) {
			LOG.info("no files to process");
		}

		for (String filename : fileNameList) {

			LOG.info("Processing file: " + filename);

			File aFile = new File(filename);

			if (aFile.exists() && aFile.isFile() && aFile.canRead()) {
				generateReport(aFile);
			} else {
				LOG.info("\"" + filename + "\" is not a file.");
			}
		}
	}

	private static void generateReport(File logFile) {
		// TODO - generate report
		LOG.info("Generate Report - not implemented yet!!!.");
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		LOG.info("Default Locale: " + Locale.getDefault() + " args length: " + args.length);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {

					if (args.length > 0) {

						List<String> fileNameList = new ArrayList<String>();

						boolean isReport = false;

						Getopt getopt = new Getopt("LogViewer", args, "f:r:h:?;");

						int c;
						String arg;

						while ((c = getopt.getopt()) != -1) {

							switch (c) {

							case 'f':

								int index = getopt.getOptind() - 1;
								// LOG.info("index -> " + index);

								while (index < args.length) {

									String next = args[index];
									index++;

									if (next.startsWith("-")) {
										break;
									} else {
										fileNameList.add(next);
									}
								}

								getopt.setOptind(index - 1);

								break;

							case 'r':
								arg = getopt.getOptarg();
								isReport = Boolean.parseBoolean(arg);

								break;

							case 'h':
								printUsageAndExit();
								break;

							case '?':
								printUsageAndExit();
								break;

							default:
								LOG.info("getopt() returned " + c);
								break;
							}
						}

						if (isReport) {
							generateReport(fileNameList);
						} else {
							LogViewer logViewer = new LogViewer();
							logViewer.loadFile(fileNameList);
						}
					} else {
						LogViewer logViewer = new LogViewer();
						logViewer.setVisible(true);
					}

				} catch (Exception e) {
					LOG.error("Error in LogViewer main.", e);
				}
			}
		});
	}

	protected static void printUsageAndExit() {
		String usageStr = "\t-f <space seperated list of file path> \n\t-r <true|false generate report, no UI \n\t-h <print command usage>";
		LOG.info("Usage Arguments: \n" + usageStr);
		System.exit(0);
	}

}
