
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0028ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0028ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Collection usage threshold exceeded for long-lived object memory pool (G1 Old Gen) INFO:MemoryPool=G1 Old Gen;Po"
                + "olType=long-lived object;Event=java.management.memory.collection.threshold.exceeded;TimeStamp=2019-05-17 00:01:27,742 GM"
                + "T;Count=669;NotificationSequenceNo=564;Threshold=9663676416;Used=10358904768;Max=10737418240;Committed=10359930880;Init="
                + "10171187200;";

        LOG.info("dataText: " + dataText);

        PEGA0028ReportModel pega0028ReportModel = new PEGA0028ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0028ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("long-lived object (G1 Old Gen)", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Collection usage threshold exceeded for long-lived object memory pool (Java heap) INFO:MemoryPool=Java heap;Pool"
                + "Type=long-lived object;Event=java.management.memory.collection.threshold.exceeded;TimeStamp=2013-04-26 12:37:48,186 GMT;"
                + "Count=8;NotificationSequenceNo=7;Threshold=2415919104;Used=2418644144;Max=2684354560;Committed=2668770816;Init=268435456"
                + "0;";

        LOG.info("dataText: " + dataText);

        PEGA0028ReportModel pega0028ReportModel = new PEGA0028ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0028ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("long-lived object (Java heap)", alertMessageReportEntryKey);

    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Collection usage threshold exceeded for class storage memory pool (PS Perm Gen) INFO:MemoryPool=PS Perm Gen;Pool"
                + "Type=class storage;Event=java.management.memory.collection.threshold.exceeded;TimeStamp=2016-07-20 11:32:01,480 GMT;Coun"
                + "t=2;NotificationSequenceNo=1;Threshold=77384908;Used=85552744;Max=85983232;Committed=85983232;Init=21757952;";

        LOG.info("dataText: " + dataText);

        PEGA0028ReportModel pega0028ReportModel = new PEGA0028ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0028ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("class storage (PS Perm Gen)", alertMessageReportEntryKey);

    }
}
