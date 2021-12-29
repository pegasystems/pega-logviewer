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
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.utilities.Identifiable;

public class HotfixEntry implements Identifiable<HotfixEntryKey>, Comparable<HotfixEntry> {

    private HotfixEntryKey hotfixEntryKey;

    private String hotfixId;

    private HotfixStatus hotfixStatus;

    private ProductInfo productInfo;

    private boolean hybrid;

    private CriticalLevel criticalLevel;

    private Color background;

    private List<HotfixRecordEntry> hotfixRecordEntryList;

    private boolean catalogSearchFound;

    private boolean searchFound;

    private TreeSet<HotfixEntry> backwardHotfixEntrySet;

    private TreeSet<HotfixEntry> forwardHotfixEntrySet;

    // used in catalog scanner
    public HotfixEntry(HotfixEntryKey hotfixEntryKey, String hotfixId, HotfixStatus hotfixStatus,
            ProductInfo productInfo, boolean hybrid) {
        this(hotfixEntryKey, hotfixId, hotfixStatus, productInfo, hybrid, null);
    }

    // used in compare to set diff color
    public HotfixEntry(HotfixEntryKey hotfixEntryKey, String hotfixId, HotfixStatus hotfixStatus,
            ProductInfo productInfo, boolean hybrid, Color background) {
        super();

        this.hotfixEntryKey = hotfixEntryKey;
        this.hotfixId = hotfixId;
        this.hotfixStatus = hotfixStatus;
        this.productInfo = productInfo;
        this.hybrid = hybrid;
        this.criticalLevel = null;

        setBackground(background);

        hotfixRecordEntryList = new ArrayList<>();

        Comparator<HotfixEntry> dependencyComparator = new Comparator<HotfixEntry>() {

            @Override
            public int compare(HotfixEntry o1, HotfixEntry o2) {

                int compare = 0;
                HotfixEntryKey o1HotfixEntryKey = o1.getKey();
                HotfixEntryKey o2HotfixEntryKey = o2.getKey();

                if ((o1HotfixEntryKey != null) && (o2HotfixEntryKey != null)) {

                    compare = o1HotfixEntryKey.getHotfixNumber().compareTo(o2HotfixEntryKey.getHotfixNumber());

                } else {

                    HotfixIDComparator hotfixIDComparator = new HotfixIDComparator();

                    String o1HotfixId = o1.getHotfixId();
                    String o2HotfixId = o2.getHotfixId();

                    compare = hotfixIDComparator.compare(o1HotfixId, o2HotfixId);
                }

                return compare;
            }
        };

        backwardHotfixEntrySet = new TreeSet<>(dependencyComparator);
        forwardHotfixEntrySet = new TreeSet<>(dependencyComparator);
    }

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
        builder.append(", hotfixId=");
        builder.append(hotfixId);
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

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.utilities.Identifiable#getKey()
     */
    @Override
    public HotfixEntryKey getKey() {
        return hotfixEntryKey;
    }

    public String getHotfixId() {
        return hotfixId;
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

        if ((thisHotfixEntryKey != null) && (otherHotfixEntryKey != null)) {

            compare = thisHotfixEntryKey.compareTo(otherHotfixEntryKey);

        } else {

            HotfixIDComparator hotfixIDComparator = new HotfixIDComparator();

            String thisHotfixId = getHotfixId();
            String otherHotfixId = other.getHotfixId();

            compare = hotfixIDComparator.compare(thisHotfixId, otherHotfixId);
        }

        return compare;

    }

    public String getHotfixEntryData(HotfixColumn hotfixColumn, int columnIndex) {

        String hotfixdata = null;

        if (hotfixColumn.equals(HotfixColumn.ID)) {

            HotfixEntryKey hotfixEntryKey = getKey();
            Integer key = hotfixEntryKey.getKey();

            hotfixdata = key.toString();

        } else if (hotfixColumn.equals(HotfixColumn.HOTFIX_ID)) {

            hotfixdata = getHotfixId();

        } else if (hotfixColumn.equals(HotfixColumn.HOTFIX_STATUS)) {

            HotfixStatus hotfixStatus = getHotfixStatus();

            hotfixdata = (hotfixStatus != null) ? hotfixStatus.getDisplayString() : null;

        } else if (hotfixColumn.equals(HotfixColumn.PRODUCT_LABEL)) {

            ProductInfo productInfo = getProductInfo();

            hotfixdata = (productInfo != null) ? productInfo.getProductInfoStr() : null;

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
