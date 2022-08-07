
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

public class TaskRetryMessage extends LifeCycleEventMessage {

    private String runId;

    private String reason;

    private String exceptionMessage;

    private String originator;

    public TaskRetryMessage() {
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String getOriginator() {
        return originator;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public String getPartitionStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPreviousStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIntention() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getPartitions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getThreadName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getEvent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getForegroundColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getBackgroundColor() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

}
