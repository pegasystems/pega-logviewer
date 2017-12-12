/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.model;

class ScanResultHotfixEntrySort implements Comparable<ScanResultHotfixEntrySort> {

	private ScanResultHotfixChangeEntry scanResultHotfixChangeEntry;

	private String scanResultHotfixEntryData;

	protected ScanResultHotfixEntrySort(ScanResultHotfixChangeEntry scanResultHotfixChangeEntry,
			String scanResultHotfixEntryData) {

		super();

		this.scanResultHotfixChangeEntry = scanResultHotfixChangeEntry;
		this.scanResultHotfixEntryData = scanResultHotfixEntryData;
	}

	protected ScanResultHotfixChangeEntry getScanResultHotfixEntry() {
		return scanResultHotfixChangeEntry;
	}

	protected String getScanResultHotfixEntryData() {
		return scanResultHotfixEntryData;
	}

	@Override
	public int compareTo(ScanResultHotfixEntrySort o) {
		return this.getScanResultHotfixEntryData().compareTo(o.getScanResultHotfixEntryData());
	}

}