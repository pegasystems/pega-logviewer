
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pega.gcs.logviewer.LogViewerUtil;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public abstract class LifeCycleEventMessage implements Serializable {

    private static final long serialVersionUID = 840279671030822460L;

    private String type;

    private String senderNodeId;

    private long timestamp;

    private String messageId;

    public abstract String getRunId();

    public abstract String getOriginator();

    public abstract String getReason();

    public abstract String getPartitionStatus();

    public abstract String getPreviousStatus();

    public abstract String getIntention();

    public abstract List<String> getPartitions();

    public abstract String getThreadName();

    public abstract String getEvent();

    public abstract Color getForegroundColor();

    public abstract Color getBackgroundColor();

    @JsonIgnore
    private boolean searchFound;

    public LifeCycleEventMessage() {
        super();
    }

    public String getType() {
        return type;
    }

    public String getSenderNodeId() {
        return senderNodeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public boolean isSearchFound() {
        return searchFound;
    }

    public void setSearchFound(boolean searchFound) {
        this.searchFound = searchFound;
    }

    public String getColumnValueForLifeCycleEventColumn(LogEntryColumn lifeCycleEventColumn,
            DateTimeFormatter displayDateTimeFormatter, ZoneId displayZoneId) {

        String value = null;

        if (lifeCycleEventColumn.equals(LogEntryColumn.MESSAGEID)) {
            value = getMessageId();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.TIMESTAMP)) {
            value = LogViewerUtil.getFormattedTimeStr(getTimestamp(), displayDateTimeFormatter, displayZoneId);
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.EVENT_TYPE)) {
            value = getType();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.SENDER_NODE_ID)) {
            value = getSenderNodeId();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.RUN_ID)) {
            value = getRunId();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.ORIGINATOR)) {
            value = getOriginator();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.REASON)) {
            value = getReason();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.PARTITION_STATUS)) {
            value = getPartitionStatus();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.PREVIOUS_STATUS)) {
            value = getPreviousStatus();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.INTENTION)) {
            value = getIntention();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.PARTITIONS)) {
            List<String> partitions = getPartitions();
            value = partitions != null ? partitions.toString() : null;
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.THREAD_NAME)) {
            value = getThreadName();
        } else if (lifeCycleEventColumn.equals(LogEntryColumn.EVENT)) {
            value = getEvent();
        }

        return value;
    }

}
