/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.logviewer.systemscan.PegaVersion;
import com.pega.gcs.logviewer.systemscan.SystemScanColumn;
import com.pega.gcs.logviewer.systemscan.hybrid.HybridHotfixListProvider;

public class ScanResult {

	private Date catalogTimestamp;

	private Date inventoryTimestamp;

	private List<String> productInfoColumnList;

	private Map<Integer, List<String>> productInfoMap;

	private List<String> hotfixColumnList;

	// temporary map during file read. cleared once post processsing is run
	private Map<String, ScanResultHotfixEntryKey> hotfixIdMap;

	private Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> hotfixEntryMap;

	private int hotfixIdIndex;

	private int pegaVersionIndex;

	private int codeChangeClassnameIndex;

	private int ruleChangeKeyIndex;

	private int schemaChangeDADTKeyIndex;

	public ScanResult() {

		super();

		catalogTimestamp = null;
		inventoryTimestamp = null;
		productInfoColumnList = null;
		hotfixColumnList = null;

		productInfoMap = new HashMap<>();

		hotfixIdMap = new HashMap<>();
		hotfixEntryMap = new HashMap<>();

		hotfixIdIndex = -1;
		pegaVersionIndex = -1;
		codeChangeClassnameIndex = -1;
		ruleChangeKeyIndex = -1;
		schemaChangeDADTKeyIndex = -1;
	}

	public Date getCatalogTimestamp() {
		return catalogTimestamp;
	}

	public void setCatalogTimestamp(Date catalogTimestamp) {
		this.catalogTimestamp = catalogTimestamp;
	}

	public Date getInventoryTimestamp() {
		return inventoryTimestamp;
	}

	public void setInventoryTimestamp(Date inventoryTimestamp) {
		this.inventoryTimestamp = inventoryTimestamp;
	}

	public List<String> getProductInfoColumnList() {
		return productInfoColumnList;
	}

	public void setProductInfoColumnList(List<String> productInfoColumnList) {
		this.productInfoColumnList = productInfoColumnList;
	}

	public void addProductInfoEntry(Integer index, List<String> recordDataList) {

		productInfoMap.put(index, recordDataList);
	}

	public List<String> getHotfixColumnList() {
		return hotfixColumnList;
	}

	public void setHotfixColumnList(List<String> hotfixColumnList) {
		this.hotfixColumnList = hotfixColumnList;

		hotfixIdIndex = getHotfixColumnIndex(SystemScanColumn.HOTFIX_ID);

		pegaVersionIndex = getHotfixColumnIndex(SystemScanColumn.PRODUCT_LABEL);

		codeChangeClassnameIndex = getHotfixColumnIndex(SystemScanColumn.CODE_CHANGE_CLASSNAME);
		ruleChangeKeyIndex = getHotfixColumnIndex(SystemScanColumn.RULE_CHANGE_KEY);
		schemaChangeDADTKeyIndex = getHotfixColumnIndex(SystemScanColumn.SCHEMA_CHANGE_DADT_KEY);

	}

	public Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> getScanResultHotfixEntryMap() {
		return Collections.unmodifiableMap(hotfixEntryMap);
	}

	public void setScanResultHotfixEntryMap(Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> hotfixEntryMap) {
		this.hotfixEntryMap = hotfixEntryMap;
	}

	public void addScanResultHotfixChangeEntry(ScanResultHotfixChangeEntry hotfixChangeEntry) {

		String hotfixId = hotfixChangeEntry.getRecordDataList().get(hotfixIdIndex);
		hotfixId = hotfixId.toUpperCase();

		ScanResultHotfixEntry hotfixEntry;

		ScanResultHotfixEntryKey key = hotfixIdMap.get(hotfixId);

		if (key == null) {

			boolean hybrid = false;

			String pegaVersionStr = hotfixChangeEntry.getRecordDataList().get(pegaVersionIndex);

			PegaVersion pegaVersion = PegaVersion.getPegaVersion(pegaVersionStr);

			if (pegaVersion != null) {

				String pvStr = pegaVersion.getVersion();

				HybridHotfixListProvider hybridHotfixListProvider;
				hybridHotfixListProvider = HybridHotfixListProvider.getInstance();

				List<String> hybridHotfixList = hybridHotfixListProvider.getReleaseHotfixIdMap().get(pvStr);

				if (hybridHotfixList.contains(hotfixId)) {
					hybrid = true;
				}
			}

			Integer index = hotfixIdMap.keySet().size();

			ScanResultHotfixEntryKey hotfixEntryKey;
			hotfixEntryKey = new ScanResultHotfixEntryKey(index, index);

			hotfixEntry = new ScanResultHotfixEntry(hotfixEntryKey, hotfixId, hybrid);

			hotfixEntryMap.put(hotfixEntryKey, hotfixEntry);

			hotfixIdMap.put(hotfixId, hotfixEntryKey);

		} else {
			hotfixEntry = hotfixEntryMap.get(key);
		}

		List<ScanResultHotfixChangeEntry> hotfixChangeEntryList = hotfixEntry.getScanResultHotfixChangeEntryList();

		hotfixChangeEntryList.add(hotfixChangeEntry);
	}

