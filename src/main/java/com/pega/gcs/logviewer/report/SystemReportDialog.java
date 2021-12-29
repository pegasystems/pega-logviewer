/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainerPanel;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntryKey;

public abstract class SystemReportDialog extends JFrame implements TableModelListener {

    private static final Log4j2Helper LOG = new Log4j2Helper(SystemReportDialog.class);

    private static final long serialVersionUID = 8438579888122942639L;

    private LogTableModel logTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private JTabbedPane reportTabbedPane;

    protected abstract void buildTabs();

    public SystemReportDialog(String title, LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, ImageIcon appIcon, Component parent) {

        super();

        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;

        logTableModel.addTableModelListener(this);

        setIconImage(appIcon.getImage());

        // setPreferredSize(new Dimension(1200, 600));

        setTitle(title);
        // setResizable(true);
        // setModalityType(ModalityType.MODELESS);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        // setAlwaysOnTop(true);

        setLocationRelativeTo(parent);

        // setVisible called by caller.
        // setVisible(true);
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {

        if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
            rebuildOverview();
        }
    }

    private void rebuildOverview() {

        LOG.info("rebuildOverview()");

        JTabbedPane reportTabbedPane = getReportTabbedPane();

        reportTabbedPane.removeAll();

        buildTabs();

        validate();
        repaint();
    }

    public void destroyFrame() {
        LogTableModel logTableModel = getLogTableModel();
        logTableModel.removeTableModelListener(this);
        setVisible(false);
    }

    protected JTabbedPane getReportTabbedPane() {

        if (reportTabbedPane == null) {
            reportTabbedPane = new JTabbedPane();
        }

        return reportTabbedPane;
    }

    protected LogTableModel getLogTableModel() {
        return logTableModel;
    }

    protected NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    protected JComponent getMainJPanel() {

        JPanel mainJPanel = new JPanel();
        mainJPanel.setLayout(new BorderLayout());

        JTabbedPane reportTabbedPane = getReportTabbedPane();
        mainJPanel.add(reportTabbedPane, BorderLayout.CENTER);

        buildTabs();

        return mainJPanel;
    }

    protected void addDefaultTabs() {

        LogTableModel logTableModel = getLogTableModel();

        BookmarkModel<LogEntryKey> bookmarkModel = logTableModel.getBookmarkModel();
        Object searchStrObj = logTableModel.getSearchModel().getSearchStrObj();

        boolean containsBookmark = (bookmarkModel != null) ? bookmarkModel.getMarkerCount() > 0 : false;

        boolean containsSearch = (searchStrObj != null) ? true : false;

        JTabbedPane reportTabbedPane = getReportTabbedPane();

        Dimension labelDim = new Dimension(120, 26);

        if (containsSearch) {

            JPanel searchResultsPanel = getSearchResultsPanel();

            String tabLabelText = "Search Results";

            GUIUtilities.addTab(reportTabbedPane, searchResultsPanel, tabLabelText, labelDim);

        }

        if (containsBookmark) {

            JPanel bookmarkContainerPanel = getBookmarkContainerPanel();

            String tabLabelText = "Bookmarks";

            GUIUtilities.addTab(reportTabbedPane, bookmarkContainerPanel, tabLabelText, labelDim);

        }
    }

    private JPanel getSearchResultsPanel() {

        LogTableModel logTableModel = getLogTableModel();

        NavigationTableController<LogEntryKey> navigationTableController;
        navigationTableController = getNavigationTableController();

        JPanel searchResultsPanel = new SearchResultsPanel(logTableModel, navigationTableController,
                SystemReportDialog.this);

        return searchResultsPanel;
    }

    private BookmarkContainerPanel<LogEntryKey> getBookmarkContainerPanel() {

        final LogTableModel logTableModel = getLogTableModel();

        BookmarkModel<LogEntryKey> bookmarkModel = logTableModel.getBookmarkModel();

        BookmarkContainerPanel<LogEntryKey> bookmarkContainerPanel;
        bookmarkContainerPanel = new BookmarkContainerPanel<LogEntryKey>(bookmarkModel, navigationTableController) {

            private static final long serialVersionUID = 5672957295689747776L;

            @Override
            public FilterTableModel<LogEntryKey> getFilterTableModel() {
                return logTableModel;
            }
        };

        return bookmarkContainerPanel;
    }

}
