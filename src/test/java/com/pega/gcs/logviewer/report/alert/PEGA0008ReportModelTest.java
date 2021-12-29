
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0008ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0008ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "PegaRULES engine successfully started. TimeZone: Etc/UTC AESPerfStatMode: PUSH Server: ip-10-123-4-56 System: ab"
                + "c-prod1 Description: web_i-0304312bbd34c2e99 Production Level : 5 URL: http://ip-10-123-4-56:8080/prweb/PRSOAPServlet";

        LOG.info("dataText: " + dataText);

        PEGA0008ReportModel pega0008ReportModel = new PEGA0008ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0008ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("ip-10-123-4-56 - abc-prod1", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "PegaRULES engine successfully started. TimeZone: US/Central AESPerfStatMode: PUSH Server: abcd821.corp.test.com "
                + "System: abc_prod Description: 440c782348d4412442d30a2547c67363 Production Level : 5 URL: http://abcd821.corp.test.com:80"
                + "80/prweb/PRSOAPServlet";

        LOG.info("dataText: " + dataText);

        PEGA0008ReportModel pega0008ReportModel = new PEGA0008ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0008ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("abcd821.corp.test.com - abc_prod", alertMessageReportEntryKey);

    }
}
