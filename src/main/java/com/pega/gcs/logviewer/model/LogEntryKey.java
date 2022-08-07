
package com.pega.gcs.logviewer.model;

import java.io.Serializable;
import java.util.Objects;

public class LogEntryKey implements Comparable<LogEntryKey>, Serializable {

    private static final long serialVersionUID = 3160598968591124902L;

    private int lineNo;

    private long timestamp;

    // For Kryo
    @SuppressWarnings("unused")
    private LogEntryKey() {
    }

    public LogEntryKey(int lineNo, long timestamp) {
        super();
        this.lineNo = lineNo;
        this.timestamp = timestamp;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getLineNo() {
        return lineNo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // updating timestamp when timezone becomes known
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNo, timestamp);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LogEntryKey)) {
            return false;
        }

        LogEntryKey other = (LogEntryKey) obj;

        return lineNo == other.lineNo && timestamp == other.timestamp;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(lineNo);
        builder.append("[");
        builder.append(timestamp);
        builder.append("]");

        return builder.toString();
    }

    @Override
    public int compareTo(LogEntryKey other) {

        int compare;

        Long thisTimestamp = getTimestamp();
        Long otherTimestamp = other.getTimestamp();

        compare = thisTimestamp.compareTo(otherTimestamp);

        if (compare == 0) {

            Integer thisLineNo = getLineNo();
            Integer otherLineNo = other.getLineNo();

            compare = thisLineNo.compareTo(otherLineNo);
        }

        return compare;
    }

}
