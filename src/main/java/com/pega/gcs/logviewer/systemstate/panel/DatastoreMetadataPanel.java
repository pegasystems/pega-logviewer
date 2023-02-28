
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTablePanel;
import com.pega.gcs.logviewer.systemstate.SystemStateUtil;
import com.pega.gcs.logviewer.systemstate.model.DatastoreMetadata;
import com.pega.gcs.logviewer.systemstate.table.DatastoreMetadataTableModel;

public class DatastoreMetadataPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public DatastoreMetadataPanel(DatastoreMetadata datastoreMetadata) {

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

        JPanel titlePanel = SystemStateUtil.getTitlePanel(datastoreMetadata);

        DatastoreMetadataTableModel datastoreMetadataTableModel = new DatastoreMetadataTableModel(datastoreMetadata);
        DataTablePanel datastoreMetadataTablePanel = new DataTablePanel(datastoreMetadataTableModel, false,
                "DatastoreMetadata", this);

        add(titlePanel, gbc1);
        add(datastoreMetadataTablePanel, gbc2);
    }

}
