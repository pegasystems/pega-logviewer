
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0090ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0090ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "A partition was lost: com.hazelcast.partition.PartitionLostEvent{partitionId=73, lostBackupCount=1, eventSource="
                + "[10.0.22.235]:5701}";

        LOG.info("dataText: " + dataText);

        PEGA0090ReportModel pega0090ReportModel = new PEGA0090ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0090ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "partitionId=73, lostBackupCount=1, eventSource=[10.0.22.235]:5701", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "A partition was lost: com.hazelcast.partition.PartitionLostEvent{partitionId=16, lostBackupCount=0, eventSource="
                + "[10.0.21.253]:5701}";

        LOG.info("dataText: " + dataText);

        PEGA0090ReportModel pega0090ReportModel = new PEGA0090ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0090ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "partitionId=16, lostBackupCount=0, eventSource=[10.0.21.253]:5701", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "A partition was lost: com.pega.hazelcast.internal.partition.PartitionLostEventImpl{partitionId=50, lostBackupCou"
                + "nt=0, eventSource=[198.18.138.20]:5701}";

        LOG.info("dataText: " + dataText);

        PEGA0090ReportModel pega0090ReportModel = new PEGA0090ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0090ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "partitionId=50, lostBackupCount=0, eventSource=[198.18.138.20]:5701", alertMessageReportEntryKey);
    }

}
