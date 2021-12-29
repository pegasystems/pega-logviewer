
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class AnalysisMarkerTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final AnalysisMarkerTableColumn SNO      = new AnalysisMarkerTableColumn("S No"     , 50  , SwingConstants.CENTER , false);
    public static final AnalysisMarkerTableColumn NODEID   = new AnalysisMarkerTableColumn("Node Id"  , 300 , SwingConstants.LEFT);
    public static final AnalysisMarkerTableColumn CATEGORY = new AnalysisMarkerTableColumn("Category" , 100 , SwingConstants.CENTER);
    public static final AnalysisMarkerTableColumn MESSAGE  = new AnalysisMarkerTableColumn("Message"  , 800 , SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public AnalysisMarkerTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public AnalysisMarkerTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<AnalysisMarkerTableColumn> getColumnList() {

        List<AnalysisMarkerTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(NODEID);
        columnList.add(CATEGORY);
        columnList.add(MESSAGE);

        return columnList;
    }
}
