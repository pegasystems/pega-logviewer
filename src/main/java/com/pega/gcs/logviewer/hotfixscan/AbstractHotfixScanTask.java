/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.ScanResult;

public abstract class AbstractHotfixScanTask extends SwingWorker<Void, ProgressTaskInfo> {

    private static final Log4j2Helper LOG = new Log4j2Helper(AbstractHotfixScanTask.class);

    private Component parent;

    private HotfixScanTableModel hotfixScanTableModel;

    // avoid on loading right file during compare mode
    private boolean loadNotInstalledHotfixes;

    private ModalProgressMonitor progressMonitor;

    private boolean wait;

    public abstract Date getInventoryTimestamp();

    public abstract Map<String, String> getProductInfoMap();

    public abstract List<String> getHotfixColumnNameList();

    public abstract Map<Integer, List<String>> getHotfixDataMap();

    public AbstractHotfixScanTask(Component parent, HotfixScanTableModel hotfixScanTableModel,
            boolean loadNotInstalledHotfixes, ModalProgressMonitor progressMonitor, boolean wait) {

        this.parent = parent;
        this.hotfixScanTableModel = hotfixScanTableModel;
        this.loadNotInstalledHotfixes = loadNotInstalledHotfixes;
        this.progressMonitor = progressMonitor;
        this.wait = wait;
    }

    private Component getParent() {
        return parent;
    }

    private boolean isLoadNotInstalledHotfixes() {
        return loadNotInstalledHotfixes;
    }

    protected HotfixScanTableModel getHotfixScanTableModel() {
        return hotfixScanTableModel;
    }

    protected ModalProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @Override
    protected void process(List<ProgressTaskInfo> chunks) {

        if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
            return;
        }

        Collections.sort(chunks);

        ProgressTaskInfo progressTaskInfo = chunks.get(chunks.size() - 1);

        int progressVal = (int) progressTaskInfo.getCount();
        String note = progressTaskInfo.getNote();

        ModalProgressMonitor progressMonitor = getProgressMonitor();

        if (progressMonitor != null) {
            progressMonitor.setProgress(progressVal);
            progressMonitor.setNote(note);
        }
    }

    protected void setProgressAndNote(int progressVal, String note) {

        ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(-1, progressVal, note);

        publish(progressTaskInfo);
    }

    @Override
    protected void done() {
        if (!wait) {
            completeLoad();
        }
    }

    public void completeTask() {

        if (wait) {
            completeLoad();
        }
    }

    private void completeLoad() {

        String filePath = hotfixScanTableModel.getFilePath();

        Message.MessageType messageType = MessageType.INFO;

        StringBuilder messageB = null;

        try {

            get();

            Date inventoryTimestamp = getInventoryTimestamp();

            Map<String, String> productInfoMap = getProductInfoMap();

            // v6 returns null here. use default columns list and avoid parsing
            List<String> hotfixColumnNameList = getHotfixColumnNameList();

            List<HotfixColumn> hotfixColumnList = getHotfixColumnList(hotfixColumnNameList);

            Map<Integer, List<String>> hotfixDataMap = getHotfixDataMap();

            ScanResult scanResult = new ScanResult(inventoryTimestamp, hotfixColumnList);

            boolean loadNotInstalledHotfixes = isLoadNotInstalledHotfixes();

            scanResult.processHotfixMaps(productInfoMap, hotfixDataMap, loadNotInstalledHotfixes);

            hotfixScanTableModel.setScanResult(scanResult);

            messageB = new StringBuilder();

            messageB.append(filePath);
            messageB.append(". Hotfix Installed = ");
            messageB.append(scanResult.getInstalledHotfixCount());
            messageB.append(". Not Installed = ");
            messageB.append(scanResult.getNotInstalledHotfixCount());
            messageB.append(".");

        } catch (CancellationException ce) {

            LOG.error("System Scan Task - Cancelled " + filePath);

            messageType = MessageType.ERROR;

            messageB = new StringBuilder();

            messageB.append(filePath);
            messageB.append(" - file loading cancelled.");

        } catch (ExecutionException ee) {

            LOG.error("Execution Error in System Scan Task", ee);

            messageType = MessageType.ERROR;

            messageB = new StringBuilder();

            Component parent = getParent();

            if (ee.getCause() instanceof OutOfMemoryError) {

                messageB.append("Out Of Memory Error has occured while loading ");
                messageB.append(filePath);
                messageB.append(".\nPlease increase the JVM's max heap size (-Xmx) and try again.");

                JOptionPane.showMessageDialog(parent, messageB.toString(), "Out Of Memory Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                messageB.append(ee.getCause().getMessage());
                messageB.append(" has occured while loading ");
                messageB.append(filePath);
                messageB.append(".");

                JOptionPane.showMessageDialog(parent, messageB.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {

            LOG.error("Error loading file: " + filePath, e);

            messageType = MessageType.ERROR;

            messageB = new StringBuilder();

            messageB.append(filePath);
            messageB.append(". Error - ");
            messageB.append(e.getMessage());
        } finally {

            RecentFile recentFile = hotfixScanTableModel.getRecentFile();

            BookmarkContainer<HotfixEntryKey> bookmarkContainer;
            bookmarkContainer = (BookmarkContainer<HotfixEntryKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);

            if (bookmarkContainer == null) {

                bookmarkContainer = new BookmarkContainer<HotfixEntryKey>();

                recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
            }

            bookmarkContainer = (BookmarkContainer<HotfixEntryKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);

            BookmarkModel<HotfixEntryKey> bookmarkModel = new BookmarkModel<HotfixEntryKey>(bookmarkContainer,
                    hotfixScanTableModel);

            hotfixScanTableModel.setBookmarkModel(bookmarkModel);

            hotfixScanTableModel.fireTableDataChanged();

            try {
                progressMonitor.close();
            } catch (Exception e) {
                // close can throw NPE because during compare the same PM is changed into
                // indetermintae which in turn calls close
            }

            Message message = new Message(messageType, messageB.toString());
            hotfixScanTableModel.setMessage(message);

        }

    }

    private List<HotfixColumn> getHotfixColumnList(List<String> hotfixColumnNameList) {

        List<HotfixColumn> hotfixColumnList = null;

        if ((hotfixColumnNameList != null) && (hotfixColumnNameList.size() > 0)) {

            hotfixColumnList = new ArrayList<>();

            for (String hotfixColumnName : hotfixColumnNameList) {

                HotfixColumn hotfixColumn = HotfixColumn.fromValue(hotfixColumnName);

                if (hotfixColumn == null) {
                    hotfixColumn = new HotfixColumn(hotfixColumnName, 100, SwingConstants.LEFT, true, hotfixColumnName);
                }

                hotfixColumnList.add(hotfixColumn);
            }

        } else {
            hotfixColumnList = HotfixColumn.getCatalogHotfixColumnList();
        }

        return hotfixColumnList;
    }

}
