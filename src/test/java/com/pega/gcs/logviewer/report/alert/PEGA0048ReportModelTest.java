
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0048ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0048ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Page copy time and waiting time 25 s is more than Data Source execution time 16 s for data page D_BISEquipmentDe"
                + "tails having pzInskey=RULE-DECLARE-PAGES D_BISEQUIPMENTDETAILS #20170207T201818.984 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0048ReportModel pega0048ReportModel = new PEGA0048ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0048ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_BISEquipmentDetails", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Page copy time and waiting time 16 s is more than Data Source execution time 8 s for data page D_GetSalesOrder h"
                + "aving pzInsKey RULE-DECLARE-PAGES D_GETSALESORDER #20190116T122147.347 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0048ReportModel pega0048ReportModel = new PEGA0048ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0048ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_GetSalesOrder", alertMessageReportEntryKey);
    }
}
