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

public class RequestorPoolTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final RequestorPoolTableColumn SNO                   = new RequestorPoolTableColumn("S No"                     , 50 , SwingConstants.CENTER , false);
    public static final RequestorPoolTableColumn SERVICEPACKAGEINSNAME = new RequestorPoolTableColumn("Service Package Ins Name" , 200, SwingConstants.LEFT);
    public static final RequestorPoolTableColumn SERVICEPACKAGENAME    = new RequestorPoolTableColumn("Service Package Name"     , 300, SwingConstants.LEFT);
    public static final RequestorPoolTableColumn TIMEOUTCOUNT          = new RequestorPoolTableColumn("Timeout Count"            , 130, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn IDLECOUNT             = new RequestorPoolTableColumn("Idle Count"               , 100, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn MAXIDLECOUNT          = new RequestorPoolTableColumn("Max Idle Count"           , 130, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn MOSTIDLECOUNT         = new RequestorPoolTableColumn("Most Idle Count"          , 150, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn MAXWAITTIME           = new RequestorPoolTableColumn("Max Wait Time"            , 130, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn LONGESTWAITTIME       = new RequestorPoolTableColumn("Longest Wait Time"        , 150, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn MAXACTIVECOUNT        = new RequestorPoolTableColumn("Max Active Count"         , 150, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn MOSTACTIVECOUNT       = new RequestorPoolTableColumn("Most Active Count"        , 150, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn ACTIVECOUNT           = new RequestorPoolTableColumn("Active Count"             , 130, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn LASTACCESSTIME        = new RequestorPoolTableColumn("Last Access Time"         , 150, SwingConstants.CENTER);
    public static final RequestorPoolTableColumn APPLICATIONINFO       = new RequestorPoolTableColumn("Application Info"         , 150, SwingConstants.LEFT);
    public static final RequestorPoolTableColumn ACCESSGROUPNAME       = new RequestorPoolTableColumn("Access Group Name"        , 200, SwingConstants.LEFT);
    public static final RequestorPoolTableColumn ACCESSGROUPLIST       = new RequestorPoolTableColumn("Access Group List"        , 300, SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public RequestorPoolTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public RequestorPoolTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<RequestorPoolTableColumn> getColumnList() {

        List<RequestorPoolTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(SERVICEPACKAGEINSNAME); // treeset sorted on this column
        columnList.add(SERVICEPACKAGENAME);
        columnList.add(TIMEOUTCOUNT);
        columnList.add(IDLECOUNT);
        columnList.add(MAXIDLECOUNT);
        columnList.add(MOSTIDLECOUNT);
        columnList.add(MAXWAITTIME);
        columnList.add(LONGESTWAITTIME);
        columnList.add(MAXACTIVECOUNT);
        columnList.add(MOSTACTIVECOUNT);
        columnList.add(ACTIVECOUNT);
        columnList.add(LASTACCESSTIME);
        columnList.add(APPLICATIONINFO);
        columnList.add(ACCESSGROUPNAME);
        columnList.add(ACCESSGROUPLIST);

        return columnList;
    }
}
