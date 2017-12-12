/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class SystemScanColumn extends DefaultTableColumn {

	// @formatter:off
	public static final SystemScanColumn ID                       = new SystemScanColumn("ID"                       , 40  , SwingConstants.LEFT   , false, "ID"              , ""                    );
	public static final SystemScanColumn HOTFIX_ID                = new SystemScanColumn("HOTFIX ID"                , 100 , SwingConstants.LEFT   ,"Hotfix ID"               , "pxHotfixID"          );
	public static final SystemScanColumn HOTFIX_DESCRIPTION       = new SystemScanColumn("HOTFIX DESCRIPTION"       , 500 , SwingConstants.LEFT   ,"Hotfix Description"      , "pxHotfixDescription" );
	public static final SystemScanColumn PRODUCT_LABEL            = new SystemScanColumn("PRODUCT LABEL"            , 100 , SwingConstants.CENTER ,"Product Label"           , ""                    );
	public static final SystemScanColumn PXHOTFIXSTATE            = new SystemScanColumn("PXHOTFIXSTATE"            , 100 , SwingConstants.CENTER ,"pxHotfixState"           , ""                    );
	public static final SystemScanColumn HOTFIX_STATUS            = new SystemScanColumn("HOTFIX STATUS"            , 100 , SwingConstants.CENTER ,"Hotfix Status"           , "pxHotfixStatus"      );
	public static final SystemScanColumn PXINSTALLSTATUS          = new SystemScanColumn("PXINSTALLSTATUS"          , 100 , SwingConstants.CENTER ,"pxInstallStatus"         , ""                    );
	public static final SystemScanColumn CODE_CHANGE_CLASSNAME    = new SystemScanColumn("CODE CHANGE CLASSNAME"    , 300 , SwingConstants.LEFT   ,"Code Change ClassName"   , "pxClassName"         );
	public static final SystemScanColumn CODE_CHANGE_JARNAME      = new SystemScanColumn("CODE CHANGE JARNAME"      , 150 , SwingConstants.LEFT   ,"Code Change JarName"     , "pxJarName"           );
	public static final SystemScanColumn CODE_CHANGE_PACKAGENAME  = new SystemScanColumn("CODE CHANGE PACKAGENAME"  , 300 , SwingConstants.LEFT   ,"Code Change PackageName" , "pxPackageName"       );
	public static final SystemScanColumn MODULE                   = new SystemScanColumn("MODULE"                   , 120 , SwingConstants.LEFT   ,"Module"                  , ""                    );
	public static final SystemScanColumn CODE_MODIFIED_DATE       = new SystemScanColumn("MODIFIED DATE"            , 150 , SwingConstants.CENTER ,"Modified Date"           , ""                    );
	public static final SystemScanColumn CLASS_NAME               = new SystemScanColumn("CLASS NAME"               , 300 , SwingConstants.LEFT   ,"Class Name"              , ""                    );
	public static final SystemScanColumn JAR_NAME                 = new SystemScanColumn("JAR NAME"                 , 120 , SwingConstants.LEFT   ,"Jar Name"                , ""                    );
	public static final SystemScanColumn PACKAGE_NAME             = new SystemScanColumn("PACKAGE NAME"             , 120 , SwingConstants.LEFT   ,"Package Name"            , ""                    );
	public static final SystemScanColumn HASH                     = new SystemScanColumn("HASH"                     , 120 , SwingConstants.LEFT   ,"Hash"                    , ""                    );
	public static final SystemScanColumn RULE_MODIFIED_DATE       = new SystemScanColumn("MODIFIED DATE"            , 150 , SwingConstants.CENTER ,"Modified Date"           , ""                    );
	public static final SystemScanColumn RULE_CHANGE_KEY          = new SystemScanColumn("RULE CHANGE KEY"          , 300 , SwingConstants.LEFT   ,"Rule Change Key"         , "pxKey"               );
	public static final SystemScanColumn RULE_CHANGE_RULESET      = new SystemScanColumn("RULE CHANGE RULESET"      , 200 , SwingConstants.LEFT   ,"Rule Change RuleSet"     , "pxRuleSet"           );
	public static final SystemScanColumn SCHEMA_CHANGE_DADT_KEY   = new SystemScanColumn("SCHEMA CHANGE DADT KEY"   , 200 , SwingConstants.LEFT   ,"Schema Change DADT Key"  , "pxDADTKey"           );
	public static final SystemScanColumn SCHEMA_CHANGE_TABLE_NAME = new SystemScanColumn("SCHEMA CHANGE TABLE NAME" , 200 , SwingConstants.LEFT   ,"Schema Change Table Name", "pxTableName"         );
	public static final SystemScanColumn HYBRID                   = new SystemScanColumn("HYBRID"                   , 80  , SwingConstants.CENTER ,"Hybrid"                  , ""                    );

	// @formatter:on

	private String[] altNames;

	private SystemScanColumn(String columnId, int prefColumnWidth, int horizontalAlignment, String... altNames) {
		this(columnId, prefColumnWidth, horizontalAlignment, true, altNames);
	}

	private SystemScanColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable,
			String... altNames) {

		super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);

		this.altNames = altNames;
	}

	public String[] getAltNames() {
		return altNames;
	}

	public static List<SystemScanColumn> getSystemScanColumnList() {

		List<SystemScanColumn> systemScanColumnList = new ArrayList<SystemScanColumn>();

		systemScanColumnList.add(ID);
		systemScanColumnList.add(HOTFIX_ID);
		systemScanColumnList.add(HOTFIX_DESCRIPTION);
		systemScanColumnList.add(PRODUCT_LABEL);
		systemScanColumnList.add(PXHOTFIXSTATE);
		systemScanColumnList.add(HOTFIX_STATUS);
		systemScanColumnList.add(HYBRID);

		return systemScanColumnList;
	}

	public static List<SystemScanColumn> getHotfixInfoColumnList() {

		List<SystemScanColumn> hotfixInfoColumnList = new ArrayList<SystemScanColumn>();

		hotfixInfoColumnList.add(ID);
		hotfixInfoColumnList.add(PXINSTALLSTATUS);
		hotfixInfoColumnList.add(CODE_CHANGE_CLASSNAME);
		hotfixInfoColumnList.add(CODE_CHANGE_JARNAME);
		hotfixInfoColumnList.add(CODE_CHANGE_PACKAGENAME);
		hotfixInfoColumnList.add(MODULE);
		hotfixInfoColumnList.add(CODE_MODIFIED_DATE);
		hotfixInfoColumnList.add(CLASS_NAME);
		hotfixInfoColumnList.add(JAR_NAME);
		hotfixInfoColumnList.add(PACKAGE_NAME);
		hotfixInfoColumnList.add(HASH);
		hotfixInfoColumnList.add(RULE_MODIFIED_DATE);
		hotfixInfoColumnList.add(RULE_CHANGE_KEY);
		hotfixInfoColumnList.add(RULE_CHANGE_RULESET);
		hotfixInfoColumnList.add(SCHEMA_CHANGE_DADT_KEY);
		hotfixInfoColumnList.add(SCHEMA_CHANGE_TABLE_NAME);

		return hotfixInfoColumnList;
	}

}
