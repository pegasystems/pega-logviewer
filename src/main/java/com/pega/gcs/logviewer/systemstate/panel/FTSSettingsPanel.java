
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
import com.pega.gcs.logviewer.systemstate.model.FTSSettings;
import com.pega.gcs.logviewer.systemstate.table.FTSSettingsTableModel;

public class FTSSettingsPanel extends JPanel {

    private static final long serialVersionUID = -8286696738677813599L;

    public FTSSettingsPanel(FTSSettings ftsSettings) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(8, 5, 2, 0);

        FTSSettingsTableModel ftsSettingsTableModel = new FTSSettingsTableModel(ftsSettings);

        DataTable<CsvData, DefaultTableColumn> ftsSettingsTable;
        ftsSettingsTable = new DataTable<>(ftsSettingsTableModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(ftsSettingsTable);

        scrollPane.setPreferredSize(ftsSettingsTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

}
