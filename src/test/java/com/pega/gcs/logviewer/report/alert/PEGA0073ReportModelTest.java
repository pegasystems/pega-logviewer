
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0073ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0073ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "[pyBatchIndexProcessor] Preparing retry 438. Next attempt in 2m on data flow [pyBatchIndexProcessor] on stage [I"
                + "nputStage] with affected partitions: [12, 15, 16, 18]";

        LOG.info("dataText: " + dataText);

        PEGA0073ReportModel pega0073ReportModel = new PEGA0073ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0073ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pyBatchIndexProcessor", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "[MKTWTPR-1572444001447911] Preparing retry 4 out of 6. Next attempt in 8s on data flow [DF_Wait] on stage [Actua"
                + "ls]";

        LOG.info("dataText: " + dataText);

        PEGA0073ReportModel pega0073ReportModel = new PEGA0073ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0073ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("MKTWTPR-1572444001447911", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "At [1628703810329] run [PRTest-4336] with data flow [MyCompany-Marketing-Data-Company-SEGCustAll.DFPRTest4336] e"
                + "ncountered an error on stage [GCS_Strategy]: [java.lang.NullPointerException]";

        LOG.info("dataText: " + dataText);

        PEGA0073ReportModel pega0073ReportModel = new PEGA0073ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0073ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PRTest-4336", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString4() {

        String dataText = "Run [PR-5365] with data flow [DF_ProcessResponse] encountered an error: [com.pega.decision.vbd.service.VBDServic"
                + "eNotActiveException: Cache was invalidated. Please retry.]";

        LOG.info("dataText: " + dataText);

        PEGA0073ReportModel pega0073ReportModel = new PEGA0073ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0073ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PR-5365", alertMessageReportEntryKey);
    }

}
