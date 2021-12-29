/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.awt.Color;

public interface LogSeries {

    public String getName();

    public int getCount();

    public Color getColor();

    public boolean isShowCount();

    public boolean isDefaultShowLogTimeSeries();

    public void setDefaultShowLogTimeSeries(boolean defaultShowLogTimeSeries);
}
