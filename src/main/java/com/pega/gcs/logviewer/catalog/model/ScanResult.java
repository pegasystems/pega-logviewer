/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.CatalogManagerWrapper;
import com.pega.gcs.logviewer.hotfixscan.PegaVersion;
import com.pega.gcs.logviewer.hotfixscan.hybrid.HybridHotfixListProvider;

public class ScanResult {

    private static final Log4j2Helper LOG = new Log4j2Helper(ScanResult.class);

    private Date inventoryTimestamp;

    private List<HotfixColumn> hotfixColumnList;

    private TreeMap<HotfixEntryKey, HotfixEntry> scanResultHotfixEntryMap;

    private TreeMap<HotfixEntryKey, HotfixEntry> notInstalledHotfixEntryMap;

    private Map<ProductInfo, List<String>> productInfoHotfixIdMap;

    private Map<String, TreeSet<String>> changeKeyInstalledHotfixIdMap;

    private Map<String, TreeSet<String>> changeKeyNotInstalledHotfixIdMap;

    private int installedHotfixCount;

    private int notInstalledHotfixCount;

    private ScanResultVersion scanResultVersion;

    public ScanResult(Date inventoryTimestamp, List<HotfixColumn> hotfixColumnList) {

        super();

        this.inventoryTimestamp = inventoryTimestamp;
        this.hotfixColumnList = hotfixColumnList;

        scanResultHotfixEntryMap = new TreeMap<>();
        notInstalledHotfixEntryMap = new TreeMap<>();
        productInfoHotfixIdMap = new TreeMap<>();
        changeKeyInstalledHotfixIdMap = new HashMap<>();
        changeKeyNotInstalledHotfixIdMap = new HashMap<>();

        installedHotfixCount = 0;
        notInstalledHotfixCount = 0;

        scanResultVersion = ScanResultVersion.getScanResultVersion(hotfixColumnList);
    }

    public Date getInventoryTimestamp() {
        return inventoryTimestamp;
    }

    public List<HotfixColumn> getHotfixColumnList() {
        return hotfixColumnList;
    }

    public List<ProductInfo> getProductInfoList() {

        List<ProductInfo> productInfoList = new ArrayList<>(productInfoHotfixIdMap.keySet());

        return productInfoList;
    }

    public Map<HotfixEntryKey, HotfixEntry> getScanResultHotfixEntryMap() {
        return Collections.unmodifiableMap(scanResultHotfixEntryMap);
    }

    public Map<HotfixEntryKey, HotfixEntry> getNotInstalledHotfixEntryMap() {
        return Collections.unmodifiableMap(notInstalledHotfixEntryMap);
    }

    public Map<String, TreeSet<String>> getChangeKeyInstalledHotfixIdMap() {
        return Collections.unmodifiableMap(changeKeyInstalledHotfixIdMap);
    }

    public Map<String, TreeSet<String>> getChangeKeyNotInstalledHotfixIdMap() {
        return Collections.unmodifiableMap(changeKeyNotInstalledHotfixIdMap);
    }

    public void setCompareScanResultHotfixEntryMap(Map<HotfixEntryKey, HotfixEntry> newHotfixEntryMap) {
        scanResultHotfixEntryMap.clear();
        scanResultHotfixEntryMap.putAll(newHotfixEntryMap);
    }

