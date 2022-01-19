
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0068ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0068ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Social data flow recieved error for dataset id: <datasetId> for dataset type: <datasetType> with error type: <er"
                + "rorType> on social object: <socialobject> due to reason: <reason>";

        LOG.info("dataText: " + dataText);

        PEGA0068ReportModel pega0068ReportModel = new PEGA0068ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0068ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("<datasetType> [<datasetId>]", alertMessageReportEntryKey);
    }

}
