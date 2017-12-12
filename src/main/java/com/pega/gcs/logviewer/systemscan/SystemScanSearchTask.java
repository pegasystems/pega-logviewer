/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class SystemScanSearchTask extends SwingWorker<List<ScanResultHotfixEntryKey>, ProgressTaskInfo> {

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanSearchTask.class);

	private SystemScanTableModel systemScanTableModel;

	private ModalProgressMonitor mProgressMonitor;

	private Object searchStrObj;

	public SystemScanSearchTask(ModalProgressMonitor mProgressMonitor, SystemScanTableModel systemScanTableModel, Object searchStrObj) {
		super();
		this.mProgressMonitor = mProgressMonitor;
		this.systemScanTableModel = systemScanTableModel;
		this.searchStrObj = searchStrObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected List<ScanResultHotfixEntryKey> doInBackground() throws Exception {

		List<ScanResultHotfixEntryKey> searchResultList = new LinkedList<ScanResultHotfixEntryKey>();

		if (systemScanTableModel != null) {

			searchResultList = systemScanTableModel.getSearchModel().getSearchResultList(searchStrObj);

			if (searchResultList == null) {
				searchResultList = search();
			} else {
				searchResultList = update(searchResultList);
			}

		}

		return searchResultList;
	}

	private List<ScanResultHotfixEntryKey> search() {

		List<ScanResultHotfixEntryKey> searchResultList = new LinkedList<ScanResultHotfixEntryKey>();

		long before = System.currentTimeMillis();

		try {

			int logEntryCount = 0;

			List<ScanResultHotfixEntryKey> filteredList = systemScanTableModel.getFtmEntryKeyList();

			int totalSize = filteredList.size();

			Iterator<ScanResultHotfixEntryKey> fListIterator = filteredList.iterator();

			while ((!isCancelled()) && (fListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				ScanResultHotfixEntryKey key = fListIterator.next();

				boolean found = systemScanTableModel.search(key, searchStrObj);

				if (found) {
					searchResultList.add(key);
				}

				logEntryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
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

	private List<ScanResultHotfixEntryKey> update(List<ScanResultHotfixEntryKey> searchResultList) {

		long before = System.currentTimeMillis();

		try {

			int entryCount = 0;

			List<ScanResultHotfixEntryKey> filteredList = systemScanTableModel.getFtmEntryKeyList();

			int totalSize = filteredList.size() + searchResultList.size();

			Iterator<ScanResultHotfixEntryKey> fListIterator = filteredList.iterator();

			while ((!isCancelled()) && (fListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				ScanResultHotfixEntryKey key = fListIterator.next();

				// set false for all searches
				ScanResultHotfixEntry scanResultHotfixEntry = systemScanTableModel.getEventForKey(key);
				scanResultHotfixEntry.setSearchFound(false);

				entryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, entryCount);
				publish(progressTaskInfo);

			}

			// now update the log entries from search result list
			Iterator<ScanResultHotfixEntryKey> sListIterator = searchResultList.iterator();

			while ((!isCancelled()) && (sListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				ScanResultHotfixEntryKey key = sListIterator.next();

				ScanResultHotfixEntry scanResultHotfixEntry = systemScanTableModel.getEventForKey(key);
				scanResultHotfixEntry.setSearchFound(true);

				entryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, entryCount);
				publish(progressTaskInfo);
			}

		} finally {
			long diff = System.currentTimeMillis() - before;

			int secs = (int) Math.ceil((double) diff / 1E3);

			LOG.info("Search updated '" + searchStrObj + "' completed in " + secs + " secs. "
					+ searchResultList.size() + " results found.");
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

		mProgressMonitor.setProgress(progress);

		String message = String.format("Searching %d log events (%d%%)", count, progress);

		mProgressMonitor.setNote(message);
	}
}
