
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.Database;
import com.pega.gcs.logviewer.systemstate.model.DatabaseInfo;

public class DatabaseInfoTableModel extends AbstractDataTableModel<Database, DatabaseInfoTableColumn> {

    private static final long serialVersionUID = 9044208647082744370L;

    private Map<Integer, Database> dataMap;

    private List<DatabaseInfoTableColumn> columnList;

    public DatabaseInfoTableModel(DatabaseInfo databaseInfo) {

        super(null);

        columnList = DatabaseInfoTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<Database> databaseSet = databaseInfo.getDatabaseSet();

        if (databaseSet != null) {
            for (Database data : databaseSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, Database> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<DatabaseInfoTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(Database data, DatabaseInfoTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(DatabaseInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.DATABASENAME)) {
            columndata = data.getDatabaseName();
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.DATABASEVERSION)) {
            columndata = data.getDatabaseVersion();
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.DATABASEVENDORNAME)) {
            columndata = data.getDatabaseVendorName();
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.DRIVERNAME)) {
            columndata = data.getDriverName();
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.DRIVERVERSION)) {
            columndata = data.getDriverVersion();
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.MAXCOLUMNSINTABLE)) {
            Integer maxColumnsInTable = data.getMaxColumnsInTable();
            columndata = (maxColumnsInTable != null) ? Integer.toString(maxColumnsInTable) : null;
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.MAXCONNECTIONS)) {
            Integer maxConnections = data.getMaxConnections();
            columndata = (maxConnections != null) ? Integer.toString(maxConnections) : null;
        } else if (dataTableColumn.equals(DatabaseInfoTableColumn.MAXROWSIZE)) {
            Integer maxRowSize = data.getMaxRowSize();
            columndata = (maxRowSize != null) ? Integer.toString(maxRowSize) : null;
        }

        return columndata;
    }

}
