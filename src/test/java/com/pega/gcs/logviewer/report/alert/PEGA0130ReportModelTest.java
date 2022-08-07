
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0130ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0130ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Current Job Scheduler run is taking too long {job scheduler=pyExecuteDbSystemPulse, ruleset=Pega-RulesEngine, ru"
                + "leset version=08-06-01, activity name=SystemPulse, activity class name=Code-} Job Scheduler started at Tue May 31 23:08:"
                + "18 UTC 2022 and it's been running for 121024 seconds. This is alert #2007 for this Job Scheduler execution. Thread info "
                + "{thread name=JOBSCHEDULER_THREAD_8, thread state=RUNNABLE}";

        LOG.info("dataText: " + dataText);

        PEGA0130ReportModel pega0130ReportModel = new PEGA0130ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0130ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pyExecuteDbSystemPulse [Code- SystemPulse]", alertMessageReportEntryKey);
    }

}
