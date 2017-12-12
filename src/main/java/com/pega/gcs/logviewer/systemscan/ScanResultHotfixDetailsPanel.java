/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntry;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;

public class ScanResultHotfixDetailsPanel extends JPanel {

	private static final long serialVersionUID = -3435154792009295016L;

	private ScanResultHotfixEntryKey scanResultHotfixEntryKey;

	private ScanResult scanResult;

	private JLabel idJLabel;

	private JLabel descriptionJLabel;

	private JLabel stateJLabel;

	private JLabel statusJLabel;

	private ScanResultHotfixTable scanResultHotfixTable;

	public ScanResultHotfixDetailsPanel(ScanResultHotfixEntryKey scanResultHotfixEntryKey, ScanResult scanResult) {
		super();
		this.scanResultHotfixEntryKey = scanResultHotfixEntryKey;
		this.scanResult = scanResult;

		setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(2, 2, 2, 2);

		JPanel headerPanel = getHeaderPanel();
		
		ScanResultHotfixTable scanResultHotfixTable = getScanResultHotfixTable();
		
		JScrollPane hotfixDetailsTableJScrollPane = new JScrollPane(scanResultHotfixTable);

		add(headerPanel, gbc1);
		add(hotfixDetailsTableJScrollPane, gbc2);
		
		populateHeaderData();
	}

	private JLabel getIdJLabel() {

		if (idJLabel == null) {
			idJLabel = new JLabel();
		}

		return idJLabel;
	}

	private JLabel getDescriptionJLabel() {

		if (descriptionJLabel == null) {
			descriptionJLabel = new JLabel();
		}

		return descriptionJLabel;
	}

	private JLabel getStateJLabel() {

		if (stateJLabel == null) {
			stateJLabel = new JLabel();
		}

		return stateJLabel;
	}

	private JLabel getStatusJLabel() {

		if (statusJLabel == null) {
			statusJLabel = new JLabel();
		}

		return statusJLabel;
	}

	private ScanResultHotfixTable getScanResultHotfixTable() {

		if (scanResultHotfixTable == null) {

			ScanResultHotfixTableModel scanResultHotfixTableModel = new ScanResultHotfixTableModel(
					scanResultHotfixEntryKey, scanResult);

			scanResultHotfixTable = new ScanResultHotfixTable(scanResultHotfixTableModel);
		}

		return scanResultHotfixTable;
	}

	private JPanel getHeaderPanel() {

		JPanel headerPanel = new JPanel();

		headerPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 0;
		gbc3.gridy = 1;
		gbc3.weightx = 0.0D;
		gbc3.weighty = 1.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 1;
		gbc4.gridy = 1;
		gbc4.weightx = 1.0D;
		gbc4.weighty = 1.0D;
		gbc4.fill = GridBagConstraints.BOTH;
		gbc4.anchor = GridBagConstraints.NORTHWEST;
		gbc4.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.gridx = 0;
		gbc5.gridy = 2;
		gbc5.weightx = 0.0D;
		gbc5.weighty = 1.0D;
		gbc5.fill = GridBagConstraints.BOTH;
		gbc5.anchor = GridBagConstraints.NORTHWEST;
		gbc5.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc6 = new GridBagConstraints();
		gbc6.gridx = 1;
		gbc6.gridy = 2;
		gbc6.weightx = 1.0D;
		gbc6.weighty = 1.0D;
		gbc6.fill = GridBagConstraints.BOTH;
		gbc6.anchor = GridBagConstraints.NORTHWEST;
		gbc6.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc7 = new GridBagConstraints();
		gbc7.gridx = 0;
		gbc7.gridy = 3;
		gbc7.weightx = 0.0D;
		gbc7.weighty = 1.0D;
		gbc7.fill = GridBagConstraints.BOTH;
		gbc7.anchor = GridBagConstraints.NORTHWEST;
		gbc7.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc8 = new GridBagConstraints();
		gbc8.gridx = 1;
		gbc8.gridy = 3;
		gbc8.weightx = 1.0D;
		gbc8.weighty = 1.0D;
		gbc8.fill = GridBagConstraints.BOTH;
		gbc8.anchor = GridBagConstraints.NORTHWEST;
		gbc8.insets = new Insets(2, 2, 2, 2);

		Font existingFont = getFont();
		String existingFontName = existingFont.getName();
		int existFontSize = existingFont.getSize();
		Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);

		JLabel idLabelJLabel = new JLabel("Hotfix ID: ");
		JLabel descriptionLabelJLabel = new JLabel("Hotfix Description: ");
		JLabel stateLabelJLabel = new JLabel("Hotfix State: ");
		JLabel statusLabelJLabel = new JLabel("Hotfix Status: ");

		idLabelJLabel.setFont(newFont);
		descriptionLabelJLabel.setFont(newFont);
		stateLabelJLabel.setFont(newFont);
		statusLabelJLabel.setFont(newFont);

		JLabel idJLabel = getIdJLabel();
		JLabel descriptionJLabel = getDescriptionJLabel();
		JLabel stateJLabel = getStateJLabel();
		JLabel statusJLabel = getStatusJLabel();

		headerPanel.add(idLabelJLabel, gbc1);
		headerPanel.add(idJLabel, gbc2);
		headerPanel.add(descriptionLabelJLabel, gbc3);
		headerPanel.add(descriptionJLabel, gbc4);
		headerPanel.add(stateLabelJLabel, gbc5);
		headerPanel.add(stateJLabel, gbc6);
		headerPanel.add(statusLabelJLabel, gbc7);
		headerPanel.add(statusJLabel, gbc8);

		return headerPanel;
	}

	private void populateHeaderData() {

		JLabel idJLabel = getIdJLabel();
		JLabel descriptionJLabel = getDescriptionJLabel();
		JLabel stateJLabel = getStateJLabel();
		JLabel statusJLabel = getStatusJLabel();
	
		String data =null;
		ScanResultHotfixEntry scanResultHotfixEntry = scanResult.getScanResultHotfixEntry(scanResultHotfixEntryKey);

		data = scanResult.getScanResultHotfixEntryData(scanResultHotfixEntry, SystemScanColumn.HOTFIX_ID);
		idJLabel.setText(data);
		
		data = scanResult.getScanResultHotfixEntryData(scanResultHotfixEntry, SystemScanColumn.HOTFIX_DESCRIPTION);
		descriptionJLabel.setText(data);
		
		data = scanResult.getScanResultHotfixEntryData(scanResultHotfixEntry, SystemScanColumn.PXHOTFIXSTATE);
		stateJLabel.setText(data);
		
		data = scanResult.getScanResultHotfixEntryData(scanResultHotfixEntry, SystemScanColumn.HOTFIX_STATUS);
		statusJLabel.setText(data);
	}
}
