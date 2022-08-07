
package com.pega.gcs.logviewer.dataflow.lifecycleevent.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerBar;
import com.pega.gcs.fringecommon.guiutilities.search.SearchMarkerModel;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventKey;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventTableModel;

public abstract class LifeCycleEventsView extends JPanel implements TableModelListener {

    private static final long serialVersionUID = 8968434918075553315L;

    protected abstract void updateSupplementUtilityPanel();

    protected abstract void performComponentResized(Rectangle oldBounds, Rectangle newBounds);

    private LifeCycleEventTableModel lifeCycleEventTableModel;

    private JPanel supplementUtilityJPanel;

    private NavigationTableController<LifeCycleEventKey> navigationTableController;

    private Rectangle oldBounds;

    public LifeCycleEventsView(LifeCycleEventTableModel lifeCycleEventTableModel, JPanel supplementUtilityJPanel,
            NavigationTableController<LifeCycleEventKey> navigationTableController) {

        super();

        this.lifeCycleEventTableModel = lifeCycleEventTableModel;
        this.lifeCycleEventTableModel.addTableModelListener(this);

        this.supplementUtilityJPanel = supplementUtilityJPanel;
        this.navigationTableController = navigationTableController;

        oldBounds = new Rectangle(1915, 941);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent componentEvent) {

                Rectangle newBounds = componentEvent.getComponent().getBounds();

                if (!oldBounds.equals(newBounds)) {
                    try {
                        performComponentResized(oldBounds, newBounds);
                    } finally {
                        oldBounds = newBounds;
                    }
                }
            }
        });
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
            revalidate();
            repaint();
        }
    }

    protected LifeCycleEventTableModel getLifeCycleEventTableModel() {
        return lifeCycleEventTableModel;
    }

    protected JPanel getSupplementUtilityJPanel() {
        return supplementUtilityJPanel;
    }

    protected NavigationTableController<LifeCycleEventKey> getNavigationTableController() {
        return navigationTableController;
    }

    public void switchToFront() {
        updateSupplementUtilityPanel();
    }

    protected JPanel getMarkerBarPanel(LifeCycleEventTableModel lifeCycleEventTableModel) {

        JPanel markerBarPanel = new JPanel();
        markerBarPanel.setLayout(new BorderLayout());

        Dimension topDimension = new Dimension(16, 28);

        JLabel topSpacer = new JLabel();
        topSpacer.setPreferredSize(topDimension);
        topSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        Dimension bottomDimension = new Dimension(16, 17);

        JLabel bottomSpacer = new JLabel();
        bottomSpacer.setPreferredSize(bottomDimension);
        bottomSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        MarkerBar<LifeCycleEventKey> markerBar = getMarkerBar(lifeCycleEventTableModel);

        markerBarPanel.add(topSpacer, BorderLayout.NORTH);
        markerBarPanel.add(markerBar, BorderLayout.CENTER);
        markerBarPanel.add(bottomSpacer, BorderLayout.SOUTH);

        return markerBarPanel;
    }

    private MarkerBar<LifeCycleEventKey> getMarkerBar(LifeCycleEventTableModel lifeCycleEventTableModel) {

        NavigationTableController<LifeCycleEventKey> navigationTableController = getNavigationTableController();

        SearchMarkerModel<LifeCycleEventKey> searchMarkerModel = new SearchMarkerModel<>(lifeCycleEventTableModel);

        MarkerBar<LifeCycleEventKey> markerBar = new MarkerBar<>(navigationTableController, searchMarkerModel);

        BookmarkModel<LifeCycleEventKey> bookmarkModel;
        bookmarkModel = lifeCycleEventTableModel.getBookmarkModel();

        markerBar.addMarkerModel(bookmarkModel);

        return markerBar;
    }
}
