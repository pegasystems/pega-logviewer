
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0138ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0138ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Could not apply cloud optimization to simulation [S-2] using strategy rule [Data-Decision-Request-Customer.Trigg"
                + "er_NBA_TopLevel].\r\nCloud optimization failure message: ''Data-Decision-Request-Customer.Trigger_NBA_TopLevel' strategy"
                + " cannot be executed on SKS. details: SKS incompatible patterns detected.'.\r\nList of optimization violations: ";

        LOG.info("dataText: " + dataText);

        PEGA0138ReportModel pega0138ReportModel = new PEGA0138ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0138ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Data-Decision-Request-Customer.Trigger_NBA_TopLevel",
                alertMessageReportEntryKey);
    }

}
