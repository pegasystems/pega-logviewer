/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import com.pega.gcs.fringecommon.guiutilities.FilterTable;

public class AlertMessageReportTable extends FilterTable<AlertMessageReportEntry> {

    private static final long serialVersionUID = -636596447061745747L;

    private AlertMessageReportTableMouseListener alertMessageReportTableMouseListener;

    public AlertMessageReportTable(String alertModelName, AlertMessageReportModel alertMessageReportModel) {

        super(alertMessageReportModel);
    }

    public void setAlertMessageReportTableMouseListener(
            AlertMessageReportTableMouseListener alertMessageReportTableMouseListener) {
        this.alertMessageReportTableMouseListener = alertMessageReportTableMouseListener;

        addMouseListener(alertMessageReportTableMouseListener);
    }

    @Override
    public void removeNotify() {

        super.removeNotify();

        if (alertMessageReportTableMouseListener != null) {

            removeMouseListener(alertMessageReportTableMouseListener);

            alertMessageReportTableMouseListener.clearFrameMap();
        }
    }
}
