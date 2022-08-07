/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class LogNavigationDialog extends JFrame {

    private static final long serialVersionUID = 1116053624131571491L;

    private LogEntry logEntry;

    private LogEntry currentLogEntry;

    private LogTable logTable;

    private JPanel mainLogEntryJPanel;

    private ImageIcon firstImageIcon;

    private ImageIcon lastImageIcon;

    private ImageIcon prevImageIcon;

    private ImageIcon nextImageIcon;

    private JButton navPrevJButton;

    private JButton navNextJButton;

    private JButton navFirstJButton;

    private JButton navLastJButton;

    private JButton navResetJButton;

    private JLabel navIndexJLabel;

    protected LogNavigationDialog(LogEntry logEntry, LogTable logTable, ImageIcon appIcon, Component parent)
            throws HeadlessException {

        super();

        this.logEntry = logEntry;
        this.logTable = logTable;

        LogTableModel ltm = (LogTableModel) logTable.getModel();
        String modelName = ltm.getModelName();

        initialize();

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1280, 800));

        setTitle(modelName + " - Log Entry Details [" + logEntry.getKey().getLineNo() + "]");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getOverallJPanel());

        pack();

        GUIUtilities.centerFrameOnScreen(this);

        setLocationRelativeTo(parent);

        // setVisible called by caller.
        // setVisible(true);

    }

    private JButton getNavPrevJButton() {

        if (navPrevJButton == null) {

            navPrevJButton = new JButton("Previous", prevImageIcon);

            Dimension size = new Dimension(90, 26);
            navPrevJButton.setPreferredSize(size);
            navPrevJButton.setMaximumSize(size);
            navPrevJButton.setBorder(BorderFactory.createEmptyBorder());
            navPrevJButton.setToolTipText("Previous entry");
            navPrevJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    navigatePrevious();
                }
            });
        }

        return navPrevJButton;
    }

    private JButton getNavNextJButton() {

        if (navNextJButton == null) {

            navNextJButton = new JButton("Next", nextImageIcon);

            Dimension size = new Dimension(90, 26);
            navNextJButton.setPreferredSize(size);
            navNextJButton.setMaximumSize(size);
            navNextJButton.setBorder(BorderFactory.createEmptyBorder());
            navNextJButton.setToolTipText("Next entry");
            navNextJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    navigateNext();
                }
            });
        }

        return navNextJButton;
    }

    private JButton getNavFirstJButton() {

        if (navFirstJButton == null) {

            navFirstJButton = new JButton("First", firstImageIcon);

            Dimension size = new Dimension(90, 26);
            navFirstJButton.setPreferredSize(size);
            navFirstJButton.setMaximumSize(size);
            navFirstJButton.setBorder(BorderFactory.createEmptyBorder());
            navFirstJButton.setToolTipText("First entry");
            navFirstJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    navigateFirst();
                }
            });
        }

        return navFirstJButton;
    }

    private JButton getNavLastJButton() {

        if (navLastJButton == null) {

            navLastJButton = new JButton("Last", lastImageIcon);

            Dimension size = new Dimension(90, 26);
            navLastJButton.setPreferredSize(size);
            navLastJButton.setMaximumSize(size);
            navLastJButton.setBorder(BorderFactory.createEmptyBorder());
            navLastJButton.setToolTipText("Last entry");
            navLastJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    navigateLast();
                }
            });
        }

        return navLastJButton;
    }

    private JLabel getNavIndexJLabel() {
        if (navIndexJLabel == null) {
            navIndexJLabel = new JLabel();

            Dimension size = new Dimension(80, 26);
            navIndexJLabel.setPreferredSize(size);
            navIndexJLabel.setMaximumSize(size);
            navIndexJLabel.setForeground(Color.BLUE);
            navIndexJLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            navIndexJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return navIndexJLabel;
    }

    private JButton getNavResetJButton() {
        if (navResetJButton == null) {

            navResetJButton = new JButton("Reset");

            Dimension size = new Dimension(80, 26);
            navResetJButton.setPreferredSize(size);
            navResetJButton.setMaximumSize(size);
            navResetJButton.setBorder(BorderFactory.createEmptyBorder());
            navResetJButton.setToolTipText("Reset to original log Entry");
            navResetJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    navigateReset();
                }
            });
        }

        return navResetJButton;
    }

    private void initialize() {

        firstImageIcon = FileUtilities.getImageIcon(getClass(), "first.png");

        lastImageIcon = FileUtilities.getImageIcon(getClass(), "last.png");

        prevImageIcon = FileUtilities.getImageIcon(getClass(), "prev.png");

        nextImageIcon = FileUtilities.getImageIcon(getClass(), "next.png");

        currentLogEntry = logEntry;

        populateMainLogEntryJPanel();
    }

    private JPanel getOverallJPanel() {

        JPanel overallJPanel = new JPanel();

        overallJPanel.setLayout(new BorderLayout());

        JPanel toolbarJPanel = getToolbarJPanel();
        JPanel mainLogEntryJPanel = getMainLogEntryJPanel();

        overallJPanel.add(toolbarJPanel, BorderLayout.NORTH);
        overallJPanel.add(mainLogEntryJPanel, BorderLayout.CENTER);

        return overallJPanel;
    }

    private JPanel getToolbarJPanel() {

        JPanel toolbarJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(toolbarJPanel, BoxLayout.LINE_AXIS);
        toolbarJPanel.setLayout(layout);

        JButton navPrevJButton = getNavPrevJButton();
        JButton navNextJButton = getNavNextJButton();
        JLabel navIndexJLabel = getNavIndexJLabel();
        JButton navFirstJButton = getNavFirstJButton();
        JButton navLastJButton = getNavLastJButton();
        JButton navResetJButton = getNavResetJButton();

        Dimension spacer = new Dimension(10, 40);
        Dimension rigidArea = new Dimension(60, 40);

        toolbarJPanel.add(Box.createHorizontalGlue());
        toolbarJPanel.add(Box.createRigidArea(spacer));
        toolbarJPanel.add(navFirstJButton);
        toolbarJPanel.add(Box.createRigidArea(spacer));
        toolbarJPanel.add(navPrevJButton);
        toolbarJPanel.add(Box.createRigidArea(spacer));
        toolbarJPanel.add(navIndexJLabel);
        toolbarJPanel.add(Box.createRigidArea(spacer));
        toolbarJPanel.add(navNextJButton);
        toolbarJPanel.add(Box.createRigidArea(spacer));
        toolbarJPanel.add(navLastJButton);
        toolbarJPanel.add(Box.createRigidArea(rigidArea));
        toolbarJPanel.add(navResetJButton);
        toolbarJPanel.add(Box.createHorizontalGlue());

        toolbarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return toolbarJPanel;
    }

    private JPanel getMainLogEntryJPanel() {

        if (mainLogEntryJPanel == null) {
            mainLogEntryJPanel = new JPanel();
            mainLogEntryJPanel.setLayout(new BorderLayout());

        }

        return mainLogEntryJPanel;
    }

    private void populateMainLogEntryJPanel() {

        JPanel mainLogEntryJPanel = getMainLogEntryJPanel();

        mainLogEntryJPanel.removeAll();

        LogTableModel ltm = (LogTableModel) logTable.getModel();

        JPanel logEntryPanel = currentLogEntry.getDetailsPanel(ltm);

        LogEntryKey logEntryKey = currentLogEntry.getKey();

        mainLogEntryJPanel.add(logEntryPanel, BorderLayout.CENTER);

        logTable.logTableScrollToKey(logEntryKey);

        updateNavButtonStates(logEntryKey);
    }

    private void navigate(boolean forward, boolean first, boolean last) {

        int rowNumber = -1;

        LogTableModel ltm = (LogTableModel) logTable.getModel();

        List<LogEntryKey> ltmKeyList = ltm.getFtmEntryKeyList();
        int ltmLastIndex = ltmKeyList.size() - 1;

        if (ltmKeyList.size() > 0) {

            if (first) {
                rowNumber = 0;

            } else if (last) {
                rowNumber = ltmLastIndex;

            } else {

                LogEntryKey currentLogEntryKey = currentLogEntry.getKey();

                rowNumber = Collections.binarySearch(ltmKeyList, currentLogEntryKey);

                if (rowNumber < 0) {
                    rowNumber = (rowNumber * -1) - 1;
                }

                if (forward) {
                    rowNumber++;
                } else {
                    rowNumber--;
                }

                if (rowNumber < 0) {
                    rowNumber = 0;
                } else if (rowNumber > ltmLastIndex) {
                    rowNumber = ltmLastIndex;
                }

            }

            currentLogEntry = (LogEntry) ltm.getValueAt(rowNumber, 0);

            populateMainLogEntryJPanel();

            logTable.setRowSelectionInterval(rowNumber, rowNumber);
            logTable.scrollRowToVisible(rowNumber);
        }
    }

    protected void navigatePrevious() {
        navigate(false, false, false);
    }

    protected void navigateNext() {
        navigate(true, false, false);
    }

    protected void navigateFirst() {
        navigate(false, true, false);
    }

    protected void navigateLast() {
        navigate(false, false, true);
    }

    protected void navigateReset() {

        currentLogEntry = logEntry;

        populateMainLogEntryJPanel();
    }

    private void updateNavButtonStates(LogEntryKey logEntryKey) {

        LogTableModel logTableModel = (LogTableModel) logTable.getModel();

        JButton navFirstJButton = getNavFirstJButton();
        JButton navPrevJButton = getNavPrevJButton();
        JButton navNextJButton = getNavNextJButton();
        JButton navLastJButton = getNavLastJButton();
        JLabel navIndexJLabel = getNavIndexJLabel();

        SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

        List<LogEntryKey> logEntryKeyList = logTableModel.getFtmEntryKeyList();

        if (logEntryKeyList.size() > 0) {

            int size = logEntryKeyList.size();

            long currentTime = logEntryKey.getTimestamp();
            long firstTime = logEntryKeyList.get(0).getTimestamp();
            long lastTime = logEntryKeyList.get(size - 1).getTimestamp();

            if ((currentTime > firstTime) && (currentTime < lastTime)) {
                selectedRowPosition = SelectedRowPosition.BETWEEN;
            } else if (currentTime <= firstTime) {
                selectedRowPosition = SelectedRowPosition.FIRST;
            } else if (currentTime >= lastTime) {
                selectedRowPosition = SelectedRowPosition.LAST;
            } else {
                selectedRowPosition = SelectedRowPosition.NONE;
            }
        }

        switch (selectedRowPosition) {

        case FIRST:
            navFirstJButton.setEnabled(false);
            navPrevJButton.setEnabled(false);
            navNextJButton.setEnabled(true);
            navLastJButton.setEnabled(true);
            break;

        case LAST:
            navFirstJButton.setEnabled(true);
            navPrevJButton.setEnabled(true);
            navNextJButton.setEnabled(false);
            navLastJButton.setEnabled(false);
            break;

        case BETWEEN:
            navFirstJButton.setEnabled(true);
            navPrevJButton.setEnabled(true);
            navNextJButton.setEnabled(true);
            navLastJButton.setEnabled(true);
            break;

        case NONE:
            navFirstJButton.setEnabled(false);
            navPrevJButton.setEnabled(false);
            navNextJButton.setEnabled(false);
            navLastJButton.setEnabled(false);
            break;
        default:
            break;
        }

        navIndexJLabel.setText(logEntryKey.toString());

    }
}
