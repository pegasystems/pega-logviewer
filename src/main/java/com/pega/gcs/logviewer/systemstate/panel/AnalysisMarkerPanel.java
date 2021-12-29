
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;
import com.pega.gcs.logviewer.systemstate.table.AnalysisMarkerTableColumn;
import com.pega.gcs.logviewer.systemstate.table.AnalysisMarkerTableModel;
import com.pega.gcs.logviewer.systemstate.table.AnalysisMarkerTableMouseListener;

public class AnalysisMarkerPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    private SystemStateTreeNavigationController systemStateTreeNavigationController;

    private AnalysisMarkerTableModel analysisMarkerTableModel;

    public AnalysisMarkerPanel(SystemStateTreeNavigationController systemStateTreeNavigationController,
            List<AnalysisMarker> analysisMarkerList) {

        this.systemStateTreeNavigationController = systemStateTreeNavigationController;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(8, 5, 2, 0);

        analysisMarkerTableModel = new AnalysisMarkerTableModel(analysisMarkerList);

        DataTable<AnalysisMarker, AnalysisMarkerTableColumn> analysisMarkerTable;
        analysisMarkerTable = getAnalysisMarkerTable();

        JScrollPane scrollPane = new JScrollPane(analysisMarkerTable);

        scrollPane.setPreferredSize(analysisMarkerTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

    private DataTable<AnalysisMarker, AnalysisMarkerTableColumn> getAnalysisMarkerTable() {

        DataTable<AnalysisMarker, AnalysisMarkerTableColumn> analysisMarkerTable;
        analysisMarkerTable = new DataTable<>(analysisMarkerTableModel, JTable.AUTO_RESIZE_OFF);

        AnalysisMarkerTableMouseListener analysisMarkerTableMouseListener;
        analysisMarkerTableMouseListener = new AnalysisMarkerTableMouseListener(systemStateTreeNavigationController);

        analysisMarkerTable.addMouseListener(analysisMarkerTableMouseListener);

        return analysisMarkerTable;

    }

}
