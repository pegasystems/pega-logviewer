
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0064ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0064ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Strategy component generated more rows than the threshold of 10000 rows: 19339 rows. For strategy [GetContacts] "
                + "component [FetchABCIH] in data flow [MyDecisionAPI]. Strategy shape metrics are: {\"stageId\":\"Strategy1\",\"dataFlowKe"
                + "ys\":{\"className\":\"GCSGrp-ABC-Data-Customer\",\"name\":\"MyDecisionAPI\"},\"stageName\":\"My NBA\",\"recordsIn\":1,\""
                + "recordsOut\":2,\"errorsCount\":0,\"lastRecordTimestamp\":1586131658076,\"internalMetrics\":{\"@class\":\"com.pega.dsm.dn"
                + "ode.api.metrics.PerformanceMetrics$NonMeasuringMetrics\"},\"executionTime\":7826613251,\"averageTimeInMicros\":7826613.2"
                + "51,\"withDetailedMetrics\":false}";

        LOG.info("dataText: " + dataText);

        PEGA0064ReportModel pega0064ReportModel = new PEGA0064ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0064ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("FetchABCIH [GetContacts] [MyDecisionAPI]",
                alertMessageReportEntryKey);
    }

}
