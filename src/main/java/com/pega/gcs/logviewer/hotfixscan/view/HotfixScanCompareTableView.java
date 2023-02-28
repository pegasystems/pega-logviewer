/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.NavigationPanel;
import com.pega.gcs.fringecommon.guiutilities.NavigationPanelController;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.guiutilities.TableCompareEntry;
import com.pega.gcs.fringecommon.guiutilities.TableWidthColumnModelListener;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerBar;
import com.pega.gcs.fringecommon.guiutilities.markerbar.MarkerModel;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.LogViewer;
import com.pega.gcs.logviewer.catalog.HotfixTable;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.ScanResult;
import com.pega.gcs.logviewer.hotfixscan.CompareHotfixEntryKey;
import com.pega.gcs.logviewer.hotfixscan.CompareMarkerModel;
import com.pega.gcs.logviewer.hotfixscan.HotfixCompareTask;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableCompareModel;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableModel;
import com.pega.gcs.logviewer.model.LogViewerSetting;

public class HotfixScanCompareTableView extends HotfixScanView {

    private static final long serialVersionUID = -7206637173169538669L;

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixScanCompareTableView.class);

    private RecentFileContainer recentFileContainer;

    private LogViewerSetting logViewerSetting;

    private JButton fileOpenJButton;

    private JFileChooser generateCompareReportFileChooser;

    private JButton generateCompareReportJButton;

    private MarkerBar<HotfixEntryKey> compareMarkerBar;

    private NavigationPanel<HotfixEntryKey> navigationPanel;

    private NavigationPanelController<HotfixEntryKey> navigationPanelController;

    private HotfixTable hotfixTableLeft;

    private HotfixTable hotfixTableRight;

    private JScrollPane scrollPaneLeft;

    private JScrollPane scrollPaneRight;

    public HotfixScanCompareTableView(HotfixScanTableModel hotfixScanTableModel, JPanel supplementUtilityJPanel,
            NavigationTableController<HotfixEntryKey> navigationTableController,
            RecentFileContainer recentFileContainer, LogViewerSetting logViewerSetting) {

        super(hotfixScanTableModel, supplementUtilityJPanel, navigationTableController);

        this.recentFileContainer = recentFileContainer;
        this.logViewerSetting = logViewerSetting;

        HotfixTable hotfixTableLeft = getHotfixTableLeft();
        navigationTableController.addCustomJTable(hotfixTableLeft);

        setLayout(new BorderLayout());

        JSplitPane compareJSplitPane = getCompareJSplitPane();
        JPanel compareMarkerJPanel = getCompareMarkerJPanel();

        add(compareJSplitPane, BorderLayout.CENTER);
        add(compareMarkerJPanel, BorderLayout.EAST);

        updateSupplementUtilityJPanel();
    }

    @Override
    protected void updateSupplementUtilityJPanel() {

        JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

        supplementUtilityJPanel.removeAll();

        LayoutManager supplementUtilityLayout = new BoxLayout(supplementUtilityJPanel, BoxLayout.LINE_AXIS);
        supplementUtilityJPanel.setLayout(supplementUtilityLayout);

        Dimension spacer = new Dimension(5, 10);
        Dimension endspacer = new Dimension(15, 10);

        JButton compareFileOpenJButton = getFileOpenJButton();
        JButton generateCompareReportJButton = getGenerateCompareReportJButton();

        JPanel compareFileOpenPanel = new JPanel();
        LayoutManager compareFileOpenLayout = new BoxLayout(compareFileOpenPanel, BoxLayout.LINE_AXIS);
        compareFileOpenPanel.setLayout(compareFileOpenLayout);

        compareFileOpenPanel.add(Box.createRigidArea(spacer));
        compareFileOpenPanel.add(compareFileOpenJButton);
        compareFileOpenPanel.add(Box.createRigidArea(spacer));
        compareFileOpenPanel.add(generateCompareReportJButton);
        compareFileOpenPanel.add(Box.createRigidArea(spacer));
        // compareFileOpenPanel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHT_GRAY,
        // 1));

        JPanel navigationPanel = getNavigationPanel();

        supplementUtilityJPanel.add(Box.createHorizontalGlue());
        supplementUtilityJPanel.add(Box.createRigidArea(endspacer));
        supplementUtilityJPanel.add(compareFileOpenPanel);
        supplementUtilityJPanel.add(navigationPanel);
        supplementUtilityJPanel.add(Box.createRigidArea(endspacer));

        supplementUtilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        supplementUtilityJPanel.revalidate();
        supplementUtilityJPanel.repaint();
    }

    @Override
    protected void performComponentResized(Rectangle oldBounds, Rectangle newBounds) {

        HotfixTable hotfixTableLeft = getHotfixTableLeft();
        HotfixTable hotfixTableRight = getHotfixTableRight();

        TableColumnModel tableColumnModelLeft = hotfixTableLeft.getColumnModel();
        TableColumnModel tableColumnModelRight = hotfixTableRight.getColumnModel();

        GUIUtilities.applyTableColumnResize(tableColumnModelLeft, oldBounds, newBounds);
        GUIUtilities.applyTableColumnResize(tableColumnModelRight, oldBounds, newBounds);
    }

    private RecentFileContainer getRecentFileContainer() {
        return recentFileContainer;
    }

    private LogViewerSetting getLogViewerSetting() {
        return logViewerSetting;
    }

    private JButton getFileOpenJButton() {

        if (fileOpenJButton == null) {

            fileOpenJButton = new JButton("Open Scan Results file for compare");

            ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

            fileOpenJButton.setIcon(ii);

            Dimension size = new Dimension(250, 20);
            fileOpenJButton.setPreferredSize(size);
            // compareJButton.setMinimumSize(size);
            fileOpenJButton.setMaximumSize(size);
            fileOpenJButton.setHorizontalTextPosition(SwingConstants.LEADING);

            fileOpenJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

                    File fileChooserBase = null;

                    RecentFile recentFile = getHotfixScanTableModel().getRecentFile();

                    // check for previous comparison folder
                    String leftPrevComparisionFilePath = (String) recentFile
                            .getAttribute(LogViewer.RECENT_FILE_PREV_COMPARE_FILE);

                    if ((leftPrevComparisionFilePath != null) && (!"".equals(leftPrevComparisionFilePath))) {

                        File leftPrevComparisionFile = new File(leftPrevComparisionFilePath);

                        if (leftPrevComparisionFile.exists()) {
                            fileChooserBase = leftPrevComparisionFile;
                        }
                    }

                    if (fileChooserBase == null) {
                        // file open on the same folder as left file.
                        String leftFilePath = hotfixScanTableModel.getFilePath();

                        if ((leftFilePath != null) && (!"".equals(leftFilePath))) {
                            fileChooserBase = new File(leftFilePath);
                        }
                    }

                    FileFilter fileFilter = LogViewer.getSystemScanFileFilter();

                    File file = LogViewer.openFileChooser(getFileOpenJButton(), LogViewer.class,
                            LogViewer.SYSTEM_SCAN_FILE_CHOOSER_DIALOG_TITLE, fileFilter, fileChooserBase);

                    if (file != null) {

                        // for compare tree, the compare tree view should be
                        // inherited from compare table view, so that the same
                        // right hand data get passed over.
                        HotfixTable hotfixTableRight = getHotfixTableRight();
                        HotfixScanTableCompareModel hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableRight
                                .getModel();

                        RecentFileContainer recentFileContainer = getRecentFileContainer();
                        String charset = getLogViewerSetting().getCharset();

                        RecentFile compareRecentFile;
                        compareRecentFile = recentFileContainer.getRecentFile(file, charset);

                        // also reset the model and clears old stuff
                        hotfixScanTableCompareModel.setRecentFile(compareRecentFile);

                        // save the compare file path to main file
                        recentFile.setAttribute(LogViewer.RECENT_FILE_PREV_COMPARE_FILE, file.getAbsolutePath());

                        HotfixTable hotfixTableLeft = getHotfixTableLeft();

                        UIManager.put("ModalProgressMonitor.progressText", "Loading system scan file");

                        Component parent = HotfixScanCompareTableView.this;

                        final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "",
                                "Loaded 0 hotfixes (0%)                                                ", 0, 100);

                        progressMonitor.setMillisToDecideToPopup(0);
                        progressMonitor.setMillisToPopup(0);

                        HotfixCompareTask hotfixCompareTask = new HotfixCompareTask(parent, hotfixScanTableModel,
                                hotfixTableLeft, hotfixTableRight, progressMonitor) {

                            @Override
                            protected void done() {

                                try {

                                    get();

                                    if (!isCancelled()) {
                                        HotfixTable hotfixTableLeft = getHotfixTableLeft();
                                        HotfixTable hotfixTableRight = getHotfixTableRight();

                                        HotfixScanTableCompareModel hotfixScanTableCompareModelLeft;
                                        hotfixScanTableCompareModelLeft = (HotfixScanTableCompareModel) hotfixTableLeft
                                                .getModel();

                                        HotfixScanTableCompareModel hotfixScanTableCompareModelRight;
                                        hotfixScanTableCompareModelRight = (HotfixScanTableCompareModel) hotfixTableRight
                                                .getModel();

                                        getNavigationPanel().setEnabled(true);

                                        MarkerModel<HotfixEntryKey> thisMarkerModel;
                                        thisMarkerModel = new CompareMarkerModel(MyColor.LIGHT_GREEN,
                                                hotfixScanTableCompareModelLeft);

                                        MarkerModel<HotfixEntryKey> otherMarkerModel;
                                        otherMarkerModel = new CompareMarkerModel(Color.LIGHT_GRAY,
                                                hotfixScanTableCompareModelRight);

                                        MarkerBar<HotfixEntryKey> markerBar = getCompareMarkerBar();
                                        markerBar.addMarkerModel(thisMarkerModel);
                                        markerBar.addMarkerModel(otherMarkerModel);

                                        syncScrollBars();
                                        getNavigationPanelController().updateState();

                                        JButton generateCompareReportJButton = getGenerateCompareReportJButton();
                                        generateCompareReportJButton.setEnabled(true);
                                    }
                                } catch (Exception e) {
                                    LOG.error("Error performing compare task", e);

                                } finally {
                                    progressMonitor.close();
                                }
                            }

                        };

                        hotfixCompareTask.execute();

                        // HotfixScanMainPanel.loadFile(systemScanTableCompareModel,
                        // HotfixScanCompareTableView.this,
                        // true);
                        //
                        // applyModelCompare();
                    }
                }
            });

        }

        return fileOpenJButton;
    }

    private JFileChooser getGenerateCompareReportFileChooser() {

        if (generateCompareReportFileChooser == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

            String filePath = hotfixScanTableModel.getFilePath();

            File file = new File(filePath);

            String fileName = FileUtilities.getFileBaseName(file);

            StringBuilder sb = new StringBuilder();
            sb.append(fileName);
            sb.append("-");
            sb.append("patch");
            sb.append(".txt");

            String defaultReportFileName = sb.toString();

            File currentDirectory = file.getParentFile();

            File proposedFile = new File(currentDirectory, defaultReportFileName);

            generateCompareReportFileChooser = new JFileChooser(currentDirectory);

            generateCompareReportFileChooser.setDialogTitle("Save Compare report file (.txt)");
            generateCompareReportFileChooser.setSelectedFile(proposedFile);
            generateCompareReportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT File (TXT)", "txt");

            generateCompareReportFileChooser.setFileFilter(filter);
        }

        return generateCompareReportFileChooser;
    }

    private JButton getGenerateCompareReportJButton() {

        if (generateCompareReportJButton == null) {

            generateCompareReportJButton = new JButton("Generate Compare Report");

            Dimension size = new Dimension(200, 20);
            generateCompareReportJButton.setPreferredSize(size);
            generateCompareReportJButton.setMaximumSize(size);
            generateCompareReportJButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    showGenerateCompareReportChooser();
                }
            });

            generateCompareReportJButton.setEnabled(false);

        }

        return generateCompareReportJButton;
    }

    private MarkerBar<HotfixEntryKey> getCompareMarkerBar() {

        if (compareMarkerBar == null) {
            NavigationPanelController<HotfixEntryKey> navigationPanelController = getNavigationPanelController();
            compareMarkerBar = new MarkerBar<HotfixEntryKey>(navigationPanelController, null);
        }

        return compareMarkerBar;
    }

    private NavigationPanel<HotfixEntryKey> getNavigationPanel() {

        if (navigationPanel == null) {

            JLabel label = new JLabel("Compare:");

            NavigationPanelController<HotfixEntryKey> compareNavigationPanelController = getNavigationPanelController();

            navigationPanel = new NavigationPanel<>(label, compareNavigationPanelController);
            navigationPanel.setEnabled(false);

        }

        return navigationPanel;
    }

    private NavigationPanelController<HotfixEntryKey> getNavigationPanelController() {

        if (navigationPanelController == null) {

            navigationPanelController = new NavigationPanelController<HotfixEntryKey>() {

                @Override
                public void navigateToRow(int startRowIndex, int endRowIndex) {

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();
                    HotfixTable hotfixTableRight = getHotfixTableRight();

                    hotfixTableLeft.setRowSelectionInterval(startRowIndex, endRowIndex);
                    hotfixTableLeft.scrollRowToVisible(startRowIndex);

                    hotfixTableRight.setRowSelectionInterval(startRowIndex, endRowIndex);
                    hotfixTableRight.scrollRowToVisible(startRowIndex);

                    hotfixTableLeft.updateUI();
                    hotfixTableRight.updateUI();
                }

                @Override
                public void scrollToKey(HotfixEntryKey key) {

                }

                @Override
                public void first() {

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();

                    HotfixScanTableCompareModel hotfixScanTableCompareModel;
                    hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();

                    TableCompareEntry tableCompareEntry;
                    tableCompareEntry = hotfixScanTableCompareModel.compareFirst();

                    int startEntry = tableCompareEntry.getStartEntry();
                    int endEntry = tableCompareEntry.getEndEntry();

                    navigateToRow(startEntry, endEntry);

                    updateState();

                }

                @Override
                public void previous() {

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();

                    HotfixScanTableCompareModel hotfixScanTableCompareModel;
                    hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();

                    int[] selectedrows = hotfixTableLeft.getSelectedRows();

                    int selectedRow = -1;

                    if (selectedrows.length > 0) {
                        selectedRow = selectedrows[selectedrows.length - 1];
                    }

                    TableCompareEntry tableCompareEntry;
                    tableCompareEntry = hotfixScanTableCompareModel.comparePrevious(selectedRow);

                    int startEntry = tableCompareEntry.getStartEntry();
                    int endEntry = tableCompareEntry.getEndEntry();

                    navigateToRow(startEntry, endEntry);

                    updateState();

                }

                @Override
                public void next() {

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();

                    HotfixScanTableCompareModel hotfixScanTableCompareModel;
                    hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();

                    int[] selectedrows = hotfixTableLeft.getSelectedRows();

                    int selectedRow = -1;

                    if (selectedrows.length > 0) {
                        selectedRow = selectedrows[selectedrows.length - 1];
                    }

                    TableCompareEntry tableCompareEntry;
                    tableCompareEntry = hotfixScanTableCompareModel.compareNext(selectedRow);

                    int startEntry = tableCompareEntry.getStartEntry();
                    int endEntry = tableCompareEntry.getEndEntry();

                    navigateToRow(startEntry, endEntry);

                    updateState();

                }

                @Override
                public void last() {

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();

                    HotfixScanTableCompareModel hotfixScanTableCompareModel;
                    hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();

                    TableCompareEntry tableCompareEntry;
                    tableCompareEntry = hotfixScanTableCompareModel.compareLast();

                    int startEntry = tableCompareEntry.getStartEntry();
                    int endEntry = tableCompareEntry.getEndEntry();

                    navigateToRow(startEntry, endEntry);

                    updateState();

                }

                @Override
                public void updateState() {

                    NavigationPanel<HotfixEntryKey> navigationPanel = getNavigationPanel();

                    JLabel dataJLabel = navigationPanel.getDataJLabel();
                    JButton firstJButton = navigationPanel.getFirstJButton();
                    JButton prevJButton = navigationPanel.getPrevJButton();
                    JButton nextJButton = navigationPanel.getNextJButton();
                    JButton lastJButton = navigationPanel.getLastJButton();

                    HotfixTable hotfixTableLeft = getHotfixTableLeft();

                    HotfixScanTableCompareModel hotfixScanTableCompareModel;
                    hotfixScanTableCompareModel = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();

                    int[] selectedrows = hotfixTableLeft.getSelectedRows();

                    int selectedRow = -1;

                    if (selectedrows.length > 0) {
                        selectedRow = selectedrows[selectedrows.length - 1];
                    }

                    int compareCount = hotfixScanTableCompareModel.getCompareCount();
                    int compareNavIndex = hotfixScanTableCompareModel.getCompareNavIndex();

                    SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

                    if (compareCount == 0) {
                        selectedRowPosition = SelectedRowPosition.NONE;
                    } else if (hotfixScanTableCompareModel.isCompareResultsWrap()) {
                        selectedRowPosition = SelectedRowPosition.BETWEEN;
                    } else {

                        selectedRow = (selectedRow >= 0) ? (selectedRow) : 0;

                        selectedRowPosition = hotfixScanTableCompareModel.getCompareSelectedRowPosition(selectedRow);
                    }

                    switch (selectedRowPosition) {

                    case FIRST:
                        firstJButton.setEnabled(false);
                        prevJButton.setEnabled(false);
                        nextJButton.setEnabled(true);
                        lastJButton.setEnabled(true);
                        break;

                    case LAST:
                        firstJButton.setEnabled(true);
                        prevJButton.setEnabled(true);
                        nextJButton.setEnabled(false);
                        lastJButton.setEnabled(false);
                        break;

                    case BETWEEN:
                        firstJButton.setEnabled(true);
                        prevJButton.setEnabled(true);
                        nextJButton.setEnabled(true);
                        lastJButton.setEnabled(true);
                        break;

                    case NONE:
                        firstJButton.setEnabled(false);
                        prevJButton.setEnabled(false);
                        nextJButton.setEnabled(false);
                        lastJButton.setEnabled(false);
                        break;
                    default:
                        break;
                    }

                    String text = String.format(SearchPanel.PAGES_FORMAT_STR, compareNavIndex, compareCount);

                    dataJLabel.setText(text);

                }
            };
        }
        return navigationPanelController;
    }

    private HotfixTable getHotfixTableLeft() {

        if (hotfixTableLeft == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

            hotfixTableLeft = new HotfixTable(hotfixScanTableModel, false);
            hotfixTableLeft.setFillsViewportHeight(true);

            // mouse listener is setup in getCompareJSplitPane

        }

        return hotfixTableLeft;
    }

    private HotfixTable getHotfixTableRight() {

        if (hotfixTableRight == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();
            List<HotfixColumn> visibleColumnList = hotfixScanTableModel.getVisibleColumnList();

            HotfixScanTableCompareModel hotfixScanTableCompareModel = new HotfixScanTableCompareModel(null, null,
                    visibleColumnList);

            hotfixTableRight = new HotfixTable(hotfixScanTableCompareModel);
            hotfixTableRight.setFillsViewportHeight(true);

            // mouse listener is setup in getCompareJSplitPane

        }

        return hotfixTableRight;
    }

    private JScrollPane getScrollPaneLeft() {

        if (scrollPaneLeft == null) {

            HotfixTable hotfixTableLeft = getHotfixTableLeft();

            scrollPaneLeft = getScrollPane(hotfixTableLeft);

        }

        return scrollPaneLeft;
    }

    private JScrollPane getScrollPaneRight() {

        if (scrollPaneRight == null) {

            HotfixTable hotfixTableRight = getHotfixTableRight();

            scrollPaneRight = getScrollPane(hotfixTableRight);

        }

        return scrollPaneRight;
    }

    private JScrollPane getScrollPane(HotfixTable hotfixTable) {

        JScrollPane scrollpane = new JScrollPane(hotfixTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        hotfixTable.setPreferredScrollableViewportSize(scrollpane.getPreferredSize());

        return scrollpane;
    }

    private JSplitPane getCompareJSplitPane() {

        HotfixTable hotfixTableLeft = getHotfixTableLeft();
        HotfixTable hotfixTableRight = getHotfixTableRight();

        // set selection model
        hotfixTableRight.setSelectionModel(hotfixTableLeft.getSelectionModel());

        // setup column model listener
        TableWidthColumnModelListener tableWidthColumnModelListener;
        tableWidthColumnModelListener = new TableWidthColumnModelListener();
        tableWidthColumnModelListener.addTable(hotfixTableLeft);
        tableWidthColumnModelListener.addTable(hotfixTableRight);

        hotfixTableLeft.getColumnModel().addColumnModelListener(tableWidthColumnModelListener);
        hotfixTableRight.getColumnModel().addColumnModelListener(tableWidthColumnModelListener);

        // setup JScrollBar
        JScrollPane scrollPaneLeft = getScrollPaneLeft();
        JScrollPane scrollPaneRight = getScrollPaneRight();

        JPanel scanResultTablePanelLeft = getSingleTableJPanel(scrollPaneLeft, hotfixTableLeft);
        JPanel scanResultTablePanelRight = getSingleTableJPanel(scrollPaneRight, hotfixTableRight);

        JSplitPane compareJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scanResultTablePanelLeft,
                scanResultTablePanelRight);

        compareJSplitPane.setContinuousLayout(true);
        // compareJSplitPane.setDividerLocation(260);
        compareJSplitPane.setResizeWeight(0.5);

        // not movable divider
        // compareJSplitPane.setEnabled(false);

        return compareJSplitPane;
    }

    private JPanel getCompareMarkerJPanel() {

        JPanel compareMarkerJPanel = new JPanel();
        compareMarkerJPanel.setLayout(new BorderLayout());

        Dimension topDimension = new Dimension(16, 60);

        JLabel topSpacer = new JLabel();
        topSpacer.setPreferredSize(topDimension);
        topSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        Dimension bottomDimension = new Dimension(16, 35);

        JLabel bottomSpacer = new JLabel();
        bottomSpacer.setPreferredSize(bottomDimension);
        bottomSpacer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        MarkerBar<HotfixEntryKey> compareMarkerBar = getCompareMarkerBar();

        compareMarkerJPanel.add(topSpacer, BorderLayout.NORTH);
        compareMarkerJPanel.add(compareMarkerBar, BorderLayout.CENTER);
        compareMarkerJPanel.add(bottomSpacer, BorderLayout.SOUTH);

        return compareMarkerJPanel;

    }

    private JPanel getSingleTableJPanel(JScrollPane scanResultTableScrollpane, HotfixTable hotfixTable) {

        JPanel singleTableJPanel = new JPanel();

        singleTableJPanel.setLayout(new BorderLayout());

        JTextField statusBar = getStatusBar();

        SearchPanel<HotfixEntryKey> searchPanel = getSearchPanel(hotfixTable);

        HotfixScanTableModel hotfixScanTableModel = (HotfixScanTableModel) hotfixTable.getModel();

        JPanel tableJPanel = getTableDataJPanel(hotfixScanTableModel, scanResultTableScrollpane);
        JPanel statusBarJPanel = getStatusBarJPanel(statusBar);

        singleTableJPanel.add(searchPanel, BorderLayout.NORTH);
        singleTableJPanel.add(tableJPanel, BorderLayout.CENTER);
        singleTableJPanel.add(statusBarJPanel, BorderLayout.SOUTH);

        hotfixScanTableModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {
                    Message message = (Message) evt.getNewValue();
                    GUIUtilities.setMessage(statusBar, message);
                }

            }
        });

        return singleTableJPanel;
    }

    private JTextField getStatusBar() {

        JTextField statusBar = new JTextField();
        statusBar.setEditable(false);
        statusBar.setBackground(null);
        statusBar.setBorder(null);

        return statusBar;
    }

    private JPanel getStatusBarJPanel(JTextField statusBar) {

        JPanel statusBarJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(statusBarJPanel, BoxLayout.LINE_AXIS);
        statusBarJPanel.setLayout(layout);

        Dimension spacer = new Dimension(5, 16);

        statusBarJPanel.add(Box.createRigidArea(spacer));
        statusBarJPanel.add(statusBar);
        statusBarJPanel.add(Box.createRigidArea(spacer));

        statusBarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statusBarJPanel;

    }

    protected JPanel getTableDataJPanel(HotfixScanTableModel hotfixScanTableModel, JScrollPane tableScrollpane) {

        JPanel tableJPanel = new JPanel();
        tableJPanel.setLayout(new BorderLayout());

        JPanel markerBarPanel = getMarkerBarPanel(hotfixScanTableModel);

        tableJPanel.add(tableScrollpane, BorderLayout.CENTER);
        tableJPanel.add(markerBarPanel, BorderLayout.EAST);

        return tableJPanel;
    }

    private SearchPanel<HotfixEntryKey> getSearchPanel(HotfixTable hotfixTable) {

        final HotfixScanTableModel hotfixScanTableModel = (HotfixScanTableModel) hotfixTable.getModel();

        NavigationTableController<HotfixEntryKey> navigationTableController;
        navigationTableController = new NavigationTableController<HotfixEntryKey>(hotfixScanTableModel);

        navigationTableController.addCustomJTable(hotfixTable);

        SearchPanel<HotfixEntryKey> searchPanel;
        searchPanel = new SearchPanel<HotfixEntryKey>(navigationTableController, hotfixScanTableModel.getSearchModel());

        return searchPanel;
    }

    private void syncScrollBars() {

        JScrollPane scrollPaneLeft = getScrollPaneLeft();
        JScrollPane scrollPaneRight = getScrollPaneRight();

        JScrollBar scrollBarLeftH = scrollPaneLeft.getHorizontalScrollBar();
        JScrollBar scrollBarLeftV = scrollPaneLeft.getVerticalScrollBar();
        JScrollBar scrollBarRightH = scrollPaneRight.getHorizontalScrollBar();
        JScrollBar scrollBarRightV = scrollPaneRight.getVerticalScrollBar();

        scrollBarRightH.setModel(scrollBarLeftH.getModel());
        scrollBarRightV.setModel(scrollBarLeftV.getModel());
    }

    private void showGenerateCompareReportChooser() {

        File exportFile = null;

        JFileChooser fileChooser = getGenerateCompareReportFileChooser();

        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {

            exportFile = fileChooser.getSelectedFile();

            returnValue = JOptionPane.YES_OPTION;

            if (exportFile.exists()) {

                returnValue = JOptionPane.showConfirmDialog(null, "Replace Existing File?", "File Exists",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }

            if (returnValue == JOptionPane.YES_OPTION) {

                String reportHtml = getCompareReport();

                try {
                    FileUtils.writeStringToFile(exportFile, reportHtml, getHotfixScanTableModel().getCharset());
                } catch (IOException e) {
                    LOG.error("Error generating report: " + exportFile, e);
                }

            }
        }
    }

    private String getCompareReport() {

        StringBuilder compareReportSB = new StringBuilder();

        List<HotfixColumn> hotfixColumns = new ArrayList<>();

        hotfixColumns.add(HotfixColumn.HOTFIX_ID);
        hotfixColumns.add(HotfixColumn.HOTFIX_STATUS);
        hotfixColumns.add(HotfixColumn.HOTFIX_DESCRIPTION);

        HotfixTable hotfixTableLeft = getHotfixTableLeft();
        HotfixTable hotfixTableRight = getHotfixTableRight();

        HotfixScanTableCompareModel hotfixScanTableCompareModelLeft;
        hotfixScanTableCompareModelLeft = (HotfixScanTableCompareModel) hotfixTableLeft.getModel();
        String fileLeft = hotfixScanTableCompareModelLeft.getFilePath();
        ScanResult scanResultLeft = hotfixScanTableCompareModelLeft.getScanResult();

        HotfixScanTableCompareModel hotfixScanTableCompareModelRight;
        hotfixScanTableCompareModelRight = (HotfixScanTableCompareModel) hotfixTableRight.getModel();
        String fileRight = hotfixScanTableCompareModelRight.getFilePath();

        ScanResult scanResultRight = hotfixScanTableCompareModelRight.getScanResult();

        String leftHText = "==== %d Hotfixes missing on environment for Left scan file %s ====";
        String rightHText = "==== %d Hotfixes missing on environment for Right scan file %s ====";

        List<CompareHotfixEntryKey> compareMarkerListLeft;
        compareMarkerListLeft = hotfixScanTableCompareModelLeft.getCompareMarkerList();

        List<CompareHotfixEntryKey> compareMarkerListRight;
        compareMarkerListRight = hotfixScanTableCompareModelRight.getCompareMarkerList();

        leftHText = String.format(leftHText, compareMarkerListLeft.size(), fileLeft);
        rightHText = String.format(rightHText, compareMarkerListRight.size(), fileRight);

        compareReportSB.append(leftHText);
        compareReportSB.append(System.getProperty("line.separator"));

        // Left entry pick data from Right model
        List<HotfixEntryKey> hotfixEntryKeyListLeft;
        hotfixEntryKeyListLeft = new ArrayList<>();

        for (CompareHotfixEntryKey compareHotfixEntryKey : compareMarkerListLeft) {
            for (HotfixEntryKey key : scanResultRight.getScanResultHotfixEntryMap().keySet()) {

                if (compareHotfixEntryKey.getKey().equals(key.getKey())) {
                    hotfixEntryKeyListLeft.add(key);
                    break;
                }
            }
        }

        String compareReportLeft = hotfixScanTableCompareModelRight.getSelectedColumnsData(hotfixEntryKeyListLeft,
                hotfixColumns);
        compareReportSB.append(compareReportLeft);

        compareReportSB.append(System.getProperty("line.separator"));
        compareReportSB.append(rightHText);
        compareReportSB.append(System.getProperty("line.separator"));

        // Right entry pick data from Left model
        List<HotfixEntryKey> hotfixEntryKeyListRight;
        hotfixEntryKeyListRight = new ArrayList<>();

        for (HotfixEntryKey hotfixEntryKey : compareMarkerListRight) {
            for (HotfixEntryKey key : scanResultLeft.getScanResultHotfixEntryMap().keySet()) {

                if (hotfixEntryKey.getKey().equals(key.getKey())) {
                    hotfixEntryKeyListRight.add(key);
                    break;
                }
            }
        }

        String compareReportRight = hotfixScanTableCompareModelLeft.getSelectedColumnsData(hotfixEntryKeyListRight,
                hotfixColumns);
        compareReportSB.append(compareReportRight);

        return compareReportSB.toString();
    }

}