    public void processHotfixMaps(Map<String, String> productInfoMap, Map<Integer, List<String>> hotfixDataMap,
            boolean loadNotInstalledHotfixes) {
        // loadNotInstalledHotfixes - avoid on loading right file during compare mode

        for (Map.Entry<String, String> productInfoEntry : productInfoMap.entrySet()) {

            String productName = productInfoEntry.getKey();
            String productVersion = productInfoEntry.getValue();

            ProductInfo productInfo = new ProductInfo(productName, productVersion);

            productInfoHotfixIdMap.put(productInfo, null);
        }

        int hotfixIdIndex = hotfixColumnList.indexOf(HotfixColumn.HOTFIX_ID);
        int hotfixStatusIndex = hotfixColumnList.indexOf(HotfixColumn.HOTFIX_STATUS);
        int productLabelIndex = hotfixColumnList.indexOf(HotfixColumn.PRODUCT_LABEL);

        // Processing installed hotfixDataMap
        Map<String, HotfixEntry> hotfixIdMap = new HashMap<>();

        for (Integer index : hotfixDataMap.keySet()) {

            List<String> recordDataList = hotfixDataMap.get(index);

            String hotfixId = recordDataList.get(hotfixIdIndex);
            hotfixId = hotfixId.toUpperCase();

            HotfixEntry hotfixEntry = hotfixIdMap.get(hotfixId);

            if (hotfixEntry == null) {

                String hotfixStatusString = recordDataList.get(hotfixStatusIndex);
                HotfixStatus hotfixStatus = HotfixStatus.fromValue(hotfixStatusString);
                String hfixProductVersionLabel = recordDataList.get(productLabelIndex);
                boolean hybrid = checkHybrid(hotfixId, hfixProductVersionLabel);

                if (hotfixStatus == null) {
                    LOG.error("hotfixId: " + hotfixId + " hotfixStatusString - " + hotfixStatusString);
                }

                // making sure same ProductInfo object is set into every HotfixEntry.
                ProductInfo productInfo = getProductInfo(hfixProductVersionLabel);

                // key will be generated in postProcessHotfixIdMap
                hotfixEntry = new HotfixEntry(null, hotfixId, hotfixStatus, productInfo, hybrid, null);

                hotfixIdMap.put(hotfixId, hotfixEntry);

                List<String> hotfixIdList = productInfoHotfixIdMap.get(productInfo);

                if (hotfixIdList == null) {

                    hotfixIdList = new ArrayList<>();

                    productInfoHotfixIdMap.put(productInfo, hotfixIdList);
                }

                hotfixIdList.add(hotfixId);

            } else {

                hotfixEntry = hotfixIdMap.get(hotfixId);
            }

            HotfixRecordEntry hotfixRecordEntry = new HotfixRecordEntry(index, recordDataList);

            hotfixEntry.addHotfixRecordEntry(hotfixRecordEntry);
        }

        // Processing not installed hotfixDataMap
        Map<String, HotfixEntry> notInstalledHotfixIdMap = new HashMap<>();

        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();

        if (loadNotInstalledHotfixes && catalogManagerWrapper.isInitialised()
                && catalogManagerWrapper.isCatalogFileAvailable()) {

            for (ProductInfo productInfo : productInfoHotfixIdMap.keySet()) {

                String productName = productInfo.getProductName();
                String productVersion = productInfo.getProductVersion();

                List<String> installedHotfixIdList = productInfoHotfixIdMap.get(productInfo);

                Set<String> catalogProductNameSet = catalogManagerWrapper.getCatalogProductNameSet(productName);

                if (catalogProductNameSet != null) {

                    for (String catalogProductName : catalogProductNameSet) {

                        Map<String, List<List<String>>> hotfixEntryDataListMap;

                        hotfixEntryDataListMap = catalogManagerWrapper
                                .getHotfixEntryDataListMapForProductRelease(catalogProductName, productVersion);

                        if (hotfixEntryDataListMap != null) {
                            List<String> catalogHotfiIdList = new ArrayList<>(hotfixEntryDataListMap.keySet());

                            if (installedHotfixIdList != null) {
                                catalogHotfiIdList.removeAll(installedHotfixIdList);
                            }

                            for (String hotfixId : catalogHotfiIdList) {

                                // TODO evaluate hybrid for not installed list?
                                boolean hybrid = false;

                                // key will be generated in postProcessHotfixIdMap
                                HotfixEntry hotfixEntry = new HotfixEntry(null, hotfixId, HotfixStatus.NOT_INSTALLED,
                                        productInfo, hybrid, null);

                                notInstalledHotfixIdMap.put(hotfixId, hotfixEntry);

                                List<List<String>> hotfixEntryDataList = hotfixEntryDataListMap.get(hotfixId);

                                AtomicInteger index = new AtomicInteger(0);

                                for (List<String> hotfixRecordEntryList : hotfixEntryDataList) {

                                    HotfixRecordEntry hotfixRecordEntry = new HotfixRecordEntry(index.incrementAndGet(),
                                            hotfixRecordEntryList);

                                    hotfixEntry.addHotfixRecordEntry(hotfixRecordEntry);
                                }
                            }
                        }
                    }
                }
            }
        }

        // postProcessing
        postProcessHotfixIdMap(hotfixIdMap, notInstalledHotfixIdMap);
    }

