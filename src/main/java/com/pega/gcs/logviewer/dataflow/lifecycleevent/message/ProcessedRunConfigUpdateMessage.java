
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

public class ProcessedRunConfigUpdateMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 4447440961526805236L;

    private String runId;

    public ProcessedRunConfigUpdateMessage() {
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
        return null;
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

}
