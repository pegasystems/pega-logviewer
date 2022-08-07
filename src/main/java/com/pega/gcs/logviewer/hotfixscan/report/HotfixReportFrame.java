
package com.pega.gcs.logviewer.hotfixscan.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.catalog.model.HotfixColumn;
import com.pega.gcs.logviewer.catalog.model.ProductInfo;
import com.pega.gcs.logviewer.catalog.model.ScanResult;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableModel;

public class HotfixReportFrame extends JFrame implements TableModelListener {

    private static final long serialVersionUID = 3160769471468490455L;

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixReportFrame.class);

    private HotfixScanTableModel hotfixScanTableModel;

    private JTabbedPane reportJTabbedPane;

    public HotfixReportFrame(HotfixScanTableModel hotfixScanTableModel, ImageIcon appIcon, Component parent) {

        super();

        this.hotfixScanTableModel = hotfixScanTableModel;

        hotfixScanTableModel.addTableModelListener(this);

        setTitle(hotfixScanTableModel.getModelName());

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1500, 800));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainJPanel());

        pack();

        setLocationRelativeTo(parent);

        // visible should be the last step
        setVisible(true);
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {

        if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
            rebuildOverview();
        }
    }

    private void rebuildOverview() {

        LOG.info("rebuildOverview()");

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();

        reportJTabbedPane.removeAll();

        buildTabs();

        validate();
        repaint();
    }

    public void destroyFrame() {
        hotfixScanTableModel.removeTableModelListener(this);
        setVisible(false);
    }

    private JTabbedPane getReportJTabbedPane() {

        if (reportJTabbedPane == null) {
            reportJTabbedPane = new JTabbedPane();
        }

        return reportJTabbedPane;
    }

    private JComponent getMainJPanel() {

        JPanel mainJPanel = new JPanel();
        mainJPanel.setLayout(new BorderLayout());

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();
        mainJPanel.add(reportJTabbedPane);

        buildTabs();
        return mainJPanel;
    }

    private void buildTabs() {

        ScanResult scanResult = hotfixScanTableModel.getScanResult();

        int tabIndex = 0;

        // Current unique hotfixed classes/rules -> with affected hotfix ids
        // Hotfixes not installed
        // Possible overridden hotfixes
        // Possible Issue specific "not-Installed" hotfixes

        String tabText;
        String description;

        List<ProductInfo> productInfoList = scanResult.getProductInfoList();

        Map<String, TreeSet<String>> changeKeyInstalledHotfixIdMap;
        changeKeyInstalledHotfixIdMap = scanResult.getChangeKeyInstalledHotfixIdMap();

        Map<String, TreeSet<String>> changeKeyNotInstalledHotfixIdMap;
        changeKeyNotInstalledHotfixIdMap = scanResult.getChangeKeyNotInstalledHotfixIdMap();

        // Product Info
        tabText = "Product Info";
        description = "List of products in the env.";

        HotfixColumn[] productInfoColumns = { HotfixColumn.ID, HotfixColumn.PRODUCT_NAME,
                HotfixColumn.PRODUCT_VERSION };

        Map<Integer, HotfixReportRecord> productInfoHotfixRecordMap = getProductInfoHotfixRecordMap(productInfoList);

        JPanel productInfoJPanel = getTableJPanel(description, productInfoHotfixRecordMap,
                Arrays.asList(productInfoColumns));

        addTab(tabText, 140, productInfoJPanel, tabIndex);
        tabIndex++;

        // Hotfixed Artifacts
        tabText = "Hotfixed Artifacts";
        description = "List of classes/rules/schema entities updated by the hotfixes.";

        HotfixColumn[] hotfixedArtifactsColumns = { HotfixColumn.ID, HotfixColumn.HOTFIX_CHANGE_KEY,
                HotfixColumn.INSTALLED_HOTFIX_ID, HotfixColumn.NOT_INSTALLED_HOTFIX_ID };

        Map<Integer, HotfixReportRecord> installedChangeKeyHotfixRecordMap;
        installedChangeKeyHotfixRecordMap = getInstalledChangeKeyHotfixRecordMap(changeKeyInstalledHotfixIdMap,
                changeKeyNotInstalledHotfixIdMap);

        JPanel installedHotfixChangeJPanel = getTableJPanel(description, installedChangeKeyHotfixRecordMap,
                Arrays.asList(hotfixedArtifactsColumns));

        addTab(tabText, 140, installedHotfixChangeJPanel, tabIndex);
        tabIndex++;

        // Not Installed Hotfixed Artifacts
        tabText = "Not Installed Hotfixed Artifacts";
        description = "List of classes/rules/schema entities updated by the hotfixes that are not installed.";

        HotfixColumn[] notHotfixedArtifactsColumns = { HotfixColumn.ID, HotfixColumn.HOTFIX_CHANGE_KEY,
                HotfixColumn.NOT_INSTALLED_HOTFIX_ID };

        Map<Integer, HotfixReportRecord> notInstalledChangeKeyHotfixRecordMap;
        notInstalledChangeKeyHotfixRecordMap = getNotInstalledChangeKeyHotfixRecordMap(
                changeKeyNotInstalledHotfixIdMap);

        JPanel notIinstalledHotfixChangeJPanel = getTableJPanel(description, notInstalledChangeKeyHotfixRecordMap,
                Arrays.asList(notHotfixedArtifactsColumns));
        addTab(tabText, 200, notIinstalledHotfixChangeJPanel, tabIndex);
        tabIndex++;

    }

    private void addTab(String tabText, int tabWidth, JPanel panel, int tabIndex) {

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();

        JLabel tabLabel = new JLabel(tabText);
        Font labelFont = tabLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
        Dimension dim = new Dimension(tabWidth, 26);
        tabLabel.setFont(tabFont);
        tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        reportJTabbedPane.addTab(tabText, panel);
        reportJTabbedPane.setTabComponentAt(tabIndex, tabLabel);
    }

    private Map<Integer, HotfixReportRecord> getProductInfoHotfixRecordMap(List<ProductInfo> productInfoList) {

        Map<Integer, HotfixReportRecord> productInfoHotfixRecordMap = new HashMap<>();

        AtomicInteger indexCounter = new AtomicInteger(0);

        for (ProductInfo productInfo : productInfoList) {

            Integer key = indexCounter.incrementAndGet();

            String productName = productInfo.getProductName();
            String productVersion = productInfo.getProductVersion();

            List<String> valueList = new ArrayList<>();

            valueList.add(productName);
            valueList.add(productVersion);

            HotfixReportRecord hotfixReportRecord = new HotfixReportRecord(key, valueList);
            productInfoHotfixRecordMap.put(key, hotfixReportRecord);

        }

        return productInfoHotfixRecordMap;
    }

    private Map<Integer, HotfixReportRecord> getInstalledChangeKeyHotfixRecordMap(
            Map<String, TreeSet<String>> changeKeyInstalledHotfixIdMap,
            Map<String, TreeSet<String>> changeKeyNotInstalledHotfixIdMap) {

        Map<Integer, HotfixReportRecord> installedChangeKeyHotfixRecordMap = new HashMap<>();

        AtomicInteger indexCounter = new AtomicInteger(0);

        for (Map.Entry<String, TreeSet<String>> changeKeyEntry : changeKeyInstalledHotfixIdMap.entrySet()) {

            Integer key = indexCounter.incrementAndGet();

            String changeKey = changeKeyEntry.getKey();
            TreeSet<String> valueTreeSet = changeKeyEntry.getValue();

            String changeKeyHotfixIDsValue = GeneralUtilities.getCollectionAsSeperatedValues(valueTreeSet, ", ", false);
            String changeKeyNotInstalledHotfixIDsValue = null;

            if (changeKeyNotInstalledHotfixIdMap != null) {

                TreeSet<String> notInstalledValueTreeSet = changeKeyNotInstalledHotfixIdMap.get(changeKey);

                changeKeyNotInstalledHotfixIDsValue = GeneralUtilities
                        .getCollectionAsSeperatedValues(notInstalledValueTreeSet, ", ", false);
            }

            List<String> valueList = new ArrayList<>();

            valueList.add(changeKey);
            valueList.add(changeKeyHotfixIDsValue);
            valueList.add(changeKeyNotInstalledHotfixIDsValue);

            HotfixReportRecord hotfixReportRecord = new HotfixReportRecord(key, valueList);
            installedChangeKeyHotfixRecordMap.put(key, hotfixReportRecord);
        }

        return installedChangeKeyHotfixRecordMap;
    }

    private Map<Integer, HotfixReportRecord> getNotInstalledChangeKeyHotfixRecordMap(
            Map<String, TreeSet<String>> changeKeyNotInstalledHotfixIdMap) {

        Map<Integer, HotfixReportRecord> notInstalledChangeKeyHotfixRecordMap = new HashMap<>();

        AtomicInteger indexCounter = new AtomicInteger(0);

        for (Map.Entry<String, TreeSet<String>> changeKeyEntry : changeKeyNotInstalledHotfixIdMap.entrySet()) {

            Integer key = indexCounter.incrementAndGet();

            String changeKey = changeKeyEntry.getKey();
            TreeSet<String> valueTreeSet = changeKeyEntry.getValue();

            String changeKeyHotfixIDsValue = GeneralUtilities.getCollectionAsSeperatedValues(valueTreeSet, ", ", false);

            List<String> valueList = new ArrayList<>();

            valueList.add(changeKey);
            valueList.add(changeKeyHotfixIDsValue);

            HotfixReportRecord hotfixReportRecord = new HotfixReportRecord(key, valueList);
            notInstalledChangeKeyHotfixRecordMap.put(key, hotfixReportRecord);
        }

        return notInstalledChangeKeyHotfixRecordMap;
    }

    private JPanel getTableJPanel(String description, Map<Integer, HotfixReportRecord> hotfixReportRecordMap,
            List<HotfixColumn> tableColumnList) {

        JPanel tableJPanel = new JPanel(new BorderLayout());

        JPanel labelJPanel = getLabelJPanel(description);

        JTable changeKeyTable = getHotfixReportTable(hotfixReportRecordMap, tableColumnList);

        JScrollPane changeKeyTableScrollPane = new JScrollPane(changeKeyTable);

        tableJPanel.add(labelJPanel, BorderLayout.NORTH);
        tableJPanel.add(changeKeyTableScrollPane, BorderLayout.CENTER);

        return tableJPanel;
    }

    private JPanel getLabelJPanel(String text) {

        JPanel labelJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(labelJPanel, BoxLayout.LINE_AXIS);
        labelJPanel.setLayout(layout);

        JLabel label = new JLabel(text);

        int height = 30;

        Dimension spacer = new Dimension(10, height);
        labelJPanel.add(Box.createRigidArea(spacer));
        labelJPanel.add(label);
        labelJPanel.add(Box.createHorizontalGlue());

        labelJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return labelJPanel;

    }

    private HotfixReportTable getHotfixReportTable(Map<Integer, HotfixReportRecord> hotfixReportRecordMap,
            List<HotfixColumn> tableColumnList) {

        HotfixReportTableModel hotfixReportTableModel = new HotfixReportTableModel(hotfixReportRecordMap,
                tableColumnList);

        HotfixReportTable hotfixReportTable = new HotfixReportTable(hotfixReportTableModel);

        return hotfixReportTable;
    }
}