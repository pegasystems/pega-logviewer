
package com.pega.gcs.logviewer.dataflow.lifecycleevent.message;

import java.awt.Color;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.MyColor;

public class IntentChangedMessage extends LifeCycleEventMessage {

    private static final long serialVersionUID = 3975577435239691873L;

    private String runId;

    private String reason;

    private String originator;

    private String intention;

    public IntentChangedMessage() {
    }

    @Override
    public String getRunId() {
        return runId;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public String getOriginator() {
        return originator;
    }

    @Override
    public String getIntention() {
        return intention;
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
        return Color.BLACK;
    }

    @Override
    public Color getBackgroundColor() {
        return MyColor.LIGHTEST_LIGHT_GRAY;
    }

}
