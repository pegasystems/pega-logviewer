
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class SystemStateErrorTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final SystemStateErrorTableColumn SNO     = new SystemStateErrorTableColumn("S No"    , 50  , SwingConstants.CENTER , false);
    public static final SystemStateErrorTableColumn NODEID  = new SystemStateErrorTableColumn("Node Id" , 300 , SwingConstants.LEFT);
    public static final SystemStateErrorTableColumn MESSAGE = new SystemStateErrorTableColumn("Message" , 650 , SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public SystemStateErrorTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public SystemStateErrorTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<SystemStateErrorTableColumn> getColumnList() {

        List<SystemStateErrorTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(NODEID);
        columnList.add(MESSAGE);

        return columnList;
    }
}
