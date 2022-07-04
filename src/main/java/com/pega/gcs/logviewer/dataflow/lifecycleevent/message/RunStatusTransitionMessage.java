
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.MyColor;

public class RunStatusTransitionMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 4744571104923402244L;

    private String runId;

    private String previousStatus;

    private String newStatus;

    private String originator;

    private String reason;

    public RunStatusTransitionMessage() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String getPreviousStatus() {
        return previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
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
        return getNewStatus();
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
        return Color.BLACK;
    }

    @Override
    public Color getBackgroundColor() {
        return MyColor.LIGHTEST_LIGHT_GRAY;
    }

}
