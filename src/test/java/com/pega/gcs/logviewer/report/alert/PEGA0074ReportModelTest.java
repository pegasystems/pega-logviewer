
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0074ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0074ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "DDS write duration <actual value> ms exceeds the threshold of <threshold> ms for column family <column family> w"
                + "ith keys <keys> record size <record size> KB";

        LOG.info("dataText: " + dataText);

        PEGA0074ReportModel pega0074ReportModel = new PEGA0074ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0074ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("", alertMessageReportEntryKey);
        org.junit.jupiter.api.Assertions.fail();
    }

}
