
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.MyColor;

public class PartitionStatusTransitionMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 3445782811717287577L;

    private String runId;

    private String originator;

    private String reason;

    private String oldStatus;

    private String newStatus;

    private String[] partitionIds;

    public PartitionStatusTransitionMessage() {
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

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String[] getPartitionIds() {
        return partitionIds;
    }

    @Override
    public String getPartitionStatus() {
        return getNewStatus();
    }

    @Override
    public String getPreviousStatus() {
        return getOldStatus();
    }

    @Override
    public String getIntention() {
        return null;
    }

    @Override
    public List<String> getPartitions() {

        List<String> partitions = null;

        if (partitionIds != null) {
            partitions = Arrays.asList(partitionIds);
        }

        return partitions;
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
        return Color.BLACK;
    }

    @Override
    public Color getBackgroundColor() {
        return MyColor.LIGHTEST_LIGHT_GRAY;
    }

}
