
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class IndexInfoTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final IndexInfoTableColumn SNO            = new IndexInfoTableColumn("S No"             , 50 , SwingConstants.CENTER , false);
    public static final IndexInfoTableColumn NAME           = new IndexInfoTableColumn("Name"             , 300, SwingConstants.LEFT);
    public static final IndexInfoTableColumn INDEXEXISTS    = new IndexInfoTableColumn("Index Exists"     , 170, SwingConstants.CENTER);
    public static final IndexInfoTableColumn INDEXSTATUS    = new IndexInfoTableColumn("Index Status"     , 170, SwingConstants.CENTER);
    public static final IndexInfoTableColumn INDEXSTATE     = new IndexInfoTableColumn("Index State"      , 170, SwingConstants.CENTER);
    public static final IndexInfoTableColumn PRIMARYSIZE    = new IndexInfoTableColumn("Primary Size"     , 170, SwingConstants.CENTER);
    public static final IndexInfoTableColumn TOTALSIZE      = new IndexInfoTableColumn("Total Size"       , 170, SwingConstants.CENTER);
    public static final IndexInfoTableColumn NUMOFDOCUMENTS = new IndexInfoTableColumn("No. Of Documents" , 200, SwingConstants.CENTER);
    // CHECKSTYLE:ON
    // @formatter:on

    public IndexInfoTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public IndexInfoTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<IndexInfoTableColumn> getColumnList() {

        List<IndexInfoTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(NAME);
        columnList.add(INDEXEXISTS);
        columnList.add(INDEXSTATUS);
        columnList.add(INDEXSTATE);
        columnList.add(PRIMARYSIZE);
        columnList.add(TOTALSIZE);
        columnList.add(NUMOFDOCUMENTS);

        return columnList;
    }
}
