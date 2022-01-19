/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import com.pega.gcs.fringecommon.guiutilities.ButtonTabComponent;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanMainPanel;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.socketreceiver.SocketReceiverLogMainPanel;
import com.pega.gcs.logviewer.systemstate.SystemStateMainPanel;

public class LogTabbedPane extends JTabbedPane implements DropTargetListener {

    private static final long serialVersionUID = 8534656255850550268L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogTabbedPane.class);

    private LogViewerSetting logViewerSetting;

    private RecentFileContainer recentFileContainer;

    private Map<String, Integer> fileTabIndexMap;

    private List<String> tailingFileList;

    private Border normalBorder;

    public LogTabbedPane(LogViewerSetting logViewerSetting, RecentFileContainer recentFileContainer) {
        super();

        this.logViewerSetting = logViewerSetting;
        this.recentFileContainer = recentFileContainer;

        fileTabIndexMap = new LinkedHashMap<String, Integer>();

        tailingFileList = new ArrayList<>();

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        normalBorder = getBorder();

        try {
            DropTarget dt = new DropTarget();
            dt.addDropTargetListener(this);
            setDropTarget(dt);
        } catch (TooManyListenersException e) {
            LOG.error("Error initialising LogTabbedPane.", e);
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (isDragOk(dtde)) {

            setBorder(BorderFactory.createLineBorder(Color.RED));

            dtde.acceptDrag(DnDConstants.ACTION_NONE);
        } else {
            dtde.rejectDrag();
        }

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        if (isDragOk(dtde)) {
            dtde.acceptDrag(DnDConstants.ACTION_NONE);
        } else {
            dtde.rejectDrag();
        }

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        setBorder(normalBorder);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

        try {
            Transferable tr = dtde.getTransferable();

            if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

                dtde.acceptDrop(DnDConstants.ACTION_COPY);

                // Get a useful list
                @SuppressWarnings("unchecked")
                List<File> fileList = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);

                for (File file : fileList) {

                    if (file.isFile()) {
                        loadFile(file);
                    }
                }

                // Mark that drop is completed.
                dtde.getDropTargetContext().dropComplete(true);

            }

        } catch (Exception e) {
            LOG.error("Error processing drop event.", e);
        } finally {
            // reset border
            setBorder(normalBorder);
        }

    }

    private boolean isDragOk(final DropTargetDragEvent evt) {

        boolean retValue = false;

        // Get data flavors being dragged
        DataFlavor[] dataFlavorArray = evt.getCurrentDataFlavors();

        for (int i = 0; i < dataFlavorArray.length; i++) {

            final DataFlavor dataFlavor = dataFlavorArray[i];

            if (dataFlavor.equals(DataFlavor.javaFileListFlavor) || dataFlavor.isRepresentationClassReader()) {
                retValue = true;
                break;
            }
        }

        return retValue;
    }

    private void addTab(String tabTitle, String path, JPanel tabPanel) {

        addTab(tabTitle, null, tabPanel, path);

        int index = getTabCount() - 1;

        final ButtonTabComponent btc = new ButtonTabComponent(tabTitle, this);

        fileTabIndexMap.put(path, index);

        setTabComponentAt(index, btc);
        setSelectedIndex(index);
    }

    @Override
    public void remove(int index) {

        super.remove(index);

        String filePath = null;

        int count = 0;

        for (String key : fileTabIndexMap.keySet()) {

            if (count == index) {
                filePath = key;
                break;
            }
            count++;
        }

        fileTabIndexMap.values().remove(index);
        tailingFileList.remove(filePath);

        int tabIndex = 0;

        for (String key : fileTabIndexMap.keySet()) {
            fileTabIndexMap.put(key, tabIndex);
            tabIndex++;
        }

        System.gc();

    }

    public void loadFile(final File selectedFile) throws Exception {

        if (LogViewer.isSystemScanFile(selectedFile)) {
            loadSystemScanFile(selectedFile);
        } else if (LogViewer.isSystemStateFile(selectedFile)) {
            loadSystemStateFile(selectedFile);
        } else {
            loadLogFile(selectedFile);
        }
    }

    public void loadLogFile(final File selectedFile) throws Exception {

        Integer index = fileTabIndexMap.get(selectedFile.getPath());

        if (index != null) {

            setSelectedIndex(index);

        } else {

            // in case of tailing, restrict number of tab to 8.
            // This is limited by SwingWorker thread pool size of 10.
            // One thread reserved for search functionality.
            // One thread reserved for file load, in case user de-selects 'tail'
            // setting.
            int totalTailingTabCount = tailingFileList.size();

            boolean tailLogFile = logViewerSetting.isTailLogFile();

            if (tailLogFile && (totalTailingTabCount == 8)) {

                StringBuilder messageSB = new StringBuilder();
                messageSB.append(
                        "Unable to load file because 'tail log file' setting is enabled. Maximun of 8 tabs can be opened.");
                messageSB.append(System.getProperty("line.separator"));
                messageSB.append("Either close existing tabs or disable 'tail log file' setting.");

                JOptionPane.showMessageDialog(this, messageSB.toString(), "Error - Max Tabs ",
                        JOptionPane.ERROR_MESSAGE);
            } else {

                LogDataMainPanel logDataMainPanel = new LogDataMainPanel(selectedFile, recentFileContainer,
                        logViewerSetting);

                tailingFileList.add(selectedFile.getPath());

                String tabTitle = selectedFile.getName();

                String path = selectedFile.getPath();

                addTab(tabTitle, path, logDataMainPanel);
            }
        }

    }

    public void loadSystemScanFile(final File selectedFile) throws Exception {

        Integer index = fileTabIndexMap.get(selectedFile.getPath());

        if (index != null) {

            setSelectedIndex(index);

        } else {

            HotfixScanMainPanel hotfixScanMainPanel = new HotfixScanMainPanel(selectedFile, recentFileContainer,
                    logViewerSetting);

            String tabTitle = selectedFile.getName();

            String path = selectedFile.getPath();

            addTab(tabTitle, path, hotfixScanMainPanel);
        }

    }

    public void loadSocketReceiverLog(final int port, AbstractLogPattern abstractLogPattern) throws Exception {

        String path = "SockectReceiver_UDP_" + port;

        Integer index = fileTabIndexMap.get(path);

        if (index != null) {

            setSelectedIndex(index);

        } else {

            SocketReceiverLogMainPanel socketReceiverLogMainPanel = new SocketReceiverLogMainPanel(port, path,
                    abstractLogPattern, recentFileContainer, logViewerSetting);

            addTab(path, path, socketReceiverLogMainPanel);
        }

    }

    public void loadSystemStateFile(final File systemStateFile) throws Exception {

        Integer index = fileTabIndexMap.get(systemStateFile.getPath());

        if (index != null) {

            setSelectedIndex(index);

        } else {

            JPanel systemStateMainPanel = new SystemStateMainPanel(systemStateFile, recentFileContainer,
                    logViewerSetting);

            String tabTitle = systemStateFile.getName();

            String path = systemStateFile.getPath();

            addTab(tabTitle, path, systemStateMainPanel);
        }

    }

    public ArrayList<String> getOpenFileList() {

        ArrayList<String> openFileList = new ArrayList<>(fileTabIndexMap.keySet());

        return openFileList;
    }
}
