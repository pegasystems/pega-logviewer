/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixStatus;

public class HotfixEntryDetailPanel extends JPanel {

    private static final long serialVersionUID = -3435154792009295016L;

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixEntryDetailPanel.class);

    private HotfixEntry hotfixEntry;

    private List<HotfixColumn> hotfixColumnList;

    private JEditorPane hfixIdJEditorPane;

    private JEditorPane hfixDescJEditorPane;

    private JLabel stateJLabel;

    private JLabel statusJLabel;

    private JTextArea dependencyTextArea;

    private HotfixRecordEntryTable hotfixRecordEntryTable;

    public HotfixEntryDetailPanel(HotfixEntry hotfixEntry, List<HotfixColumn> hotfixColumnList) {
        super();
        this.hotfixEntry = hotfixEntry;
        this.hotfixColumnList = hotfixColumnList;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 2, 2, 2);
        gbc3.gridwidth = GridBagConstraints.REMAINDER;

        JPanel detailsPanel = getDetailsJPanel();
        JScrollPane dependencyJScrollPane = getDependencyJScrollPane();
        JScrollPane tableJScrollPane = getTableJScrollPane();

        add(detailsPanel, gbc1);
        add(dependencyJScrollPane, gbc2);
        add(tableJScrollPane, gbc3);

        populateData();
    }

    private JScrollPane getDependencyJScrollPane() {

        JTextArea dependencyTextArea = getDependencyTextArea();

        JScrollPane dependencyJScrollPane = new JScrollPane(dependencyTextArea);

        return dependencyJScrollPane;
    }

    private JScrollPane getTableJScrollPane() {

        HotfixRecordEntryTable hotfixRecordEntryTable = getHotfixRecordEntryTable();

        JScrollPane tableJScrollPane = new JScrollPane(hotfixRecordEntryTable);

        return tableJScrollPane;
    }

    private JEditorPane getHfixIdJEditorPane() {

        if (hfixIdJEditorPane == null) {

            hfixIdJEditorPane = new JEditorPane();

            hfixIdJEditorPane.setSize(Integer.MAX_VALUE, 80);
            hfixIdJEditorPane.setEditable(false);
            hfixIdJEditorPane.setContentType("text/html");
            hfixIdJEditorPane.setOpaque(false);
            hfixIdJEditorPane.setBackground(this.getBackground());

            StyleSheet styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

            if (styleSheet != null) {

                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
                htmlStyleSheet.addStyleSheet(styleSheet);

                hfixIdJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
            }

            hfixIdJEditorPane.addHyperlinkListener(new HyperlinkListener() {

                @Override
                public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {

                    if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {

                        if (Desktop.isDesktopSupported()) {

                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(hyperlinkEvent.getURL().toURI());
                            } catch (Exception ex) {
                                LOG.error("Error in invoking browser url: " + hyperlinkEvent.getURL(), ex);
                            }
                        }
                    }

                }
            });
        }

        return hfixIdJEditorPane;
    }

    private JEditorPane getHfixDescJEditorPane() {

        if (hfixDescJEditorPane == null) {

            hfixDescJEditorPane = new JEditorPane();

            hfixDescJEditorPane.setEditable(false);
            hfixDescJEditorPane.setContentType("text/html");
            hfixDescJEditorPane.setOpaque(false);
            hfixDescJEditorPane.setBackground(this.getBackground());

            StyleSheet styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

            if (styleSheet != null) {

                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
                htmlStyleSheet.addStyleSheet(styleSheet);

                hfixDescJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
            }

            hfixDescJEditorPane.addHyperlinkListener(new HyperlinkListener() {

                @Override
                public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {

                    if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {

                        if (Desktop.isDesktopSupported()) {

                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(hyperlinkEvent.getURL().toURI());
                            } catch (Exception ex) {
                                LOG.error("Error in invoking browser url: " + hyperlinkEvent.getURL(), ex);
                            }
                        }
                    }

                }
            });
        }

        return hfixDescJEditorPane;
    }

    private JLabel getStateJLabel() {

        if (stateJLabel == null) {
            stateJLabel = new JLabel();
        }

        return stateJLabel;
    }

    private JLabel getStatusJLabel() {

        if (statusJLabel == null) {
            statusJLabel = new JLabel();
        }

        return statusJLabel;
    }

    private JTextArea getDependencyTextArea() {

        if (dependencyTextArea == null) {

            dependencyTextArea = new JTextArea();
            dependencyTextArea.setRows(5);
            dependencyTextArea.setEditable(false);
            dependencyTextArea.setBackground(null);
            dependencyTextArea.setBorder(null);
            dependencyTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }

        return dependencyTextArea;
    }

    private HotfixRecordEntryTable getHotfixRecordEntryTable() {

        if (hotfixRecordEntryTable == null) {

            HotfixRecordEntryTableModel hotfixRecordEntryTableModel = new HotfixRecordEntryTableModel(hotfixEntry,
                    hotfixColumnList);

            hotfixRecordEntryTable = new HotfixRecordEntryTable(hotfixRecordEntryTableModel);
        }

        return hotfixRecordEntryTable;
    }

    private JPanel getDetailsJPanel() {

        JPanel detailsPanel = new JPanel();

        detailsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 1.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 1.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 1.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 3;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 1.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 1.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(2, 2, 2, 2);

        Font existingFont = getFont();
        String existingFontName = existingFont.getName();
        int existFontSize = existingFont.getSize();
        Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);

        JLabel idLabelJLabel = new JLabel("Hotfix ID: ");
        JLabel descriptionLabelJLabel = new JLabel("Hotfix Description: ");
        JLabel stateLabelJLabel = new JLabel("Hotfix State: ");
        JLabel statusLabelJLabel = new JLabel("Hotfix Status: ");

        idLabelJLabel.setFont(newFont);
        descriptionLabelJLabel.setFont(newFont);
        stateLabelJLabel.setFont(newFont);
        statusLabelJLabel.setFont(newFont);

        JEditorPane hfixIdJEditorPane = getHfixIdJEditorPane();
        JEditorPane hfixDescJEditorPane = getHfixDescJEditorPane();
        JLabel stateJLabel = getStateJLabel();
        JLabel statusJLabel = getStatusJLabel();

        detailsPanel.add(idLabelJLabel, gbc1);
        detailsPanel.add(hfixIdJEditorPane, gbc2);
        detailsPanel.add(descriptionLabelJLabel, gbc3);
        detailsPanel.add(hfixDescJEditorPane, gbc4);
        detailsPanel.add(stateLabelJLabel, gbc5);
        detailsPanel.add(stateJLabel, gbc6);
        detailsPanel.add(statusLabelJLabel, gbc7);
        detailsPanel.add(statusJLabel, gbc8);

        detailsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return detailsPanel;
    }

    private void populateData() {

        JEditorPane hfixIdJEditorPane = getHfixIdJEditorPane();
        JEditorPane hfixDescJEditorPane = getHfixDescJEditorPane();
        JLabel stateJLabel = getStateJLabel();
        JLabel statusJLabel = getStatusJLabel();
        JTextArea dependencyTextArea = getDependencyTextArea();

        String hotfixId;
        String description;
        String data = null;
        HotfixColumn hotfixColumn = null;
        int columnIndex = -1;

        hotfixId = hotfixEntry.getHotfixId();
        data = LogViewerUtil.getHotfixIDHyperlinkText(hotfixId);
        hfixIdJEditorPane.setText(data);

        hotfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
        columnIndex = hotfixColumnList.indexOf(hotfixColumn);
        description = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);

        data = LogViewerUtil.getHotfixDescHyperlinkText(description);
        hfixDescJEditorPane.setText(data);

        hotfixColumn = HotfixColumn.PXHOTFIXSTATE;
        columnIndex = hotfixColumnList.indexOf(hotfixColumn);
        data = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);
        stateJLabel.setText(data);

        // catalog doesnt have status
        HotfixStatus hotfixStatus = hotfixEntry.getHotfixStatus();

        if (hotfixStatus != null) {
            data = hotfixStatus.getDisplayString();
        } else {
            data = "";
        }

        statusJLabel.setText(data);

        StringBuilder sb = new StringBuilder();
        TreeSet<HotfixEntry> totalBackwardHotfixEntrySet = new TreeSet<>();

        LogViewerUtil.recursiveEvaluateBackwardHotfixEntrySet(hotfixEntry, totalBackwardHotfixEntrySet);

        for (HotfixEntry hotfixEntry : totalBackwardHotfixEntrySet) {

            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);

            sb.append(hotfixEntry.getHotfixId());
            sb.append(": ");
            sb.append(hfixDesc);
            sb.append("\n");
        }

        sb.append("\n");
        sb.append(hotfixId);
        sb.append(": ");
        sb.append(description);
        sb.append("\n\n");

        Set<HotfixEntry> forwardHotfixEntryList = hotfixEntry.getForwardHotfixEntrySet();

        for (HotfixEntry hotfixEntry : forwardHotfixEntryList) {
            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);

            sb.append(hotfixEntry.getHotfixId());
            sb.append(": ");
            sb.append(hfixDesc);
            sb.append("\n");
        }

        dependencyTextArea.setText(sb.toString());
    }
}
