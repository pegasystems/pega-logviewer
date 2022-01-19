
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0038ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0038ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Cache find synchronization time has exceeded threshold of 30 ms: 856 ms. Details: Total find time: ELAPSED time "
                + "= 1,712; Synchronization time = 856; Caller ActivityClassName = ; Caller ActivityRuleSet time = ; aFUA Aspect = Action; "
                + "aFUA Identification = Rule-Obj-Activity Code-.SystemIndexer;aFUA Defining Class = Rule-Obj-Activity";

        LOG.info("dataText: " + dataText);

        PEGA0038ReportModel pega0038ReportModel = new PEGA0038ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0038ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-Obj-Activity Code-.SystemIndexer", alertMessageReportEntryKey);
    }

}
