/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.view;

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
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableModel;

public abstract class HotfixScanView extends JPanel implements TableModelListener {

    private static final long serialVersionUID = 8968434918075553315L;

    protected abstract void updateSupplementUtilityJPanel();

    protected abstract void performComponentResized(Rectangle oldBounds, Rectangle newBounds);

    private HotfixScanTableModel hotfixScanTableModel;

    private JPanel supplementUtilityJPanel;

    private NavigationTableController<HotfixEntryKey> navigationTableController;

    private Rectangle oldBounds;

    public HotfixScanView(HotfixScanTableModel hotfixScanTableModel, JPanel supplementUtilityJPanel,
            NavigationTableController<HotfixEntryKey> navigationTableController) {

        super();

        this.hotfixScanTableModel = hotfixScanTableModel;
        this.hotfixScanTableModel.addTableModelListener(this);

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

    protected HotfixScanTableModel getHotfixScanTableModel() {
        return hotfixScanTableModel;
    }

    protected JPanel getSupplementUtilityJPanel() {
        return supplementUtilityJPanel;
    }

    protected NavigationTableController<HotfixEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    public void switchToFront() {
        updateSupplementUtilityJPanel();
    }

    protected JPanel getMarkerBarPanel(HotfixScanTableModel hotfixScanTableModel) {

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

        MarkerBar<HotfixEntryKey> markerBar = getMarkerBar(hotfixScanTableModel);

        markerBarPanel.add(topSpacer, BorderLayout.NORTH);
        markerBarPanel.add(markerBar, BorderLayout.CENTER);
        markerBarPanel.add(bottomSpacer, BorderLayout.SOUTH);

        return markerBarPanel;
    }

    private MarkerBar<HotfixEntryKey> getMarkerBar(HotfixScanTableModel hotfixScanTableModel) {

        NavigationTableController<HotfixEntryKey> navigationTableController = getNavigationTableController();

        SearchMarkerModel<HotfixEntryKey> searchMarkerModel = new SearchMarkerModel<HotfixEntryKey>(
                hotfixScanTableModel);

        MarkerBar<HotfixEntryKey> markerBar = new MarkerBar<HotfixEntryKey>(navigationTableController,
                searchMarkerModel);

        BookmarkModel<HotfixEntryKey> bookmarkModel;
        bookmarkModel = hotfixScanTableModel.getBookmarkModel();

        markerBar.addMarkerModel(bookmarkModel);

        return markerBar;
    }

}
