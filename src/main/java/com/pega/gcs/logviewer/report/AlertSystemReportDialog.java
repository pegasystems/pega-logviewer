/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTable;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;
import com.pega.gcs.logviewer.report.alert.AlertMessageReportModel;
import com.pega.gcs.logviewer.report.alert.AlertSummaryJPanel;
import com.pega.gcs.logviewer.report.alert.AlertTypeSummaryJPanel;

public class AlertSystemReportDialog extends SystemReportDialog {

    private static final long serialVersionUID = -4294293375116690122L;

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertSystemReportDialog.class);

    private Map<String, JPanel> alertMessageTabComponentMap;

    private LogTable logTable;

    public AlertSystemReportDialog(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, LogTable logTable, ImageIcon appIcon,
            Component parent) {

        super("Alert Overview - " + logTableModel.getModelName(), logTableModel, navigationTableController, appIcon,
                parent);

        this.logTable = logTable;

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1400, 800));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainJPanel());

        pack();

        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    @Override
    protected void buildTabs() {

        try {

            addDefaultTabs();

            NavigationTableController<LogEntryKey> navigationTableController;
            navigationTableController = getNavigationTableController();

            LogTableModel logTableModel = getLogTableModel();
            AlertLogEntryModel alertLogEntryModel = (AlertLogEntryModel) logTableModel.getLogEntryModel();
            Locale locale = logTableModel.getLocale();
            boolean isAnyColumnFiltered = logTableModel.isAnyColumnFiltered();

            Set<LogSeriesCollection> logTimeSeriesCollectionSet;
            logTimeSeriesCollectionSet = alertLogEntryModel.getLogTimeSeriesCollectionSet(isAnyColumnFiltered, locale);

            Map<Integer, AlertMessageReportModel> alertMessageReportModelMap;
            alertMessageReportModelMap = alertLogEntryModel.getFilteredAlertMessageReportModelMap(isAnyColumnFiltered, locale);

            // creating before as AlertTypeSummaryJPanel uses this for setting
            // up mouse listener
            JTabbedPane reportTabbedPane = getReportTabbedPane();

            Dimension labelDim = new Dimension(100, 26);

            alertMessageTabComponentMap = new HashMap<>();

            NumberFormat numberFormat = NumberFormat.getInstance(locale);

            JPanel alertTypeSummaryPanel = new AlertTypeSummaryJPanel(logTimeSeriesCollectionSet, numberFormat,
                    reportTabbedPane, alertMessageTabComponentMap);

            String tabLabelText = "Alerts Summary";

            GUIUtilities.addTab(reportTabbedPane, alertTypeSummaryPanel, tabLabelText, labelDim);

            labelDim = new Dimension(70, 26);

            Iterator<LogSeriesCollection> ltscIt = logTimeSeriesCollectionSet.iterator();

            while (ltscIt.hasNext()) {

                LogSeriesCollection ltsc = ltscIt.next();

                String messageId = ltsc.getName();

                Integer alertId = AlertMessageListProvider.getInstance().getAlertId(messageId);

                AlertMessageReportModel alertMessageReportModel = alertMessageReportModelMap.get(alertId);

                JPanel alertJPanel = new AlertSummaryJPanel(ltsc, alertMessageReportModel, logTable,
                        navigationTableController);

                GUIUtilities.addTab(reportTabbedPane, alertJPanel, messageId, labelDim);

                alertMessageTabComponentMap.put(messageId, alertJPanel);
            }
        } catch (Exception e) {
            LOG.error("Error building overview tabs.", e);
        }

    }

}
