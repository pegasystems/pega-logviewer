
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0060ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0060ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "The number of read interaction history fact records is more than the threshold of 500 rows : 25411 rows : SQL :S"
                + "ELECT pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \""
                + "pxInteractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzChannelID AS \"pzChannelID\", \"FACTTABLE"
                + "\".pzCustomerID AS \"pzCustomerID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"FACTTABLE\".pxDecisionTime AS \"pxD"
                + "ecisionTime\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"pyAssociatedID\" FROM  (S"
                + "ELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"F"
                + "ACTTABLE\".pzActionID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".p"
                + "xDecisionTime, null AS \"pyAssociationStrength\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTA"
                + "BLE\"  WHERE (\"FACTTABLE\".pySubjectID IN ('31625480468'))  UNION ALL SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOu"
                + "tcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzChannelID"
                + ", \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pxDecisionTime, \"ASSOCIATIONTABLE\".pyAssociatio"
                + "nStrength AS \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision"
                + "-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAsso"
                + "ciatedID = \"FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN ('31625480468')) )  \"FACTTABLE\"  LE"
                + "FT OUTER JOIN {Class:Data-Decision-IH-Dimension-Action} \"ACTIONTABLE\" ON ((\"FACTTABLE\".pzActionID = \"ACTIONTABLE\"."
                + "pzID) ) LEFT OUTER JOIN {Class:Data-Decision-IH-Dimension-Customer} \"CUSTOMERTABLE\" ON ((\"FACTTABLE\".pzCustomerID = "
                + "\"CUSTOMERTABLE\".pzID) ) WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime}) AND ( ( (\"CUSTOMERT"
                + "ABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\"ACTIONTABLE\".pyIssue = 'Mobi"
                + "leB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\"AC"
                + "TIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".p"
                + "yGroup = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Cus"
                + "tomer') AND (\"ACTIONTABLE\".pyGroup = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\""
                + ".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Default') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2"
                + "C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Service') AND (\"ACTI"
                + "ONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyG"
                + "roup = 'Default') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Custom"
                + "er') AND (\"ACTIONTABLE\".pyGroup = 'Service') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pyS"
                + "ubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Migration') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C'"
                + ") ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Upsell') AND (\"ACTIONT"
                + "ABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGrou"
                + "p = 'Migration') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Custome"
                + "r') AND (\"ACTIONTABLE\".pyGroup = 'Upsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySub"
                + "jectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') "
                + ") OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND (\"ACTION"
                + "TABLE\".pyIssue = 'MobileB2B') ) )  ORDER BY 2 DESC";

        LOG.info("dataText: " + dataText);

        PEGA0060ReportModel pega0060ReportModel = new PEGA0060ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0060ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "SELECT pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \""
                        + "pxInteractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzChannelID AS \"pzChannelID\", \"F"
                        + "ACTTABLE\".pzCustomerID AS \"pzCustomerID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"FACTTABLE\".pxDecis"
                        + "ionTime AS \"pxDecisionTime\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"p"
                        + "yAssociatedID\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \""
                        + "FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzCustomerID, \""
                        + "FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pxDecisionTime, null AS \"pyAssociationStrength\", null AS \"pyAssociated"
                        + "ID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  WHERE (\"FACTTABLE\".pySubjectID IN (...))  UNION ALL SEL"
                        + "ECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionI"
                        + "D, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOutcomeID, \""
                        + "FACTTABLE\".pxDecisionTime, \"ASSOCIATIONTABLE\".pyAssociationStrength AS \"pyAssociationStrength\", \"ASSOCIATI"
                        + "ONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-D"
                        + "ecision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociatedID = \"FACTTABLE\".pySubjectI"
                        + "D) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN (...)) )  \"FACTTABLE\"  LEFT OUTER JOIN {Class:Data-Decision-IH"
                        + "-Dimension-Action} \"ACTIONTABLE\" ON ((\"FACTTABLE\".pzActionID = \"ACTIONTABLE\".pzID) ) LEFT OUTER JOIN {Clas"
                        + "s:Data-Decision-IH-Dimension-Customer} \"CUSTOMERTABLE\" ON ((\"FACTTABLE\".pzCustomerID = \"CUSTOMERTABLE\".pzI"
                        + "D) ) WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime}) AND ( ( (\"CUSTOMERTABLE\".pySubj"
                        + "ectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C"
                        + "') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\""
                        + "ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIO"
                        + "NTABLE\".pyGroup = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectTy"
                        + "pe = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B')"
                        + " ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Default') AND (\""
                        + "ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIO"
                        + "NTABLE\".pyGroup = 'Service') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectTyp"
                        + "e = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Default') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') )"
                        + " OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Service') AND (\"A"
                        + "CTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTION"
                        + "TABLE\".pyGroup = 'Migration') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectTy"
                        + "pe = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Upsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') )"
                        + " OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Migration') AND (\""
                        + "ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIO"
                        + "NTABLE\".pyGroup = 'Upsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType"
                        + " = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') "
                        + ") OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND ("
                        + "\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) )  ORDER BY 2 DESC",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "The number of read interaction history fact records is more than the threshold of 500 rows : 916 rows : SQL :SEL"
                + "ECT pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \"p"
                + "xInteractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzApplicationID AS \"pzApplicationID\", \"FA"
                + "CTTABLE\".pzChannelID AS \"pzChannelID\", \"FACTTABLE\".pzContextID AS \"pzContextID\", \"FACTTABLE\".pzCustomerID AS \""
                + "pzCustomerID\", \"FACTTABLE\".pzOperatorID AS \"pzOperatorID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"FACTTABL"
                + "E\".pzJourneyID AS \"pzJourneyID\", \"FACTTABLE\".pxDecisionTime AS \"pxDecisionTime\", \"FACTTABLE\".pxPriority AS \"px"
                + "Priority\", \"FACTTABLE\".pxRank AS \"pxRank\", \"FACTTABLE\".pyExternalID AS \"pyExternalID\", \"FACTTABLE\".pyGroupID "
                + "AS \"pyGroupID\", \"FACTTABLE\".pyLatitude AS \"pyLatitude\", \"FACTTABLE\".pyLongitude AS \"pyLongitude\", \"FACTTABLE\""
                + ".pyPropensity AS \"pyPropensity\", \"FACTTABLE\".pyPartitionKey AS \"pyPartitionKey\", \"FACTTABLE\".ConsentValue AS \"C"
                + "onsentValue\", \"FACTTABLE\".Customer_ID AS \"Customer_ID\", \"FACTTABLE\".LocationName AS \"LocationName\", \"FACTTABLE"
                + "\".Session_ID AS \"Session_ID\", \"FACTTABLE\".Visitor_ID AS \"Visitor_ID\", \"FACTTABLE\".AdaptiveModelUsed AS \"Adapti"
                + "veModelUsed\", \"FACTTABLE\".pyRevenue AS \"pyRevenue\", \"FACTTABLE\".pyISFactID AS \"pyISFactID\", \"FACTTABLE\".pyMax"
                + "Budget AS \"pyMaxBudget\", \"FACTTABLE\".pyTargetBudget AS \"pyTargetBudget\", \"FACTTABLE\".pyFulfilled AS \"pyFulfille"
                + "d\", \"FACTTABLE\".pyWeight AS \"pyWeight\", \"FACTTABLE\".ReferrerUrl AS \"ReferrerUrl\", \"FACTTABLE\".Utm_medium AS \""
                + "Utm_medium\", \"FACTTABLE\".ExternalAudienceId AS \"ExternalAudienceId\", \"FACTTABLE\".IPAddress AS \"IPAddress\", \"FA"
                + "CTTABLE\".Revenue AS \"Revenue\", \"FACTTABLE\".Cost AS \"Cost\", \"pyAssociationStrength\" AS \"pyAssociationStrength\""
                + ", \"pyAssociatedID\" AS \"pyAssociatedID\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABL"
                + "E\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \"FACTTABLE\".p"
                + "zChannelID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FACTTABLE\".pzOutcomeID"
                + ", \"FACTTABLE\".pzJourneyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACTTABLE\".pxRank, \"FACTTABLE\""
                + ".pyExternalID, \"FACTTABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyLongitude, \"FACTTABLE\".pyPropensity,"
                + " \"FACTTABLE\".pyPartitionKey, \"FACTTABLE\".ConsentValue, \"FACTTABLE\".Customer_ID, \"FACTTABLE\".LocationName, \"FACT"
                + "TABLE\".Session_ID, \"FACTTABLE\".Visitor_ID, \"FACTTABLE\".AdaptiveModelUsed, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".py"
                + "ISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTargetBudget, \"FACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \""
                + "FACTTABLE\".ReferrerUrl, \"FACTTABLE\".Utm_medium, \"FACTTABLE\".ExternalAudienceId, \"FACTTABLE\".IPAddress, \"FACTTABL"
                + "E\".Revenue, \"FACTTABLE\".Cost, null AS \"pyAssociationStrength\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision"
                + "-IH-Fact} \"FACTTABLE\"  WHERE (\"FACTTABLE\".pySubjectID IN ('000000123456789'))  UNION ALL SELECT \"FACTTABLE\".pxFact"
                + "ID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"F"
                + "ACTTABLE\".pzApplicationID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABL"
                + "E\".pzOperatorID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJourneyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPr"
                + "iority, \"FACTTABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTTABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE"
                + "\".pyLongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyPartitionKey, \"FACTTABLE\".ConsentValue, \"FACTTABLE\".Cust"
                + "omer_ID, \"FACTTABLE\".LocationName, \"FACTTABLE\".Session_ID, \"FACTTABLE\".Visitor_ID, \"FACTTABLE\".AdaptiveModelUsed"
                + ", \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTargetBudget, \"FACTTABL"
                + "E\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".ReferrerUrl, \"FACTTABLE\".Utm_medium, \"FACTTABLE\".ExternalAudi"
                + "enceId, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACTTABLE\".Cost, \"ASSOCIATIONTABLE\".pyAssociationStrength A"
                + "S \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \""
                + "FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociatedID = \""
                + "FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN ('000000123456789')) )  \"FACTTABLE\"  LEFT OUTER "
                + "JOIN {Class:Data-Decision-IH-Dimension-Customer} \"CUSTOMERTABLE\" ON ((\"FACTTABLE\".pzCustomerID = \"CUSTOMERTABLE\".p"
                + "zID) ) WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime}) AND ( ( (\"CUSTOMERTABLE\".pySubjectTyp"
                + "e = 'ABC-Data-Customer') ) )  ORDER BY 2 DESC";

        LOG.info("dataText: " + dataText);

        PEGA0060ReportModel pega0060ReportModel = new PEGA0060ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0060ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "SELECT pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \""
                        + "pxInteractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzApplicationID AS \"pzApplicationI"
                        + "D\", \"FACTTABLE\".pzChannelID AS \"pzChannelID\", \"FACTTABLE\".pzContextID AS \"pzContextID\", \"FACTTABLE\".p"
                        + "zCustomerID AS \"pzCustomerID\", \"FACTTABLE\".pzOperatorID AS \"pzOperatorID\", \"FACTTABLE\".pzOutcomeID AS \""
                        + "pzOutcomeID\", \"FACTTABLE\".pzJourneyID AS \"pzJourneyID\", \"FACTTABLE\".pxDecisionTime AS \"pxDecisionTime\","
                        + " \"FACTTABLE\".pxPriority AS \"pxPriority\", \"FACTTABLE\".pxRank AS \"pxRank\", \"FACTTABLE\".pyExternalID AS \""
                        + "pyExternalID\", \"FACTTABLE\".pyGroupID AS \"pyGroupID\", \"FACTTABLE\".pyLatitude AS \"pyLatitude\", \"FACTTABL"
                        + "E\".pyLongitude AS \"pyLongitude\", \"FACTTABLE\".pyPropensity AS \"pyPropensity\", \"FACTTABLE\".pyPartitionKey"
                        + " AS \"pyPartitionKey\", \"FACTTABLE\".ConsentValue AS \"ConsentValue\", \"FACTTABLE\".Customer_ID AS \"Customer_"
                        + "ID\", \"FACTTABLE\".LocationName AS \"LocationName\", \"FACTTABLE\".Session_ID AS \"Session_ID\", \"FACTTABLE\"."
                        + "Visitor_ID AS \"Visitor_ID\", \"FACTTABLE\".AdaptiveModelUsed AS \"AdaptiveModelUsed\", \"FACTTABLE\".pyRevenue "
                        + "AS \"pyRevenue\", \"FACTTABLE\".pyISFactID AS \"pyISFactID\", \"FACTTABLE\".pyMaxBudget AS \"pyMaxBudget\", \"FA"
                        + "CTTABLE\".pyTargetBudget AS \"pyTargetBudget\", \"FACTTABLE\".pyFulfilled AS \"pyFulfilled\", \"FACTTABLE\".pyWe"
                        + "ight AS \"pyWeight\", \"FACTTABLE\".ReferrerUrl AS \"ReferrerUrl\", \"FACTTABLE\".Utm_medium AS \"Utm_medium\", "
                        + "\"FACTTABLE\".ExternalAudienceId AS \"ExternalAudienceId\", \"FACTTABLE\".IPAddress AS \"IPAddress\", \"FACTTABL"
                        + "E\".Revenue AS \"Revenue\", \"FACTTABLE\".Cost AS \"Cost\", \"pyAssociationStrength\" AS \"pyAssociationStrength"
                        + "\", \"pyAssociatedID\" AS \"pyAssociatedID\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, "
                        + "\"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationI"
                        + "D, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID,"
                        + " \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJourneyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \""
                        + "FACTTABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTTABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\""
                        + ".pyLongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyPartitionKey, \"FACTTABLE\".ConsentValue, \"FACTTABLE\""
                        + ".Customer_ID, \"FACTTABLE\".LocationName, \"FACTTABLE\".Session_ID, \"FACTTABLE\".Visitor_ID, \"FACTTABLE\".Adap"
                        + "tiveModelUsed, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTar"
                        + "getBudget, \"FACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".ReferrerUrl, \"FACTTABLE\".Utm_mediu"
                        + "m, \"FACTTABLE\".ExternalAudienceId, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACTTABLE\".Cost, null AS"
                        + " \"pyAssociationStrength\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  WHERE ("
                        + "\"FACTTABLE\".pySubjectID IN (...))  UNION ALL SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FAC"
                        + "TTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \""
                        + "FACTTABLE\".pzChannelID, \"FACTTABLE\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FA"
                        + "CTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJourneyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACT"
                        + "TABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTTABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyL"
                        + "ongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyPartitionKey, \"FACTTABLE\".ConsentValue, \"FACTTABLE\".Cu"
                        + "stomer_ID, \"FACTTABLE\".LocationName, \"FACTTABLE\".Session_ID, \"FACTTABLE\".Visitor_ID, \"FACTTABLE\".Adaptiv"
                        + "eModelUsed, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTarget"
                        + "Budget, \"FACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".ReferrerUrl, \"FACTTABLE\".Utm_medium, "
                        + "\"FACTTABLE\".ExternalAudienceId, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACTTABLE\".Cost, \"ASSOCIAT"
                        + "IONTABLE\".pyAssociationStrength AS \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociate"
                        + "dID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONT"
                        + "ABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociatedID = \"FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySub"
                        + "jectID IN (...)) )  \"FACTTABLE\"  LEFT OUTER JOIN {Class:Data-Decision-IH-Dimension-Customer} \"CUSTOMERTABLE\""
                        + " ON ((\"FACTTABLE\".pzCustomerID = \"CUSTOMERTABLE\".pzID) ) WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage."
                        + "pzStartFrom DateTime}) AND ( ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') ) )  ORDER BY 2 DESC",
                alertMessageReportEntryKey);
    }
}
