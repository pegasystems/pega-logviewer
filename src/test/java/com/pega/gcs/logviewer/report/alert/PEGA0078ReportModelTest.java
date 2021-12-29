
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0078ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0078ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Shape Compose1 retrieved too many (717) rows in data flow RuleKeys{className=Abcdef-ABC-MKT-Data-Customer, name="
                + "ContainerDFP126}";

        LOG.info("dataText: " + dataText);

        PEGA0078ReportModel pega0078ReportModel = new PEGA0078ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0078ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "Compose1 - [className=Abcdef-ABC-MKT-Data-Customer, name=ContainerDFP126]",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Shape Compose6 retrieved too many (347) rows in data flow RuleKeys{className=Abcef-Mrkt-ABCDEF-Data-Person, name"
                + "=InboundCustomerDF}";

        LOG.info("dataText: " + dataText);

        PEGA0078ReportModel pega0078ReportModel = new PEGA0078ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0078ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "Compose6 - [className=Abcef-Mrkt-ABCDEF-Data-Person, name=InboundCustomerDF]",
                alertMessageReportEntryKey);
    }
}
