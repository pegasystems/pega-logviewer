/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.markerbar.Marker;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class CompareMarkerModel extends MarkerModel<ScanResultHotfixEntryKey> {

	private static final Log4j2Helper LOG = new Log4j2Helper(CompareMarkerModel.class);

	private SystemScanTableCompareModel systemScanTableCompareModel;

	public CompareMarkerModel(Color markerColor, SystemScanTableCompareModel systemScanTableCompareModel) {

		super(markerColor, systemScanTableCompareModel);

		this.systemScanTableCompareModel = systemScanTableCompareModel;

		resetFilteredMarkerMap();
	}

	@Override
	protected void resetFilteredMarkerMap() {

		clearFilteredMarkerMap();

		List<ScanResultHotfixEntryKey> compareMarkerList = systemScanTableCompareModel.getCompareMarkerList();

		if (compareMarkerList != null) {
			Iterator<ScanResultHotfixEntryKey> iterator = compareMarkerList.iterator();

			while (iterator.hasNext()) {

				ScanResultHotfixEntryKey key = iterator.next();

				addToFilteredMarkerMap(key);
			}
		}
	}

	@Override
	public List<Marker<ScanResultHotfixEntryKey>> getMarkers(ScanResultHotfixEntryKey key) {

		Marker<ScanResultHotfixEntryKey> marker = new Marker<ScanResultHotfixEntryKey>(key, key.toString());

		List<Marker<ScanResultHotfixEntryKey>> markerList = new ArrayList<>();
		markerList.add(marker);

		return markerList;
	}

	@Override
	public void addMarker(Marker<ScanResultHotfixEntryKey> marker) {
		LOG.info("Error: CompareMarkerModel doesnt explictly add marker.");
	}

	@Override
	public void removeMarker(ScanResultHotfixEntryKey key, int index) {
		LOG.info("Error: CompareMarkerModel doesnt explictly remove marker.");
	}

	@Override
	public void clearMarkers() {
		LOG.info("Error: CompareMarkerModel doesnt explictly clear markers.");

	}

}
