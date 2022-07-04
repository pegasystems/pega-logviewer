
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0131ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0131ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Slow component [LaunchRTTriggers] with id [Destination] detected in data flow run [DF-123456] on data flow [Rule"
                + "Keys{className=ABC-Int-Data-RealTimeTriggers, name=RealTimeTriggers}] and is still running on thread [DataFlow-Service-P"
                + "ickingupRun-DF-123456:230, Access group: [GCSMKT:MarketAdmins], Partitions=[2]]";

        LOG.info("dataText: " + dataText);

        PEGA0131ReportModel pega0131ReportModel = new PEGA0131ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0131ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("LaunchRTTriggers-Destination", alertMessageReportEntryKey);
    }

}
