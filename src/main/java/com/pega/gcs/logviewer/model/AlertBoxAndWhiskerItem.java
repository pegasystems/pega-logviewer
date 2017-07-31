/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model;

import java.util.List;

import org.jfree.data.statistics.BoxAndWhiskerItem;

public class AlertBoxAndWhiskerItem extends BoxAndWhiskerItem implements Comparable<AlertBoxAndWhiskerItem> {

	private static final long serialVersionUID = -3173881343396055569L;

	private int count;

	private long thresholdKPI;

	private String kpiUnit;

	private double total;

	public AlertBoxAndWhiskerItem(int count, long thresholdKPI, String kpiUnit, double total, double mean,
			double median, double q1, double q3, double minRegularValue, double maxRegularValue, double minOutlier,
			double maxOutlier, List<Double> outliers) {

		super(mean, median, q1, q3, minRegularValue, maxRegularValue, minOutlier, maxOutlier, outliers);

		this.count = count;
		this.thresholdKPI = thresholdKPI;
		this.kpiUnit = kpiUnit;
		this.total = total;
	}

	public int getCount() {
		return count;
	}

	public long getThresholdKPI() {
		return thresholdKPI;
	}

	public String getKpiUnit() {
		return kpiUnit;
	}

	public double getTotal() {
		return total;
	}

	public double getIQR() {
		return getQ3().doubleValue() - getQ1().doubleValue();
	}

	@Override
	public int compareTo(AlertBoxAndWhiskerItem o) {
		Double thisTotal = new Double(getMaxOutlier().toString());
		Double otherTotal = new Double(o.getMaxOutlier().toString());

		int result = thisTotal.compareTo(otherTotal);

		if (result == 0) {
			Integer thisCount = Integer.valueOf(getCount());
			Integer otherCount = Integer.valueOf(o.getCount());
			result = thisCount.compareTo(otherCount);
		}

		return result;
	}

}
