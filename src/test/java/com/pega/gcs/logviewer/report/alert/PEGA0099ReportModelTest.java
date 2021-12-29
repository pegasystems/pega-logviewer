
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0099ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0099ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Job Scheduler \"MyTestOutlet\" activity \"OWYWRR-MyTestFa-Work:logmessage\" execution failed in node 2ec2006c975"
                + "c with error msg: Failed to find a 'RULE-OBJ-ACTIVITY' with the name 'LOGMESSAGE' that applies to 'OWYWRR-MyTestFa-Work'"
                + ". There were 1 rules with this name in the rulebase, but none matched this request. The 1 rules named 'LOGMESSAGE' defin"
                + "ed in the rulebase are:<CR>1 unrelated to applies-to class 'OWYWRR-MyTestFa-Work', for example: 'PegaAccel-Management-Mi"
                + "gration'.<CR>";

        LOG.info("dataText: " + dataText);

        PEGA0099ReportModel pega0099ReportModel = new PEGA0099ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0099ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("MyTestOutlet [OWYWRR-MyTestFa-Work:logmessage]",
                alertMessageReportEntryKey);
    }

}
