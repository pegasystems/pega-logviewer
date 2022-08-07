
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0142ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0142ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Stuck emails reached limit while processing for listener myEmailListener with 5 emails";

        LOG.info("dataText: " + dataText);

        PEGA0142ReportModel pega0142ReportModel = new PEGA0142ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0142ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("myEmailListener", alertMessageReportEntryKey);
    }

}
