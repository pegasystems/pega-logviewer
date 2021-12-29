
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.table.NodeStateTableColumn;
import com.pega.gcs.logviewer.systemstate.table.NodeStateTableModel;
import com.pega.gcs.logviewer.systemstate.table.NodeStateTableMouseListener;

public class NodeStateTablePanel extends JPanel {

    private static final long serialVersionUID = 3642081217750700205L;

    private SystemStateTreeNavigationController systemStateTreeNavigationController;

    private NodeStateTableModel nodeStateTableModel;

    private JComboBox<String> nodeTypeComboBox;

    public NodeStateTablePanel(SystemStateTreeNavigationController systemStateTreeNavigationController,
            SystemState systemState) {

        this.systemStateTreeNavigationController = systemStateTreeNavigationController;

        this.nodeStateTableModel = new NodeStateTableModel(systemState);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 5, 2, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 5, 2, 0);

        JPanel filterPanel = getFilterJPanel();

        DataTable<NodeState, NodeStateTableColumn> nodeStateTable;
        nodeStateTable = getNodeStateTable();

        JScrollPane scrollPane = new JScrollPane(nodeStateTable);

        scrollPane.setPreferredSize(nodeStateTable.getPreferredSize());

        add(filterPanel, gbc1);
        add(scrollPane, gbc2);
    }

    private JComboBox<String> getNodeTypeComboBox() {

        if (nodeTypeComboBox == null) {

            Set<String> nodeTypeSet = nodeStateTableModel.getNodeTypeSet();

            String[] nodeTypeArray = nodeTypeSet.toArray(new String[nodeTypeSet.size()]);

            nodeTypeComboBox = new JComboBox<>(nodeTypeArray);
            nodeTypeComboBox.setEditable(false);

            Dimension preferredSize = new Dimension(300, 22);

            nodeTypeComboBox.setPreferredSize(preferredSize);

            nodeTypeComboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    String nodeType;
                    nodeType = (String) nodeTypeComboBox.getSelectedItem();

                    nodeStateTableModel.applyFilter(nodeType);
                }
            });

        }
        return nodeTypeComboBox;
    }

    private JPanel getFilterJPanel() {

        JPanel filterPanel = new JPanel();

        filterPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 10, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 0.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 10, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 2;
        gbc3.gridy = 0;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(10, 5, 10, 0);

        JLabel filterLabel = new JLabel("Node Type Filter");
        JComboBox<String> nodeTypeComboBox = getNodeTypeComboBox();
        JLabel filler = new JLabel();

        filterPanel.add(filterLabel, gbc1);
        filterPanel.add(nodeTypeComboBox, gbc2);
        filterPanel.add(filler, gbc3);

        filterPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return filterPanel;

    }

    private DataTable<NodeState, NodeStateTableColumn> getNodeStateTable() {

        DataTable<NodeState, NodeStateTableColumn> nodeStateTable;
        nodeStateTable = new DataTable<>(nodeStateTableModel, JTable.AUTO_RESIZE_OFF);

        NodeStateTableMouseListener nodeStateTableMouseListener;
        nodeStateTableMouseListener = new NodeStateTableMouseListener(systemStateTreeNavigationController);

        nodeStateTable.addMouseListener(nodeStateTableMouseListener);

        return nodeStateTable;

    }

}
