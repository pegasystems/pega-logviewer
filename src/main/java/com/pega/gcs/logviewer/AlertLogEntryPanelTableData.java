
package com.pega.gcs.logviewer;

public class AlertLogEntryPanelTableData {

    private String nameColumn;

    private String valueColumn;

    private boolean isHref;

    public AlertLogEntryPanelTableData(String nameColumn, String valueColumn, boolean isHref) {
        super();
        this.nameColumn = nameColumn;
        this.valueColumn = valueColumn;
        this.isHref = isHref;
    }

    public String getNameColumn() {
        return nameColumn;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    public boolean isHref() {
        return isHref;
    }

}