    private ProductInfo getProductInfo(String hfixProductVersionLabel) {

        // add hotfixId to productInfoHotfixIdMap
        ProductInfo productInfoFromLabel = ProductInfo.getProductInfoFromLabel(hfixProductVersionLabel);

        // making sure same ProductInfo object is set into every HotfixEntry.
        ProductInfo productInfo = null;

        for (ProductInfo pi : productInfoHotfixIdMap.keySet()) {

            if (pi.equals(productInfoFromLabel)) {
                productInfo = pi;
                break;
            }
        }

        if (productInfo == null) {
            productInfo = productInfoFromLabel;
        }

        return productInfo;
    }

    private boolean checkHybrid(String hotfixId, String hfixProductVersionLabel) {

        boolean hybrid = false;

        PegaVersion pegaVersion = PegaVersion.getPegaVersion(hfixProductVersionLabel);

        if (pegaVersion != null) {

            String pvStr = pegaVersion.getVersion();

            HybridHotfixListProvider hybridHotfixListProvider;
            hybridHotfixListProvider = HybridHotfixListProvider.getInstance();

            List<String> hybridHotfixList = hybridHotfixListProvider.getReleaseHotfixIdMap().get(pvStr);

            if ((hybridHotfixList != null) && (hybridHotfixList.contains(hotfixId))) {
                hybrid = true;
            }
        }

        return hybrid;
    }

    private void postProcessHotfixIdMap(Map<String, HotfixEntry> hotfixIdMap,
            Map<String, HotfixEntry> notInstalledHotfixIdMap) {

        scanResultHotfixEntryMap.clear();
        notInstalledHotfixEntryMap.clear();
        changeKeyInstalledHotfixIdMap.clear();
        changeKeyNotInstalledHotfixIdMap.clear();

        List<String> hotfixIdList = new ArrayList<>(hotfixIdMap.keySet());
        List<String> notInstalledHotfixIdList = new ArrayList<>(notInstalledHotfixIdMap.keySet());

        installedHotfixCount = hotfixIdList.size();
        notInstalledHotfixCount = notInstalledHotfixIdList.size();

        // order hotfixIds to generate HotfixEntryKey in that order
        HotfixIDComparator hotfixIDComparator = new HotfixIDComparator();

        Collections.sort(hotfixIdList, hotfixIDComparator);
        Collections.sort(notInstalledHotfixIdList, hotfixIDComparator);

        AtomicInteger hotfixEntryIndex = new AtomicInteger(0);

        for (String hotfixId : hotfixIdList) {

            HotfixEntry hotfixEntry = hotfixIdMap.get(hotfixId);

            int id = hotfixEntryIndex.incrementAndGet();

            HotfixEntryKey hotfixEntryKey = new HotfixEntryKey(id, hotfixId);

            hotfixEntry.setHotfixEntryKey(hotfixEntryKey);

            scanResultHotfixEntryMap.put(hotfixEntryKey, hotfixEntry);

            organiseHotfixEntry(hotfixEntry, changeKeyInstalledHotfixIdMap, hotfixColumnList);

        }

        List<HotfixColumn> catalogHotfixColumnList = HotfixColumn.getCatalogHotfixColumnList();

        for (String hotfixId : notInstalledHotfixIdList) {

            HotfixEntry hotfixEntry = notInstalledHotfixIdMap.get(hotfixId);

            int id = hotfixEntryIndex.incrementAndGet();

            HotfixEntryKey hotfixEntryKey = new HotfixEntryKey(id, hotfixId);

            hotfixEntry.setHotfixEntryKey(hotfixEntryKey);

            notInstalledHotfixEntryMap.put(hotfixEntryKey, hotfixEntry);

            // catalog columns are still old style
            organiseHotfixEntryV7(hotfixEntry, changeKeyNotInstalledHotfixIdMap, catalogHotfixColumnList);

        }
    }

    private void organiseHotfixEntry(HotfixEntry hotfixEntry, Map<String, TreeSet<String>> changeKeyHotfixIdMap,
            List<HotfixColumn> hotfixColumnList) {

        switch (scanResultVersion) {

        case SCAN_RESULT_V7:
            organiseHotfixEntryV7(hotfixEntry, changeKeyHotfixIdMap, hotfixColumnList);
            break;

        case SCAN_RESULT_V8:
            organiseHotfixEntryV8(hotfixEntry, hotfixColumnList);
            break;

        default:
            break;

        }

    }

