/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.diff.EditCommand;
import com.pega.gcs.fringecommon.utilities.diff.Matcher;
import com.pega.gcs.fringecommon.utilities.diff.MyersDifferenceAlgorithm;
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanCompareTask extends SwingWorker<Void, String> {

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanCompareTask.class);

	private static final String PROGRESS_MONITOR_STATUS_CHANGE = "indeterminate";

	private Component parent;

	private SystemScanTableModel systemScanTableModel;

	private SystemScanTable systemScanTableLeft;

	private SystemScanTable systemScanTableRight;

	// use progress monitor only in process() method.
	private ModalProgressMonitor progressMonitor;

	public SystemScanCompareTask(Component parent, SystemScanTableModel systemScanTableModel,
			SystemScanTable systemScanTableLeft, SystemScanTable systemScanTableRight,
			ModalProgressMonitor progressMonitor) {

		super();

		this.parent = parent;
		this.systemScanTableModel = systemScanTableModel;
		this.systemScanTableLeft = systemScanTableLeft;
		this.systemScanTableRight = systemScanTableRight;
		this.progressMonitor = progressMonitor;
	}

	private SystemScanTableModel getSystemScanTableModel() {
		return systemScanTableModel;
	}

	private SystemScanTable getSystemScanTableLeft() {
		return systemScanTableLeft;
	}

	private SystemScanTable getSystemScanTableRight() {
		return systemScanTableRight;
	}

	private ModalProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	@Override
	protected Void doInBackground() throws Exception {

		SystemScanTableModel systemScanTableModel = getSystemScanTableModel();
		SystemScanTable systemScanTableLeft = getSystemScanTableLeft();
		SystemScanTable systemScanTableRight = getSystemScanTableRight();

		// right side model is built freshly for every load of the file
		SystemScanTableCompareModel systemScanTableCompareModelRight;
		systemScanTableCompareModelRight = (SystemScanTableCompareModel) systemScanTableRight.getModel();

		SystemScanMainPanel.loadFile(parent, systemScanTableCompareModelRight, progressMonitor, true);

		// trigger changing progress monitor to indeterminate
		publish(PROGRESS_MONITOR_STATUS_CHANGE);

		// built the left side compare model afresh for every compare
		RecentFile recentFile = systemScanTableModel.getRecentFile();
		SearchData<ScanResultHotfixEntryKey> searchData = systemScanTableModel.getSearchData();
		SystemScanTableCompareModel systemScanTableCompareModelLeft;
		systemScanTableCompareModelLeft = new SystemScanTableCompareModel(recentFile, searchData);

		ScanResult origScanResultLeft = systemScanTableModel.getScanResult();
		ScanResult origScanResultRight = systemScanTableCompareModelRight.getScanResult();

		TreeMap<ScanResultHotfixEntryKey, List<ScanResultHotfixEntryKey>> compareNavIndexMap;
		compareNavIndexMap = new TreeMap<ScanResultHotfixEntryKey, List<ScanResultHotfixEntryKey>>();

		try {

			List<ScanResultHotfixEntryKey> thisMarkerScanResultHotfixEntryKeyList = new ArrayList<>();
			List<ScanResultHotfixEntryKey> otherMarkerScanResultHotfixEntryKeyList = new ArrayList<>();

			Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> thisScanResultHotfixEntryMap;
			thisScanResultHotfixEntryMap = origScanResultLeft.getScanResultHotfixEntryMap();

			Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> otherScanResultHotfixEntryMap;
			otherScanResultHotfixEntryMap = origScanResultRight.getScanResultHotfixEntryMap();

			// THIS
			List<ScanResultHotfixEntryKey> thisScanResultHotfixEntryKeyList = new ArrayList<>();
			List<ScanResultHotfixEntry> thisScanResultHotfixEntryList = new ArrayList<>();

			// OTHER
			List<ScanResultHotfixEntryKey> otherScanResultHotfixEntryKeyList = new ArrayList<ScanResultHotfixEntryKey>();
			List<ScanResultHotfixEntry> otherScanResultHotfixEntryList = new ArrayList<>();

			getKeyAndEntryList(thisScanResultHotfixEntryMap, thisScanResultHotfixEntryKeyList,
					thisScanResultHotfixEntryList);
			getKeyAndEntryList(otherScanResultHotfixEntryMap, otherScanResultHotfixEntryKeyList,
					otherScanResultHotfixEntryList);

			Matcher<ScanResultHotfixEntry> matcher = new Matcher<ScanResultHotfixEntry>() {

				@Override
				public boolean match(ScanResultHotfixEntry o1, ScanResultHotfixEntry o2) {
					return o1.getHotfixId().equals(o2.getHotfixId());
				}
			};

			long before = System.currentTimeMillis();

			List<EditCommand> editScript = MyersDifferenceAlgorithm.diffGreedyLCS(progressMonitor,
					thisScanResultHotfixEntryList, otherScanResultHotfixEntryList, matcher);

			long diff = System.currentTimeMillis() - before;
			DecimalFormat df = new DecimalFormat("#0.000");

			String time = df.format((double) diff / 1E3);

			LOG.info("MyersDifferenceAlgorithm diffGreedyLCS took " + time + "s.");

			Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> scanResultHotfixEntryMapLeft;
			scanResultHotfixEntryMapLeft = new HashMap<>();

			Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> scanResultHotfixEntryMapRight;
			scanResultHotfixEntryMapRight = new HashMap<>();

			int index = 0;
			int indexThis = 0;
			int indexOther = 0;

			ScanResultHotfixEntryKey compareNavIndexKey = null;
			EditCommand prevEC = EditCommand.SNAKE;

			ScanResultHotfixEntryKey teKeyThis;
			ScanResultHotfixEntryKey teKeyOther;
			ScanResultHotfixEntryKey teKey;

			ScanResultHotfixEntry teCompare;
			ScanResultHotfixEntry te;

			Color deleteColor = Color.LIGHT_GRAY;
			Color insertColor = MyColor.LIGHTEST_GREEN;

			for (EditCommand ec : editScript) {

				ScanResultHotfixEntryKey teKeyCompare = null;

				switch (ec) {
				case DELETE:
					// Add compare type to OTHER List
					// OTHER
					teKeyCompare = new ScanResultHotfixEntryKey(index, -1);
					teCompare = new ScanResultHotfixEntry(teKeyCompare, "", false, deleteColor);
					scanResultHotfixEntryMapRight.put(teKeyCompare, teCompare);

					// THIS
					teKeyThis = thisScanResultHotfixEntryKeyList.get(indexThis);
					te = thisScanResultHotfixEntryList.get(indexThis);
					teKey = new ScanResultHotfixEntryKey(index, teKeyThis.getScanResultHotfixEntryIndex());
					te.setScanResultHotfixEntryKey(teKey);
					scanResultHotfixEntryMapLeft.put(teKey, te);
					indexThis++;

					otherMarkerScanResultHotfixEntryKeyList.add(teKeyCompare);

					break;
				case INSERT:
					// Add compare type to THIS List
					// OTHER
					teKeyOther = otherScanResultHotfixEntryKeyList.get(indexOther);
					te = otherScanResultHotfixEntryList.get(indexOther);
					teKey = new ScanResultHotfixEntryKey(index, teKeyOther.getScanResultHotfixEntryIndex());
					te.setScanResultHotfixEntryKey(teKey);
					scanResultHotfixEntryMapRight.put(teKey, te);
					indexOther++;

					// THIS
					teKeyCompare = new ScanResultHotfixEntryKey(index, -1);
					teCompare = new ScanResultHotfixEntry(teKeyCompare, "", false, insertColor);
					scanResultHotfixEntryMapLeft.put(teKeyCompare, teCompare);

					thisMarkerScanResultHotfixEntryKeyList.add(teKeyCompare);

					break;
				case SNAKE:
					// OTHER
					teKeyOther = otherScanResultHotfixEntryKeyList.get(indexOther);
					// te = otherTEM.get(teKeyOther);
					te = otherScanResultHotfixEntryList.get(indexOther);
					teKey = new ScanResultHotfixEntryKey(index, teKeyOther.getScanResultHotfixEntryIndex());
					te.setScanResultHotfixEntryKey(teKey);
					scanResultHotfixEntryMapRight.put(teKey, te);
					indexOther++;

					// THIS
					teKeyThis = thisScanResultHotfixEntryKeyList.get(indexThis);
					// te = thisTEM.get(teKeyThis);
					te = thisScanResultHotfixEntryList.get(indexThis);
					teKey = new ScanResultHotfixEntryKey(index, teKeyThis.getScanResultHotfixEntryIndex());
					te.setScanResultHotfixEntryKey(teKey);
					scanResultHotfixEntryMapLeft.put(teKey, te);
					indexThis++;

					break;
				default:
					break;

				}

				if ((!prevEC.equals(ec)) && (teKeyCompare != null)) {

					compareNavIndexKey = teKeyCompare;

					List<ScanResultHotfixEntryKey> compareIndexList = new ArrayList<ScanResultHotfixEntryKey>();
					compareIndexList.add(teKeyCompare);

					compareNavIndexMap.put(compareNavIndexKey, compareIndexList);

				} else if ((compareNavIndexKey != null) && (teKeyCompare != null)) {

					List<ScanResultHotfixEntryKey> compareIndexList;
					compareIndexList = compareNavIndexMap.get(compareNavIndexKey);

					compareIndexList.add(teKeyCompare);
				}

				prevEC = ec;
				index++;
			}

			ScanResult scanResultLeft = new ScanResult();
			ScanResult scanResultRight = new ScanResult();

			scanResultLeft.setCatalogTimestamp(origScanResultLeft.getCatalogTimestamp());
			scanResultRight.setCatalogTimestamp(origScanResultRight.getCatalogTimestamp());

			scanResultLeft.setInventoryTimestamp(origScanResultLeft.getInventoryTimestamp());
			scanResultRight.setInventoryTimestamp(origScanResultRight.getInventoryTimestamp());

			scanResultLeft.setHotfixColumnList(origScanResultLeft.getHotfixColumnList());
			scanResultRight.setHotfixColumnList(origScanResultRight.getHotfixColumnList());

			scanResultLeft.setScanResultHotfixEntryMap(scanResultHotfixEntryMapLeft);
			scanResultRight.setScanResultHotfixEntryMap(scanResultHotfixEntryMapRight);

			systemScanTableCompareModelLeft.setScanResult(scanResultLeft);
			systemScanTableCompareModelRight.setScanResult(scanResultRight);

			systemScanTableCompareModelLeft.setCompareMarkerList(thisMarkerScanResultHotfixEntryKeyList);
			systemScanTableCompareModelRight.setCompareMarkerList(otherMarkerScanResultHotfixEntryKeyList);

			systemScanTableCompareModelLeft.setCompareNavIndexMap(compareNavIndexMap);

			LOG.info("SystemScanTable CompareTask done " + compareNavIndexMap.size() + " chunks found");

			// set the left table model as compare model
			systemScanTableLeft.setModel(systemScanTableCompareModelLeft);

		} catch (Exception e) {
			LOG.error("Exception in compare task", e);
		} finally {
			// cleanup
			System.gc();
		}

		return null;
	}

	@Override
	protected void process(List<String> chunks) {

		if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
			return;
		}

		Collections.sort(chunks);

		String changeStatus = chunks.get(chunks.size() - 1);

		ModalProgressMonitor progressMonitor = getProgressMonitor();

		if ((changeStatus.equals(PROGRESS_MONITOR_STATUS_CHANGE))
				&& ((progressMonitor != null) && (!progressMonitor.isIndeterminate()))) {

			progressMonitor.setIndeterminate(true);
			progressMonitor.setNote("Comparing ...");
			progressMonitor.show();

		}
	}

	private void getKeyAndEntryList(Map<ScanResultHotfixEntryKey, ScanResultHotfixEntry> scanResultHotfixEntryMap,
			List<ScanResultHotfixEntryKey> scanResultHotfixEntryKeyList,
			List<ScanResultHotfixEntry> scanResultHotfixEntryList) {

		scanResultHotfixEntryKeyList.clear();
		scanResultHotfixEntryList.clear();

		for (Map.Entry<ScanResultHotfixEntryKey, ScanResultHotfixEntry> entry : scanResultHotfixEntryMap.entrySet()) {

			ScanResultHotfixEntryKey scanResultHotfixEntryKey = entry.getKey();

			if (scanResultHotfixEntryKey.getScanResultHotfixEntryIndex() != -1) {

				ScanResultHotfixEntry scanResultHotfixEntry = entry.getValue();

				scanResultHotfixEntryKeyList.add(scanResultHotfixEntryKey);
				scanResultHotfixEntryList.add(scanResultHotfixEntry);

			}
		}
	}

}
