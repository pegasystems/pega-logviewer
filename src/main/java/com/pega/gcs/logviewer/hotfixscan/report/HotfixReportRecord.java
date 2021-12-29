
package com.pega.gcs.logviewer.hotfixscan.report;

import java.util.List;

/**
 * Index 0 is key.
 *
 */
public class HotfixReportRecord {

    private Integer key;

    private List<String> valueList;

    public HotfixReportRecord(Integer key, List<String> valueList) {
        super();
        this.key = key;
        this.valueList = valueList;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue(int index) {

        String value = null;

        if (index == 0) {

            value = key.toString();

        } else {

            index = index - 1;
            value = valueList.get(index);
        }

        return value;
    }
}
