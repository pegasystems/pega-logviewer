
package com.pega.gcs.logviewer.systemstate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AnalysisMarkerListNodeMap {

    Map<String, List<AnalysisMarker>> analysisMarkerListNodeMap;

    public AnalysisMarkerListNodeMap() {
        analysisMarkerListNodeMap = new TreeMap<>();
    }

    public List<AnalysisMarker> getAnalysisMarkerList(String nodeId) {

        List<AnalysisMarker> analysisMarkerList = analysisMarkerListNodeMap.get(nodeId);

        return (analysisMarkerList != null) ? Collections.unmodifiableList(analysisMarkerList) : null;
    }

    public List<AnalysisMarker> getAnalysisMarkerList() {

        List<AnalysisMarker> analysisMarkerList = new ArrayList<>();

        for (List<AnalysisMarker> amList : analysisMarkerListNodeMap.values()) {
            analysisMarkerList.addAll(amList);
        }

        return analysisMarkerList;

    }

    public void addAnalysisMarker(AnalysisMarker analysisMarker) {

        if (analysisMarker != null) {

            String nodeId = analysisMarker.getNodeId();

            List<AnalysisMarker> analysisMarkerList = analysisMarkerListNodeMap.get(nodeId);

            if (analysisMarkerList == null) {

                analysisMarkerList = new ArrayList<>();

                analysisMarkerListNodeMap.put(nodeId, analysisMarkerList);
            }

            analysisMarkerList.add(analysisMarker);

        }
    }

    public void regenrateIndexes() {
        // index the analysisMarkerList
        AtomicInteger index = new AtomicInteger(0);

        for (List<AnalysisMarker> analysisMarkerList : analysisMarkerListNodeMap.values()) {

            for (AnalysisMarker analysisMarker : analysisMarkerList) {
                analysisMarker.setIndex(index.incrementAndGet());
            }
        }
    }
}
