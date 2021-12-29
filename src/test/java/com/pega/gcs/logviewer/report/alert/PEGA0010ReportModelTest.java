
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0010ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0010ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Agent [PegaAESRemote] activity [PegaAESRemote-Interface-NodesInfo.PushNodesInfo] queue number [1] disabled due t"
                + "o execution errors ErrorMsg: unable to execute agent: PegaAESRemote #1: PegaAESRemote-Interface-NodesInfo.PushNodesInfoU"
                + "nable to fetch complete node info for the node web_i-0b42376d618d4f519";

        LOG.info("dataText: " + dataText);

        PEGA0010ReportModel pega0010ReportModel = new PEGA0010ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0010ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "[PegaAESRemote] - [PegaAESRemote-Interface-NodesInfo.PushNodesInfo]", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Agent [PegaMKT-Engine] activity [System-Queue-ProgramRun.ProcessProgramRun] queue number [0] terminated manually"
                + " ErrorMsg: Terminated manually";

        LOG.info("dataText: " + dataText);

        PEGA0010ReportModel pega0010ReportModel = new PEGA0010ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0010ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("[PegaMKT-Engine] - [System-Queue-ProgramRun.ProcessProgramRun]",
                alertMessageReportEntryKey);
    }
}
