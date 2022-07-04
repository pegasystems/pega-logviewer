
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTablePanel;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;
import com.pega.gcs.logviewer.systemstate.model.CsvDataMap;
import com.pega.gcs.logviewer.systemstate.model.JVMInfo;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.RequestorsResult;
import com.pega.gcs.logviewer.systemstate.table.DatabaseInfoTableModel;
import com.pega.gcs.logviewer.systemstate.table.ReportsCsvDataTableModel;

public class NodeStatePanel extends JPanel {

    private static final long serialVersionUID = 7325140181034964359L;

    private JTabbedPane nodeInfoTabbedPane;

    public NodeStatePanel(NodeState nodeState, List<AnalysisMarker> analysisMarkerList) {

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

        JPanel nodePanel = getNodePanel(nodeState);
        JTabbedPane nodeInfoTabbedPane = getNodeInfoTabbedPane();

        add(nodePanel, gbc1);
        add(nodeInfoTabbedPane, gbc2);

        JPanel jvmInfoPanel = new JVMInfoPanel(nodeState.getJvmInfo());
        JPanel osInfoPanel = new OSInfoPanel(nodeState.getOsInfo());

        DatabaseInfoTableModel databaseInfoTableModel = new DatabaseInfoTableModel(nodeState.getDatabaseInfo());
        DataTablePanel databaseInfoTablePanel = new DataTablePanel(databaseInfoTableModel, false, "DatabaseInfo", this);
        JPanel prLoggingPanel = new PRLoggingPanel(nodeState.getPrLogging());
        JPanel prConfigPanel = new PRConfigPanel(nodeState.getPrConfig());

        JPanel analysisMarkerPanel = new AnalysisMarkerPanel(null, analysisMarkerList);

        Dimension labelDim = new Dimension(150, 26);
        String tabLabelText = null;

        tabLabelText = "JVM Info";
        GUIUtilities.addTab(nodeInfoTabbedPane, jvmInfoPanel, tabLabelText, labelDim);

        tabLabelText = "OS Info";
        GUIUtilities.addTab(nodeInfoTabbedPane, osInfoPanel, tabLabelText, labelDim);

        tabLabelText = "Database Info";
        GUIUtilities.addTab(nodeInfoTabbedPane, databaseInfoTablePanel, tabLabelText, labelDim);

        tabLabelText = "PRLogging";
        GUIUtilities.addTab(nodeInfoTabbedPane, prLoggingPanel, tabLabelText, labelDim);

        tabLabelText = "PRConfig";
        GUIUtilities.addTab(nodeInfoTabbedPane, prConfigPanel, tabLabelText, labelDim);

        RequestorsResult requestorsResult = nodeState.getRequestorsResult();

        if (requestorsResult != null) {
            JPanel requestorResultPanel = new RequestorResultPanel(requestorsResult);
            tabLabelText = "Requestors Result";
            GUIUtilities.addTab(nodeInfoTabbedPane, requestorResultPanel, tabLabelText, labelDim);
        }

        CsvDataMap databaseClassReport = nodeState.getDatabaseClassReport();

        if (databaseClassReport != null) {

            tabLabelText = databaseClassReport.getReportCsvName();

            ReportsCsvDataTableModel reportsCsvDataTableModel = new ReportsCsvDataTableModel(databaseClassReport);
            DataTablePanel reportsCsvDataTablePanel = new DataTablePanel(reportsCsvDataTableModel, false, tabLabelText,
                    this);

            GUIUtilities.addTab(nodeInfoTabbedPane, reportsCsvDataTablePanel, tabLabelText, labelDim);
        }

        tabLabelText = "Analysis Markers";
        GUIUtilities.addTab(nodeInfoTabbedPane, analysisMarkerPanel, tabLabelText, labelDim);

    }

    private JTabbedPane getNodeInfoTabbedPane() {

        if (nodeInfoTabbedPane == null) {
            nodeInfoTabbedPane = new JTabbedPane();
        }

        return nodeInfoTabbedPane;
    }

    private JPanel getNodePanel(NodeState nodeState) {

        JPanel nodePanel = new JPanel();

        LayoutManager layout = new BoxLayout(nodePanel, BoxLayout.X_AXIS);
        nodePanel.setLayout(layout);

        JVMInfo jvmInfo = nodeState.getJvmInfo();
        List<String> nodeTypeList = (jvmInfo != null) ? jvmInfo.getNodeTypeList() : null;
        String nodeTypeListStr = (nodeTypeList != null) ? nodeTypeList.toString() : "<Error getting Node Type>";

        String nodeInfo = nodeState.getNodeId() + " " + nodeTypeListStr;

        JLabel nodeInfoLabel = new JLabel(nodeInfo);
        Font labelFont = nodeInfoLabel.getFont();
        Font nodeInfoFont = labelFont.deriveFont(Font.BOLD, 14);
        nodeInfoLabel.setFont(nodeInfoFont);

        Dimension dim = new Dimension(10, 40);

        nodePanel.add(Box.createRigidArea(dim));
        nodePanel.add(nodeInfoLabel);
        nodePanel.add(Box.createHorizontalGlue());

        nodePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return nodePanel;
    }

}
