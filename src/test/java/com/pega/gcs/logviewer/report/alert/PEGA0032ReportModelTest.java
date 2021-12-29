
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0032ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0032ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Change to Rule-HTML-Property:OMFDELETEROWICONCTRL in ruleset pbiswas@ caused 17 shortcut and 4 classes to be inv"
                + "alidated.Rules invalidated (entry count): html_property:omfdeleterowiconctrl (13), html_section:bundledetaillist (6)";

        LOG.info("dataText: " + dataText);

        PEGA0032ReportModel pega0032ReportModel = new PEGA0032ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0032ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-HTML-Property:OMFDELETEROWICONCTRL",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Change to Rule-File-Text:WEBWB!SPELLCHECKERSCRIPT!JS caused 25 shortcut and 1 classes to be invalidated.Rules in"
                + "validated (entry count): html:wizardicons (1)";

        LOG.info("dataText: " + dataText);

        PEGA0032ReportModel pega0032ReportModel = new PEGA0032ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0032ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals("Rule-File-Text:WEBWB!SPELLCHECKERSCRIPT!JS",
                alertMessageReportEntryKey);
    }
}
