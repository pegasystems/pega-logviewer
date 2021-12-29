
package com.pega.gcs.logviewer.report;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class HazelcastMembershipTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final HazelcastMembershipTableColumn SNO             = new HazelcastMembershipTableColumn("S No"           , 80 , SwingConstants.CENTER , false);
    public static final HazelcastMembershipTableColumn NAME            = new HazelcastMembershipTableColumn("Name"           , 300, SwingConstants.LEFT);
    public static final HazelcastMembershipTableColumn HOSTNAME        = new HazelcastMembershipTableColumn("Hostname"       , 200, SwingConstants.LEFT);
    public static final HazelcastMembershipTableColumn CLUSTER_ADDRESS = new HazelcastMembershipTableColumn("Address"        , 200, SwingConstants.LEFT);
    public static final HazelcastMembershipTableColumn UUID            = new HazelcastMembershipTableColumn("UUID"           , 300, SwingConstants.LEFT);
    public static final HazelcastMembershipTableColumn OPERATING_MODE  = new HazelcastMembershipTableColumn("Operating Mode" , 150, SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public HazelcastMembershipTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public HazelcastMembershipTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<HazelcastMembershipTableColumn> getColumnList() {

        List<HazelcastMembershipTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(NAME);
        columnList.add(HOSTNAME);
        columnList.add(CLUSTER_ADDRESS);
        columnList.add(UUID);
        columnList.add(OPERATING_MODE);

        return columnList;
    }
}
