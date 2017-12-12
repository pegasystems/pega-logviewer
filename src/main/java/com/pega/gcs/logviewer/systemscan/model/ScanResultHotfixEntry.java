/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.utilities.Identifiable;

public class ScanResultHotfixEntry implements Identifiable<ScanResultHotfixEntryKey> {

	private ScanResultHotfixEntryKey scanResultHotfixEntryKey;

	private String hotfixId;

	private boolean hybrid;

	private Color background;

	private List<ScanResultHotfixChangeEntry> scanResultHotfixChangeEntryList;

	private boolean searchFound;

	public ScanResultHotfixEntry(ScanResultHotfixEntryKey scanResultHotfixEntryKey, String hotfixId, boolean hybrid) {

		this(scanResultHotfixEntryKey, hotfixId, hybrid, (hybrid) ? MyColor.LIGHTEST_RED : MyColor.LIGHTEST_LIGHT_GRAY);
	}

	public ScanResultHotfixEntry(ScanResultHotfixEntryKey scanResultHotfixEntryKey, String hotfixId, boolean hybrid,
			Color background) {
		super();

		this.scanResultHotfixEntryKey = scanResultHotfixEntryKey;
		this.hotfixId = hotfixId;
		this.hybrid = hybrid;
		this.background = background;

		scanResultHotfixChangeEntryList = new ArrayList<>();
	}

	public List<ScanResultHotfixChangeEntry> getScanResultHotfixChangeEntryList() {
		return scanResultHotfixChangeEntryList;
	}

	public void setScanResultHotfixEntryKey(ScanResultHotfixEntryKey scanResultHotfixEntryKey) {
		this.scanResultHotfixEntryKey = scanResultHotfixEntryKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pega.gcs.fringecommon.utilities.Identifiable#getKey()
	 */
	@Override
	public ScanResultHotfixEntryKey getKey() {
		return scanResultHotfixEntryKey;
	}

	public String getHotfixId() {
		return hotfixId;
	}

	public boolean isHybrid() {
		return hybrid;
	}

	public Color getBackground() {
		return background;
	}

	public boolean isSearchFound() {
		return searchFound;
	}

	public void setSearchFound(boolean searchFound) {

		this.searchFound = searchFound;

		for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : scanResultHotfixChangeEntryList) {

			scanResultHotfixChangeEntry.setSearchFound(searchFound);

		}
	}

	public void organiseScanResultHotfixEntryList(int codeChangeClassnameIndex, int ruleChangeKeyIndex,
			int schemaChangeDADTKeyIndex) {

		List<ScanResultHotfixEntrySort> codeChangeClassnameSRHESList = new ArrayList<>(); // CODE_CHANGE_CLASSNAME
		List<ScanResultHotfixEntrySort> ruleChangeKeySRHESList = new ArrayList<>();// RULE_CHANGE_KEY
		List<ScanResultHotfixEntrySort> schemaChangeDADTKeySRHESList = new ArrayList<>(); // SCHEMA_CHANGE_DADT_KEY
		List<ScanResultHotfixChangeEntry> otherSRHESList = new ArrayList<>();

		for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : scanResultHotfixChangeEntryList) {

			ScanResultHotfixEntrySort scanResultHotfixEntrySort = null;

			List<String> recordDataList = scanResultHotfixChangeEntry.getRecordDataList();

			String codeChangeClassName = recordDataList.get(codeChangeClassnameIndex);

			if ((codeChangeClassName != null) && (!"".equals(codeChangeClassName))) {

				scanResultHotfixEntrySort = new ScanResultHotfixEntrySort(scanResultHotfixChangeEntry,
						codeChangeClassName);

				codeChangeClassnameSRHESList.add(scanResultHotfixEntrySort);

			} else {

				String ruleChangeKey = recordDataList.get(ruleChangeKeyIndex);

				if ((ruleChangeKey != null) && (!"".equals(ruleChangeKey))) {

					scanResultHotfixEntrySort = new ScanResultHotfixEntrySort(scanResultHotfixChangeEntry,
							ruleChangeKey);

					ruleChangeKeySRHESList.add(scanResultHotfixEntrySort);
				} else {

					String schemaChangeDADTKey = recordDataList.get(schemaChangeDADTKeyIndex);

					if ((schemaChangeDADTKey != null) && (!"".equals(schemaChangeDADTKey))) {

						scanResultHotfixEntrySort = new ScanResultHotfixEntrySort(scanResultHotfixChangeEntry,
								schemaChangeDADTKey);

						schemaChangeDADTKeySRHESList.add(scanResultHotfixEntrySort);

					} else {

						otherSRHESList.add(scanResultHotfixChangeEntry);
					}
				}

			}

		}

		Collections.sort(codeChangeClassnameSRHESList);
		Collections.sort(ruleChangeKeySRHESList);
		Collections.sort(schemaChangeDADTKeySRHESList);
		Collections.sort(otherSRHESList);

		AtomicInteger index = new AtomicInteger(1);

		for (ScanResultHotfixEntrySort scanResultHotfixEntrySort : codeChangeClassnameSRHESList) {

			ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixEntrySort
					.getScanResultHotfixEntry();

			scanResultHotfixChangeEntry.setEntryId(index.getAndIncrement());
		}

		for (ScanResultHotfixEntrySort scanResultHotfixEntrySort : ruleChangeKeySRHESList) {

			ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixEntrySort
					.getScanResultHotfixEntry();

			scanResultHotfixChangeEntry.setEntryId(index.getAndIncrement());
		}

		for (ScanResultHotfixEntrySort scanResultHotfixEntrySort : schemaChangeDADTKeySRHESList) {

			ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = scanResultHotfixEntrySort
					.getScanResultHotfixEntry();

			scanResultHotfixChangeEntry.setEntryId(index.getAndIncrement());
		}

		for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : otherSRHESList) {

			scanResultHotfixChangeEntry.setEntryId(index.getAndIncrement());
		}

		Collections.sort(scanResultHotfixChangeEntryList);
	}

	public boolean search(String searchStr) {

		boolean found = false;

		for (ScanResultHotfixChangeEntry scanResultHotfixChangeEntry : scanResultHotfixChangeEntryList) {

			boolean innerFound = scanResultHotfixChangeEntry.search(searchStr);

			found = found || innerFound;

		}

		searchFound = found;
		return found;
	}
}
