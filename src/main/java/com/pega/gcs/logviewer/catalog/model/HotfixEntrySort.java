/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

class HotfixEntrySort implements Comparable<HotfixEntrySort> {

    private HotfixRecordEntry hotfixRecordEntry;

    private String hotfixEntrySortColData;

    protected HotfixEntrySort(HotfixRecordEntry hotfixRecordEntry, String hotfixEntrySortColData) {

        super();

        this.hotfixRecordEntry = hotfixRecordEntry;
        this.hotfixEntrySortColData = hotfixEntrySortColData;
    }

    protected HotfixRecordEntry getHotfixRecordEntry() {
        return hotfixRecordEntry;
    }

    protected String getHotfixEntrySortColData() {
        return hotfixEntrySortColData;
    }

    @Override
    public int compareTo(HotfixEntrySort other) {
        return this.getHotfixEntrySortColData().compareTo(other.getHotfixEntrySortColData());
    }

}