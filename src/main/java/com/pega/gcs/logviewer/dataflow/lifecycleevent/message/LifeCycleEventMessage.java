
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventColumn;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventTableModel;

public abstract class LifeCycleEventMessage implements Serializable {

    private static final long serialVersionUID = 840279671030822460L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventMessage.class);

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

    public String getColumnValueForLifeCycleEventColumn(LifeCycleEventColumn lifeCycleEventColumn) {

        String value = null;

        switch (lifeCycleEventColumn) {

        case MESSAGEID:
            value = getMessageId();
            break;
        case TIMESTAMP:
            value = getDisplayTimestamp();
            break;
        case EVENT_TYPE:
            value = getType();
            break;
        case SENDER_NODE_ID:
            value = getSenderNodeId();
            break;

        case RUN_ID:
            value = getRunId();
            break;
        case ORIGINATOR:
            value = getOriginator();
            break;
        case REASON:
            value = getReason();
            break;
        case PARTITION_STATUS:
            value = getPartitionStatus();
            break;
        case PREVIOUS_STATUS:
            value = getPreviousStatus();
            break;

        case INTENTION:
            value = getIntention();
            break;
        case PARTITIONS:
            List<String> partitions = getPartitions();
            value = partitions != null ? partitions.toString() : null;
            break;
        case THREAD_NAME:
            value = getThreadName();
            break;
        case EVENT:
            value = getEvent();
            break;

        default:
            break;

        }

        return value;
    }

    public String getDisplayTimestamp() {

        String timestampStr = "";

        try {
            DateFormat df = LifeCycleEventTableModel.getDisplayDateFormat();
            timestampStr = df.format(timestamp);
        } catch (Exception e) {
            LOG.error("Error formatting timestamp: " + timestamp, e);
        }

        return timestampStr;
    }
}