	public Set<ScanResultHotfixEntryKey> getScanResultHotfixEntryKeySet() {
		return hotfixEntryMap.keySet();
	}

	// TODO: multiple calls to this function - try to optimise this.
	public int getHotfixColumnIndex(SystemScanColumn systemScanColumn) {

		int hotfixColumnIndex = -1;

		int counter = 0;

		for (String hotfixColumn : hotfixColumnList) {

			boolean match = false;

			for (String altName : systemScanColumn.getAltNames()) {

				if (hotfixColumn.equals(altName)) {
					match = true;
					break;
				}
			}

			if (match) {
				hotfixColumnIndex = counter;
				break;
			}

			counter++;
		}

		return hotfixColumnIndex;
	}

	public ScanResultHotfixEntry getScanResultHotfixEntry(ScanResultHotfixEntryKey hotfixEntryKey) {

		ScanResultHotfixEntry hotfixEntry;
		hotfixEntry = hotfixEntryMap.get(hotfixEntryKey);

		return hotfixEntry;
	}

	public String getScanResultHotfixEntryData(ScanResultHotfixEntry hotfixEntry, SystemScanColumn systemScanColumn) {

		String hotfixdata = null;

		if (systemScanColumn.equals(SystemScanColumn.ID)) {

			ScanResultHotfixEntryKey hotfixEntryKey = hotfixEntry.getKey();
			int index = hotfixEntryKey.getScanResultHotfixEntryIndex();

			// in case of compare, it will be set -1
			if (index != -1) {
				hotfixdata = String.valueOf(index);
			}
		} else if (systemScanColumn.equals(SystemScanColumn.HYBRID)) {

			hotfixdata = Boolean.toString(hotfixEntry.isHybrid());

		} else {
			int index = getHotfixColumnIndex(systemScanColumn);

			List<ScanResultHotfixChangeEntry> hotfixChangeEntryList;
			hotfixChangeEntryList = hotfixEntry.getScanResultHotfixChangeEntryList();

			if ((index != -1) && (hotfixChangeEntryList != null) && (hotfixChangeEntryList.size() > 0)) {

				ScanResultHotfixChangeEntry hotfixChangeEntry = hotfixChangeEntryList.get(0);

				hotfixdata = hotfixChangeEntry.getRecordDataList().get(index);
			}
		}

		return hotfixdata;
	}

	public String getScanResultHotfixChangeEntryData(ScanResultHotfixChangeEntry hotfixChangeEntry,
			SystemScanColumn systemScanColumn) {

		String hotfixdata = null;

		if (systemScanColumn.equals(SystemScanColumn.ID)) {
			hotfixdata = hotfixChangeEntry.getEntryId().toString();
		} else {
			int index = getHotfixColumnIndex(systemScanColumn);

			if (index != -1) {
				hotfixdata = hotfixChangeEntry.getRecordDataList().get(index);
			}
		}

		return hotfixdata;
	}

	public void postProcessScanResultHotfixEntryList() {

		List<String> hotfixIdList = new ArrayList<>(hotfixIdMap.keySet());
		Collections.sort(hotfixIdList);

		AtomicInteger indexAI = new AtomicInteger(1);

		Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> newScanResultHotfixEntryMap = new HashMap<>();

		for (String hotfixId : hotfixIdList) {

			ScanResultHotfixEntryKey hotfixEntryKey = hotfixIdMap.get(hotfixId);

			ScanResultHotfixEntry hotfixEntry = hotfixEntryMap.get(hotfixEntryKey);

			Integer index = indexAI.getAndIncrement();

			ScanResultHotfixEntryKey newScanResultHotfixEntryKey;
			newScanResultHotfixEntryKey = new ScanResultHotfixEntryKey(index, index);

			hotfixEntry.setScanResultHotfixEntryKey(newScanResultHotfixEntryKey);

			hotfixIdMap.put(hotfixId, newScanResultHotfixEntryKey);
			newScanResultHotfixEntryMap.put(newScanResultHotfixEntryKey, hotfixEntry);

			hotfixEntry.organiseScanResultHotfixEntryList(codeChangeClassnameIndex, ruleChangeKeyIndex,
					schemaChangeDADTKeyIndex);
		}

		hotfixEntryMap.clear();
		hotfixEntryMap.putAll(newScanResultHotfixEntryMap);

		hotfixIdMap.clear(); // clearing to avoid usage later
	}

}
