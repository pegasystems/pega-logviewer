
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.RuleSetVersion;
import com.pega.gcs.logviewer.systemstate.model.RuleSetVersionInfo;

public class RuleSetVersionInfoTableModel
        extends AbstractDataTableModel<RuleSetVersion, RuleSetVersionInfoTableColumn> {

    private static final long serialVersionUID = -2359287752983530526L;

    private Map<Integer, RuleSetVersion> dataMap;

    private List<RuleSetVersionInfoTableColumn> columnList;

    public RuleSetVersionInfoTableModel(RuleSetVersionInfo ruleSetVersionInfo) {

        super(null);

        columnList = RuleSetVersionInfoTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<RuleSetVersion> ruleSetVersionSet = ruleSetVersionInfo.getRuleSetVersionSet();

        if (ruleSetVersionSet != null) {
            for (RuleSetVersion data : ruleSetVersionSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, RuleSetVersion> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<RuleSetVersionInfoTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(RuleSetVersion data, RuleSetVersionInfoTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.RULESETNAME)) {
            columndata = data.getRuleSetName();
        } else if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.RULESETVERSION)) {
            columndata = data.getRuleSetVersion();
        } else if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.ISRULESETVERSIONLOCKED)) {
            Boolean isRuleSetVersionLocked = data.getIsRuleSetVersionLocked();
            columndata = (isRuleSetVersionLocked != null) ? Boolean.toString(isRuleSetVersionLocked) : null;
        } else if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.RULESCOUNT)) {
            Integer rulesCount = data.getRulesCount();
            columndata = (rulesCount != null) ? Integer.toString(rulesCount) : null;
        } else if (dataTableColumn.equals(RuleSetVersionInfoTableColumn.LASTMODIFIED)) {
            columndata = data.getLastModified();
        }

        return columndata;
    }
}
