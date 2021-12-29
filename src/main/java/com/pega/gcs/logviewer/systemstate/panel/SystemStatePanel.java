
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.SystemStateUtil;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarkerListNodeMap;
import com.pega.gcs.logviewer.systemstate.model.CsvDataMap;
import com.pega.gcs.logviewer.systemstate.model.SystemState;

public class SystemStatePanel extends JPanel {

    private static final long serialVersionUID = -5296797202587197599L;

    private JTabbedPane systemStateTabbedPane;

    public SystemStatePanel(SystemStateTreeNavigationController systemStateTreeNavigationController,
            SystemState systemState) {

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

        JPanel titlePanel = SystemStateUtil.getTitlePanel(systemState);

        JTabbedPane systemStateTabbedPane = getSystemStateTabbedPane();

        add(titlePanel, gbc1);
        add(systemStateTabbedPane, gbc2);

        Dimension labelDim = new Dimension(150, 26);

        JPanel systemStateTablePanel = new NodeStateTablePanel(systemStateTreeNavigationController, systemState);
        String tabLabelText = "Node List";
        GUIUtilities.addTab(systemStateTabbedPane, systemStateTablePanel, tabLabelText, labelDim);

        Set<CsvDataMap> reportCsvDataMapSet = systemState.getReportCsvDataMapSet();

        for (CsvDataMap csvDataMap : reportCsvDataMapSet) {

            JPanel reportsCsvDataPanel = new ReportsCsvDataPanel(csvDataMap);
            tabLabelText = csvDataMap.getReportCsvName();
            GUIUtilities.addTab(systemStateTabbedPane, reportsCsvDataPanel, tabLabelText, labelDim);
        }

        boolean systemStateError = systemState.getErrorSet() != null;

        if (systemStateError) {
            JPanel systemStateErrorPanel = new SystemStateErrorPanel(systemState);
            tabLabelText = "Error";
            GUIUtilities.addTab(systemStateTabbedPane, systemStateErrorPanel, tabLabelText, labelDim);
        }

        List<AnalysisMarker> analysisMarkerList = null;

        AnalysisMarkerListNodeMap analysisMarkerListNodeMap = systemState.getAnalysisMarkerListNodeMap();

        if (analysisMarkerListNodeMap != null) {
            analysisMarkerList = analysisMarkerListNodeMap.getAnalysisMarkerList();
        }

        JPanel analysisMarkerPanel = new AnalysisMarkerPanel(systemStateTreeNavigationController, analysisMarkerList);
        tabLabelText = "Analysis Markers";
        GUIUtilities.addTab(systemStateTabbedPane, analysisMarkerPanel, tabLabelText, labelDim);
    }

    private JTabbedPane getSystemStateTabbedPane() {

        if (systemStateTabbedPane == null) {
            systemStateTabbedPane = new JTabbedPane();
        }

        return systemStateTabbedPane;
    }

}
