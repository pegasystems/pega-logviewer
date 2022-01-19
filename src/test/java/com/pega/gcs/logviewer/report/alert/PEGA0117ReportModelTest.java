
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0117ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0117ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Long running queue processor activity{queue processor=TestQP, ruleset=DMSample_Branch_test, ruleset version=01-0"
                + "1-01, execution time (ms)=5481, exceeded threshold value (ms)=100} Producer operator{id=admin, name=admin} Producer acti"
                + "vity{class name=DMOrg-DMSample-Work, activity name=QueueToTestQP, activity step=3, ruleset=DMSample_Branch_test, ruleset"
                + " version=01-01-01, circumstance=0} Consumer activity{class name=@baseclass, activity name=Accelerate, ruleset=Pega-ProCo"
                + "m, ruleset version=08-01-01, circumstance=}";

        LOG.info("dataText: " + dataText);

        PEGA0117ReportModel pega0117ReportModel = new PEGA0117ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0117ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("TestQP", alertMessageReportEntryKey);
    }

}
