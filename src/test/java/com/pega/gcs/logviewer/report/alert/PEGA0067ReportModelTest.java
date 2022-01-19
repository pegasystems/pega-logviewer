
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0067ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0067ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Social data flow recieved error for dataset id: <datasetId> for dataset type: <datasetType> with error type: <er"
                + "rorType> on social object: <socialobject> due to reason: <reason>";

        LOG.info("dataText: " + dataText);

        PEGA0067ReportModel pega0067ReportModel = new PEGA0067ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0067ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("<datasetType> [<datasetId>]", alertMessageReportEntryKey);
    }

}
