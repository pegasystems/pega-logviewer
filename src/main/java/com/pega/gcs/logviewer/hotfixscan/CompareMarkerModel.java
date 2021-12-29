/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pega.gcs.fringecommon.guiutilities.markerbar.Marker;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class CompareMarkerModel extends MarkerModel<HotfixEntryKey> {

    private static final Log4j2Helper LOG = new Log4j2Helper(CompareMarkerModel.class);

    private HotfixScanTableCompareModel hotfixScanTableCompareModel;

    public CompareMarkerModel(Color markerColor, HotfixScanTableCompareModel hotfixScanTableCompareModel) {

        super(markerColor, hotfixScanTableCompareModel);

        this.hotfixScanTableCompareModel = hotfixScanTableCompareModel;

        resetFilteredMarkerMap();
    }

    @Override
    protected void resetFilteredMarkerMap() {

        clearFilteredMarkerMap();

        List<CompareHotfixEntryKey> compareMarkerList = hotfixScanTableCompareModel.getCompareMarkerList();

        if (compareMarkerList != null) {

            Iterator<CompareHotfixEntryKey> iterator = compareMarkerList.iterator();

            while (iterator.hasNext()) {

                HotfixEntryKey key = iterator.next();

                addToFilteredMarkerMap(key);
            }
        }
    }

    @Override
    public List<Marker<HotfixEntryKey>> getMarkers(HotfixEntryKey key) {

        Marker<HotfixEntryKey> marker = new Marker<HotfixEntryKey>(key, key.toString());

        List<Marker<HotfixEntryKey>> markerList = new ArrayList<>();
        markerList.add(marker);

        return markerList;
    }

    @Override
    public void addMarker(Marker<HotfixEntryKey> marker) {
        LOG.info("Error: CompareMarkerModel doesnt explictly add marker.");
    }

    @Override
    public void removeMarker(HotfixEntryKey key, int index) {
        LOG.info("Error: CompareMarkerModel doesnt explictly remove marker.");
    }

    @Override
    public void clearMarkers() {
        LOG.info("Error: CompareMarkerModel doesnt explictly clear markers.");

    }

}
