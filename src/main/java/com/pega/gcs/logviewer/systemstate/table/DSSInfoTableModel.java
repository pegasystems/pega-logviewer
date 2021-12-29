
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.DSSInfo;
import com.pega.gcs.logviewer.systemstate.model.Setting;

public class DSSInfoTableModel extends AbstractDataTableModel<Setting, DSSInfoTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, Setting> dataMap;

    private List<DSSInfoTableColumn> columnList;

    public DSSInfoTableModel(DSSInfo dassInfo) {

        columnList = DSSInfoTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<Setting> settingSet = dassInfo.getSettingSet();

        if (settingSet != null) {
            for (Setting data : settingSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, Setting> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<DSSInfoTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(Setting data, DSSInfoTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(DSSInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(DSSInfoTableColumn.RULESET)) {
            columndata = data.getRulesetName();
        } else if (dataTableColumn.equals(DSSInfoTableColumn.NAME)) {
            columndata = data.getSettingName();
        } else if (dataTableColumn.equals(DSSInfoTableColumn.VALUE)) {
            columndata = data.getValue();
        }

        return columndata;
    }
}
