
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.guiutilities.MyColor;

public class RetryContextMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 3445782811717287577L;

    private String runId;

    private String dataFlow;

    private String stage;

    private String reason;

    @JsonProperty("partitions")
    private String[] partitionIds;

    public RetryContextMessage() {
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
        return reason;
    }

    public String getDataFlow() {
        return dataFlow;
    }

    public String getStage() {
        return stage;
    }

    public String[] getPartitionIds() {
        return partitionIds;
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
