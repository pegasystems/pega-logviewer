/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.model;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanResultHotfixChangeEntry implements Comparable<ScanResultHotfixChangeEntry> {

	private Integer entryId;

	private List<String> recordDataList;

	private List<AtomicBoolean> searchList;

	public ScanResultHotfixChangeEntry(Integer entryId, List<String> recordDataList) {

		super();

		this.entryId = entryId;
		this.recordDataList = recordDataList;

		searchList = new LinkedList<>();

		for (int i = 0; i < recordDataList.size(); i++) {
			AtomicBoolean ab = new AtomicBoolean(false);
			searchList.add(ab);
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

	public boolean isSearchFound(int index) {

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

	public void setSearchFound(boolean searchFound) {

		for (AtomicBoolean search : searchList) {
			search.set(searchFound);
		}
	}

	public boolean search(String searchStr) {

		boolean found = false;

		int index = 0;

		for (String recordData : recordDataList) {

			if (recordData.indexOf(searchStr) != -1) {
				searchList.get(index).set(true);
				found = true;
			}

			index++;
		}

		return found;
	}

	@Override
	public String toString() {
		return "ScanResultHotfixChangeEntry [recordDataList=" + recordDataList + "]";
	}

	@Override
	public int compareTo(ScanResultHotfixChangeEntry o) {
		return this.getEntryId().compareTo(o.getEntryId());
	}

}
