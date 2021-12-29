
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0053ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0053ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Data page 'D_OpenWorkOrders_pa9786112031836488pz' is being reloaded frequently. The average time between reloads"
                + " was 1.58 seconds. Consider revisiting refresh strategy.";

        LOG.info("dataText: " + dataText);

        PEGA0053ReportModel pega0053ReportModel = new PEGA0053ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0053ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_OpenWorkOrders", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Data page 'D_pxPipelineConfiguration_pa2629919853100651pz' is being reloaded frequently. The average time betwee"
                + "n reloads was 0.31 seconds. Consider revisiting refresh strategy.";

        LOG.info("dataText: " + dataText);

        PEGA0053ReportModel pega0053ReportModel = new PEGA0053ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0053ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_pxPipelineConfiguration", alertMessageReportEntryKey);
    }
}
