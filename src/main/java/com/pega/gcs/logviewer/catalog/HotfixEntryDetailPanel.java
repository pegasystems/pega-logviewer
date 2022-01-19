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

    private JEditorPane hfixIdEditorPane;

    private JEditorPane hfixDescEditorPane;

    private JLabel stateLabel;

    private JLabel statusLabel;

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

        JPanel detailsPanel = getDetailsPanel();
        JScrollPane dependencyJScrollPane = getDependencyScrollPane();
        JScrollPane tableJScrollPane = getTableScrollPane();

        add(detailsPanel, gbc1);
        add(dependencyJScrollPane, gbc2);
        add(tableJScrollPane, gbc3);

        populateData();
    }

    private JScrollPane getDependencyScrollPane() {

        JTextArea dependencyTextArea = getDependencyTextArea();

        JScrollPane dependencyScrollPane = new JScrollPane(dependencyTextArea);

        return dependencyScrollPane;
    }

    private JScrollPane getTableScrollPane() {

        HotfixRecordEntryTable hotfixRecordEntryTable = getHotfixRecordEntryTable();

        JScrollPane tableScrollPane = new JScrollPane(hotfixRecordEntryTable);

        return tableScrollPane;
    }

    private JEditorPane getHfixIdEditorPane() {

        if (hfixIdEditorPane == null) {

            hfixIdEditorPane = new JEditorPane();

            hfixIdEditorPane.setSize(Integer.MAX_VALUE, 80);
            hfixIdEditorPane.setEditable(false);
            hfixIdEditorPane.setContentType("text/html");
            hfixIdEditorPane.setOpaque(false);
            hfixIdEditorPane.setBackground(this.getBackground());

            StyleSheet styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

            if (styleSheet != null) {

                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
                htmlStyleSheet.addStyleSheet(styleSheet);

                hfixIdEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
            }

            hfixIdEditorPane.addHyperlinkListener(new HyperlinkListener() {

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

        return hfixIdEditorPane;
    }

    private JEditorPane getHfixDescEditorPane() {

        if (hfixDescEditorPane == null) {

            hfixDescEditorPane = new JEditorPane();

            hfixDescEditorPane.setEditable(false);
            hfixDescEditorPane.setContentType("text/html");
            hfixDescEditorPane.setOpaque(false);
            hfixDescEditorPane.setBackground(this.getBackground());

            StyleSheet styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

            if (styleSheet != null) {

                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
                htmlStyleSheet.addStyleSheet(styleSheet);

                hfixDescEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
            }

            hfixDescEditorPane.addHyperlinkListener(new HyperlinkListener() {

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

        return hfixDescEditorPane;
    }

    private JLabel getStateLabel() {

        if (stateLabel == null) {
            stateLabel = new JLabel();
        }

        return stateLabel;
    }

    private JLabel getStatusLabel() {

        if (statusLabel == null) {
            statusLabel = new JLabel();
        }

        return statusLabel;
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

    private JPanel getDetailsPanel() {

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

        JLabel idNameLabel = new JLabel("Hotfix ID: ");
        JLabel descriptionNameLabel = new JLabel("Hotfix Description: ");
        JLabel stateNameLabel = new JLabel("Hotfix State: ");
        JLabel statusNameLabel = new JLabel("Hotfix Status: ");

        idNameLabel.setFont(newFont);
        descriptionNameLabel.setFont(newFont);
        stateNameLabel.setFont(newFont);
        statusNameLabel.setFont(newFont);

        JEditorPane hfixIdEditorPane = getHfixIdEditorPane();
        JEditorPane hfixDescEditorPane = getHfixDescEditorPane();
        JLabel stateLabel = getStateLabel();
        JLabel statusLabel = getStatusLabel();

        detailsPanel.add(idNameLabel, gbc1);
        detailsPanel.add(hfixIdEditorPane, gbc2);
        detailsPanel.add(descriptionNameLabel, gbc3);
        detailsPanel.add(hfixDescEditorPane, gbc4);
        detailsPanel.add(stateNameLabel, gbc5);
        detailsPanel.add(stateLabel, gbc6);
        detailsPanel.add(statusNameLabel, gbc7);
        detailsPanel.add(statusLabel, gbc8);

        detailsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return detailsPanel;
    }

    private void populateData() {

        JEditorPane hfixIdEditorPane = getHfixIdEditorPane();
        JEditorPane hfixDescEditorPane = getHfixDescEditorPane();
        JLabel stateLabel = getStateLabel();
        JLabel statusLabel = getStatusLabel();
        JTextArea dependencyTextArea = getDependencyTextArea();

        String hotfixId;
        String description;
        String data = null;
        HotfixColumn hotfixColumn = null;
        int columnIndex = -1;

        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();

        hotfixId = hotfixEntry.getHotfixId();
        data = catalogManagerWrapper.getWorkItemHyperlinkText(hotfixId);
        hfixIdEditorPane.setText(data);

        hotfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
        columnIndex = hotfixColumnList.indexOf(hotfixColumn);
        description = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);

        data = catalogManagerWrapper.getWorkDescHyperlinkText(description);
        hfixDescEditorPane.setText(data);

        hotfixColumn = HotfixColumn.PXHOTFIXSTATE;
        columnIndex = hotfixColumnList.indexOf(hotfixColumn);
        data = hotfixEntry.getHotfixEntryData(hotfixColumn, columnIndex);
        stateLabel.setText(data);

        // catalog doesnt have status
        HotfixStatus hotfixStatus = hotfixEntry.getHotfixStatus();

        if (hotfixStatus != null) {
            data = hotfixStatus.getDisplayString();
        } else {
            data = "";
        }

        statusLabel.setText(data);

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
