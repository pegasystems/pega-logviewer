
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0019ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0019ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Long running interaction detected (Requestor:ZL3KEFUGGWTVM5C5M0TFFOT0WC0OLN6O7A,Java Thread:JOBSCHEDULER_THREAD_"
                + "POOL,Last Access Time:20190220T081952.269 GMT,User:null,Access Group Name:unavailable,Application Name:PegaRULES 8,Last "
                + "Known Processing:null)";

        LOG.info("dataText: " + dataText);

        PEGA0019ReportModel pega0019ReportModel = new PEGA0019ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0019ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ZL3KEFUGGWTVM5C5M0TFFOT0WC0OLN6O7A", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Long running interaction detected (Requestor:H27B9B4C0DE035A2E16876EB9217D5AFB,Java Thread:WebContainer : 17,Las"
                + "t Access Time:20160905T070147.034 GMT,User:43955845,Last Known Processing:Activity=GoToPreviousTask)";

        LOG.info("dataText: " + dataText);

        PEGA0019ReportModel pega0019ReportModel = new PEGA0019ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0019ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("H27B9B4C0DE035A2E16876EB9217D5AFB", alertMessageReportEntryKey);

    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Long running interaction detected -- THIS WILL BE THE LAST NOTIFICATION -- (Requestor:HQVYAKGQ6N13KPZOPNU4B2SRPB"
                + "G6RY0J8A,Java Thread:[STUCK] ExecuteThread: '16' for queue: 'weblogic.kernel.Default (self-tuning)',Last Access Time:201"
                + "91105T133347.966 GMT,User:ozge.aydin1,Access Group Name:MCCM:SystemArchitect,Application Name:MCCM 01-16-01,Last Known P"
                + "rocessing:Activity=WBRun)";

        LOG.info("dataText: " + dataText);

        PEGA0019ReportModel pega0019ReportModel = new PEGA0019ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0019ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("HQVYAKGQ6N13KPZOPNU4B2SRPBG6RY0J8A", alertMessageReportEntryKey);

    }
}
