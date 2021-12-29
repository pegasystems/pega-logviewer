
package com.pega.gcs.logviewer.systemstate.table;

import com.pega.gcs.fringecommon.guiutilities.CustomJTable;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.model.NodeState;

public class NodeStateTableMouseListener extends AbstractNodeTableMouseListener {

    public NodeStateTableMouseListener(SystemStateTreeNavigationController systemStateTreeNavigationController) {
        super(systemStateTreeNavigationController);
    }

    @Override
    protected String getNodeId(CustomJTable customJTable, int selectedRow) {

        NodeStateTableModel nodeStateTableModel = (NodeStateTableModel) customJTable.getModel();

        NodeState nodeState = (NodeState) nodeStateTableModel.getValueAt(selectedRow, 0);

        String nodeId = nodeState.getDisplayName();

        return nodeId;
    }
}
