
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0091ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0091ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Migration failed MigrationEvent{partitionId=0, status=FAILED, oldOwner=null, newOwner=Member [10.0.20.245]:5701 "
                + "- d4fc294f-5010-473c-abcf-13c63d01db1b}";

        LOG.info("dataText: " + dataText);

        PEGA0091ReportModel pega0091ReportModel = new PEGA0091ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0091ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "partitionId=0, status=FAILED, oldOwner=null, newOwner=Member [10.0.20.245]:5701 - d4fc294f-5010-473c-abcf-13c63d01db1b",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Migration failed MigrationEvent{partitionId=0, status=FAILED, oldOwner=Member [10.0.22.54]:5701 - a55b0f98-01a9-"
                + "4ace-b507-6dd96ace49f1, newOwner=Member [10.0.21.231]:5701 - 5bf53df8-cb66-4306-aa70-e5a2a08eb307}";

        LOG.info("dataText: " + dataText);

        PEGA0091ReportModel pega0091ReportModel = new PEGA0091ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0091ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "partitionId=0, status=FAILED, oldOwner=Member [10.0.22.54]:5701 - a55b0f98-01a9-4ace-b507-6dd96ace49f1, newOwner=Member [1"
                        + "0.0.21.231]:5701 - 5bf53df8-cb66-4306-aa70-e5a2a08eb307",
                alertMessageReportEntryKey);
    }
}
