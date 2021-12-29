
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0080ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0080ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "DDS record size 1846KB exceeds the threshold of 1024KB for column family data.fca_mccm_5f02b41c3c36a4f70b2a4ae9c"
                + "e131063 with keys {CustomerID=123452593, ds_=top_}";

        LOG.info("dataText: " + dataText);

        PEGA0080ReportModel pega0080ReportModel = new PEGA0080ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0080ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "data.fca_mccm_5f02b41c3c36a4f70b2a4ae9ce131063 - [CustomerID=123452593, ds_=top_]",
                alertMessageReportEntryKey);
    }

}
