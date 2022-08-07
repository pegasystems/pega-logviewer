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
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.model.SystemStateError;

public class SystemStateErrorTableModel extends AbstractDataTableModel<SystemStateError, SystemStateErrorTableColumn> {

    private static final long serialVersionUID = 9044208647082744370L;

    private Map<Integer, SystemStateError> dataMap;

    private List<SystemStateErrorTableColumn> columnList;

    public SystemStateErrorTableModel(SystemState systemState) {

        super(null);

        columnList = SystemStateErrorTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<SystemStateError> errorSet = systemState.getErrorSet();

        if (errorSet != null) {
            for (SystemStateError data : errorSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, SystemStateError> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<SystemStateErrorTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(SystemStateError data, SystemStateErrorTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(SystemStateErrorTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(SystemStateErrorTableColumn.NODEID)) {
            columndata = data.getNodeId();
        } else if (dataTableColumn.equals(SystemStateErrorTableColumn.MESSAGE)) {
            columndata = data.getMessage();
        }

        return columndata;
    }

}
