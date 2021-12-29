
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.CodeSetVersion;
import com.pega.gcs.logviewer.systemstate.model.CodeSetVersionInfo;
import com.pega.gcs.logviewer.systemstate.table.CodeSetVersionInfoTableColumn;
import com.pega.gcs.logviewer.systemstate.table.CodeSetVersionInfoTableModel;

public class CodeSetVersionInfoPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public CodeSetVersionInfoPanel(CodeSetVersionInfo codeSetVersionInfo) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 2, 0);

        CodeSetVersionInfoTableModel codeSetVersionInfoModel = new CodeSetVersionInfoTableModel(codeSetVersionInfo);

        DataTable<CodeSetVersion, CodeSetVersionInfoTableColumn> codeSetVersionInfoTable;
        codeSetVersionInfoTable = new DataTable<>(codeSetVersionInfoModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(codeSetVersionInfoTable);

        scrollPane.setPreferredSize(codeSetVersionInfoTable.getPreferredSize());

        add(scrollPane, gbc1);

    }

}
