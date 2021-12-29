
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0011ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0011ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "RULE-SERVICE-EMAIL ABCCSEMAILDEFAULT ABC-CS-WORK-INTERACTION-INCORR!CREATEPYSTARTCASE #20191107T201910.582 GMT h"
                + "as exceeded the threshold of  1,000 ms: 1,416 ms;pxServiceInMapReqElapsed=1;pxServiceOutMapReqElapsed=0;pxServiceActivit"
                + "yElapsed=206;pxParseRuleTimeElapsed=0;";

        LOG.info("dataText: " + dataText);

        PEGA0011ReportModel pega0011ReportModel = new PEGA0011ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0011ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-SERVICE-EMAIL ABCCSEMAILDEFAULT ABC-CS-WORK-INTERACTION-INCORR!CREATEPYSTARTCASE #20191107T201910.582 GMT",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "RULE-SERVICE-REST ABC20 V1!NOTIFICATIONACKC55ABC2C423968E0B7D0112318FDC2E1 #20160214T140258.505 GMT has exceeded"
                + " the threshold of  1,000 ms: 33,005 ms;pxServiceInMapReqElapsed=1;pxServiceOutMapReqElapsed=8;pxServiceActivityElapsed=1"
                + "291;pxParseRuleTimeElapsed=0;";

        LOG.info("dataText: " + dataText);

        PEGA0011ReportModel pega0011ReportModel = new PEGA0011ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0011ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-SERVICE-REST ABC20 V1!NOTIFICATIONACKC55ABC2C423968E0B7D0112318FDC2E1 #20160214T140258.505 GMT",
                alertMessageReportEntryKey);
    }
}
