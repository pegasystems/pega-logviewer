/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class LogTimeSeriesColor {

	private static final Map<String, Color> logTimeSeriesColorMap;

	static {

		Color a = new Color(240, 163, 255);
		Color b = new Color(0, 117, 220);
		Color c = new Color(153, 63, 0);
		Color d = new Color(76, 0, 92);
		Color e = new Color(25, 25, 25);
		Color f = new Color(0, 92, 49);
		Color g = new Color(43, 206, 72);
		Color h = new Color(255, 192, 153);
		Color i = new Color(128, 128, 128);
		Color j = new Color(148, 235, 181);
		Color k = new Color(143, 124, 0);
		Color l = new Color(157, 204, 0);
		Color m = new Color(194, 0, 136);
		Color n = new Color(0, 51, 128);
		Color o = new Color(255, 164, 5);
		Color p = new Color(255, 168, 187);
		Color q = new Color(66, 102, 0);
		Color r = new Color(255, 0, 16);
		Color s = new Color(50, 200, 242);
		Color t = new Color(0, 153, 143);
		Color u = new Color(116, 10, 255);
		Color v = new Color(192, 0, 0);
		Color w = new Color(255, 80, 5);
		Color x = new Color(237, 118, 81);
		Color y = new Color(126, 126, 184);

		logTimeSeriesColorMap = new HashMap<String, Color>();

		// Colour for Log4J
		String key;
		Color value;

		key = Log4jLogEntryModel.TS_TOTAL_MEMORY;
		value = r;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.TS_USED_MEMORY;
		value = b;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.TS_REQUESTOR_COUNT;
		value = g;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.TS_SHARED_PAGE_MEMORY;
		value = c;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.TS_NUMBER_OF_THREADS;
		value = o;
		logTimeSeriesColorMap.put(key, value);
		
		key = Log4jLogEntryModel.IM_SYSTEM_START;
		value = s;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.IM_THREAD_DUMP;
		value = w;
		logTimeSeriesColorMap.put(key, value);

		key = Log4jLogEntryModel.IM_EXCEPTIONS;
		value = a;
		logTimeSeriesColorMap.put(key, value);

	}

	/**
	 * @return the logtimeseriescolormap
	 */
	public static Map<String, Color> getLogTimeSeriesColorMap() {
		return logTimeSeriesColorMap;
	}

}
