/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan.v7;

import java.awt.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.systemscan.AbstractSystemScanTask;
import com.pega.gcs.logviewer.systemscan.SystemScanTableModel;

public class SystemScanTaskV7 extends AbstractSystemScanTask {

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanTaskV7.class);

	private static final String REGEX_SCAN_RESULT_FILENAME = "ScanResults_(.*?)\\.";

	// ScanResults_20171012T120532.211 GMT.zip
	private static final String INV_TIMESTAMP_FORMAT = "yyyyMMdd'T'HHmmss'.'SSS z";

	@SuppressWarnings("unused")
	private static final String FILENAME_PRODUCT_INFO = "ProductInstallInfo_";

	private static final String FILENAME_HOTFIX_INFO = "Hotfix_";

	private Date inventoryTimestamp;

	private LinkedList<String> hotfixColumnList;

	private Map<Integer, List<String>> hotfixDataMap;

	private DateFormat invTimestampDateFormat;

	public SystemScanTaskV7(Component parent, SystemScanTableModel systemScanTableModel,
			ModalProgressMonitor progressMonitor, boolean wait) {

		super(parent, systemScanTableModel, progressMonitor, wait);

		hotfixColumnList = new LinkedList<>();

		hotfixDataMap = new HashMap<>();

		String filename = systemScanTableModel.getModelName();

		Pattern pattern = Pattern.compile(REGEX_SCAN_RESULT_FILENAME);
		Matcher patternMatcher = pattern.matcher(filename);
		boolean matches = patternMatcher.find();

		invTimestampDateFormat = new SimpleDateFormat(INV_TIMESTAMP_FORMAT);

		if (matches) {
			String timestampStr = patternMatcher.group(1).trim();
			try {
				inventoryTimestamp = invTimestampDateFormat.parse(timestampStr);
			} catch (ParseException e) {
				LOG.error("SystemScanTaskV7 - couldnt parse timestamp from string: " + timestampStr + " - "
						+ e.getMessage());
			}
		} else {
			LOG.error("SystemScanTaskV7 - couldnt get timestamp from string: " + filename);
		}

	}

	@Override
	public Date getCatalogTimestamp() {
		return null;
	}

	@Override
	public Date getInventoryTimestamp() {
		return inventoryTimestamp;
	}

	@Override
	public List<String> getHotfixColumnList() {
		return hotfixColumnList;
	}

	@Override
	public Map<Integer, List<String>> getHotfixDataMap() {
		return hotfixDataMap;
	}

	@Override
	protected Void doInBackground() throws Exception {

		long before = System.currentTimeMillis();

		String note = "";

		setProgressAndNote(0, "Loading Scan Result File ...");

		SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

		String systemScanFilePath = systemScanTableModel.getFilePath();

		ModalProgressMonitor progressMonitor = getProgressMonitor();

		try (ZipFile zipFile = new ZipFile(systemScanFilePath)) {

			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

			while (zipEntries.hasMoreElements()) {

				ZipEntry zipEntry = zipEntries.nextElement();

				LOG.debug("Extracting: " + zipEntry);

				InputStream is = zipFile.getInputStream(zipEntry);

				InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");

				if (zipEntry.getName().indexOf(FILENAME_HOTFIX_INFO) != -1) {

					processHotfixInfoCSV(inputStreamReader);

				} else {
					LOG.info("Ignoring file: " + zipEntry);
				}

				if (progressMonitor.isCanceled()) {
					break;
				}
			}

			note = "Completed processing hotfixes.";

			LOG.info(hotfixDataMap.size() + " hotfixes found.");

		} finally {

			if (progressMonitor.isCanceled()) {
				cancel(true);
			}

			setProgressAndNote(100, note);

			long diff = System.currentTimeMillis() - before;

			int secs = (int) Math.ceil((double) diff / 1E3);

			LOG.info("Inventory parsed in " + secs + " secs.");
		}

		return null;
	}

	private void processHotfixInfoCSV(Reader reader) throws Exception {

		CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);

		Iterator<CSVRecord> csvRecordIt = csvParser.iterator();

		boolean headerRecord = true;

		int index = 0;

		ModalProgressMonitor progressMonitor = getProgressMonitor();

		while (csvRecordIt.hasNext()) {

			CSVRecord csvRecord = csvRecordIt.next();

			LinkedList<String> recordDataList = getRecordDataList(csvRecord);

			if (headerRecord) {

				headerRecord = false;
				hotfixColumnList = recordDataList;

			} else {

				hotfixDataMap.put(new Integer(index), recordDataList);
				index++;
			}

			if (progressMonitor.isCanceled()) {
				break;
			}
		}
	}

	private LinkedList<String> getRecordDataList(CSVRecord csvRecord) {

		LinkedList<String> recordDataList = new LinkedList<>();

		Iterator<String> colIt = csvRecord.iterator();

		while (colIt.hasNext()) {

			String col = colIt.next();

			recordDataList.add(col);
		}

		return recordDataList;
	}

}
