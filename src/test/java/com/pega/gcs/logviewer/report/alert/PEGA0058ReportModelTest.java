
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0058ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0058ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Reading the interaction history fact record(s) took more than the threshold of 1000 ms : 2797 ms : SQL : SELECT "
                + "pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \"pxInt"
                + "eractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzChannelID AS \"pzChannelID\", \"FACTTABLE\".pz"
                + "CustomerID AS \"pzCustomerID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"FACTTABLE\".pxDecisionTime AS \"pxDecisi"
                + "onTime\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"pyAssociatedID\" FROM  (SELECT"
                + " \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTA"
                + "BLE\".pzActionID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pxDeci"
                + "sionTime, null AS \"pyAssociationStrength\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\""
                + "  WHERE (\"FACTTABLE\".pySubjectID IN ('31625480468'))  UNION ALL SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcome"
                + "Time, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzChannelID, \"F"
                + "ACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pxDecisionTime, \"ASSOCIATIONTABLE\".pyAssociationStre"
                + "ngth AS \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-F"
                + "act} \"FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociate"
                + "dID = \"FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN ('31625480468')) )  \"FACTTABLE\"  LEFT OU"
                + "TER JOIN {Class:Data-Decision-IH-Dimension-Action} \"ACTIONTABLE\" ON ((\"FACTTABLE\".pzActionID = \"ACTIONTABLE\".pzID)"
                + " ) LEFT OUTER JOIN {Class:Data-Decision-IH-Dimension-Customer} \"CUSTOMERTABLE\" ON ((\"FACTTABLE\".pzCustomerID = \"CUS"
                + "TOMERTABLE\".pzID) ) WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime}) AND ( ( (\"CUSTOMERTABLE\""
                + ".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C'"
                + ") ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Xsell') AND (\"ACTIONTA"
                + "BLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup"
                + " = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer'"
                + ") AND (\"ACTIONTABLE\".pyGroup = 'Deepsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySub"
                + "jectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Default') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) "
                + "OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Service') AND (\"ACTIONTABL"
                + "E\".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup ="
                + " 'Default') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') A"
                + "ND (\"ACTIONTABLE\".pyGroup = 'Service') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubject"
                + "Type = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Migration') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR"
                + " ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Upsell') AND (\"ACTIONTABLE\""
                + ".pyIssue = 'MobileB2C') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'M"
                + "igration') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AN"
                + "D (\"ACTIONTABLE\".pyGroup = 'Upsell') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2B') ) OR ( (\"CUSTOMERTABLE\".pySubjectTy"
                + "pe = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND (\"ACTIONTABLE\".pyIssue = 'MobileB2C') ) OR ("
                + " (\"CUSTOMERTABLE\".pySubjectType = 'ABC-Data-Customer') AND (\"ACTIONTABLE\".pyGroup = 'Retention') AND (\"ACTIONTABLE\""
                + ".pyIssue = 'MobileB2B') ) )  ORDER BY 2 DESC";

        LOG.info("dataText: " + dataText);

        PEGA0058ReportModel pega0058ReportModel = new PEGA0058ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0058ReportModel.getAlertMessageReportEntryKey(dataText);

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

        String dataText = "Reading the interaction history fact record(s) took more than the threshold of 1000 ms : 2961 ms : SQL : SELECT "
                + "pxFactID AS \"pxFactID\", pxOutcomeTime AS \"pxOutcomeTime\", pySubjectID AS \"pySubjectID\", pxInteractionID AS \"pxInt"
                + "eractionID\", \"FACTTABLE\".pzActionID AS \"pzActionID\", \"FACTTABLE\".pzApplicationID AS \"pzApplicationID\", \"FACTTA"
                + "BLE\".pzChannelID AS \"pzChannelID\", \"FACTTABLE\".pzContextID AS \"pzContextID\", \"FACTTABLE\".pzCustomerID AS \"pzCu"
                + "stomerID\", \"FACTTABLE\".pzOperatorID AS \"pzOperatorID\", \"FACTTABLE\".pzOutcomeID AS \"pzOutcomeID\", \"FACTTABLE\"."
                + "pzJourneyID AS \"pzJourneyID\", \"FACTTABLE\".pxDecisionTime AS \"pxDecisionTime\", \"FACTTABLE\".pxPriority AS \"pxPrio"
                + "rity\", \"FACTTABLE\".pxRank AS \"pxRank\", \"FACTTABLE\".pyExternalID AS \"pyExternalID\", \"FACTTABLE\".pyGroupID AS \""
                + "pyGroupID\", \"FACTTABLE\".pyLatitude AS \"pyLatitude\", \"FACTTABLE\".pyLongitude AS \"pyLongitude\", \"FACTTABLE\".pyP"
                + "ropensity AS \"pyPropensity\", \"FACTTABLE\".pyPartitionKey AS \"pyPartitionKey\", \"FACTTABLE\".pyRevenue AS \"pyRevenu"
                + "e\", \"FACTTABLE\".pyISFactID AS \"pyISFactID\", \"FACTTABLE\".pyMaxBudget AS \"pyMaxBudget\", \"FACTTABLE\".pyTargetBud"
                + "get AS \"pyTargetBudget\", \"FACTTABLE\".pyFulfilled AS \"pyFulfilled\", \"FACTTABLE\".pyWeight AS \"pyWeight\", \"FACTT"
                + "ABLE\".IPAddress AS \"IPAddress\", \"FACTTABLE\".Revenue AS \"Revenue\", \"FACTTABLE\".Cost AS \"Cost\", \"FACTTABLE\".A"
                + "CCOUNT_ID AS \"ACCOUNT_ID\", \"FACTTABLE\".AnalyticsPriorityWeight AS \"AnalyticsPriorityWeight\", \"FACTTABLE\".SmoothP"
                + "ropensity AS \"SmoothPropensity\", \"FACTTABLE\".IMAGE_NAME AS \"IMAGE_NAME\", \"FACTTABLE\".CallID AS \"CallID\", \"FAC"
                + "TTABLE\".ContractIdKey AS \"ContractIdKey\", \"FACTTABLE\".MainProductID AS \"MainProductID\", \"FACTTABLE\".ChildProduc"
                + "tID AS \"ChildProductID\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"pyAssociatedI"
                + "D\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxIntera"
                + "ctionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzContextID, "
                + "\"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJourneyID, \"FACTTABL"
                + "E\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACTTABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTTABLE\".pyGroupID,"
                + " \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyLongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyPartitionKey, \"FACTTA"
                + "BLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTargetBudget, \"FACTTABLE\".pyFulf"
                + "illed, \"FACTTABLE\".pyWeight, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACTTABLE\".Cost, \"FACTTABLE\".ACCOUNT"
                + "_ID, \"FACTTABLE\".AnalyticsPriorityWeight, \"FACTTABLE\".SmoothPropensity, \"FACTTABLE\".IMAGE_NAME, \"FACTTABLE\".Call"
                + "ID, \"FACTTABLE\".ContractIdKey, \"FACTTABLE\".MainProductID, \"FACTTABLE\".ChildProductID, null AS \"pyAssociationStren"
                + "gth\", null AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  WHERE (\"FACTTABLE\".pySubjectID IN "
                + "('ABCD8374849'))  UNION ALL SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FAC"
                + "TTABLE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \"FACTTABLE\".pzChannelID, \"FACTTABL"
                + "E\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJour"
                + "neyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACTTABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTT"
                + "ABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyLongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyParti"
                + "tionKey, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTargetBudget, \"F"
                + "ACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACTTABLE\".Cost, \"FA"
                + "CTTABLE\".ACCOUNT_ID, \"FACTTABLE\".AnalyticsPriorityWeight, \"FACTTABLE\".SmoothPropensity, \"FACTTABLE\".IMAGE_NAME, \""
                + "FACTTABLE\".CallID, \"FACTTABLE\".ContractIdKey, \"FACTTABLE\".MainProductID, \"FACTTABLE\".ChildProductID, \"ASSOCIATIO"
                + "NTABLE\".pyAssociationStrength AS \"pyAssociationStrength\", \"ASSOCIATIONTABLE\".pySubjectID AS \"pyAssociatedID\" FROM"
                + " {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-Decision-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSO"
                + "CIATIONTABLE\".pyAssociatedID = \"FACTTABLE\".pySubjectID) ) WHERE (\"ASSOCIATIONTABLE\".pySubjectID IN ('ABCD8374849'))"
                + " )  \"FACTTABLE\"  WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQueryPage.pzStartFrom DateTime})  ORDER BY 2 DESC";

        LOG.info("dataText: " + dataText);

        PEGA0058ReportModel pega0058ReportModel = new PEGA0058ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0058ReportModel.getAlertMessageReportEntryKey(dataText);

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
                        + " AS \"pyPartitionKey\", \"FACTTABLE\".pyRevenue AS \"pyRevenue\", \"FACTTABLE\".pyISFactID AS \"pyISFactID\", \""
                        + "FACTTABLE\".pyMaxBudget AS \"pyMaxBudget\", \"FACTTABLE\".pyTargetBudget AS \"pyTargetBudget\", \"FACTTABLE\".py"
                        + "Fulfilled AS \"pyFulfilled\", \"FACTTABLE\".pyWeight AS \"pyWeight\", \"FACTTABLE\".IPAddress AS \"IPAddress\", "
                        + "\"FACTTABLE\".Revenue AS \"Revenue\", \"FACTTABLE\".Cost AS \"Cost\", \"FACTTABLE\".ACCOUNT_ID AS \"ACCOUNT_ID\""
                        + ", \"FACTTABLE\".AnalyticsPriorityWeight AS \"AnalyticsPriorityWeight\", \"FACTTABLE\".SmoothPropensity AS \"Smoo"
                        + "thPropensity\", \"FACTTABLE\".IMAGE_NAME AS \"IMAGE_NAME\", \"FACTTABLE\".CallID AS \"CallID\", \"FACTTABLE\".Co"
                        + "ntractIdKey AS \"ContractIdKey\", \"FACTTABLE\".MainProductID AS \"MainProductID\", \"FACTTABLE\".ChildProductID"
                        + " AS \"ChildProductID\", \"pyAssociationStrength\" AS \"pyAssociationStrength\", \"pyAssociatedID\" AS \"pyAssoci"
                        + "atedID\" FROM  (SELECT \"FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTAB"
                        + "LE\".pxInteractionID, \"FACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \"FACTTABLE\".pzChannelID, \"FACT"
                        + "TABLE\".pzContextID, \"FACTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FACTTABLE\".pzOutcomeID, \"FACTTA"
                        + "BLE\".pzJourneyID, \"FACTTABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACTTABLE\".pxRank, \"FACTTABLE\".p"
                        + "yExternalID, \"FACTTABLE\".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyLongitude, \"FACTTABLE\".pyPrope"
                        + "nsity, \"FACTTABLE\".pyPartitionKey, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudge"
                        + "t, \"FACTTABLE\".pyTargetBudget, \"FACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".IPAddress, \"F"
                        + "ACTTABLE\".Revenue, \"FACTTABLE\".Cost, \"FACTTABLE\".ACCOUNT_ID, \"FACTTABLE\".AnalyticsPriorityWeight, \"FACTT"
                        + "ABLE\".SmoothPropensity, \"FACTTABLE\".IMAGE_NAME, \"FACTTABLE\".CallID, \"FACTTABLE\".ContractIdKey, \"FACTTABL"
                        + "E\".MainProductID, \"FACTTABLE\".ChildProductID, null AS \"pyAssociationStrength\", null AS \"pyAssociatedID\" F"
                        + "ROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  WHERE (\"FACTTABLE\".pySubjectID IN (...))  UNION ALL SELECT \""
                        + "FACTTABLE\".pxFactID, \"FACTTABLE\".pxOutcomeTime, \"FACTTABLE\".pySubjectID, \"FACTTABLE\".pxInteractionID, \"F"
                        + "ACTTABLE\".pzActionID, \"FACTTABLE\".pzApplicationID, \"FACTTABLE\".pzChannelID, \"FACTTABLE\".pzContextID, \"FA"
                        + "CTTABLE\".pzCustomerID, \"FACTTABLE\".pzOperatorID, \"FACTTABLE\".pzOutcomeID, \"FACTTABLE\".pzJourneyID, \"FACT"
                        + "TABLE\".pxDecisionTime, \"FACTTABLE\".pxPriority, \"FACTTABLE\".pxRank, \"FACTTABLE\".pyExternalID, \"FACTTABLE\""
                        + ".pyGroupID, \"FACTTABLE\".pyLatitude, \"FACTTABLE\".pyLongitude, \"FACTTABLE\".pyPropensity, \"FACTTABLE\".pyPar"
                        + "titionKey, \"FACTTABLE\".pyRevenue, \"FACTTABLE\".pyISFactID, \"FACTTABLE\".pyMaxBudget, \"FACTTABLE\".pyTargetB"
                        + "udget, \"FACTTABLE\".pyFulfilled, \"FACTTABLE\".pyWeight, \"FACTTABLE\".IPAddress, \"FACTTABLE\".Revenue, \"FACT"
                        + "TABLE\".Cost, \"FACTTABLE\".ACCOUNT_ID, \"FACTTABLE\".AnalyticsPriorityWeight, \"FACTTABLE\".SmoothPropensity, \""
                        + "FACTTABLE\".IMAGE_NAME, \"FACTTABLE\".CallID, \"FACTTABLE\".ContractIdKey, \"FACTTABLE\".MainProductID, \"FACTTA"
                        + "BLE\".ChildProductID, \"ASSOCIATIONTABLE\".pyAssociationStrength AS \"pyAssociationStrength\", \"ASSOCIATIONTABL"
                        + "E\".pySubjectID AS \"pyAssociatedID\" FROM {Class:Data-Decision-IH-Fact} \"FACTTABLE\"  JOIN {Class:Data-Decisio"
                        + "n-IH-Association} \"ASSOCIATIONTABLE\" ON ((\"ASSOCIATIONTABLE\".pyAssociatedID = \"FACTTABLE\".pySubjectID) ) W"
                        + "HERE (\"ASSOCIATIONTABLE\".pySubjectID IN (...)) )  \"FACTTABLE\"  WHERE (\"FACTTABLE\".pxOutcomeTime >= {IHQuer"
                        + "yPage.pzStartFrom DateTime})  ORDER BY 2 DESC",
                alertMessageReportEntryKey);
    }
}
