
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0072ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0072ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Run [pyBatchIndexClassesProcessor] with data flow [] has failed";

        LOG.info("dataText: " + dataText);

        PEGA0072ReportModel pega0072ReportModel = new PEGA0072ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0072ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("pyBatchIndexClassesProcessor", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Data flow run [Custom-151024-1] for data flow name [DF_GenerateFilesCustom] failed on node [dataflow-i-01579b4e3"
                + "71aa7a45]. Node data flow metrics: [{\"stageMetrics\":[{\"stageId\":\"Source\",\"dataFlowKeys\":{\"className\":\"ABC-SR-"
                + "BatchOutput\",\"name\":\"DF_GenerateFilesCustom\"},\"stageName\":\"Batch Output ";

        LOG.info("dataText: " + dataText);

        PEGA0072ReportModel pega0072ReportModel = new PEGA0072ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0072ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("DF_GenerateFilesCustom", alertMessageReportEntryKey);
    }
}
