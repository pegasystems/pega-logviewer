
package com.pega.gcs.logviewer.systemstate.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class RuleSetVersionInfoTableColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final RuleSetVersionInfoTableColumn SNO                    = new RuleSetVersionInfoTableColumn("S No"                      , 60 , SwingConstants.CENTER , false);
    public static final RuleSetVersionInfoTableColumn RULESETNAME            = new RuleSetVersionInfoTableColumn("Ruleset Name"              , 400, SwingConstants.LEFT);
    public static final RuleSetVersionInfoTableColumn RULESETVERSION         = new RuleSetVersionInfoTableColumn("Ruleset Version"           , 200, SwingConstants.CENTER);
    public static final RuleSetVersionInfoTableColumn ISRULESETVERSIONLOCKED = new RuleSetVersionInfoTableColumn("Is Ruleset Version Locked" , 200, SwingConstants.CENTER);
    public static final RuleSetVersionInfoTableColumn RULESCOUNT             = new RuleSetVersionInfoTableColumn("Rules Count"               , 200, SwingConstants.CENTER);
    public static final RuleSetVersionInfoTableColumn LASTMODIFIED           = new RuleSetVersionInfoTableColumn("Last Modified"             , 200, SwingConstants.LEFT);
    // CHECKSTYLE:ON
    // @formatter:on

    public RuleSetVersionInfoTableColumn(String displayName, int prefColumnWidth, int horizontalAlignment) {
        super(displayName, prefColumnWidth, horizontalAlignment);
    }

    public RuleSetVersionInfoTableColumn(String columnId, int prefColumnWidth, int horizontalAlignment,
            boolean filterable) {
        super(columnId, columnId, prefColumnWidth, horizontalAlignment, true, filterable);
    }

    public static List<RuleSetVersionInfoTableColumn> getColumnList() {

        List<RuleSetVersionInfoTableColumn> columnList = new ArrayList<>();

        columnList.add(SNO);
        columnList.add(RULESETNAME);
        columnList.add(RULESETVERSION);
        columnList.add(ISRULESETVERSIONLOCKED);
        columnList.add(RULESCOUNT);
        columnList.add(LASTMODIFIED);

        return columnList;
    }
}
