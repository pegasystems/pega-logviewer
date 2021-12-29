/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.v7;

import java.awt.Component;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.catalog.CatalogManagerWrapper;
import com.pega.gcs.logviewer.hotfixscan.AbstractHotfixScanTask;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableModel;

public class HotfixScanTaskV7 extends AbstractHotfixScanTask {

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixScanTaskV7.class);

    private static final String REGEX_SCAN_RESULT_FILENAME = "ScanResults_(.*)\\.";

    // ScanResults_20171012T120532.211 GMT.zip
    private static final String INV_TIMESTAMP_FORMAT = "yyyyMMdd'T'HHmmss'.'SSS z";

    private static final String FILENAME_PRODUCT_INFO = "ProductInstallInfo_";

    private static final String FILENAME_HOTFIX_INFO = "Hotfix_";

    private Date inventoryTimestamp;

    private Map<String, String> productInfoMap;

    private List<String> hotfixColumnNameList;

    private Map<Integer, List<String>> hotfixDataMap;

    private DateFormat invTimestampDateFormat;

    public HotfixScanTaskV7(Component parent, HotfixScanTableModel hotfixScanTableModel,
            boolean loadNotInstalledHotfixes, ModalProgressMonitor progressMonitor, boolean wait) {

        super(parent, hotfixScanTableModel, loadNotInstalledHotfixes, progressMonitor, wait);

        productInfoMap = new HashMap<>();

        hotfixColumnNameList = new ArrayList<>();

        hotfixDataMap = new HashMap<>();

        String filename = hotfixScanTableModel.getModelName();

        Pattern pattern = Pattern.compile(REGEX_SCAN_RESULT_FILENAME);
        Matcher patternMatcher = pattern.matcher(filename);
        boolean matches = patternMatcher.find();

        invTimestampDateFormat = new SimpleDateFormat(INV_TIMESTAMP_FORMAT);

        if (matches) {
            String timestampStr = patternMatcher.group(1).trim();
            try {
                inventoryTimestamp = invTimestampDateFormat.parse(timestampStr);
            } catch (ParseException e) {
                LOG.error("HotfixScanTaskV7 - couldnt parse timestamp from string: " + timestampStr + " - "
                        + e.getMessage());
            }
        } else {
            LOG.error("HotfixScanTaskV7 - couldnt get timestamp from string: " + filename);
        }

    }

    @Override
    public Date getInventoryTimestamp() {
        return inventoryTimestamp;
    }

    @Override
    public Map<String, String> getProductInfoMap() {
        return productInfoMap;
    }

    @Override
    public List<String> getHotfixColumnNameList() {
        return hotfixColumnNameList;
    }

    @Override
    public Map<Integer, List<String>> getHotfixDataMap() {
        return hotfixDataMap;
    }

    @Override
    protected Void doInBackground() throws Exception {

        long before = System.currentTimeMillis();

        HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

        String systemScanFilePath = hotfixScanTableModel.getFilePath();

        ModalProgressMonitor progressMonitor = getProgressMonitor();

        setProgressAndNote(0, "Loading Catalog File ...");

        // trigger Catalog file load if any, so that the progress monitor is used
        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();
        catalogManagerWrapper.isCatalogFileAvailable();

        setProgressAndNote(0, "Loading Scan Results File ...");

        try (ZipFile zipFile = new ZipFile(systemScanFilePath)) {

            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            while (zipEntries.hasMoreElements()) {

                ZipEntry zipEntry = zipEntries.nextElement();

                LOG.debug("Extracting: " + zipEntry);

                InputStream is = zipFile.getInputStream(zipEntry);

                InputStreamReader inputStreamReader = new InputStreamReader(is, "UTF-8");

                if (zipEntry.getName().indexOf(FILENAME_PRODUCT_INFO) != -1) {

                    processProductInfoCSV(inputStreamReader);

                } else if (zipEntry.getName().indexOf(FILENAME_HOTFIX_INFO) != -1) {

                    Map<Integer, List<String>> dataMap;
                    dataMap = GeneralUtilities.getDataMapFromCSV(inputStreamReader, progressMonitor);
                    Integer headerKey = -1;
                    hotfixColumnNameList = dataMap.remove(headerKey);
                    hotfixDataMap = dataMap;

                    // processHotfixInfoCSV(inputStreamReader);

                } else {
                    LOG.info("Ignoring file: " + zipEntry);
                }

                if (progressMonitor.isCanceled()) {
                    break;
                }
            }

            LOG.info(hotfixDataMap.size() + " rows processed.");

        } finally {

            if (progressMonitor.isCanceled()) {
                cancel(true);
            }

            setProgressAndNote(100, "Completed processing file");

            long diff = System.currentTimeMillis() - before;

            int secs = (int) Math.ceil((double) diff / 1E3);

            LOG.info("Scan results parsed in " + secs + " secs.");
        }

        return null;
    }

    private void processProductInfoCSV(Reader reader) throws Exception {

        CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);

        Iterator<CSVRecord> csvRecordIt = csvParser.iterator();

        boolean headerRecord = true;

        ModalProgressMonitor progressMonitor = getProgressMonitor();

        while (csvRecordIt.hasNext()) {

            CSVRecord csvRecord = csvRecordIt.next();

            List<String> recordDataList = getRecordDataList(csvRecord);

            if (headerRecord) {
                headerRecord = false;
            } else {

                String key = recordDataList.get(0);
                String value = recordDataList.get(1);

                productInfoMap.put(key, value);
            }

            if (progressMonitor.isCanceled()) {
                break;
            }
        }
    }

    private void processHotfixInfoCSV(Reader reader) throws Exception {

        CSVParser csvParser = CSVFormat.DEFAULT.parse(reader);

        Iterator<CSVRecord> csvRecordIt = csvParser.iterator();

        boolean headerRecord = true;

        int index = 0;

        ModalProgressMonitor progressMonitor = getProgressMonitor();

        while (csvRecordIt.hasNext()) {

            CSVRecord csvRecord = csvRecordIt.next();

            List<String> recordDataList = getRecordDataList(csvRecord);

            if (headerRecord) {

                headerRecord = false;
                hotfixColumnNameList = recordDataList;

            } else {

                hotfixDataMap.put(Integer.valueOf(index), recordDataList);
                index++;
            }

            if (progressMonitor.isCanceled()) {
                break;
            }
        }
    }

    private List<String> getRecordDataList(CSVRecord csvRecord) {

        List<String> recordDataList = new ArrayList<>();

        Iterator<String> colIt = csvRecord.iterator();

        while (colIt.hasNext()) {

            String col = colIt.next();

            recordDataList.add(col);
        }

        return recordDataList;
    }

}
