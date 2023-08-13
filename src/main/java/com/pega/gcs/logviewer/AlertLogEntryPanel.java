/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.commons.text.StringEscapeUtils;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.alert.AlertLogEntryPanelUtil;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogEntryPanel extends JPanel {

    private static final long serialVersionUID = 7722214923071969992L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogEntryPanel.class);

    private static int selectedIndex = 0;

    private AlertLogEntry alertLogEntry;

    // multiple usages hence extract it once
    private ArrayList<String> logEntryValueList;

    private String logEntryText;

    private Charset charset;

    private AlertLogEntryModel alertLogEntryModel;

    private JTabbedPane alertTabbedPane;

    private StyleSheet styleSheet;

    private List<LogEntryColumn> tabColumnList;

    private List<LogEntryColumn> longDataColumnList;

    public AlertLogEntryPanel(AlertLogEntry alertLogEntry, AlertLogEntryModel alertLogEntryModel, Charset charset) {
        super();

        this.alertLogEntry = alertLogEntry;
        this.charset = charset;

        this.logEntryValueList = alertLogEntry.getLogEntryValueList();
        this.logEntryText = alertLogEntry.getLogEntryText();

        this.alertLogEntryModel = alertLogEntryModel;

        tabColumnList = new ArrayList<>();
        tabColumnList.add(LogEntryColumn.PALDATA);
        tabColumnList.add(LogEntryColumn.TRACELIST);
        tabColumnList.add(LogEntryColumn.PRSTACKTRACE);
        tabColumnList.add(LogEntryColumn.PARAMETERPAGEDATA);

        longDataColumnList = new ArrayList<>();
        longDataColumnList.add(LogEntryColumn.LOGGER);
        longDataColumnList.add(LogEntryColumn.STACK);
        longDataColumnList.add(LogEntryColumn.LASTINPUT);
        longDataColumnList.add(LogEntryColumn.FIRSTACTIVITY);
        longDataColumnList.add(LogEntryColumn.LASTSTEP);
        longDataColumnList.add(LogEntryColumn.MESSAGE);

        styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

        setLayout(new BorderLayout());

        JTabbedPane alertTabbedPane = getAlertTabbedPane();
        add(alertTabbedPane, BorderLayout.CENTER);

        JComponent alertComponent = getAlertComponent();
        JScrollPane palComponent = getPalComponent();
        JComponent traceListComponent = getTraceListComponent();
        JComponent prStacktraceComponent = getPRStacktraceComponent();
        JComponent parameterPageComponent = getParameterPageComponent();
        JComponent rawTextComponent = getRawTextComponent();

        int tabIndex = 0;

        String tabText = "Alert Detail";
        JLabel tabLabel = new JLabel(tabText);
        Font labelFont = tabLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
        Dimension dim = new Dimension(140, 26);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, alertComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        tabText = LogEntryColumn.PALDATA.getDisplayName();
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, palComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        tabText = LogEntryColumn.TRACELIST.getDisplayName();
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, traceListComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        tabText = LogEntryColumn.PRSTACKTRACE.getDisplayName();
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, prStacktraceComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        tabText = LogEntryColumn.PARAMETERPAGEDATA.getDisplayName();
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, parameterPageComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        tabText = "Raw Text";
        tabLabel = new JLabel(tabText);
        tabLabel.setFont(tabFont);
        // tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        alertTabbedPane.addTab(tabText, rawTextComponent);
        alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);

        ChangeListener changeListener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeEvent) {

                JTabbedPane alertTabbedPane = getAlertTabbedPane();
                selectedIndex = alertTabbedPane.getSelectedIndex();
            }
        };

        // alert specific tabs
        AlertLogEntryPanelUtil alertLogEntryPanelUtil = AlertLogEntryPanelUtil.getInstance();

        Map<String, JComponent> additionalAlertTabs;
        additionalAlertTabs = alertLogEntryPanelUtil.getAdditionalAlertTabs(alertLogEntryModel, alertLogEntry);

        if (additionalAlertTabs != null) {

            for (Map.Entry<String, JComponent> entry : additionalAlertTabs.entrySet()) {

                tabText = entry.getKey();
                JComponent additionalComponent = entry.getValue();

                tabLabel = new JLabel(tabText);
                tabLabel.setFont(tabFont);
                // tabLabel.setSize(dim);
                tabLabel.setPreferredSize(dim);
                tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

                alertTabbedPane.addTab(tabText, additionalComponent);
                alertTabbedPane.setTabComponentAt(tabIndex++, tabLabel);
            }
        }

        int tabCount = alertTabbedPane.getTabCount();

        if (selectedIndex < tabCount) {
            alertTabbedPane.setSelectedIndex(selectedIndex);
        } else {
            selectedIndex = 0;
        }

        alertTabbedPane.addChangeListener(changeListener);
    }

    private ArrayList<String> getLogEntryValueList() {
        return logEntryValueList;
    }

    private String getLogEntryText() {
        return logEntryText;
    }

    private JTabbedPane getAlertTabbedPane() {

        if (alertTabbedPane == null) {
            alertTabbedPane = new JTabbedPane();
        }

        return alertTabbedPane;
    }

    private LogEntryModel getAlertLogEntryModel() {
        return alertLogEntryModel;
    }

    private JComponent getAlertComponent() {

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane alertJEditorPane = new JEditorPane();
        alertJEditorPane.setEditable(false);
        alertJEditorPane.setContentType("text/html");
        alertJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
        alertJEditorPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {

                if (hyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {

                    if (Desktop.isDesktopSupported()) {

                        Desktop desktop = Desktop.getDesktop();
                        try {
                            desktop.browse(hyperlinkEvent.getURL().toURI());
                        } catch (Exception e1) {
                            LOG.error("Error opening url: " + hyperlinkEvent.getURL(), e1);
                        }
                    }
                }
            }
        });

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<HTML><HEAD/><BODY><DIV>");

        htmlStringBuilder.append("<H3 align='center'>Alert Detail</H3>");

        List<AlertLogEntryPanelTableData> dataList = new ArrayList<AlertLogEntryPanelTableData>();
        List<AlertLogEntryPanelTableData> longDataList = new ArrayList<AlertLogEntryPanelTableData>();

        List<String> logEntryValueList = getLogEntryValueList();

        LogEntryModel logEntryModel = getAlertLogEntryModel();
        List<LogEntryColumn> logEntryColumnList = logEntryModel.getLogEntryColumnList();
        int columnIndex = 0;

        for (LogEntryColumn logEntryColumn : logEntryColumnList) {

            if (!tabColumnList.contains(logEntryColumn)) {

                String nameColumn = logEntryColumn.getDisplayName();
                String valueColumn = logEntryValueList.get(columnIndex);

                // bug - some sql contains '<>' in criteria, which splits the html table row.
                valueColumn = StringEscapeUtils.escapeHtml4(valueColumn);

                boolean isHref = false;

                if (logEntryColumn.equals(LogEntryColumn.MESSAGEID)) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("<b>");
                    sb.append(valueColumn);
                    sb.append("</b>");
                    valueColumn = sb.toString();
                } else if (logEntryColumn.equals(LogEntryColumn.STACK)) {
                    valueColumn = valueColumn.replaceAll("\\|", "\\<br/\\>");
                } else if (logEntryColumn.equals(LogEntryColumn.MESSAGE)) {
                    valueColumn = valueColumn.replaceAll("&lt;CR&gt;", "\\<br/\\>");
                }

                AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn,
                        valueColumn, isHref);

                if (longDataColumnList.contains(logEntryColumn)) {
                    longDataList.add(alertLogEntryPanelTableData);
                } else {
                    dataList.add(alertLogEntryPanelTableData);
                }
            }

            columnIndex++;
        }

        Integer alertId = alertLogEntry.getAlertId();

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

        AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(alertId);

        if (alertMessage != null) {

            String nameColumn;
            String valueColumn;
            boolean isHref;
            AlertLogEntryPanelTableData alertLogEntryPanelTableData;

            nameColumn = "Category";
            StringBuilder valueSB = new StringBuilder();
            valueSB.append(alertMessage.getCategory());
            valueSB.append("/");
            valueSB.append(alertMessage.getSubcategory());
            valueColumn = valueSB.toString();
            isHref = false;

            alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn, valueColumn, isHref);
            longDataList.add(alertLogEntryPanelTableData);

            nameColumn = "Title";
            valueColumn = alertMessage.getTitle();
            isHref = false;

            alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn, valueColumn, isHref);
            longDataList.add(alertLogEntryPanelTableData);

            nameColumn = "Pega URL";
            valueColumn = alertMessage.getPegaUrl();
            isHref = true;

            alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn, valueColumn, isHref);
            longDataList.add(alertLogEntryPanelTableData);

            nameColumn = "Description";
            valueColumn = alertMessage.getDescription();
            isHref = false;

            alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn, valueColumn, isHref);
            longDataList.add(alertLogEntryPanelTableData);

        }

        int middleIndex = (int) Math.ceil((dataList.size() / 3d));

        String defStyle = "width=\"100%\" border=\"1\"";
        List<AlertLogEntryPanelTableData> subDataList = dataList.subList(0, middleIndex);
        int size = subDataList.size();
        String table1Str = getTableHtmlStr(null, subDataList, defStyle);

        subDataList = dataList.subList(middleIndex, (middleIndex + size));
        String table2Str = getTableHtmlStr(null, subDataList, defStyle);

        subDataList = dataList.subList(middleIndex + size, dataList.size());
        int subSize = subDataList.size();

        for (int i = subSize; i < size; i++) {

            // empty strings will create empty rows
            String nameColumn = "";
            String valueColumn = "";
            boolean isHref = false;

            AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(nameColumn,
                    valueColumn, isHref);

            subDataList.add(alertLogEntryPanelTableData);
        }

        String table3Str = getTableHtmlStr(null, subDataList, defStyle);

        String tableLongStr = getTableHtmlStr(null, longDataList, defStyle);

        StringBuilder tablehtmlSB = new StringBuilder();

        tablehtmlSB.append("<table border=\"0\" width=\"900px\">");
        tablehtmlSB.append("<tr>");
        tablehtmlSB.append("<td valign=\"top\" width=\"33%\">");
        tablehtmlSB.append(table1Str);
        tablehtmlSB.append("</td>");
        tablehtmlSB.append("<td valign=\"top\" width=\"33%\">");
        tablehtmlSB.append(table2Str);
        tablehtmlSB.append("</td>");
        tablehtmlSB.append("<td valign=\"top\" width=\"34%\">");
        tablehtmlSB.append(table3Str);
        tablehtmlSB.append("</td>");
        tablehtmlSB.append("</tr>");
        tablehtmlSB.append("<tr>");
        tablehtmlSB.append("<td colspan=3 width=\"100%\">");
        tablehtmlSB.append(tableLongStr);
        tablehtmlSB.append("</td>");
        tablehtmlSB.append("</tr>");
        tablehtmlSB.append("</table>");

        htmlStringBuilder.append(tablehtmlSB);

        htmlStringBuilder.append("</DIV></BODY></HTML>");

        // LOG.info("htmlStringBuilder:" + htmlStringBuilder);
        alertJEditorPane.setText(htmlStringBuilder.toString());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 15, 2, 2);

        JPanel alertJPanel = new JPanel();
        alertJPanel.setLayout(new GridBagLayout());

        alertJPanel.add(alertJEditorPane, gbc1);
        alertJPanel.setBackground(Color.WHITE);
        alertJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JScrollPane alertJScrollPane = new JScrollPane(alertJPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        alertJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return alertJScrollPane;
    }

    private JScrollPane getPalComponent() {

        LogEntryColumn logEntryColumn = LogEntryColumn.PALDATA;

        LogEntryModel logEntryModel = getAlertLogEntryModel();

        int columnIndex = logEntryModel.getLogEntryColumnIndex(logEntryColumn);

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane palJEditorPane = new JEditorPane();
        palJEditorPane.setEditable(false);
        palJEditorPane.setContentType("text/html");
        palJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>" + logEntryColumn.getDisplayName() + "</H3>");

        if (columnIndex != -1) {

            List<String> logEntryValueList = getLogEntryValueList();

            String palData = logEntryValueList.get(columnIndex);

            if ((palData != null) && (!"".equals(palData)) && (!"NA".equals(palData))) {

                String[] palStatArray = palData.split(";", 0);

                List<AlertLogEntryPanelTableData> timeDataList = new ArrayList<AlertLogEntryPanelTableData>();
                List<AlertLogEntryPanelTableData> cpuDataList = new ArrayList<AlertLogEntryPanelTableData>();
                List<AlertLogEntryPanelTableData> countDataList = new ArrayList<AlertLogEntryPanelTableData>();

                for (String palStat : palStatArray) {

                    String[] palStatNameValue = palStat.split("=", 2);

                    String nameColumn = palStatNameValue[0];
                    String valueColumn = null;
                    boolean isHref = false;

                    if (palStatNameValue.length > 1) {
                        valueColumn = palStatNameValue[1];
                    }

                    AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(
                            nameColumn, valueColumn, isHref);

                    if (nameColumn.contains("CPU")) {
                        cpuDataList.add(alertLogEntryPanelTableData);
                    } else if ((nameColumn.contains("Time")) || (nameColumn.contains("Elapsed"))) {
                        timeDataList.add(alertLogEntryPanelTableData);
                    } else {
                        countDataList.add(alertLogEntryPanelTableData);
                    }
                }

                String timeTableStr = getTableHtmlStr("Time", timeDataList, "border=\"1\"");
                String cpuTableStr = getTableHtmlStr("CPU", cpuDataList, "border=\"1\"");
                String countTableStr = getTableHtmlStr("Count", countDataList, "border=\"1\"");

                StringBuilder tablehtmlSB = new StringBuilder();

                tablehtmlSB.append("<table border=\"0\">");
                tablehtmlSB.append("<tr>");
                tablehtmlSB.append("<td valign=\"top\">");
                tablehtmlSB.append(timeTableStr);
                tablehtmlSB.append("</td>");
                tablehtmlSB.append("<td valign=\"top\">");
                tablehtmlSB.append(cpuTableStr);
                tablehtmlSB.append("</td>");
                tablehtmlSB.append("<td valign=\"top\">");
                tablehtmlSB.append(countTableStr);
                tablehtmlSB.append("</td>");
                tablehtmlSB.append("</tr>");
                tablehtmlSB.append("</table>");

                htmlStringBuilder.append(tablehtmlSB);

                palJEditorPane.setText(htmlStringBuilder.toString());
            }
        }

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 15, 2, 2);

        JPanel palJPanel = new JPanel();
        palJPanel.setLayout(new GridBagLayout());

        palJPanel.add(palJEditorPane, gbc1);
        palJPanel.setBackground(Color.WHITE);
        palJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JScrollPane palScrollPane = new JScrollPane(palJPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        palScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return palScrollPane;
    }

    private JComponent getTraceListComponent() {

        LogEntryColumn logEntryColumn = LogEntryColumn.TRACELIST;

        LogEntryModel logEntryModel = getAlertLogEntryModel();

        int columnIndex = logEntryModel.getLogEntryColumnIndex(logEntryColumn);

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane traceListJEditorPane = new JEditorPane();
        traceListJEditorPane.setEditable(false);
        traceListJEditorPane.setContentType("text/html");
        traceListJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>" + logEntryColumn.getDisplayName() + "</H3>");

        if (columnIndex != -1) {

            List<String> logEntryValueList = getLogEntryValueList();

            String traceListData = logEntryValueList.get(columnIndex);

            if ((traceListData != null) && (!"".equals(traceListData)) && (!"NA".equals(traceListData))) {
                String[] traceListArray = traceListData.split(";", 0);

                List<AlertLogEntryPanelTableData> traceListDataList = new ArrayList<AlertLogEntryPanelTableData>();

                for (String traceList : traceListArray) {

                    String[] traceListNameValue = traceList.split(":", 2);

                    String nameColumn = traceListNameValue[0];
                    String valueColumn = null;
                    boolean isHref = false;

                    if (traceListNameValue.length > 1) {
                        valueColumn = traceListNameValue[1];
                    }

                    AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(
                            nameColumn, valueColumn, isHref);

                    traceListDataList.add(alertLogEntryPanelTableData);
                }

                String traceListTableStr = getTableHtmlStr("Trace List", traceListDataList, "border=\"1\"");

                htmlStringBuilder.append(traceListTableStr);

                traceListJEditorPane.setText(htmlStringBuilder.toString());
            }
        }

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 15, 2, 2);

        JPanel traceListJPanel = new JPanel();
        traceListJPanel.setLayout(new GridBagLayout());

        traceListJPanel.add(traceListJEditorPane, gbc1);
        traceListJPanel.setBackground(Color.WHITE);
        traceListJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JScrollPane traceListJScrollPane = new JScrollPane(traceListJPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        traceListJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return traceListJScrollPane;
    }

    private JComponent getPRStacktraceComponent() {

        LogEntryColumn logEntryColumn = LogEntryColumn.PRSTACKTRACE;

        LogEntryModel logEntryModel = getAlertLogEntryModel();

        int columnIndex = logEntryModel.getLogEntryColumnIndex(logEntryColumn);

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane prStacktraceJEditorPane = new JEditorPane();
        prStacktraceJEditorPane.setEditable(false);
        prStacktraceJEditorPane.setContentType("text/html");
        prStacktraceJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>" + logEntryColumn.getDisplayName() + "</H3>");

        if (columnIndex != -1) {

            List<String> logEntryValueList = getLogEntryValueList();

            String prStacktraceData = logEntryValueList.get(columnIndex);

            if ((prStacktraceData != null) && (!"".equals(prStacktraceData)) && (!"NA".equals(prStacktraceData))) {
                String[] traceListArray = prStacktraceData.split(";", 0);

                List<AlertLogEntryPanelTableData> prStacktraceDataList = new ArrayList<AlertLogEntryPanelTableData>();

                for (String traceList : traceListArray) {

                    AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(traceList,
                            null, false);

                    prStacktraceDataList.add(alertLogEntryPanelTableData);

                }

                String prStacktraceTableStr = getTableHtmlStr("PR Stacktrace", prStacktraceDataList, "border=\"1\"");

                htmlStringBuilder.append(prStacktraceTableStr);

                prStacktraceJEditorPane.setText(htmlStringBuilder.toString());
            }
        }

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 15, 2, 2);

        JPanel prStackTraceJPanel = new JPanel();
        prStackTraceJPanel.setLayout(new GridBagLayout());

        prStackTraceJPanel.add(prStacktraceJEditorPane, gbc1);
        prStackTraceJPanel.setBackground(Color.WHITE);
        prStackTraceJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JScrollPane prStacktraceJScrollPane = new JScrollPane(prStackTraceJPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        prStacktraceJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return prStacktraceJScrollPane;
    }

    private JComponent getParameterPageComponent() {

        LogEntryColumn logEntryColumn = LogEntryColumn.PARAMETERPAGEDATA;

        LogEntryModel logEntryModel = getAlertLogEntryModel();

        int columnIndex = logEntryModel.getLogEntryColumnIndex(logEntryColumn);

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
        htmlStyleSheet.addStyleSheet(styleSheet);

        JEditorPane parameterPageJEditorPane = new JEditorPane();
        parameterPageJEditorPane.setEditable(false);
        parameterPageJEditorPane.setContentType("text/html");
        parameterPageJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

        StringBuilder htmlStringBuilder = new StringBuilder();

        htmlStringBuilder.append("<H3 align='center'>" + logEntryColumn.getDisplayName() + "</H3>");

        if (columnIndex != -1) {

            List<String> logEntryValueList = getLogEntryValueList();

            String parameterPageData = logEntryValueList.get(columnIndex);

            if ((parameterPageData != null) && (!"".equals(parameterPageData)) && (!"NA".equals(parameterPageData))) {

                // sanitise the data
                parameterPageData = parameterPageData.replaceAll(";gt;", ">");
                parameterPageData = parameterPageData.replaceAll(";lt;", "<");

                String[] parameterPageArray = parameterPageData.split(";", 0);

                List<AlertLogEntryPanelTableData> parameterPageDataList = new ArrayList<AlertLogEntryPanelTableData>();

                for (String palStat : parameterPageArray) {

                    String[] parameterPageNameValue = palStat.split("=", 2);

                    String nameColumn = parameterPageNameValue[0];
                    String valueColumn = null;
                    boolean isHref = false;

                    if (parameterPageNameValue.length > 1) {
                        valueColumn = StringEscapeUtils.escapeXml11(parameterPageNameValue[1]);
                    }

                    AlertLogEntryPanelTableData alertLogEntryPanelTableData = new AlertLogEntryPanelTableData(
                            nameColumn, valueColumn, isHref);

                    parameterPageDataList.add(alertLogEntryPanelTableData);

                }

                String parameterPageTableStr = getTableHtmlStr("Time", parameterPageDataList, "border=\"1\"");

                htmlStringBuilder.append(parameterPageTableStr);

                parameterPageJEditorPane.setText(htmlStringBuilder.toString());

            }
        }

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 15, 2, 2);

        JPanel parameterPageJPanel = new JPanel();
        parameterPageJPanel.setLayout(new GridBagLayout());

        parameterPageJPanel.add(parameterPageJEditorPane, gbc1);
        parameterPageJPanel.setBackground(Color.WHITE);
        parameterPageJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JScrollPane parameterPageJScrollPane = new JScrollPane(parameterPageJPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        parameterPageJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return parameterPageJScrollPane;
    }

    private JComponent getRawTextComponent() {

        String logEntryText = getLogEntryText();

        JPanel rawTextJPanel = new LogEntryPanel(logEntryText, charset);

        return rawTextJPanel;
    }

    private String getTableHtmlStr(String title, List<AlertLogEntryPanelTableData> tableDataList, String tableStyle) {

        StringBuilder tablehtmlSB = new StringBuilder();

        tablehtmlSB.append("<table " + tableStyle + ">");

        if (title != null) {
            tablehtmlSB.append("<tr><th class=\"tableHeaderCenter\" colspan=\"2\">" + title + "</th></tr>");
        }

        for (AlertLogEntryPanelTableData alertLogEntryPanelTableData : tableDataList) {

            tablehtmlSB.append("<tr>");

            String nameColumn = alertLogEntryPanelTableData.getNameColumn();
            String valueColumn = alertLogEntryPanelTableData.getValueColumn();
            boolean isHref = alertLogEntryPanelTableData.isHref();

            tablehtmlSB.append("<td class=\"nameColumn\">");
            tablehtmlSB.append(nameColumn);
            tablehtmlSB.append("</td>");

            if (valueColumn != null) {
                tablehtmlSB.append("<td class=\"valueColumn\">");

                if (isHref) {
                    tablehtmlSB.append("<a href=\"");
                    tablehtmlSB.append(valueColumn);
                    tablehtmlSB.append("\">");
                    tablehtmlSB.append(valueColumn);
                    tablehtmlSB.append("</a>");
                } else {
                    tablehtmlSB.append(valueColumn);
                }

                tablehtmlSB.append("</td>");
            }

            tablehtmlSB.append("</tr>");
        }

        tablehtmlSB.append("</table>");

        return tablehtmlSB.toString();
    }

}
