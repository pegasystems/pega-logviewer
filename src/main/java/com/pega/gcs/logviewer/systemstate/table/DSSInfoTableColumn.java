
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class DSSInfoTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final DSSInfoTableColumn SNO     = new DSSInfoTableColumn("S No"     , 60 , SwingConstants.CENTER , false);
    public static final DSSInfoTableColumn RULESET = new DSSInfoTableColumn("Ruleset" , 200, SwingConstants.LEFT);
    public static final DSSInfoTableColumn NAME    = new DSSInfoTableColumn("Name"    , 400, SwingConstants.LEFT);
    public static final DSSInfoTableColumn VALUE   = new DSSInfoTableColumn("Value"   , 600, SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public DSSInfoTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public DSSInfoTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<DSSInfoTableColumn> getColumnList() {

        List<DSSInfoTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(RULESET);
        columnList.add(NAME);
        columnList.add(VALUE);

        return columnList;
    }
}
