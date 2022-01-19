
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0110ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0110ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Data page size has exceeded the alert threshold of 512KB, total size 800KB, key: D_MyDataPage. Synchronization p"
                + "erformance is low. Configure the pyModificationDateTime and pyIsRecordDeleted columns and corresponding filters in the d"
                + "ata page's source.";

        LOG.info("dataText: " + dataText);

        PEGA0110ReportModel pega0110ReportModel = new PEGA0110ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0110ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("D_MyDataPage", alertMessageReportEntryKey);
    }

}
