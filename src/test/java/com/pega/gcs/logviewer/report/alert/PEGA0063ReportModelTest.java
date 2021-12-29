
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0063ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0063ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Strategy record processing time exceeds threshold of 5000 ms: 6763 ms. For strategy shape [Next-Best-Action] in "
                + "data flow [DFbe4f3119c71ba9ab183dda131d765f058bddf3d7]. Strategy shape metrics are: {\"stageId\":\"Strategy1\",\"dataFlo"
                + "wKeys\":{\"className\":\"Data-Decision-Request-Customer\",\"name\":\"DFbe4f3119c71ba9ab183dda131d765f058bddf3d7\"},\"sta"
                + "geName\":\"Next-Best-Action\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,\"lastRecordTimestamp\":1559644242694,\""
                + "avgTimePerRecord\":6763154.794,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dnode.api.metrics.PerformanceMetrics$NonMe"
                + "asuringMetrics\"},\"executionTime\":6763154794,\"withDetailedMetrics\":false}";

        LOG.info("dataText: " + dataText);

        PEGA0063ReportModel pega0063ReportModel = new PEGA0063ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0063ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Next-Best-Action [DFbe4f3119c71ba9ab183dda131d765f058bddf3d7]",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Strategy record processing time exceeds threshold of 5000 ms: 13990 ms. For strategy shape [NextBestAction] in d"
                + "ata flow [DF_NBA]. Strategy shape metrics are: {\"stageId\":\"Strategy1\",\"dataFlowKeys\":{\"className\":\"SPR-DSM-Data"
                + "-NBACustomer\",\"name\":\"DF_NBA\"},\"stageName\":\"NextBestAction\",\"recordsIn\":1,\"recordsOut\":1,\"errorsCount\":0,"
                + "\"lastRecordTimestamp\":1548749627262,\"avgTimePerRecord\":1.3990518974E7,\"internalMetrics\":{\"@class\":\"com.pega.dsm"
                + ".dnode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":13990518974,\"withDetailedMetrics\":false"
                + "}";

        LOG.info("dataText: " + dataText);

        PEGA0063ReportModel pega0063ReportModel = new PEGA0063ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0063ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("NextBestAction [DF_NBA]", alertMessageReportEntryKey);
    }
}
