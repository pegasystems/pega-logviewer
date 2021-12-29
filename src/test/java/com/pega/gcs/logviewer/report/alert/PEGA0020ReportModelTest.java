
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0020ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0020ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "RULE-CONNECT-SOAP ABC-INT-SALESORDER-GETSALESORDER GETSALESORDEROPERATION #20190128T102344.508 GMT has exceeded "
                + "the threshold of  1,000 ms: 6,075 ms;pxConnectOutMapReqElapsed=0;pxConnectClientResponseElapsed=6059;pxConnectInMapReqEl"
                + "apsed=8;";

        LOG.info("dataText: " + dataText);

        PEGA0020ReportModel pega0020ReportModel = new PEGA0020ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0020ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-CONNECT-SOAP ABC-INT-SALESORDER-GETSALESORDER GETSALESORDEROPERATION #20190128T102344.508 GMT",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "RULE-CONNECT-REST PEGA-INT-PDN-RSS PYRSSCONNECTSERVICE #20180713T135044.314 GMT has exceeded the threshold of  1"
                + ",000 ms: 3,250 ms;pxConnectOutMapReqElapsed=19;pxConnectClientResponseElapsed=2739;pxConnectInMapReqElapsed=56;";

        LOG.info("dataText: " + dataText);

        PEGA0020ReportModel pega0020ReportModel = new PEGA0020ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0020ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-CONNECT-REST PEGA-INT-PDN-RSS PYRSSCONNECTSERVICE #20180713T135044.314 GMT",
                alertMessageReportEntryKey);

    }
}
