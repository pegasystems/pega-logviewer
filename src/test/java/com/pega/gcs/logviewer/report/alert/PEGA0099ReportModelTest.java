
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

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Job Scheduler ReassignToTransfer activity PegaCA-Work:CACTIReassignToTransferor execution failed on node util-i-"
                + "0fe31cc8201c83db8. Error message: Job Scheduler [ReassignToTransfer] activity [CACTIReassignToTransferor] execution mark"
                + "ed as failed with message [Attempting to access a rule with a bad defined-on class: Trying to open rule \"CACTICALLTRANS"
                + "FERITEMS\" of class \"Rule-Obj-Report-Definition\", but no defined-on class (pyClassName) was specified. ]. Exception me"
                + "ssage [-].";

        LOG.info("dataText: " + dataText);

        PEGA0099ReportModel pega0099ReportModel = new PEGA0099ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0099ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ReassignToTransfer [PegaCA-Work:CACTIReassignToTransferor]",
                alertMessageReportEntryKey);
    }
}
