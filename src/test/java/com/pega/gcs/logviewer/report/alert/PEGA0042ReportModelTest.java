
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0042ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0042ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Packaging of database query took more than threshold of 500 ms: 1,323 ms SQL: SELECT PYUSERNAME AS \"pyUserName\""
                + " , PYUSERIDENTIFIER AS \"pyUserIdentifier\" , PYACCESSGROUP AS \"pyAccessGroup\" , ACCESSROLE AS \"AccessRole\" , PXCREA"
                + "TEDATETIME AS \"pxCreateDateTime\" , DISABLED AS \"disabled\" , PYREPORTTO AS \"pyReportTo\" , HUBSKILL AS \"HUBSkill\" "
                + ", LEGALENTITYSKILL AS \"LegalEntitySkill\" , CLIENTTYPESKILL AS \"ClientTypeSKill\" , ACCESSROLE AS \"AccessRole\" , TEA"
                + "MIDENTIFIER AS \"TeamIdentifier\" , DATEOFDEACTIVATION AS \"DateOfDeactivation\" , JURISDICTIONSKILL AS \"Jurisdictionsk"
                + "ill\" , PYACCESSGROUPADDITIONAL AS \"PYACCESSGROUPADDITIONAL\" FROM HSBC_NGT_NGTUSER_VW WHERE ( PYACCESSGROUP = ? OR PYA"
                + "CCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? "
                + "OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROU"
                + "P = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCE"
                + "SSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? ) AND ( pxObjClass = ? )";

        LOG.info("dataText: " + dataText);

        PEGA0042ReportModel pega0042ReportModel = new PEGA0042ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0042ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "SELECT PYUSERNAME AS \"pyUserName\" , PYUSERIDENTIFIER AS \"pyUserIdentifier\" , PYACCESSGROUP AS \"pyAccessGroup\" , ACCE"
                        + "SSROLE AS \"AccessRole\" , PXCREATEDATETIME AS \"pxCreateDateTime\" , DISABLED AS \"disabled\" , PYREPORTTO AS \""
                        + "pyReportTo\" , HUBSKILL AS \"HUBSkill\" , LEGALENTITYSKILL AS \"LegalEntitySkill\" , CLIENTTYPESKILL AS \"Client"
                        + "TypeSKill\" , ACCESSROLE AS \"AccessRole\" , TEAMIDENTIFIER AS \"TeamIdentifier\" , DATEOFDEACTIVATION AS \"Date"
                        + "OfDeactivation\" , JURISDICTIONSKILL AS \"Jurisdictionskill\" , PYACCESSGROUPADDITIONAL AS \"PYACCESSGROUPADDITI"
                        + "ONAL\" FROM HSBC_NGT_NGTUSER_VW WHERE ( PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSG"
                        + "ROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = "
                        + "? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PY"
                        + "ACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSGROUP = ? OR PYACCESSG"
                        + "ROUP = ? OR PYACCESSGROUP = ? ) AND ( pxObjClass = ? )",
                alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "Packaging of database query took more than threshold of 500 ms: 824 ms   SQL: select pzPVStream , pxCommitDateTi"
                + "me  from pegadata.pr_data_admin  where pzInsKey = ?";

        LOG.info("dataText: " + dataText);

        PEGA0042ReportModel pega0042ReportModel = new PEGA0042ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0042ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertEquals(
                "select pzPVStream , pxCommitDateTime  from pegadata.pr_data_admin  where pzInsKey = ?",
                alertMessageReportEntryKey);
    }
}
