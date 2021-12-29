
package com.pega.gcs.logviewer.catalog.model;

public enum CriticalLevel {

    // @formatter:off
    // CHECKSTYLE:OFF
    CRITICAL    ("Critical"  , 100),
    IMPORTANT   ("Important" , 50 ),
    NONCRITICAL (""          , 0  );
    // CHECKSTYLE:ON
    // @formatter:on

    private final String displayName;

    private final int level;

    CriticalLevel(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public static CriticalLevel fromValue(int value) {
        switch (value) {
        case 0:
            return NONCRITICAL;
        case 50:
            return IMPORTANT;
        case 100:
            return CRITICAL;
        default:
            throw new IllegalStateException(value + " does not correspond to a known criticality level.");
        }
    }
}