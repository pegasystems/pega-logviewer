
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0037ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0037ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Rule assembly process has exceeded the threshold of 400 ms: 417 ms. Details: ;Total Rule assembly process: ELAPS"
                + "ED time = 417;CPU time = 0;Delta Java assembly process: Delta Assembly ELAPSED time = 53;Delta Assembly CPU time = 0;Del"
                + "ta Compile process: Delta Compile ELAPSED time = 364;Delta Compile CPU time = 0;Personal Ruleset =  ;AccessGroup = ProdM"
                + "gmtMB:Mgr_BackOffice;Application = ProdMgmtMB 01.06;FuaKeys = PXINSHANELD=RULE-OBJ-WHEN WORK- PZSHOWACTIONASBUTTONS #201"
                + "30919T005132.921 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0037ReportModel pega0037ReportModel = new PEGA0037ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0037ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-OBJ-WHEN WORK- PZSHOWACTIONASBUTTONS #20130919T005132.921 GMT", alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Rule assembly process has exceeded the threshold of 400 ms: 1,912 ms. Details: ;Total Rule assembly process: ELA"
                + "PSED time = 1,912;CPU time = 0;Delta Java assembly process: Delta Assembly ELAPSED time = 4;Delta Assembly CPU time = 0;"
                + "Delta Compile process: Delta Compile ELAPSED time = 1,908;Delta Compile CPU time = 0;Personal Ruleset = <virtual>;Access"
                + "Group = PRPC:Administrators;Application = PegaRULES 8;FuaKeys = PXINSHANDLE=RULE-OBJ-ACTIVITY RULE-ASYNC-QUEUEPROCESSOR "
                + "PZINITIALIZEQUEUEPROCESSORS #20180713T132651.292 GMT";

        LOG.info("dataText: " + dataText);

        PEGA0037ReportModel pega0037ReportModel = new PEGA0037ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0037ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "RULE-OBJ-ACTIVITY RULE-ASYNC-QUEUEPROCESSOR PZINITIALIZEQUEUEPROCESSORS #20180713T132651.292 GMT",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString3() {

        String dataText = "Rule assembly process has exceeded the threshold of 400 ms: 717 ms. Details: ;Total Rule assembly process: ELAPS"
                + "ED time = 717;CPU time = 0;Delta Java assembly process: Delta Assembly ELAPSED time = 468;Delta Assembly CPU time = 0;De"
                + "lta Compile process: Delta Compile ELAPSED time = 249;Delta Compile CPU time = 0;Assembled class name = com.pegarules.ge"
                + "nerated.html_section.ra_stream_pyactionarea_af7431ae43e91f2c3e3813daeeb5643d;Assembled class size = 31,528;Rules count ="
                + " 11;Personal Ruleset = ;AccessGroup = CPMS:UserAdministration;Application = UserAdministrator 01.01.01;FuaKeys =";

        LOG.info("dataText: " + dataText);

        PEGA0037ReportModel pega0037ReportModel = new PEGA0037ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0037ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "com.pegarules.generated.html_section.ra_stream_pyactionarea_af7431ae43e91f2c3e3813daeeb5643d",
                alertMessageReportEntryKey);
    }

}
