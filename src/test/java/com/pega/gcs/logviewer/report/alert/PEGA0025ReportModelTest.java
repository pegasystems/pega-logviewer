
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0025ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0025ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Performing list with blob; blob necessary due to following 2 properties [pxLongitude pxLatitude ] table [APPS_PR"
                + "OD_01.pc_History_ABC_WB_XYZM_Work] database [PegaDATA]";

        LOG.info("dataText: " + dataText);

        PEGA0025ReportModel pega0025ReportModel = new PEGA0025ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0025ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "[APPS_PROD_01.pc_History_ABC_WB_XYZM_Work] pxLongitude pxLatitude", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Performing list with blob; blob necessary due to following 1 property [pyXMLSignature ] table [pr4_rule] databas"
                + "e [PegaRULES]";

        LOG.info("dataText: " + dataText);

        PEGA0025ReportModel pega0025ReportModel = new PEGA0025ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0025ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("[pr4_rule] pyXMLSignature", alertMessageReportEntryKey);

    }
}
