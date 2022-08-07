/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.catalog.model.HotfixEntry;
import com.pega.gcs.logviewer.model.LogSeries;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.model.LogTimeSeries;

public class LogViewerUtil {

    private static final Log4j2Helper LOG = new Log4j2Helper(LogViewerUtil.class);

    private static final String ICON_LOCATION = "./images";

    public static ImageIcon createImageIcon(String iconName, String description) {

        String path = ICON_LOCATION + "/" + iconName;
        java.net.URL imgURL = LogViewerUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static JLabel getHeaderLabel(String labelText, int width) {

        JLabel headerLabel = new JLabel(labelText);

        Font labelFont = headerLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 11);

        headerLabel.setFont(tabFont);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (width > 0) {
            Dimension size = new Dimension(width, 20);
            headerLabel.setPreferredSize(size);
            headerLabel.setSize(size);
        }

        return headerLabel;
    }

    public static void expandAll(JTree tree, boolean expand) {

        Object rootNode = tree.getModel().getRoot();

        TreePath rootPath = new TreePath(rootNode);

        expandAll(tree, rootPath, expand, 0);
    }

    public static void expandAll(JTree tree, TreePath parent, boolean expand, int level) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();

        Enumeration<TreeNode> en = node.children();

        while (en.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) en.nextElement();

            TreePath path = parent.pathByAddingChild(childNode);

