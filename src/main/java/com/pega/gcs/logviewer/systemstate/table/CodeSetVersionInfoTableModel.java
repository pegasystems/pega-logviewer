
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.CodeSetVersion;
import com.pega.gcs.logviewer.systemstate.model.CodeSetVersionInfo;

public class CodeSetVersionInfoTableModel
        extends AbstractDataTableModel<CodeSetVersion, CodeSetVersionInfoTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, CodeSetVersion> dataMap;

    private List<CodeSetVersionInfoTableColumn> columnList;

    public CodeSetVersionInfoTableModel(CodeSetVersionInfo codeSetVersionInfo) {

        columnList = CodeSetVersionInfoTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<CodeSetVersion> codeSetVersionSet = codeSetVersionInfo.getCodeSetVersionSet();

        if (codeSetVersionSet != null) {
            for (CodeSetVersion data : codeSetVersionSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, CodeSetVersion> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<CodeSetVersionInfoTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(CodeSetVersion data, CodeSetVersionInfoTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(CodeSetVersionInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(CodeSetVersionInfoTableColumn.CODESETNAME)) {
            columndata = data.getCodeSetName();
        } else if (dataTableColumn.equals(CodeSetVersionInfoTableColumn.CODESETVERSION)) {
            columndata = data.getCodesetVersion();
        } else if (dataTableColumn.equals(CodeSetVersionInfoTableColumn.CLASSCOUNT)) {
            Integer classCount = data.getClassCount();
            columndata = (classCount != null) ? Integer.toString(classCount) : null;
        }

        return columndata;
    }
}
