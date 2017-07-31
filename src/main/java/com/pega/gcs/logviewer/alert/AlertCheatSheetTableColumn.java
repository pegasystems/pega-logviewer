/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.alert;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class AlertCheatSheetTableColumn extends DefaultTableColumn {

	// @formatter:off
	public static final AlertCheatSheetTableColumn ID                 = new AlertCheatSheetTableColumn("ID", "Id"			     , 40  , SwingConstants.CENTER , false);
	public static final AlertCheatSheetTableColumn MESSAGEID          = new AlertCheatSheetTableColumn("MESSAGEID", "MessageID"          , 100 , SwingConstants.CENTER , true );
	public static final AlertCheatSheetTableColumn CATEGORY           = new AlertCheatSheetTableColumn("CATEGORY", "Category"           , 100 , SwingConstants.LEFT   , true );
	public static final AlertCheatSheetTableColumn SUBCATEGORY        = new AlertCheatSheetTableColumn("SUBCATEGORY", "Subcategory"        , 150 , SwingConstants.LEFT   , true );
	public static final AlertCheatSheetTableColumn TITLE              = new AlertCheatSheetTableColumn("TITLE", "Title"              , 300 , SwingConstants.LEFT   , false);
	public static final AlertCheatSheetTableColumn SEVERITY           = new AlertCheatSheetTableColumn("SEVERITY", "Severity"           , 80  , SwingConstants.CENTER , true );
	public static final AlertCheatSheetTableColumn PDNURL             = new AlertCheatSheetTableColumn("PDNURL", "PDNURL"             , 200 , SwingConstants.LEFT   , false);
	public static final AlertCheatSheetTableColumn DESCRIPTION        = new AlertCheatSheetTableColumn("DESCRIPTION", "Description"        , 600 , SwingConstants.LEFT   , false);
	public static final AlertCheatSheetTableColumn DSSENABLECONFIG    = new AlertCheatSheetTableColumn("DSSENABLECONFIG", "DSSEnableConfig"    , 400 , SwingConstants.LEFT   , false);
	public static final AlertCheatSheetTableColumn DSSENABLED         = new AlertCheatSheetTableColumn("DSSENABLED", "DSSEnabled"         , 80  , SwingConstants.CENTER , false);
	public static final AlertCheatSheetTableColumn DSSTHRESHOLDCONFIG = new AlertCheatSheetTableColumn("DSSTHRESHOLDCONFIG", "DSSThresholdConfig" , 400 , SwingConstants.LEFT   , false);
	public static final AlertCheatSheetTableColumn DSSVALUETYPE       = new AlertCheatSheetTableColumn("DSSVALUETYPE", "DSSValueType"       , 100 , SwingConstants.CENTER , true );
	public static final AlertCheatSheetTableColumn DSSVALUEUNIT       = new AlertCheatSheetTableColumn("DSSVALUEUNIT", "DSSValueUnit"       , 100 , SwingConstants.CENTER , false);
	public static final AlertCheatSheetTableColumn DSSDEFAULTVALUE    = new AlertCheatSheetTableColumn("DSSDEFAULTVALUE", "DSSDefaultValue"    , 200 , SwingConstants.LEFT   , false);
	// @formatter:on

	public static List<AlertCheatSheetTableColumn> alertCheatSheetTableColumnList;

	static {
		alertCheatSheetTableColumnList = new ArrayList<>();

		alertCheatSheetTableColumnList.add(ID);
		alertCheatSheetTableColumnList.add(MESSAGEID);
		alertCheatSheetTableColumnList.add(CATEGORY);
		alertCheatSheetTableColumnList.add(SUBCATEGORY);
		alertCheatSheetTableColumnList.add(TITLE);
		alertCheatSheetTableColumnList.add(SEVERITY);
		alertCheatSheetTableColumnList.add(PDNURL);
		alertCheatSheetTableColumnList.add(DESCRIPTION);
		alertCheatSheetTableColumnList.add(DSSENABLECONFIG);
		alertCheatSheetTableColumnList.add(DSSENABLED);
		alertCheatSheetTableColumnList.add(DSSTHRESHOLDCONFIG);
		alertCheatSheetTableColumnList.add(DSSVALUETYPE);
		alertCheatSheetTableColumnList.add(DSSVALUEUNIT);
		alertCheatSheetTableColumnList.add(DSSDEFAULTVALUE);

	}

	private AlertCheatSheetTableColumn(String columnId, String displayName, int prefColumnWidth,
			int horizontalAlignment, boolean filterable) {
		super(columnId, displayName, prefColumnWidth, horizontalAlignment, true, filterable);
	}

	public static List<AlertCheatSheetTableColumn> getTableColumnList() {
		return alertCheatSheetTableColumnList;
	}

	public static AlertCheatSheetTableColumn getTableColumnByName(String displayName) {

		AlertCheatSheetTableColumn tableColumn = DefaultTableColumn.getTableColumnByName(displayName,
				getTableColumnList());

		return tableColumn;
	}

	public static AlertCheatSheetTableColumn getTableColumnById(String columnId) {

		AlertCheatSheetTableColumn tableColumn = DefaultTableColumn.getTableColumnById(columnId, getTableColumnList());

		return tableColumn;
	}
}
