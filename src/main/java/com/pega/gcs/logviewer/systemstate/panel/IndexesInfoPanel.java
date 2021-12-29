
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.IndexInfo;
import com.pega.gcs.logviewer.systemstate.model.IndexesInfo;
import com.pega.gcs.logviewer.systemstate.table.IndexInfoTableColumn;
import com.pega.gcs.logviewer.systemstate.table.IndexInfoTableModel;

public class IndexesInfoPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public IndexesInfoPanel(IndexesInfo indexesInfo) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(5, 5, 5, 5);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 5);

        JPanel defaultIndexesPanel = getIndexInfoTablePanel("Default Indexes", indexesInfo.getDefaultIndexInfoSet());
        JPanel dedicatedIndexesPanel = getIndexInfoTablePanel("Dedicated Indexes",
                indexesInfo.getDedicatedIndexInfoSet());
        JPanel customIndexesPanel = getIndexInfoTablePanel("Custom Indexes", indexesInfo.getCustomIndexInfoSet());

        add(defaultIndexesPanel, gbc1);
        add(dedicatedIndexesPanel, gbc2);
        add(customIndexesPanel, gbc3);

    }

    private JPanel getIndexInfoTablePanel(String title, TreeSet<IndexInfo> indexeInfoSet) {

        JPanel indexInfoTablePanel = new JPanel();

        indexInfoTablePanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        IndexInfoTableModel indexInfoTableModel;
        indexInfoTableModel = new IndexInfoTableModel(indexeInfoSet);

        DataTable<IndexInfo, IndexInfoTableColumn> indexInfoTable;
        indexInfoTable = new DataTable<>(indexInfoTableModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(indexInfoTable);
        scrollPane.setPreferredSize(indexInfoTable.getPreferredSize());

        indexInfoTablePanel.add(scrollPane, gbc1);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        indexInfoTablePanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, title));

        return indexInfoTablePanel;
    }
}
