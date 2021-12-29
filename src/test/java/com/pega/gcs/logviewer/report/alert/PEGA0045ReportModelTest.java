
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0045ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0045ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "A new request submitted for task data page D_GetSalesOrder having pzInsKey RULE-DECLARE-PAGES D_GETSALESORDER #2"
                + "0190116T122147.347 GMT without using the existing one";

        LOG.info("dataText: " + dataText);

        PEGA0045ReportModel pega0045ReportModel = new PEGA0045ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0045ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "data page D_GetSalesOrder having pzInsKey RULE-DECLARE-PAGES D_GETSALESORDER #20190116T122147.347 GMT",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "A new request submitted for task ASTERISK_ASTERISK_H2E92C1F9573B9BFEBC66227EB7D1F5BF_ASTERISK__D_XREStatus_'Host"
                + "Mac=48:F7:C0:18:D1:20'_'SerialNumber=MSACJLFGPZAA'_'SiteID=126'_'Type=SNMP' without using the existing one";

        LOG.info("dataText: " + dataText);

        PEGA0045ReportModel pega0045ReportModel = new PEGA0045ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0045ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "ASTERISK_ASTERISK_H2E92C1F9573B9BFEBC66227EB7D1F5BF_ASTERISK__D_XREStatus_'HostMac=48:F7:C0:18:D1:20'_'SerialNumber=MSACJL"
                        + "FGPZAA'_'SiteID=126'_'Type=SNMP'",
                alertMessageReportEntryKey);
    }
}
