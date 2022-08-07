/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
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

    @Override
    protected Void doInBackground() throws Exception {

        int batchSize = 4194304; // 4096KB
        StringBuilder outputStrBatch = new StringBuilder();
        boolean append = false;
        Charset charset = logTableModel.getCharset();

        int row = 0;

        publish(row);

        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        String logEventName = logEntryModel.getTypeName();

        StringBuilder xmlSB = new StringBuilder();

        StringBuilder xmlHeaderSB = new StringBuilder();

        xmlHeaderSB.append("<?xml version=\"1.0\" ");
        xmlHeaderSB.append("encoding=\"");
        xmlHeaderSB.append(charset);
        xmlHeaderSB.append("\"?>\n");

        String xmlHeader = xmlHeaderSB.toString();
        String xmlStylesheet = "<?xml-stylesheet type=\"text/xsl\" href=\"ConvertToExcel.xsl\"?>\n";

        xmlSB.append(xmlHeader);
        xmlSB.append(xmlStylesheet);
        xmlSB.append("<");
        xmlSB.append(logEventName);
        xmlSB.append("List>");

        outputStrBatch.append(xmlSB.toString());

        String rootElemEnd = "\n</" + logEventName + "List>";

        Element logEventElementTemplate = getLogEventElementTemplate(logEventName);

        Map<LogEntryKey, LogEntry> logEntryMap = logEntryModel.getLogEntryMap();

        List<LogEntryKey> logEntryKeyList = new ArrayList<>(logEntryMap.keySet());

        Collections.sort(logEntryKeyList);

        try {

            // FileUtils.writeStringToFile(xmlFile, rootElemStart, logTableModel.getCharset());

            for (LogEntryKey logEntryKey : logEntryKeyList) {

                if (!isCancelled()) {

                    LogEntry logEntry = logEntryMap.get(logEntryKey);
                    Element logEventElement = getLogEventElement(logEventElementTemplate, logEntry);

                    String xmlStr = GeneralUtilities.getElementAsXML(logEventElement);
                    outputStrBatch.append(xmlStr);

                    int accumulatedSize = outputStrBatch.length();

                    if (accumulatedSize > batchSize) {

                        FileUtils.writeStringToFile(xmlFile, outputStrBatch.toString(), charset, append);

                        outputStrBatch = new StringBuilder();

                        if (!append) {
                            append = true;
                        }

                        publish(row);
                    }

                    row++;
                }
            }

            if (!isCancelled()) {

                int accumulatedSize = outputStrBatch.length();

                if (accumulatedSize > 0) {

                    FileUtils.writeStringToFile(xmlFile, outputStrBatch.toString(), charset, append);

                    if (!append) {
                        append = true;
                    }
                }

                publish(row);
            }

        } catch (Exception e) {
            LOG.error("Error performing LogXMLExportTask", e);
        } finally {
            LOG.info("LogXMLExportTask - doInBackground last write");
            FileUtils.writeStringToFile(xmlFile, rootElemEnd, charset, append);
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

        DocumentFactory factory = DocumentFactory.getInstance();

        Element logEventElement = factory.createElement(logEventName);

        for (LogEntryColumn logEntryColumn : logEntryModel.getLogEntryColumnList()) {
            Element logEntryColumnElement = factory.createElement(logEntryColumn.getColumnId());
            logEventElement.add(logEntryColumnElement);
        }

        return logEventElement;
    }
}
