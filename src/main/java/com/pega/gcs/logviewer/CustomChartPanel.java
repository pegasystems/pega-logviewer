
package com.pega.gcs.logviewer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;

public class CustomChartPanel extends ChartPanel {

    private static final long serialVersionUID = 201114145017504830L;

    public CustomChartPanel(JFreeChart chart) {
        super(chart);
    }

    public CustomChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    @Override
    public void doSaveAs() throws IOException {

        JFreeChart chart = getChart();

        File defaultDirectoryForSaveAs = getDefaultDirectoryForSaveAs();

        JFileChooser fileChooser = new JFileChooser(defaultDirectoryForSaveAs);

        TextTitle textTitle = chart.getTitle();

        String proposedFilename = (textTitle != null) ? (textTitle.getText() + ".png") : null;

        if (proposedFilename != null) {
            File proposedFile = new File(defaultDirectoryForSaveAs, proposedFilename);
            fileChooser.setSelectedFile(proposedFile);
        }

        FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PNG_Image_Files"),
                "png");

        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        int option = fileChooser.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {

            String filename = fileChooser.getSelectedFile().getPath();

            if (isEnforceFileExtensions()) {
                if (!filename.endsWith(".png")) {
                    filename = filename + ".png";
                }
            }

            ChartUtils.saveChartAsPNG(new File(filename), getChart(), getWidth(), getHeight());
        }
    }
}
