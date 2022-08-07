/*******************************************************************************
 * Copyright (c) 2022 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class NodeStateTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final NodeStateTableColumn SNO               = new NodeStateTableColumn("S No"              , 60 , SwingConstants.CENTER , false);
    public static final NodeStateTableColumn NODEID            = new NodeStateTableColumn("Node Id"           , 300, SwingConstants.LEFT);
    public static final NodeStateTableColumn NODETYPE          = new NodeStateTableColumn("Node Type(s)"      , 500, SwingConstants.LEFT);
    public static final NodeStateTableColumn HEAP_YOUNG_GEN    = new NodeStateTableColumn("Heap (Young)"      , 150, SwingConstants.CENTER);
    public static final NodeStateTableColumn HEAP_INITIAL      = new NodeStateTableColumn("Heap (Initial)"    , 150, SwingConstants.CENTER);
    public static final NodeStateTableColumn HEAP_MAX          = new NodeStateTableColumn("Heap (Maximum)"    , 150, SwingConstants.CENTER);
    public static final NodeStateTableColumn THREAD_STACK_SIZE = new NodeStateTableColumn("Thread Stack Size" , 150, SwingConstants.CENTER);
    // CHECKSTYLE:ON
    // @formatter:on

    public NodeStateTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public NodeStateTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<NodeStateTableColumn> getColumnList() {

        List<NodeStateTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(NODEID);
        columnList.add(NODETYPE);
        columnList.add(HEAP_YOUNG_GEN);
        columnList.add(HEAP_INITIAL);
        columnList.add(HEAP_MAX);
        columnList.add(THREAD_STACK_SIZE);

        return columnList;
    }
}
