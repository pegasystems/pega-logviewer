/*******************************************************************************
 * Copyright (c) 2022 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.systemstate.table;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.systemstate.model.JVMInfo;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;

public class NodeStateTableModel extends AbstractDataTableModel<NodeState, NodeStateTableColumn> {

    private static final long serialVersionUID = -2739513116196768882L;

    private Map<Integer, NodeState> dataMap;

    private List<NodeStateTableColumn> columnList;

    private Map<NodeState, Integer> nodeLookupMap;

    private Map<String, List<Integer>> nodeTypeIndexMap;

    public NodeStateTableModel(SystemState systemState) {

        super(null);

        columnList = NodeStateTableColumn.getColumnList();

        dataMap = new HashMap<>();

        nodeLookupMap = new HashMap<>();

        nodeTypeIndexMap = new TreeMap<>();

        nodeTypeIndexMap.put("", null);

        AtomicInteger indexAi = new AtomicInteger(0);

        for (NodeState nodeState : systemState.getNodeStateCollection()) {

            Integer index = indexAi.incrementAndGet();

            dataMap.put(index, nodeState);
            nodeLookupMap.put(nodeState, index);

            JVMInfo jvmInfo = nodeState.getJvmInfo();

            List<String> nodeTypeList = (jvmInfo != null) ? jvmInfo.getNodeTypeList() : null;

            if (nodeTypeList != null) {

                for (String nodeType : nodeTypeList) {

                    List<Integer> nodeIndexList = nodeTypeIndexMap.get(nodeType);

                    if (nodeIndexList == null) {
                        nodeIndexList = new ArrayList<Integer>();
                        nodeTypeIndexMap.put(nodeType, nodeIndexList);
                    }

                    nodeIndexList.add(index);
                }
            }
        }

        reset();
    }

    @Override
    protected Map<Integer, NodeState> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<NodeStateTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(NodeState data, NodeStateTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(NodeStateTableColumn.SNO)) {
            columndata = Integer.toString(nodeLookupMap.get(data));
        } else if (dataTableColumn.equals(NodeStateTableColumn.NODEID)) {
            columndata = data.getNodeId();
        } else if (dataTableColumn.equals(NodeStateTableColumn.NODETYPE)) {
            JVMInfo jvmInfo = data.getJvmInfo();
            List<String> nodeTypeList = (jvmInfo != null) ? jvmInfo.getNodeTypeList() : null;
            columndata = GeneralUtilities.getCollectionAsSeperatedValues(nodeTypeList, null, false);
        } else if (dataTableColumn.equals(NodeStateTableColumn.HEAP_YOUNG_GEN)) {
            JVMInfo jvmInfo = data.getJvmInfo();
            columndata = (jvmInfo != null) ? jvmInfo.getJvmHeapYoungGen() : null;
        } else if (dataTableColumn.equals(NodeStateTableColumn.HEAP_INITIAL)) {
            JVMInfo jvmInfo = data.getJvmInfo();
            columndata = (jvmInfo != null) ? jvmInfo.getJvmHeapInitialSize() : null;
        } else if (dataTableColumn.equals(NodeStateTableColumn.HEAP_MAX)) {
            JVMInfo jvmInfo = data.getJvmInfo();
            columndata = (jvmInfo != null) ? jvmInfo.getJvmHeapMaximumSize() : null;
        } else if (dataTableColumn.equals(NodeStateTableColumn.THREAD_STACK_SIZE)) {
            JVMInfo jvmInfo = data.getJvmInfo();
            columndata = (jvmInfo != null) ? jvmInfo.getJvmThreadStackSize() : null;
        }

        return columndata;
    }

    @Override
    protected Color getBackground(NodeState data) {

        Color backgroundColor = super.getBackground(data);

        boolean error = data.hasError();

        if (error) {
            backgroundColor = Color.RED;
        }

        return backgroundColor;
    }

    public Set<String> getNodeTypeSet() {
        return Collections.unmodifiableSet(nodeTypeIndexMap.keySet());
    }

    public void applyFilter(String nodeType) {

        List<Integer> ftmEntryKeyList = getFtmEntryKeyList();
        ftmEntryKeyList.clear();

        List<Integer> nodeIndexList = nodeTypeIndexMap.get(nodeType);

        if (nodeIndexList != null) {

            ftmEntryKeyList.addAll(nodeIndexList);

        } else {

            Map<Integer, NodeState> dataMap = getDataMap();
            ftmEntryKeyList.addAll(dataMap.keySet());
        }

        Collections.sort(ftmEntryKeyList);

        fireTableDataChanged();
    }

}
