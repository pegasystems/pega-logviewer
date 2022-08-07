/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.pega.gcs.logviewer.model.AlertBoxAndWhiskerItem;

public class AlertBoxAndWhiskerStatisticsJPanel extends JPanel {

    private static final long serialVersionUID = 5136211947722573170L;

    public AlertBoxAndWhiskerStatisticsJPanel(AlertBoxAndWhiskerItem alertBoxAndWhiskerItem, Locale locale) {
        super();

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        buildAlertBoxAndWhiskerHeaderRow(this, 0);
        buildAlertBoxAndWhiskerDataRow(alertBoxAndWhiskerItem, locale, this, 0, 1);
    }

    private JPanel getNameJPanel(String name) {

        JPanel nameJPanel = new JPanel();

        nameJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 5, 5);

        JLabel nameJLabel = new JLabel(name);

        Font labelFont = nameJLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 11);

        nameJLabel.setFont(tabFont);
        nameJLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nameJPanel.add(nameJLabel, gbc1);

        nameJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return nameJPanel;
    }

    private JPanel getValueJPanel(String value) {

        JPanel valueJPanel = new JPanel();

        valueJPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 5, 5, 5);

        JComponent valueComponent;

        JTextField valueJTextField = new JTextField(value);
        valueJTextField.setEditable(false);
        valueJTextField.setBackground(null);
        valueJTextField.setBorder(null);

        valueJTextField.setHorizontalAlignment(SwingConstants.CENTER);
        valueComponent = valueJTextField;

        valueJPanel.add(valueComponent, gbc1);

        valueJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return valueJPanel;
    }

    private void buildAlertBoxAndWhiskerHeaderRow(JPanel parent, int startColumnIndex) {

        List<String> nameList = new ArrayList<>();

        // "KPI Threshold";
        nameList.add(AlertBoxAndWhiskerReportColumn.KPI_THRESHOLD.getDisplayName());

        // "Count";
        nameList.add(AlertBoxAndWhiskerReportColumn.COUNT.getDisplayName());

        // "Min";
        nameList.add(AlertBoxAndWhiskerReportColumn.MINOUTLIER.getDisplayName());

        // "Q1";
        nameList.add(AlertBoxAndWhiskerReportColumn.Q1.getDisplayName());

        // "Median";
        nameList.add(AlertBoxAndWhiskerReportColumn.MEDIAN.getDisplayName());

        // "Mean";
        nameList.add(AlertBoxAndWhiskerReportColumn.MEAN.getDisplayName());

        // "Q3";
        nameList.add(AlertBoxAndWhiskerReportColumn.Q3.getDisplayName());

        // "Max";
        nameList.add(AlertBoxAndWhiskerReportColumn.MAXOUTLIER.getDisplayName());

        // "IQR";
        nameList.add(AlertBoxAndWhiskerReportColumn.IQR.getDisplayName());

        // "Outliers";
        nameList.add(AlertBoxAndWhiskerReportColumn.OUTLIERS.getDisplayName());

        int xindex = startColumnIndex;

        for (String name : nameList) {

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = xindex++;
            gbc.gridy = 0;
            gbc.weightx = 1.0D;
            gbc.weighty = 1.0D;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(0, 0, 0, 0);

            JPanel nameJPanel = getNameJPanel(name);
            parent.add(nameJPanel, gbc);
        }

    }

    private void buildAlertBoxAndWhiskerDataRow(AlertBoxAndWhiskerItem alertBoxAndWhiskerItem, Locale locale,
            JPanel parent, int startColumnIndex, int rowIndex) {

        List<String> valueList = new ArrayList<>();

        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        // "KPI Threshold";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getThresholdKPI()));

        // "Count";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getCount()));

        // "Min";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getMinOutlier()));

        // "Q1";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getQ1()));

        // "Median";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getMedian()));

        // "Mean";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getMean()));

        // "Q3";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getQ3()));

        // "Max";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getMaxOutlier()));

        // "IQR";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getIQR()));

        // "Outliers";
        valueList.add(numberFormat.format(alertBoxAndWhiskerItem.getOutliers().size()));

        int xindex = startColumnIndex;

        for (String value : valueList) {

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = xindex++;
            gbc.gridy = rowIndex;
            gbc.weightx = 1.0D;
            gbc.weighty = 1.0D;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(0, 0, 0, 0);

            JPanel valueJPanel = getValueJPanel(value);
            parent.add(valueJPanel, gbc);
        }

    }
}
