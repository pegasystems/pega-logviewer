
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0075ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0075ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "DDS read duration 138ms exceeds the threshold of 50ms for column family data.accountsbdp_abcmktuk_rules_35378392"
                + "a7c1b059dadce with keys {CustomerID=F12347150}";

        LOG.info("dataText: " + dataText);

        PEGA0075ReportModel pega0075ReportModel = new PEGA0075ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0075ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("data.accountsbdp_abcmktuk_rules_35378392a7c1b059dadce",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Cassandra interaction above threshold on 10.0.30.158: query BEGIN BATCH com.pega.dsm.dnode.impl.dataset.cassandr"
                + "a.statement.RequestorAwareBoundStatement@53fcc0dd[statement=insert into data.es_1291363649_adaptive_cst(pxCustomerId, px"
                + "CaptureTime, pxEventId, pxEventType, pxDescription, pxGroupId) values(?,?,?,?,?,?) using ttl ? and timestamp ?,token=<nu"
                + "ll>] APPLY BATCH execution time was 173ms and exceeded the threshold for single query (100ms).";

        LOG.info("dataText: " + dataText);

        PEGA0075ReportModel pega0075ReportModel = new PEGA0075ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0075ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("data.es_1291363649_adaptive_cst", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Cassandra interaction above threshold on 198.18.13.75: query insert into aggregation.ih_customer_v2 (sid, st, ot"
                + ") values (?, ?, ?) execution time was 200ms and exceeded the threshold for single query (100ms).";

        LOG.info("dataText: " + dataText);

        PEGA0075ReportModel pega0075ReportModel = new PEGA0075ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0075ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("aggregation.ih_customer_v2", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString4() {

        String dataText = "Cassandra interaction above threshold on 198.18.12.146: query select ot from aggregation.ih_customer_v2 where si"
                + "d = ? and st = ? execution time was 130ms and exceeded the threshold for single query (100ms).";

        LOG.info("dataText: " + dataText);

        PEGA0075ReportModel pega0075ReportModel = new PEGA0075ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0075ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("aggregation.ih_customer_v2", alertMessageReportEntryKey);
    }

}
