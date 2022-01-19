
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0054ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0054ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Elasticsearch query took more than alert threshold of 500 ms: 638 ms SearchQuery: { \"from\" : 0, \"size\" : 50,"
                + " \"query\" : { \"filtered\" : { \"query\" : { \"query_string\" : { \"query\" : \"ASTERISKpzlpsearchmanagerluceneASTERISK"
                + "\", \"default_field\" : \"_instancename\", \"default_operator\" : \"and\", \"allow_leading_wildcard\" : true, \"analyze_"
                + "wildcard\" : true } }, \"filter\" : { \"fquery\" : { \"query\" : { \"query_string\" : { \"query\" : \"( pyRuleSet:Admin@"
                + "es OR pyRuleSet:PegaDeveloper OR pyRuleSet:C3PO OR pyRuleSet:C3POInt OR pyRuleSet:Es OR pyRuleSet:EsInt OR pyRuleSet:Peg"
                + "a-BIX OR pyRuleSet:C3POFW OR pyRuleSet:C3POFWInt OR pyRuleSet:Pega-ProcessCommander OR pyRuleSet:Pega-LP-ProcessAndRules"
                + " OR pyRuleSet:Pega-LP-Integration OR pyRuleSet:Pega-LP-Reports OR pyRuleSet:Pega-LP-SystemSettings_Branch_ES-1 OR pyRule"
                + "Set:Pega-LP-SystemSettings OR pyRuleSet:Pega-LP-UserInterface OR pyRuleSet:Pega-LP-OrgAndSecurity OR pyRuleSet:Pega-LP-D"
                + "ataModel OR pyRuleSet:Pega-LP-Application OR pyRuleSet:Pega-LP OR pyRuleSet:Pega-UpdateManager OR pyRuleSet:Pega-Securit"
                + "yVA OR pyRuleSet:Pega-Feedback OR pyRuleSet:Pega-AutoTest OR pyRuleSet:Pega-AppDefinition OR pyRuleSet:Pega-ImportExport"
                + " OR pyRuleSet:Pega-LocalizationTools OR pyRuleSet:Pega-RuleRefactoring OR pyRuleSet:Pega-ProcessArchitect OR pyRuleSet:P"
                + "ega-Portlet OR pyRuleSet:Pega-Content OR pyRuleSet:Pega-IntegrationArchitect OR pyRuleSet:Pega-SystemArchitect OR pyRule"
                + "Set:Pega-Desktop OR pyRuleSet:Pega-EndUserUI OR pyRuleSet:Pega-Social OR pyRuleSet:Pega-EventProcessing_Branch_ES-1 OR p"
                + "yRuleSet:Pega-EventProcessing OR pyRuleSet:Pega-Reporting OR pyRuleSet:Pega-UIDesign OR pyRuleSet:Pega-Gadgets OR pyRule"
                + "Set:Pega-UIEngine OR pyRuleSet:Pega-ProcessEngine OR pyRuleSet:Pega-SearchEngine_Branch_ES-1 OR pyRuleSet:Pega-SearchEng"
                + "ine OR pyRuleSet:Pega-IntegrationEngine OR pyRuleSet:Pega-RulesEngine_Branch_ES-1 OR pyRuleSet:Pega-RulesEngine OR pyRul"
                + "eSet:Pega-Engine OR pyRuleSet:Pega-ProCom OR pyRuleSet:Pega-IntSvcs OR pyRuleSet:Pega-WB OR pyRuleSet:Pega-RULES ) AND N"
                + "OT _isexternal:true\", \"default_field\" : \"_instancename\", \"default_operator\" : \"and\", \"allow_leading_wildcard\""
                + " : true } }, \"_cache\" : false } } } }, \"fields\" : \"ASTERISK\"}";

        LOG.info("dataText: " + dataText);

        PEGA0054ReportModel pega0054ReportModel = new PEGA0054ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0054ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "{ \"from\" : 0, \"size\" : 50, \"query\" : { \"filtered\" : { \"query\" : { \"query_string\" : { \"query\" : \"ASTERISKpzl"
                        + "psearchmanagerluceneASTERISK\", \"default_field\" : \"_instancename\", \"default_operator\" : \"and\", \"allow_l"
                        + "eading_wildcard\" : true, \"analyze_wildcard\" : true } }, \"filter\" : { \"fquery\" : { \"query\" : { \"query_s"
                        + "tring\" : { \"query\" : \"( pyRuleSet:Admin@es OR pyRuleSet:PegaDeveloper OR pyRuleSet:C3PO OR pyRuleSet:C3POInt"
                        + " OR pyRuleSet:Es OR pyRuleSet:EsInt OR pyRuleSet:Pega-BIX OR pyRuleSet:C3POFW OR pyRuleSet:C3POFWInt OR pyRuleSe"
                        + "t:Pega-ProcessCommander OR pyRuleSet:Pega-LP-ProcessAndRules OR pyRuleSet:Pega-LP-Integration OR pyRuleSet:Pega-"
                        + "LP-Reports OR pyRuleSet:Pega-LP-SystemSettings_Branch_ES-1 OR pyRuleSet:Pega-LP-SystemSettings OR pyRuleSet:Pega"
                        + "-LP-UserInterface OR pyRuleSet:Pega-LP-OrgAndSecurity OR pyRuleSet:Pega-LP-DataModel OR pyRuleSet:Pega-LP-Applic"
                        + "ation OR pyRuleSet:Pega-LP OR pyRuleSet:Pega-UpdateManager OR pyRuleSet:Pega-SecurityVA OR pyRuleSet:Pega-Feedba"
                        + "ck OR pyRuleSet:Pega-AutoTest OR pyRuleSet:Pega-AppDefinition OR pyRuleSet:Pega-ImportExport OR pyRuleSet:Pega-L"
                        + "ocalizationTools OR pyRuleSet:Pega-RuleRefactoring OR pyRuleSet:Pega-ProcessArchitect OR pyRuleSet:Pega-Portlet "
                        + "OR pyRuleSet:Pega-Content OR pyRuleSet:Pega-IntegrationArchitect OR pyRuleSet:Pega-SystemArchitect OR pyRuleSet:"
                        + "Pega-Desktop OR pyRuleSet:Pega-EndUserUI OR pyRuleSet:Pega-Social OR pyRuleSet:Pega-EventProcessing_Branch_ES-1 "
                        + "OR pyRuleSet:Pega-EventProcessing OR pyRuleSet:Pega-Reporting OR pyRuleSet:Pega-UIDesign OR pyRuleSet:Pega-Gadge"
                        + "ts OR pyRuleSet:Pega-UIEngine OR pyRuleSet:Pega-ProcessEngine OR pyRuleSet:Pega-SearchEngine_Branch_ES-1 OR pyRu"
                        + "leSet:Pega-SearchEngine OR pyRuleSet:Pega-IntegrationEngine OR pyRuleSet:Pega-RulesEngine_Branch_ES-1 OR pyRuleS"
                        + "et:Pega-RulesEngine OR pyRuleSet:Pega-Engine OR pyRuleSet:Pega-ProCom OR pyRuleSet:Pega-IntSvcs OR pyRuleSet:Peg"
                        + "a-WB OR pyRuleSet:Pega-RULES ) AND NOT _isexternal:true\", \"default_field\" : \"_instancename\", \"default_oper"
                        + "ator\" : \"and\", \"allow_leading_wildcard\" : true } }, \"_cache\" : false } } } }, \"fields\" : \"ASTERISK\"}",
                alertMessageReportEntryKey);
    }

}
