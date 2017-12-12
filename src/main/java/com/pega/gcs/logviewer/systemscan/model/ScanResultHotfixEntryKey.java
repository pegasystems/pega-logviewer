/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.model;

import java.io.Serializable;

public class ScanResultHotfixEntryKey implements Comparable<ScanResultHotfixEntryKey>, Serializable {

	private static final long serialVersionUID = -7168939175693927250L;

	private int id;

	// -1 set in case of compare events
	private int scanResultHotfixEntryIndex;

	// for kyro
	@SuppressWarnings("unused")
	private ScanResultHotfixEntryKey() {
		super();
	}
	
	public ScanResultHotfixEntryKey(int id, int scanResultHotfixEntryIndex) {
		super();
		this.id = id;
		this.scanResultHotfixEntryIndex = scanResultHotfixEntryIndex;
	}

	@Override
	public int compareTo(ScanResultHotfixEntryKey o) {

		Integer thisScanResultHotfixEntryIndex = getScanResultHotfixEntryIndex();
		Integer otherScanResultHotfixEntryIndex = o.getScanResultHotfixEntryIndex();

		if ((thisScanResultHotfixEntryIndex != -1) && (otherScanResultHotfixEntryIndex != -1)) {
			return thisScanResultHotfixEntryIndex.compareTo(otherScanResultHotfixEntryIndex);
		} else {
			return Integer.valueOf(getId()).compareTo(Integer.valueOf(o.getId()));
		}

	}

	public int getId() {
		return id;
	}

	public int getScanResultHotfixEntryIndex() {
		return scanResultHotfixEntryIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + scanResultHotfixEntryIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ScanResultHotfixEntryKey other = (ScanResultHotfixEntryKey) obj;

		if (scanResultHotfixEntryIndex != -1) {
			if (scanResultHotfixEntryIndex != other.scanResultHotfixEntryIndex) {
				return false;
			}
		} else if (id != other.id) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "ScanResultHotfixEntryKey [id=" + id + ", scanResultHotfixEntryIndex=" + scanResultHotfixEntryIndex
				+ "]";
	}

}
