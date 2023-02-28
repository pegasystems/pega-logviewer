/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.catalog.model.InventoryVersion;
import com.pega.gcs.logviewer.hotfixscan.report.HotfixReportFrame;
import com.pega.gcs.logviewer.hotfixscan.v7.HotfixScanTaskV7;
import com.pega.gcs.logviewer.hotfixscan.view.HotfixScanCompareTableView;
import com.pega.gcs.logviewer.hotfixscan.view.HotfixScanSingleTableView;
import com.pega.gcs.logviewer.hotfixscan.view.HotfixScanView;
import com.pega.gcs.logviewer.hotfixscan.view.HotfixScanViewMode;
import com.pega.gcs.logviewer.model.LogViewerSetting;

public class HotfixScanMainPanel extends JPanel {

    private static final long serialVersionUID = 1172626021990445833L;

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixScanMainPanel.class);

    private RecentFileContainer recentFileContainer;

    private LogViewerSetting logViewerSetting;

    private HotfixScanTableModel hotfixScanTableModel;

    private NavigationTableController<HotfixEntryKey> navigationTableController;

    private HashMap<String, HotfixScanView> hotfixScanViewMap;

    private JComboBox<HotfixScanViewMode> hotfixScanViewModeComboBox;

    private JPanel scanResultViewCardPanel;

    private JPanel supplementUtilityPanel;

    private JButton overviewButton;

    private HotfixReportFrame hotfixReportFrame;

    @SuppressWarnings("unchecked")
    public HotfixScanMainPanel(File scanResultZipFile, RecentFileContainer recentFileContainer,
            LogViewerSetting logViewerSetting) {

        super();

        this.recentFileContainer = recentFileContainer;
        this.logViewerSetting = logViewerSetting;

        String charset = logViewerSetting.getCharset();
        Locale locale = logViewerSetting.getLocale();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(RecentFile.KEY_LOCALE, locale);

        RecentFile recentFile = recentFileContainer.getRecentFile(scanResultZipFile, charset, attributes);

        List<HotfixColumn> visibleColumnList = HotfixColumn.getTableHotfixColumnList();

        hotfixScanTableModel = new HotfixScanTableModel(recentFile, null, visibleColumnList);

        // moving bookmark loading to end of file load, so that bookmarked key are avilable in model.
        // BookmarkContainer<HotfixEntryKey> bookmarkContainer;
        // bookmarkContainer = (BookmarkContainer<HotfixEntryKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);
        //
        // if (bookmarkContainer == null) {
        //
        // bookmarkContainer = new BookmarkContainer<HotfixEntryKey>();
        //
        // recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
        // }
        //
        // bookmarkContainer = (BookmarkContainer<HotfixEntryKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);
        //
        // BookmarkModel<HotfixEntryKey> bookmarkModel = new BookmarkModel<HotfixEntryKey>(bookmarkContainer,
        // hotfixScanTableModel);
        //
        // hotfixScanTableModel.setBookmarkModel(bookmarkModel);

        navigationTableController = new NavigationTableController<>(hotfixScanTableModel);

        hotfixScanViewMap = new HashMap<String, HotfixScanView>();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel utilityCompositeJPanel = getUtilityCompositeJPanel();
        JPanel scanResultViewCardJPanel = getScanResultViewCardPanel();

        add(utilityCompositeJPanel, gbc1);
        add(scanResultViewCardJPanel, gbc2);

        // set default view
        JComboBox<HotfixScanViewMode> hotfixScanViewModeJComboBox = getHotfixScanViewModeComboBox();

        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4699927
        // tree need to be built once the root node has some child nodes. hence
        // setting the default to single table
        hotfixScanViewModeJComboBox.setSelectedItem(HotfixScanViewMode.SINGLE_TABLE);

        boolean loadNotInstalledHotfixes = true;

        loadFile(this, hotfixScanTableModel, loadNotInstalledHotfixes, false);
    }

    private HotfixScanTableModel getHotfixScanTableModel() {
        return hotfixScanTableModel;
    }

    private NavigationTableController<HotfixEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private JComboBox<HotfixScanViewMode> getHotfixScanViewModeComboBox() {

        if (hotfixScanViewModeComboBox == null) {

            hotfixScanViewModeComboBox = new JComboBox<HotfixScanViewMode>(HotfixScanViewMode.values());

            Dimension size = new Dimension(200, 20);
            hotfixScanViewModeComboBox.setPreferredSize(size);
            // hotfixScanViewModeJComboBox.setMinimumSize(size);
            hotfixScanViewModeComboBox.setMaximumSize(size);

            hotfixScanViewModeComboBox.addActionListener(new ActionListener() {

                @SuppressWarnings("unchecked")
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    JComboBox<HotfixScanViewMode> hotfixScanViewModeJComboBox;
                    hotfixScanViewModeJComboBox = (JComboBox<HotfixScanViewMode>) actionEvent.getSource();

                    HotfixScanViewMode hotfixScanViewMode;
                    hotfixScanViewMode = (HotfixScanViewMode) hotfixScanViewModeJComboBox.getSelectedItem();

                    switchHotfixScanViewMode(hotfixScanViewMode);
                }
            });

        }

        return hotfixScanViewModeComboBox;
    }

    private JPanel getScanResultViewCardPanel() {

        if (scanResultViewCardPanel == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

            NavigationTableController<HotfixEntryKey> navigationTableController = getNavigationTableController();

            JPanel supplementUtilityJPanel = getSupplementUtilityPanel();

            scanResultViewCardPanel = new JPanel(new CardLayout());

            for (HotfixScanViewMode hotfixScanViewMode : HotfixScanViewMode.values()) {

                HotfixScanView hotfixScanView;

                switch (hotfixScanViewMode) {

                case SINGLE_TABLE:
                    hotfixScanView = new HotfixScanSingleTableView(hotfixScanTableModel, supplementUtilityJPanel,
                            navigationTableController);
                    break;

                case COMPARE_TABLE:
                    hotfixScanView = new HotfixScanCompareTableView(hotfixScanTableModel, supplementUtilityJPanel,
                            navigationTableController, recentFileContainer, logViewerSetting);
                    break;

                default:
                    hotfixScanView = new HotfixScanSingleTableView(hotfixScanTableModel, supplementUtilityJPanel,
                            navigationTableController);
                    break;
                }

                String scanResultViewModeName = hotfixScanViewMode.name();

                hotfixScanViewMap.put(scanResultViewModeName, hotfixScanView);

                scanResultViewCardPanel.add(hotfixScanView, scanResultViewModeName);
            }
        }

        return scanResultViewCardPanel;
    }

    protected JPanel getSupplementUtilityPanel() {

        if (supplementUtilityPanel == null) {

            supplementUtilityPanel = new JPanel();
            LayoutManager layout = new BoxLayout(supplementUtilityPanel, BoxLayout.LINE_AXIS);
            supplementUtilityPanel.setLayout(layout);
        }

        return supplementUtilityPanel;
    }

    private JPanel getUtilityCompositeJPanel() {

        JPanel utilityCompositeJPanel = new JPanel();

        utilityCompositeJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel utilityJPanel = getUtilityJPanel();
        JPanel supplementUtilityJPanel = getSupplementUtilityPanel();

        utilityCompositeJPanel.add(utilityJPanel, gbc1);
        utilityCompositeJPanel.add(supplementUtilityJPanel, gbc2);

        return utilityCompositeJPanel;
    }

    private JPanel getUtilityJPanel() {

        JPanel utilityJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilityJPanel, BoxLayout.LINE_AXIS);
        utilityJPanel.setLayout(layout);

        Dimension spacer = new Dimension(15, 30);
        Dimension endSpacer = new Dimension(10, 30);

        JLabel scanResultViewModeJLabel = new JLabel("Select view: ");

        JComboBox<HotfixScanViewMode> hotfixScanViewModeJComboBox = getHotfixScanViewModeComboBox();

        JButton overviewJButton = getOverviewButton();

        utilityJPanel.add(Box.createRigidArea(endSpacer));
        utilityJPanel.add(scanResultViewModeJLabel);
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.add(hotfixScanViewModeJComboBox);
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.add(overviewJButton);
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return utilityJPanel;
    }

    private void switchHotfixScanViewMode(HotfixScanViewMode hotfixScanViewMode) {

        String scanResultViewModeName = hotfixScanViewMode.name();

        HotfixScanView hotfixScanView = hotfixScanViewMap.get(scanResultViewModeName);

        if (hotfixScanView != null) {

            hotfixScanView.switchToFront();

            JPanel scanResultViewCardJPanel = getScanResultViewCardPanel();
            CardLayout cardLayout = (CardLayout) (scanResultViewCardJPanel.getLayout());

            cardLayout.show(scanResultViewCardJPanel, scanResultViewModeName);

        }
    }

    // public static void loadFile(HotfixScanTableModel hotfixScanTableModel,
    // boolean loadNotInstalledHotfixes,
    // Component parent, boolean wait) {
    // UIManager.put("ModalProgressMonitor.progressText", "Loading system scan
    // file");
    //
    // final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent,
    // "", "", 0, 100);
    //
    // progressMonitor.setMillisToDecideToPopup(0);
    // progressMonitor.setMillisToPopup(0);
    //
    // loadFile(parent, hotfixScanTableModel, loadNotInstalledHotfixes,
    // progressMonitor, wait);
    // }

    public static void loadFile(Component parent, HotfixScanTableModel hotfixScanTableModel,
            boolean loadNotInstalledHotfixes, boolean wait) {

        RecentFile recentFile = hotfixScanTableModel.getRecentFile();

        if (recentFile != null) {

            try {

                AbstractHotfixScanTask abstractHotfixScanTask = null;

                InventoryVersion inventoryVersion = hotfixScanTableModel.getInventoryVersion();

                switch (inventoryVersion) {

                case INVENTORY_VERSION_6:
                    try {
                        Class<?> hotfixScanTaskV6Class = Class
                                .forName("com.pega.gcs.logviewer.hotfix.v6.HotfixScanTaskV6");

                        Constructor<?> systemScanTaskV6Constructor = hotfixScanTaskV6Class.getDeclaredConstructor(
                                Component.class, HotfixScanTableModel.class, boolean.class, ModalProgressMonitor.class,
                                boolean.class);
                        systemScanTaskV6Constructor.setAccessible(true);

                        UIManager.put("ModalProgressMonitor.progressText", "Loading system scan file");

                        ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "",
                                "Loading Scan Result File ...                                          ", 0, 100);

                        progressMonitor.setMillisToDecideToPopup(0);
                        progressMonitor.setMillisToPopup(0);

                        abstractHotfixScanTask = (AbstractHotfixScanTask) systemScanTaskV6Constructor.newInstance(
                                parent, hotfixScanTableModel, loadNotInstalledHotfixes, progressMonitor, wait);

                        abstractHotfixScanTask.execute();

                        abstractHotfixScanTask.completeTask();

                    } catch (ClassNotFoundException cnfe) {
                        JOptionPane.showMessageDialog(parent, "Hotfix Scan class not found.\nLoad hotfixscan plugin",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (NoSuchMethodException nsme) {
                        LOG.error("Error initialising-", nsme);
                        JOptionPane.showMessageDialog(parent, "Error initialising-" + nsme.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (InvocationTargetException ite) {
                        LOG.error("Error initialising-", ite);
                        JOptionPane.showMessageDialog(parent,
                                "Error initialising-" + ite.getTargetException().getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalAccessException iae) {
                        LOG.error("Error initialising-", iae);
                        JOptionPane.showMessageDialog(parent, "Error initialising-" + iae.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (InstantiationException ie) {
                        LOG.error("Error initialising-", ie);
                        JOptionPane.showMessageDialog(parent, "Error initialising-" + ie.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    break;
                case INVENTORY_VERSION_7:

                    UIManager.put("ModalProgressMonitor.progressText", "Loading system scan file");

                    ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "",
                            "Loading Scan Result File ...                                          ", 0, 100);

                    progressMonitor.setMillisToDecideToPopup(0);
                    progressMonitor.setMillisToPopup(0);

                    abstractHotfixScanTask = new HotfixScanTaskV7(parent, hotfixScanTableModel,
                            loadNotInstalledHotfixes, progressMonitor, wait);

                    abstractHotfixScanTask.execute();

                    abstractHotfixScanTask.completeTask();

                    break;

                default:
                    break;
                }

            } catch (Exception e) {
                LOG.error("Error in Hotfix Scan Task : ", e);
                hotfixScanTableModel
                        .setMessage(new Message(MessageType.ERROR, "Error in Hotfix Scan Task :" + e.getMessage()));

            }

        } else {
            hotfixScanTableModel.setMessage(new Message(MessageType.ERROR, "No file selected for model"));
        }
    }

    private JButton getOverviewButton() {

        if (overviewButton == null) {

            overviewButton = new JButton("Overview");
            Dimension size = new Dimension(90, 20);
            overviewButton.setPreferredSize(size);
            overviewButton.setMaximumSize(size);

            overviewButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    HotfixReportFrame hotfixReportFrame;
                    hotfixReportFrame = getHotfixReportFrame();

                    hotfixReportFrame.toFront();
                }
            });
        }

        return overviewButton;

    }

    private HotfixReportFrame getHotfixReportFrame() {

        if (hotfixReportFrame == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

            hotfixReportFrame = new HotfixReportFrame(hotfixScanTableModel, BaseFrame.getAppIcon(), this);

            hotfixReportFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    HotfixReportFrame hotfixReportFrame;
                    hotfixReportFrame = getHotfixReportFrame();

                    hotfixReportFrame.destroyFrame();
                    setHotfixReportFrame(null);
                }

            });
        }

        return hotfixReportFrame;
    }

    private void setHotfixReportFrame(HotfixReportFrame hotfixReportFrame) {
        this.hotfixReportFrame = hotfixReportFrame;
    }
}
