/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingConstants;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;

public class AlertBoxAndWhiskerReportColumn extends DefaultTableColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    public static final AlertBoxAndWhiskerReportColumn ALERT_MESSAGE   = new AlertBoxAndWhiskerReportColumn("ALERT_MESSAGE"   , "Alert Message"  , 100, SwingConstants.CENTER , false);
    public static final AlertBoxAndWhiskerReportColumn KPI_THRESHOLD   = new AlertBoxAndWhiskerReportColumn("KPI_THRESHOLD"   , "KPI Threshold"  , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn KPI_UNIT        = new AlertBoxAndWhiskerReportColumn("KPI_UNIT"        , "KPI Value Unit" , 100, SwingConstants.CENTER , false);
    public static final AlertBoxAndWhiskerReportColumn COUNT           = new AlertBoxAndWhiskerReportColumn("COUNT"           , "Count"          , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn TOTAL           = new AlertBoxAndWhiskerReportColumn("TOTAL"           , "Total"          , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MEAN            = new AlertBoxAndWhiskerReportColumn("MEAN"            , "Mean"           , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MEDIAN          = new AlertBoxAndWhiskerReportColumn("MEDIAN"          , "Median"         , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn Q1              = new AlertBoxAndWhiskerReportColumn("Q1"              , "Q1"             , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn Q3              = new AlertBoxAndWhiskerReportColumn("Q3"              , "Q3"             , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MINREGULARVALUE = new AlertBoxAndWhiskerReportColumn("MINREGULARVALUE" , "Min Regular"    , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MAXREGULARVALUE = new AlertBoxAndWhiskerReportColumn("MAXREGULARVALUE" , "Max Regular"    , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MINOUTLIER      = new AlertBoxAndWhiskerReportColumn("MINOUTLIER"      , "Min"            , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn MAXOUTLIER      = new AlertBoxAndWhiskerReportColumn("MAXOUTLIER"      , "Max \u25BC"     , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn IQR             = new AlertBoxAndWhiskerReportColumn("IQR"             , "IQR"            , 100, SwingConstants.RIGHT  , false);
    public static final AlertBoxAndWhiskerReportColumn OUTLIERS        = new AlertBoxAndWhiskerReportColumn("OUTLIERS"        , "Outliers"       , 100, SwingConstants.RIGHT  , false);
    // CHECKSTYLE:ON
    // @formatter:on

    public static final String KEY = "KEY";

    private static List<AlertBoxAndWhiskerReportColumn> defaultAlertMessageReportColumnList;

    static {

        List<AlertBoxAndWhiskerReportColumn> alertMessageReportColumnList = new ArrayList<AlertBoxAndWhiskerReportColumn>();

        alertMessageReportColumnList.add(COUNT);
        alertMessageReportColumnList.add(TOTAL);
        alertMessageReportColumnList.add(MINOUTLIER);
        alertMessageReportColumnList.add(Q1);
        alertMessageReportColumnList.add(MEDIAN);
        alertMessageReportColumnList.add(MEAN);
        alertMessageReportColumnList.add(Q3);
        alertMessageReportColumnList.add(MAXOUTLIER);
        alertMessageReportColumnList.add(IQR);
        alertMessageReportColumnList.add(OUTLIERS);

        defaultAlertMessageReportColumnList = Collections.unmodifiableList(alertMessageReportColumnList);
    }

    public AlertBoxAndWhiskerReportColumn(String columnId, String displayName, int prefColumnWidth,
            int horizontalAlignment, boolean filterable) {

        super(columnId, displayName, prefColumnWidth, horizontalAlignment, true, filterable);

    }

    public static List<AlertBoxAndWhiskerReportColumn> getDefaultAlertMessageReportColumnList() {
        return defaultAlertMessageReportColumnList;
    }

}
