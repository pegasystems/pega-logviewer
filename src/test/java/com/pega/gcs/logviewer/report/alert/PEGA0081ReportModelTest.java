
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0081ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0081ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "VBD query time exceeds threshold of 10 s: 16.01 s. For query [select Volume(Outcome == Impression) from Actuals "
                + "where Timestamp in range(25891395,26013479) and include(Context in ({WorkID=P-3},{WorkID=P-1},{WorkID=P-9},{WorkID=P-8},"
                + "{WorkID=P-10},{WorkID=P-7},{WorkID=P-5})) group by Timestamp(ALL, 1553483700000, 1560808740000), Context(WorkID)]";

        LOG.info("dataText: " + dataText);

        PEGA0081ReportModel pega0081ReportModel = new PEGA0081ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0081ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "select Volume(Outcome == Impression) from Actuals where Timestamp in range(25891395,26013479) and include(Context in ({Wor"
                        + "kID=P-3},{WorkID=P-1},{WorkID=P-9},{WorkID=P-8},{WorkID=P-10},{WorkID=P-7},{WorkID=P-5})) group by Timestamp(ALL"
                        + ", 1553483700000, 1560808740000), Context(WorkID)",
                alertMessageReportEntryKey);
    }

}
