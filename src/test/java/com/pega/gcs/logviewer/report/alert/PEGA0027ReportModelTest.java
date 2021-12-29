
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0027ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0027ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Database ResultSet list returned more than 25,000 rows: 32,523. SQL: select pyLabel as \"pyLabel\" , pyStringTyp"
                + "e as \"pyStringType\" , pyColumnInclusion as \"pyColumnInclusion\" , pxObjClass as \"pxObjClass\" , pyPropertyMode as \""
                + "pyPropertyMode\" , pyMaxLength as \"pyMaxLength\" , pyPropertyName as \"pyPropertyName\" , pyClassName as \"pyClassName\""
                + " , pzInsKey as \"pzInsKey\" from pr4_rule_property where pyPropertyMode >= ? and (pyPropertyMode <=  ? or pyPropertyMode"
                + " like 'String%') and pyPropertyMode like '%String' and pxObjClass = ? order by pzInsKey";

        LOG.info("dataText: " + dataText);

        PEGA0027ReportModel pega0027ReportModel = new PEGA0027ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0027ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "select pyLabel as \"pyLabel\" , pyStringType as \"pyStringType\" , pyColumnInclusion as \"pyColumnInclusion\" , pxObjClass"
                        + " as \"pxObjClass\" , pyPropertyMode as \"pyPropertyMode\" , pyMaxLength as \"pyMaxLength\" , pyPropertyName as \""
                        + "pyPropertyName\" , pyClassName as \"pyClassName\" , pzInsKey as \"pzInsKey\" from pr4_rule_property where pyProp"
                        + "ertyMode >= ? and (pyPropertyMode <=  ? or pyPropertyMode like 'String%') and pyPropertyMode like '%String' and "
                        + "pxObjClass = ? order by pzInsKey",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Database ResultSet list returned more than 25,000 rows: 163,779. SQL: select pzPVStream from DATA.ABRFS_CITY whe"
                + "re pzInsKey >= ? and (pzInsKey = ? or pzInsKey like 'ABR-FW-DATA-DICT-CITY %') and pxObjClass = ? order by pzInsKey";

        LOG.info("dataText: " + dataText);

        PEGA0027ReportModel pega0027ReportModel = new PEGA0027ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0027ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "select pzPVStream from DATA.ABRFS_CITY where pzInsKey >= ? and (pzInsKey = ? or pzInsKey like 'ABR-FW-DATA-DICT-CITY %') a"
                        + "nd pxObjClass = ? order by pzInsKey",
                alertMessageReportEntryKey);

    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Database RowSet list returned more than 25,000 rows: 69,243. SQL: ";

        LOG.info("dataText: " + dataText);

        PEGA0027ReportModel pega0027ReportModel = new PEGA0027ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0027ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);

    }
}
