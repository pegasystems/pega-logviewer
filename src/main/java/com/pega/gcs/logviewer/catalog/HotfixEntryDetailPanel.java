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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
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

    private JLabel packageDateLabel;

    private JTextArea jarsToRemoveTextArea;

    private JTextPane ancestryTextArea;

    private HotfixRecordEntryTable hotfixRecordEntryTable;

    private String hotfixIdUrl;

    public HotfixEntryDetailPanel(HotfixEntry hotfixEntry, List<HotfixColumn> hotfixColumnList) {

        super();

        this.hotfixEntry = hotfixEntry;
        this.hotfixColumnList = hotfixColumnList;
        this.hotfixIdUrl = null;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        JPanel headerPanel = getHeaderPanel();
        HotfixRecordEntryTable hotfixRecordEntryTable = getHotfixRecordEntryTable();
        JScrollPane tableScrollPane = new JScrollPane(hotfixRecordEntryTable);

        JSplitPane dataTableSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headerPanel, tableScrollPane);

        dataTableSplitPane.setContinuousLayout(true);
        dataTableSplitPane.setDividerLocation(0.4d);
        // dataTableSplitPane.setResizeWeight(0.5);

        add(dataTableSplitPane, gbc1);

        populateData();
    }

    private JPanel getHeaderPanel() {

        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel detailsPanel = getDetailsPanel();

        JPanel ancestryPanel = getHotfixAncestryPanel();

        headerPanel.add(detailsPanel, gbc1);
        headerPanel.add(ancestryPanel, gbc2);

        return headerPanel;
    }

    private JPanel getDetailsPanel() {

        JPanel detailsPanel = new JPanel();

        detailsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(5, 2, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 2, 2, 2);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 2, 2, 2);

        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 3;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(5, 2, 2, 2);

        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 0;
        gbc9.gridy = 4;
        gbc9.weightx = 0.0D;
        gbc9.weighty = 0.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.gridx = 1;
        gbc10.gridy = 4;
        gbc10.weightx = 1.0D;
        gbc10.weighty = 0.0D;
        gbc10.fill = GridBagConstraints.BOTH;
        gbc10.anchor = GridBagConstraints.NORTHWEST;
        gbc10.insets = new Insets(5, 2, 2, 2);

        GridBagConstraints gbc11 = new GridBagConstraints();
        gbc11.gridx = 0;
        gbc11.gridy = 5;
        gbc11.weightx = 0.0D;
        gbc11.weighty = 1.0D;
        gbc11.fill = GridBagConstraints.BOTH;
        gbc11.anchor = GridBagConstraints.NORTHWEST;
        gbc11.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc12 = new GridBagConstraints();
        gbc12.gridx = 1;
        gbc12.gridy = 5;
        gbc12.weightx = 1.0D;
        gbc12.weighty = 1.0D;
        gbc12.fill = GridBagConstraints.BOTH;
        gbc12.anchor = GridBagConstraints.NORTHWEST;
        gbc12.insets = new Insets(2, 2, 2, 2);

        Font existingFont = getFont();
        String existingFontName = existingFont.getName();
        int existFontSize = existingFont.getSize();
        Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);

        JLabel idNameLabel = new JLabel("Hotfix ID: ");
        JLabel descriptionNameLabel = new JLabel("Hotfix Description: ");
        JLabel stateNameLabel = new JLabel("Hotfix State: ");
        JLabel statusNameLabel = new JLabel("Hotfix Status: ");
        JLabel packageDateNameLabel = new JLabel("Package Date: ");
        JLabel jarsToRemoveNameLabel = new JLabel("Jars To Remove: ");

        idNameLabel.setFont(newFont);
        descriptionNameLabel.setFont(newFont);
        stateNameLabel.setFont(newFont);
        statusNameLabel.setFont(newFont);
        packageDateNameLabel.setFont(newFont);
        jarsToRemoveNameLabel.setFont(newFont);

        JEditorPane hfixIdEditorPane = getHfixIdEditorPane();
        JEditorPane hfixDescEditorPane = getHfixDescEditorPane();
        JLabel stateLabel = getStateLabel();
        JLabel statusLabel = getStatusLabel();
        JLabel packageDateLabel = getPackageDateLabel();
        JTextArea jarsToRemoveTextArea = getJarsToRemoveTextArea();
        JScrollPane jarsToRemoveScrollPane = new JScrollPane(jarsToRemoveTextArea);
        jarsToRemoveScrollPane.setBorder(null);

        detailsPanel.add(idNameLabel, gbc1);
        detailsPanel.add(hfixIdEditorPane, gbc2);
        detailsPanel.add(descriptionNameLabel, gbc3);
        detailsPanel.add(hfixDescEditorPane, gbc4);
        detailsPanel.add(stateNameLabel, gbc5);
        detailsPanel.add(stateLabel, gbc6);
        detailsPanel.add(statusNameLabel, gbc7);
        detailsPanel.add(statusLabel, gbc8);
        detailsPanel.add(packageDateNameLabel, gbc9);
        detailsPanel.add(packageDateLabel, gbc10);
        detailsPanel.add(jarsToRemoveNameLabel, gbc11);
        detailsPanel.add(jarsToRemoveScrollPane, gbc12);

        detailsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return detailsPanel;
    }

    private JPanel getHotfixAncestryPanel() {

        JPanel ancestryPanel = new JPanel();

        ancestryPanel.setLayout(new GridBagLayout());

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
        gbc2.insets = new Insets(2, 5, 2, 2);

        Font existingFont = getFont();
        String existingFontName = existingFont.getName();
        int existFontSize = existingFont.getSize();
        Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);

        JLabel ancestryLabel = new JLabel("Hotfix Ancestry", SwingConstants.CENTER);
        ancestryLabel.setFont(newFont);

        JPanel ancestryLabelPanel = new JPanel();
        ancestryLabelPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc11 = new GridBagConstraints();
        gbc11.gridx = 0;
        gbc11.gridy = 0;
        gbc11.weightx = 1.0D;
        gbc11.weighty = 0.0D;
        gbc11.fill = GridBagConstraints.BOTH;
        gbc11.anchor = GridBagConstraints.NORTHWEST;
        gbc11.insets = new Insets(5, 5, 5, 5);

        ancestryLabelPanel.add(ancestryLabel, gbc11);
        ancestryLabelPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JTextPane ancestryTextArea = getAncestryTextArea();
        JScrollPane dependencyScrollPane = new JScrollPane(ancestryTextArea);
        dependencyScrollPane.setBorder(null);

        ancestryPanel.add(ancestryLabelPanel, gbc1);
        ancestryPanel.add(dependencyScrollPane, gbc2);

        ancestryPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return ancestryPanel;
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

            hfixIdEditorPane.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                        final JPopupMenu popupMenu = new JPopupMenu();

                        final RightClickMenuItem copyUrlMenuItem = new RightClickMenuItem("Copy Link");

                        copyUrlMenuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {

                                String hotfixIdUrl = getHotfixIdUrl();

                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                                clipboard.setContents(new StringSelection(hotfixIdUrl), copyUrlMenuItem);

                                popupMenu.setVisible(false);

                            }
                        });

                        popupMenu.add(copyUrlMenuItem);

                        popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());

                    } else {
                        super.mouseClicked(mouseEvent);
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
            hfixDescEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));

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

    private JLabel getPackageDateLabel() {

        if (packageDateLabel == null) {
            packageDateLabel = new JLabel();
        }

        return packageDateLabel;
    }

    private JTextArea getJarsToRemoveTextArea() {

        if (jarsToRemoveTextArea == null) {

            jarsToRemoveTextArea = new JTextArea();
            jarsToRemoveTextArea.setRows(5);
            jarsToRemoveTextArea.setEditable(false);
            jarsToRemoveTextArea.setBackground(null);
            jarsToRemoveTextArea.setBorder(null);
            jarsToRemoveTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            jarsToRemoveTextArea.setLineWrap(true);
            jarsToRemoveTextArea.setWrapStyleWord(true);
        }

        return jarsToRemoveTextArea;
    }

    private JTextPane getAncestryTextArea() {

        if (ancestryTextArea == null) {

            ancestryTextArea = new JTextPane();

            ancestryTextArea.setEditable(false);
            ancestryTextArea.setContentType("text/html");
            ancestryTextArea.setOpaque(false);
            ancestryTextArea.setBackground(this.getBackground());
            ancestryTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

            StyleSheet styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

            if (styleSheet != null) {

                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
                htmlStyleSheet.addStyleSheet(styleSheet);

                ancestryTextArea.setEditorKitForContentType("text/html", htmlEditorKit);
            }

        }

        return ancestryTextArea;
    }

    private HotfixRecordEntryTable getHotfixRecordEntryTable() {

        if (hotfixRecordEntryTable == null) {

            HotfixRecordEntryTableModel hotfixRecordEntryTableModel = new HotfixRecordEntryTableModel(hotfixEntry,
                    hotfixColumnList);

            hotfixRecordEntryTable = new HotfixRecordEntryTable(hotfixRecordEntryTableModel);
        }

        return hotfixRecordEntryTable;
    }

    private String getHotfixIdUrl() {
        return hotfixIdUrl;
    }

    private void setHotfixIdUrl(String hotfixIdUrl) {
        this.hotfixIdUrl = hotfixIdUrl;
    }

    private void populateData() {

        JEditorPane hfixIdEditorPane = getHfixIdEditorPane();
        JEditorPane hfixDescEditorPane = getHfixDescEditorPane();

        JLabel stateLabel = getStateLabel();
        JLabel statusLabel = getStatusLabel();
        JLabel packageDateLabel = getPackageDateLabel();
        JTextArea jarsToRemoveTextArea = getJarsToRemoveTextArea();
        JTextPane ancestryTextArea = getAncestryTextArea();

        String hotfixId;
        String description;
        String data;
        HotfixColumn hotfixColumn;
        int columnIndex;

        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();

        hotfixId = hotfixEntry.getKey().getHotfixId();
        String hotfixIdUrl = catalogManagerWrapper.getWorkItemLink(hotfixId);

        setHotfixIdUrl(hotfixIdUrl);

        data = GUIUtilities.getHyperlinkText(hotfixIdUrl, hotfixId);

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

        data = hotfixEntry.getPackageDate();
        packageDateLabel.setText(data);

        data = hotfixEntry.getJarsToRemove();

        jarsToRemoveTextArea.setText(data);

        String dependencyText = getAncestryText(hotfixId, description);
        ancestryTextArea.setText(dependencyText);
    }

    private String getDependencyText(String hotfixId, String description) {

        StringBuilder dependencyTextSB = new StringBuilder();

        TreeSet<HotfixEntry> totalBackwardHotfixEntrySet = new TreeSet<>();

        LogViewerUtil.recursiveEvaluateBackwardHotfixEntrySet(hotfixEntry, totalBackwardHotfixEntrySet);

        for (HotfixEntry backwardHotfixEntry : totalBackwardHotfixEntrySet) {

            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            int columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = backwardHotfixEntry.getHotfixEntryData(hfixColumn, columnIndex);

            dependencyTextSB.append(backwardHotfixEntry.getKey().getHotfixId());
            dependencyTextSB.append(": ");
            dependencyTextSB.append(hfixDesc);
            dependencyTextSB.append("\n");
        }

        dependencyTextSB.append("\n");
        dependencyTextSB.append(hotfixId);
        dependencyTextSB.append(": ");
        dependencyTextSB.append(description);
        dependencyTextSB.append("\n\n");

        Set<HotfixEntry> forwardHotfixEntryList = hotfixEntry.getForwardHotfixEntrySet();

        for (HotfixEntry forwardHotfixEntry : forwardHotfixEntryList) {
            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            int columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = forwardHotfixEntry.getHotfixEntryData(hfixColumn, columnIndex);

            dependencyTextSB.append(forwardHotfixEntry.getKey().getHotfixId());
            dependencyTextSB.append(": ");
            dependencyTextSB.append(hfixDesc);
            dependencyTextSB.append("\n");
        }

        return dependencyTextSB.toString();

    }

    private String getAncestryText(String hotfixId, String description) {

        StringBuilder ancestryTextSB = new StringBuilder();

        ancestryTextSB.append("<html><div>");

        TreeSet<HotfixEntry> totalBackwardHotfixEntrySet = new TreeSet<>();

        LogViewerUtil.recursiveEvaluateBackwardHotfixEntrySet(hotfixEntry, totalBackwardHotfixEntrySet);

        for (HotfixEntry backwardHotfixEntry : totalBackwardHotfixEntrySet) {

            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            int columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = backwardHotfixEntry.getHotfixEntryData(hfixColumn, columnIndex);

            ancestryTextSB.append("<p>");
            ancestryTextSB.append(backwardHotfixEntry.getKey().getHotfixId());
            ancestryTextSB.append(": ");
            ancestryTextSB.append(hfixDesc);
            ancestryTextSB.append("</p>");
        }

        ancestryTextSB.append("<p></p>");
        ancestryTextSB.append("<p style=\"color:blue; font-weight:bold;\">");
        ancestryTextSB.append(hotfixId);
        ancestryTextSB.append(": ");
        ancestryTextSB.append(description);
        ancestryTextSB.append("</p>");
        ancestryTextSB.append("<p></p>");

        Set<HotfixEntry> forwardHotfixEntryList = hotfixEntry.getForwardHotfixEntrySet();

        for (HotfixEntry forwardHotfixEntry : forwardHotfixEntryList) {
            HotfixColumn hfixColumn = HotfixColumn.HOTFIX_DESCRIPTION;
            int columnIndex = hotfixColumnList.indexOf(hfixColumn);
            String hfixDesc = forwardHotfixEntry.getHotfixEntryData(hfixColumn, columnIndex);

            ancestryTextSB.append("<p>");
            ancestryTextSB.append(forwardHotfixEntry.getKey().getHotfixId());
            ancestryTextSB.append(": ");
            ancestryTextSB.append(hfixDesc);
            ancestryTextSB.append("</p>");
        }

        ancestryTextSB.append("</div></html>");

        return ancestryTextSB.toString();

    }
}
