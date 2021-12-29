
package com.pega.gcs.logviewer.report.alert;

import org.junit.jupiter.api.Test;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class PEGA0005ReportModelTest {

    private static final Log4j2Helper LOG = new Log4j2Helper(PEGA0005ReportModelTest.class);

    @Test
    public void testGetAlertMessageReportEntryKeyString1() {

        String dataText = "Database operation took more than the threshold of 500 ms: 518 ms    "
                + "SQL: select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", "
                + "pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_work "
                + "where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.pc_Work_ProcessEmail where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", "
                + "pyStatusWork as \"pyStatusWork\" from DATA.pfw_nps_survey where pxCoverInsKey = ? "
                + "UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", "
                + "pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.CPM_WORK where "
                + "pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.pfw_cs_work where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", "
                + "pyStatusWork as \"pyStatusWork\" from DATA.pca_alert where pxCoverInsKey = ? UNION "
                + "ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey "
                + "as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.scm_work where "
                + "pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.SCM_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", "
                + "pyStatusWork as \"pyStatusWork\" from DATA.pca_work where pxCoverInsKey = ? UNION "
                + "ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey "
                + "as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pc_work_accel where "
                + "pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.LOANCONV_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", "
                + "pyStatusWork as \"pyStatusWork\" from DATA.CPMFS_Work where pxCoverInsKey = ? UNION "
                + "ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey "
                + "as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.pca_nps_survey where "
                + "pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.pfw_km_work where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", "
                + "pyStatusWork as \"pyStatusWork\" from DATA.PC_WORK_DSM_BATCH where pxCoverInsKey = ? "
                + "UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", "
                + "pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from DATA.ABR_DICTIONARY_WORK "
                + "where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", pxCoverInsKey "
                + "as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" from "
                + "DATA.PCA_QUALITYRVW where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", "
                + "pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.simplereq_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", "
                + "pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.cpm_work where pxCoverInsKey = ? UNION ALL select pxObjClass as \"pxObjClass\", "
                + "pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork as \"pyStatusWork\" "
                + "from DATA.CPM_WORK_VIEWCLIENT where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork "
                + "as \"pyStatusWork\" from DATA.paf_work where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork "
                + "as \"pyStatusWork\" from DATA.pc_pegatask where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork "
                + "as \"pyStatusWork\" from DATA.ACCMGMT_WORK where pxCoverInsKey = ? UNION ALL select pxObjClass "
                + "as \"pxObjClass\", pxCoverInsKey as \"pxCoverInsKey\", pzInsKey as \"pzInsKey\", pyStatusWork "
                + "as \"pyStatusWork\" from DATA.ACCMGMT_MAINTAIN where pxCoverInsKey = ?";

        LOG.info("dataText: " + dataText);

        PEGA0005ReportModel pega0005ReportModel = new PEGA0005ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0005ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertNotNull(alertMessageReportEntryKey);
    }

    @Test
    public void testGetAlertMessageReportEntryKeyString2() {

        String dataText = "metadata.getPrimaryKeys took more than the threshold of 500 ms: 1,387 ms SQL:";

        LOG.info("dataText: " + dataText);

        PEGA0005ReportModel pega0005ReportModel = new PEGA0005ReportModel(null, 0, null, null);

        String alertMessageReportEntryKey;
        alertMessageReportEntryKey = pega0005ReportModel.getAlertMessageReportEntryKey(dataText);

        LOG.info("alertMessageReportEntryKey: " + alertMessageReportEntryKey);

        org.junit.jupiter.api.Assertions.assertNotNull(alertMessageReportEntryKey);
    }

}
