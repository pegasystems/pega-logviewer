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

public class HostNodeSettingTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final HostNodeSettingTableColumn SNO                   = new HostNodeSettingTableColumn("S No"             , 80 , SwingConstants.CENTER , false);
    public static final HostNodeSettingTableColumn HOSTNODEID            = new HostNodeSettingTableColumn("hostNodeID" , 200, SwingConstants.LEFT);
    public static final HostNodeSettingTableColumn FILEDIRECTORY         = new HostNodeSettingTableColumn("fileDirectory" , 500, SwingConstants.LEFT);
    public static final HostNodeSettingTableColumn HOSTNODESTATUS        = new HostNodeSettingTableColumn("hostNodeStatus" , 200, SwingConstants.LEFT);
    public static final HostNodeSettingTableColumn HOSTNODEINDEXERSTATUS = new HostNodeSettingTableColumn("hostNodeIndexerStatus" , 200, SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public HostNodeSettingTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public HostNodeSettingTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<HostNodeSettingTableColumn> getColumnList() {

        List<HostNodeSettingTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(HOSTNODEID);
        columnList.add(FILEDIRECTORY);
        columnList.add(HOSTNODESTATUS);
        columnList.add(HOSTNODEINDEXERSTATUS);

        return columnList;
    }
}
