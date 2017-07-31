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
import java.util.ArrayList;
import java.util.LinkedList;
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

import org.apache.commons.lang3.StringEscapeUtils;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryData;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogEntryPanel extends JPanel {

	private static final long serialVersionUID = 7722214923071969992L;

	private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogEntryPanel.class);

	private static int selectedIndex = 0;

	private AlertLogEntry alertLogEntry;

	// multiple usages hence extract it once
	private LogEntryData logEntryData;

	private ArrayList<String> logEntryColumnList;

	private JTabbedPane alertTabbedPane;

	private StyleSheet styleSheet;

	private List<String> tabKeyList;

	private List<String> longDataKeyList;

	public AlertLogEntryPanel(AlertLogEntry alertLogEntry, LogEntryModel logEntryModel) {
		super();

		this.alertLogEntry = alertLogEntry;
		this.logEntryData = alertLogEntry.getLogEntryData();

		this.logEntryColumnList = logEntryModel.getLogEntryColumnList();

		tabKeyList = new ArrayList<String>();
		tabKeyList.add(LogEntryColumn.PALDATA.getColumnId());
		tabKeyList.add(LogEntryColumn.TRACELIST.getColumnId());
		tabKeyList.add(LogEntryColumn.PRSTACKTRACE.getColumnId());
		tabKeyList.add(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());

		longDataKeyList = new ArrayList<String>();
		longDataKeyList.add(LogEntryColumn.LOGGER.getColumnId());
		longDataKeyList.add(LogEntryColumn.STACK.getColumnId());
		longDataKeyList.add(LogEntryColumn.LASTINPUT.getColumnId());
		longDataKeyList.add(LogEntryColumn.FIRSTACTIVITY.getColumnId());
		longDataKeyList.add(LogEntryColumn.LASTSTEP.getColumnId());
		longDataKeyList.add(LogEntryColumn.MESSAGE.getColumnId());

		styleSheet = FileUtilities.getStyleSheet(this.getClass(), "styles.css");

		setLayout(new BorderLayout());

		JTabbedPane alertTabbedPane = getAlertTabbedPane();
		add(alertTabbedPane, BorderLayout.CENTER);

		JComponent alertJComponent = getAlertJComponent();
		JComponent palJComponent = getPALJComponent();
		JComponent traceListJComponent = getTraceListJComponent();
		JComponent prStacktraceJComponent = getPRStacktraceJComponent();
		JComponent parameterPageJComponent = getParameterPageJComponent();
		JComponent rawTextJComponent = getRawTextJComponent();

		String tabText = "Alert Detail";
		JLabel tabLabel = new JLabel(tabText);
		Font labelFont = tabLabel.getFont();
		Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
		Dimension dim = new Dimension(140, 26);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, alertJComponent);
		alertTabbedPane.setTabComponentAt(0, tabLabel);

		tabText = LogEntryColumn.PALDATA.getDisplayName();
		tabLabel = new JLabel(tabText);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, palJComponent);
		alertTabbedPane.setTabComponentAt(1, tabLabel);

		tabText = LogEntryColumn.TRACELIST.getDisplayName();
		tabLabel = new JLabel(tabText);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, traceListJComponent);
		alertTabbedPane.setTabComponentAt(2, tabLabel);

		tabText = LogEntryColumn.PRSTACKTRACE.getDisplayName();
		tabLabel = new JLabel(tabText);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, prStacktraceJComponent);
		alertTabbedPane.setTabComponentAt(3, tabLabel);

		tabText = LogEntryColumn.PARAMETERPAGEDATA.getDisplayName();
		tabLabel = new JLabel(tabText);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, parameterPageJComponent);
		alertTabbedPane.setTabComponentAt(4, tabLabel);

		tabText = "Raw Text";
		tabLabel = new JLabel(tabText);
		tabLabel.setFont(tabFont);
		// tabLabel.setSize(dim);
		tabLabel.setPreferredSize(dim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		alertTabbedPane.addTab(tabText, rawTextJComponent);
		alertTabbedPane.setTabComponentAt(5, tabLabel);

		alertTabbedPane.setSelectedIndex(selectedIndex);

		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				JTabbedPane alertTabbedPane = getAlertTabbedPane();
				setSelectedIndex(alertTabbedPane.getSelectedIndex());

			}
		};

		alertTabbedPane.addChangeListener(changeListener);
	}

	protected static int getSelectedIndex() {
		return selectedIndex;
	}

	protected static void setSelectedIndex(int aSelectedIndex) {
		selectedIndex = aSelectedIndex;
	}

	protected JTabbedPane getAlertTabbedPane() {

		if (alertTabbedPane == null) {
			alertTabbedPane = new JTabbedPane();
		}

		return alertTabbedPane;
	}

	private JComponent getAlertJComponent() {

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
		htmlStyleSheet.addStyleSheet(styleSheet);

		JEditorPane alertJEditorPane = new JEditorPane();
		alertJEditorPane.setEditable(false);
		alertJEditorPane.setContentType("text/html");
		alertJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);
		alertJEditorPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {

				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {

					if (Desktop.isDesktopSupported()) {

						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.browse(e.getURL().toURI());
						} catch (Exception e1) {
							LOG.error("Error opening url: " + e.getURL(), e1);
						}
					}
				}
			}
		});

		StringBuffer htmlStringBuffer = new StringBuffer();

		htmlStringBuffer.append("<HTML><HEAD/><BODY><DIV>");

		htmlStringBuffer.append("<H3 align='center'>Alert Detail</H3>");

		List<TableData> dataList = new LinkedList<TableData>();
		List<TableData> longDataList = new LinkedList<TableData>();

		List<String> logEntryValueList = logEntryData.getLogEntryValueList();

		for (int index = 0; index < logEntryColumnList.size(); index++) {

			String name = logEntryColumnList.get(index);

			if (!tabKeyList.contains(name)) {

				LogEntryColumn logEntryColumn = LogEntryColumn.getTableColumnById(name);

				String nameColumn = logEntryColumn.getDisplayName();
				String valueColumn = logEntryValueList.get(index);
				boolean isHREF = false;

				if ((logEntryColumn != null) && (logEntryColumn.equals(LogEntryColumn.MESSAGEID))) {

					StringBuffer sb = new StringBuffer();
					sb.append("<b>");
					sb.append(valueColumn);
					sb.append("</b>");
					valueColumn = sb.toString();
				}

				TableData tableData = new TableData(nameColumn, valueColumn, isHREF);

				if (longDataKeyList.contains(name)) {
					longDataList.add(tableData);
				} else {
					dataList.add(tableData);
				}

			}
		}

		AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();
		Map<Integer, AlertMessage> alertMessageMap = alertMessageListProvider.getAlertMessageMap();

		Integer alertId = alertLogEntry.getAlertId();

		AlertMessage alertMessage = alertMessageMap.get(alertId);

		if (alertMessage != null) {

			String nameColumn = null;
			String valueColumn = null;
			boolean isHREF = false;
			TableData tableData = null;

			nameColumn = "Category";
			StringBuffer valueSB = new StringBuffer();
			valueSB.append(alertMessage.getCategory());
			valueSB.append("/");
			valueSB.append(alertMessage.getSubcategory());
			valueColumn = valueSB.toString();
			isHREF = false;

			tableData = new TableData(nameColumn, valueColumn, isHREF);
			longDataList.add(tableData);

			nameColumn = "Title";
			valueColumn = alertMessage.getTitle();
			isHREF = false;

			tableData = new TableData(nameColumn, valueColumn, isHREF);
			longDataList.add(tableData);

			nameColumn = "PDN";
			valueColumn = alertMessage.getPDNURL();
			isHREF = true;

			tableData = new TableData(nameColumn, valueColumn, isHREF);
			longDataList.add(tableData);

			nameColumn = "Description";
			valueColumn = alertMessage.getDescription();
			isHREF = false;

			tableData = new TableData(nameColumn, valueColumn, isHREF);
			longDataList.add(tableData);

		}

		int middleIndex = (int) Math.ceil((dataList.size() / 3d));

		String defStyle = "width=\"100%\" border=\"1\"";
		List<TableData> subDataList = dataList.subList(0, middleIndex);
		int size = subDataList.size();
		String table1Str = getTableHTMLStr(null, subDataList, defStyle);

		subDataList = dataList.subList(middleIndex, (middleIndex + size));
		String table2Str = getTableHTMLStr(null, subDataList, defStyle);

		subDataList = dataList.subList(middleIndex + size, dataList.size());
		int subSize = subDataList.size();

		for (int i = subSize; i < size; i++) {

			// empty strings will create empty rows
			String nameColumn = "";
			String valueColumn = "";
			boolean isHREF = false;

			TableData tableData = new TableData(nameColumn, valueColumn, isHREF);

			subDataList.add(tableData);
		}

		String table3Str = getTableHTMLStr(null, subDataList, defStyle);

		String tableLongStr = getTableHTMLStr(null, longDataList, defStyle);

		StringBuffer tablehtmlSB = new StringBuffer();

		tablehtmlSB.append("<table border=\"0\" width=\"860px\">");
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

		htmlStringBuffer.append(tablehtmlSB);

		htmlStringBuffer.append("</DIV></BODY></HTML>");

		// LOG.info("htmlStringBuffer:" + htmlStringBuffer);
		alertJEditorPane.setText(htmlStringBuffer.toString());

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

	private JComponent getPALJComponent() {

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
		htmlStyleSheet.addStyleSheet(styleSheet);

		JEditorPane palJEditorPane = new JEditorPane();
		palJEditorPane.setEditable(false);
		palJEditorPane.setContentType("text/html");
		palJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

		StringBuffer htmlStringBuffer = new StringBuffer();

		htmlStringBuffer.append("<H3 align='center'>" + LogEntryColumn.PALDATA.getDisplayName() + "</H3>");

		int columnIndex = logEntryColumnList.indexOf(LogEntryColumn.PALDATA.getColumnId());

		if (columnIndex != -1) {

			List<String> logEntryValueList = logEntryData.getLogEntryValueList();

			String palData = logEntryValueList.get(columnIndex);

			if ((palData != null) && (!"".equals(palData)) && (!"NA".equals(palData))) {
				
				String[] palStatArray = palData.split(";");

				List<TableData> timeDataList = new LinkedList<TableData>();
				List<TableData> cpuDataList = new LinkedList<TableData>();
				List<TableData> countDataList = new LinkedList<TableData>();

				for (String palStat : palStatArray) {

					String[] palStatNameValue = palStat.split("=", 2);

					String nameColumn = palStatNameValue[0];
					String valueColumn = null;
					boolean isHREF = false;

					if (palStatNameValue.length > 1) {
						valueColumn = palStatNameValue[1];
					}

					TableData tableData = new TableData(nameColumn, valueColumn, isHREF);

					if (nameColumn.contains("CPU")) {
						cpuDataList.add(tableData);
					} else if ((nameColumn.contains("Time")) || (nameColumn.contains("Elapsed"))) {
						timeDataList.add(tableData);
					} else {
						countDataList.add(tableData);
					}
				}

				String timeTableStr = getTableHTMLStr("Time", timeDataList, "border=\"1\"");
				String cpuTableStr = getTableHTMLStr("CPU", cpuDataList, "border=\"1\"");
				String countTableStr = getTableHTMLStr("Count", countDataList, "border=\"1\"");

				StringBuffer tablehtmlSB = new StringBuffer();

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

				htmlStringBuffer.append(tablehtmlSB);

				palJEditorPane.setText(htmlStringBuffer.toString());
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

		JScrollPane palJScrollPane = new JScrollPane(palJPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		palJScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		return palJScrollPane;
	}

	private JComponent getTraceListJComponent() {

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
		htmlStyleSheet.addStyleSheet(styleSheet);

		JEditorPane traceListJEditorPane = new JEditorPane();
		traceListJEditorPane.setEditable(false);
		traceListJEditorPane.setContentType("text/html");
		traceListJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

		StringBuffer htmlStringBuffer = new StringBuffer();

		htmlStringBuffer.append("<H3 align='center'>" + LogEntryColumn.TRACELIST.getDisplayName() + "</H3>");

		int columnIndex = logEntryColumnList.indexOf(LogEntryColumn.TRACELIST.getColumnId());

		if (columnIndex != -1) {

			List<String> logEntryValueList = logEntryData.getLogEntryValueList();

			String traceListData = logEntryValueList.get(columnIndex);

			if ((traceListData != null) && (!"".equals(traceListData)) && (!"NA".equals(traceListData))) {
				String[] traceListArray = traceListData.split(";");

				List<TableData> traceListDataList = new LinkedList<TableData>();

				for (String traceList : traceListArray) {

					String[] traceListNameValue = traceList.split(":", 2);

					String nameColumn = traceListNameValue[0];
					String valueColumn = null;
					boolean isHREF = false;

					if (traceListNameValue.length > 1) {
						valueColumn = traceListNameValue[1];
					}

					TableData tableData = new TableData(nameColumn, valueColumn, isHREF);

					traceListDataList.add(tableData);
				}

				String traceListTableStr = getTableHTMLStr("Trace List", traceListDataList, "border=\"1\"");

				htmlStringBuffer.append(traceListTableStr);

				traceListJEditorPane.setText(htmlStringBuffer.toString());
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

	private JComponent getPRStacktraceJComponent() {

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
		htmlStyleSheet.addStyleSheet(styleSheet);

		JEditorPane prStacktraceJEditorPane = new JEditorPane();
		prStacktraceJEditorPane.setEditable(false);
		prStacktraceJEditorPane.setContentType("text/html");
		prStacktraceJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

		StringBuffer htmlStringBuffer = new StringBuffer();

		htmlStringBuffer.append("<H3 align='center'>" + LogEntryColumn.PRSTACKTRACE.getDisplayName() + "</H3>");

		int columnIndex = logEntryColumnList.indexOf(LogEntryColumn.PRSTACKTRACE.getColumnId());

		if (columnIndex != -1) {

			List<String> logEntryValueList = logEntryData.getLogEntryValueList();

			String prStacktraceData = logEntryValueList.get(columnIndex);

			if ((prStacktraceData != null) && (!"".equals(prStacktraceData)) && (!"NA".equals(prStacktraceData))) {
				String[] traceListArray = prStacktraceData.split(";");

				List<TableData> prStacktraceDataList = new LinkedList<TableData>();

				for (String traceList : traceListArray) {

					TableData tableData = new TableData(traceList, null, false);

					prStacktraceDataList.add(tableData);

				}

				String prStacktraceTableStr = getTableHTMLStr("PR Stacktrace", prStacktraceDataList, "border=\"1\"");

				htmlStringBuffer.append(prStacktraceTableStr);

				prStacktraceJEditorPane.setText(htmlStringBuffer.toString());
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

	private JComponent getParameterPageJComponent() {

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet htmlStyleSheet = htmlEditorKit.getStyleSheet();
		htmlStyleSheet.addStyleSheet(styleSheet);

		JEditorPane parameterPageJEditorPane = new JEditorPane();
		parameterPageJEditorPane.setEditable(false);
		parameterPageJEditorPane.setContentType("text/html");
		parameterPageJEditorPane.setEditorKitForContentType("text/html", htmlEditorKit);

		StringBuffer htmlStringBuffer = new StringBuffer();

		htmlStringBuffer.append("<H3 align='center'>" + LogEntryColumn.PARAMETERPAGEDATA.getDisplayName() + "</H3>");

		int columnIndex = logEntryColumnList.indexOf(LogEntryColumn.PARAMETERPAGEDATA.getColumnId());

		if (columnIndex != -1) {

			List<String> logEntryValueList = logEntryData.getLogEntryValueList();

			String parameterPageData = logEntryValueList.get(columnIndex);

			if ((parameterPageData != null) && (!"".equals(parameterPageData)) && (!"NA".equals(parameterPageData))) {

				// sanitise the data
				parameterPageData = parameterPageData.replaceAll(";gt;", ">");
				parameterPageData = parameterPageData.replaceAll(";lt;", "<");

				String[] parameterPageArray = parameterPageData.split(";");

				List<TableData> parameterPageDataList = new LinkedList<TableData>();

				for (String palStat : parameterPageArray) {

					String[] parameterPageNameValue = palStat.split("=", 2);

					String nameColumn = parameterPageNameValue[0];
					String valueColumn = null;
					boolean isHREF = false;

					if (parameterPageNameValue.length > 1) {
						valueColumn = StringEscapeUtils.escapeXml11(parameterPageNameValue[1]);
					}

					TableData tableData = new TableData(nameColumn, valueColumn, isHREF);

					parameterPageDataList.add(tableData);

				}

				String parameterPageTableStr = getTableHTMLStr("Time", parameterPageDataList, "border=\"1\"");

				htmlStringBuffer.append(parameterPageTableStr);

				parameterPageJEditorPane.setText(htmlStringBuffer.toString());

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

	private JComponent getRawTextJComponent() {

		JPanel rawTextJPanel = new LogEntryPanel(logEntryData.getLogEntryText());

		return rawTextJPanel;
	}

	private String getTableHTMLStr(String title, List<TableData> tableDataList, String tableStyle) {

		StringBuffer tablehtmlSB = new StringBuffer();

		tablehtmlSB.append("<table " + tableStyle + ">");

		if (title != null) {
			tablehtmlSB.append("<tr><th class=\"tableHeaderCenter\" colspan=\"2\">" + title + "</th></tr>");
		}

		for (TableData tableData : tableDataList) {

			tablehtmlSB.append("<tr>");

			String nameColumn = tableData.getNameColumn();
			String valueColumn = tableData.getValueColumn();
			boolean isHREF = tableData.isHREF();

			tablehtmlSB.append("<td class=\"nameColumn\">");
			tablehtmlSB.append(nameColumn);
			tablehtmlSB.append("</td>");

			if (valueColumn != null) {
				tablehtmlSB.append("<td class=\"valueColumn\">");

				if (isHREF) {
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

	private class TableData {

		private String nameColumn;

		private String valueColumn;

		private boolean isHREF;

		public TableData(String nameColumn, String valueColumn, boolean isHREF) {
			super();
			this.nameColumn = nameColumn;
			this.valueColumn = valueColumn;
			this.isHREF = isHREF;
		}

		public String getNameColumn() {
			return nameColumn;
		}

		public String getValueColumn() {
			return valueColumn;
		}

		public boolean isHREF() {
			return isHREF;
		}

	}
}
