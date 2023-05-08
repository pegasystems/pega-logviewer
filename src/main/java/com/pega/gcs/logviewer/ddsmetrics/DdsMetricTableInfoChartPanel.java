
package com.pega.gcs.logviewer.ddsmetrics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.CustomChartPanel;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.ddsmetrics.model.DdsMetricTableInfo;

public class DdsMetricTableInfoChartPanel extends JPanel implements TableModelListener {

    private static final long serialVersionUID = -2341634845323578612L;

    private static final Log4j2Helper LOG = new Log4j2Helper(DdsMetricTableInfoChartPanel.class);

    private DateFormat displayDateFormat;

    private DateAxis domainAxis;

    private long lowerDomainRange;

    private long upperDomainRange;

    private DdsMetricTableInfo ddsMetricTableInfo;

    private LogTableModel logTableModel;

    private CombinedDomainXYPlot combinedDomainXYPlot;

    private CustomChartPanel customChartPanel;

    public DdsMetricTableInfoChartPanel(DateFormat displayDateFormat, DateAxis domainAxis, long lowerDomainRange,
            long upperDomainRange, DdsMetricTableInfo ddsMetricTableInfo, LogTableModel logTableModel) {

        super();

        this.displayDateFormat = displayDateFormat;
        this.domainAxis = domainAxis;
        this.lowerDomainRange = lowerDomainRange;
        this.upperDomainRange = upperDomainRange;
        this.ddsMetricTableInfo = ddsMetricTableInfo;
        this.logTableModel = logTableModel;

        refreshChart();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        JPanel chartPanel = getCustomChartPanel();

        JScrollPane scrollPane = new JScrollPane(chartPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, gbc1);

    }

    private DateFormat getDisplayDateFormat() {
        return displayDateFormat;
    }

    private DateAxis getDomainAxis() {
        return domainAxis;
    }

    private long getLowerDomainRange() {
        return lowerDomainRange;
    }

    private long getUpperDomainRange() {
        return upperDomainRange;
    }

    private DdsMetricTableInfo getDdsMetricTableInfo() {
        return ddsMetricTableInfo;
    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
    }

    private CombinedDomainXYPlot getCombinedDomainXYPlot() {

        if (combinedDomainXYPlot == null) {

            DateAxis domainAxis = new DateAxis("Time (-NA-)");
            domainAxis.setLowerMargin(0.02);
            domainAxis.setUpperMargin(0.02);

            Font labelFont = new Font("Arial", Font.PLAIN, 10);
            domainAxis.setLabelFont(labelFont);

            combinedDomainXYPlot = new CombinedDomainXYPlot(domainAxis);
            combinedDomainXYPlot.setGap(5.0);
            combinedDomainXYPlot.setOrientation(PlotOrientation.VERTICAL);
            combinedDomainXYPlot.setDomainPannable(true);

        }

        return combinedDomainXYPlot;
    }

    private CustomChartPanel getCustomChartPanel() {

        if (customChartPanel == null) {

            CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

            LogTableModel logTableModel = getLogTableModel();
            String filePath = logTableModel.getFilePath();
            File file = new File(filePath);
            File parentDir = file.getParentFile();

            DdsMetricTableInfo ddsMetricTableInfo = getDdsMetricTableInfo();
            String tablename = ddsMetricTableInfo.getTablename();

            JFreeChart chart = new JFreeChart(tablename, JFreeChart.DEFAULT_TITLE_FONT, combinedDomainXYPlot, false);

            ChartUtils.applyCurrentTheme(chart);

            // customise the title position and font
            TextTitle textTitle = chart.getTitle();
            textTitle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            textTitle.setPaint(Color.DARK_GRAY);
            textTitle.setFont(new Font("Arial", Font.BOLD, 12));
            textTitle.setPadding(10, 10, 5, 10);

            customChartPanel = new CustomChartPanel(chart);

            customChartPanel.setMinimumDrawWidth(0);
            customChartPanel.setMinimumDrawHeight(0);
            customChartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
            customChartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
            customChartPanel.setPreferredSize(new Dimension(1890, 5000));
            customChartPanel.setMouseWheelEnabled(false);
            customChartPanel.setRangeZoomable(false);
            customChartPanel.setDefaultDirectoryForSaveAs(parentDir);

            customChartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        }

        return customChartPanel;
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {

        if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
            refreshChart();
        }
    }

