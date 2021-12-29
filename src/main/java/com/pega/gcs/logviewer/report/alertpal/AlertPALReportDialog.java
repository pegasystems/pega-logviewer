/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alertpal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.MultipleTableSelectionListener;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class AlertPALReportDialog extends JFrame {

    private static final long serialVersionUID = -4294293375116690122L;

    private LogTableModel logTableModel;

    private AlertPALTable alertPalTable;

    private JButton exportToTsvButton;

    private MultipleTableSelectionListener<LogEntryKey> multipleTableSelectionListener;

    public AlertPALReportDialog(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, ImageIcon appIcon, Component parent) {

        super();

        this.logTableModel = logTableModel;

        setTitle("Alert PAL Overview - " + logTableModel.getModelName());

        multipleTableSelectionListener = new MultipleTableSelectionListener<>(logTableModel, navigationTableController);

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1200, 600));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainPanel());

        pack();

        setLocationRelativeTo(parent);

        // setVisible called by caller.
        // setVisible(true);

    }

    public void destroyPanel() {
        multipleTableSelectionListener.clearCustomJTables();
    }

    private JComponent getMainPanel() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel controlsPanel = getControlsPanel();

        JTable alertPalTable = getAlertPalTable();

        JScrollPane palTableScrollpane = new JScrollPane(alertPalTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        palTableScrollpane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        mainPanel.add(controlsPanel, BorderLayout.NORTH);

        mainPanel.add(palTableScrollpane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel getControlsPanel() {

        JPanel controlsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(controlsPanel, BoxLayout.X_AXIS);
        controlsPanel.setLayout(layout);

        JButton exportToTsvButton = getExportToTsvButton();

        Dimension startDim = new Dimension(20, 40);

        controlsPanel.add(Box.createHorizontalGlue());
        controlsPanel.add(Box.createRigidArea(startDim));
        controlsPanel.add(exportToTsvButton);
        controlsPanel.add(Box.createRigidArea(startDim));
        controlsPanel.add(Box.createHorizontalGlue());
        controlsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return controlsPanel;
    }

    private AlertPALTable getAlertPalTable() {

        if (alertPalTable == null) {

            AlertPALTableModel aptm = new AlertPALTableModel(logTableModel);

            alertPalTable = new AlertPALTable(aptm);

            multipleTableSelectionListener.addCustomJTable(alertPalTable);
        }

        return alertPalTable;
    }

    private JButton getExportToTsvButton() {

        if (exportToTsvButton == null) {
            exportToTsvButton = new JButton("Export table as TSV");

            Dimension size = new Dimension(200, 26);
            exportToTsvButton.setPreferredSize(size);

            exportToTsvButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    JTable alertPalTable = getAlertPalTable();

                    AlertPALTableModel aptm = (AlertPALTableModel) alertPalTable.getModel();

                    AlertPALExportDialog apaled = new AlertPALExportDialog(logTableModel, aptm, BaseFrame.getAppIcon(),
                            AlertPALReportDialog.this);
                    apaled.setVisible(true);
                }
            });

        }

        return exportToTsvButton;
    }

}
