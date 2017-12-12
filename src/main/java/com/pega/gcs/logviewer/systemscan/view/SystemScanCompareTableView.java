/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.NavigationPanel;
import com.pega.gcs.fringecommon.guiutilities.NavigationPanelController;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.guiutilities.TableCompareEntry;
import com.pega.gcs.fringecommon.guiutilities.TableWidthColumnModelListener;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerBar;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerModel;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogViewer;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.systemscan.CompareMarkerModel;
import com.pega.gcs.logviewer.systemscan.SystemScanColumn;
import com.pega.gcs.logviewer.systemscan.SystemScanCompareTask;
import com.pega.gcs.logviewer.systemscan.SystemScanTable;
import com.pega.gcs.logviewer.systemscan.SystemScanTableCompareModel;
import com.pega.gcs.logviewer.systemscan.SystemScanTableModel;
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanCompareTableView extends SystemScanView {

	private static final long serialVersionUID = -7206637173169538669L;

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanCompareTableView.class);

	private RecentFileContainer recentFileContainer;

	private LogViewerSetting logViewerSetting;

	private JButton fileOpenJButton;

	private JFileChooser generateCompareReportFileChooser;

	private JButton generateCompareReportJButton;

	private MarkerBar<ScanResultHotfixEntryKey> compareMarkerBar;

	private NavigationPanel<ScanResultHotfixEntryKey> navigationPanel;

	private NavigationPanelController<ScanResultHotfixEntryKey> navigationPanelController;

	private SystemScanTable systemScanTableLeft;

	private SystemScanTable systemScanTableRight;

	private JScrollPane jScrollPaneLeft;

	private JScrollPane jScrollPaneRight;

	public SystemScanCompareTableView(SystemScanTableModel systemScanTableModel, JPanel supplementUtilityJPanel,
			NavigationTableController<ScanResultHotfixEntryKey> navigationTableController,
			RecentFileContainer recentFileContainer, LogViewerSetting logViewerSetting) {

		super(systemScanTableModel, supplementUtilityJPanel, navigationTableController);

		this.recentFileContainer = recentFileContainer;
		this.logViewerSetting = logViewerSetting;

		SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
		navigationTableController.addCustomJTable(systemScanTableLeft);

		setLayout(new BorderLayout());

		JSplitPane compareJSplitPane = getCompareJSplitPane();
		JPanel compareMarkerJPanel = getCompareMarkerJPanel();

		add(compareJSplitPane, BorderLayout.CENTER);
		add(compareMarkerJPanel, BorderLayout.EAST);

		updateSupplementUtilityJPanel();
	}

	@Override
	protected void updateSupplementUtilityJPanel() {

		JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

		supplementUtilityJPanel.removeAll();

		LayoutManager layout = new BoxLayout(supplementUtilityJPanel, BoxLayout.LINE_AXIS);
		supplementUtilityJPanel.setLayout(layout);

		Dimension spacer = new Dimension(5, 10);
		Dimension endspacer = new Dimension(15, 10);

		JButton compareFileOpenJButton = getFileOpenJButton();
		JButton generateCompareReportJButton = getGenerateCompareReportJButton();

		JPanel compareFileOpenPanel = new JPanel();
		layout = new BoxLayout(compareFileOpenPanel, BoxLayout.LINE_AXIS);

		compareFileOpenPanel.add(Box.createRigidArea(spacer));
		compareFileOpenPanel.add(compareFileOpenJButton);
		compareFileOpenPanel.add(Box.createRigidArea(spacer));
		compareFileOpenPanel.add(generateCompareReportJButton);
		compareFileOpenPanel.add(Box.createRigidArea(spacer));
		// compareFileOpenPanel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHT_GRAY,
		// 1));

		JPanel navigationPanel = getNavigationPanel();

		supplementUtilityJPanel.add(Box.createHorizontalGlue());
		supplementUtilityJPanel.add(Box.createRigidArea(endspacer));
		supplementUtilityJPanel.add(compareFileOpenPanel);
		supplementUtilityJPanel.add(navigationPanel);
		supplementUtilityJPanel.add(Box.createRigidArea(endspacer));

		supplementUtilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		supplementUtilityJPanel.revalidate();
		supplementUtilityJPanel.repaint();
	}

	private RecentFileContainer getRecentFileContainer() {
		return recentFileContainer;
	}

	private LogViewerSetting getLogViewerSetting() {
		return logViewerSetting;
	}

	private JButton getFileOpenJButton() {

		if (fileOpenJButton == null) {

			fileOpenJButton = new JButton("Open Scan Results file for compare");

			ImageIcon ii = FileUtilities.getImageIcon(this.getClass(), "open.png");

			fileOpenJButton.setIcon(ii);

			Dimension size = new Dimension(250, 20);
			fileOpenJButton.setPreferredSize(size);
			// compareJButton.setMinimumSize(size);
			fileOpenJButton.setMaximumSize(size);
			fileOpenJButton.setHorizontalTextPosition(SwingConstants.LEADING);

			fileOpenJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

					File fileChooserBase = null;

					RecentFile recentFile = getSystemScanTableModel().getRecentFile();

					// check for previous comparison folder
					String leftPrevComparisionFilePath = (String) recentFile
							.getAttribute(LogViewer.RECENT_FILE_PREV_COMPARE_FILE);

					if ((leftPrevComparisionFilePath != null) && (!"".equals(leftPrevComparisionFilePath))) {

						File leftPrevComparisionFile = new File(leftPrevComparisionFilePath);

						if (leftPrevComparisionFile.exists()) {
							fileChooserBase = leftPrevComparisionFile;
						}
					}

					if (fileChooserBase == null) {
						// file open on the same folder as left file.
						String leftFilePath = systemScanTableModel.getFilePath();

						if ((leftFilePath != null) && (!"".equals(leftFilePath))) {
							fileChooserBase = new File(leftFilePath);
						}
					}

					FileFilter fileFilter = LogViewer.getSystemScanFileFilter();

					File aFile = LogViewer.openFileChooser(getFileOpenJButton(), LogViewer.class,
							LogViewer.SYSTEM_SCAN_FILE_CHOOSER_DIALOG_TITLE_PEGARULES, fileFilter, fileChooserBase);

					if (aFile != null) {

						// for compare tree, the compare tree view should be
						// inherited from compare table view, so that the same
						// right hand data get passed over.
						SystemScanTable systemScanTableRight = getSystemScanTableRight();
						SystemScanTableCompareModel systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableRight
								.getModel();

						RecentFileContainer recentFileContainer = getRecentFileContainer();
						String charset = getLogViewerSetting().getCharset();

						RecentFile compareRecentFile;
						compareRecentFile = recentFileContainer.getRecentFile(aFile, charset);

						// also reset the model and clears old stuff
						systemScanTableCompareModel.setRecentFile(compareRecentFile);

						// save the compare file path to main file
						recentFile.setAttribute(LogViewer.RECENT_FILE_PREV_COMPARE_FILE, aFile.getAbsolutePath());

						SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

						UIManager.put("ModalProgressMonitor.progressText", "Loading system scan file");

						Component parent = SystemScanCompareTableView.this;

						final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "",
								"Loaded 0 hotfixes (0%)", 0, 100);

						progressMonitor.setMillisToDecideToPopup(0);
						progressMonitor.setMillisToPopup(0);

						SystemScanCompareTask systemScanCompareTask = new SystemScanCompareTask(parent,
								systemScanTableModel, systemScanTableLeft, systemScanTableRight, progressMonitor) {

							@Override
							protected void done() {

								try {

									get();

									if (!isCancelled()) {
										SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
										SystemScanTable systemScanTableRight = getSystemScanTableRight();

										SystemScanTableCompareModel systemScanTableCompareModelLeft;
										systemScanTableCompareModelLeft = (SystemScanTableCompareModel) systemScanTableLeft
												.getModel();

										SystemScanTableCompareModel systemScanTableCompareModelRight;
										systemScanTableCompareModelRight = (SystemScanTableCompareModel) systemScanTableRight
												.getModel();

										getNavigationPanel().setEnabled(true);

										MarkerModel<ScanResultHotfixEntryKey> thisMarkerModel;
										thisMarkerModel = new CompareMarkerModel(MyColor.LIGHT_GREEN,
												systemScanTableCompareModelLeft);

										MarkerModel<ScanResultHotfixEntryKey> otherMarkerModel;
										otherMarkerModel = new CompareMarkerModel(Color.LIGHT_GRAY,
												systemScanTableCompareModelRight);

										MarkerBar<ScanResultHotfixEntryKey> markerBar = getCompareMarkerBar();
										markerBar.addMarkerModel(thisMarkerModel);
										markerBar.addMarkerModel(otherMarkerModel);

										syncScrollBars();
										getNavigationPanelController().updateState();

										JButton generateCompareReportJButton = getGenerateCompareReportJButton();
										generateCompareReportJButton.setEnabled(true);
									}
								} catch (Exception e) {
									LOG.error("Error performing compare task");

								} finally {
									progressMonitor.close();
								}
							}

						};

						systemScanCompareTask.execute();

						// SystemScanMainPanel.loadFile(systemScanTableCompareModel,
						// SystemScanCompareTableView.this,
						// true);
						//
						// applyModelCompare();
					}
				}
			});

		}

		return fileOpenJButton;
	}

	private JFileChooser getGenerateCompareReportFileChooser() {

		if (generateCompareReportFileChooser == null) {

			SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

			String filePath = systemScanTableModel.getFilePath();

			File file = new File(filePath);

			String fileName = FileUtilities.getNameWithoutExtension(file);

			StringBuffer sb = new StringBuffer();
			sb.append(fileName);
			sb.append("-");
			sb.append("patch");
			sb.append(".txt");

			String defaultReportFileName = sb.toString();

			File currentDirectory = file.getParentFile();

			File proposedFile = new File(currentDirectory, defaultReportFileName);

			generateCompareReportFileChooser = new JFileChooser(currentDirectory);

			generateCompareReportFileChooser.setDialogTitle("Save Compare report file (.txt)");
			generateCompareReportFileChooser.setSelectedFile(proposedFile);
			generateCompareReportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT File (TXT)", "txt");

			generateCompareReportFileChooser.setFileFilter(filter);
		}

		return generateCompareReportFileChooser;
	}

	private JButton getGenerateCompareReportJButton() {

		if (generateCompareReportJButton == null) {

			generateCompareReportJButton = new JButton("Generate Compare Report");

			Dimension size = new Dimension(200, 20);
			generateCompareReportJButton.setPreferredSize(size);
			generateCompareReportJButton.setMaximumSize(size);
			generateCompareReportJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showSaveReportHtmlChooser();
				}
			});

			generateCompareReportJButton.setEnabled(false);

		}

		return generateCompareReportJButton;
	}

	private MarkerBar<ScanResultHotfixEntryKey> getCompareMarkerBar() {

		if (compareMarkerBar == null) {
			NavigationPanelController<ScanResultHotfixEntryKey> navigationPanelController = getNavigationPanelController();
			compareMarkerBar = new MarkerBar<ScanResultHotfixEntryKey>(navigationPanelController, null);
		}

		return compareMarkerBar;
	}

	private NavigationPanel<ScanResultHotfixEntryKey> getNavigationPanel() {

		if (navigationPanel == null) {

			JLabel label = new JLabel("Compare:");

			NavigationPanelController<ScanResultHotfixEntryKey> compareNavigationPanelController = getNavigationPanelController();

			navigationPanel = new NavigationPanel<>(label, compareNavigationPanelController);
			navigationPanel.setEnabled(false);

		}

		return navigationPanel;
	}

	private NavigationPanelController<ScanResultHotfixEntryKey> getNavigationPanelController() {

		if (navigationPanelController == null) {

			navigationPanelController = new NavigationPanelController<ScanResultHotfixEntryKey>() {

				@Override
				public void navigateToRow(int startRowIndex, int endRowIndex) {

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
					SystemScanTable systemScanTableRight = getSystemScanTableRight();

					systemScanTableLeft.setRowSelectionInterval(startRowIndex, endRowIndex);
					systemScanTableLeft.scrollRowToVisible(startRowIndex);

					systemScanTableRight.setRowSelectionInterval(startRowIndex, endRowIndex);
					systemScanTableRight.scrollRowToVisible(startRowIndex);

					systemScanTableLeft.updateUI();
					systemScanTableRight.updateUI();
				}

				@Override
				public void scrollToKey(ScanResultHotfixEntryKey key) {

				}

				@Override
				public void first() {

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

					SystemScanTableCompareModel systemScanTableCompareModel;
					systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableLeft.getModel();

					TableCompareEntry tableCompareEntry;
					tableCompareEntry = systemScanTableCompareModel.compareFirst();

					int startEntry = tableCompareEntry.getStartEntry();
					int endEntry = tableCompareEntry.getEndEntry();

					navigateToRow(startEntry, endEntry);

					updateState();

				}

				@Override
				public void previous() {

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

					SystemScanTableCompareModel systemScanTableCompareModel;
					systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableLeft.getModel();

					int[] selectedrows = systemScanTableLeft.getSelectedRows();

					int selectedRow = -1;

					if (selectedrows.length > 0) {
						selectedRow = selectedrows[selectedrows.length - 1];
					}

					TableCompareEntry tableCompareEntry;
					tableCompareEntry = systemScanTableCompareModel.comparePrevious(selectedRow);

					int startEntry = tableCompareEntry.getStartEntry();
					int endEntry = tableCompareEntry.getEndEntry();

					navigateToRow(startEntry, endEntry);

					updateState();

				}

				@Override
				public void next() {

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

					SystemScanTableCompareModel systemScanTableCompareModel;
					systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableLeft.getModel();

					int[] selectedrows = systemScanTableLeft.getSelectedRows();

					int selectedRow = -1;

					if (selectedrows.length > 0) {
						selectedRow = selectedrows[selectedrows.length - 1];
					}

					TableCompareEntry tableCompareEntry;
					tableCompareEntry = systemScanTableCompareModel.compareNext(selectedRow);

					int startEntry = tableCompareEntry.getStartEntry();
					int endEntry = tableCompareEntry.getEndEntry();

					navigateToRow(startEntry, endEntry);

					updateState();

				}

				@Override
				public void last() {

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

					SystemScanTableCompareModel systemScanTableCompareModel;
					systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableLeft.getModel();

					TableCompareEntry tableCompareEntry;
					tableCompareEntry = systemScanTableCompareModel.compareLast();

					int startEntry = tableCompareEntry.getStartEntry();
					int endEntry = tableCompareEntry.getEndEntry();

					navigateToRow(startEntry, endEntry);

					updateState();

				}

				@Override
				public void updateState() {

					NavigationPanel<ScanResultHotfixEntryKey> navigationPanel = getNavigationPanel();

					JLabel dataJLabel = navigationPanel.getDataJLabel();
					JButton firstJButton = navigationPanel.getFirstJButton();
					JButton prevJButton = navigationPanel.getPrevJButton();
					JButton nextJButton = navigationPanel.getNextJButton();
					JButton lastJButton = navigationPanel.getLastJButton();

					SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

					SystemScanTableCompareModel systemScanTableCompareModel;
					systemScanTableCompareModel = (SystemScanTableCompareModel) systemScanTableLeft.getModel();

					int[] selectedrows = systemScanTableLeft.getSelectedRows();

					int selectedRow = -1;

					if (selectedrows.length > 0) {
						selectedRow = selectedrows[selectedrows.length - 1];
					}

					int compareCount = systemScanTableCompareModel.getCompareCount();
					int compareNavIndex = systemScanTableCompareModel.getCompareNavIndex();

					SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

					if (compareCount == 0) {
						selectedRowPosition = SelectedRowPosition.NONE;
					} else if (systemScanTableCompareModel.isCompareResultsWrap()) {
						selectedRowPosition = SelectedRowPosition.BETWEEN;
					} else {

						selectedRow = (selectedRow >= 0) ? (selectedRow) : 0;

						selectedRowPosition = systemScanTableCompareModel.getCompareSelectedRowPosition(selectedRow);
					}

					switch (selectedRowPosition) {

					case FIRST:
						firstJButton.setEnabled(false);
						prevJButton.setEnabled(false);
						nextJButton.setEnabled(true);
						lastJButton.setEnabled(true);
						break;

					case LAST:
						firstJButton.setEnabled(true);
						prevJButton.setEnabled(true);
						nextJButton.setEnabled(false);
						lastJButton.setEnabled(false);
						break;

					case BETWEEN:
						firstJButton.setEnabled(true);
						prevJButton.setEnabled(true);
						nextJButton.setEnabled(true);
						lastJButton.setEnabled(true);
						break;

					case NONE:
						firstJButton.setEnabled(false);
						prevJButton.setEnabled(false);
						nextJButton.setEnabled(false);
						lastJButton.setEnabled(false);
						break;
					default:
						break;
					}

					String text = String.format(SearchPanel.PAGES_FORMAT_STR, compareNavIndex, compareCount);

					dataJLabel.setText(text);

				}
			};
		}
		return navigationPanelController;
	}

	private SystemScanTable getSystemScanTableLeft() {

		if (systemScanTableLeft == null) {

			SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

			systemScanTableLeft = new SystemScanTable(systemScanTableModel, false);
			systemScanTableLeft.setFillsViewportHeight(true);

			// mouse listener is setup in getCompareJSplitPane

		}

		return systemScanTableLeft;
	}

	private SystemScanTable getSystemScanTableRight() {

		if (systemScanTableRight == null) {

			SystemScanTableCompareModel systemScanTableCompareModel = new SystemScanTableCompareModel(null, null);

			systemScanTableRight = new SystemScanTable(systemScanTableCompareModel);
			systemScanTableRight.setFillsViewportHeight(true);

			// mouse listener is setup in getCompareJSplitPane

		}

		return systemScanTableRight;
	}

	private JScrollPane getjScrollPaneLeft() {

		if (jScrollPaneLeft == null) {

			SystemScanTable systemScanTableLeft = getSystemScanTableLeft();

			jScrollPaneLeft = getJScrollPane(systemScanTableLeft);

		}

		return jScrollPaneLeft;
	}

	private JScrollPane getjScrollPaneRight() {

		if (jScrollPaneRight == null) {

			SystemScanTable systemScanTableRight = getSystemScanTableRight();

			jScrollPaneRight = getJScrollPane(systemScanTableRight);

		}

		return jScrollPaneRight;
	}

	private JScrollPane getJScrollPane(SystemScanTable systemScanTable) {

		JScrollPane jScrollpane = new JScrollPane(systemScanTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		systemScanTable.setPreferredScrollableViewportSize(jScrollpane.getPreferredSize());

		return jScrollpane;
	}

	private JSplitPane getCompareJSplitPane() {

		SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
		SystemScanTable systemScanTableRight = getSystemScanTableRight();

		// set selection model
		systemScanTableRight.setSelectionModel(systemScanTableLeft.getSelectionModel());

		// TraceTableCompareMouseListener scanResultTableCompareMouseListener = new
		// TraceTableCompareMouseListener(this);
		//
		// // add combined mouse listener
		// scanResultTableCompareMouseListener.addTraceTable(systemScanTableLeft);
		// scanResultTableCompareMouseListener.addTraceTable(systemScanTableRight);
		//
		// systemScanTableLeft.addMouseListener(scanResultTableCompareMouseListener);
		// systemScanTableRight.addMouseListener(scanResultTableCompareMouseListener);

		// setup column model listener
		TableWidthColumnModelListener tableWidthColumnModelListener;
		tableWidthColumnModelListener = new TableWidthColumnModelListener();
		tableWidthColumnModelListener.addTable(systemScanTableLeft);
		tableWidthColumnModelListener.addTable(systemScanTableRight);

		systemScanTableLeft.getColumnModel().addColumnModelListener(tableWidthColumnModelListener);
		systemScanTableRight.getColumnModel().addColumnModelListener(tableWidthColumnModelListener);

		// setup JScrollBar
		JScrollPane jScrollPaneLeft = getjScrollPaneLeft();
		JScrollPane jScrollPaneRight = getjScrollPaneRight();

		JPanel scanResultTablePanelLeft = getSingleTableJPanel(jScrollPaneLeft, systemScanTableLeft);
		JPanel scanResultTablePanelRight = getSingleTableJPanel(jScrollPaneRight, systemScanTableRight);

		JSplitPane compareJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scanResultTablePanelLeft,
				scanResultTablePanelRight);

		compareJSplitPane.setContinuousLayout(true);
		// compareJSplitPane.setDividerLocation(260);
		compareJSplitPane.setResizeWeight(0.5);

		// not movable divider
		// compareJSplitPane.setEnabled(false);

		return compareJSplitPane;
	}

	private JPanel getCompareMarkerJPanel() {

		JPanel compareMarkerJPanel = new JPanel();
		compareMarkerJPanel.setLayout(new BorderLayout());

		Dimension topDimension = new Dimension(16, 60);

		JLabel topSpacer = new JLabel();
		topSpacer.setPreferredSize(topDimension);
		topSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		Dimension bottomDimension = new Dimension(16, 35);

		JLabel bottomSpacer = new JLabel();
		bottomSpacer.setPreferredSize(bottomDimension);
		bottomSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		MarkerBar<ScanResultHotfixEntryKey> compareMarkerBar = getCompareMarkerBar();

		compareMarkerJPanel.add(topSpacer, BorderLayout.NORTH);
		compareMarkerJPanel.add(compareMarkerBar, BorderLayout.CENTER);
		compareMarkerJPanel.add(bottomSpacer, BorderLayout.SOUTH);

		return compareMarkerJPanel;

	}

	private JPanel getSingleTableJPanel(JScrollPane scanResultTableScrollpane, SystemScanTable systemScanTable) {

		JPanel singleTableJPanel = new JPanel();

		singleTableJPanel.setLayout(new BorderLayout());

		JTextField statusBar = getStatusBar();

		SearchPanel<ScanResultHotfixEntryKey> searchPanel = getSearchPanel(systemScanTable);

		SystemScanTableModel systemScanTableModel = (SystemScanTableModel) systemScanTable.getModel();

		JPanel tableJPanel = getTableDataJPanel(systemScanTableModel, scanResultTableScrollpane);
		JPanel statusBarJPanel = getStatusBarJPanel(statusBar);

		singleTableJPanel.add(searchPanel, BorderLayout.NORTH);
		singleTableJPanel.add(tableJPanel, BorderLayout.CENTER);
		singleTableJPanel.add(statusBarJPanel, BorderLayout.SOUTH);

		systemScanTableModel.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				String propertyName = evt.getPropertyName();

				if ("message".equals(propertyName)) {
					Message message = (Message) evt.getNewValue();
					setMessage(statusBar, message);
				}

			}
		});

		return singleTableJPanel;
	}

	private JTextField getStatusBar() {

		JTextField statusBar = new JTextField();
		statusBar.setEditable(false);
		statusBar.setBackground(null);
		statusBar.setBorder(null);

		return statusBar;
	}

	private JPanel getStatusBarJPanel(JTextField statusBar) {

		JPanel statusBarJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(statusBarJPanel, BoxLayout.LINE_AXIS);
		statusBarJPanel.setLayout(layout);

		Dimension spacer = new Dimension(5, 16);

		statusBarJPanel.add(Box.createRigidArea(spacer));
		statusBarJPanel.add(statusBar);
		statusBarJPanel.add(Box.createRigidArea(spacer));

		statusBarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return statusBarJPanel;

	}

	protected JPanel getTableDataJPanel(SystemScanTableModel systemScanTableModel, JScrollPane tableScrollpane) {

		JPanel tableJPanel = new JPanel();
		tableJPanel.setLayout(new BorderLayout());

		JPanel markerBarPanel = getMarkerBarPanel(systemScanTableModel);

		tableJPanel.add(tableScrollpane, BorderLayout.CENTER);
		tableJPanel.add(markerBarPanel, BorderLayout.EAST);

		return tableJPanel;
	}

	private SearchPanel<ScanResultHotfixEntryKey> getSearchPanel(SystemScanTable systemScanTable) {

		final SystemScanTableModel systemScanTableModel = (SystemScanTableModel) systemScanTable.getModel();

		NavigationTableController<ScanResultHotfixEntryKey> navigationTableController;
		navigationTableController = new NavigationTableController<ScanResultHotfixEntryKey>(systemScanTableModel);

		navigationTableController.addCustomJTable(systemScanTable);

		SearchPanel<ScanResultHotfixEntryKey> searchPanel;
		searchPanel = new SearchPanel<ScanResultHotfixEntryKey>(navigationTableController,
				systemScanTableModel.getSearchModel());

		return searchPanel;
	}

	private void syncScrollBars() {

		JScrollPane jScrollPaneLeft = getjScrollPaneLeft();
		JScrollPane jScrollPaneRight = getjScrollPaneRight();

		JScrollBar jScrollBarLeftH = jScrollPaneLeft.getHorizontalScrollBar();
		JScrollBar jScrollBarLeftV = jScrollPaneLeft.getVerticalScrollBar();
		JScrollBar jScrollBarRightH = jScrollPaneRight.getHorizontalScrollBar();
		JScrollBar jScrollBarRightV = jScrollPaneRight.getVerticalScrollBar();

		jScrollBarRightH.setModel(jScrollBarLeftH.getModel());
		jScrollBarRightV.setModel(jScrollBarLeftV.getModel());
	}

	private void showSaveReportHtmlChooser() {

		File exportFile = null;

		JFileChooser fileChooser = getGenerateCompareReportFileChooser();

		int returnValue = fileChooser.showSaveDialog(this);

		if (returnValue == JFileChooser.APPROVE_OPTION) {

			exportFile = fileChooser.getSelectedFile();

			returnValue = JOptionPane.YES_OPTION;

			if (exportFile.exists()) {

				returnValue = JOptionPane.showConfirmDialog(null, "Replace Existing File?", "File Exists",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			}

			if (returnValue == JOptionPane.YES_OPTION) {

				String reportHTML = getCompareReport();

				try {
					FileUtils.writeStringToFile(exportFile, reportHTML, getSystemScanTableModel().getCharset());
				} catch (IOException e) {
					LOG.error("Error generating report: " + exportFile, e);
				}

			}
		}
	}

	private String getCompareReport() {

		StringBuffer compareReportSB = new StringBuffer();

		List<SystemScanColumn> systemScanColumns = new ArrayList<>();
		systemScanColumns.add(SystemScanColumn.HOTFIX_ID);
		systemScanColumns.add(SystemScanColumn.HOTFIX_DESCRIPTION);

		SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
		SystemScanTable systemScanTableRight = getSystemScanTableRight();

		SystemScanTableCompareModel systemScanTableCompareModelLeft;
		systemScanTableCompareModelLeft = (SystemScanTableCompareModel) systemScanTableLeft.getModel();
		String fileLeft = systemScanTableCompareModelLeft.getFilePath();
		ScanResult scanResultLeft = systemScanTableCompareModelLeft.getScanResult();

		SystemScanTableCompareModel systemScanTableCompareModelRight;
		systemScanTableCompareModelRight = (SystemScanTableCompareModel) systemScanTableRight.getModel();
		String fileRight = systemScanTableCompareModelRight.getFilePath();

		ScanResult scanResultRight = systemScanTableCompareModelRight.getScanResult();

		String leftHText = "==== %d Hotfixes missing on environment for Left scan file %s ====";
		String rightHText = "==== %d Hotfixes missing on environment for Right scan file %s ====";

		List<ScanResultHotfixEntryKey> compareMarkerListLeft;
		compareMarkerListLeft = systemScanTableCompareModelLeft.getCompareMarkerList();

		List<ScanResultHotfixEntryKey> compareMarkerListRight;
		compareMarkerListRight = systemScanTableCompareModelRight.getCompareMarkerList();

		leftHText = String.format(leftHText, compareMarkerListLeft.size(), fileLeft);
		rightHText = String.format(rightHText, compareMarkerListRight.size(), fileRight);

		compareReportSB.append(leftHText);
		compareReportSB.append(System.getProperty("line.separator"));

		// Left entry pick data from Right model
		List<ScanResultHotfixEntryKey> scanResultHotfixEntryKeyListLeft;
		scanResultHotfixEntryKeyListLeft = new ArrayList<>();

		for (ScanResultHotfixEntryKey scanResultHotfixEntryKey : compareMarkerListLeft) {
			for (ScanResultHotfixEntryKey key : scanResultRight.getScanResultHotfixEntryMap().keySet()) {

				if (scanResultHotfixEntryKey.getId() == key.getId()) {
					scanResultHotfixEntryKeyListLeft.add(key);
					break;
				}
			}
		}

		String compareReportLeft = systemScanTableCompareModelRight
				.getSelectedColumnsData(scanResultHotfixEntryKeyListLeft, systemScanColumns);
		compareReportSB.append(compareReportLeft);

		compareReportSB.append(System.getProperty("line.separator"));
		compareReportSB.append(rightHText);
		compareReportSB.append(System.getProperty("line.separator"));

		// Right entry pick data from Left model
		List<ScanResultHotfixEntryKey> scanResultHotfixEntryKeyListRight;
		scanResultHotfixEntryKeyListRight = new ArrayList<>();

		for (ScanResultHotfixEntryKey scanResultHotfixEntryKey : compareMarkerListRight) {
			for (ScanResultHotfixEntryKey key : scanResultLeft.getScanResultHotfixEntryMap().keySet()) {

				if (scanResultHotfixEntryKey.getId() == key.getId()) {
					scanResultHotfixEntryKeyListRight.add(key);
					break;
				}
			}
		}

		String compareReportRight = systemScanTableCompareModelLeft
				.getSelectedColumnsData(scanResultHotfixEntryKeyListRight, systemScanColumns);
		compareReportSB.append(compareReportRight);

		return compareReportSB.toString();
	}

}
