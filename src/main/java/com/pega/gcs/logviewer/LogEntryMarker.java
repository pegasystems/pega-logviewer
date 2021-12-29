
package com.pega.gcs.logviewer;

import com.pega.gcs.fringecommon.guiutilities.markerbar.Marker;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class LogEntryMarker extends Marker<LogEntryKey> {

    private static final long serialVersionUID = 2320253040820607958L;

    private String logEntryTimeText;

    @SuppressWarnings("unused")
    private LogEntryMarker() {
        // for kryo
        super();
    }

    public LogEntryMarker(LogEntryKey logEntryKey, String logEntryTimeText, String text) {
        super(logEntryKey, text);

        this.logEntryTimeText = logEntryTimeText;
    }

    public String getLogEntryTimeText() {
        return logEntryTimeText;
    }

    @Override
    public String toString() {

        StringBuilder stringSb = new StringBuilder();

        stringSb.append("Line: ");
        stringSb.append(getKey());
        stringSb.append(" [");
        stringSb.append(getLogEntryTimeText());
        stringSb.append("] - ");
        stringSb.append(getText());

        return stringSb.toString();
    }
}
