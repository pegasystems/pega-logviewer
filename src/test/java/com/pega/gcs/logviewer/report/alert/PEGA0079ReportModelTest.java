
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0079ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0079ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "DDS record size 2476KB exceeds the threshold of 1024KB for column family data.mysimulat_d86b9bd6558b527f5a249920"
                + "b855ce31 with keys {CustomerID=000000123456789, ds_=ClickBehavior}";

        LOG.info("dataText: " + dataText);

        PEGA0079ReportModel pega0079ReportModel = new PEGA0079ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0079ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("{CustomerID=000000123456789, ds_=ClickBehavior}",
                alertMessageReportEntryKey);
    }

}