    // 1. sort hotfix records within a hotfix.
    // 2. prepare a map of change key vs hotfix ids
    private void organiseHotfixEntryV7(HotfixEntry hotfixEntry, Map<String, TreeSet<String>> changeKeyHotfixIdMap,
            List<HotfixColumn> hotfixColumnList) {

        int codeChangeClassIndex = hotfixColumnList.indexOf(HotfixColumn.CODE_CHANGE_CLASSNAME);
        int codeChangePackageIndex = hotfixColumnList.indexOf(HotfixColumn.CODE_CHANGE_PACKAGENAME);
        int ruleChangeKeyIndex = hotfixColumnList.indexOf(HotfixColumn.RULE_CHANGE_KEY);
        int schemaChangeKeyIndex = hotfixColumnList.indexOf(HotfixColumn.SCHEMA_CHANGE_KEY);

        List<HotfixEntrySort> codeChangeClassnameSortList = new ArrayList<>(); // CODE_CHANGE_CLASSNAME
        List<HotfixEntrySort> ruleChangeKeySortList = new ArrayList<>();// RULE_CHANGE_KEY
        List<HotfixEntrySort> schemaChangeKeySortList = new ArrayList<>(); // SCHEMA_CHANGE_KEY
        List<HotfixRecordEntry> otherSortList = new ArrayList<>();

        String hotfixId = hotfixEntry.getHotfixId();

        List<HotfixRecordEntry> hotfixRecordEntryList = hotfixEntry.getHotfixRecordEntryList();

        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            HotfixEntrySort hotfixEntrySort = null;

            String codeChangeKey = hotfixRecordEntry.getCodeChangeKey(codeChangeClassIndex, codeChangePackageIndex);

            String codeChangeClass = hotfixRecordEntry.getHotfixRecordEntryData(codeChangeClassIndex);

            if ((codeChangeClass != null) && (!"".equals(codeChangeClass))) {

                hotfixEntrySort = new HotfixEntrySort(hotfixRecordEntry, codeChangeClass);

                codeChangeClassnameSortList.add(hotfixEntrySort);

                updateChangeKeyHotfixIdMap(hotfixId, codeChangeKey, changeKeyHotfixIdMap);

            } else {

                String ruleChangeKey = hotfixRecordEntry.getHotfixRecordEntryData(ruleChangeKeyIndex);

                if ((ruleChangeKey != null) && (!"".equals(ruleChangeKey))) {

                    hotfixEntrySort = new HotfixEntrySort(hotfixRecordEntry, ruleChangeKey);

                    ruleChangeKeySortList.add(hotfixEntrySort);

                    updateChangeKeyHotfixIdMap(hotfixId, ruleChangeKey, changeKeyHotfixIdMap);

                } else {

                    String schemaChangeKey = hotfixRecordEntry.getHotfixRecordEntryData(schemaChangeKeyIndex);

                    if ((schemaChangeKey != null) && (!"".equals(schemaChangeKey))) {

                        hotfixEntrySort = new HotfixEntrySort(hotfixRecordEntry, schemaChangeKey);

                        schemaChangeKeySortList.add(hotfixEntrySort);

                        updateChangeKeyHotfixIdMap(hotfixId, schemaChangeKey, changeKeyHotfixIdMap);

                    } else {

                        otherSortList.add(hotfixRecordEntry);
                    }
                }
            }
        }

        Collections.sort(codeChangeClassnameSortList);
        Collections.sort(ruleChangeKeySortList);
        Collections.sort(schemaChangeKeySortList);
        Collections.sort(otherSortList);

        AtomicInteger index = new AtomicInteger(1);

        for (HotfixEntrySort hotfixEntrySort : codeChangeClassnameSortList) {

            HotfixRecordEntry hotfixRecordEntry = hotfixEntrySort.getHotfixRecordEntry();

            hotfixRecordEntry.setEntryId(index.getAndIncrement());
        }

        for (HotfixEntrySort hotfixEntrySort : ruleChangeKeySortList) {

            HotfixRecordEntry hotfixRecordEntry = hotfixEntrySort.getHotfixRecordEntry();

            hotfixRecordEntry.setEntryId(index.getAndIncrement());
        }

        for (HotfixEntrySort hotfixEntrySort : schemaChangeKeySortList) {

            HotfixRecordEntry hotfixRecordEntry = hotfixEntrySort.getHotfixRecordEntry();

            hotfixRecordEntry.setEntryId(index.getAndIncrement());
        }

        for (HotfixRecordEntry hotfixRecordEntry : otherSortList) {

            hotfixRecordEntry.setEntryId(index.getAndIncrement());
        }

