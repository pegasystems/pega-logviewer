/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.logviewer.catalog.CatalogManagerWrapper;
import com.pega.gcs.logviewer.catalog.HotfixTable;
import com.pega.gcs.logviewer.catalog.HotfixTableMouseListener;
import com.pega.gcs.logviewer.catalog.model.HotfixEntryKey;
import com.pega.gcs.logviewer.hotfixscan.HotfixScanTableModel;

public class HotfixScanSingleTableView extends HotfixScanView {

    private static final long serialVersionUID = 5124626447046521621L;

    private JCheckBox displayNotInstalledHfixesCheckBox;

    private HotfixTable hotfixTable;

    private SearchPanel<HotfixEntryKey> searchPanel;

    private JTextField statusBar;

    public HotfixScanSingleTableView(HotfixScanTableModel hotfixScanTableModel, JPanel supplementUtilityJPanel,
            NavigationTableController<HotfixEntryKey> navigationTableController) {

        super(hotfixScanTableModel, supplementUtilityJPanel, navigationTableController);

        HotfixTable hotfixTable = getHotfixTable();

        navigationTableController.addCustomJTable(hotfixTable);

        searchPanel = new SearchPanel<>(navigationTableController, hotfixScanTableModel.getSearchModel());

        setLayout(new BorderLayout());

        JPanel utilityJPanel = getUtilityJPanel();
        JPanel scanResultDataJPanel = getScanResultDataJPanel();
        JPanel statusBarJPanel = getStatusBarJPanel();

        add(utilityJPanel, BorderLayout.NORTH);
        add(scanResultDataJPanel, BorderLayout.CENTER);
        add(statusBarJPanel, BorderLayout.SOUTH);

        hotfixScanTableModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {

                    JTextField statusBar = getStatusBar();
                    Message message = (Message) evt.getNewValue();

                    GUIUtilities.setMessage(statusBar, message);
                }

            }
        });

        updateSupplementUtilityJPanel();
    }

    @Override
    protected void updateSupplementUtilityJPanel() {
        JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

        supplementUtilityJPanel.removeAll();
        LayoutManager layout = new BoxLayout(supplementUtilityJPanel, BoxLayout.LINE_AXIS);
        supplementUtilityJPanel.setLayout(layout);
        supplementUtilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        CatalogManagerWrapper catalogManagerWrapper = CatalogManagerWrapper.getInstance();

        if (catalogManagerWrapper.isInitialised()) {

            Dimension endspacer = new Dimension(15, 10);

            JCheckBox displayNotInstalledHfixesCheckBox = getDisplayNotInstalledHfixesCheckBox();

            supplementUtilityJPanel.add(Box.createHorizontalGlue());
            supplementUtilityJPanel.add(Box.createRigidArea(endspacer));
            supplementUtilityJPanel.add(displayNotInstalledHfixesCheckBox);
            supplementUtilityJPanel.add(Box.createRigidArea(endspacer));
            supplementUtilityJPanel.add(Box.createHorizontalGlue());
        }

        supplementUtilityJPanel.revalidate();
        supplementUtilityJPanel.repaint();
    }

    @Override
    protected void performComponentResized(Rectangle oldBounds, Rectangle newBounds) {

        HotfixTable hotfixTable = getHotfixTable();

        TableColumnModel tableColumnModel = hotfixTable.getColumnModel();

        GUIUtilities.applyTableColumnResize(tableColumnModel, oldBounds, newBounds);
    }

    private HotfixTable getHotfixTable() {

        if (hotfixTable == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();
            hotfixTable = new HotfixTable(hotfixScanTableModel);

            hotfixTable.setFillsViewportHeight(true);

            HotfixTableMouseListener hotfixTableMouseListener = new HotfixTableMouseListener(this);

            hotfixTable.addMouseListener(hotfixTableMouseListener);
        }

        return hotfixTable;
    }

    private JCheckBox getDisplayNotInstalledHfixesCheckBox() {

        if (displayNotInstalledHfixesCheckBox == null) {

            HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

            boolean displayNotInstalledHfixes;
            displayNotInstalledHfixes = hotfixScanTableModel.isDisplayNotInstalledHfixes();

            displayNotInstalledHfixesCheckBox = new JCheckBox("Show Catalog 'Not Installed' hotfixes",
                    displayNotInstalledHfixes);

            displayNotInstalledHfixesCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (displayNotInstalledHfixesCheckBox.isSelected()) {
                        hotfixScanTableModel.setDisplayNotInstalledHfixes(true);
                    } else {
                        hotfixScanTableModel.setDisplayNotInstalledHfixes(false);
                    }

                }
            });
        }

        return displayNotInstalledHfixesCheckBox;
    }

    private JTextField getStatusBar() {

        if (statusBar == null) {
            statusBar = new JTextField();
            statusBar.setEditable(false);
            statusBar.setBackground(null);
            statusBar.setBorder(null);
        }

        return statusBar;
    }

    private JPanel getUtilityJPanel() {

        JPanel utilityJPanel = new JPanel();

        utilityJPanel.setLayout(new GridBagLayout());

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

        JPanel utilsPanel = getUtilsPanel();

        utilityJPanel.add(searchPanel, gbc1);
        utilityJPanel.add(utilsPanel, gbc2);

        return utilityJPanel;
    }

    private JPanel getScanResultDataJPanel() {

        HotfixTable hotfixTable = getHotfixTable();

        JPanel scanResultTableJPanel = new JPanel();
        scanResultTableJPanel.setLayout(new BorderLayout());

        JScrollPane traceTableScrollpane = new JScrollPane(hotfixTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        hotfixTable.setPreferredScrollableViewportSize(traceTableScrollpane.getPreferredSize());

        HotfixScanTableModel hotfixScanTableModel = getHotfixScanTableModel();

        JPanel markerBarPanel = getMarkerBarPanel(hotfixScanTableModel);

        scanResultTableJPanel.add(traceTableScrollpane, BorderLayout.CENTER);
        scanResultTableJPanel.add(markerBarPanel, BorderLayout.EAST);

        return scanResultTableJPanel;
    }

    private JPanel getStatusBarJPanel() {

        JPanel statusBarJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(statusBarJPanel, BoxLayout.LINE_AXIS);
        statusBarJPanel.setLayout(layout);

        Dimension spacer = new Dimension(5, 20);

        JTextField statusBar = getStatusBar();

        statusBarJPanel.add(Box.createRigidArea(spacer));
        statusBarJPanel.add(statusBar);
        statusBarJPanel.add(Box.createRigidArea(spacer));

        statusBarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statusBarJPanel;

    }

    private JPanel getUtilsPanel() {

        JPanel utilsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilsPanel, BoxLayout.LINE_AXIS);
        utilsPanel.setLayout(layout);

        Dimension dim = new Dimension(5, 40);

        utilsPanel.add(Box.createHorizontalGlue());
        utilsPanel.add(Box.createRigidArea(dim));

        utilsPanel.add(Box.createHorizontalGlue());

        utilsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return utilsPanel;
    }
}
