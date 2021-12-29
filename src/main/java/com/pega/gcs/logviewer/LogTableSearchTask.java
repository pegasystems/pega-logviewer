/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class LogTableSearchTask extends SwingWorker<List<LogEntryKey>, ProgressTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogTableSearchTask.class);

    private LogTableModel logTableModel;

    private ModalProgressMonitor modalProgressMonitor;

    private Object searchStrObj;

    public LogTableSearchTask(ModalProgressMonitor modalProgressMonitor, LogTableModel logTableModel,
            Object searchStrObj) {
        super();
        this.modalProgressMonitor = modalProgressMonitor;
        this.logTableModel = logTableModel;
        this.searchStrObj = searchStrObj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected List<LogEntryKey> doInBackground() throws Exception {

        List<LogEntryKey> searchResultList = null;

        if (logTableModel != null) {

            searchResultList = logTableModel.getSearchModel().getSearchResultList(searchStrObj);

            if (searchResultList == null) {
                searchResultList = search();
            } else {
                searchResultList = update(searchResultList);
            }

        }

        return searchResultList;
    }

    private List<LogEntryKey> search() {

        ArrayList<LogEntryKey> searchResultList = new ArrayList<>();

        long before = System.currentTimeMillis();

        try {

            int logEntryCount = 0;

            List<LogEntryKey> filteredList = logTableModel.getFtmEntryKeyList();

            int totalSize = filteredList.size();

            ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
            publish(progressTaskInfo);

            Iterator<LogEntryKey> filteredListIterator = filteredList.iterator();

            while ((!isCancelled()) && (filteredListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                LogEntryKey key = filteredListIterator.next();

                boolean found = logTableModel.search(key, searchStrObj);

                if (found) {
                    searchResultList.add(key);
                }

                logEntryCount++;

                progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
                publish(progressTaskInfo);

            }

        } finally {
            long diff = System.currentTimeMillis() - before;

            int secs = (int) Math.ceil((double) diff / 1E3);

            LOG.info("Search '" + searchStrObj + "' completed in " + secs + " secs. " + searchResultList.size()
                    + " results found.");
        }

        return searchResultList;
    }

    private List<LogEntryKey> update(List<LogEntryKey> searchResultList) {

        long before = System.currentTimeMillis();

        try {

            int logEntryCount = 0;

            List<LogEntryKey> filteredList = logTableModel.getFtmEntryKeyList();

            int totalSize = filteredList.size() + searchResultList.size();

            ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
            publish(progressTaskInfo);

            Iterator<LogEntryKey> filteredListIterator = filteredList.iterator();

            while ((!isCancelled()) && (filteredListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                LogEntryKey key = filteredListIterator.next();

                // set false for all searches
                LogEntry logEntry = logTableModel.getEventForKey(key);
                logEntry.setSearchFound(false);

                logEntryCount++;

                progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
                publish(progressTaskInfo);

            }

            // now update the log entries from search result list
            Iterator<LogEntryKey> searchResultListIterator = searchResultList.iterator();

            while ((!isCancelled()) && (searchResultListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                LogEntryKey key = searchResultListIterator.next();

                // set false for all searches
                LogEntry logEntry = logTableModel.getEventForKey(key);
                logEntry.setSearchFound(true);

                logEntryCount++;

                progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
                publish(progressTaskInfo);
            }

        } finally {
            long diff = System.currentTimeMillis() - before;

            int secs = (int) Math.ceil((double) diff / 1E3);

            LOG.info("Search updated '" + searchStrObj + "' completed in " + secs + " secs. " + searchResultList.size()
                    + " results found.");
        }

        return searchResultList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#process(java.util.List)
     */
    @Override
    protected void process(List<ProgressTaskInfo> chunks) {
        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        Collections.sort(chunks);

        ProgressTaskInfo progressTaskInfo = chunks.get(chunks.size() - 1);

        long total = progressTaskInfo.getTotal();
        long count = progressTaskInfo.getCount();

        int progress = (int) ((count * 100) / total);

        modalProgressMonitor.setProgress(progress);

        String message = String.format("Searching %d log events (%d%%)", count, progress);

        modalProgressMonitor.setNote(message);
    }
}
