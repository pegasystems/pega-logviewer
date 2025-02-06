/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.RecentFileJMenu;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.fringecommon.utilities.kyro.KryoSerializer;
import com.pega.gcs.logviewer.alert.AlertCheatSheetFrame;
import com.pega.gcs.logviewer.catalog.CatalogManagerWrapper;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.patchreleasecatalog.PatchReleaseCatalogWrapper;
import com.pega.gcs.logviewer.socketreceiver.SocketReceiverOpenDialog;

import gnu.getopt.Getopt;

public class LogViewer extends BaseFrame {

    private static final long serialVersionUID = 7083567192063249944L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogViewer.class);

    private static LogViewer _INSTANCE;

    /* ================================== */
    private static final String LOG_FILE_CHOOSER_FILTER_DESC = "Log Files";

    private static final String[] LOG_FILE_CHOOSER_FILTER_EXT = { "log", "" };

    private static final String LOG_FILE_CHOOSER_DIALOG_TITLE = "Select Pega log or Alert file";

    /* ================================== */

    private static final String SYSTEM_SCAN_FILE_CHOOSER_FILTER_DESC = "Pega Inventory File";

    private static final String[] SYSTEM_SCAN_FILE_CHOOSER_FILTER_EXT = { "zip", "PEGA", "" };

    public static final String SYSTEM_SCAN_FILE_CHOOSER_DIALOG_TITLE = "Select Pega Hotfix Inventory File";

    public static final String SYSTEM_SCAN_FILE_NAME_REGEX_V7 = "(.*?)SCANRESULTS_(.*?)";

    public static final String SYSTEM_SCAN_FILE_NAME_REGEX_V6 = "(.*?)INVENTORY(.*?)";

    public static final String SYSTEM_SCAN_FILE_NAME_REGEX = SYSTEM_SCAN_FILE_NAME_REGEX_V7 + "|"
            + SYSTEM_SCAN_FILE_NAME_REGEX_V6;

    public static final String SYSTEM_SCAN_CATALOG_FILE_NAME = "CATALOG.PEGA";

    public static final String SYSTEM_SCAN_CATALOG_ZIP_FILE = "/CATALOG.ZIP";

    public static final String RECENT_FILE_PREV_COMPARE_FILE = "prevCompareFile";

    public static final String PREF_CATALOG_BOOKMARK = "catalog_bookmark";

    /* ================================== */
    // System State
    private static final String SYSTEM_STATE_FILE_CHOOSER_FILTER_DESC = "System State JSON files";

    private static final String[] SYSTEM_STATE_FILE_CHOOSER_FILTER_EXT = { "json", "zip", "" };

    private static final String SYSTEM_STATE_FILE_CHOOSER_DIALOG_TITLE = "Select System State JSON file";

    // SystemState_60b1741a47967d78c6ee8c392d0397b6_20190501T092240.420 GMT.json
    // SystemState_cluster.json
    private static final String SYSTEM_STATE_FILE_NAME_REGEX = ".*?SystemState.*?(_.*?)?";

    /* ================================== */
    // Life Cycle Events
    private static final String LIFECYCLEEVENTS_FILE_CHOOSER_FILTER_DESC = "Life Cycle Events files";

    private static final String[] LIFECYCLEEVENTS_FILE_CHOOSER_FILTER_EXT = { "xlsx", "" };

    private static final String LIFECYCLEEVENTS_FILE_CHOOSER_DIALOG_TITLE = "Select Life Cycle Events Excel file";

    // Get_events_for_run_2021-01-19_18-35-50.xlsx
    private static final String LIFECYCLEEVENTS_FILE_NAME_REGEX = "Get_events_for_run_(.*?)";

    /* ================================== */
    // GCP GOC file pattern
    private static final String GCP_GOC_FILE_NAME_REGEX = "^\\d+$";

    /* ================================== */

    private String appName;

    private LogViewerSetting logViewerSetting;

    private ArrayList<String> openFileList;

    private File selectedFile;

    private RecentFileJMenu recentFileMenu;

    private RecentFileContainer recentFileContainer;

    private LogTabbedPane logTabbedPane;

    private AlertCheatSheetFrame alertCheatSheetFrame;

    private PluginFrame hotfixCatalogViewerFrame;

    private PluginFrame patchReleaseCatalogViewerFrame;

    private LogViewer() throws Exception {

        super();
        // preload plugin jars
        PluginClassloader.getInstance();

        // setPreferredSize(new Dimension(1200, 700));

        setFocusTraversalKeysEnabled(false);

        pack();

        // setLocationRelativeTo(null);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        setVisible(true);

        // openFileList is loaded in initialize method
        if ((openFileList != null) && (openFileList.size() > 0)) {
            loadFile(openFileList);
        }

        LOG.info(getAppName() + " - Started");

    }

    public static LogViewer getInstance() {

        if (_INSTANCE == null) {
            try {
                _INSTANCE = new LogViewer();
            } catch (Exception e) {
                LOG.error("Error  instantiating LogViewer ", e);
            }
        }

        return _INSTANCE;
    }

    public LogViewerSetting getLogViewerSetting() {
        return logViewerSetting;
    }

    private File getSelectedFile() {
        return selectedFile;
    }

    private RecentFileContainer getRecentFileContainer() {
        return recentFileContainer;
    }

    public static boolean isSystemScanFile(File systemScanFile) {

        boolean scanResultZipFile = false;

        String ext = FileUtilities.getFileExtension(systemScanFile);

        for (String fileExt : SYSTEM_SCAN_FILE_CHOOSER_FILTER_EXT) {

            if (fileExt.equalsIgnoreCase(ext)) {
                scanResultZipFile = true;
                break;
            }
        }

        if (scanResultZipFile) {

            String filename = FileUtilities.getFileBaseName(systemScanFile);

            Pattern fileNamePattern = Pattern.compile(SYSTEM_SCAN_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);

            Matcher fileNameMatcher = fileNamePattern.matcher(filename);

            if (fileNameMatcher.matches()) {
                scanResultZipFile = true;
            } else {
                scanResultZipFile = false;
            }
        }

        return scanResultZipFile;

    }

    public static boolean isSystemStateFile(File systemStateFile) {

        boolean isSystemStateFile = false;

        String ext = FileUtilities.getFileExtension(systemStateFile);

        for (String fileExt : SYSTEM_STATE_FILE_CHOOSER_FILTER_EXT) {

            if (fileExt.equalsIgnoreCase(ext)) {
                isSystemStateFile = true;
                break;
            }
        }

        if (isSystemStateFile) {

            String filename = FileUtilities.getFileBaseName(systemStateFile);

            Pattern fileNamePattern = Pattern.compile(SYSTEM_STATE_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);

            Matcher fileNameMatcher = fileNamePattern.matcher(filename);

            if (fileNameMatcher.matches()) {
                isSystemStateFile = true;
            } else {
                isSystemStateFile = false;
            }
        }

        return isSystemStateFile;

    }

    public static boolean isLifeCycleEventsFile(File lifeCycleEventsFile) {

        boolean isLifeCycleEventsFile = false;

        String ext = FileUtilities.getFileExtension(lifeCycleEventsFile);

        for (String fileExt : LIFECYCLEEVENTS_FILE_CHOOSER_FILTER_EXT) {

            if (fileExt.equalsIgnoreCase(ext)) {
                isLifeCycleEventsFile = true;
                break;
            }
        }

        if (isLifeCycleEventsFile) {

            String filename = FileUtilities.getFileBaseName(lifeCycleEventsFile);

            Pattern fileNamePattern = Pattern.compile(LIFECYCLEEVENTS_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);

            Matcher fileNameMatcher = fileNamePattern.matcher(filename);

            if (fileNameMatcher.matches()) {
                isLifeCycleEventsFile = true;
            } else {
                isLifeCycleEventsFile = false;
            }
        }

        return isLifeCycleEventsFile;

    }

    public static boolean isGcpGocFile(File gcpGocFile) {

        boolean isGcpGocFile = false;

        String filename = FileUtilities.getFileBaseName(gcpGocFile);

        Pattern fileNamePattern = Pattern.compile(GCP_GOC_FILE_NAME_REGEX, Pattern.CASE_INSENSITIVE);

        Matcher fileNameMatcher = fileNamePattern.matcher(filename);

        if (fileNameMatcher.matches()) {
            isGcpGocFile = true;
        } else {
            isGcpGocFile = false;
        }

        return isGcpGocFile;

    }

    public static FileFilter getSystemScanFileFilter() {

        FileFilter systemScanFileFilter = new FileFilter() {

            @Override
            public String getDescription() {
                return SYSTEM_SCAN_FILE_CHOOSER_FILTER_DESC;
            }

            @Override
            public boolean accept(File file) {

                boolean retVal = true;

                // pass through directories
                if (file.isFile()) {
                    retVal = isSystemScanFile(file);
                }

                return retVal;
            }
        };

        return systemScanFileFilter;
    }

    public static FileFilter getSystemStateFileFilter() {

        FileFilter systemStateFileFilter = new FileFilter() {

            @Override
            public String getDescription() {
                return SYSTEM_STATE_FILE_CHOOSER_FILTER_DESC;
            }

            @Override
            public boolean accept(File file) {

                boolean retVal = true;

                // pass through directories
                if (file.isFile()) {
                    retVal = isSystemStateFile(file);
                }

                return retVal;
            }
        };

        return systemStateFileFilter;
    }

    public static FileFilter getLifeCycleEventsFileFilter() {

        FileFilter lifeCycleEventsFileFilter = new FileFilter() {

            @Override
            public String getDescription() {
                return LIFECYCLEEVENTS_FILE_CHOOSER_FILTER_DESC;
            }

            @Override
            public boolean accept(File file) {

                boolean retVal = true;

                // pass through directories
                if (file.isFile()) {
                    retVal = isLifeCycleEventsFile(file);
                }

                return retVal;
            }
        };

        return lifeCycleEventsFileFilter;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize() throws Exception {

        byte[] byteArray;

        // get LogViewerSetting
        byteArray = GeneralUtilities.getPreferenceByteArray(LogViewer.class, PREF_SETTINGS);

        if (byteArray != null) {

            try {

                logViewerSetting = KryoSerializer.decompress(byteArray, LogViewerSetting.class);

                int settingVersion = LogViewerSetting.getSettingVersion();
                int objVersion = logViewerSetting.getObjVersion();

                if (settingVersion != objVersion) {
                    LOG.info("LogViewerSetting defaults changed - resetting to ootb");
                    logViewerSetting = null;
                }

            } catch (Exception e) {
                LOG.error("Error decompressing logViewerSetting.", e);
            }
        }

        if (logViewerSetting == null) {
            logViewerSetting = new LogViewerSetting();
            byteArray = KryoSerializer.compress(logViewerSetting);
            GeneralUtilities.setPreferenceByteArray(LogViewer.class, PREF_SETTINGS, byteArray);
        }

        // get OpenFileList
        byteArray = GeneralUtilities.getPreferenceByteArray(LogViewer.class, PREF_OPEN_FILE_LIST);

        if (byteArray != null) {
            try {
                openFileList = KryoSerializer.decompress(byteArray, ArrayList.class);
            } catch (Exception e) {
                LOG.error("Error decompressing open file list.", e);
            }
        }

        if (openFileList == null) {
            openFileList = new ArrayList<>();
            byteArray = KryoSerializer.compress(openFileList);
            GeneralUtilities.setPreferenceByteArray(LogViewer.class, PREF_OPEN_FILE_LIST, byteArray);
        }

        int capacity = logViewerSetting.getRecentItemsCount();

        recentFileContainer = new RecentFileContainer(getClass(), capacity);
    }

    @Override
    protected JMenuBar getToolMenuBar() {

        JMenu fileJMenu = getFileMenu();

        JMenu editJMenu = getEditMenu();

        JMenu helpJMenu = getHelpMenu();

        JMenuBar menuBar = new JMenuBar();

        menuBar.add(fileJMenu);
        menuBar.add(editJMenu);
        menuBar.add(helpJMenu);

        return menuBar;

    }

    @Override
    protected JComponent getMainJPanel() {

        JPanel mainJPanel = new JPanel();

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        mainJPanel.setLayout(new BorderLayout());

        mainJPanel.add(logTabbedPane, BorderLayout.CENTER);

        return mainJPanel;

    }

    @Override
    protected String getAppName() {

        if (appName == null) {

            Package classPackage = LogViewer.class.getPackage();

            StringBuilder appNameSB = new StringBuilder();
            appNameSB.append(classPackage.getImplementationTitle());
            appNameSB.append(" ");
            appNameSB.append(classPackage.getImplementationVersion());

            appName = appNameSB.toString();
        }

        return appName;
    }

    @Override
    protected void release() {

        savePreferences();

        LOG.info("LogViewer - Stopped");
    }

    protected JMenu getFileMenu() {

        JMenu fileMenu = getToolMenu("File", KeyEvent.VK_F);

        JMenuItem loadPegaLogFileMenuItem = getLoadPegaLogFileMenuItem();
        JMenuItem loadHotfixInventoryFileMenuItem = getLoadHotfixInventoryFileMenuItem();
        JMenuItem loadSystemStateFileMenuItem = getLoadSystemStateFileMenuItem();
        JMenuItem loadLifeCycleEventsFileMenuItem = getLoadLifeCycleEventsFileMenuItem();
        JMenuItem socketReceiverLogFileMenuItem = getSocketReceiverLogFileMenuItem();
        RecentFileJMenu recentFileMenu = getRecentFileMenu();
        JMenuItem clearRecentMenuItem = getClearRecentMenuItem();
        JMenuItem exitMenuItem = getExitMenuItem();

        // fileJMenu.addSeparator();
        fileMenu.add(loadPegaLogFileMenuItem);
        fileMenu.add(loadHotfixInventoryFileMenuItem);
        fileMenu.add(loadSystemStateFileMenuItem);
        fileMenu.add(loadLifeCycleEventsFileMenuItem);
        fileMenu.add(socketReceiverLogFileMenuItem);
        fileMenu.add(recentFileMenu);
        fileMenu.add(clearRecentMenuItem);
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    protected JMenu getEditMenu() {

        JMenu editMenu = getToolMenu("Edit", KeyEvent.VK_E);

        JMenuItem alertCheatSheetMenuItem = getAlertCheatSheetMenuItem();
        JMenuItem settingsMenuItem = getSettingsMenuItem();
        JMenuItem hotfixCatalogViewerMenuItem = getHotfixCatalogViewerMenuItem();
        JMenuItem patchReleaseCatalogViewerMenuItem = getPatchReleaseCatalogViewerMenuItem();

        editMenu.add(alertCheatSheetMenuItem);

        if (hotfixCatalogViewerMenuItem != null) {
            editMenu.add(hotfixCatalogViewerMenuItem);
        }

        if (patchReleaseCatalogViewerMenuItem != null) {
            editMenu.add(patchReleaseCatalogViewerMenuItem);
        }

        editMenu.add(settingsMenuItem);

        return editMenu;
    }

    private JMenuItem getLoadPegaLogFileMenuItem() {

        JMenuItem loadPegaLogFileMenuItem = new JMenuItem("Load Pega Log File");

        loadPegaLogFileMenuItem.setMnemonic(KeyEvent.VK_L);
        loadPegaLogFileMenuItem.setToolTipText("Load Pega application log file");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

        loadPegaLogFileMenuItem.setIcon(ii);

        loadPegaLogFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                File selectedFile = getSelectedFile();

                FileFilter fileFilter = LogViewer.getDefaultFileFilter(LogViewer.LOG_FILE_CHOOSER_FILTER_DESC,
                        Arrays.asList(LogViewer.LOG_FILE_CHOOSER_FILTER_EXT));

                File file = openFileChooser(LogViewer.this, LogViewer.class, LOG_FILE_CHOOSER_DIALOG_TITLE, fileFilter,
                        selectedFile);

                if (file != null) {
                    loadFile(file);
                }
            }
        });

        return loadPegaLogFileMenuItem;
    }

    private JMenuItem getSocketReceiverLogFileMenuItem() {

        JMenuItem socketReceiverLogFileMenuItem = new JMenuItem("Open Socket Receiver");

        socketReceiverLogFileMenuItem.setMnemonic(KeyEvent.VK_S);
        socketReceiverLogFileMenuItem.setToolTipText("Open Socket Receiver");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

        socketReceiverLogFileMenuItem.setIcon(ii);

        socketReceiverLogFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                SocketReceiverOpenDialog socketReceiverOpenDialog = new SocketReceiverOpenDialog(getAppIcon(),
                        LogViewer.this);

                socketReceiverOpenDialog.setVisible(true);

                int port = socketReceiverOpenDialog.getPort();

                AbstractLogPattern abstractLogPattern = socketReceiverOpenDialog.getAbstractLogPattern();

                if ((port > 0) && (abstractLogPattern != null)) {
                    loadSocketReceiverLog(port, abstractLogPattern);
                }
            }
        });

        return socketReceiverLogFileMenuItem;
    }

    private JMenuItem getLoadHotfixInventoryFileMenuItem() {

        JMenuItem loadHotfixInventoryFileMenuItem = new JMenuItem("Load Hotfix Inventory File");

        loadHotfixInventoryFileMenuItem.setMnemonic(KeyEvent.VK_S);
        loadHotfixInventoryFileMenuItem.setToolTipText("Load Hotfix Inventory zip or .PEGA File");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

        loadHotfixInventoryFileMenuItem.setIcon(ii);

        loadHotfixInventoryFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                File selectedFile = getSelectedFile();

                FileFilter fileFilter = getSystemScanFileFilter();

                File file = openFileChooser(LogViewer.this, LogViewer.class, SYSTEM_SCAN_FILE_CHOOSER_DIALOG_TITLE,
                        fileFilter, selectedFile);

                if (file != null) {
                    loadSystemScanFile(file);
                }
            }
        });

        return loadHotfixInventoryFileMenuItem;
    }

    private JMenuItem getLoadSystemStateFileMenuItem() {

        JMenuItem loadSystemStateFileMenuItem = new JMenuItem("Load System State File");

        loadSystemStateFileMenuItem.setMnemonic(KeyEvent.VK_K);
        loadSystemStateFileMenuItem.setToolTipText("Load System State JSON File");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

        loadSystemStateFileMenuItem.setIcon(ii);

        loadSystemStateFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                File selectedFile = getSelectedFile();

                FileFilter fileFilter = getSystemStateFileFilter();

                File file = openFileChooser(LogViewer.this, LogViewer.class, SYSTEM_STATE_FILE_CHOOSER_DIALOG_TITLE,
                        fileFilter, selectedFile);

                if (file != null) {
                    loadSystemStateFile(file);
                }
            }
        });

        return loadSystemStateFileMenuItem;
    }

    private JMenuItem getLoadLifeCycleEventsFileMenuItem() {

        JMenuItem loadLifeCycleEventsFileMenuItem = new JMenuItem("Load Life Cycle Events File");

        loadLifeCycleEventsFileMenuItem.setMnemonic(KeyEvent.VK_K);
        loadLifeCycleEventsFileMenuItem.setToolTipText("Load Dataflow Life Cycle Events Excel File");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "open.png");

        loadLifeCycleEventsFileMenuItem.setIcon(ii);

        loadLifeCycleEventsFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                File selectedFile = getSelectedFile();

                FileFilter fileFilter = getLifeCycleEventsFileFilter();

                File file = openFileChooser(LogViewer.this, LogViewer.class, LIFECYCLEEVENTS_FILE_CHOOSER_DIALOG_TITLE,
                        fileFilter, selectedFile);

                if (file != null) {
                    loadLifeCycleEventsFile(file);
                }
            }
        });

        return loadLifeCycleEventsFileMenuItem;
    }

    private RecentFileJMenu getRecentFileMenu() {

        if (recentFileMenu == null) {

            recentFileMenu = new RecentFileJMenu(recentFileContainer) {

                private static final long serialVersionUID = -5159590911380579230L;

                @Override
                public void onSelect(RecentFile recentFile) {

                    String filePath = (String) recentFile.getPath();

                    File file = new File(filePath);

                    if (file.exists() && file.isFile() && file.canRead()) {

                        loadFile(file);

                    } else {

                        JOptionPane.showMessageDialog(this, "File: " + file + " cannot be read.", "File not found",
                                JOptionPane.ERROR_MESSAGE);

                        getRecentFileContainer().deleteRecentFile(recentFile);
                    }
                }
            };
        }

        return recentFileMenu;
    }

    private JMenuItem getClearRecentMenuItem() {

        JMenuItem clearRecentMenuItem = new JMenuItem("Clear Recent");

        clearRecentMenuItem.setMnemonic(KeyEvent.VK_C);
        clearRecentMenuItem.setToolTipText("Clear Recent");

        clearRecentMenuItem.setIcon(null);

        clearRecentMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                clearRecentPreferences();
                savePreferences();
            }
        });

        return clearRecentMenuItem;
    }

    private JMenuItem getExitMenuItem() {

        JMenuItem exitMenuItem = new JMenuItem("Exit");

        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.setToolTipText("Exit application");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "exit.png");

        exitMenuItem.setIcon(ii);

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                exit(0);
            }
        });

        return exitMenuItem;
    }

    private JMenuItem getAlertCheatSheetMenuItem() {

        JMenuItem alertCheatSheetMenuItem = new JMenuItem("Alert Cheat Sheet");
        alertCheatSheetMenuItem.setToolTipText("Alert Cheat Sheet");

        alertCheatSheetMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                if (alertCheatSheetFrame == null) {
                    alertCheatSheetFrame = new AlertCheatSheetFrame(getAppName(), getAppIcon(), LogViewer.this);
                    alertCheatSheetFrame.addWindowListener(new WindowAdapter() {

                        @Override
                        public void windowClosed(WindowEvent windowEvent) {
                            super.windowClosed(windowEvent);
                            alertCheatSheetFrame = null;
                        }
                    });

                    alertCheatSheetFrame.setVisible(true);
                } else {
                    alertCheatSheetFrame.toFront();
                }
            }
        });

        return alertCheatSheetMenuItem;
    }

    private JMenuItem getHotfixCatalogViewerMenuItem() {

        JMenuItem hotfixCatalogViewerMenuItem = null;

        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();

        if (catalogManagerWrapper.isInitialised()) {

            hotfixCatalogViewerMenuItem = new JMenuItem("Hotfix Catalog Viewer");

            hotfixCatalogViewerMenuItem.setToolTipText("Hotfix Catalog Viewer");

            hotfixCatalogViewerMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {

                    if (hotfixCatalogViewerFrame == null) {

                        hotfixCatalogViewerFrame = catalogManagerWrapper.getPluginFrame(LogViewer.this);

                        hotfixCatalogViewerFrame.addWindowListener(new WindowAdapter() {

                            @Override
                            public void windowClosed(WindowEvent windowEvent) {
                                super.windowClosed(windowEvent);
                                hotfixCatalogViewerFrame = null;
                            }
                        });

                        hotfixCatalogViewerFrame.setVisible(true);

                    } else {
                        hotfixCatalogViewerFrame.toFront();
                    }
                }
            });
        }

        return hotfixCatalogViewerMenuItem;
    }

    private JMenuItem getPatchReleaseCatalogViewerMenuItem() {

        JMenuItem patchReleaseCatalogViewerMenuItem = null;

        PatchReleaseCatalogWrapper patchReleaseCatalogWrapper = PatchReleaseCatalogWrapper.getInstance();

        if (patchReleaseCatalogWrapper.isInitialised()) {

            patchReleaseCatalogViewerMenuItem = new JMenuItem("Patch Release Catalog Viewer");

            patchReleaseCatalogViewerMenuItem.setToolTipText("Patch Release Catalog Viewer");

            patchReleaseCatalogViewerMenuItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {

                    if (patchReleaseCatalogViewerFrame == null) {

                        patchReleaseCatalogViewerFrame = patchReleaseCatalogWrapper.getPluginFrame(LogViewer.this);

                        patchReleaseCatalogViewerFrame.addWindowListener(new WindowAdapter() {

                            @Override
                            public void windowClosed(WindowEvent windowEvent) {
                                super.windowClosed(windowEvent);
                                patchReleaseCatalogViewerFrame = null;
                            }
                        });

                        patchReleaseCatalogViewerFrame.setVisible(true);

                    } else {
                        patchReleaseCatalogViewerFrame.toFront();
                    }
                }
            });
        }

        return patchReleaseCatalogViewerMenuItem;
    }

    private JMenuItem getSettingsMenuItem() {

        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.setToolTipText("Settings");

        ImageIcon ii = FileUtilities.getImageIcon(getClass(), "settings.png");
        settingsMenuItem.setIcon(ii);

        settingsMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                LogViewerSettingsDialog logViewerSettingsDialog;
                logViewerSettingsDialog = new LogViewerSettingsDialog(getLogViewerSetting(), getAppIcon(),
                        LogViewer.this);

                logViewerSettingsDialog.setVisible(true);

                if (logViewerSettingsDialog.isSettingUpdated()) {

                    int recentItemsCount = logViewerSettingsDialog.getRecentItemsCount();
                    String charset = logViewerSettingsDialog.getSelectedCharset();
                    boolean tailLogFile = logViewerSettingsDialog.isTailLogFile();
                    boolean reloadPreviousFiles = logViewerSettingsDialog.isReloadPreviousFiles();

                    LogViewerSetting logViewerSetting = getLogViewerSetting();

                    logViewerSetting.setRecentItemsCount(recentItemsCount);
                    logViewerSetting.setCharset(charset);
                    logViewerSetting.setTailLogFile(tailLogFile);
                    logViewerSetting.setReloadPreviousFiles(reloadPreviousFiles);

                    savePreferences();
                }
            }
        });

        return settingsMenuItem;
    }

    private void savePreferences() {
        // save recent file container
        recentFileContainer.saveRecentFilesPreferrence();

        // save logViewerSetting
        try {
            byte[] byteArray = KryoSerializer.compress(logViewerSetting);

            LOG.info("LogViewerSetting ByteSize: " + byteArray.length);

            GeneralUtilities.setPreferenceByteArray(this.getClass(), PREF_SETTINGS, byteArray);
        } catch (Exception e) {
            LOG.error("Error compressing Log Viewer Setting.", e);
        }

        // save openFileList
        saveOpenFileList();

    }

    private void saveOpenFileList() {

        try {

            boolean reloadPreviousFiles = logViewerSetting.isReloadPreviousFiles();

            if (reloadPreviousFiles) {
                LogTabbedPane logTabbedPane = getLogTabbedPane();
                openFileList = logTabbedPane.getOpenFileList();
            } else {
                openFileList = new ArrayList<>();
            }

            byte[] byteArray = KryoSerializer.compress(openFileList);

            LOG.info("Open File List ByteSize: " + byteArray.length);

            GeneralUtilities.setPreferenceByteArray(this.getClass(), PREF_OPEN_FILE_LIST, byteArray);

        } catch (Exception e) {
            LOG.error("Error compressing open file list.", e);
        }
    }

    private LogTabbedPane getLogTabbedPane() {

        if (logTabbedPane == null) {
            logTabbedPane = new LogTabbedPane(logViewerSetting, recentFileContainer);
            logTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        }

        return logTabbedPane;
    }

    private void clearRecentPreferences() {
        recentFileContainer.clearRecentFilesPreferrence();
        openFileList = new ArrayList<>();
    }

    protected void loadFile(List<String> fileNameList) {

        for (String filename : fileNameList) {

            LOG.info("Processing file: " + filename);

            File file = new File(filename);

            if (file.exists() && file.isFile() && file.canRead()) {
                loadFile(file);
            } else {
                LOG.info("\"" + filename + "\" is not a file.");
            }
        }
    }

    protected void loadFile(File file) {

        this.selectedFile = file;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadFile(selectedFile);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading file: " + selectedFile, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile), "Error loading file: ",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadLogFile(File file) {

        this.selectedFile = file;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadLogFile(selectedFile);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading Log file: " + selectedFile, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile), "Error loading Log file: ",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSocketReceiverLog(final int port, final AbstractLogPattern abstractLogPattern) {

        this.selectedFile = null;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadSocketReceiverLog(port, abstractLogPattern);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading Socket receiver: Port" + port + " LogPattern: " + abstractLogPattern, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile), "Error loading Log file: ",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadSystemScanFile(File file) {

        this.selectedFile = file;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadSystemScanFile(selectedFile);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading Scan Result Zip file: " + selectedFile, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile),
                    "Error loading Scan Result Zip file: ", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadSystemStateFile(File file) {

        this.selectedFile = file;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadSystemStateFile(selectedFile);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading System State file: " + selectedFile, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile),
                    "Error loading System State file: ", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void loadLifeCycleEventsFile(File file) {

        this.selectedFile = file;

        LogTabbedPane logTabbedPane = getLogTabbedPane();

        try {

            logTabbedPane.loadLifeCycleEventsFile(selectedFile);

            saveOpenFileList();

        } catch (Exception e) {
            LOG.error("Error loading Life Cycle Events file: " + selectedFile, e);

            JOptionPane.showMessageDialog(this, (e.getMessage() + " " + selectedFile),
                    "Error loading Life Cycle Events file: ", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void generateReport(List<String> fileNameList) {

        if (fileNameList.size() == 0) {
            LOG.info("no files to process");
        }

        for (String filename : fileNameList) {

            LOG.info("Processing file: " + filename);

            File file = new File(filename);

            if (file.exists() && file.isFile() && file.canRead()) {
                generateReport(file);
            } else {
                LOG.info("\"" + filename + "\" is not a file.");
            }
        }
    }

    private static void generateReport(File logFile) {
        // TODO - generate report
        LOG.info("Generate Report - not implemented yet!!!. " + logFile);
    }

    public static void main(final String[] args) {

        LOG.info("Default Locale: " + Locale.getDefault() + " args length: " + args.length);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {

                    if (args.length > 0) {

                        List<String> fileNameList = new ArrayList<String>();

                        boolean isReport = false;

                        Getopt getopt = new Getopt("LogViewer", args, "f:r:h:?;");

                        int optChar;
                        String arg;

                        while ((optChar = getopt.getopt()) != -1) {

                            switch (optChar) {

                            case 'f':

                                int index = getopt.getOptind() - 1;
                                // LOG.info("index -> " + index);

                                while (index < args.length) {

                                    String next = args[index];
                                    index++;

                                    if (next.startsWith("-")) {
                                        break;
                                    } else {
                                        fileNameList.add(next);
                                    }
                                }

                                getopt.setOptind(index - 1);

                                break;

                            case 'r':
                                arg = getopt.getOptarg();
                                isReport = Boolean.parseBoolean(arg);

                                break;

                            case 'h':
                                printUsageAndExit();
                                break;

                            case '?':
                                printUsageAndExit();
                                break;

                            default:
                                LOG.info("getopt() returned " + optChar);
                                break;
                            }
                        }

                        // handle non option arguments - for ex starting using open-with menu command on
                        // windows. assume them as file names
                        for (int i = getopt.getOptind(); i < args.length; i++) {
                            String filename = args[i];
                            LOG.info("Non option arg element: " + filename + "\n");
                            fileNameList.add(filename);
                        }

                        if (isReport) {
                            generateReport(fileNameList);
                        } else {
                            LogViewer logViewer = LogViewer.getInstance();
                            logViewer.loadFile(fileNameList);
                        }
                    } else {
                        LogViewer logViewer = LogViewer.getInstance();
                        logViewer.setVisible(true);
                    }

                } catch (Exception e) {
                    LOG.error("Error in LogViewer main.", e);
                }
            }
        });
    }

    protected static void printUsageAndExit() {
        String usageStr = "\t-f <space seperated list of file path> \n\t-r <true|false generate report, no UI> \n\t-h <print command usage>";
        LOG.info("Usage Arguments: \n" + usageStr);
        System.exit(0);
    }

}
