
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

public class TaskAssignedMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 1183268238123079964L;

    private String runId;

    private String taskDescription;

    public TaskAssignedMessage() {
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String getOriginator() {
        return null;
    }

    @Override
    public String getReason() {
        return getTaskDescription();
    }

    @Override
    public String getPartitionStatus() {
        return null;
    }

    @Override
    public String getPreviousStatus() {
        return null;
    }

    @Override
    public String getIntention() {
        return null;
    }

    @Override
    public List<String> getPartitions() {
        return null;
    }

    @Override
    public String getThreadName() {
        return null;
    }

    @Override
    public String getEvent() {
        return null;
    }

    @Override
    public Color getForegroundColor() {
        return null;
    }

    @Override
    public Color getBackgroundColor() {
        return null;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

}
