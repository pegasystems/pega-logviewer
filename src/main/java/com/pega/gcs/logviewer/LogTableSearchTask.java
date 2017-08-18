/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogEntry;

public class LogTableSearchTask extends SwingWorker<List<Integer>, ProgressTaskInfo> {

	private static final Log4j2Helper LOG = new Log4j2Helper(LogTableSearchTask.class);

	private LogTableModel logTableModel;

	private ModalProgressMonitor mProgressMonitor;

	private Object searchStrObj;

	public LogTableSearchTask(ModalProgressMonitor mProgressMonitor, LogTableModel logTableModel, Object searchStrObj) {
		super();
		this.mProgressMonitor = mProgressMonitor;
		this.logTableModel = logTableModel;
		this.searchStrObj = searchStrObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected List<Integer> doInBackground() throws Exception {

		List<Integer> searchResultList = new LinkedList<Integer>();

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

	private List<Integer> search() {

		List<Integer> searchResultList = new LinkedList<Integer>();

		long before = System.currentTimeMillis();

		try {

			int logEntryCount = 0;

			List<Integer> filteredList = logTableModel.getFtmEntryKeyList();

			int totalSize = filteredList.size();

			Iterator<Integer> fListIterator = filteredList.iterator();

			while ((!isCancelled()) && (fListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				Integer key = fListIterator.next();

				boolean found = logTableModel.search(key, searchStrObj);

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

	private List<Integer> update(List<Integer> searchResultList) {

		long before = System.currentTimeMillis();

		try {

			int logEntryCount = 0;

			List<Integer> filteredList = logTableModel.getFtmEntryKeyList();

			int totalSize = filteredList.size() + searchResultList.size();

			Iterator<Integer> fListIterator = filteredList.iterator();

			while ((!isCancelled()) && (fListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				Integer key = fListIterator.next();

				// set false for all searches
				LogEntry logEntry = logTableModel.getEventForKey(key);
				logEntry.setSearchFound(false);

				logEntryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
				publish(progressTaskInfo);

			}

			// now update the log entries from search result list
			Iterator<Integer> sListIterator = searchResultList.iterator();

			while ((!isCancelled()) && (sListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				Integer key = sListIterator.next();

				// set false for all searches
				LogEntry logEntry = logTableModel.getEventForKey(key);
				logEntry.setSearchFound(true);

				logEntryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);
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

	private List<Integer> update_old() {

		long before = System.currentTimeMillis();

		List<Integer> searchResultList = logTableModel.getSearchModel().getSearchResultList(searchStrObj);

		try {

			int logEntryCount = 0;

			List<Integer> filteredList = logTableModel.getFtmEntryKeyList();

			int totalSize = filteredList.size();

			Iterator<Integer> fListIterator = filteredList.iterator();

			while ((!isCancelled()) && (fListIterator.hasNext())) {

				if (mProgressMonitor.isCanceled()) {
					cancel(true);
				}

				Integer key = fListIterator.next();

				int index = Collections.binarySearch(searchResultList, key);

				boolean searchFound = false;

				if (index >= 0) {
					searchFound = true;
				}

				LogEntry logEntry = logTableModel.getEventForKey(key);
				logEntry.setSearchFound(searchFound);

				logEntryCount++;

				ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(totalSize, logEntryCount);

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
