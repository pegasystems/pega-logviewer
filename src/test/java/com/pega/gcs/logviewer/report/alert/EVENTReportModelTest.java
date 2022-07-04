
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class EVENTReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(EVENTReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "PegaMarketing|MKT003|INFO|0.0|items|null|null|false|null|null|3380ee54-16fa-47ed-a047-34bd37fac77b|Email queue d"
                + "epth:0.0|false|null|null|null|null|null|null|null|null|null|null|null|null";

        LOG.info("dataText: " + dataText);

        EVENTReportModel eventReportModel = new EVENTReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = eventReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PegaMarketing-MKT003", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "PegaMarketing|MKT007|INFO|0.0|items|null|null|false|null|null|3380ee54-16fa-47ed-a047-34bd37fac77b|SMS queue dep"
                + "th:0.0|false|null|null|null|null|null|null|null|null|null|null|null|null";

        LOG.info("dataText: " + dataText);

        EVENTReportModel eventReportModel = new EVENTReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = eventReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("PegaMarketing-MKT007", alertMessageReportEntryKey);
    }

}
