/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;

public class HotfixSearchTask extends SwingWorker<List<HotfixEntryKey>, ProgressTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixSearchTask.class);

    private HotfixTableModel hotfixTableModel;

    private ModalProgressMonitor modalProgressMonitor;

    private Object searchStrObj;

    public HotfixSearchTask(ModalProgressMonitor modalProgressMonitor, HotfixTableModel hotfixTableModel,
            Object searchStrObj) {
        super();
        this.modalProgressMonitor = modalProgressMonitor;
        this.hotfixTableModel = hotfixTableModel;
        this.searchStrObj = searchStrObj;
    }

    @Override
    protected List<HotfixEntryKey> doInBackground() throws Exception {

        List<HotfixEntryKey> searchResultList = new ArrayList<HotfixEntryKey>();

        if (hotfixTableModel != null) {

            // should also update the hotfixentry record list as well, hence not using the
            // stored search results, but doing fresh search every time.

            // searchResultList =
            // hotfixTableModel.getSearchModel().getSearchResultList(searchStrObj);

            // if (searchResultList == null) {
            searchResultList = search();
            // } else {
            // searchResultList = update(searchResultList);
            // }

        }

        return searchResultList;
    }

    private List<HotfixEntryKey> search() {

        List<HotfixEntryKey> searchResultList = new ArrayList<HotfixEntryKey>();

        long before = System.currentTimeMillis();

        try {

            int searchCount = 0;

            List<HotfixEntryKey> filteredList = hotfixTableModel.getFtmEntryKeyList();

            int totalSize = filteredList.size();

            Iterator<HotfixEntryKey> filteredListIterator = filteredList.iterator();

            String searchStr = searchStrObj.toString().toUpperCase();

            while ((!isCancelled()) && (filteredListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                HotfixEntryKey key = filteredListIterator.next();

                boolean found = hotfixTableModel.search(key, searchStr);

                if (found) {
                    searchResultList.add(key);
                }

                searchCount++;

                ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, searchCount);
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

    // should also update the hotfixentry record list as well, hence not using the
    // stored search results, but doing fresh search every time.
    @SuppressWarnings("unused")
    private List<HotfixEntryKey> update(List<HotfixEntryKey> searchResultList) {

        long before = System.currentTimeMillis();

        try {

            int entryCount = 0;

            List<HotfixEntryKey> filteredList = hotfixTableModel.getFtmEntryKeyList();

            int totalSize = filteredList.size() + searchResultList.size();

            Iterator<HotfixEntryKey> filteredListIterator = filteredList.iterator();

            while ((!isCancelled()) && (filteredListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                HotfixEntryKey key = filteredListIterator.next();

                // set false for all searches
                HotfixEntry hotfixEntry = hotfixTableModel.getEventForKey(key);
                hotfixEntry.clearSearch();

                entryCount++;

                ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, entryCount);
                publish(progressTaskInfo);

            }

            // now update the log entries from search result list
            Iterator<HotfixEntryKey> searchResultListIterator = searchResultList.iterator();

            while ((!isCancelled()) && (searchResultListIterator.hasNext())) {

                if (modalProgressMonitor.isCanceled()) {
                    cancel(true);
                }

                HotfixEntryKey key = searchResultListIterator.next();

                HotfixEntry hotfixEntry = hotfixTableModel.getEventForKey(key);
                // hotfixEntry.setSearchFound(true); // fix compilation

                entryCount++;

                ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, entryCount);
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
