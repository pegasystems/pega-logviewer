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

public class CodeSetVersionInfoTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final CodeSetVersionInfoTableColumn SNO            = new CodeSetVersionInfoTableColumn("S No"            , 60 , SwingConstants.CENTER , false);
    public static final CodeSetVersionInfoTableColumn CODESETNAME    = new CodeSetVersionInfoTableColumn("Codeset Name"    , 400, SwingConstants.LEFT);
    public static final CodeSetVersionInfoTableColumn CODESETVERSION = new CodeSetVersionInfoTableColumn("Codeset Version" , 200, SwingConstants.CENTER);
    public static final CodeSetVersionInfoTableColumn CLASSCOUNT     = new CodeSetVersionInfoTableColumn("Class Count"     , 200, SwingConstants.CENTER);
    // CHECKSTYLE:ON
    // @formatter:on

    public CodeSetVersionInfoTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public CodeSetVersionInfoTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<CodeSetVersionInfoTableColumn> getColumnList() {

        List<CodeSetVersionInfoTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(CODESETNAME);
        columnList.add(CODESETVERSION);
        columnList.add(CLASSCOUNT);

        return columnList;
    }
}
