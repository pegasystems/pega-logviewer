
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;

public class LifeCycleEventFileParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventFileParser.class);

    private LifeCycleEventTableModel lifeCycleEventTableModel;

    private LifeCycleEventMessageParser lifeCycleEventMessageParser;

    public LifeCycleEventFileParser(LifeCycleEventTableModel lifeCycleEventTableModel) {

        this.lifeCycleEventTableModel = lifeCycleEventTableModel;

        this.lifeCycleEventMessageParser = new LifeCycleEventMessageParser();
    }

    private LifeCycleEventTableModel getLifeCycleEventTableModel() {
        return lifeCycleEventTableModel;
    }

    private LifeCycleEventMessageParser getLifeCycleEventMessageParser() {
        return lifeCycleEventMessageParser;
    }

    public int processLifeCycleEventfile(File lifeCycleEventFile) {

        LOG.info("Start - process LifeCycleEvent file: " + lifeCycleEventFile);

        long before = System.currentTimeMillis();
        int rowCount = 0;
        int processedCount = 0;
        int errorCount = 0;

        try {

            if ((lifeCycleEventFile != null) && (lifeCycleEventFile.exists()) && (lifeCycleEventFile.canRead())) {

                ZipSecureFile.setMinInflateRatio(0.001D);

                try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(lifeCycleEventFile)) {

                    XSSFSheet xssfSheet = xssfWorkbook.getSheet("Data");

                    int eventDetailsColumnIndex = -1;
                    boolean headerRow = true;

                    for (Row row : xssfSheet) {

                        // get the 'Event Details' column index
                        if (headerRow) {
                            headerRow = false;
                            eventDetailsColumnIndex = getEventDetailsColumnIndex(row);
                        } else {
                            rowCount++;
                            Cell eventDetailsCell = row.getCell(eventDetailsColumnIndex);

                            String messageJson = eventDetailsCell.getStringCellValue();

                            LifeCycleEventMessageParser lifeCycleEventMessageParser;
                            lifeCycleEventMessageParser = getLifeCycleEventMessageParser();

                            LifeCycleEventMessage lifeCycleEventMessage;
                            lifeCycleEventMessage = lifeCycleEventMessageParser.getLifeCycleEventMessage(messageJson);

                            if (lifeCycleEventMessage != null) {

                                LifeCycleEventTableModel lifeCycleEventTableModel;
                                lifeCycleEventTableModel = getLifeCycleEventTableModel();

                                lifeCycleEventTableModel.addLifeCycleMessageEntry(lifeCycleEventMessage);

                                processedCount++;
                            } else {
                                LOG.error("Null LifeCycleEventMessage, ignoring entry");
                                errorCount++;
                            }

                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error opening excel file: " + lifeCycleEventFile, e);
                }

                parseFinal();

                Message.MessageType messageType = MessageType.INFO;

                StringBuilder messageB = new StringBuilder();
                messageB.append(lifeCycleEventFile.getAbsolutePath());
                messageB.append(". ");

                String text = "Processed " + processedCount + " events.";
                messageB.append(text);

                if (errorCount > 0) {
                    text = errorCount + " Error" + (errorCount > 1 ? "s" : "") + " while loading log file";

                    messageType = MessageType.ERROR;
                    messageB.append(text);

                }

                Message message = new Message(messageType, messageB.toString());
                lifeCycleEventTableModel.setMessage(message);

                LOG.debug("Calling fireTableDataChanged");
                // this should be last step.
                lifeCycleEventTableModel.fireTableDataChanged();
            }
        } finally {

            long diff = System.currentTimeMillis() - before;

            int secs = (int) Math.ceil((double) diff / 1E3);

            LOG.info("process LifeCycleEvent took " + secs + " secs.");
        }

        LOG.info("End - process LifeCycleEvent file: " + lifeCycleEventFile + " rowCount: " + rowCount
                + " processedCount: " + processedCount);

        return processedCount;
    }

    private int getEventDetailsColumnIndex(Row headerRow) {

        int index = -1;

        for (Cell cell : headerRow) {

            int columnIndex = cell.getColumnIndex();
            String columnName = cell.getRichStringCellValue().getString().trim();

            if ("Event details".equalsIgnoreCase(columnName)) {
                index = columnIndex;
                break;
            }
        }

        return index;

    }

    public void parseFinal() {

        // sort entries with timestamp.
        LifeCycleEventTableModel lifeCycleEventTableModel;
        lifeCycleEventTableModel = getLifeCycleEventTableModel();

        List<LifeCycleEventKey> lceKeyList = lifeCycleEventTableModel.getFtmEntryKeyList();

        Collections.sort(lceKeyList);

        // moved from LogEntryModel.addLogEntry()
        HashMap<LifeCycleEventKey, Integer> keyIndexMap = lifeCycleEventTableModel.getKeyIndexMap();

        if (keyIndexMap != null) {

            keyIndexMap.clear();

            for (int index = 0; index < lceKeyList.size(); index++) {

                LifeCycleEventKey key = lceKeyList.get(index);

                keyIndexMap.put(key, index);
            }
        }
    }
}
