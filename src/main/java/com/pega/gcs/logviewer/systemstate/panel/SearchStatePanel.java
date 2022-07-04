
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTablePanel;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.model.ClusterStatus;
import com.pega.gcs.logviewer.systemstate.model.SearchState;
import com.pega.gcs.logviewer.systemstate.table.DSSInfoTableModel;
import com.pega.gcs.logviewer.systemstate.table.FTSSettingsTableModel;

public class SearchStatePanel extends JPanel {

    private static final long serialVersionUID = 7325140181034964359L;

    private JTabbedPane searchStateTabbedPane;

    public SearchStatePanel(SearchState searchState, RecentFile recentFile) {

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

        JPanel titlePanel = getTitlePanel(searchState);

        JTabbedPane searchStateTabbedPane = getSearchStateTabbedPane();

        add(titlePanel, gbc1);
        add(searchStateTabbedPane, gbc2);

        Dimension labelDim = new Dimension(150, 26);
        String tabLabelText = null;

        JPanel indexesInfoPanel = new IndexesInfoPanel(searchState.getIndexesInfo());
        tabLabelText = "Indexes Info";
        GUIUtilities.addTab(searchStateTabbedPane, indexesInfoPanel, tabLabelText, labelDim);

        JPanel queueInformationContainerPanel;
        queueInformationContainerPanel = new QueueInformationContainerPanel(searchState.getQueueInformationContainer());
        tabLabelText = "Queue Information";
        GUIUtilities.addTab(searchStateTabbedPane, queueInformationContainerPanel, tabLabelText, labelDim);

        FTSSettingsTableModel ftsSettingsTableModel = new FTSSettingsTableModel(
                searchState.getCleanedFTSSettings().getFullSettings(), recentFile);
        DataTablePanel ftsSettingsDataTablePanel = new DataTablePanel(ftsSettingsTableModel, true, "FTS Settings",
                this);

        tabLabelText = "FTS Settings";
        GUIUtilities.addTab(searchStateTabbedPane, ftsSettingsDataTablePanel, tabLabelText, labelDim);

        JPanel cleanedFTSSettingsPanel;
        cleanedFTSSettingsPanel = new CleanedFTSSettingsPanel(searchState.getCleanedFTSSettings());
        tabLabelText = "Other FTS Settings";
        GUIUtilities.addTab(searchStateTabbedPane, cleanedFTSSettingsPanel, tabLabelText, labelDim);

    }

    private JTabbedPane getSearchStateTabbedPane() {

        if (searchStateTabbedPane == null) {
            searchStateTabbedPane = new JTabbedPane();
        }

        return searchStateTabbedPane;
    }

    private JPanel getTitlePanel(SearchState searchState) {

        JPanel titlePanel = new JPanel();

        LayoutManager layout = new BoxLayout(titlePanel, BoxLayout.X_AXIS);
        titlePanel.setLayout(layout);

        JLabel titleLabel = new JLabel(searchState.getDisplayName());
        Font labelFont = titleLabel.getFont();
        Font titleFont = labelFont.deriveFont(Font.BOLD, 14);
        titleLabel.setFont(titleFont);

        ClusterStatus clusterStatus = searchState.getClusterStatus();

        String isIndexingEnabledText;
        isIndexingEnabledText = (clusterStatus != null) ? Boolean.toString(clusterStatus.isIndexingEnabled()) : "";

        JLabel isIndexingEnabledLabel = new JLabel("Indexing Enabled:");
        JLabel isIndexingEnabledTextLabel = new JLabel(isIndexingEnabledText);

        Dimension dim = new Dimension(10, 40);
        Dimension spacer = new Dimension(5, 40);

        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(isIndexingEnabledLabel);
        titlePanel.add(Box.createRigidArea(spacer));
        titlePanel.add(isIndexingEnabledTextLabel);
        titlePanel.add(Box.createHorizontalGlue());

        titlePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return titlePanel;
    }

}
