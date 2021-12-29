
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.DefaultTableColumn;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.CsvData;
import com.pega.gcs.logviewer.systemstate.model.CsvDataMap;
import com.pega.gcs.logviewer.systemstate.table.ReportsCsvDataTableModel;

public class ReportsCsvDataPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public ReportsCsvDataPanel(CsvDataMap csvDataMap) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 2, 0);

        ReportsCsvDataTableModel reportsCsvDataModel = new ReportsCsvDataTableModel(csvDataMap);

        DataTable<CsvData, DefaultTableColumn> reportsCsvDataTable;
        reportsCsvDataTable = new DataTable<>(reportsCsvDataModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(reportsCsvDataTable);

        scrollPane.setPreferredSize(reportsCsvDataTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

}
