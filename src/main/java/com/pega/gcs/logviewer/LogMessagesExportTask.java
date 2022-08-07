
package com.pega.gcs.logviewer;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogMessagesExportDialog.ParseOption;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class LogMessagesExportTask extends SwingWorker<List<LogEntryKey>, Integer> {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogMessagesExportTask.class);

    private LogTableModel logTableModel;

    private File exportFile;

    private Set<LogEntryKey> logEntryKeySet;

    private ParseOption parseOption;

    private String delimiter;

    public LogMessagesExportTask(LogTableModel logTableModel, File exportFile, Set<LogEntryKey> logEntryKeySet,
            ParseOption parseOption, String delimiter) {

        super();

        this.logTableModel = logTableModel;
        this.exportFile = exportFile;
        this.logEntryKeySet = logEntryKeySet;
        this.parseOption = parseOption;
        this.delimiter = delimiter;
    }

    @Override
    protected List<LogEntryKey> doInBackground() throws Exception {

        publish(0);

        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        Map<LogEntryColumn, Integer> logEntryColumnIndexMap = logEntryModel.getLogEntryColumnIndexMap();

        int timestampColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.TIMESTAMP);
        int messageColumnIndex = logEntryColumnIndexMap.get(LogEntryColumn.MESSAGE);

        List<LogEntryKey> splitFailedList = new ArrayList<>();

        try {

            switch (parseOption) {

            case DELIMITER:

                processDelimiter(timestampColumnIndex, messageColumnIndex, splitFailedList);

                break;

            case JSON:

                processJson(timestampColumnIndex, messageColumnIndex, splitFailedList);

                break;

            default:
                break;
            }

        } finally {
            LOG.info("LogMessagesExportTask - doInBackground finished");
        }

        return splitFailedList;
    }

    private void processDelimiter(int timestampColumnIndex, int messageColumnIndex, List<LogEntryKey> splitFailedList)
            throws Exception {

        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        Map<LogEntryKey, LogEntry> logEntryMap = logEntryModel.getLogEntryMap();

        int row = 0;
        int batchSize = 4194304; // 4096KB
        StringBuilder outputStrBatch = new StringBuilder();
        boolean append = false;
        Charset charset = logTableModel.getCharset();

        for (LogEntryKey logEntryKey : logEntryKeySet) {

            if (!isCancelled()) {

                LogEntry logEntry = logEntryMap.get(logEntryKey);

                ArrayList<String> logEntryValueList = logEntry.getLogEntryValueList();

                String timestamp = logEntryValueList.get(timestampColumnIndex);
                String message = logEntryValueList.get(messageColumnIndex);

                StringBuilder messageDataSB = new StringBuilder();

                boolean success = processDelimiterMessage(timestamp, message, messageDataSB);

                if (success) {

                    outputStrBatch.append(messageDataSB);
                    outputStrBatch.append(System.getProperty("line.separator"));

                    int accumulatedSize = outputStrBatch.length();

                    if (accumulatedSize > batchSize) {

                        FileUtils.writeStringToFile(exportFile, outputStrBatch.toString(), charset, append);

                        outputStrBatch = new StringBuilder();

                        if (!append) {
                            append = true;
                        }

                        publish(row);
                    }
                } else {
                    splitFailedList.add(logEntryKey);
                }

                row++;
            }
        }

        if (!isCancelled()) {

            int accumulatedSize = outputStrBatch.length();

            if (accumulatedSize > 0) {

                FileUtils.writeStringToFile(exportFile, outputStrBatch.toString(), charset, append);
            }

            publish(row);
        }
    }

    private boolean processDelimiterMessage(String timestamp, String message, StringBuilder messageDataSB) {

        boolean success = true;

        String[] messageDataArray = message.split(delimiter, 0);

        // also keeping unparsed entries, for scenarios where we just need to export the message column
        if (messageDataArray.length >= 1) {

            messageDataSB.append(timestamp);

            for (String messageData : messageDataArray) {
                messageDataSB.append("\t");
                messageDataSB.append(messageData);
            }

        } else {
            success = false;
        }

        return success;
    }

    private void processJson(int timestampColumnIndex, int messageColumnIndex, List<LogEntryKey> splitFailedList)
            throws Exception {

        LogEntryModel logEntryModel = logTableModel.getLogEntryModel();

        Map<LogEntryKey, LogEntry> logEntryMap = logEntryModel.getLogEntryMap();

        ObjectMapper objectMapper = new ObjectMapper();

        Set<String> headerSet = new LinkedHashSet<>();

        HashMap<LogEntryKey, JsonData> jsonDataMap = new HashMap<>();

        // collect the keys to identify all the headeers.
        for (LogEntryKey logEntryKey : logEntryKeySet) {

            if (!isCancelled()) {

                LogEntry logEntry = logEntryMap.get(logEntryKey);

                ArrayList<String> logEntryValueList = logEntry.getLogEntryValueList();

                String timestamp = logEntryValueList.get(timestampColumnIndex);
                String message = logEntryValueList.get(messageColumnIndex);

                JsonNode jsonNode = objectMapper.readTree(message);
                JsonData jsonData = processJsonMessage(timestamp, jsonNode);

                if (jsonData != null) {
                    String[] keyArray = jsonData.getKeyArray();

                    for (String key : keyArray) {
                        headerSet.add(key);
                    }

                    jsonDataMap.put(logEntryKey, jsonData);
                } else {
                    splitFailedList.add(logEntryKey);
                }
            }
        }

        int row = 0;
        int batchSize = 4194304; // 4096KB
        StringBuilder outputStrBatch = new StringBuilder();
        boolean append = false;
        Charset charset = logTableModel.getCharset();
        boolean first = true;

        // loop - 2: write the data
        // header
        for (String header : headerSet) {

            if (!first) {
                outputStrBatch.append("\t");
            }

            first = false;
            outputStrBatch.append(header);
        }

        outputStrBatch.append(System.getProperty("line.separator"));

        for (LogEntryKey logEntryKey : logEntryKeySet) {

            if (!isCancelled()) {

                StringBuilder messageDataSB = new StringBuilder();
                first = true;

                JsonData jsonData = jsonDataMap.get(logEntryKey);

                if (jsonData != null) {

                    for (String header : headerSet) {

                        String value = jsonData.getValue(header);

                        if (!first) {
                            messageDataSB.append("\t");
                        }

                        first = false;
                        messageDataSB.append(value);
                    }

                    outputStrBatch.append(messageDataSB);
                    outputStrBatch.append(System.getProperty("line.separator"));

                    int accumulatedSize = outputStrBatch.length();

                    if (accumulatedSize > batchSize) {

                        FileUtils.writeStringToFile(exportFile, outputStrBatch.toString(), charset, append);

                        outputStrBatch = new StringBuilder();

                        if (!append) {
                            append = true;
                        }

                        publish(row);
                    }
                }
            }

            row++;
        }

        if (!isCancelled()) {

            int accumulatedSize = outputStrBatch.length();

            if (accumulatedSize > 0) {

                FileUtils.writeStringToFile(exportFile, outputStrBatch.toString(), charset, append);
            }

            publish(row);
        }
    }

    private JsonData processJsonMessage(String timestamp, JsonNode jsonNode) {

        JsonData jsonData = null;
        String timestampKey = "Timestamp";

        Iterator<Map.Entry<String, JsonNode>> jsonNodeIt = jsonNode.fields();
        int jsonSize = jsonNode.size();

        if (jsonSize > 0) {

            String[] keyArray = new String[jsonSize + 1];
            String[] valueArray = new String[jsonSize + 1];
            int index = 0;

            keyArray[index] = timestampKey;
            valueArray[index] = timestamp;

            index++;

            while (jsonNodeIt.hasNext()) {

                Map.Entry<String, JsonNode> fieldEntry = jsonNodeIt.next();

                String key = fieldEntry.getKey();
                String value = fieldEntry.getValue().asText();

                keyArray[index] = key;
                valueArray[index] = value;

                index++;
            }

            jsonData = new JsonData(keyArray, valueArray);
        }

        return jsonData;
    }

    private class JsonData {

        private String[] keyArray;

        private String[] valueArray;

        private HashMap<String, Integer> keyIndexMap;

        private JsonData(String[] keyArray, String[] valueArray) {
            super();
            this.keyArray = keyArray;
            this.valueArray = valueArray;

            keyIndexMap = new HashMap<>();

            int index = 0;

            for (String key : keyArray) {
                keyIndexMap.put(key, index++);
            }
        }

        private String[] getKeyArray() {
            return keyArray;
        }

        private String getValue(String key) {

            String value = null;

            Integer index = keyIndexMap.get(key);

            if (index != null) {

                value = valueArray[index];
            }

            return value;
        }
    }
}
