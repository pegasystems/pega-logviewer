/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class HotfixColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final HotfixColumn ID                       = new HotfixColumn("ID"                       , 60  , SwingConstants.CENTER , false, "ID"               , ""                    );
    public static final HotfixColumn HOTFIX_ID                = new HotfixColumn("HOTFIX ID"                , 100 , SwingConstants.CENTER , false, "Hotfix ID"               , "pxHotfixID"          );
    public static final HotfixColumn HOTFIX_DESCRIPTION       = new HotfixColumn("HOTFIX DESCRIPTION"       , 600 , SwingConstants.LEFT   , false, "Hotfix Description"      , "pxHotfixDescription" );
    public static final HotfixColumn PRODUCT_LABEL            = new HotfixColumn("PRODUCT LABEL"            , 200 , SwingConstants.CENTER , true , "Product Label"           , ""                    );
    public static final HotfixColumn PXHOTFIXSTATE            = new HotfixColumn("PXHOTFIXSTATE"            , 120 , SwingConstants.CENTER , true , "pxHotfixState"           , ""                    );
    public static final HotfixColumn HOTFIX_STATUS            = new HotfixColumn("HOTFIX STATUS"            , 120 , SwingConstants.CENTER , true , "Hotfix Status"           , "pxHotfixStatus"      );
    public static final HotfixColumn PXINSTALLSTATUS          = new HotfixColumn("PXINSTALLSTATUS"          , 120 , SwingConstants.CENTER , true , "pxInstallStatus"         , ""                    );
    public static final HotfixColumn CODE_CHANGE_CLASSNAME    = new HotfixColumn("CODE CHANGE CLASSNAME"    , 300 , SwingConstants.LEFT   , false, "Code Change ClassName"   , "pxClassName"         );
    public static final HotfixColumn CODE_CHANGE_JARNAME      = new HotfixColumn("CODE CHANGE JARNAME"      , 150 , SwingConstants.LEFT   , false, "Code Change JarName"     , "pxJarName"           );
    public static final HotfixColumn CODE_CHANGE_PACKAGENAME  = new HotfixColumn("CODE CHANGE PACKAGENAME"  , 300 , SwingConstants.LEFT   , false, "Code Change PackageName" , "pxPackageName"       );
    public static final HotfixColumn MODULE                   = new HotfixColumn("MODULE"                   , 120 , SwingConstants.LEFT   , false, "Module"                  , ""                    );
    public static final HotfixColumn CODE_MODIFIED_DATE       = new HotfixColumn("MODIFIED DATE"            , 160 , SwingConstants.CENTER , false, "Modified Date"           , ""                    );
    public static final HotfixColumn CLASS_NAME               = new HotfixColumn("CLASS NAME"               , 300 , SwingConstants.LEFT   , false, "Class Name"              , ""                    );
    public static final HotfixColumn JAR_NAME                 = new HotfixColumn("JAR NAME"                 , 120 , SwingConstants.LEFT   , false, "Jar Name"                , ""                    );
    public static final HotfixColumn PACKAGE_NAME             = new HotfixColumn("PACKAGE NAME"             , 120 , SwingConstants.LEFT   , false, "Package Name"            , "Package"             );
    public static final HotfixColumn HASH                     = new HotfixColumn("HASH"                     , 120 , SwingConstants.LEFT   , false, "Hash"                    , ""                    );
    public static final HotfixColumn MODIFIED_DATE            = new HotfixColumn("MODIFIED DATE"            , 160 , SwingConstants.CENTER , false, "Modified Date"           , "Modified"            );
    public static final HotfixColumn RULE_CHANGE_KEY          = new HotfixColumn("RULE CHANGE KEY"          , 300 , SwingConstants.LEFT   , false, "Rule Change Key"         , "pxKey"               );
    public static final HotfixColumn RULE_CHANGE_RULESET      = new HotfixColumn("RULE CHANGE RULESET"      , 200 , SwingConstants.LEFT   , false, "Rule Change RuleSet"     , "pxRuleSet"           );
    public static final HotfixColumn SCHEMA_CHANGE_KEY        = new HotfixColumn("SCHEMA CHANGE KEY"        , 200 , SwingConstants.LEFT   , false, "Schema Change DADT Key"  , "pxDADTKey"           );
    public static final HotfixColumn SCHEMA_CHANGE_TABLE_NAME = new HotfixColumn("SCHEMA CHANGE TABLE NAME" , 200 , SwingConstants.LEFT   , false, "Schema Change Table Name", "pxTableName"         );
    public static final HotfixColumn CRITICAL_LEVEL           = new HotfixColumn("CRITICAL LEVEL"           , 150 , SwingConstants.CENTER , true , "Critical Level"          , ""                    );
    public static final HotfixColumn HYBRID                   = new HotfixColumn("HYBRID"                   , 100 , SwingConstants.CENTER , true ,  "Hybrid"                  , ""                    );

    public static final HotfixColumn INSTANCE                 = new HotfixColumn("INSTANCE"                 , 500 , SwingConstants.LEFT   , false, "Instance"                , ""                    );
    public static final HotfixColumn CONTAINER                = new HotfixColumn("CONTAINER"                , 200 , SwingConstants.LEFT   , false, "Container"               , ""                    );
    public static final HotfixColumn REVISION                 = new HotfixColumn("REVISION"                 , 160 , SwingConstants.LEFT   , false, "Revision"                , ""                    );

    public static final HotfixColumn PRODUCT_NAME             = new HotfixColumn("PRODUCT NAME"             , 400 , SwingConstants.LEFT   , false, "Product Name"            , ""                    );
    public static final HotfixColumn PRODUCT_VERSION          = new HotfixColumn("PRODUCT VERSION"          , 200 , SwingConstants.CENTER , false, "Product Version"         , ""                    );
    public static final HotfixColumn HOTFIX_CHANGE_KEY        = new HotfixColumn("HOTFIX CHANGE KEY"        , 800 , SwingConstants.LEFT   , false, "Hotfix Change Key"       , ""                    );
    public static final HotfixColumn INSTALLED_HOTFIX_ID      = new HotfixColumn("INSTALLED HOTFIX ID"      , 300 , SwingConstants.LEFT   , false, "Installed Hotfix Id"     , ""                    );
    public static final HotfixColumn NOT_INSTALLED_HOTFIX_ID  = new HotfixColumn("NOT INSTALLED HOTFIX ID"  , 500 , SwingConstants.LEFT   , false, "Not Installed Hotfix Id" , ""                    );
    // CHECKSTYLE:ON
    // @formatter:on

    // "", "", "Not Installed Hotfix Ids"

    private static List<HotfixColumn> hotfixColumnList;

    static {
        hotfixColumnList = new ArrayList<>();

        hotfixColumnList.add(ID);
        hotfixColumnList.add(HOTFIX_ID);
        hotfixColumnList.add(HOTFIX_DESCRIPTION);
        hotfixColumnList.add(PRODUCT_LABEL);
        hotfixColumnList.add(PXHOTFIXSTATE);
        hotfixColumnList.add(HOTFIX_STATUS);
        hotfixColumnList.add(PXINSTALLSTATUS);

        hotfixColumnList.add(INSTANCE);
        hotfixColumnList.add(CONTAINER);
        hotfixColumnList.add(REVISION);

        hotfixColumnList.add(CODE_CHANGE_CLASSNAME);
        hotfixColumnList.add(CODE_CHANGE_JARNAME);
        hotfixColumnList.add(CODE_CHANGE_PACKAGENAME);
        hotfixColumnList.add(MODULE);
        hotfixColumnList.add(CODE_MODIFIED_DATE);
        hotfixColumnList.add(CLASS_NAME);
        hotfixColumnList.add(JAR_NAME);
        hotfixColumnList.add(PACKAGE_NAME);
        hotfixColumnList.add(HASH);
        hotfixColumnList.add(MODIFIED_DATE);
        hotfixColumnList.add(RULE_CHANGE_KEY);
        hotfixColumnList.add(RULE_CHANGE_RULESET);
        hotfixColumnList.add(SCHEMA_CHANGE_KEY);
        hotfixColumnList.add(SCHEMA_CHANGE_TABLE_NAME);
        hotfixColumnList.add(CRITICAL_LEVEL);
        hotfixColumnList.add(HYBRID);

    }

    private String[] altNames;

    public HotfixColumn(String columnId, int prefColumnWidth, int horizontalAlignment, boolean filterable,
            String... altNames) {

        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);

        this.altNames = altNames;
    }

    private String[] getAltNames() {
        return altNames;
    }

    public static HotfixColumn fromValue(String hotfixColumnName) {

        HotfixColumn hotfixColumn = null;

        for (HotfixColumn hc : hotfixColumnList) {

            for (String altName : hc.getAltNames()) {

                if (hotfixColumnName.equals(altName)) {
                    hotfixColumn = hc;
                    break;
                }
            }

            if (hotfixColumn != null) {
                break;
            }
        }

        return hotfixColumn;
    }

    /**
     * Below order is important as v6 hotfixes and 'not installed' hotfixes data is constructed in below order.
     * 
     * @see <code>CatalogManager.getHotfixEntryDataList</code>
     * 
     * @return
     */
    public static List<HotfixColumn> getCatalogHotfixColumnList() {

        List<HotfixColumn> catalogHotfixColumnList = new ArrayList<HotfixColumn>();

        catalogHotfixColumnList.add(HOTFIX_ID);
        catalogHotfixColumnList.add(HOTFIX_DESCRIPTION);
        catalogHotfixColumnList.add(PRODUCT_LABEL);
        catalogHotfixColumnList.add(PXHOTFIXSTATE);
        catalogHotfixColumnList.add(HOTFIX_STATUS);
        catalogHotfixColumnList.add(PXINSTALLSTATUS);
        catalogHotfixColumnList.add(CODE_CHANGE_CLASSNAME);
        catalogHotfixColumnList.add(CODE_CHANGE_JARNAME);
        catalogHotfixColumnList.add(CODE_CHANGE_PACKAGENAME);
        catalogHotfixColumnList.add(MODULE);
        catalogHotfixColumnList.add(CODE_MODIFIED_DATE);
        catalogHotfixColumnList.add(CLASS_NAME);
        catalogHotfixColumnList.add(JAR_NAME);
        catalogHotfixColumnList.add(PACKAGE_NAME);
        catalogHotfixColumnList.add(HASH);
        catalogHotfixColumnList.add(MODIFIED_DATE);
        catalogHotfixColumnList.add(RULE_CHANGE_KEY);
        catalogHotfixColumnList.add(RULE_CHANGE_RULESET);
        catalogHotfixColumnList.add(SCHEMA_CHANGE_KEY);
        catalogHotfixColumnList.add(SCHEMA_CHANGE_TABLE_NAME);
        catalogHotfixColumnList.add(CRITICAL_LEVEL);

        return catalogHotfixColumnList;
    }

    public static List<HotfixColumn> getTableHotfixColumnList() {

        List<HotfixColumn> hotfixColumnList = new ArrayList<HotfixColumn>();

        hotfixColumnList.add(ID);
        hotfixColumnList.add(HOTFIX_ID);
        hotfixColumnList.add(HOTFIX_DESCRIPTION);
        hotfixColumnList.add(PRODUCT_LABEL);
        hotfixColumnList.add(PXHOTFIXSTATE);
        hotfixColumnList.add(HOTFIX_STATUS);
        hotfixColumnList.add(HYBRID);

        return hotfixColumnList;
    }
}
