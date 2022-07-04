
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.MyColor;

public class ProcessingThreadLifecycleMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 5956163836141920446L;

    private String runId;

    private String threadName;

    private String event;

    private String partitionStatus;

    private List<String> partitions;

    public ProcessingThreadLifecycleMessage() {
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getPartitionStatus() {
        return partitionStatus;
    }

    @Override
    public List<String> getPartitions() {
        return partitions;
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
    public String getPreviousStatus() {
        return null;
    }

    @Override
    public String getIntention() {
        return null;
    }

    @Override
    public Color getForegroundColor() {
        return Color.BLACK;
    }

    @Override
    public Color getBackgroundColor() {
        return MyColor.LIGHTEST_LIGHT_GRAY;
    }

}
