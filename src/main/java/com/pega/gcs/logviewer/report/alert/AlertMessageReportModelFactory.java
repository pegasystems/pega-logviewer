/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertMessageReportModelFactory {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageReportModelFactory.class);

    private static AlertMessageReportModelFactory _INSTANCE;

    private AlertMessageReportModelFactory() {

    }

    public static AlertMessageReportModelFactory getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new AlertMessageReportModelFactory();
        }

        return _INSTANCE;
    }

    public AlertMessageReportModel getAlertMessageReportModel(Integer alertId, long thresholdKPI,
            AlertLogEntryModel alertLogEntryModel, Locale locale) {

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

        AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(alertId);

        return getAlertMessageReportModel(alertMessage, thresholdKPI, alertLogEntryModel, locale);

    }

    @SuppressWarnings("unchecked")
    private AlertMessageReportModel getAlertMessageReportModel(AlertMessage alertMessage, long thresholdKPI,
            AlertLogEntryModel alertLogEntryModel, Locale locale) {

        String messageId = alertMessage.getMessageID();

        AlertMessageReportModel alertMessageReportModel = null;

        StringBuilder reportModelClassSB = new StringBuilder();
        reportModelClassSB.append("com.pega.gcs.logviewer.report.alert.");
        reportModelClassSB.append(messageId);
        reportModelClassSB.append("ReportModel");

        String reportModelClassName = reportModelClassSB.toString();

        try {

            Class<? extends AlertMessageReportModel> reportModelClass;
            reportModelClass = (Class<? extends AlertMessageReportModel>) Class.forName(reportModelClassName);

            Class<?>[] constructorArgTypeArray = new Class[] { AlertMessage.class, long.class, AlertLogEntryModel.class,
                    Locale.class };

            Constructor<? extends AlertMessageReportModel> reportModelClassConstructor;
            reportModelClassConstructor = reportModelClass.getDeclaredConstructor(constructorArgTypeArray);

            alertMessageReportModel = reportModelClassConstructor.newInstance(alertMessage, thresholdKPI,
                    alertLogEntryModel, locale);
        } catch (InvocationTargetException ite) {
            LOG.error("InvocationTargetException loading Report model class: " + reportModelClassName,
                    ite.getTargetException());
        } catch (Exception e) {
            LOG.error("Error loading Report model class: " + reportModelClassName, e);
        }

        if (alertMessageReportModel == null) {
            alertMessageReportModel = new DefaultReportModel(alertMessage, thresholdKPI, alertLogEntryModel, locale);
        }

        return alertMessageReportModel;
    }

    public static void main(String[] args) {
        // test if AlertMessageReportModel are created successfully

        AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();

        AlertMessageReportModelFactory alertMessageReportModelFactory = AlertMessageReportModelFactory.getInstance();

        List<String> unImplementedList = new ArrayList<>();

        for (String messageId : alertMessageListProvider.getMessageIdList()) {

            AlertMessage alertMessage = alertMessageListProvider.getAlertMessage(messageId);

            AlertMessageReportModel alertMessageReportModel;
            alertMessageReportModel = alertMessageReportModelFactory.getAlertMessageReportModel(alertMessage, 0, null,
                    Locale.getDefault());

            if (alertMessageReportModel != null) {
                LOG.info("Report Model for message id: " + messageId + " " + alertMessageReportModel.toString());
            } else {
                unImplementedList.add(messageId);
            }
        }

        LOG.info("Unimplemented List size: " + unImplementedList.size());

        for (String messageId : unImplementedList) {
            LOG.info("Did not find report Model for message id: " + messageId);
        }
    }
}
