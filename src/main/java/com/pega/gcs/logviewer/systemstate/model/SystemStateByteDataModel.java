
package com.pega.gcs.logviewer.systemstate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemStateByteDataModel {

    private ArchiveEntryByteData systemStateJsonByteData;

    private List<ArchiveEntryByteData> otherEntryByteDataList;

    private Map<String, List<ArchiveEntryByteData>> nodeEntryByteDataMap;

    public SystemStateByteDataModel() {
        systemStateJsonByteData = null;
        otherEntryByteDataList = new ArrayList<>();
        nodeEntryByteDataMap = new HashMap<>();
    }

    public ArchiveEntryByteData getSystemStateJsonByteData() {
        return systemStateJsonByteData;
    }

    public void setSystemStateJsonByteData(ArchiveEntryByteData systemStateJsonByteData) {
        this.systemStateJsonByteData = systemStateJsonByteData;
    }

    public void addOtherEntryByte(String key, byte[] byteArray) {

        ArchiveEntryByteData archiveEntryByteData = new ArchiveEntryByteData(key, byteArray);

        otherEntryByteDataList.add(archiveEntryByteData);
    }

    public List<ArchiveEntryByteData> getOtherEntryByteDataList() {
        return Collections.unmodifiableList(otherEntryByteDataList);
    }

    public Map<String, List<ArchiveEntryByteData>> getnodeEntryByteDataMap() {
        return Collections.unmodifiableMap(nodeEntryByteDataMap);
    }

    public void addNodeEntryByte(String nodeId, ArchiveEntryByteData archiveEntryByteData) {

        List<ArchiveEntryByteData> nodeEntryByteDataList = nodeEntryByteDataMap.get(nodeId);

        if (nodeEntryByteDataList == null) {

            nodeEntryByteDataList = new ArrayList<>();

            nodeEntryByteDataMap.put(nodeId, nodeEntryByteDataList);
        }

        nodeEntryByteDataList.add(archiveEntryByteData);
    }

}
