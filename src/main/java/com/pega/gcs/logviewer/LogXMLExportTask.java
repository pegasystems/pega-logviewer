/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class LogXMLExportTask extends SwingWorker<Void, Integer> {

	private static final Log4j2Helper LOG = new Log4j2Helper(LogXMLExportTask.class);

	private LogTableModel logTableModel;

	private File xmlFile;

	public LogXMLExportTask(LogTableModel logTableModel, File xmlFile) {
		super();
		this.logTableModel = logTableModel;
		this.xmlFile = xmlFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception {

		int batchsize = 50;
		int row = 0;

		publish(row);

		OutputFormat format = new OutputFormat();
		format.setIndentSize(2);
		format.setNewlines(true);
		// format.setTrimText(true);
		format.setPadText(true);

		LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

		String logEventName = logEntryModel.getTypeName();
		String encoding = logTableModel.getCharset();

		StringBuffer xmlSB = new StringBuffer();

		StringBuffer xmlHeaderSB = new StringBuffer();

		xmlHeaderSB.append("<?xml version=\"1.0\" ");
		xmlHeaderSB.append("encoding=\"");
		xmlHeaderSB.append(encoding);
		xmlHeaderSB.append("\"?>\n");

		String xmlHeader = xmlHeaderSB.toString();
		String xmlStylesheet = "<?xml-stylesheet type=\"text/xsl\" href=\"ConvertToExcel.xsl\"?>\n";

		xmlSB.append(xmlHeader);
		xmlSB.append(xmlStylesheet);
		xmlSB.append("<");
		xmlSB.append(logEventName);
		xmlSB.append("List>");

		String rootElemStart = xmlSB.toString();

		String rootElemEnd = "\n</" + logEventName + "List>";

		Element logEventElementTemplate = getLogEventElementTemplate(logEventName);

		Map<Integer, LogEntry> logEntryMap = logEntryModel.getLogEntryMap();

		List<Integer> logEntryKeyList = new LinkedList<Integer>(logEntryMap.keySet());

		Collections.sort(logEntryKeyList);

		try {

			StringBuffer outputStrBatch = new StringBuffer();

			FileUtils.writeStringToFile(xmlFile, rootElemStart, logTableModel.getCharset());

			for (Integer logEntryKey : logEntryKeyList) {

				if (!isCancelled()) {
					LogEntry logEntry = logEntryMap.get(logEntryKey);
					Element logEventElement = getLogEventElement(logEventElementTemplate, logEntry);

					String xmlStr = getElementsAsXML(logEventElement, format);
					outputStrBatch.append(xmlStr);

					if (row == batchsize) {

						FileUtils.writeStringToFile(xmlFile, outputStrBatch.toString(), logTableModel.getCharset(),
								true);

						outputStrBatch = new StringBuffer();
					}

					row++;
					publish(row);
				}
			}

			if (!isCancelled()) {

				FileUtils.writeStringToFile(xmlFile, outputStrBatch.toString(), logTableModel.getCharset(), true);
				publish(row);
			}
		} catch (Exception e) {
			LOG.error("Error performing LogXMLExportTask", e);
		} finally {
			LOG.info("LogXMLExportTask - doInBackground last write");
			FileUtils.writeStringToFile(xmlFile, rootElemEnd, logTableModel.getCharset(), true);
			LOG.info("LogXMLExportTask - doInBackground finished");
		}

		return null;
	}

	private Element getLogEventElement(Element logEventElementTemplate, LogEntry logEntry) {

		Element logEventElement = logEventElementTemplate.createCopy();

		List<String> valueList = logEntry.getLogEntryValueList();

		@SuppressWarnings("unchecked")
		Iterator<Element> logEntryColumnElementIt = logEventElement.elementIterator();

		int columnIndex = 0;

		while (logEntryColumnElementIt.hasNext()) {
			Element logEntryColumnElement = logEntryColumnElementIt.next();
			String columnValue = valueList.get(columnIndex);

			if (columnValue != null) {
				logEntryColumnElement.setText(columnValue);
			}

			columnIndex++;
		}

		return logEventElement;
	}

	private Element getLogEventElementTemplate(String logEventName) {

		LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

		List<String> logEntryColumnList = logEntryModel.getLogEntryColumnList();

		DocumentFactory factory = DocumentFactory.getInstance();

		Element logEventElement = factory.createElement(logEventName);

		for (String logEntryColumn : logEntryColumnList) {
			Element logEntryColumnElement = factory.createElement(logEntryColumn);
			logEventElement.add(logEntryColumnElement);
		}

		return logEventElement;
	}

	public String getElementsAsXML(Element element, OutputFormat format) {
		StringWriter sw = new StringWriter();

		try {

			XMLWriter writer = new XMLWriter(sw, format);

			writer.write(element);
			writer.flush();

			// xmlStr = xmlStr + "\n";

		} catch (IOException e) {
			throw new RuntimeException("IOException while generating " + "textual representation: " + e.getMessage());
		}

		return sw.toString();
	}
}
