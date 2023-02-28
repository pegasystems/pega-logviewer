
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.CustomChartPanel;
import com.pega.gcs.logviewer.LogViewerUtil;

public class PartitionsChartPanel extends JPanel {

    private static final long serialVersionUID = 562570677833930208L;

    private static final Log4j2Helper LOG = new Log4j2Helper(PartitionsChartPanel.class);

    private LifeCycleEventTableModel lifeCycleEventTableModel;

    private CustomChartPanel customChartPanel;

    private CombinedDomainXYPlot combinedDomainXYPlot;

    public PartitionsChartPanel(LifeCycleEventTableModel lifeCycleEventTableModel) {

        super();

        this.lifeCycleEventTableModel = lifeCycleEventTableModel;

        setLayout(new BorderLayout());

        CustomChartPanel customChartPanel = getCustomChartPanel();

        JScrollPane scrollPane = new JScrollPane(customChartPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        refreshChart();
    }

    private LifeCycleEventTableModel getLifeCycleEventTableModel() {
        return lifeCycleEventTableModel;
    }

    private CustomChartPanel getCustomChartPanel() {

        if (customChartPanel == null) {

            CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

            LifeCycleEventTableModel lifeCycleEventTableModel;
            lifeCycleEventTableModel = getLifeCycleEventTableModel();

            String filePath = lifeCycleEventTableModel.getFilePath();

            File file = new File(filePath);
            String name = FileUtilities.getFileBaseName(file);
            File parentDir = file.getParentFile();

            JFreeChart chart = new JFreeChart(name, JFreeChart.DEFAULT_TITLE_FONT, combinedDomainXYPlot, false);

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
            customChartPanel.setMouseWheelEnabled(true);
            customChartPanel.setRangeZoomable(false);
            customChartPanel.setDefaultDirectoryForSaveAs(parentDir);

            customChartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        }

        return customChartPanel;
    }

    private CombinedDomainXYPlot getCombinedDomainXYPlot() {

        if (combinedDomainXYPlot == null) {

            combinedDomainXYPlot = new CombinedDomainXYPlot();
            combinedDomainXYPlot.setDomainPannable(true);

        }

        return combinedDomainXYPlot;
    }

    private void refreshChart() {

        LOG.info("Refresh chart - start");

        CombinedDomainXYPlot combinedDomainXYPlot = getCombinedDomainXYPlot();

        LifeCycleEventTableModel lifeCycleEventTableModel;
        lifeCycleEventTableModel = getLifeCycleEventTableModel();

        ValueAxis domainAxis = lifeCycleEventTableModel.getDomainAxis();

        combinedDomainXYPlot.setDomainAxis(domainAxis);

        // build dummy plot to extend the chart covering the full file time period
        long lowerDomainRange = lifeCycleEventTableModel.getLowerDomainRange();
        long upperDomainRange = lifeCycleEventTableModel.getUpperDomainRange();

        DateFormat dateFormat = new SimpleDateFormat(DateTimeUtilities.DATEFORMAT_ISO8601);

        Locale locale = lifeCycleEventTableModel.getLocale();

        XYPlot dummyXYPlot = LogViewerUtil.getLogXYPlot(lowerDomainRange, upperDomainRange, dateFormat, locale);

        combinedDomainXYPlot.add(dummyXYPlot);

        dummyXYPlot.setWeight(0);

        // build task collection
        Map<String, TaskSeries> partitionTaskSeriesMap;
        partitionTaskSeriesMap = lifeCycleEventTableModel.getPartitionTaskSeriesMap();

        ArrayList<String> axisPartitionList = new ArrayList<>();

        TaskSeriesCollection taskSeriesCollection = new TaskSeriesCollection();

        for (Map.Entry<String, TaskSeries> entry : partitionTaskSeriesMap.entrySet()) {

            String partition = entry.getKey();
            TaskSeries taskSeries = entry.getValue();

            taskSeriesCollection.add(taskSeries);
            axisPartitionList.add(partition);

        }

        XYTaskDataset xyTaskDataset = new XYTaskDataset(taskSeriesCollection);

        xyTaskDataset.setTransposed(true);

        // task plot

        SymbolAxis rangeAxis = new SymbolAxis("Partitions",
                axisPartitionList.toArray(new String[axisPartitionList.size()]));
        rangeAxis.setGridBandsVisible(false);

        XYToolTipGenerator toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();

        XYBarRenderer xyBarRenderer = new PartitionRenderer();
        xyBarRenderer.setUseYInterval(true);
        xyBarRenderer.setDefaultToolTipGenerator(toolTipGenerator);
        xyBarRenderer.setShadowVisible(false);

        StandardXYBarPainter standardXYBarPainter = new StandardXYBarPainter();
        xyBarRenderer.setBarPainter(standardXYBarPainter);

        XYPlot xyPlot = new XYPlot();

        xyPlot.setRangeAxis(rangeAxis);
        xyPlot.setRenderer(xyBarRenderer);

        xyPlot.setDataset(xyTaskDataset);

        combinedDomainXYPlot.add(xyPlot);

    }

}
