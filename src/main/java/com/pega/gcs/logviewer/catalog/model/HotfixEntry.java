/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.utilities.Identifiable;

public class HotfixEntry implements Identifiable<HotfixEntryKey>, Comparable<HotfixEntry> {

    private HotfixEntryKey hotfixEntryKey;

    private HotfixStatus hotfixStatus;

    private ProductInfo productInfo;

    private boolean hybrid;

    private CriticalLevel criticalLevel;

    private String packageDate;

    private String jarsToRemove;

    private Color background;

    private List<HotfixRecordEntry> hotfixRecordEntryList;

    private boolean catalogSearchFound;

    private boolean searchFound;

    private TreeSet<HotfixEntry> backwardHotfixEntrySet;

    private TreeSet<HotfixEntry> forwardHotfixEntrySet;

    // used in catalog scanner
    public HotfixEntry(HotfixEntryKey hotfixEntryKey, HotfixStatus hotfixStatus, ProductInfo productInfo,
            boolean hybrid) {
        this(hotfixEntryKey, hotfixStatus, productInfo, hybrid, null);
    }

    // used in compare to set diff color
    public HotfixEntry(HotfixEntryKey hotfixEntryKey, HotfixStatus hotfixStatus, ProductInfo productInfo,
            boolean hybrid, Color background) {
        super();

        this.hotfixEntryKey = hotfixEntryKey;
        this.hotfixStatus = hotfixStatus;
        this.productInfo = productInfo;
        this.hybrid = hybrid;
        this.criticalLevel = null;

        setBackground(background);

        hotfixRecordEntryList = new ArrayList<>();

        backwardHotfixEntrySet = new TreeSet<>();

        forwardHotfixEntrySet = new TreeSet<>();
    }

    // should only be unsed in compare task
    public void setHotfixEntryKey(HotfixEntryKey hotfixEntryKey) {
        this.hotfixEntryKey = hotfixEntryKey;
    }

    private void setBackground(Color background) {

        if (background == null) {

            if (hybrid) {
                background = MyColor.ORANGE;
            } else if (criticalLevel != null) {

                switch (criticalLevel) {

                case CRITICAL:
                    background = MyColor.FATAL;
                    break;

                case IMPORTANT:
                    background = MyColor.ALERT;
                    break;

                case NONCRITICAL:
                    background = MyColor.LIGHTEST_LIGHT_GRAY;
                    break;

                default:
                    background = MyColor.LIGHTEST_LIGHT_GRAY;
                    break;
                }
            } else {
                background = MyColor.LIGHTEST_LIGHT_GRAY;
            }
        }

        this.background = background;
    }

    public CriticalLevel getCriticalLevel() {
        return criticalLevel;
    }

    public void setCriticalLevel(CriticalLevel criticalLevel) {
        this.criticalLevel = criticalLevel;

        setBackground(null);
    }

    public String getPackageDate() {
        return packageDate;
    }

    public void setPackageDate(String packageDate) {
        this.packageDate = packageDate;
    }

    public String getJarsToRemove() {
        return jarsToRemove;
    }

    public void setJarsToRemove(String jarsToRemove) {
        this.jarsToRemove = jarsToRemove;
    }

    public List<HotfixRecordEntry> getHotfixRecordEntryList() {
        return hotfixRecordEntryList;
    }

    public void setHotfixRecordEntryList(List<HotfixRecordEntry> hotfixRecordEntryList) {
        this.hotfixRecordEntryList = hotfixRecordEntryList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HotfixEntry [hotfixEntryKey=");
        builder.append(hotfixEntryKey);
        builder.append(", hotfixStatus=");
        builder.append(hotfixStatus);
        builder.append(", productInfo=");
        builder.append(productInfo);
        builder.append(", hybrid=");
        builder.append(hybrid);
        builder.append(", searchFound=");
        builder.append(searchFound);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public HotfixEntryKey getKey() {
        return hotfixEntryKey;
    }

    public HotfixStatus getHotfixStatus() {
        return hotfixStatus;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public boolean isHybrid() {
        return hybrid;
    }

    public Color getBackground() {
        return background;
    }

    public boolean isCatalogSearchFound() {
        return catalogSearchFound;
    }

    public boolean isSearchFound() {
        return searchFound;
    }

    public void clearCatalogSearch() {

        this.catalogSearchFound = false;

        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            hotfixRecordEntry.clearCatalogSearch();
        }
    }

    public void clearSearch() {

        this.searchFound = false;

        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            hotfixRecordEntry.clearSearch();
        }
    }

    public void addHotfixRecordEntry(HotfixRecordEntry hotfixRecordEntry) {
        hotfixRecordEntryList.add(hotfixRecordEntry);
    }

    public boolean catalogSearch(String searchStr) {

        boolean found = false;

        // loop through even if we get the hits, so as to set search markers within each
        // record
        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            boolean innerFound = hotfixRecordEntry.catalogSearch(searchStr);

            found = found || innerFound;

        }

        catalogSearchFound = found;

        return found;
    }

