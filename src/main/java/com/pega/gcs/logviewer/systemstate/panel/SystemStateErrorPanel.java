
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.model.SystemStateError;
import com.pega.gcs.logviewer.systemstate.table.SystemStateErrorTableColumn;
import com.pega.gcs.logviewer.systemstate.table.SystemStateErrorTableModel;

public class SystemStateErrorPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public SystemStateErrorPanel(SystemState systemState) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(8, 5, 2, 0);

        SystemStateErrorTableModel systemStateErrorTableModel = new SystemStateErrorTableModel(systemState);

        DataTable<SystemStateError, SystemStateErrorTableColumn> systemStateErrorTable;
        systemStateErrorTable = new DataTable<>(systemStateErrorTableModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(systemStateErrorTable);

        scrollPane.setPreferredSize(systemStateErrorTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

}
