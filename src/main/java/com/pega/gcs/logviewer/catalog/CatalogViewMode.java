package com.pega.gcs.logviewer.catalog;

public enum CatalogViewMode {

    // @formatter:off
    // CHECKSTYLE:OFF
    SINGLE_TABLE        ( " Table " ),
    SINGLE_TREE         ( " Tree  " );
    // CHECKSTYLE:ON
    // @formatter:on

    private final String displaytext;

    private CatalogViewMode(String displaytext) {
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
