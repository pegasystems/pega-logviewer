
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class DatabaseInfoTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final DatabaseInfoTableColumn SNO                = new DatabaseInfoTableColumn("S No"                 , 50  , SwingConstants.CENTER , false);
    public static final DatabaseInfoTableColumn DATABASENAME       = new DatabaseInfoTableColumn("Database Name"        , 200 , SwingConstants.LEFT);
    public static final DatabaseInfoTableColumn DATABASEVERSION    = new DatabaseInfoTableColumn("Database Version"     , 200 , SwingConstants.LEFT);
    public static final DatabaseInfoTableColumn DATABASEVENDORNAME = new DatabaseInfoTableColumn("Database Vendor Name" , 200 , SwingConstants.LEFT);
    public static final DatabaseInfoTableColumn DRIVERNAME         = new DatabaseInfoTableColumn("Driver Name"          , 300 , SwingConstants.CENTER);
    public static final DatabaseInfoTableColumn DRIVERVERSION      = new DatabaseInfoTableColumn("Driver Version"       , 150 , SwingConstants.CENTER);
    public static final DatabaseInfoTableColumn MAXCOLUMNSINTABLE  = new DatabaseInfoTableColumn("Max Columns In Table" , 150 , SwingConstants.CENTER);
    public static final DatabaseInfoTableColumn MAXCONNECTIONS     = new DatabaseInfoTableColumn("Max Connections"      , 150 , SwingConstants.CENTER);
    public static final DatabaseInfoTableColumn MAXROWSIZE         = new DatabaseInfoTableColumn("Max Row Size"         , 150 , SwingConstants.CENTER);
    // CHECKSTYLE:ON
    // @formatter:on

    public DatabaseInfoTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public DatabaseInfoTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<DatabaseInfoTableColumn> getColumnList() {

        List<DatabaseInfoTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(DATABASENAME);
        columnList.add(DATABASEVERSION);
        columnList.add(DATABASEVENDORNAME);
        columnList.add(DRIVERNAME);
        columnList.add(DRIVERVERSION);
        columnList.add(MAXCOLUMNSINTABLE);
        columnList.add(MAXCONNECTIONS);
        columnList.add(MAXROWSIZE);

        return columnList;
    }
}
