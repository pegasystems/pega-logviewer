
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.DSSInfo;
import com.pega.gcs.logviewer.systemstate.model.Setting;
import com.pega.gcs.logviewer.systemstate.table.DSSInfoTableColumn;
import com.pega.gcs.logviewer.systemstate.table.DSSInfoTableModel;

public class DSSInfoPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public DSSInfoPanel(DSSInfo dassInfo) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 2, 0);

        DSSInfoTableModel dassInfoModel = new DSSInfoTableModel(dassInfo);

        DataTable<Setting, DSSInfoTableColumn> dassInfoTable;
        dassInfoTable = new DataTable<>(dassInfoModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(dassInfoTable);

        scrollPane.setPreferredSize(dassInfoTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

}
