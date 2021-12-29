/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;

public class AlertBoxAndWhiskerCalculator {

    public static AlertBoxAndWhiskerItem calculateStatistics(List<Double> valueList, long thresholdKPI,
            String kpiUnit) {

        AlertBoxAndWhiskerItem alertBoxAndWhiskerItem = null;

        if (valueList != null) {

            double total = 0;

            List<Double> workinglist;

            workinglist = new ArrayList<Double>(valueList.size());

            for (Double value : valueList) {

                if ((value != null) && (!Double.isNaN(value))) {
                    workinglist.add(value);

                    total += value;
                }
            }

            int count = workinglist.size();

            double mean = total / count;
            double median = calculateMedian(workinglist);
            double q1 = calculateQ1(workinglist);
            double q3 = calculateQ3(workinglist);
            double iqr = q3 - q1;

            double upperOutlierThreshold = q3 + (iqr * 1.5);
            double lowerOutlierThreshold = q1 - (iqr * 1.5);

            double minRegularValue = Double.POSITIVE_INFINITY;
            double maxRegularValue = Double.NEGATIVE_INFINITY;
            double minOutlier = Double.POSITIVE_INFINITY;
            double maxOutlier = Double.NEGATIVE_INFINITY;

            List<Double> outliers = new ArrayList<Double>();

            for (Double value : workinglist) {

                if (value > upperOutlierThreshold) {
                    outliers.add(value);

                    if (value > maxOutlier) {
                        maxOutlier = value;
                    }

                } else if (value < lowerOutlierThreshold) {
                    outliers.add(value);

                    if (value < minOutlier) {
                        minOutlier = value;
                    }

                } else {
                    minRegularValue = Math.min(minRegularValue, value);
                    maxRegularValue = Math.max(maxRegularValue, value);
                }

                minOutlier = Math.min(minOutlier, minRegularValue);
                maxOutlier = Math.max(maxOutlier, maxRegularValue);
            }

            alertBoxAndWhiskerItem = new AlertBoxAndWhiskerItem(count, thresholdKPI, kpiUnit, total, mean, median, q1,
                    q3, minRegularValue, maxRegularValue, minOutlier, maxOutlier, outliers);

        }

        return alertBoxAndWhiskerItem;
    }

    private static double calculateQ1(List<Double> values) {

        double result = Double.NaN;

        int count = values.size();

        if (count > 0) {

            if (count % 2 == 1) {

                if (count > 1) {
                    result = calculateMedian(values, 0, count / 2);
                } else {
                    result = calculateMedian(values, 0, 0);
                }
            } else {
                result = calculateMedian(values, 0, count / 2 - 1);
            }

        }
        return result;
    }

    private static double calculateQ3(List<Double> values) {

        double result = Double.NaN;

        int count = values.size();

        if (count > 0) {

            if (count % 2 == 1) {

                if (count > 1) {
                    result = calculateMedian(values, count / 2, count - 1);
                } else {
                    result = calculateMedian(values, 0, 0);
                }
            } else {
                result = calculateMedian(values, count / 2, count - 1);
            }
        }
        return result;
    }

    private static double calculateMedian(List<Double> values) {

        double result = Double.NaN;

        int count = values.size();

        if (count > 0) {
            result = calculateMedian(values, 0, count - 1);
        }

        return result;
    }

    private static double calculateMedian(List<Double> values, int start, int end) {

        double result = Double.NaN;

        int count = end - start + 1;

        if (count > 0) {

            if (count % 2 == 1) {

                if (count > 1) {
                    result = values.get(start + (count - 1) / 2);
                } else {
                    result = values.get(start);
                }
            } else {
                double value1 = values.get(start + count / 2 - 1);
                double value2 = values.get(start + count / 2);
                result = (value1 + value2) / 2.0;
            }
        }

        return result;

    }
}
