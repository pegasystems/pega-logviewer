/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogEntryPanel;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.ThreadDumpPanel;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryData;
import com.pega.gcs.logviewer.model.SystemStart;
import com.pega.gcs.logviewer.parser.LogSystemStartParser;

public class Log4jSystemReportDialog extends SystemReportDialog {

	private static final long serialVersionUID = -9039423327032627896L;

	private static final Log4j2Helper LOG = new Log4j2Helper(Log4jSystemReportDialog.class);

	private static final String EXPAND_ALL_ACTION = "Expand all nodes";

	private static final String COLLAPSE_ALL_ACTION = "Collapse all nodes";

	private JList<SystemStart> systemStartJList;

	private JTable systemStartJTable;

	private DefaultListModel<ExceptionLeafNode> errorDefaultListModel;

	private DefaultTreeModel exceptionDefaultTreeModel;

	private JList<ExceptionLeafNode> errorJList;

	private JTree exceptionJTree;

	private JPanel errorAreaJPanel;

	private JButton expandAllExceptionJButton;

	private JList<String> threadDumpJList;

	private JPanel threadDumpJPanel;

	private List<Integer> errorLogEntryIndexList;

	private AtomicInteger threadDumpSelectedTab;

	public Log4jSystemReportDialog(LogTableModel logTableModel,
			NavigationTableController<Integer> navigationTableController, ImageIcon appIcon, Component parent) {

		super("System Overview - " + logTableModel.getModelName(), logTableModel, navigationTableController, appIcon,
				parent);

		threadDumpSelectedTab = new AtomicInteger(0);

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(1400, 800));

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setExtendedState(Frame.MAXIMIZED_BOTH);

	}

	protected List<Integer> getErrorLogEntryIndexList() {
		return errorLogEntryIndexList;
	}

	@Override
	protected void buildTabs() {

		try {

			addDefaultTabs();

			LogTableModel logTableModel = getLogTableModel();

			Log4jLogEntryModel log4jLogEntryModel;
			log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

			List<SystemStart> systemStartList = log4jLogEntryModel.getSystemStartList();
			Map<String, List<Integer>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();
			List<Integer> threadDumpIndexList = log4jLogEntryModel.getThreadDumpLogEntryIndexList();

			boolean containsSysStart = systemStartList.size() > 0;
			boolean containsError = errorLogEntryIndexMap.size() > 0;
			boolean containsThreadDump = threadDumpIndexList.size() > 0;

			if (containsSysStart) {

				JComponent systemStartTabJComponent = getSystemStartTabJComponent();

				String tabLabelText = "System Start";
				Dimension labelDim = new Dimension(140, 26);

				addTab(systemStartTabJComponent, tabLabelText, labelDim);

			}

			if (containsError) {

				initialiseErrorListModel();

				JComponent errorTabJComponent = getErrorTabJComponent();

				String tabLabelText = "Errors";
				Dimension labelDim = new Dimension(140, 26);

				addTab(errorTabJComponent, tabLabelText, labelDim);
			}

			if (containsThreadDump) {

				JComponent threadDumpTabJComponent = getThreadDumpTabJComponent();

				String tabLabelText = "Thread Dumps";
				Dimension labelDim = new Dimension(140, 26);

				addTab(threadDumpTabJComponent, tabLabelText, labelDim);
			}

		} catch (Exception e) {
			LOG.error("Error building overview tabs.", e);
		}
	}

	private JComponent getSystemStartTabJComponent() {

		JList<SystemStart> systemStartJList = getSystemStartJList();
		JTable systemStartJTable = getSystemStartJTable();

		JScrollPane systemStartJListJScrollPane = new JScrollPane(systemStartJList);
		JScrollPane systemStartJTableJScrollPane = new JScrollPane(systemStartJTable);

		JSplitPane systemStartJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, systemStartJListJScrollPane,
				systemStartJTableJScrollPane);

		systemStartJSplitPane.setDividerLocation(400);

		return systemStartJSplitPane;
	}

	private JComponent getErrorTabJComponent() {

		JComponent errorJComponent = getErrorJComponent();
		JPanel errorAreaJPanel = getErrorAreaJPanel();

		JSplitPane errorJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, errorJComponent, errorAreaJPanel);

		errorJSplitPane.setDividerLocation(400);

		return errorJSplitPane;
	}

	private JComponent getThreadDumpTabJComponent() {

		JList<String> threadDumpJList = getThreadDumpJList();
		JPanel threadDumpJPanel = getThreadDumpJPanel();

		JScrollPane threadDumpJListJScrollPane = new JScrollPane(threadDumpJList);

		JSplitPane threadDumpJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, threadDumpJListJScrollPane,
				threadDumpJPanel);

		return threadDumpJSplitPane;
	}

	/**
	 * @return the systemStartJList
	 */
	protected JList<SystemStart> getSystemStartJList() {

		if (systemStartJList == null) {

			LogTableModel logTableModel = getLogTableModel();

			final Log4jLogEntryModel log4jLogEntryModel;
			log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

			List<SystemStart> systemStartList = log4jLogEntryModel.getSystemStartList();

			DefaultListModel<SystemStart> dlm = new DefaultListModel<SystemStart>();

			for (SystemStart systemStart : systemStartList) {
				dlm.addElement(systemStart);
			}

			systemStartJList = new JList<SystemStart>(dlm);
			systemStartJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			ListSelectionListener lsl = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList<SystemStart> systemStartJList = getSystemStartJList();

					if ((!e.getValueIsAdjusting()) && (systemStartJList.getSelectedIndex() != -1)) {

						SystemStart systemStart = (SystemStart) systemStartJList.getSelectedValue();

						LogSystemStartParser logSystemStartParser = LogSystemStartParser.getInstance();

						Map<String, String> systemStartMap;
						systemStartMap = logSystemStartParser.getSystemStartMap(systemStart, log4jLogEntryModel);

						DefaultTableModel dtm = new DefaultTableModel(new String[] { "Key", "Value" }, 0);

						for (Map.Entry<String, String> entrySet : systemStartMap.entrySet()) {

							dtm.addRow(new String[] { entrySet.getKey(), entrySet.getValue() });
						}

						JTable systemStartJTable = getSystemStartJTable();
						systemStartJTable.setModel(dtm);

						TableColumnModel tcm = systemStartJTable.getColumnModel();

						for (int column = 0; column < dtm.getColumnCount(); column++) {

							TableColumn tc = tcm.getColumn(column);
							DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();

							if (column == 0) {
								tc.setPreferredWidth(280);
								tc.setWidth(270);
							} else {
								tc.setPreferredWidth(500);
								tc.setWidth(450);
							}

							tc.setCellRenderer(dtcr);
						}

						systemStartJTable.updateUI();
					}
				}
			};

			systemStartJList.addListSelectionListener(lsl);

			systemStartJList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (e.getClickCount() == 2) {

						JList<SystemStart> systemStartJList = getSystemStartJList();

						int clickedIndex = systemStartJList.locationToIndex(e.getPoint());

						List<SystemStart> systemStartList = log4jLogEntryModel.getSystemStartList();

						SystemStart systemStart = systemStartList.get(clickedIndex);

						Integer logEntryIndex = systemStart.getBeginIndex();

						NavigationTableController<Integer> navigationTableController;
						navigationTableController = getNavigationTableController();

						navigationTableController.scrollToKey(logEntryIndex);

					} else {
						super.mouseClicked(e);
					}
				}

			});

			DefaultListCellRenderer dlcr = getDefaultListCellRenderer();

			systemStartJList.setCellRenderer(dlcr);

			systemStartJList.setFixedCellHeight(20);
		}

		return systemStartJList;
	}

	/**
	 * @return the systemStartJTable
	 */
	protected JTable getSystemStartJTable() {

		if (systemStartJTable == null) {

			DefaultTableModel dtm = new DefaultTableModel();

			systemStartJTable = new JTable(dtm);

			systemStartJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			systemStartJTable.setRowHeight(20);

			JTableHeader tableHeader = systemStartJTable.getTableHeader();

			DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER);

			Font existingFont = tableHeader.getFont();
			String existingFontName = existingFont.getName();
			int existFontSize = existingFont.getSize();
			Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
			tableHeader.setFont(newFont);

			tableHeader.setReorderingAllowed(false);

		}

		return systemStartJTable;
	}

	private void initialiseErrorListModel() {

		LogTableModel logTableModel = getLogTableModel();

		Log4jLogEntryModel log4jLogEntryModel;
		log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

		Map<String, List<Integer>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();

		Map<Integer, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

		DateFormat displayDateFormat = log4jLogEntryModel.getDisplayDateFormat();

		errorDefaultListModel = new DefaultListModel<ExceptionLeafNode>();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("TD");
		exceptionDefaultTreeModel = new DefaultTreeModel(root);

		errorLogEntryIndexList = new ArrayList<Integer>();

		Map<String, DefaultMutableTreeNode> exceptionNodeMap;
		exceptionNodeMap = new TreeMap<String, DefaultMutableTreeNode>();

		Map<Integer, ExceptionLeafNode> errorLogElementMap = new TreeMap<Integer, ExceptionLeafNode>();

		List<String> logEntryColumnList = log4jLogEntryModel.getLogEntryColumnList();

		int messageIndex = logEntryColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());

		for (String errorText : errorLogEntryIndexMap.keySet()) {

			DefaultMutableTreeNode dmtn = exceptionNodeMap.get(errorText);

			if (dmtn == null) {

				dmtn = new DefaultMutableTreeNode(errorText) {

					private static final long serialVersionUID = -4512307930740847502L;

					@Override
					public String toString() {

						String userObjectStr = super.toString();
						int count = getChildCount();
						return userObjectStr + " (" + count + ")";
					}
				};

				root.add(dmtn);
				exceptionNodeMap.put(errorText, dmtn);
			}

			List<Integer> errorLEIndexList;

			errorLEIndexList = errorLogEntryIndexMap.get(errorText);

			for (Integer errorLogEntryIndex : errorLEIndexList) {

				LogEntry logEntry = logEntryMap.get(errorLogEntryIndex);

				String timeText = null;

				Date logEntryDate = logEntry.getLogEntryDate();

				// date can be null in case of corrupted log file.
				if (logEntryDate != null) {
					timeText = displayDateFormat.format(logEntryDate);
				}

				LogEntryData logEntryData = logEntry.getLogEntryData();

				String message = logEntryData.getLogEntryValueList().get(messageIndex);

				String element = "Time [" + timeText + "] Line No [" + errorLogEntryIndex + "] [" + message + "]";

				ExceptionLeafNode eln = new ExceptionLeafNode(element, logEntry);

				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(eln);

				dmtn.add(newChild);

				errorLogElementMap.put(errorLogEntryIndex, eln);

			}

		}

		int errorCounter = 1;

		for (Integer errorIndex : errorLogElementMap.keySet()) {

			ExceptionLeafNode element = errorLogElementMap.get(errorIndex);
			element.setCounter(errorCounter);

			errorLogEntryIndexList.add(errorIndex);
			errorDefaultListModel.addElement(element);

			errorCounter++;

		}

	}

	private JComponent getErrorJComponent() {

		JPanel errorJListJPanel = getErrorJListJPanel();

		JPanel exceptionJTreeJPanel = getExceptionJTreeJPanel();

		JSplitPane errorJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, errorJListJPanel, exceptionJTreeJPanel);

		errorJSplitPane.setDividerLocation(300);

		return errorJSplitPane;
	}

	private JPanel getErrorJListJPanel() {

		JPanel errorJListJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(errorJListJPanel, BoxLayout.PAGE_AXIS);

		errorJListJPanel.setLayout(layout);

		JPanel errorJTreeHeaderJPanel = getErrorJTreeHeaderJPanel();

		JList<ExceptionLeafNode> errorJList = getErrorJList();
		JScrollPane errorJTreeJScrollPane = new JScrollPane(errorJList);

		errorJListJPanel.add(errorJTreeHeaderJPanel);
		errorJListJPanel.add(errorJTreeJScrollPane);

		return errorJListJPanel;
	}

	private JPanel getErrorJTreeHeaderJPanel() {

		JPanel errorJTreeHeaderJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(errorJTreeHeaderJPanel, BoxLayout.LINE_AXIS);

		errorJTreeHeaderJPanel.setLayout(layout);

		String labelText = "Error List";
		JLabel errorListJLabel = LogViewerUtil.getHeaderJLabel(labelText, 200);

		Dimension edge = new Dimension(10, 35);

		errorJTreeHeaderJPanel.add(Box.createRigidArea(edge));
		errorJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		errorJTreeHeaderJPanel.add(errorListJLabel);
		errorJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		errorJTreeHeaderJPanel.add(Box.createRigidArea(edge));

		errorJTreeHeaderJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		return errorJTreeHeaderJPanel;
	}

	private JPanel getExceptionJTreeJPanel() {

		JPanel exceptionJTreeJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(exceptionJTreeJPanel, BoxLayout.PAGE_AXIS);

		exceptionJTreeJPanel.setLayout(layout);

		JPanel exceptionJTreeHeaderJPanel = getExceptionJTreeHeaderJPanel();

		JTree exceptionJTree = getExceptionJTree();
		JScrollPane exceptionJTreeJScrollPane = new JScrollPane(exceptionJTree);

		exceptionJTreeJPanel.add(exceptionJTreeHeaderJPanel);
		exceptionJTreeJPanel.add(exceptionJTreeJScrollPane);

		return exceptionJTreeJPanel;
	}

	private JPanel getExceptionJTreeHeaderJPanel() {

		JPanel exceptionJTreeHeaderJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(exceptionJTreeHeaderJPanel, BoxLayout.LINE_AXIS);

		exceptionJTreeHeaderJPanel.setLayout(layout);

		String labelText = "Error List - Grouped by Exception";
		JLabel threadDumpAtTraceJLabel = LogViewerUtil.getHeaderJLabel(labelText, 200);

		JButton expandAllExceptionJButton = getExpandAllExceptionJButton();

		Dimension edge = new Dimension(10, 35);
		Dimension spacer = new Dimension(1, 35);

		exceptionJTreeHeaderJPanel.add(Box.createRigidArea(edge));
		exceptionJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		exceptionJTreeHeaderJPanel.add(threadDumpAtTraceJLabel);
		exceptionJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		exceptionJTreeHeaderJPanel.add(Box.createRigidArea(spacer));
		exceptionJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		exceptionJTreeHeaderJPanel.add(expandAllExceptionJButton);
		exceptionJTreeHeaderJPanel.add(Box.createHorizontalGlue());
		exceptionJTreeHeaderJPanel.add(Box.createRigidArea(edge));

		exceptionJTreeHeaderJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		return exceptionJTreeHeaderJPanel;
	}

	/**
	 * @return the errorJList
	 */
	protected JList<ExceptionLeafNode> getErrorJList() {

		if (errorJList == null) {

			errorJList = new JList<ExceptionLeafNode>(errorDefaultListModel);
			errorJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			ListSelectionListener lsl = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList<ExceptionLeafNode> errorJList = getErrorJList();
					List<Integer> errorLogEntryIndexList = getErrorLogEntryIndexList();

					int selectedIndex = errorJList.getSelectedIndex();

					if ((!e.getValueIsAdjusting()) && (selectedIndex != -1)) {

						Integer logEntryIndex = errorLogEntryIndexList.get(selectedIndex);

						LogTableModel logTableModel = getLogTableModel();

						Log4jLogEntryModel log4jLogEntryModel;
						log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

						Map<Integer, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

						LogEntry logEntry = logEntryMap.get(logEntryIndex);

						String logEntryText = logEntry.getLogEntryText();

						JPanel rawTextJPanel = new LogEntryPanel(logEntryText);

						JPanel errorJPanel = getErrorAreaJPanel();

						errorJPanel.removeAll();

						errorJPanel.add(rawTextJPanel, BorderLayout.CENTER);

						errorJPanel.revalidate();
					}
				}
			};

			errorJList.addListSelectionListener(lsl);

			DefaultListCellRenderer dlcr = getDefaultListCellRenderer();

			errorJList.setCellRenderer(dlcr);

			errorJList.setFixedCellHeight(20);

			errorJList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (e.getClickCount() == 2) {

						JList<ExceptionLeafNode> errorJList = getErrorJList();
						List<Integer> errorLogEntryIndexList = getErrorLogEntryIndexList();

						int clickedIndex = errorJList.locationToIndex(e.getPoint());

						Integer logEntryIndex = errorLogEntryIndexList.get(clickedIndex);

						NavigationTableController<Integer> navigationTableController;
						navigationTableController = getNavigationTableController();
						navigationTableController.scrollToKey(logEntryIndex);

					} else {
						super.mouseClicked(e);
					}
				}

			});
		}

		return errorJList;
	}

	/**
	 * @return the exceptionJTree
	 */
	protected JTree getExceptionJTree() {

		if (exceptionJTree == null) {

			exceptionJTree = new JTree(exceptionDefaultTreeModel);
			exceptionJTree.setRootVisible(false);
			exceptionJTree.setShowsRootHandles(true);

			DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer() {

				private static final long serialVersionUID = 6967086772869544871L;

				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
						boolean leaf, int row, boolean hasFocus) {

					JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
							hasFocus);

					label.setBorder(new EmptyBorder(1, 5, 1, 1));

					return label;
				}

			};

			dtcr.setIcon(null);
			dtcr.setOpenIcon(null);
			dtcr.setClosedIcon(null);
			dtcr.setLeafIcon(null);

			exceptionJTree.setCellRenderer(dtcr);

			TreeSelectionListener tsl = new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {

					JTree exceptionJTree = getExceptionJTree();

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) exceptionJTree
							.getLastSelectedPathComponent();

					if (node != null) {

						Object userObject = node.getUserObject();

						if (userObject instanceof ExceptionLeafNode) {

							ExceptionLeafNode eln = (ExceptionLeafNode) userObject;

							LogEntry logEntry = eln.getLogEntry();
							Integer logEntryIndex = logEntry.getKey();

							errorJListScrollToIndex(logEntryIndex);

						}

					}
				}
			};

			exceptionJTree.addTreeSelectionListener(tsl);

			TreeSelectionModel tsm = exceptionJTree.getSelectionModel();

			tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

			exceptionJTree.setRowHeight(20);
			exceptionJTree.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					JTree exceptionJTree = getExceptionJTree();

					int selRow = exceptionJTree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = exceptionJTree.getPathForLocation(e.getX(), e.getY());

					if (selRow != -1) {

						DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

						Object userObject = dmtn.getUserObject();

						if (userObject instanceof ExceptionLeafNode) {
							ExceptionLeafNode eln = (ExceptionLeafNode) userObject;

							LogEntry logEntry = eln.getLogEntry();
							Integer logEntryIndex = logEntry.getKey();

							if (e.getClickCount() == 2) {

								NavigationTableController<Integer> navigationTableController;
								navigationTableController = getNavigationTableController();
								navigationTableController.scrollToKey(logEntryIndex);

							} else {
								errorJListScrollToIndex(logEntryIndex);
							}
						}
					}
				}

			});

		}

		return exceptionJTree;
	}

	protected void errorJListScrollToIndex(Integer logEntryIndex) {

		JList<ExceptionLeafNode> errorJList = getErrorJList();

		int index = errorLogEntryIndexList.indexOf(logEntryIndex);

		errorJList.setSelectedIndex(index);
		errorJList.ensureIndexIsVisible(index);
	}

	/**
	 * @return the expandAllExceptionJButton
	 */
	public JButton getExpandAllExceptionJButton() {
		if (expandAllExceptionJButton == null) {

			expandAllExceptionJButton = new JButton(EXPAND_ALL_ACTION);
			expandAllExceptionJButton.setActionCommand(EXPAND_ALL_ACTION);

			Dimension dim = new Dimension(150, 25);
			expandAllExceptionJButton.setPreferredSize(dim);
			expandAllExceptionJButton.setMaximumSize(dim);

			expandAllExceptionJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JButton expandAllExceptionJButton = getExpandAllExceptionJButton();

					if (EXPAND_ALL_ACTION.equals(e.getActionCommand())) {

						JTree exceptionJTree = getExceptionJTree();
						LogViewerUtil.expandAll(exceptionJTree, true);

						expandAllExceptionJButton.setText(COLLAPSE_ALL_ACTION);
						expandAllExceptionJButton.setActionCommand(COLLAPSE_ALL_ACTION);

					} else {

						JTree exceptionJTree = getExceptionJTree();
						LogViewerUtil.expandAll(exceptionJTree, false);

						expandAllExceptionJButton.setText(EXPAND_ALL_ACTION);
						expandAllExceptionJButton.setActionCommand(EXPAND_ALL_ACTION);

					}

				}
			});
		}

		return expandAllExceptionJButton;
	}

	/**
	 * @return the errorAreaJPanel
	 */
	protected JPanel getErrorAreaJPanel() {

		if (errorAreaJPanel == null) {
			errorAreaJPanel = new JPanel();
			errorAreaJPanel.setLayout(new BorderLayout());
		}

		return errorAreaJPanel;
	}

	/**
	 * @return the threadDumpJList
	 */
	protected JList<String> getThreadDumpJList() {

		if (threadDumpJList == null) {

			final LogTableModel logTableModel = getLogTableModel();

			Log4jLogEntryModel log4jLogEntryModel;
			log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

			List<Integer> threadDumpIndexList = log4jLogEntryModel.getThreadDumpLogEntryIndexList();

			Map<Integer, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

			DateFormat displayDateFormat = log4jLogEntryModel.getDisplayDateFormat();

			DefaultListModel<String> dlm = new DefaultListModel<String>();

			int threadDumpCounter = 1;

			for (Integer threadDumpIndex : threadDumpIndexList) {

				LogEntry logEntry = logEntryMap.get(threadDumpIndex);

				Date logEntryDate = logEntry.getLogEntryDate();
				String timeText = displayDateFormat.format(logEntryDate);

				String element = threadDumpCounter + ". Thread Dump - Time [" + timeText + "] Line No ["
						+ threadDumpIndex + "]";
				dlm.addElement(element);

				threadDumpCounter++;
			}

			threadDumpJList = new JList<String>(dlm);
			threadDumpJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			ListSelectionListener lsl = new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList<String> threadDumpJList = getThreadDumpJList();

					int selectedIndex = threadDumpJList.getSelectedIndex();

					if ((!e.getValueIsAdjusting()) && (selectedIndex != -1)) {

						Log4jLogEntryModel log4jLogEntryModel;
						log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

						List<Integer> threadDumpIndexList = log4jLogEntryModel.getThreadDumpLogEntryIndexList();

						Integer logEntryIndex = threadDumpIndexList.get(selectedIndex);

						Map<Integer, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();
						LogEntry logEntry = logEntryMap.get(logEntryIndex);

						JPanel threadDumpPanel = new ThreadDumpPanel((Log4jLogThreadDumpEntry) logEntry, logTableModel,
								threadDumpSelectedTab);
						// JPanel threadDumpPanel =
						// logEntry.getDetailsJPanel(logTableModel);

						JPanel threadDumpJPanel = getThreadDumpJPanel();

						threadDumpJPanel.removeAll();

						threadDumpJPanel.add(threadDumpPanel, BorderLayout.CENTER);

						threadDumpJPanel.revalidate();
					}
				}
			};

			threadDumpJList.addListSelectionListener(lsl);

			threadDumpJList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {

					if (e.getClickCount() == 2) {

						JList<String> threadDumpJList = getThreadDumpJList();

						int clickedIndex = threadDumpJList.locationToIndex(e.getPoint());

						Log4jLogEntryModel log4jLogEntryModel;
						log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

						List<Integer> threadDumpIndexList = log4jLogEntryModel.getThreadDumpLogEntryIndexList();

						Integer logEntryIndex = threadDumpIndexList.get(clickedIndex);

						NavigationTableController<Integer> navigationTableController;
						navigationTableController = getNavigationTableController();
						navigationTableController.scrollToKey(logEntryIndex);

					} else {
						super.mouseClicked(e);
					}
				}

			});

			DefaultListCellRenderer dlcr = getDefaultListCellRenderer();

			threadDumpJList.setCellRenderer(dlcr);

			threadDumpJList.setFixedCellHeight(20);
			threadDumpJList.setFixedCellWidth(400);
		}

		return threadDumpJList;
	}

	/**
	 * @return the threadDumpJPanel
	 */
	protected JPanel getThreadDumpJPanel() {

		if (threadDumpJPanel == null) {
			threadDumpJPanel = new JPanel();
			threadDumpJPanel.setLayout(new BorderLayout());
		}

		return threadDumpJPanel;
	}

	private DefaultListCellRenderer getDefaultListCellRenderer() {

		DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

			private static final long serialVersionUID = 7578704614877257466L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				setBorder(new EmptyBorder(1, 10, 1, 1));

				setToolTipText(value.toString());

				return this;
			}

		};

		return dlcr;
	}

	protected DefaultTableCellRenderer getDefaultTableCellRenderer() {

		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 7231010655893711987L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setBorder(new EmptyBorder(1, 5, 1, 1));

				if (!isSelected) {

					if ((row % 2) == 0) {
						setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
					} else {
						setBackground(Color.WHITE);
					}
				}
				return this;
			}

		};

		return dtcr;
	}

	private class ExceptionLeafNode {

		private int counter;

		private String nodeText;

		private LogEntry logEntry;

		protected ExceptionLeafNode(String nodeText, LogEntry logEntry) {
			super();
			this.nodeText = nodeText;
			this.logEntry = logEntry;
			this.counter = 0;
		}

		/**
		 * @return the logEntry
		 */
		protected LogEntry getLogEntry() {
			return logEntry;
		}

		public void setCounter(int counter) {
			this.counter = counter;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(counter);
			sb.append(".");
			sb.append(nodeText);

			return sb.toString();
		}

	}
}
