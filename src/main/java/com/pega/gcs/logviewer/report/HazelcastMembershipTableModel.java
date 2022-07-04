
package com.pega.gcs.logviewer.report;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.model.HazelcastMemberInfo;

public class HazelcastMembershipTableModel
        extends AbstractDataTableModel<HazelcastMemberInfo, HazelcastMembershipTableColumn> {

    private static final long serialVersionUID = 8666540758025302300L;

    private Map<Integer, HazelcastMemberInfo> dataMap;

    private List<HazelcastMembershipTableColumn> columnList;

    public HazelcastMembershipTableModel(List<HazelcastMemberInfo> hazelcastMemberInfoList) {

        super(null);

        columnList = HazelcastMembershipTableColumn.getColumnList();

        dataMap = new HashMap<>();

        if (hazelcastMemberInfoList != null) {
            for (HazelcastMemberInfo data : hazelcastMemberInfoList) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, HazelcastMemberInfo> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<HazelcastMembershipTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(HazelcastMemberInfo data, HazelcastMembershipTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(HazelcastMembershipTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(HazelcastMembershipTableColumn.NAME)) {
            columndata = data.getName();
        } else if (dataTableColumn.equals(HazelcastMembershipTableColumn.HOSTNAME)) {
            columndata = data.getHostname();
        } else if (dataTableColumn.equals(HazelcastMembershipTableColumn.CLUSTER_ADDRESS)) {
            columndata = data.getClusterAddress();
        } else if (dataTableColumn.equals(HazelcastMembershipTableColumn.UUID)) {
            columndata = data.getUuid();
        } else if (dataTableColumn.equals(HazelcastMembershipTableColumn.OPERATING_MODE)) {
            columndata = data.getOperatingMode();
        }

        return columndata;
    }

    @Override
    protected Color getBackground(HazelcastMemberInfo data) {

        Color backgroundColor = super.getBackground(data);

        boolean error = data.isErrorMarker();

        if (error) {
            backgroundColor = Color.RED;
        }

        return backgroundColor;
    }
}
