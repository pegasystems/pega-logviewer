/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.view;

public enum HotfixScanViewMode {

    // @formatter:off
    // CHECKSTYLE:OFF
    SINGLE_TABLE        ( "Table"   ),
    COMPARE_TABLE       ( "Compare" );
    // CHECKSTYLE:ON
    // @formatter:on

    private final String displaytext;

    private HotfixScanViewMode(String displaytext) {
        this.displaytext = displaytext;
    }

    public String getDisplaytext() {
        return displaytext;
    }

    @Override
    public String toString() {
        return displaytext;
    }
}
