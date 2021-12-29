/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.pegatdp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.fringecommon.guiutilities.ClickableFilePathPanel;
import com.pega.gcs.fringecommon.guiutilities.ClickablePathPanel;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.ThreadDumpRequestorLockTableMouseListener;
import com.pega.gcs.logviewer.model.Log4jLogThreadDumpEntry;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class PegaThreadDumpParserPanel extends JPanel {

    private static final long serialVersionUID = 4566725855309906575L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PegaThreadDumpParserPanel.class);

    private Log4jLogThreadDumpEntry log4jLogThreadDumpEntry;

    private boolean v7ThreadDump;

    private LogTableModel logTableModel;

    private ThreadDumpRequestorLockTableMouseListener threadDumpRequestorLockTableMouseListener;

    private JFileChooser fileChooser;

    private File logFile;

    private JButton generateReportHtmlButton;

    private ClickableFilePathPanel reportClickableFilePathPanel;

    private PegaThreadDumpParser pegaThreadDumpParser;

    private Object ptdpThreadDump;

    // passing the text separately as it is already uncompressed in the caller.
    public PegaThreadDumpParserPanel(String logEntryText, Log4jLogThreadDumpEntry log4jLogThreadDumpEntry,
            boolean v7ThreadDump, LogTableModel logTableModel,
            ThreadDumpRequestorLockTableMouseListener threadDumpRequestorLockTableMouseListener) {

        this.log4jLogThreadDumpEntry = log4jLogThreadDumpEntry;
        this.v7ThreadDump = v7ThreadDump;
        this.logTableModel = logTableModel;
        this.threadDumpRequestorLockTableMouseListener = threadDumpRequestorLockTableMouseListener;

        RecentFile recentFile = logTableModel.getRecentFile();
        String fileStr = recentFile.getPath();

        logFile = new File(fileStr);

        String text = "Full Java thread dump with locks info";
        int java7ThreadDumpIndex = logEntryText.indexOf(text);

        pegaThreadDumpParser = PegaThreadDumpParser.getInstance();

        if ((v7ThreadDump) && (java7ThreadDumpIndex != -1)) {

            ptdpThreadDump = log4jLogThreadDumpEntry.getPtdpThreadDump();

            if (ptdpThreadDump == null) {

                try {
                    logEntryText = logEntryText.substring(java7ThreadDumpIndex);

                    boolean isCW = logTableModel.getLogFileType().getLogPattern().isCW();

                    if (isCW) {
                        logEntryText = sanitiseThreadDumpText(logEntryText);
                    }

                    ptdpThreadDump = pegaThreadDumpParser.getThreadDumpObject(logEntryText);
                    log4jLogThreadDumpEntry.setPtdpThreadDump(ptdpThreadDump);

                } catch (Exception e) {
                    LOG.error("Error sanitising thread dump text", e);
                }
            }
        }

        setGeneratedFileIfAvailable();

        setLayout(new BorderLayout());

        JPanel threadDumpReportJPanel = null;

        boolean isTDPAvailable = pegaThreadDumpParser.isInitialised();

        if (isTDPAvailable) {

            threadDumpReportJPanel = getPegaThreadDumpReportJPanel();
        } else {
            threadDumpReportJPanel = getDefaultThreadDumpReportJPanel();
        }

        add(threadDumpReportJPanel, BorderLayout.CENTER);

    }

    private JFileChooser getFileChooser() {

        if (fileChooser == null) {

            String fileName = getDefaultReportFileName();

            File currentDirectory = logFile.getParentFile();

            File proposedFile = new File(currentDirectory, fileName);

            fileChooser = new JFileChooser(currentDirectory);

            fileChooser.setDialogTitle("Save PegaThreadDumpParser report file (.html)");
            fileChooser.setSelectedFile(proposedFile);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML File (HTML)", "html");

            fileChooser.setFileFilter(filter);
        }

        return fileChooser;
    }

    private JButton getGenerateReportHtmlButton() {

        if (generateReportHtmlButton == null) {

            generateReportHtmlButton = new JButton("Generate Thread Dump Report");

            Dimension size = new Dimension(200, 20);
            generateReportHtmlButton.setPreferredSize(size);
            generateReportHtmlButton.setMaximumSize(size);
            generateReportHtmlButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    showSaveReportHtmlChooser();
                }
            });

            generateReportHtmlButton.setEnabled(v7ThreadDump);

        }

        return generateReportHtmlButton;
    }

    private ClickableFilePathPanel getReportClickableFilePathPanel() {

        if (reportClickableFilePathPanel == null) {
            reportClickableFilePathPanel = new ClickableFilePathPanel(true);
        }

        return reportClickableFilePathPanel;
    }

    private JPanel getCopyrightJPanel() {

        JPanel copyrightJPanel = new JPanel();

        copyrightJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 2, 5, 2);

        JLabel copyrightLabel = new JLabel("by 'Pega 7 Thread Dump Parser'");
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        copyrightJPanel.add(copyrightLabel, gbc1);

        copyrightJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return copyrightJPanel;
    }

    private JPanel getPegaThreadDumpReportJPanel() {

        JPanel pegaThreadDumpReportJPanel = new JPanel();

        pegaThreadDumpReportJPanel.setLayout(new GridBagLayout());

        int yindex = 0;

        if (!v7ThreadDump) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = yindex++;
            gbc1.weightx = 1.0D;
            gbc1.weighty = 0.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            JPanel pega6ThreadDumpMessagePanel = getPega6ThreadDumpMessagePanel();
            pegaThreadDumpReportJPanel.add(pega6ThreadDumpMessagePanel, gbc1);
        }

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = yindex++;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel pegaThreadDumpControlsJPanel = getPegaThreadDumpControlsJPanel();
        pegaThreadDumpReportJPanel.add(pegaThreadDumpControlsJPanel, gbc2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = yindex++;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(0, 0, 0, 0);
        JPanel pegaThreadDumpGraphJPanel = getPegaThreadDumpGraphJPanel();

        pegaThreadDumpReportJPanel.add(pegaThreadDumpGraphJPanel, gbc3);

        return pegaThreadDumpReportJPanel;
    }

    private JPanel getPega6ThreadDumpMessagePanel() {
        JPanel pega6ThreadDumpMessagePanel = new JPanel();

        pega6ThreadDumpMessagePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 0, 5, 0);

        JLabel pega6MessageJLabel = new JLabel(
                "Currently 'Pega 7 Thread Dump Parser' works only with Pega 7 Thread dumps");

        pega6MessageJLabel.setForeground(Color.RED);
        pega6MessageJLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Dimension preferredSize = new Dimension(Integer.MAX_VALUE, 30);

        pega6MessageJLabel.setPreferredSize(preferredSize);

        pega6ThreadDumpMessagePanel.add(pega6MessageJLabel, gbc1);

        pega6ThreadDumpMessagePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return pega6ThreadDumpMessagePanel;
    }

    private JPanel getPegaThreadDumpControlsJPanel() {

        JPanel pegaThreadDumpControlsJPanel = new JPanel();

        pegaThreadDumpControlsJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 0.3D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel pegaThreadDumpButtonsJPanel = getPegaThreadDumpButtonsJPanel();

        JPanel copyrightJPane = getCopyrightJPanel();

        pegaThreadDumpControlsJPanel.add(pegaThreadDumpButtonsJPanel, gbc1);
        pegaThreadDumpControlsJPanel.add(copyrightJPane, gbc2);

        pegaThreadDumpControlsJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return pegaThreadDumpControlsJPanel;
    }

    private JPanel getPegaThreadDumpButtonsJPanel() {

        JPanel pegaThreadDumpButtonsJPanel = new JPanel();

        pegaThreadDumpButtonsJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 10, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 2, 10, 2);

        JButton generateReportHtmlButton = getGenerateReportHtmlButton();

        ClickableFilePathPanel reportClickableFilePathPanel = getReportClickableFilePathPanel();

        pegaThreadDumpButtonsJPanel.add(generateReportHtmlButton, gbc1);
        pegaThreadDumpButtonsJPanel.add(reportClickableFilePathPanel, gbc2);

        pegaThreadDumpButtonsJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return pegaThreadDumpButtonsJPanel;
    }

    private JPanel getPegaThreadDumpGraphJPanel() {
        JPanel pegaThreadDumpGraphJPanel = new JPanel();
        pegaThreadDumpGraphJPanel.setLayout(new BorderLayout());

        if (ptdpThreadDump != null) {
            JTabbedPane tabbedPane = new JTabbedPane();

            List<FindingAdapter> findingAdapterList = pegaThreadDumpParser.getFindingAdapterList(ptdpThreadDump);

            int tabCounter = 0;

            for (FindingAdapter findingAdapter : findingAdapterList) {

                String tabText = findingAdapter.getName();

                if (findingAdapter instanceof GraphFindingAdapter) {

                    GraphFindingAdapter graphFindingAdapter = (GraphFindingAdapter) findingAdapter;
                    JPanel graphFindingAdapterJPanel = getGraphFindingAdapterJPanel(graphFindingAdapter);

                    JLabel tabLabel = new JLabel(tabText);
                    Font labelFont = tabLabel.getFont();
                    Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
                    Dimension dim = new Dimension(250, 22);
                    tabLabel.setFont(tabFont);
                    tabLabel.setPreferredSize(dim);
                    tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    tabbedPane.addTab(tabText, graphFindingAdapterJPanel);
                    tabbedPane.setTabComponentAt(tabCounter, tabLabel);

                    tabCounter++;
                } else {
                    JPanel findingAdapterJPanel = getFindingAdapterJPanel(findingAdapter);

                    JLabel tabLabel = new JLabel(tabText);
                    Font labelFont = tabLabel.getFont();
                    Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
                    Dimension dim = new Dimension(250, 22);
                    tabLabel.setFont(tabFont);
                    tabLabel.setPreferredSize(dim);
                    tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    tabbedPane.addTab(tabText, findingAdapterJPanel);
                    tabbedPane.setTabComponentAt(tabCounter, tabLabel);

                    tabCounter++;
                }
            }

            pegaThreadDumpGraphJPanel.add(tabbedPane, BorderLayout.CENTER);
        }

        return pegaThreadDumpGraphJPanel;
    }

    private JPanel getDefaultThreadDumpReportJPanel() {
        JPanel defaultThreadDumpReportJPanel = new JPanel();

        defaultThreadDumpReportJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 2, 2);
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 0.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 5, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 1;
        gbc3.gridy = 1;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 2;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(2, 5, 2, 2);
        gbc4.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 3;
        gbc5.weightx = 1.0D;
        gbc5.weighty = 1.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(2, 5, 2, 2);
        gbc5.gridwidth = GridBagConstraints.REMAINDER;

        JLabel label1 = new JLabel(
                "This tab is loaded from 'Pega 7 Thread Dump Parser' tool built by Domenico Giffone");
        JLabel label2 = new JLabel("Please download the tool from Pega Mesh.");
        ClickablePathPanel meshClickablePathPanel = new ClickablePathPanel();

        String meshLink = "https://mesh.pega.com/docs/DOC-110737";

        meshClickablePathPanel.setUrl(meshLink);

        JLabel label3 = new JLabel("Put pegatdp.jar to 'plugins' directory and restart Pega-Logviewer.");

        defaultThreadDumpReportJPanel.add(label1, gbc1);
        defaultThreadDumpReportJPanel.add(label2, gbc2);
        defaultThreadDumpReportJPanel.add(meshClickablePathPanel, gbc3);
        defaultThreadDumpReportJPanel.add(label3, gbc4);
        defaultThreadDumpReportJPanel.add(new JPanel(), gbc5);

        return defaultThreadDumpReportJPanel;
    }

    private void showSaveReportHtmlChooser() {

        File exportFile = null;

        JFileChooser fileChooser = getFileChooser();

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            exportFile = fileChooser.getSelectedFile();

            returnValue = JOptionPane.YES_OPTION;

            if (exportFile.exists()) {

                returnValue = JOptionPane.showConfirmDialog(null, "Replace Existing File?", "File Exists",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }

            if (returnValue == JOptionPane.YES_OPTION) {

                String filename = logTableModel.getModelName();
                LogEntryModel lem = logTableModel.getLogEntryModel();

                LogEntryKey logEntryKey = log4jLogThreadDumpEntry.getKey();

                String line = Integer.toString(logEntryKey.getLineNo());
                String time = lem.getLogEntryTimeDisplayString(logEntryKey);

                String reportHtml = pegaThreadDumpParser.getHtmlReport(ptdpThreadDump, filename, line, time);

                try {
                    FileUtils.writeStringToFile(exportFile, reportHtml, logTableModel.getCharset());
                    log4jLogThreadDumpEntry.setGeneratedReportFile(exportFile.getAbsolutePath());
                    populateGeneratedReportPath(exportFile);

                } catch (IOException e) {
                    LOG.error("Error generating report HTML: " + filename, e);
                }

            }
        }
    }

    private String getDefaultReportFileName() {

        String fileName = FileUtilities.getNameWithoutExtension(logFile);

        // String outputFile =
        // filename.concat("-").concat(line).concat(".html");
        StringBuilder sb = new StringBuilder();
        sb.append(fileName);
        sb.append("-");
        sb.append(log4jLogThreadDumpEntry.getKey());
        sb.append(".html");

        String defaultReportFileName = sb.toString();

        return defaultReportFileName;
    }

    private void setGeneratedFileIfAvailable() {

        boolean generatedFileAvailable = false;

        File reportFile = null;
        String reportFilePath = log4jLogThreadDumpEntry.getGeneratedReportFile();

        if ((reportFilePath != null) && (!"".equals(reportFilePath))) {

            reportFile = new File(reportFilePath);

            if (reportFile.exists() && reportFile.isFile()) {
                generatedFileAvailable = true;
            }

        } else {
            // check if the default output file is available
            String fileName = getDefaultReportFileName();
            File currentDirectory = logFile.getParentFile();

            reportFile = new File(currentDirectory, fileName);

            if (reportFile.exists() && reportFile.isFile()) {
                generatedFileAvailable = true;

                log4jLogThreadDumpEntry.setGeneratedReportFile(reportFile.getAbsolutePath());
            }
        }

        if (generatedFileAvailable) {
            populateGeneratedReportPath(reportFile);
        }
    }

    private void populateGeneratedReportPath(File reportFile) {

        ClickableFilePathPanel reportClickableFilePathPanel = getReportClickableFilePathPanel();

        reportClickableFilePathPanel.setFile(reportFile);
    }

    private JPanel getGenericJPanel(JComponent component, Insets insets, Dimension preferredSize) {

        JPanel genericJPanel = new JPanel();

        genericJPanel.setLayout(new GridBagLayout());

        if (component != null) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = 0;
            gbc1.weightx = 1.0D;
            gbc1.weighty = 1.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = insets;

            if (preferredSize != null) {
                component.setPreferredSize(preferredSize);
            }

            genericJPanel.add(component, gbc1);
        }

        genericJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return genericJPanel;
    }

    private JPanel getNameJPanel(String name, int horizontalAlignment, Insets insets, Dimension preferredSize) {

        JLabel nameJLabel = new JLabel(name);

        Font labelFont = nameJLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 11);

        nameJLabel.setFont(tabFont);

        nameJLabel.setHorizontalAlignment(horizontalAlignment);

        JPanel nameJPanel = getGenericJPanel(nameJLabel, insets, preferredSize);
        return nameJPanel;
    }

    private JPanel getLabelValueJPanel(String value, Insets insets, Dimension preferredSize) {

        JLabel valueJLabel = new JLabel(value);

        JPanel valueJPanel = getGenericJPanel(valueJLabel, insets, preferredSize);
        return valueJPanel;
    }

    private JPanel getTextAreaValueJPanel(String value, Insets insets) {

        int rows = 1;

        if (value != null) {

            int fromIndex = 0;

            while ((fromIndex = value.indexOf("\\n", fromIndex)) != -1) {
                fromIndex++;
                rows++;
            }

            value = value.replaceAll("\\\\n", System.getProperty("line.separator"));
        }

        JTextArea valueJTextArea = new JTextArea(value);
        valueJTextArea.setEditable(false);
        valueJTextArea.setBackground(null);
        valueJTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        valueJTextArea.setRows(rows);

        valueJTextArea.setAlignmentX(CENTER_ALIGNMENT);
        valueJTextArea.setAlignmentY(CENTER_ALIGNMENT);

        valueJTextArea.setFont(this.getFont());

        valueJTextArea.setPreferredSize(new Dimension(150, 150));

        JScrollPane scrollPane = new JScrollPane(valueJTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel dssValueJPanel = getGenericJPanel(scrollPane, insets, null);

        return dssValueJPanel;
    }

    private JPanel getApplyToJPanel(Map<String, String> applyToMap, Insets insets) {

        JComponent component = null;

        if (applyToMap != null) {

            DefaultTableModel dtm = new DefaultTableModel(new String[] { "Thread" }, 0) {

                private static final long serialVersionUID = 4373854997870235236L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

            };

            for (String value : applyToMap.values()) {

                dtm.addRow(new String[] { value });
            }

            JTable threadtable = new JTable(dtm);
            threadtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            threadtable.setRowHeight(22);

            JTableHeader tableHeader = threadtable.getTableHeader();

            DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
            dtcr.setHorizontalAlignment(SwingConstants.CENTER);

            Font existingFont = tableHeader.getFont();
            String existingFontName = existingFont.getName();
            int existFontSize = existingFont.getSize();
            Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
            tableHeader.setFont(newFont);

            tableHeader.setReorderingAllowed(false);

            DefaultTableCellRenderer cdtcr = new DefaultTableCellRenderer() {

                private static final long serialVersionUID = 3022165406106800014L;

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

            TableColumnModel tcm = threadtable.getColumnModel();
            TableColumn tc = tcm.getColumn(0);
            tc.setCellRenderer(cdtcr);
            tc.setPreferredWidth(300);

            threadtable.addMouseListener(threadDumpRequestorLockTableMouseListener);

            JScrollPane scrollPane = new JScrollPane(threadtable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            component = scrollPane;
        }

        JPanel dssValueJPanel = getGenericJPanel(component, insets, null);

        return dssValueJPanel;
    }

    private JPanel getFindingAdapterJPanel(FindingAdapter findingAdapter) {

        JPanel findingAdapterJPanel = new JPanel();

        findingAdapterJPanel.setLayout(new GridBagLayout());

        LinkedHashMap<WeightPanel, JPanel> findingAdapterJPanelMap = getFindingAdapterJPanelMap(findingAdapter);

        int yindex = 0;

        for (Map.Entry<WeightPanel, JPanel> entry : findingAdapterJPanelMap.entrySet()) {

            WeightPanel weightJPanel = entry.getKey();
            JPanel weightNamePanel = weightJPanel.getPanel();
            double weighty = weightJPanel.getWeighty();

            JPanel weightValuePanel = entry.getValue();

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = yindex;
            gbc1.weightx = 0.0D;
            gbc1.weighty = weighty;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 1;
            gbc2.gridy = yindex;
            gbc2.weightx = 1.0D;
            gbc2.weighty = weighty;
            gbc2.fill = GridBagConstraints.BOTH;
            gbc2.anchor = GridBagConstraints.NORTHWEST;
            gbc2.insets = new Insets(0, 0, 0, 0);

            yindex++;

            findingAdapterJPanel.add(weightNamePanel, gbc1);
            findingAdapterJPanel.add(weightValuePanel, gbc2);
        }

        return findingAdapterJPanel;
    }

    private LinkedHashMap<WeightPanel, JPanel> getFindingAdapterJPanelMap(FindingAdapter findingAdapter) {

        LinkedHashMap<WeightPanel, JPanel> findingAdapterJPanelMap = new LinkedHashMap<>();

        Insets insets = new Insets(5, 5, 5, 5);
        Dimension preferredSize = new Dimension(150, 20);

        String name = null;
        String value = null;
        JPanel nameJPanel = null;
        JPanel valueJPanel = null;
        WeightPanel weightJPanel = null;

        // //ID
        // String name = "Id";
        // String value = null;
        // try {
        // value = String.valueOf(findingAdapter.getId());
        // } catch (Exception e) {
        // }
        //
        // JPanel nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets,
        // preferredSize);
        // JPanel valueJPanel = getLabelValueJPanel(value, insets,
        // preferredSize);
        // WeightPanel weightJPanel = new WeightPanel(nameJPanel, 0);
        // findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Name
        name = "Name";
        value = findingAdapter.getName();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightJPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Severity
        name = "Severity";
        Enum<?> severity = findingAdapter.getSeverity();
        value = severity != null ? String.valueOf(severity) : null;

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightJPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Category
        name = "Category";
        value = findingAdapter.getCategory();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightJPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Symptoms
        name = "Symptoms";
        String[] symptoms = findingAdapter.getSymptoms();
        value = symptoms != null ? Arrays.toString(symptoms) : null;

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightJPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Apply To
        name = "Apply To";
        Map<String, String> applyToMap = findingAdapter.getApplyTo();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getApplyToJPanel(applyToMap, new Insets(0, 0, 0, 0));
        weightJPanel = new WeightPanel(nameJPanel, 1);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        // Description
        name = "Description";
        value = findingAdapter.getDescription();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getTextAreaValueJPanel(value, new Insets(0, 0, 0, 0));
        weightJPanel = new WeightPanel(nameJPanel, 0.5);
        findingAdapterJPanelMap.put(weightJPanel, valueJPanel);

        return findingAdapterJPanelMap;
    }

    private JPanel getGraphFindingAdapterJPanel(GraphFindingAdapter graphFindingAdapter) {

        JPanel graphFindingAdapterJPanel = new JPanel();
        graphFindingAdapterJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel getGraphFindingDetailsJPanel = getGraphFindingDetailsJPanel(graphFindingAdapter);

        graphFindingAdapterJPanel.add(getGraphFindingDetailsJPanel, gbc1);

        JTabbedPane tabbedPane = new JTabbedPane();

        int tabCounter = 0;
        Component component = null;

        // Wait-For Graph
        component = graphFindingAdapter.getWaitForGraphComponent();

        if (component != null) {

            String tabText = "Wait-For Graph";
            JPanel graphJPanel = getGraphJPanel(tabText, component);

            JLabel tabLabel = new JLabel(tabText);
            Font labelFont = tabLabel.getFont();
            Font tabFont = labelFont.deriveFont(Font.BOLD, 11);
            Dimension dim = new Dimension(200, 20);
            tabLabel.setFont(tabFont);
            tabLabel.setPreferredSize(dim);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

            tabbedPane.addTab(tabText, graphJPanel);
            tabbedPane.setTabComponentAt(tabCounter, tabLabel);

            tabCounter++;
        }

        // Resource Allocation Graph Graph
        component = graphFindingAdapter.getResourceAllocationGraphComponent();

        if (component != null) {

            String tabText = "Resource Allocation Graph";
            JPanel graphJPanel = getGraphJPanel(tabText, component);

            JLabel tabLabel = new JLabel(tabText);
            Font labelFont = tabLabel.getFont();
            Font tabFont = labelFont.deriveFont(Font.BOLD, 11);
            Dimension dim = new Dimension(200, 20);
            tabLabel.setFont(tabFont);
            tabLabel.setPreferredSize(dim);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

            tabbedPane.addTab(tabText, graphJPanel);
            tabbedPane.setTabComponentAt(tabCounter, tabLabel);

            tabCounter++;
        }

        graphFindingAdapterJPanel.add(tabbedPane, gbc2);

        return graphFindingAdapterJPanel;
    }

    private JPanel getGraphFindingDetailsJPanel(GraphFindingAdapter graphFindingAdapter) {

        JPanel graphFindingDetailsJPanel = new JPanel();

        graphFindingDetailsJPanel.setLayout(new GridBagLayout());

        LinkedHashMap<WeightPanel, JPanel> findingAdapterJPanelMap = getGraphFindingDetailsMap(graphFindingAdapter);

        int yindex = 0;

        for (Map.Entry<WeightPanel, JPanel> entry : findingAdapterJPanelMap.entrySet()) {

            WeightPanel weightJPanel = entry.getKey();
            JPanel weightNamePanel = weightJPanel.getPanel();
            double weighty = weightJPanel.getWeighty();

            JPanel weightValuePanel = entry.getValue();

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = yindex;
            gbc1.weightx = 0.0D;
            gbc1.weighty = weighty;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 0, 0, 0);

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 1;
            gbc2.gridy = yindex;
            gbc2.weightx = 1.0D;
            gbc2.weighty = weighty;
            gbc2.fill = GridBagConstraints.BOTH;
            gbc2.anchor = GridBagConstraints.NORTHWEST;
            gbc2.insets = new Insets(0, 0, 0, 0);

            yindex++;

            graphFindingDetailsJPanel.add(weightNamePanel, gbc1);
            graphFindingDetailsJPanel.add(weightValuePanel, gbc2);
        }

        return graphFindingDetailsJPanel;
    }

    private LinkedHashMap<WeightPanel, JPanel> getGraphFindingDetailsMap(GraphFindingAdapter graphFindingAdapter) {

        LinkedHashMap<WeightPanel, JPanel> findingAdapterJPanelMap = getFindingAdapterJPanelMap(graphFindingAdapter);

        Insets insets = new Insets(5, 5, 5, 5);
        Dimension preferredSize = new Dimension(150, 20);

        // isCyclic
        String name = "Is Cyclic";
        Boolean cyclic = graphFindingAdapter.isCyclic();
        String value = cyclic != null ? String.valueOf(cyclic) : null;

        JPanel nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        JPanel valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        WeightPanel weightPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightPanel, valueJPanel);

        // getCyclicPath
        name = "Cyclic Path ";
        value = graphFindingAdapter.getCyclicPath();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightPanel, valueJPanel);

        // getThreadsCount
        name = "Thread Count";
        Integer threadCount = graphFindingAdapter.getThreadsCount();
        value = threadCount != null ? String.valueOf(threadCount) : null;

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightPanel, valueJPanel);

        // getRootName
        name = "Root Name";
        value = graphFindingAdapter.getRootName();

        nameJPanel = getNameJPanel(name, SwingConstants.LEFT, insets, preferredSize);
        valueJPanel = getLabelValueJPanel(value, insets, preferredSize);
        weightPanel = new WeightPanel(nameJPanel, 0);
        findingAdapterJPanelMap.put(weightPanel, valueJPanel);

        return findingAdapterJPanelMap;
    }

    private JPanel getGraphJPanel(String title, Component component) {

        JPanel graphJPanel = new JPanel();
        graphJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        Insets insets = new Insets(5, 5, 5, 5);
        JPanel titleJPanel = getNameJPanel(title, SwingConstants.CENTER, insets, null);

        graphJPanel.add(titleJPanel, gbc1);
        graphJPanel.add(component, gbc2);

        return graphJPanel;
    }

    private String sanitiseThreadDumpText(String threadDumpText) throws Exception {

        StringBuilder sanitisedThreadDumpText = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new StringReader(threadDumpText));

        String line = null;

        while ((line = bufferedReader.readLine()) != null) {

            String sanitisedLine = line.replaceAll("(\\d+-\\d+-\\d+T\\d+:\\d+:\\d+.\\d\\d\\dZ)", "");

            sanitisedThreadDumpText.append(sanitisedLine);
            sanitisedThreadDumpText.append(System.getProperty("line.separator"));
        }

        return sanitisedThreadDumpText.toString();
    }

    private class WeightPanel {

        private JPanel panel;

        private double weighty;

        public WeightPanel(JPanel panel, double weighty) {
            super();
            this.panel = panel;
            this.weighty = weighty;
        }

        protected JPanel getPanel() {
            return panel;
        }

        protected double getWeighty() {
            return weighty;
        }

    }
}
