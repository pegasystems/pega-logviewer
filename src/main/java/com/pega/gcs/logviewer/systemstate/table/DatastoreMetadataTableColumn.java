
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class DatastoreMetadataTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final DatastoreMetadataTableColumn SNO                = new DatastoreMetadataTableColumn("S No"                  , 50 , SwingConstants.CENTER , false);
    public static final DatastoreMetadataTableColumn DATABASESIZEONDISK = new DatastoreMetadataTableColumn("Database Size On Disk" , 200, SwingConstants.CENTER);
    public static final DatastoreMetadataTableColumn DBNAME             = new DatastoreMetadataTableColumn("Db Name"               , 300, SwingConstants.CENTER);
    public static final DatastoreMetadataTableColumn DBTYPE             = new DatastoreMetadataTableColumn("Db Type"               , 200, SwingConstants.CENTER);
    public static final DatastoreMetadataTableColumn SCHEMANAME         = new DatastoreMetadataTableColumn("Schema Name"           , 200, SwingConstants.CENTER);
    public static final DatastoreMetadataTableColumn SCHEMASIZE         = new DatastoreMetadataTableColumn("Schema Size"           , 200, SwingConstants.CENTER);
    public static final DatastoreMetadataTableColumn TABLECOUNT         = new DatastoreMetadataTableColumn("Table Count"           , 100, SwingConstants.CENTER);
    // CHECKSTYLE:ON
    // @formatter:on

    public DatastoreMetadataTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public DatastoreMetadataTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<DatastoreMetadataTableColumn> getColumnList() {

        List<DatastoreMetadataTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(DBNAME);
        columnList.add(DBTYPE);
        columnList.add(DATABASESIZEONDISK);
        columnList.add(SCHEMANAME);
        columnList.add(SCHEMASIZE);
        columnList.add(TABLECOUNT);

        return columnList;
    }
}
