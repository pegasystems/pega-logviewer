/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.beans.PropertyChangeSupport;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.guiutilities.FilterColumn;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.FilterTableModelNavigation;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.guiutilities.treetable.AbstractTreeTableNode;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogEntryModel;

public class LogTableModel extends FilterTableModel<LogEntryKey> {

    private static final long serialVersionUID = 7355840960429067165L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogTableModel.class);

    private LogEntryModel logEntryModel;

    // search
    private SearchData<LogEntryKey> searchData;
    private SearchModel<LogEntryKey> searchModel;

    public LogTableModel(RecentFile recentFile, SearchData<LogEntryKey> searchData) {

        super(recentFile);
        this.searchData = searchData;

        resetModel();

    }

    public LogEntryModel getLogEntryModel() {
        return logEntryModel;
    }

    // explicitly setting the lem as the type of model will be known only after
    // parsing the log file
    public void setLogEntryModel(LogEntryModel logEntryModel) {
        LOG.debug("setting LogEntryModel: " + logEntryModel);
        this.logEntryModel = logEntryModel;

        PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        propertyChangeSupport.firePropertyChange("logEntryModel", null, logEntryModel);
    }

    @Override
    public void resetModel() {

        if (logEntryModel != null) {

            logEntryModel.resetModel();
            clearSearchResults(true);
            fireTableDataChanged();
        }

    }

    @Override
    public List<LogEntryKey> getFtmEntryKeyList() {

        List<LogEntryKey> logEntryKeyList = null;

        if (logEntryModel != null) {
            logEntryKeyList = logEntryModel.getLogEntryKeyList();
        }
        return logEntryKeyList;
    }

    @Override
    protected HashMap<LogEntryKey, Integer> getKeyIndexMap() {
        return logEntryModel.getKeyIndexMap();
    }

    @Override
    protected Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<LogEntryKey>>> getColumnFilterMap() {
        return logEntryModel.getColumnFilterMap();
    }

    @Override
    public int getColumnCount() {
        int columnCount = 0;

        if (logEntryModel != null) {
            columnCount = logEntryModel.getVisibleColumnIndexList().size();
        }

        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        List<LogEntryKey> logEntryKeyList = getFtmEntryKeyList();

        LogEntryKey logEntryKey = logEntryKeyList.get(rowIndex);
        LogEntry logEntry = getEventForKey(logEntryKey);

        return logEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getColumnValue(java. lang.Object, int)
     */
    @Override
    public String getColumnValue(Object valueAtObject, int columnIndex) {

        LogEntry logEntry = (LogEntry) valueAtObject;

        String columnValue = null;

        if (logEntry != null) {

            int modelColumnIndex = getModelColumnIndex(columnIndex);

            columnValue = logEntryModel.getFormattedLogEntryValue(logEntry, modelColumnIndex);
        }

        return columnValue;
    }

    @Override
    public String getColumnName(int column) {
        int origColumnIndex = getModelColumnIndex(column);
        return logEntryModel.getLogEntryColumn(origColumnIndex);
    }

    @Override
    protected int getModelColumnIndex(int column) {
        int origColumnIndex = logEntryModel.getVisibleColumnIndexList().get(column);
        return origColumnIndex;
    }

    @Override
    public LogEntry getEventForKey(LogEntryKey logEntryKey) {

        LogEntry logEntry = null;

        if (logEntryKey != null) {
            logEntry = logEntryModel.getLogEntry(logEntryKey);
        }

        return logEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.pega.gcs.fringecommon.guiutilities.CustomJTableModel#getTableColumnModel( )
     */
    @Override
    public TableColumnModel getTableColumnModel() {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        for (int i = 0; i < getColumnCount(); i++) {

            String text = getColumnName(i);

            TableColumn tableColumn = new TableColumn(i);
            tableColumn.setHeaderValue(text);

            int horizontalAlignment = SwingConstants.TRAILING;
            int colWidth = 70;
            LogEntryColumn logEntryColumn = null;

            logEntryColumn = LogEntryColumn.getTableColumnById(text);

            if (logEntryColumn == null) {
                LOG.error("Error getting log entry column for text: " + text);
            } else {
                horizontalAlignment = logEntryColumn.getHorizontalAlignment();
                colWidth = logEntryColumn.getPrefColumnWidth();
            }

            LogTableCellRenderer ltcr = new LogTableCellRenderer();

            ltcr.setBorder(new EmptyBorder(1, 3, 1, 1));
            ltcr.setHorizontalAlignment(horizontalAlignment);

            tableColumn.setCellRenderer(ltcr);

            tableColumn.setPreferredWidth(colWidth);
            tableColumn.setWidth(colWidth);

            tableColumn.setResizable(true);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    public AbstractLogPattern getLogPattern() {

        AbstractLogPattern abstractLogPattern = null;

        RecentFile recentFile = getRecentFile();

        if (recentFile != null) {
            abstractLogPattern = (AbstractLogPattern) recentFile.getAttribute(RecentFile.KEY_LOGFILETYPE);
        }

        return abstractLogPattern;
    }

    public TimeZone getLogTimeZone() {

        TimeZone timeZone = null;

        RecentFile recentFile = getRecentFile();

        if (recentFile != null) {
            timeZone = (TimeZone) recentFile.getAttribute(RecentFile.KEY_TIMEZONE);
        }

        return timeZone;
    }

    public void updateRecentFile(String charset, Locale locale, TimeZone timeZone) {

        LogEntryModel lem = getLogEntryModel();

        RecentFile recentFile = getRecentFile();

        if (charset != null) {
            recentFile.setAttribute(RecentFile.KEY_CHARSET, charset);
            // change in character set will trigger reloading of file.
        }

        if (locale != null) {
            recentFile.setAttribute(RecentFile.KEY_LOCALE, locale);
        }

        if (timeZone != null) {
            recentFile.setAttribute(RecentFile.KEY_TIMEZONE, timeZone);

            lem.setDisplayDateFormatTimeZone(timeZone);
        }

        fireTableDataChanged();

    }

    // performing one by one search because of showing progress in the monitor
    // also when cancelling the task we should keep the old search results
    // hence not search result is stored unless the task is completed
    @Override
    public boolean search(LogEntryKey key, Object searchStrObj) {

        boolean found = false;

        LogEntry logEntry = logEntryModel.getLogEntry(key);

        if (logEntry != null) {

            Charset charset = getCharset();

            found = logEntry.search(searchStrObj.toString(), charset);
        }

        return found;
    }

    @Override
    protected FilterTableModelNavigation<LogEntryKey> getNavigationRowIndex(List<LogEntryKey> resultList,
            int selectedRowIndex, boolean forward, boolean first, boolean last, boolean wrap) {

        int currSelectedRowIndex = selectedRowIndex;

        int navigationIndex = 0;
        int navigationRowIndex = 0;

        if ((resultList != null) && (resultList.size() > 0)) {

            int resultListSize = resultList.size();

            List<LogEntryKey> logEntryKeyList = getFtmEntryKeyList();

            int logEntryKeyListSize = logEntryKeyList.size();

            LogEntryKey logEntryKey = null;

            if (first) {

                logEntryKey = resultList.get(0);
                navigationIndex = 1;

            } else if (last) {

                int lastIndex = resultListSize - 1;
                logEntryKey = resultList.get(lastIndex);
                navigationIndex = resultListSize;

            } else if (forward) {
                // NEXT
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex < (logEntryKeyListSize - 1)) {
                        currSelectedRowIndex++;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = 0;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                LogEntryKey currSelectedLogEntryKey = logEntryKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedLogEntryKey);

                if (searchIndex >= 0) {
                    // exact search found
                    logEntryKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == resultListSize) {

                        if (wrap) {
                            searchIndex = 0;
                        } else {
                            searchIndex = resultListSize - 1;
                        }
                    }

                    logEntryKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(logEntryKey) + 1;

            } else {
                // PREVIOUS
                if (currSelectedRowIndex >= 0) {

                    if (currSelectedRowIndex > 0) {
                        currSelectedRowIndex--;
                    } else {
                        if (wrap) {
                            currSelectedRowIndex = logEntryKeyListSize - 1;
                        }
                    }
                } else {
                    currSelectedRowIndex = 0;
                }

                LogEntryKey currSelectedLogEntryKey = logEntryKeyList.get(currSelectedRowIndex);

                int searchIndex = Collections.binarySearch(resultList, currSelectedLogEntryKey);

                if (searchIndex >= 0) {
                    // exact search found
                    logEntryKey = resultList.get(searchIndex);
                } else {

                    searchIndex = (searchIndex * -1) - 1;

                    if (searchIndex == 0) {

                        if (wrap) {
                            searchIndex = resultListSize - 1;
                        } else {
                            searchIndex = 0;
                        }
                    } else {
                        searchIndex--;
                    }

                    logEntryKey = resultList.get(searchIndex);
                }

                navigationIndex = resultList.indexOf(logEntryKey) + 1;
            }

            if (logEntryKey != null) {

                navigationRowIndex = logEntryKeyList.indexOf(logEntryKey);

            } else {
                navigationRowIndex = currSelectedRowIndex;
            }

        }

        FilterTableModelNavigation<LogEntryKey> ttmn = new FilterTableModelNavigation<LogEntryKey>();
        ttmn.setNavigationIndex(navigationIndex);
        ttmn.setNavigationRowIndex(navigationRowIndex);

        return ttmn;
    }

    @Override
    public void clearSearchResults(boolean clearResults) {

        getSearchModel().resetResults(clearResults);

        LogEntryModel logEntryModel = getLogEntryModel();

        logEntryModel.clearLogEntrySearchResults();
    }

    @Override
    public AbstractTreeTableNode getTreeNodeForKey(LogEntryKey key) {
        return null;
    }

    @Override
    public SearchModel<LogEntryKey> getSearchModel() {

        if (searchModel == null) {

            searchModel = new SearchModel<LogEntryKey>(searchData) {

                @Override
                public void searchInEvents(final Object searchStrObj, final ModalProgressMonitor modalProgressMonitor) {

                    if ((searchStrObj != null) && (!"".equals(searchStrObj.toString()))) {

                        LogTableSearchTask ttst = new LogTableSearchTask(modalProgressMonitor, LogTableModel.this,
                                searchStrObj) {

                            /*
                             * (non-Javadoc)
                             * 
                             * @see javax.swing.SwingWorker#done()
                             */
                            @Override
                            protected void done() {

                                try {
                                    List<LogEntryKey> searchResultList = get();

                                    if (searchResultList != null) {
                                        // LOG.info("LogTableSearchTask
                                        // done "
                                        // + searchResultList.size() +
                                        // " entries found");
                                        // setSearchStrObj(searchStrObj);
                                        setSearchResultList(searchStrObj, searchResultList);
                                    }

                                } catch (CancellationException ce) {
                                    LOG.error("LogTableSearchTask cancelled: ", ce);
                                } catch (ExecutionException ee) {
                                    LOG.error("ExecutionException in LogTableSearchTask.", ee);
                                } catch (Exception e) {
                                    LOG.error("Exception in LogTableSearchTask.", e);
                                } finally {

                                    fireTableDataChanged();

                                    modalProgressMonitor.close();
                                }
                            }
                        };

                        ttst.execute();

                    }
                }

                @Override
                public void resetResults(boolean clearResults) {
                    // clears search result on search model and reset the search
                    // panel
                    resetSearchResults(clearResults);

                    // clear search results from within trace events and tree
                    // nodes
                    LogEntryModel logEntryModel = getLogEntryModel();
                    if (logEntryModel != null) {
                        logEntryModel.clearLogEntrySearchResults();
                    }

                    fireTableDataChanged();
                }

            };
        }

        return searchModel;
    }

    public LogEntryColumn[] getReportTableColumns() {
        return logEntryModel.getReportTableColumns();
    }

    public String getSelectedRowsData(int[] selectedRows) {

        StringBuilder selectedRowsDataSB = new StringBuilder();

        for (int selectedRow : selectedRows) {

            LogEntryKey logEntryKey = getFtmEntryKeyList().get(selectedRow);
            LogEntry logEntry = getEventForKey(logEntryKey);

            if (logEntry != null) {
                selectedRowsDataSB.append(logEntry.getLogEntryText());
                selectedRowsDataSB.append(System.getProperty("line.separator"));

            }

        }

        return selectedRowsDataSB.toString();
    }
}
