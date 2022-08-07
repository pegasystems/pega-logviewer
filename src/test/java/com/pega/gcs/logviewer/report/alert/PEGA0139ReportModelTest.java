
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0139ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0139ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Email Account sample@pega.com is getting throttled by the mail provider, BackOFFTime: 160Listener Name : <listen"
                + "er_name>, Listener Email Account Name : <email_account_ID>, Listener Id : EMAIL-Thread-74, Receiver Email Id: sample@peg"
                + "a.com, Node Id : 69c987abc8d9e0d1234f56e780123456.";

        LOG.info("dataText: " + dataText);

        PEGA0139ReportModel pega0139ReportModel = new PEGA0139ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0139ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("sample@pega.com", alertMessageReportEntryKey);
    }

}
