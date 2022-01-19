
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0107ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0107ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "ClientPageLoad has exceeded the elapsed time alert threshold of 4000 ms, total duration 5116 ms, load descriptio"
                + "n action:openWorkByHandle|key:AAA-APPPA-WORK C-1|.";

        LOG.info("dataText: " + dataText);

        PEGA0107ReportModel pega0107ReportModel = new PEGA0107ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0107ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("action:openWorkByHandle|key:AAA-APPPA-WORK C-1|.",
                alertMessageReportEntryKey);
    }

}
