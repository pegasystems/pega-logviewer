
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.io.Serializable;
import java.util.Objects;

public class LifeCycleEventKey implements Comparable<LifeCycleEventKey>, Serializable {

    private static final long serialVersionUID = 5897127181966447984L;

    private long timestamp;

    private String messageId;

    public LifeCycleEventKey(long timestamp, String messageId) {
        super();
        this.timestamp = timestamp;
        this.messageId = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LifeCycleEventKey)) {
            return false;
        }
        LifeCycleEventKey other = (LifeCycleEventKey) obj;
        return Objects.equals(messageId, other.messageId) && timestamp == other.timestamp;
    }

    @Override
    public int compareTo(LifeCycleEventKey other) {

        int compare = 0;

        long thisTimestamp = getTimestamp();
        long otherTimestamp = other.getTimestamp();

        compare = Long.compare(thisTimestamp, otherTimestamp);

        if (compare == 0) {

            String thisMessageId = getMessageId();
            String otherMessageId = other.getMessageId();

            compare = thisMessageId.compareTo(otherMessageId);
        }

        return compare;
    }

}
