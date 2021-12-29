
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.SystemStateUtil;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;

public class ClusterStatePanel extends JPanel {

    private static final long serialVersionUID = 7325140181034964359L;

    private JTabbedPane clusterStateTabbedPane;

    public ClusterStatePanel(ClusterState clusterState) {

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

        JPanel codesetVersionInfoPanel = new CodeSetVersionInfoPanel(clusterState.getCodeSetVersionInfo());
        JPanel rulesetVersionInfoPanel = new RuleSetVersionInfoPanel(clusterState.getRuleSetVersionInfo());
        JPanel dassInfoPanel = new DSSInfoPanel(clusterState.getDassInfo());

        Dimension labelDim = new Dimension(200, 26);

        String tabLabelText = "Codeset Version Info";
        GUIUtilities.addTab(clusterStateTabbedPane, codesetVersionInfoPanel, tabLabelText, labelDim);

        tabLabelText = "Ruleset Version Info";
        GUIUtilities.addTab(clusterStateTabbedPane, rulesetVersionInfoPanel, tabLabelText, labelDim);

        tabLabelText = "DASS Info";
        GUIUtilities.addTab(clusterStateTabbedPane, dassInfoPanel, tabLabelText, labelDim);

    }

    private JTabbedPane getClusterStateTabbedPane() {

        if (clusterStateTabbedPane == null) {
            clusterStateTabbedPane = new JTabbedPane();
        }

        return clusterStateTabbedPane;
    }

}
