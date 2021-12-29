/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.diff.EditCommand;
import com.pega.gcs.fringecommon.utilities.diff.Matcher;
import com.pega.gcs.fringecommon.utilities.diff.MyersDifferenceAlgorithm;
import com.pega.gcs.logviewer.catalog.HotfixTable;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.ScanResult;

public class HotfixCompareTask extends SwingWorker<Void, String> {

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixCompareTask.class);

    private static final String PROGRESS_MONITOR_STATUS_CHANGE = "indeterminate";

    private Component parent;

    private HotfixScanTableModel hotfixScanTableModel;

    private HotfixTable hotfixTableLeft;

    private HotfixTable hotfixTableRight;

    // use progress monitor only in process() method.
    private ModalProgressMonitor progressMonitor;

    public HotfixCompareTask(Component parent, HotfixScanTableModel hotfixScanTableModel, HotfixTable hotfixTableLeft,
            HotfixTable hotfixTableRight, ModalProgressMonitor progressMonitor) {

        super();

        this.parent = parent;
        this.hotfixScanTableModel = hotfixScanTableModel;
        this.hotfixTableLeft = hotfixTableLeft;
        this.hotfixTableRight = hotfixTableRight;
        this.progressMonitor = progressMonitor;
    }

    private HotfixScanTableModel getHotfixScanTableModel() {
        return hotfixScanTableModel;
    }

    private HotfixTable getHotfixTableLeft() {
        return hotfixTableLeft;
    }

    private HotfixTable getHotfixTableRight() {
        return hotfixTableRight;
    }

    private ModalProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @Override
    protected Void doInBackground() throws Exception {

        LOG.info("Hotfix compare task - Started");

        HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();
        HotfixTable hotfixTableLeft = getHotfixTableLeft();
        HotfixTable hotfixTableRight = getHotfixTableRight();

        // right side model is built freshly for every load of the file
        HotfixScanTableCompareModel hotfixScanTableCompareModelRight;
        hotfixScanTableCompareModelRight = (HotfixScanTableCompareModel) hotfixTableRight.getModel();

        boolean loadNotInstalledHotfixes = false;
        HotfixScanMainPanel.loadFile(parent, hotfixScanTableCompareModelRight, loadNotInstalledHotfixes, true);

        // trigger changing progress monitor to indeterminate
        publish(PROGRESS_MONITOR_STATUS_CHANGE);

        // built the left side compare model afresh for every compare
        RecentFile recentFile = hotfixScanTableModel.getRecentFile();
        SearchData<HotfixEntryKey> searchData = hotfixScanTableModel.getSearchData();

        List<HotfixColumn> visibleColumnList = hotfixScanTableModel.getVisibleColumnList();

        HotfixScanTableCompareModel hotfixScanTableCompareModelLeft;
        hotfixScanTableCompareModelLeft = new HotfixScanTableCompareModel(recentFile, searchData, visibleColumnList);

        ScanResult origScanResultLeft = hotfixScanTableModel.getScanResult();
        ScanResult origScanResultRight = hotfixScanTableCompareModelRight.getScanResult();

        TreeMap<CompareHotfixEntryKey, List<CompareHotfixEntryKey>> compareNavIndexMap;
        compareNavIndexMap = new TreeMap<CompareHotfixEntryKey, List<CompareHotfixEntryKey>>();

        try {

            List<CompareHotfixEntryKey> thisMarkerHotfixEntryKeyList = new ArrayList<>();
            List<CompareHotfixEntryKey> otherMarkerHotfixEntryKeyList = new ArrayList<>();

            Map<HotfixEntryKey, HotfixEntry> thisScanResultHotfixEntryMap;
            thisScanResultHotfixEntryMap = origScanResultLeft.getScanResultHotfixEntryMap();

            Map<HotfixEntryKey, HotfixEntry> otherScanResultHotfixEntryMap;
            otherScanResultHotfixEntryMap = origScanResultRight.getScanResultHotfixEntryMap();

            // THIS
            List<HotfixEntryKey> thisHotfixEntryKeyList = new ArrayList<>();
            List<HotfixEntry> thisHotfixEntryList = new ArrayList<>();

            // OTHER
            List<HotfixEntryKey> otherHotfixEntryKeyList = new ArrayList<HotfixEntryKey>();
            List<HotfixEntry> otherHotfixEntryList = new ArrayList<>();

            getKeyAndEntryList(thisScanResultHotfixEntryMap, thisHotfixEntryKeyList, thisHotfixEntryList);
            getKeyAndEntryList(otherScanResultHotfixEntryMap, otherHotfixEntryKeyList, otherHotfixEntryList);

            // bug fix: include hotfix status for difference as well.
            Matcher<HotfixEntry> matcher = new Matcher<HotfixEntry>() {

                @Override
                public boolean match(HotfixEntry o1, HotfixEntry o2) {

                    boolean match = false;

                    match = ((o1.getHotfixId().equals(o2.getHotfixId())))
                            && (o1.getHotfixStatus().equals(o2.getHotfixStatus()));

                    return match;
                }
            };

            long before = System.currentTimeMillis();

            List<EditCommand> editScript = MyersDifferenceAlgorithm.diffGreedyLCS(progressMonitor, thisHotfixEntryList,
                    otherHotfixEntryList, matcher);

            long diff = System.currentTimeMillis() - before;
            DecimalFormat df = new DecimalFormat("#0.000");

            String time = df.format((double) diff / 1E3);

            LOG.info("MyersDifferenceAlgorithm diffGreedyLCS took " + time + "s.");

            Map<HotfixEntryKey, HotfixEntry> hotfixEntryMapLeft;
            hotfixEntryMapLeft = new HashMap<>();

            Map<HotfixEntryKey, HotfixEntry> hotfixEntryMapRight;
            hotfixEntryMapRight = new HashMap<>();

            int index = 1;
            int indexThis = 0;
            int indexOther = 0;

            CompareHotfixEntryKey compareNavIndexKey = null;
            EditCommand prevEC = EditCommand.SNAKE;

            HotfixEntryKey teKeyThis;
            HotfixEntryKey teKeyOther;
            HotfixEntryKey teKey;

            HotfixEntry teCompare;
            HotfixEntry te;

            Color deleteColor = Color.LIGHT_GRAY;
            Color insertColor = MyColor.LIGHTEST_GREEN;

            for (EditCommand ec : editScript) {

                CompareHotfixEntryKey teKeyCompare = null;

                switch (ec) {
                case DELETE:
                    // Add compare type to OTHER List
                    // OTHER
                    teKeyCompare = new CompareHotfixEntryKey(index, -1, -1);
                    teCompare = new HotfixEntry(teKeyCompare, "", null, null, false, deleteColor);
                    hotfixEntryMapRight.put(teKeyCompare, teCompare);

                    // THIS
                    teKeyThis = thisHotfixEntryKeyList.get(indexThis);
                    te = thisHotfixEntryList.get(indexThis);
                    int thisId = teKeyThis.getId();
                    Integer thisHotfixNumber = teKeyThis.getHotfixNumber();
                    teKey = new CompareHotfixEntryKey(index, thisId, thisHotfixNumber);
                    te.setHotfixEntryKey(teKey);
                    hotfixEntryMapLeft.put(teKey, te);
                    indexThis++;

                    otherMarkerHotfixEntryKeyList.add(teKeyCompare);

                    break;
                case INSERT:
                    // Add compare type to THIS List
                    // OTHER
                    teKeyOther = otherHotfixEntryKeyList.get(indexOther);
                    te = otherHotfixEntryList.get(indexOther);
                    int otherId = teKeyOther.getId();
                    Integer otherHotfixNumber = teKeyOther.getHotfixNumber();
                    teKey = new CompareHotfixEntryKey(index, otherId, otherHotfixNumber);
                    te.setHotfixEntryKey(teKey);
                    hotfixEntryMapRight.put(teKey, te);
                    indexOther++;

                    // THIS
                    teKeyCompare = new CompareHotfixEntryKey(index, -1, -1);
                    teCompare = new HotfixEntry(teKeyCompare, "", null, null, false, insertColor);
                    hotfixEntryMapLeft.put(teKeyCompare, teCompare);

                    thisMarkerHotfixEntryKeyList.add(teKeyCompare);

                    break;
                case SNAKE:
                    // OTHER
                    teKeyOther = otherHotfixEntryKeyList.get(indexOther);
                    te = otherHotfixEntryList.get(indexOther);
                    otherId = teKeyOther.getId();
                    otherHotfixNumber = teKeyOther.getHotfixNumber();
                    teKey = new CompareHotfixEntryKey(index, otherId, otherHotfixNumber);
                    te.setHotfixEntryKey(teKey);
                    hotfixEntryMapRight.put(teKey, te);
                    indexOther++;

                    // THIS
                    teKeyThis = thisHotfixEntryKeyList.get(indexThis);
                    te = thisHotfixEntryList.get(indexThis);
                    thisId = teKeyThis.getId();
                    thisHotfixNumber = teKeyThis.getHotfixNumber();
                    teKey = new CompareHotfixEntryKey(index, thisId, thisHotfixNumber);
                    te.setHotfixEntryKey(teKey);
                    hotfixEntryMapLeft.put(teKey, te);
                    indexThis++;

                    break;
                default:
                    break;

                }

                if ((!prevEC.equals(ec)) && (teKeyCompare != null)) {

                    compareNavIndexKey = teKeyCompare;

                    List<CompareHotfixEntryKey> compareIndexList = new ArrayList<CompareHotfixEntryKey>();
                    compareIndexList.add(teKeyCompare);

                    compareNavIndexMap.put(compareNavIndexKey, compareIndexList);

                } else if ((compareNavIndexKey != null) && (teKeyCompare != null)) {

                    List<CompareHotfixEntryKey> compareIndexList;
                    compareIndexList = compareNavIndexMap.get(compareNavIndexKey);

                    compareIndexList.add(teKeyCompare);
                }

                prevEC = ec;
                index++;
            }

            ScanResult scanResultLeft = new ScanResult(origScanResultLeft.getInventoryTimestamp(),
                    origScanResultLeft.getHotfixColumnList());
            ScanResult scanResultRight = new ScanResult(origScanResultRight.getInventoryTimestamp(),
                    origScanResultRight.getHotfixColumnList());

            scanResultLeft.setCompareScanResultHotfixEntryMap(hotfixEntryMapLeft);
            scanResultRight.setCompareScanResultHotfixEntryMap(hotfixEntryMapRight);

            hotfixScanTableCompareModelLeft.setScanResult(scanResultLeft);
            hotfixScanTableCompareModelRight.setScanResult(scanResultRight);

            hotfixScanTableCompareModelLeft.setCompareMarkerList(thisMarkerHotfixEntryKeyList);
            hotfixScanTableCompareModelRight.setCompareMarkerList(otherMarkerHotfixEntryKeyList);

            hotfixScanTableCompareModelLeft.setCompareNavIndexMap(compareNavIndexMap);

            LOG.info("Hotfix compare task done " + compareNavIndexMap.size() + " chunks found");

            // set the left table model as compare model
            hotfixTableLeft.setModel(hotfixScanTableCompareModelLeft);

        } catch (Exception e) {
            LOG.error("Exception in compare task", e);
        } finally {

            if (progressMonitor.isCanceled()) {
                cancel(true);
            }

            LOG.info("Hotfix compare task - End");

            // cleanup
            System.gc();
        }

        return null;
    }

    @Override
    protected void process(List<String> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        Collections.sort(chunks);

        String changeStatus = chunks.get(chunks.size() - 1);

        ModalProgressMonitor progressMonitor = getProgressMonitor();

        if ((changeStatus.equals(PROGRESS_MONITOR_STATUS_CHANGE))
                && ((progressMonitor != null) && (!progressMonitor.isIndeterminate()))) {

            progressMonitor.setIndeterminate(true);
            progressMonitor.setNote("Comparing ...");
            progressMonitor.show();

        }
    }

    private void getKeyAndEntryList(Map<HotfixEntryKey, HotfixEntry> hotfixEntryMap,
            List<HotfixEntryKey> hotfixEntryKeyList, List<HotfixEntry> hotfixEntryList) {

        hotfixEntryKeyList.clear();
        hotfixEntryList.clear();

        for (Map.Entry<HotfixEntryKey, HotfixEntry> entry : hotfixEntryMap.entrySet()) {

            HotfixEntryKey hotfixEntryKey = entry.getKey();

            if (hotfixEntryKey.getId() != -1) {

                HotfixEntry hotfixEntry = entry.getValue();

                hotfixEntryKeyList.add(hotfixEntryKey);
                hotfixEntryList.add(hotfixEntry);

            }
        }
    }

}