    private void refreshChart() {

        try {

            LOG.debug("Refresh chart - start");

            CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

            ValueAxis domainAxis = getDomainAxis();
            combinedDomainXYPlot.setDomainAxis(domainAxis);

            long lowerDomainRange = getLowerDomainRange();
            long upperDomainRange = getUpperDomainRange();

            XYPlot domainRangeXYPlot = getDomainRangeXYPlot(lowerDomainRange, upperDomainRange);

            combinedDomainXYPlot.add(domainRangeXYPlot);
            domainRangeXYPlot.setWeight(0);

            DdsMetricTableInfo ddsMetricTableInfo = getDdsMetricTableInfo();

            Map<String, TimeSeriesCollection> timeSeriesCollectionMap;
            timeSeriesCollectionMap = ddsMetricTableInfo.getTimeSeriesCollectionMap();

            for (Map.Entry<String, TimeSeriesCollection> entry : timeSeriesCollectionMap.entrySet()) {

                String name = entry.getKey();
                TimeSeriesCollection timeSeriesCollection = entry.getValue();

                XYPlot metricsXYPlot = getMetricsXYPlot(name, timeSeriesCollection);

                combinedDomainXYPlot.add(metricsXYPlot, 5);
            }

        } catch (Exception e) {
            LOG.error("Error refreshing chart", e);
        } finally {
            revalidate();

            LOG.debug("Refresh chart - end");
        }
    }

    private XYPlot getDomainRangeXYPlot(long lowerDomainRange, long upperDomainRange) {

        Date lowerDomainDate = new Date(lowerDomainRange);
        Date upperDomainDate = new Date(upperDomainRange);

        LOG.debug("getXYPlot lowerDomainDate: " + lowerDomainDate + " upperDomainDate: " + upperDomainDate);

        TimeSeries ts = new TimeSeries("Log Time Series");
        RegularTimePeriod rtp;
        TimeSeriesDataItem tsdi;

        // add lower range
        rtp = new Millisecond(lowerDomainDate);
        tsdi = new TimeSeriesDataItem(rtp, 0);
        ts.add(tsdi);

        // add upper range
        rtp = new Millisecond(upperDomainDate);
        tsdi = new TimeSeriesDataItem(rtp, 0);
        ts.add(tsdi);

        TimeSeriesCollection tsc = new TimeSeriesCollection();
        tsc.addSeries(ts);

        XYPlot xyPlot = new XYPlot();
        xyPlot.setDomainCrosshairVisible(false);
        xyPlot.setDomainCrosshairLockedOnData(false);
        xyPlot.setRangeCrosshairVisible(false);
        xyPlot.setRangeCrosshairLockedOnData(false);

        xyPlot.setDataset(tsc);

        return xyPlot;
    }

    private XYPlot getMetricsXYPlot(String name, TimeSeriesCollection timeSeriesCollection) {

        XYPlot xyPlot = new XYPlot();
        xyPlot.setDomainCrosshairVisible(false);
        xyPlot.setDomainCrosshairLockedOnData(false);
        xyPlot.setRangeCrosshairVisible(false);
        xyPlot.setRangeCrosshairLockedOnData(false);

        // // UPDATE XYPLOT
        // double size = 4.0;
        // double delta = size / 2.0;
        //
        // Shape seriesShape = new Ellipse2D.Double(-delta, -delta, size, size);

        XYToolTipGenerator toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();

        // in case of bar chart wit time series
        // XYItemRenderer xyLineAndShapeRenderer = new XYBarRenderer();
        XYItemRenderer xyLineAndShapeRenderer = new XYLineAndShapeRenderer(true, true);

        xyLineAndShapeRenderer.setDefaultToolTipGenerator(toolTipGenerator);

        NumberAxis xyPlotNumberAxis = new NumberAxis(name);
        xyPlotNumberAxis.setAutoRangeIncludesZero(false);
        // xyPlotNumberAxis.setNumberFormatOverride(numberFormat);
        xyPlotNumberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        xyPlot.setRangeAxis(0, xyPlotNumberAxis, false);
        xyPlot.setRenderer(0, xyLineAndShapeRenderer, false);
        // this will fire change event
        xyPlot.clearRangeMarkers();

        // looks like this should be a last call to be set
        xyPlot.setDataset(timeSeriesCollection);

        return xyPlot;
    }
}
