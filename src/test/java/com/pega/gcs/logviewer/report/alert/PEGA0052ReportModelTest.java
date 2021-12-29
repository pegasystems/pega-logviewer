
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0052ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0052ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Waited 60,000 ms for the page ASTERISK_ASTERISK_H564E7F26C74A5520E5B0E789586E934C_ASTERISK__D_BISEquipmentDetail"
                + "s_'AccountNumber=14399207'_'SiteId=333' to load asynchronously, it has still not loaded. Proceeding to load synchronously. ";

        LOG.info("dataText: " + dataText);

        PEGA0052ReportModel pega0052ReportModel = new PEGA0052ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0052ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "ASTERISK_ASTERISK_H564E7F26C74A5520E5B0E789586E934C_ASTERISK__D_BISEquipmentDetails_'AccountNumber=14399207'_'SiteId=333'",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Waited 14,620 ms for the page ASTERISK_ASTERISK_H0A2DD3291ED42D4F89587D7B18A82DAD_ASTERISK__D_BISEquipmentDetail"
                + "s_'AccountNumber=28115401'_'SiteId=001' to load asynchronously, it has still not loaded. Proceeding to load synchronously. ";

        LOG.info("dataText: " + dataText);

        PEGA0052ReportModel pega0052ReportModel = new PEGA0052ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0052ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "ASTERISK_ASTERISK_H0A2DD3291ED42D4F89587D7B18A82DAD_ASTERISK__D_BISEquipmentDetails_'AccountNumber=28115401'_'SiteId=001'",
                alertMessageReportEntryKey);
    }
}
