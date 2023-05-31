
package com.pega.gcs.logviewer.hotfixscan;

import java.util.Objects;

import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class CompareHotfixEntryKey extends HotfixEntryKey {

    private static final long serialVersionUID = 4257980688787338281L;

    private int compareId;

    public CompareHotfixEntryKey(int compareId, int id, String hotfixId) {
        super(id, hotfixId);
        this.compareId = compareId;
    }

    public int getCompareId() {
        return compareId;
    }

    @Override
    public Integer getKey() {
        return getCompareId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(compareId);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CompareHotfixEntryKey)) {
            return false;
        }

        CompareHotfixEntryKey other = (CompareHotfixEntryKey) obj;

        return compareId == other.compareId;
    }

}
