/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import com.pega.gcs.logviewer.logfile.LogFileType;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryModel;
import com.pega.gcs.logviewer.model.LogIntervalMarker;
import com.pega.gcs.logviewer.model.LogSeriesCollection;

public class LogTableModel extends FilterTableModel<Integer> {

	private static final long serialVersionUID = 7355840960429067165L;

	private static final Log4j2Helper LOG = new Log4j2Helper(LogTableModel.class);

	private LogEntryModel logEntryModel;

	// search
	private SearchData<Integer> searchData;
	private SearchModel<Integer> searchModel;

	public LogTableModel(RecentFile recentFile, SearchData<Integer> searchData) {

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
	public List<Integer> getFtmEntryKeyList() {

		List<Integer> logEntryIndexList = null;

		if (logEntryModel != null) {
			logEntryIndexList = logEntryModel.getLogEntryIndexList();
		}
		return logEntryIndexList;
	}

	@Override
	protected Map<FilterColumn, List<CheckBoxMenuItemPopupEntry<Integer>>> getColumnFilterMap() {
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

		List<Integer> logEntryIndexList = getFtmEntryKeyList();

		Integer logEntryIndex = logEntryIndexList.get(rowIndex);
		LogEntry logEntry = getEventForKey(logEntryIndex);

		return logEntry;
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
	public LogEntry getEventForKey(Integer logEntryIndex) {

		LogEntry logEntry = null;

		if (logEntryIndex != null) {
			logEntry = logEntryModel.getLogEntry(logEntryIndex);
		}

		return logEntry;
	}

	@Override
	protected TableColumnModel getTableColumnModel() {

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

	public LogFileType getLogFileType() {

		LogFileType logFileType = null;

		RecentFile recentFile = getRecentFile();

		if (recentFile != null) {
			logFileType = (LogFileType) recentFile.getAttribute(RecentFile.KEY_LOGFILETYPE);
		}

		return logFileType;
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
			lem.setLocale(locale);
		}

		if (timeZone != null) {
			recentFile.setAttribute(RecentFile.KEY_TIMEZONE, timeZone);
			DateFormat displayDateFormat;
			displayDateFormat = lem.getDisplayDateFormat();
			displayDateFormat.setTimeZone(timeZone);
		}

		fireTableDataChanged();

	}

	public String getFormattedLogEntryValue(LogEntry logEntry, int tableColumn) {

		int modelColumn = getModelColumnIndex(tableColumn);

		String text = logEntryModel.getFormattedLogEntryValue(logEntry, modelColumn);

		return text;
	}

	// performing one by one search because of showing progress in the monitor
	// also when cancelling the task we should keep the old search results
	// hence not search result is stored unless the task is completed
	@Override
	public boolean search(Integer key, Object searchStrObj) {

		boolean found = false;

		LogEntry logEntry = logEntryModel.getLogEntry(key);

		if (logEntry != null) {

			found = logEntry.search(searchStrObj.toString());

		}

		return found;
	}

	@Override
	protected FilterTableModelNavigation<Integer> getNavigationRowIndex(List<Integer> resultList, int selectedRowIndex,
			boolean forward, boolean first, boolean last, boolean wrap) {

		int currSelectedRowIndex = selectedRowIndex;

		int navigationIndex = 0;
		int navigationRowIndex = 0;

		if ((resultList != null) && (resultList.size() > 0)) {

			int resultListSize = resultList.size();

			List<Integer> logEntryIndexList = getFtmEntryKeyList();

			int logEntryIndexListSize = logEntryIndexList.size();

			Integer logEntryKey = null;

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

					if (currSelectedRowIndex < (logEntryIndexListSize - 1)) {
						currSelectedRowIndex++;
					} else {
						if (wrap) {
							currSelectedRowIndex = 0;
						}
					}
				} else {
					currSelectedRowIndex = 0;
				}

				Integer currSelectedLogEntryIndex = logEntryIndexList.get(currSelectedRowIndex);

				int searchIndex = Collections.binarySearch(resultList, currSelectedLogEntryIndex);

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
							currSelectedRowIndex = logEntryIndexListSize - 1;
						}
					}
				} else {
					currSelectedRowIndex = 0;
				}

				int currSelectedLogEntryIndex = logEntryIndexList.get(currSelectedRowIndex);

