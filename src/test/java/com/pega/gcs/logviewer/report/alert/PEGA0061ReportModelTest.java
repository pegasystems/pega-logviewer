
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0061ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0061ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "The number of written interaction history fact records is more than the threshold of <threshold> rows : <actual "
                + "value> rows : SQL : <list of rows>";

        LOG.info("dataText: " + dataText);

        PEGA0061ReportModel pega0061ReportModel = new PEGA0061ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0061ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("<list of rows>", alertMessageReportEntryKey);

    }

}
