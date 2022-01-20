/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pega.gcs.fringecommon.guiutilities.NoteJPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.Log4jLogRequestorLockEntry;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.ThreadDumpThreadInfo;
import com.pega.gcs.logviewer.parser.LogThreadDumpParser;
import com.pega.gcs.logviewer.pegatdp.PegaThreadDumpParser;
import com.pega.gcs.logviewer.pegatdp.PegaThreadDumpParserPanel;

public class ThreadDumpPanel extends JPanel {

    private static final long serialVersionUID = 791827616015609624L;

    private static final Log4j2Helper LOG = new Log4j2Helper(ThreadDumpPanel.class);

    private Log4jLogThreadDumpEntry log4jLogThreadDumpEntry;

    // multiple usages hence extract it once
    private String logEntryText;

    private LogTableModel logTableModel;

    private JTabbedPane threadDumpTabbedPane;

    private JPanel threadDumpThreadInfoJPanel;

    private JPanel requestorLockLogEntryJPanel;

    private JTextField filterJTextField;

    private JCheckBox caseSensitiveFilterJCheckBox;

    private JCheckBox excludeBenignFilterJCheckBox;

    private ThreadDumpTableModel threadDumpTableModel;

    private ThreadDumpTable threadDumpTable;

    public ThreadDumpPanel(Log4jLogThreadDumpEntry log4jLogThreadDumpEntry, LogTableModel logTableModel,
            AtomicInteger threadDumpSelectedTab) {

        super();

        this.log4jLogThreadDumpEntry = log4jLogThreadDumpEntry;
        this.logEntryText = log4jLogThreadDumpEntry.getLogEntryText();
        this.logTableModel = logTableModel;

        List<String> threadLineList = new ArrayList<String>();

        try (StringReader stringReader = new StringReader(logEntryText);
                BufferedReader bufferedReader = new BufferedReader(stringReader)) {

            String line = null;

            line = bufferedReader.readLine();

            while (line != null) {
                threadLineList.add(line);
                line = bufferedReader.readLine();
            }

        } catch (IOException ioe) {
            LOG.error("Error reading logEntryText: " + logEntryText, ioe);
        }

        List<ThreadDumpThreadInfo> threadDumpThreadInfoList = LogThreadDumpParser.parseThreadLineList(threadLineList);

        threadDumpTableModel = new ThreadDumpTableModel(threadDumpThreadInfoList);

        setLayout(new BorderLayout());

        JTabbedPane threadDumpTabbedPane = getThreadDumpTabbedPane();
        add(threadDumpTabbedPane, BorderLayout.CENTER);

        int tabCounter = 0;

        String tabText = "Thread Dump";
        JLabel tabLabel = new JLabel(tabText);
        Font labelFont = tabLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
        Dimension dim = new Dimension(140, 26);
        tabLabel.setFont(tabFont);
        tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JSplitPane threadDumpTabJSplitPane = getThreadDumpTabJSplitPane();

        threadDumpTabbedPane.addTab(tabText, threadDumpTabJSplitPane);
        threadDumpTabbedPane.setTabComponentAt(tabCounter, tabLabel);
        tabCounter++;

        // check if we have associated requestor locked errors
        int size = log4jLogThreadDumpEntry.getLog4jLogRequestorLockEntryList().size();

        if (size > 0) {
            tabText = "Requestor Locks";
            tabLabel = new JLabel(tabText);
            tabLabel.setFont(tabFont);
            tabLabel.setSize(dim);
            tabLabel.setPreferredSize(dim);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JComponent requestorLocksJComponent = getRequestorLocksJComponent();

            threadDumpTabbedPane.addTab(tabText, requestorLocksJComponent);
            threadDumpTabbedPane.setTabComponentAt(tabCounter, tabLabel);
            tabCounter++;
        }

        tabText = "Raw View";
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel rawViewJPanel = getRawViewJPanel();

        threadDumpTabbedPane.addTab(tabText, rawViewJPanel);
        threadDumpTabbedPane.setTabComponentAt(tabCounter, tabLabel);
        tabCounter++;

        // Pega 7 Thread Dump Parser
        PegaThreadDumpParser pegaThreadDumpParser = PegaThreadDumpParser.getInstance();

        if (pegaThreadDumpParser.isInitialised()) {

            tabText = "Thread Dump Report";
            tabLabel = new JLabel(tabText);
            tabLabel.setFont(tabFont);
            tabLabel.setSize(dim);
            tabLabel.setPreferredSize(dim);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

            ThreadDumpTable threadDumpTable = getThreadDumpTable();

            boolean v7ThreadDump = threadDumpTableModel.isV7ThreadDump();

            List<Integer> threadColumnList = new ArrayList<>();
            threadColumnList.add(0);

            ThreadDumpRequestorLockTableMouseListener threadDumpRequestorLockTableMouseListener;
            threadDumpRequestorLockTableMouseListener = new ThreadDumpRequestorLockTableMouseListener(threadDumpTable,
                    threadDumpTabbedPane, threadColumnList);

            JPanel pegaThreadDumpParserPanel = new PegaThreadDumpParserPanel(logEntryText, log4jLogThreadDumpEntry,
                    v7ThreadDump, logTableModel, threadDumpRequestorLockTableMouseListener);

            threadDumpTabbedPane.addTab(tabText, pegaThreadDumpParserPanel);
            threadDumpTabbedPane.setTabComponentAt(tabCounter, tabLabel);
            tabCounter++;
        }

        int defaultSelectedTab = threadDumpSelectedTab.get();
        int tabCount = threadDumpTabbedPane.getTabCount();

        if (defaultSelectedTab >= tabCount) {
            defaultSelectedTab = tabCount - 1;
        }

        threadDumpTabbedPane.setSelectedIndex(defaultSelectedTab);

        ChangeListener tabChangeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                threadDumpSelectedTab.set(index);
            }
        };

        threadDumpTabbedPane.addChangeListener(tabChangeListener);
    }

    private JTabbedPane getThreadDumpTabbedPane() {

        if (threadDumpTabbedPane == null) {
            threadDumpTabbedPane = new JTabbedPane();
        }

        return threadDumpTabbedPane;
    }

    private ThreadDumpTable getThreadDumpTable() {

        if (threadDumpTable == null) {
            threadDumpTable = new ThreadDumpTable(threadDumpTableModel);

            ListSelectionModel lsm = threadDumpTable.getSelectionModel();

            lsm.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    if (!listSelectionEvent.getValueIsAdjusting()) {

                        int selectedRow = threadDumpTable.getSelectedRow();

                        if (selectedRow >= 0) {

                            JPanel threadDumpThreadInfoJPanel = getThreadDumpThreadInfoJPanel();

                            threadDumpThreadInfoJPanel.removeAll();

                            ThreadDumpThreadInfo threadDumpThreadInfo = threadDumpTableModel
                                    .getThreadDumpThreadInfo(selectedRow);

                            String logEntryText = threadDumpThreadInfo.getThreadDumpString();
                            Charset charset = logTableModel.getCharset();

                            JPanel threadInfoJPanel = new LogEntryPanel(logEntryText, charset);

                            threadDumpThreadInfoJPanel.add(threadInfoJPanel, BorderLayout.CENTER);

                            threadDumpThreadInfoJPanel.revalidate();
                        }
                    }

                }
            });

            lsm.setSelectionInterval(0, 0);
        }

        return threadDumpTable;
    }

    private JSplitPane getThreadDumpTabJSplitPane() {

        JPanel threadDumpJPanel = getThreadDumpJPanel();

        JPanel threadDumpThreadInfoJPanel = getThreadDumpThreadInfoJPanel();

        JSplitPane threadDumpTabJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, threadDumpJPanel,
                threadDumpThreadInfoJPanel);

        threadDumpTabJSplitPane.setResizeWeight(0.6d);
        threadDumpTabJSplitPane.setDividerLocation(0.6d);

        return threadDumpTabJSplitPane;
    }

    private JPanel getThreadDumpJPanel() {

        JPanel threadDumpJPanel = new JPanel();

        threadDumpJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 3, 2, 3);

        JPanel controlsJPanel = getControlsJPanel();
        ThreadDumpTable threadDumpTable = getThreadDumpTable();
        JScrollPane threadDumpTableJScrollPane = new JScrollPane(threadDumpTable);

        threadDumpJPanel.add(controlsJPanel, gbc1);
        threadDumpJPanel.add(threadDumpTableJScrollPane, gbc2);

        return threadDumpJPanel;
    }

    private JPanel getControlsJPanel() {

        JPanel controlsJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(controlsJPanel, BoxLayout.X_AXIS);
        controlsJPanel.setLayout(layout);

        JLabel filterJLabel = new JLabel("Filter Text");
        JTextField filterJTextField = getFilterJTextField();
        JCheckBox caseSensitiveFilterJCheckBox = getCaseSensitiveFilterJCheckBox();
        JCheckBox excludeBenignFilterJCheckBox = getExcludeBenignFilterJCheckBox();

        Dimension dim = new Dimension(10, 40);

        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(filterJLabel);
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(filterJTextField);
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(caseSensitiveFilterJCheckBox);
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(excludeBenignFilterJCheckBox);
        controlsJPanel.add(Box.createRigidArea(dim));
        controlsJPanel.add(Box.createHorizontalGlue());

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        controlsJPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Filter"));

        return controlsJPanel;
    }

    private JPanel getRawViewJPanel() {

        JPanel rawViewJPanel = new LogEntryPanel(logEntryText, logTableModel.getCharset());

        return rawViewJPanel;
    }

    private JTextField getFilterJTextField() {

        if (filterJTextField == null) {

            filterJTextField = new JTextField();

            filterJTextField.setEditable(true);

            Dimension dim = new Dimension(250, 22);
            filterJTextField.setPreferredSize(dim);
            filterJTextField.setMaximumSize(dim);

            filterJTextField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    // do nothing
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {

                    if (keyEvent.getSource() instanceof JTextField) {
                        refreshThreadDumpTable();
                    }
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    // do nothing
                }
            });
        }
        return filterJTextField;
    }

    private JCheckBox getCaseSensitiveFilterJCheckBox() {

        if (caseSensitiveFilterJCheckBox == null) {

            caseSensitiveFilterJCheckBox = new JCheckBox("Case Sensitive");

            caseSensitiveFilterJCheckBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    refreshThreadDumpTable();
                }
            });
        }

        return caseSensitiveFilterJCheckBox;
    }

    private JCheckBox getExcludeBenignFilterJCheckBox() {

        if (excludeBenignFilterJCheckBox == null) {

            excludeBenignFilterJCheckBox = new JCheckBox("Exclude Benign Threads (Stack Depth <= 5)");

            excludeBenignFilterJCheckBox.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent itemEvent) {
                    refreshThreadDumpTable();
                }
            });
        }

        return excludeBenignFilterJCheckBox;
    }

    protected void refreshThreadDumpTable() {

        JTextField filterJTextField = getFilterJTextField();
        String filterText = filterJTextField.getText().trim();

        JCheckBox caseSensitiveFilterJCheckBox = getCaseSensitiveFilterJCheckBox();
        JCheckBox excludeBenignFilterJCheckBox = getExcludeBenignFilterJCheckBox();

        boolean caseSensitiveFilter = caseSensitiveFilterJCheckBox.isSelected();
        boolean excludeBenignFilter = excludeBenignFilterJCheckBox.isSelected();

        if (!caseSensitiveFilter) {
            filterText = filterText.toUpperCase();
        }

        threadDumpTableModel.applyFilter(filterText, caseSensitiveFilter, excludeBenignFilter);

    }

    private JComponent getRequestorLocksJComponent() {

        JPanel requestorLockTableJPanel = getRequestorLockTableJPanel();
        JPanel requestorLockLogEntryJPanel = getRequestorLockLogEntryJPanel();

        JSplitPane threadDumpJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, requestorLockTableJPanel,
                requestorLockLogEntryJPanel);

        threadDumpJSplitPane.setDividerLocation(200);

        return threadDumpJSplitPane;
    }

    private JPanel getRequestorLockTableJPanel() {

        JPanel requestorLockTableJPanel = new JPanel();
        LayoutManager layout = new BoxLayout(requestorLockTableJPanel, BoxLayout.PAGE_AXIS);

        requestorLockTableJPanel.setLayout(layout);

        JTable requestorLockLogEntryJtable = getRequestorLockLogEntryJtable();

        JScrollPane requestorLockLogEntrytableJScrollPane = new JScrollPane(requestorLockLogEntryJtable);

        String noteText = "Right click on the cell to select the thread in 'Thread Dump' tab.";
        NoteJPanel noteJPanel = new NoteJPanel(noteText, 1);

        requestorLockTableJPanel.add(requestorLockLogEntrytableJScrollPane);
        requestorLockTableJPanel.add(noteJPanel);

        return requestorLockTableJPanel;
    }

    protected JTable getRequestorLockLogEntryJtable() {

        final List<Log4jLogRequestorLockEntry> requestorLockLogEntryIndexList;

        requestorLockLogEntryIndexList = log4jLogThreadDumpEntry.getLog4jLogRequestorLockEntryList();

        ThreadDumpRequestorLockTableModel threadDumpRequestorLockTableModel;

        threadDumpRequestorLockTableModel = new ThreadDumpRequestorLockTableModel(requestorLockLogEntryIndexList);

        ThreadDumpRequestorLockTable requestorLockLogEntryJtable = new ThreadDumpRequestorLockTable(
                threadDumpRequestorLockTableModel);

        ListSelectionModel lsm = requestorLockLogEntryJtable.getSelectionModel();

        lsm.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if (!listSelectionEvent.getValueIsAdjusting()) {

                    int selectedRow = requestorLockLogEntryJtable.getSelectedRow();

                    if (selectedRow >= 0) {

                        Log4jLogRequestorLockEntry log4jLogRequestorLockEntry;

                        log4jLogRequestorLockEntry = requestorLockLogEntryIndexList.get(selectedRow);

                        JPanel requestorLockLogEntryDetailJPanel = log4jLogRequestorLockEntry
                                .getDetailsJPanel(logTableModel);

                        JPanel requestorLockLogEntryJPanel = getRequestorLockLogEntryJPanel();

                        requestorLockLogEntryJPanel.removeAll();

                        requestorLockLogEntryJPanel.add(requestorLockLogEntryDetailJPanel, BorderLayout.CENTER);

                        requestorLockLogEntryJPanel.revalidate();
                    }
                }

            }
        });

        lsm.setSelectionInterval(0, 0);

        ThreadDumpTable threadDumpTable = getThreadDumpTable();
        JTabbedPane threadDumpTabbedPane = getThreadDumpTabbedPane();
        List<Integer> threadColumnList = new ArrayList<>();
        threadColumnList.add(2);
        threadColumnList.add(3);
        threadColumnList.add(4);

        ThreadDumpRequestorLockTableMouseListener threadDumpRequestorLockTableMouseListener;

        threadDumpRequestorLockTableMouseListener = new ThreadDumpRequestorLockTableMouseListener(threadDumpTable,
                threadDumpTabbedPane, threadColumnList);

        requestorLockLogEntryJtable.addMouseListener(threadDumpRequestorLockTableMouseListener);

        return requestorLockLogEntryJtable;
    }

    private JPanel getThreadDumpThreadInfoJPanel() {

        if (threadDumpThreadInfoJPanel == null) {
            threadDumpThreadInfoJPanel = new JPanel();
            threadDumpThreadInfoJPanel.setLayout(new BorderLayout());
        }

        return threadDumpThreadInfoJPanel;
    }

    private JPanel getRequestorLockLogEntryJPanel() {

        if (requestorLockLogEntryJPanel == null) {

            requestorLockLogEntryJPanel = new JPanel();
            requestorLockLogEntryJPanel.setLayout(new BorderLayout());
        }

        return requestorLockLogEntryJPanel;
    }

}
