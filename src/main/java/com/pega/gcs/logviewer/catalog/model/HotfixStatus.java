
package com.pega.gcs.logviewer.catalog.model;

public enum HotfixStatus {

    // @formatter:off
    // CHECKSTYLE:OFF
    INSTALLED           ("Installed"           ),
    PARTIALLY_INSTALLED ("Partially Installed" ),
    SUPERSEEDED         ("Superseded"          ),
    NOT_INSTALLED       ("Not Installed"       ),
    CRITICAL_MISSING    ("Critical Missing"    ),
    COMMITTED           ("Committed"           );
    // CHECKSTYLE:ON
    // @formatter:on

    private final String displayString;

    private HotfixStatus(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }

    @Override
    public String toString() {
        return getDisplayString();
    }

    public static HotfixStatus fromValue(String hotfixStatusString) {

        HotfixStatus hotfixStatus = null;

        for (HotfixStatus hs : values()) {

            if (hs.getDisplayString().equalsIgnoreCase(hotfixStatusString)) {
                hotfixStatus = hs;
                break;
            }
        }

        return hotfixStatus;
    }
}
