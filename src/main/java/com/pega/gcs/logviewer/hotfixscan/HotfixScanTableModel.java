/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogViewer;
import com.pega.gcs.logviewer.catalog.HotfixTableModel;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.InventoryVersion;
import com.pega.gcs.logviewer.catalog.model.ScanResult;

public class HotfixScanTableModel extends HotfixTableModel {

    private static final long serialVersionUID = 4368404105488278206L;

    private ScanResult scanResult;

    private boolean displayNotInstalledHfixes;

    public HotfixScanTableModel(RecentFile recentFile, SearchData<HotfixEntryKey> searchData,
            List<HotfixColumn> visibleColumnList) {

        super(recentFile, searchData, visibleColumnList);

        this.scanResult = null;
        this.displayNotInstalledHfixes = false;

    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(ScanResult scanResult) {

        this.scanResult = scanResult;

        resetModel();

        updateColumnFilterMap();

        fireTableStructureChanged();
    }

    @Override
    public List<HotfixColumn> getHotfixColumnList() {

        List<HotfixColumn> hotfixColumnList = null;

        if (scanResult != null) {
            hotfixColumnList = scanResult.getHotfixColumnList();
        }

        return hotfixColumnList;
    }

    @Override
    protected TreeMap<HotfixEntryKey, HotfixEntry> getHotfixEntryMap() {

        TreeMap<HotfixEntryKey, HotfixEntry> hotfixEntryMap = null;

        if (scanResult != null) {

            hotfixEntryMap = new TreeMap<>();

            Map<HotfixEntryKey, HotfixEntry> scanResultHotfixEntryMap;
            scanResultHotfixEntryMap = scanResult.getScanResultHotfixEntryMap();

            hotfixEntryMap.putAll(scanResultHotfixEntryMap);

            boolean displayNotInstalledHfixes = isDisplayNotInstalledHfixes();

            if (displayNotInstalledHfixes) {
                Map<HotfixEntryKey, HotfixEntry> notInstalledHotfixEntryMap;
                notInstalledHotfixEntryMap = scanResult.getNotInstalledHotfixEntryMap();

                hotfixEntryMap.putAll(notInstalledHotfixEntryMap);
            }
        }

        return hotfixEntryMap;
    }

    public InventoryVersion getInventoryVersion() {

        InventoryVersion inventoryVersion = InventoryVersion.INVENTORY_VERSION_7;

        String inventoryFilePath = getFilePath();
        File inventoryFile = new File(inventoryFilePath);
        String filename = FileUtilities.getFileBaseName(inventoryFile);
        filename = filename.toUpperCase();

        Pattern v7Pattern = Pattern.compile(LogViewer.SYSTEM_SCAN_FILE_NAME_REGEX_V7);
        Matcher v7PatternMatcher = v7Pattern.matcher(filename);
        boolean v7 = v7PatternMatcher.find();

        if (v7) {
            inventoryVersion = InventoryVersion.INVENTORY_VERSION_7;
        } else {

            Pattern v6Pattern = Pattern.compile(LogViewer.SYSTEM_SCAN_FILE_NAME_REGEX_V6);
            Matcher v6PatternMatcher = v6Pattern.matcher(filename);
            boolean v6 = v6PatternMatcher.find();

            if (v6) {
                inventoryVersion = InventoryVersion.INVENTORY_VERSION_6;
            }
        }

        return inventoryVersion;
    }

    public boolean isDisplayNotInstalledHfixes() {
        return displayNotInstalledHfixes;
    }

    public void setDisplayNotInstalledHfixes(boolean displayNotInstalledHfixes) {

        if (this.displayNotInstalledHfixes != displayNotInstalledHfixes) {

            this.displayNotInstalledHfixes = displayNotInstalledHfixes;

            resetModel();

            updateColumnFilterMap();

            fireTableDataChanged();
        }

    }

}
