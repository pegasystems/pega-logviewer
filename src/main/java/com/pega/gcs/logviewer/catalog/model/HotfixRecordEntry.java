/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HotfixRecordEntry implements Comparable<HotfixRecordEntry> {

    private Integer entryId;

    private List<String> recordDataList;

    private String changeKey;

    private List<AtomicBoolean> catalogSearchList;

    private List<AtomicBoolean> searchList;

    public HotfixRecordEntry(Integer entryId, List<String> recordDataList) {

        super();

        this.entryId = entryId;
        this.recordDataList = recordDataList;
        this.changeKey = null;

        catalogSearchList = new ArrayList<>();
        searchList = new ArrayList<>();

        for (int i = 0; i < recordDataList.size(); i++) {

            AtomicBoolean catalogSearch = new AtomicBoolean(false);
            AtomicBoolean search = new AtomicBoolean(false);

            catalogSearchList.add(catalogSearch);
            searchList.add(search);
        }
    }

    // used for reconfigure the is after sorting
    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public List<String> getRecordDataList() {
        return recordDataList;
    }

    public String getChangeKey() {
        return changeKey;
    }

    public void setChangeKey(String changeKey) {
        this.changeKey = changeKey;
    }

    public boolean isCatalogSearchFound(int columnIndex) {

        boolean searchFound = false;

        searchFound = isSearchFoundInner(columnIndex, catalogSearchList);

        return searchFound;
    }

    public void clearCatalogSearch() {

        clearSearchInner(catalogSearchList);
    }

    public boolean catalogSearch(String searchStr) {
        return searchInner(searchStr, false, true, catalogSearchList);
    }

    public boolean isSearchFound(int columnIndex) {

        boolean searchFound = false;

        searchFound = isSearchFoundInner(columnIndex, searchList);

        return searchFound;
    }

    public void clearSearch() {

        clearSearchInner(searchList);
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
        return searchInner(searchStr, casesensitive, saveResults, searchList);
    }

    private boolean isSearchFoundInner(int index, List<AtomicBoolean> searchList) {

        boolean searchFound = false;

        // in case of ID column, index will be -1
        if (index != -1) {
            searchFound = searchList.get(index).get();
        } else {
            for (AtomicBoolean search : searchList) {
                if (search.get()) {
                    searchFound = true;
                    break;
                }
            }
        }

        return searchFound;
    }

    private void clearSearchInner(List<AtomicBoolean> searchList) {

        for (AtomicBoolean search : searchList) {
            search.set(false);
        }
    }

    private boolean searchInner(String searchStr, boolean casesensitive, boolean saveResults,
            List<AtomicBoolean> searchList) {

        boolean found = false;

        int index = 0;

        for (String recordData : recordDataList) {

            if (recordData != null) {

                if (!casesensitive) {
                    recordData = recordData.toUpperCase();
                }

                if (recordData.contains(searchStr)) {

                    found = true;

                    if (saveResults) {
                        searchList.get(index).set(true);
                    }
                }
            }

            index++;
        }

        return found;
    }

    @Override
    public String toString() {
        return "HotfixRecordEntry [recordDataList=" + recordDataList + "]";
    }

    @Override
    public int compareTo(HotfixRecordEntry other) {
        return this.getEntryId().compareTo(other.getEntryId());
    }

    public String getHotfixRecordEntryData(HotfixColumn hotfixColumn, int columnIndex) {

        String hotfixdata = null;

        if (hotfixColumn.equals(HotfixColumn.ID)) {

            hotfixdata = getEntryId().toString();

        } else {

            if (columnIndex != -1) {
                hotfixdata = getRecordDataList().get(columnIndex);
            }
        }

        return hotfixdata;
    }

    public String getHotfixRecordEntryData(int columnIndex) {

        String hotfixRecordEntryDataString = null;

        List<String> recordDataList = getRecordDataList();

        hotfixRecordEntryDataString = recordDataList.get(columnIndex);

        return hotfixRecordEntryDataString;

    }

    public String getCodeChangeKey(int codeChangeClassIndex, int codeChangePackageIndex) {

        String codeChangeKey = null;

        List<String> recordDataList = getRecordDataList();

        String codeChangeClass = recordDataList.get(codeChangeClassIndex);
        String codeChangePackage = recordDataList.get(codeChangePackageIndex);

        if ((codeChangeClass != null) && (!"".equals(codeChangeClass))) {

            codeChangeClass = codeChangeClass.split("\\$", 0)[0];
            codeChangeClass = codeChangeClass.split("\\.", 0)[0];

            StringBuilder sb = new StringBuilder();

            if ((codeChangePackage != null) && (!"".equals(codeChangePackage))) {
                sb.append(codeChangePackage);
                sb.append(".");
            }

            sb.append(codeChangeClass);

            codeChangeKey = sb.toString();
        }

        return codeChangeKey;

    }

}
