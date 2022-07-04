
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.DatastoreMetadata;
import com.pega.gcs.logviewer.systemstate.model.DatastoreNodeMetadata;
import com.pega.gcs.logviewer.systemstate.model.SchemaMetadata;

public class DatastoreMetadataTableModel
        extends AbstractDataTableModel<DatastoreNodeMetadata, DatastoreMetadataTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, DatastoreNodeMetadata> dataMap;

    private List<DatastoreMetadataTableColumn> columnList;

    public DatastoreMetadataTableModel(DatastoreMetadata datastoreMetadata) {

        super(null);

        columnList = DatastoreMetadataTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<DatastoreNodeMetadata> datastoreNodeMetadataSet;
        datastoreNodeMetadataSet = datastoreMetadata.getDatastoreNodeMetadataSet();

        if (datastoreNodeMetadataSet != null) {
            for (DatastoreNodeMetadata data : datastoreNodeMetadataSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, DatastoreNodeMetadata> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<DatastoreMetadataTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(DatastoreNodeMetadata data, DatastoreMetadataTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(DatastoreMetadataTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.DBNAME)) {
            columndata = data.getDbName();
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.DBTYPE)) {
            columndata = data.getDbType();
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.DATABASESIZEONDISK)) {
            columndata = data.getDatabaseSizeOnDisk();
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.SCHEMANAME)) {
            SchemaMetadata schemaMetadata = data.getSchemaMetadata();
            columndata = (schemaMetadata != null) ? schemaMetadata.getSchemaName() : null;
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.SCHEMASIZE)) {
            SchemaMetadata schemaMetadata = data.getSchemaMetadata();
            columndata = (schemaMetadata != null) ? schemaMetadata.getSchemaSize() : null;
        } else if (dataTableColumn.equals(DatastoreMetadataTableColumn.TABLECOUNT)) {
            Integer tableCount = data.getTableCount();
            columndata = (tableCount != null) ? tableCount.toString() : null;
        }

        return columndata;
    }
}
