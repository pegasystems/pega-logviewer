
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0033ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0033ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Database query length exceeded the defined threshold value of 2,500: 5,151 SQL: SELECT pzInsKey as \"pxInsHandle"
                + "\", pyClassName AS \"pyClassName\" , pyFlowType AS \"pyFlowType\" , pyLabel AS \"pyLabel\" , pyRuleSet AS \"pyRuleSet\" "
                + ", pyRuleSetVersion AS \"pyRuleSetVersion\" , pyCategory AS \"pyCategory\" , pyCanStartInteractively AS \"pyCanStartInter"
                + "actively\" , pyCanCreateWorkObject AS \"pyCanCreateWorkObject\" , pyStreamName AS \"pyStreamName\" , pyMethodStatus AS ";

        LOG.info("dataText: " + dataText);

        PEGA0033ReportModel pega0033ReportModel = new PEGA0033ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0033ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertNotNull(alertMessageReportEntryKey);
    }
}
