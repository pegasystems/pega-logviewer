/*******************************************************************************
 * Copyright (c) 2022 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.HostNodeSetting;

public class HostNodeSettingTableModel extends AbstractDataTableModel<HostNodeSetting, HostNodeSettingTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, HostNodeSetting> dataMap;

    private List<HostNodeSettingTableColumn> columnList;

    public HostNodeSettingTableModel(TreeSet<HostNodeSetting> searchIndexHostNodeSettingSet) {

        super(null);

        columnList = HostNodeSettingTableColumn.getColumnList();

        dataMap = new HashMap<>();

        if (searchIndexHostNodeSettingSet != null) {
            for (HostNodeSetting data : searchIndexHostNodeSettingSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, HostNodeSetting> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<HostNodeSettingTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(HostNodeSetting data, HostNodeSettingTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(HostNodeSettingTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(HostNodeSettingTableColumn.HOSTNODEID)) {
            columndata = data.getHostNodeID();
        } else if (dataTableColumn.equals(HostNodeSettingTableColumn.FILEDIRECTORY)) {
            columndata = data.getFileDirectory();
        } else if (dataTableColumn.equals(HostNodeSettingTableColumn.HOSTNODESTATUS)) {
            columndata = data.getHostNodeStatus();
        } else if (dataTableColumn.equals(HostNodeSettingTableColumn.HOSTNODEINDEXERSTATUS)) {
            columndata = data.getHostNodeIndexerStatus();
        }

        return columndata;
    }
}
