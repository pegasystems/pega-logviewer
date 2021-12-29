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
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogEntryPanel;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.ThreadDumpPanel;
import com.pega.gcs.logviewer.model.HazelcastMembership;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.SystemStart;

public class Log4jSystemReportDialog extends SystemReportDialog {

    private static final long serialVersionUID = -9039423327032627896L;

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jSystemReportDialog.class);

    private static final String EXPAND_ALL_ACTION = "Expand all nodes";

    private static final String COLLAPSE_ALL_ACTION = "Collapse all nodes";

    private DefaultListModel<ExceptionLeafNode> errorDefaultListModel;

    private DefaultTreeModel exceptionDefaultTreeModel;

    private JList<ExceptionLeafNode> errorJList;

    private JTree exceptionJTree;

    private JPanel errorAreaJPanel;

    private JButton expandAllExceptionJButton;

    private JList<Log4jLogThreadDumpEntry> threadDumpJList;

    private JPanel threadDumpJPanel;

    private List<LogEntryKey> errorLogEntryIndexList;

    private AtomicInteger threadDumpSelectedTab;

    public Log4jSystemReportDialog(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, ImageIcon appIcon, Component parent) {

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

    protected List<LogEntryKey> getErrorLogEntryIndexList() {
        return errorLogEntryIndexList;
    }

    @Override
    protected void buildTabs() {

        try {

            addDefaultTabs();

            JTabbedPane reportTabbedPane = getReportTabbedPane();

            Dimension labelDim = new Dimension(140, 26);

            NavigationTableController<LogEntryKey> navigationTableController;
            navigationTableController = getNavigationTableController();

            LogTableModel logTableModel = getLogTableModel();

            Log4jLogEntryModel log4jLogEntryModel;
            log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

            List<SystemStart> systemStartList = log4jLogEntryModel.getSystemStartList();
            Map<String, List<LogEntryKey>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();
            List<LogEntryKey> threadDumpIndexList = log4jLogEntryModel.getThreadDumpLogEntryKeyList();
            List<HazelcastMembership> hazelcastMembershipList = log4jLogEntryModel.getHazelcastMembershipList();

            boolean containsSysStart = systemStartList.size() > 0;
            boolean containsError = errorLogEntryIndexMap.size() > 0;
            boolean containsThreadDump = threadDumpIndexList.size() > 0;
            boolean containsHazelcastMembership = hazelcastMembershipList.size() > 0;

            if (containsSysStart) {

                SystemStartPanel systemStartPanel = new SystemStartPanel(logTableModel, navigationTableController);

                String tabLabelText = "System Start";

                GUIUtilities.addTab(reportTabbedPane, systemStartPanel, tabLabelText, labelDim);

            }

            if (containsError) {

                initialiseErrorListModel();

                JComponent errorTabJComponent = getErrorTabJComponent();

                String tabLabelText = "Errors";

                GUIUtilities.addTab(reportTabbedPane, errorTabJComponent, tabLabelText, labelDim);
            }

            if (containsThreadDump) {

                JComponent threadDumpTabJComponent = getThreadDumpTabJComponent();

                String tabLabelText = "Thread Dumps";

                GUIUtilities.addTab(reportTabbedPane, threadDumpTabJComponent, tabLabelText, labelDim);
            }

            if (containsHazelcastMembership) {

                HazelcastMembershipReportPanel hzMembershipReportPanel;
                hzMembershipReportPanel = new HazelcastMembershipReportPanel(logTableModel, navigationTableController);

                String tabLabelText = "Cluster Info";

                GUIUtilities.addTab(reportTabbedPane, hzMembershipReportPanel, tabLabelText, labelDim);

            }

        } catch (Exception e) {
            LOG.error("Error building overview tabs.", e);
        }
    }

    private JComponent getErrorTabJComponent() {

        JComponent errorJComponent = getErrorJComponent();
        JPanel errorAreaJPanel = getErrorAreaJPanel();

        JSplitPane errorJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, errorJComponent, errorAreaJPanel);

        errorJSplitPane.setDividerLocation(400);

        return errorJSplitPane;
    }

    private JComponent getThreadDumpTabJComponent() {

        JList<Log4jLogThreadDumpEntry> threadDumpJList = getThreadDumpJList();
        JPanel threadDumpJPanel = getThreadDumpJPanel();

        JScrollPane threadDumpJListJScrollPane = new JScrollPane(threadDumpJList);

        JSplitPane threadDumpJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, threadDumpJListJScrollPane,
                threadDumpJPanel);

        return threadDumpJSplitPane;
    }

    private void initialiseErrorListModel() {

        LogTableModel logTableModel = getLogTableModel();

        Log4jLogEntryModel log4jLogEntryModel;
        log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

        Map<String, List<LogEntryKey>> errorLogEntryIndexMap = log4jLogEntryModel.getErrorLogEntryIndexMap();

        Map<LogEntryKey, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

        errorDefaultListModel = new DefaultListModel<ExceptionLeafNode>();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("TD");
        exceptionDefaultTreeModel = new DefaultTreeModel(root);

        errorLogEntryIndexList = new ArrayList<LogEntryKey>();

        Map<String, DefaultMutableTreeNode> exceptionNodeMap;
        exceptionNodeMap = new TreeMap<String, DefaultMutableTreeNode>();

        Map<LogEntryKey, ExceptionLeafNode> errorLogElementMap = new TreeMap<>();

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

            List<LogEntryKey> errorLogEntryKeyList;

            errorLogEntryKeyList = errorLogEntryIndexMap.get(errorText);

            for (LogEntryKey logEntryKey : errorLogEntryKeyList) {

                LogEntry logEntry = logEntryMap.get(logEntryKey);

                ExceptionLeafNode eln = new ExceptionLeafNode(logEntry);

                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(eln);

                dmtn.add(newChild);

                errorLogElementMap.put(logEntryKey, eln);

            }

        }

        int errorCounter = 1;

        for (LogEntryKey logEntryKey : errorLogElementMap.keySet()) {

            ExceptionLeafNode element = errorLogElementMap.get(logEntryKey);
            element.setIndex(errorCounter);

            errorLogEntryIndexList.add(logEntryKey);
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
        JLabel errorListJLabel = LogViewerUtil.getHeaderLabel(labelText, 200);

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
        JLabel threadDumpAtTraceJLabel = LogViewerUtil.getHeaderLabel(labelText, 200);

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

    protected JList<ExceptionLeafNode> getErrorJList() {

        if (errorJList == null) {

            LogTableModel logTableModel = getLogTableModel();

            LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

            errorJList = new JList<ExceptionLeafNode>(errorDefaultListModel);
            errorJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ListSelectionListener lsl = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    JList<ExceptionLeafNode> errorJList = getErrorJList();

                    int selectedIndex = errorJList.getSelectedIndex();

                    if ((!listSelectionEvent.getValueIsAdjusting()) && (selectedIndex != -1)) {

                        ExceptionLeafNode exceptionLeafNode = errorJList.getSelectedValue();

                        LogEntry logEntry = exceptionLeafNode.getLogEntry();

                        String logEntryText = logEntry.getLogEntryText();

                        Charset charset = logTableModel.getCharset();

                        JPanel rawTextJPanel = new LogEntryPanel(logEntryText, charset);

                        JPanel errorJPanel = getErrorAreaJPanel();

                        errorJPanel.removeAll();

                        errorJPanel.add(rawTextJPanel, BorderLayout.CENTER);

                        errorJPanel.revalidate();
                    }
                }
            };

            errorJList.addListSelectionListener(lsl);

            errorJList.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (mouseEvent.getClickCount() == 2) {

                        JList<ExceptionLeafNode> errorJList = getErrorJList();

                        int clickedIndex = errorJList.locationToIndex(mouseEvent.getPoint());

                        if (clickedIndex >= 0) {

                            ExceptionLeafNode exceptionLeafNode;
                            exceptionLeafNode = errorJList.getModel().getElementAt(clickedIndex);

                            LogEntry logEntry = exceptionLeafNode.getLogEntry();

                            LogEntryKey logEntryKey = logEntry.getKey();

                            NavigationTableController<LogEntryKey> navigationTableController;
                            navigationTableController = getNavigationTableController();
                            navigationTableController.scrollToKey(logEntryKey);
                        }

                    } else {
                        super.mouseClicked(mouseEvent);
                    }
                }

            });

            DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

                private static final long serialVersionUID = 8776495897305233378L;

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    setBorder(new EmptyBorder(1, 10, 1, 1));

                    ExceptionLeafNode exceptionLeafNode = (ExceptionLeafNode) value;

                    String displayString = exceptionLeafNode.getDisplayString(logEntryModel);

                    setText(displayString);
                    setToolTipText(displayString);

                    return this;
                }

            };

            errorJList.setCellRenderer(dlcr);

            errorJList.setFixedCellHeight(20);
        }

        return errorJList;
    }

    protected JTree getExceptionJTree() {

        if (exceptionJTree == null) {

            LogTableModel logTableModel = getLogTableModel();

            LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

            exceptionJTree = new JTree(exceptionDefaultTreeModel);
            exceptionJTree.setRootVisible(false);
            exceptionJTree.setShowsRootHandles(true);

            DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer() {

                private static final long serialVersionUID = 6967086772869544871L;

                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                        boolean leaf, int row, boolean hasFocus) {

                    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                    setBorder(new EmptyBorder(1, 5, 1, 1));

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                    if (node != null) {

                        Object userObject = node.getUserObject();

                        if (userObject instanceof ExceptionLeafNode) {

                            ExceptionLeafNode eln = (ExceptionLeafNode) userObject;

                            String displayString = eln.getDisplayString(logEntryModel);

                            setText(displayString);
                            setToolTipText(displayString);

                        }
                    }

                    return this;
                }
            };

            dtcr.setIcon(null);
            dtcr.setOpenIcon(null);
            dtcr.setClosedIcon(null);
            dtcr.setLeafIcon(null);

            exceptionJTree.setCellRenderer(dtcr);

            TreeSelectionListener tsl = new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

                    JTree exceptionJTree = getExceptionJTree();

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) exceptionJTree
                            .getLastSelectedPathComponent();

                    if (node != null) {

                        Object userObject = node.getUserObject();

                        if (userObject instanceof ExceptionLeafNode) {

                            ExceptionLeafNode eln = (ExceptionLeafNode) userObject;

                            LogEntry logEntry = eln.getLogEntry();
                            LogEntryKey logEntryKey = logEntry.getKey();

                            errorJListScrollToIndex(logEntryKey);

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
                public void mouseClicked(MouseEvent mouseEvent) {

                    JTree exceptionJTree = getExceptionJTree();

                    int selRow = exceptionJTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
                    TreePath selPath = exceptionJTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());

                    if (selRow != -1) {

                        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                        Object userObject = dmtn.getUserObject();

                        if (userObject instanceof ExceptionLeafNode) {
                            ExceptionLeafNode eln = (ExceptionLeafNode) userObject;

                            LogEntry logEntry = eln.getLogEntry();
                            LogEntryKey logEntryKey = logEntry.getKey();

                            if (mouseEvent.getClickCount() == 2) {

                                NavigationTableController<LogEntryKey> navigationTableController;
                                navigationTableController = getNavigationTableController();
                                navigationTableController.scrollToKey(logEntryKey);

                            } else {
                                errorJListScrollToIndex(logEntryKey);
                            }
                        }
                    }
                }

            });

        }

        return exceptionJTree;
    }

    protected void errorJListScrollToIndex(LogEntryKey logEntryKey) {

        JList<ExceptionLeafNode> errorJList = getErrorJList();

        int index = errorLogEntryIndexList.indexOf(logEntryKey);

        errorJList.setSelectedIndex(index);
        errorJList.ensureIndexIsVisible(index);
    }

    public JButton getExpandAllExceptionJButton() {
        if (expandAllExceptionJButton == null) {

            expandAllExceptionJButton = new JButton(EXPAND_ALL_ACTION);
            expandAllExceptionJButton.setActionCommand(EXPAND_ALL_ACTION);

            Dimension dim = new Dimension(150, 25);
            expandAllExceptionJButton.setPreferredSize(dim);
            expandAllExceptionJButton.setMaximumSize(dim);

            expandAllExceptionJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    JButton expandAllExceptionJButton = getExpandAllExceptionJButton();

                    if (EXPAND_ALL_ACTION.equals(actionEvent.getActionCommand())) {

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

    protected JPanel getErrorAreaJPanel() {

        if (errorAreaJPanel == null) {
            errorAreaJPanel = new JPanel();
            errorAreaJPanel.setLayout(new BorderLayout());
        }

        return errorAreaJPanel;
    }

    protected JList<Log4jLogThreadDumpEntry> getThreadDumpJList() {

        if (threadDumpJList == null) {

            final LogTableModel logTableModel = getLogTableModel();

            Log4jLogEntryModel log4jLogEntryModel;
            log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

            List<LogEntryKey> threadDumpKeyList = log4jLogEntryModel.getThreadDumpLogEntryKeyList();

            Map<LogEntryKey, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

            DefaultListModel<Log4jLogThreadDumpEntry> dlm = new DefaultListModel<Log4jLogThreadDumpEntry>();

            for (LogEntryKey logEntryKey : threadDumpKeyList) {

                Log4jLogThreadDumpEntry log4jLogThreadDumpEntry = (Log4jLogThreadDumpEntry) logEntryMap
                        .get(logEntryKey);

                dlm.addElement(log4jLogThreadDumpEntry);
            }

            threadDumpJList = new JList<Log4jLogThreadDumpEntry>(dlm);
            threadDumpJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ListSelectionListener lsl = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    JList<Log4jLogThreadDumpEntry> threadDumpJList = getThreadDumpJList();

                    int selectedIndex = threadDumpJList.getSelectedIndex();

                    if ((!listSelectionEvent.getValueIsAdjusting()) && (selectedIndex != -1)) {

                        Log4jLogThreadDumpEntry log4jLogThreadDumpEntry = threadDumpJList.getSelectedValue();

                        JPanel threadDumpPanel = new ThreadDumpPanel(log4jLogThreadDumpEntry, logTableModel,
                                threadDumpSelectedTab);

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
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (mouseEvent.getClickCount() == 2) {

                        JList<Log4jLogThreadDumpEntry> threadDumpJList = getThreadDumpJList();

                        int clickedIndex = threadDumpJList.locationToIndex(mouseEvent.getPoint());

                        if (clickedIndex >= 0) {

                            Log4jLogThreadDumpEntry log4jLogThreadDumpEntry;
                            log4jLogThreadDumpEntry = threadDumpJList.getModel().getElementAt(clickedIndex);

                            LogEntryKey logEntryKey = log4jLogThreadDumpEntry.getKey();

                            NavigationTableController<LogEntryKey> navigationTableController;
                            navigationTableController = getNavigationTableController();
                            navigationTableController.scrollToKey(logEntryKey);
                        }

                    } else {
                        super.mouseClicked(mouseEvent);
                    }
                }

            });

            DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

                private static final long serialVersionUID = 8776495897305233378L;

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    setBorder(new EmptyBorder(1, 10, 1, 1));

                    Log4jLogThreadDumpEntry log4jLogThreadDumpEntry = (Log4jLogThreadDumpEntry) value;

                    String displayString = log4jLogThreadDumpEntry.getDisplayString(log4jLogEntryModel);

                    setText(displayString);
                    setToolTipText(displayString);

                    return this;
                }

            };

            threadDumpJList.setCellRenderer(dlcr);

            threadDumpJList.setTransferHandler(new TransferHandler() {

                private static final long serialVersionUID = 5136321164682524214L;

                @Override
                protected Transferable createTransferable(JComponent component) {

                    Log4jLogThreadDumpEntry log4jLogThreadDumpEntry = threadDumpJList.getSelectedValue();
                    String displayString = log4jLogThreadDumpEntry.getDisplayString(log4jLogEntryModel);

                    return new StringSelection(displayString);
                }

                @Override
                public int getSourceActions(JComponent component) {
                    return TransferHandler.COPY;
                }
            });

            threadDumpJList.setFixedCellHeight(20);
            threadDumpJList.setFixedCellWidth(400);
        }

        return threadDumpJList;
    }

    protected JPanel getThreadDumpJPanel() {

        if (threadDumpJPanel == null) {
            threadDumpJPanel = new JPanel();
            threadDumpJPanel.setLayout(new BorderLayout());
        }

        return threadDumpJPanel;
    }

    private class ExceptionLeafNode {

        private int index;

        private LogEntry logEntry;

        protected ExceptionLeafNode(LogEntry logEntry) {
            super();
            this.logEntry = logEntry;
            this.index = 0;
        }

        protected LogEntry getLogEntry() {
            return logEntry;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getDisplayString(LogEntryModel logEntryModel) {

            LogEntryKey logEntryKey = logEntry.getKey();

            String timeText = logEntryModel.getLogEntryTimeDisplayString(logEntryKey);
            String message = logEntryModel.getFormattedLogEntryValue(logEntry, LogEntryColumn.MESSAGE);

            StringBuilder sb = new StringBuilder();
            sb.append(index);
            sb.append(". Time [");
            sb.append(timeText);
            sb.append("] Line No [");
            sb.append(logEntryKey.getLineNo());
            sb.append("] [");
            sb.append(message);
            sb.append("]");
            return sb.toString();
        }
    }
}