        Collections.sort(hotfixRecordEntryList);

        hotfixEntry.setHotfixRecordEntryList(hotfixRecordEntryList);
    }

    // 1. sort hotfix records within a hotfix.
    // 2. prepare a map of change key vs hotfix ids
    private void organiseHotfixEntryV8(HotfixEntry hotfixEntry, List<HotfixColumn> hotfixColumnList) {

        int containerIndex = hotfixColumnList.indexOf(HotfixColumn.CONTAINER);
        int packageIndex = hotfixColumnList.indexOf(HotfixColumn.PACKAGE_NAME);
        int instanceIndex = hotfixColumnList.indexOf(HotfixColumn.INSTANCE);

        Comparator<HotfixRecordEntry> hotfixRecordEntryComparator;

        hotfixRecordEntryComparator = new Comparator<HotfixRecordEntry>() {

            @Override
            public int compare(HotfixRecordEntry thisHotfixRecordEntry, HotfixRecordEntry otherHotfixRecordEntry) {

                String thisContainer = thisHotfixRecordEntry.getHotfixRecordEntryData(containerIndex);
                String thisPackage = thisHotfixRecordEntry.getHotfixRecordEntryData(packageIndex);
                String thisInstance = thisHotfixRecordEntry.getHotfixRecordEntryData(instanceIndex);

                String otherContainer = otherHotfixRecordEntry.getHotfixRecordEntryData(containerIndex);
                String otherPackage = otherHotfixRecordEntry.getHotfixRecordEntryData(packageIndex);
                String otherInstance = otherHotfixRecordEntry.getHotfixRecordEntryData(instanceIndex);

                int compare = 0;

                if ((thisContainer != null) && (otherContainer != null)) {
                    compare = thisContainer.compareTo(otherContainer);
                }

                if ((compare == 0) && (thisPackage != null) && (otherPackage != null)) {
                    compare = thisPackage.compareTo(otherPackage);
                }

                if ((compare == 0) && (thisInstance != null) && (otherInstance != null)) {
                    compare = thisInstance.compareTo(otherInstance);
                }

                return compare;
            }

        };

        List<HotfixRecordEntry> hotfixRecordEntryList = hotfixEntry.getHotfixRecordEntryList();

        Collections.sort(hotfixRecordEntryList, hotfixRecordEntryComparator);

        AtomicInteger index = new AtomicInteger(1);

        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            hotfixRecordEntry.setEntryId(index.getAndIncrement());
        }

        hotfixEntry.setHotfixRecordEntryList(hotfixRecordEntryList);
    }

    private void updateChangeKeyHotfixIdMap(String hotfixId, String changeKey,
            Map<String, TreeSet<String>> changeKeyHotfixIdMap) {

        TreeSet<String> hotfixIdList = changeKeyHotfixIdMap.get(changeKey);

        if (hotfixIdList == null) {
            HotfixIDComparator hotfixIDComparator = new HotfixIDComparator();
            hotfixIdList = new TreeSet<>(hotfixIDComparator);
            changeKeyHotfixIdMap.put(changeKey, hotfixIdList);
        }

        hotfixIdList.add(hotfixId);
    }

    public int getInstalledHotfixCount() {
        return installedHotfixCount;
    }

    public int getNotInstalledHotfixCount() {
        return notInstalledHotfixCount;
    }

    // public String getHotfixRecordEntryData(HotfixRecordEntry hotfixRecordEntry,
    // HotfixColumn hotfixColumn) {
    //
    // String hotfixdata = null;
    //
    // if (hotfixColumn.equals(HotfixColumn.ID)) {
    // hotfixdata = hotfixRecordEntry.getEntryId().toString();
    // } else {
    // int index = hotfixColumnList.indexOf(hotfixColumn);
    //
    // if (index != -1) {
    // hotfixdata = hotfixRecordEntry.getRecordDataList().get(index);
    // }
    // }
    //
    // return hotfixdata;
    // }
    //
    // public boolean getHotfixRecordEntrySearchFound(HotfixRecordEntry
    // hotfixRecordEntry, HotfixColumn hotfixColumn) {
    //
    // boolean searchFound = false;
    //
    // int index = hotfixColumnList.indexOf(hotfixColumn);
    //
    // searchFound = hotfixRecordEntry.isSearchFound(index);
    //
    // return searchFound;
    // }

}