            expandAll(tree, path, expand, level + 1);

        }

        // restricting expand/collapse to 1 level onwards
        if (level > 0) {
            // Expansion or collapse must be done bottom-up
            if (expand) {
                tree.expandPath(parent);
            } else {
                tree.collapsePath(parent);
            }
        }
    }

    // in case of combined plots this will become sub plot hence we don't need
    // domain axis for every sub plots, hence will be passing null.
    public static void updatePlots(XYPlot xyPlot, CategoryPlot categoryPlot, LogSeriesCollection logSeriesCollection,
            DateFormat modelDateFormat, Locale locale, boolean notify) {

        String logSeriesCollectionName = logSeriesCollection.getName();
        Collection<LogSeries> logSeriesList = logSeriesCollection.getLogSeriesList();

        // UPDATE XYPLOT
        double size = 4.0;
        double delta = size / 2.0;

        Shape seriesShape = new Ellipse2D.Double(-delta, -delta, size, size);

        XYToolTipGenerator toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();

        // in case of bar chart wit time series
        // XYItemRenderer xyLineAndShapeRenderer = new XYBarRenderer();
        XYItemRenderer xyLineAndShapeRenderer = new XYLineAndShapeRenderer(true, true);

        xyLineAndShapeRenderer.setDefaultToolTipGenerator(toolTipGenerator);

        TimeZone modelTimeZone = modelDateFormat.getTimeZone();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(modelTimeZone);

        int timeSeriesIndex = 0;

        // currently only one data set present in the series list,
        BoxAndWhiskerItem boxAndWhiskerItem = null;
        Color boxAndWhiskerItemColor = Color.BLACK;

        for (LogSeries logSeries : logSeriesList) {

            LogTimeSeries logTimeSeries = (LogTimeSeries) logSeries;

            TimeSeries timeSeries = logTimeSeries.getTimeSeries();
            Color color = logTimeSeries.getColor();

            timeSeriesCollection.addSeries(timeSeries);

            xyLineAndShapeRenderer.setSeriesShape(timeSeriesIndex, seriesShape);
            xyLineAndShapeRenderer.setSeriesPaint(timeSeriesIndex, color);

            timeSeriesIndex++;

            if (categoryPlot != null) {

                boxAndWhiskerItem = logTimeSeries.getBoxAndWhiskerItem();

                if (boxAndWhiskerItem != null) {
                    boxAndWhiskerItemColor = logTimeSeries.getColor();
                }
            }
        }

        NumberAxis xyPlotNumberAxis = new NumberAxis(logSeriesCollectionName);
        xyPlotNumberAxis.setAutoRangeIncludesZero(false);
        xyPlotNumberAxis.setNumberFormatOverride(numberFormat);
        xyPlotNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        xyPlot.setRangeAxis(0, xyPlotNumberAxis, notify);
        xyPlot.setRenderer(0, xyLineAndShapeRenderer, notify);
        // this will fire change event
        xyPlot.clearRangeMarkers();

        // add threshold markers if present
        for (LogSeries logSeries : logSeriesList) {

            LogTimeSeries logTimeSeries = (LogTimeSeries) logSeries;

            Marker marker = logTimeSeries.getThresholdMarker();

            if (marker != null) {
                xyPlot.addRangeMarker(0, marker, Layer.FOREGROUND, notify);
            }
        }

        // looks like this should be a last call to be set
        xyPlot.setDataset(timeSeriesCollection);

        // UPDATE CATEGORY PLOT
        if (categoryPlot != null) {

            // Box and Whisker Plot
            NumberAxis categoryPlotNumberAxis = new NumberAxis();
            categoryPlotNumberAxis.setAutoRangeIncludesZero(false);

            categoryPlotNumberAxis.setNumberFormatOverride(numberFormat);

            categoryPlotNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            BoxAndWhiskerRenderer boxAndWhiskerRenderer = new BoxAndWhiskerRenderer();
            boxAndWhiskerRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
            boxAndWhiskerRenderer.setMaximumBarWidth(0.3);
            boxAndWhiskerRenderer.setDefaultPaint(boxAndWhiskerItemColor);
            boxAndWhiskerRenderer.setDefaultOutlinePaint(boxAndWhiskerItemColor);
            boxAndWhiskerRenderer.setAutoPopulateSeriesPaint(false);
            boxAndWhiskerRenderer.setFillBox(false);
            boxAndWhiskerRenderer.setUseOutlinePaintForWhiskers(true);

            DefaultBoxAndWhiskerCategoryDataset dbawcd = new DefaultBoxAndWhiskerCategoryDataset();
            dbawcd.add(boxAndWhiskerItem, "", "");

            categoryPlot.setRenderer(boxAndWhiskerRenderer, notify);
            categoryPlot.setRangeAxis(0, categoryPlotNumberAxis, notify);

            categoryPlot.setDataset(dbawcd);
        }
    }

    public static void updatePlots(XYPlot xyPlot, CategoryPlot categoryPlot, LogTimeSeries logTimeSeries,
            DateFormat modelDateFormat, Locale locale, boolean notify, boolean autoRange, double minRangeValue,
            double maxRangeValue) {

        String logTimeSeriesName = logTimeSeries.getName();

        // UPDATE XYPLOT
        double size = 4.0;
        double delta = size / 2.0;

        Shape seriesShape = new Ellipse2D.Double(-delta, -delta, size, size);

        XYToolTipGenerator toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();

        // in case of bar chart wit time series
        // XYItemRenderer xyLineAndShapeRenderer = new XYBarRenderer();
        XYItemRenderer xyLineAndShapeRenderer = new XYLineAndShapeRenderer(true, true);

        xyLineAndShapeRenderer.setDefaultToolTipGenerator(toolTipGenerator);

        TimeZone modelTimeZone = modelDateFormat.getTimeZone();
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection(modelTimeZone);

        Color color;

        // currently only one data set present in the series list,
        BoxAndWhiskerItem boxAndWhiskerItem = null;
        Color boxAndWhiskerItemColor = Color.BLACK;

        TimeSeries timeSeries = logTimeSeries.getTimeSeries();
        color = logTimeSeries.getColor();

        timeSeriesCollection.addSeries(timeSeries);

        xyLineAndShapeRenderer.setSeriesShape(0, seriesShape);
        xyLineAndShapeRenderer.setSeriesPaint(0, color);

        if (categoryPlot != null) {

            boxAndWhiskerItem = logTimeSeries.getBoxAndWhiskerItem();

            if (boxAndWhiskerItem != null) {
                boxAndWhiskerItemColor = logTimeSeries.getColor();
            }
        }

        NumberAxis xyPlotNumberAxis = new NumberAxis(logTimeSeriesName);
        xyPlotNumberAxis.setAutoRangeIncludesZero(false);
        xyPlotNumberAxis.setNumberFormatOverride(numberFormat);
        xyPlotNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        if (!autoRange) {

            xyPlotNumberAxis.setAutoRange(false);

            // adding 5% margin
            double range = maxRangeValue - minRangeValue;

            // handle 'A positive range length is required' error
            if (range == 0) {
                range = 1;
            }

            maxRangeValue = maxRangeValue + (0.05 * range);
            minRangeValue = minRangeValue - (0.05 * range);

            xyPlotNumberAxis.setRange(minRangeValue, maxRangeValue);
        }

        xyPlot.setRangeAxis(0, xyPlotNumberAxis, notify);
        xyPlot.setRenderer(0, xyLineAndShapeRenderer, notify);
        // this will fire change event
        xyPlot.clearRangeMarkers();

        // add threshold markers if present

        Marker marker = logTimeSeries.getThresholdMarker();

        if (marker != null) {
            xyPlot.addRangeMarker(0, marker, Layer.FOREGROUND, notify);
        }

        // looks like this should be a last call to be set
        xyPlot.setDataset(timeSeriesCollection);

        // UPDATE CATEGORY PLOT
        if (categoryPlot != null) {

            // Box and Whisker Plot
            NumberAxis categoryPlotNumberAxis = new NumberAxis();
            categoryPlotNumberAxis.setAutoRangeIncludesZero(false);

            categoryPlotNumberAxis.setNumberFormatOverride(numberFormat);

            categoryPlotNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            BoxAndWhiskerRenderer boxAndWhiskerRenderer = new BoxAndWhiskerRenderer();
            boxAndWhiskerRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
            boxAndWhiskerRenderer.setMaximumBarWidth(0.3);
            boxAndWhiskerRenderer.setDefaultPaint(boxAndWhiskerItemColor);
            boxAndWhiskerRenderer.setDefaultOutlinePaint(boxAndWhiskerItemColor);
            boxAndWhiskerRenderer.setAutoPopulateSeriesPaint(false);
            boxAndWhiskerRenderer.setFillBox(false);
            boxAndWhiskerRenderer.setUseOutlinePaintForWhiskers(true);

            DefaultBoxAndWhiskerCategoryDataset dbawcd = new DefaultBoxAndWhiskerCategoryDataset();
            dbawcd.add(boxAndWhiskerItem, "", "");

            categoryPlot.setRenderer(boxAndWhiskerRenderer, notify);
            categoryPlot.setRangeAxis(0, categoryPlotNumberAxis, notify);

            categoryPlot.setDataset(dbawcd);
        }
    }

    public CategoryPlot updateCategoryPlot(CategoryPlot categoryPlot, NumberFormat numberFormat,
            BoxAndWhiskerItem boxAndWhiskerItem, Color color, boolean notify) {

        // Box and Whisker Plot
        NumberAxis valueAxis = new NumberAxis();
        valueAxis.setAutoRangeIncludesZero(false);

        valueAxis.setNumberFormatOverride(numberFormat);

        valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BoxAndWhiskerRenderer boxAndWhiskerRenderer = new BoxAndWhiskerRenderer();
        boxAndWhiskerRenderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        boxAndWhiskerRenderer.setMaximumBarWidth(0.3);
        boxAndWhiskerRenderer.setDefaultPaint(color);
        boxAndWhiskerRenderer.setDefaultOutlinePaint(color);
        boxAndWhiskerRenderer.setAutoPopulateSeriesPaint(false);
        boxAndWhiskerRenderer.setFillBox(false);
        boxAndWhiskerRenderer.setUseOutlinePaintForWhiskers(true);

        DefaultBoxAndWhiskerCategoryDataset dbawcd = new DefaultBoxAndWhiskerCategoryDataset();
        dbawcd.add(boxAndWhiskerItem, "", "");

        categoryPlot.setRenderer(boxAndWhiskerRenderer, notify);
        categoryPlot.setRangeAxis(0, valueAxis, notify);

        categoryPlot.setDataset(dbawcd);

        return categoryPlot;
    }

    public static XYPlot getLogXYPlot(long lowerDomainRange, long upperDomainRange, DateFormat modelDateFormat,
            Locale locale) {

        Date lowerDomainDate = new Date(lowerDomainRange);
        Date upperDomainDate = new Date(upperDomainRange);

        LOG.debug("getLogXYPlot lowerDomainDate: " + lowerDomainDate + " upperDomainDate: " + upperDomainDate);

        TimeZone timeZone = modelDateFormat.getTimeZone();

        TimeSeries ts = new TimeSeries("Log Time Series");
        RegularTimePeriod rtp;
        TimeSeriesDataItem tsdi;

        // add lower range
        rtp = new Millisecond(lowerDomainDate, timeZone, locale);
        tsdi = new TimeSeriesDataItem(rtp, 0);
        ts.add(tsdi);

        // add upper range
        rtp = new Millisecond(upperDomainDate, timeZone, locale);
        tsdi = new TimeSeriesDataItem(rtp, 0);
        ts.add(tsdi);

        TimeSeriesCollection tsc = new TimeSeriesCollection(timeZone);
        tsc.addSeries(ts);

        XYPlot logXYPlot = new XYPlot();
        logXYPlot.setDomainCrosshairVisible(false);
        logXYPlot.setDomainCrosshairLockedOnData(false);
        logXYPlot.setRangeCrosshairVisible(false);
        logXYPlot.setRangeCrosshairLockedOnData(false);

        logXYPlot.setDataset(tsc);

        return logXYPlot;
    }

    public static void recursiveEvaluateBackwardHotfixEntrySet(HotfixEntry hotfixEntry,
            TreeSet<HotfixEntry> totalBackwardHotfixEntrySet) {

        Set<HotfixEntry> backwardHfixEntrySet = hotfixEntry.getBackwardHotfixEntrySet();

        if (backwardHfixEntrySet != null) {

            for (HotfixEntry hfixEntry : backwardHfixEntrySet) {

                boolean added = totalBackwardHotfixEntrySet.add(hfixEntry);

                if (added) {
                    recursiveEvaluateBackwardHotfixEntrySet(hfixEntry, totalBackwardHotfixEntrySet);
                }
            }
        }
    }
}