				int searchIndex = Collections.binarySearch(resultList, currSelectedLogEntryIndex);

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

				navigationRowIndex = logEntryIndexList.indexOf(logEntryKey);

			} else {
				navigationRowIndex = currSelectedRowIndex;
			}

		}

		FilterTableModelNavigation<Integer> ttmn = new FilterTableModelNavigation<Integer>();
		ttmn.setNavigationIndex(navigationIndex);
		ttmn.setNavigationRowIndex(navigationRowIndex);

		return ttmn;
	}

	public Integer getClosestLogEntryIndex(long time) throws ParseException {
		// int logEntryRowIndex = -1;
		Integer logEntryIndex = null;

		Map<Long, Integer> timeLogEntryKeyMap = logEntryModel.getTimeLogEntryKeyMap();

		LinkedList<Long> timeKeyList = new LinkedList<Long>(timeLogEntryKeyMap.keySet());

		Collections.sort(timeKeyList);

		long firstTime = timeKeyList.get(0);

		long lastTime = timeKeyList.get(timeKeyList.size() - 1);

		if ((time >= firstTime) && (time <= lastTime)) {

			int index = Collections.binarySearch(timeKeyList, time);
			Long timeKey = null;

			if (index < 0) {
				index = (index * -1) - 1;

				// find closest index
				if (index > 0) {

					int prevIndex = index - 1;
					long prevtimeKey = timeKeyList.get(prevIndex);
					timeKey = timeKeyList.get(index);

					long diff1 = time - prevtimeKey;
					long diff2 = timeKey - time;

					if (diff1 < diff2) {
						timeKey = prevtimeKey;
					}

				} else {
					timeKey = timeKeyList.get(index);
				}
			} else {
				// exact match
				timeKey = timeKeyList.get(index);
			}

			logEntryIndex = timeLogEntryKeyMap.get(timeKey);

			// List<Integer> logEntryIndexList = getFtmEntryKeyList();
			//
			// logEntryRowIndex = logEntryIndexList.indexOf(logEntryIndex);
		}

		return logEntryIndex;
	}

	@Override
	public void clearSearchResults(boolean clearResults) {

		getSearchModel().resetResults(clearResults);

		clearLogEntrySearchResults();
	}

	protected void clearLogEntrySearchResults() {

		List<Integer> filteredList = getFtmEntryKeyList();

		if (filteredList != null) {

			Iterator<Integer> fListIterator = filteredList.iterator();

			while (fListIterator.hasNext()) {

				Integer key = fListIterator.next();

				LogEntry logEntry = getEventForKey(key);
				logEntry.setSearchFound(false);
			}
		}
	}

	@Override
	public int getIndexOfKey(Integer key) {

		List<Integer> logEntryIndexList = getFtmEntryKeyList();

		int index = -1;

		if (logEntryIndexList != null) {
			index = logEntryIndexList.indexOf(key);
		}

		return index;
	}

	@Override
	public AbstractTreeTableNode getTreeNodeForKey(Integer key) {
		return null;
	}

	@Override
	public SearchModel<Integer> getSearchModel() {

		if (searchModel == null) {

			searchModel = new SearchModel<Integer>(searchData) {

				@Override
				public boolean searchInEvents(final Object searchStrObj, final ModalProgressMonitor mProgressMonitor) {

					boolean search = false;

					if ((searchStrObj != null) && (!"".equals(searchStrObj.toString()))) {

						search = true;

						LogTableSearchTask ttst = new LogTableSearchTask(mProgressMonitor, LogTableModel.this,
								searchStrObj) {

							/*
							 * (non-Javadoc)
							 * 
							 * @see javax.swing.SwingWorker#done()
							 */
							@Override
							protected void done() {

								try {
									List<Integer> searchResultList = get();

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

									mProgressMonitor.close();
								}
							}
						};

						ttst.execute();

					}

					return search;
				}

				@Override
				public void resetResults(boolean aClearResults) {
					// clears search result on search model and reset the search
					// panel
					resetSearchResults(aClearResults);

					// clear search results from within trace events and tree
					// nodes
					clearLogEntrySearchResults();

					fireTableDataChanged();
				}

			};
		}

		return searchModel;
	}

	public LogEntryColumn[] getReportTableColumns() {
		return logEntryModel.getReportTableColumns();
	}
}
