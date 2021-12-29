
package com.pega.gcs.logviewer.systemstate.table;

import com.pega.gcs.fringecommon.guiutilities.CustomJTable;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;

public class AnalysisMarkerTableMouseListener extends AbstractNodeTableMouseListener {

    public AnalysisMarkerTableMouseListener(SystemStateTreeNavigationController systemStateTreeNavigationController) {
        super(systemStateTreeNavigationController);
    }

    @Override
    protected String getNodeId(CustomJTable customJTable, int selectedRow) {

        AnalysisMarkerTableModel analysisMarkerTableModel = (AnalysisMarkerTableModel) customJTable.getModel();

        AnalysisMarker analysisMarker = (AnalysisMarker) analysisMarkerTableModel.getValueAt(selectedRow, 0);

        String nodeId = analysisMarker.getNodeId();

        return nodeId;
    }
}