    /**
     * Search function.
     * 
     * @param searchStr     -if case sensitive is false, then the string is passed as UPPER CASE otherwise the it is passed as it is.
     * @param casesensitive - is case sensitive search
     * @param saveResults   - search is called from both filter and search functionality, don't record results when using from filter
     * @return boolean, whether the searchStr is found or not
     */
    public boolean search(String searchStr, boolean casesensitive, boolean saveResults) {

        boolean found = false;

        // loop through even if we get the hits, so as to set search markers within each
        // record
        for (HotfixRecordEntry hotfixRecordEntry : hotfixRecordEntryList) {

            boolean innerFound = hotfixRecordEntry.search(searchStr, casesensitive, saveResults);

            found = found || innerFound;

        }

        if (saveResults) {
            searchFound = found;
        }

        return found;
    }

    @Override
    public int compareTo(HotfixEntry other) {

        int compare = 0;

        HotfixEntryKey thisHotfixEntryKey = getKey();
        HotfixEntryKey otherHotfixEntryKey = other.getKey();

        HotfixIDComparator hotfixIDComparator = new HotfixIDComparator();

        String thisHotfixId = thisHotfixEntryKey.getHotfixId();
        String otherHotfixId = otherHotfixEntryKey.getHotfixId();

        compare = hotfixIDComparator.compare(thisHotfixId, otherHotfixId);

        return compare;

    }

    public String getHotfixEntryData(HotfixColumn hotfixColumn, int columnIndex) {

        String hotfixdata = null;

        if (hotfixColumn.equals(HotfixColumn.ID)) {

            HotfixEntryKey hotfixEntryKey = getKey();
            hotfixdata = hotfixEntryKey.getKey().toString();

        } else if (hotfixColumn.equals(HotfixColumn.HOTFIX_ID)) {

            HotfixEntryKey hotfixEntryKey = getKey();
            hotfixdata = hotfixEntryKey.getHotfixId();

        } else if (hotfixColumn.equals(HotfixColumn.HOTFIX_STATUS)) {

            HotfixStatus hotfixStatus = getHotfixStatus();

            hotfixdata = (hotfixStatus != null) ? hotfixStatus.getDisplayString() : null;

        } else if (hotfixColumn.equals(HotfixColumn.PRODUCT_LABEL)) {

            ProductInfo productInfo = getProductInfo();

            hotfixdata = (productInfo != null) ? productInfo.getProductInfoStr() : null;

        } else if (hotfixColumn.equals(HotfixColumn.PACKAGE_DATE)) {

            hotfixdata = getPackageDate();

        } else if (hotfixColumn.equals(HotfixColumn.JARS_TO_REMOVE)) {

            hotfixdata = getJarsToRemove();

        } else if (hotfixColumn.equals(HotfixColumn.CRITICAL_LEVEL)) {

            CriticalLevel criticalLevel = getCriticalLevel();

            if (criticalLevel != null) {
                hotfixdata = criticalLevel.getDisplayName();
            }

        } else if (hotfixColumn.equals(HotfixColumn.HYBRID)) {

            hotfixdata = Boolean.toString(isHybrid());

        } else {

            List<HotfixRecordEntry> hotfixRecordEntryList;

            hotfixRecordEntryList = getHotfixRecordEntryList();

            if ((columnIndex != -1) && (hotfixRecordEntryList != null) && (hotfixRecordEntryList.size() > 0)) {

                HotfixRecordEntry hotfixRecordEntry = hotfixRecordEntryList.get(0);

                hotfixdata = hotfixRecordEntry.getRecordDataList().get(columnIndex);
            }
        }

        return hotfixdata;
    }

    public Set<HotfixEntry> getBackwardHotfixEntrySet() {

        return Collections.unmodifiableSet(backwardHotfixEntrySet);

        // TreeSet<HotfixEntry> totalBackwardHotfixEntrySet = new TreeSet<>();
        //
        // if (backwardHotfixEntrySet != null) {
        //
        // for (HotfixEntry hotfixEntry : backwardHotfixEntrySet) {
        //
        // boolean added = totalBackwardHotfixEntrySet.add(hotfixEntry);
        //
        // if (added) {
        // recursiveEvaluateBackwardHotfixEntrySet(hotfixEntry, totalBackwardHotfixEntrySet);
        // }
        // }
        // }
        //
        // return Collections.unmodifiableSet(totalBackwardHotfixEntrySet);
    }

    public Set<HotfixEntry> getForwardHotfixEntrySet() {
        return Collections.unmodifiableSet(forwardHotfixEntrySet);
    }

    public void addToBackwardHotfixEntrySet(HotfixEntry hotfixEntry) {
        backwardHotfixEntrySet.add(hotfixEntry);
    }

    public void addToForwardHotfixEntrySet(HotfixEntry hotfixEntry) {
        forwardHotfixEntrySet.add(hotfixEntry);
    }
}
