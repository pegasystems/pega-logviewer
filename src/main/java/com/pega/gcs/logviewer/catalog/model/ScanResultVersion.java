
package com.pega.gcs.logviewer.catalog.model;

import java.util.List;

public enum ScanResultVersion {

    SCAN_RESULT_V7, SCAN_RESULT_V8;

    public static ScanResultVersion getScanResultVersion(List<HotfixColumn> hotfixColumnList) {

        ScanResultVersion scanResultVersion = null;

        if (hotfixColumnList.contains(HotfixColumn.CODE_CHANGE_CLASSNAME)) {

            scanResultVersion = ScanResultVersion.SCAN_RESULT_V7;

        } else if (hotfixColumnList.contains(HotfixColumn.CONTAINER)) {

            scanResultVersion = ScanResultVersion.SCAN_RESULT_V8;
        }

        return scanResultVersion;
    }

}
