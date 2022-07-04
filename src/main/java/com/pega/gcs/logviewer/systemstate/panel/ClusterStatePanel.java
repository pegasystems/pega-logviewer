
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTablePanel;
import com.pega.gcs.logviewer.systemstate.SystemStateUtil;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.table.CodeSetVersionInfoTableModel;
import com.pega.gcs.logviewer.systemstate.table.DSSInfoTableModel;
import com.pega.gcs.logviewer.systemstate.table.RuleSetVersionInfoTableModel;

public class ClusterStatePanel extends JPanel {

    private static final long serialVersionUID = 7325140181034964359L;

    private JTabbedPane clusterStateTabbedPane;

    public ClusterStatePanel(ClusterState clusterState, RecentFile recentFile) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel titlePanel = SystemStateUtil.getTitlePanel(clusterState);

        JTabbedPane clusterStateTabbedPane = getClusterStateTabbedPane();

        add(titlePanel, gbc1);
        add(clusterStateTabbedPane, gbc2);

        CodeSetVersionInfoTableModel codeSetVersionInfoTableModel;
        codeSetVersionInfoTableModel = new CodeSetVersionInfoTableModel(clusterState.getCodeSetVersionInfo());
        DataTablePanel codeSetVersionInfoTablePanel = new DataTablePanel(codeSetVersionInfoTableModel, false,
                "CodesetInfo", this);

        RuleSetVersionInfoTableModel ruleSetVersionInfoTableModel;
        ruleSetVersionInfoTableModel = new RuleSetVersionInfoTableModel(clusterState.getRuleSetVersionInfo());
        DataTablePanel ruleSetVersionInfoTablePanel = new DataTablePanel(ruleSetVersionInfoTableModel, false,
                "RulesetInfo", this);

        DSSInfoTableModel dassInfoModel = new DSSInfoTableModel(clusterState.getDassInfo(), recentFile);
        DataTablePanel dassInfoDataTablePanel = new DataTablePanel(dassInfoModel, true, "DASS", this);

        Dimension labelDim = new Dimension(200, 26);

        String tabLabelText = "Codeset Version Info";
        GUIUtilities.addTab(clusterStateTabbedPane, codeSetVersionInfoTablePanel, tabLabelText, labelDim);

        tabLabelText = "Ruleset Version Info";
        GUIUtilities.addTab(clusterStateTabbedPane, ruleSetVersionInfoTablePanel, tabLabelText, labelDim);

        tabLabelText = "DASS Info";
        GUIUtilities.addTab(clusterStateTabbedPane, dassInfoDataTablePanel, tabLabelText, labelDim);

    }

    private JTabbedPane getClusterStateTabbedPane() {

        if (clusterStateTabbedPane == null) {
            clusterStateTabbedPane = new JTabbedPane();
        }

        return clusterStateTabbedPane;
    }

}
