
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0118ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0118ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Long running job scheduler activity{job scheduler=Pega0118AlwaysAlerting, ruleset=AsyncPro, ruleset version=01-0"
                + "1-01, execution context=System-Runtime-Context, execution time (ms)=1524, threshold value (ms)=500} Activity{class name="
                + "@baseclass, activity name=SimpleLog, ruleset=AsyncPro, ruleset version=01-01-01, circumstance=}";

        LOG.info("dataText: " + dataText);

        PEGA0118ReportModel pega0118ReportModel = new PEGA0118ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0118ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Pega0118AlwaysAlerting", alertMessageReportEntryKey);
    }

}
