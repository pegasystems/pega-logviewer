/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class CatalogDownloadDialog extends JDialog {

	private static final long serialVersionUID = -1188188227251890406L;

	private boolean settingUpdated;

	private JCheckBox rescanInventoryCheckBox;

	public CatalogDownloadDialog(ImageIcon appIcon, Component parent) {

		super();

		this.settingUpdated = false;

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(300, 120));
		setTitle("Download Latest Catalog file");
		// setResizable(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// setAlwaysOnTop(true);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

	}

	private void setSettingUpdated(boolean settingUpdated) {
		this.settingUpdated = settingUpdated;
	}

	public boolean isSettingUpdated() {
		return settingUpdated;
	}

	public boolean isRescanInventory() {

		JCheckBox rescanInventoryCheckBox = getRescanInventoryCheckBox();

		boolean rescanInventory = rescanInventoryCheckBox.isSelected();

		return rescanInventory;
	}

	private JCheckBox getRescanInventoryCheckBox() {

		if (rescanInventoryCheckBox == null) {

			rescanInventoryCheckBox = new JCheckBox();

			rescanInventoryCheckBox.setSelected(true);
		}

		return rescanInventoryCheckBox;
	}

	private JPanel getMainJPanel() {

		JPanel mainJPanel = new JPanel();

		mainJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(2, 2, 0, 2);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 4, 2, 4);

		JPanel settingsJPanel = getSettingsJPanel();
		JPanel buttonsJPanel = getButtonsJPanel();

		mainJPanel.add(settingsJPanel, gbc1);
		mainJPanel.add(buttonsJPanel, gbc2);

		return mainJPanel;
	}

	private JPanel getSettingsJPanel() {

		JPanel settingsJPanel = new JPanel();
		settingsJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 0.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(2, 2, 2, 2);

		JLabel rescanInventoryJLabel = new JLabel("Rescan Inventory after download");

		JCheckBox rescanInventoryCheckBox = getRescanInventoryCheckBox();

		settingsJPanel.add(rescanInventoryJLabel, gbc1);
		settingsJPanel.add(rescanInventoryCheckBox, gbc2);

		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		settingsJPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Settings"));

		return settingsJPanel;
	}

	private JPanel getButtonsJPanel() {

		JPanel buttonsJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(buttonsJPanel, BoxLayout.X_AXIS);
		buttonsJPanel.setLayout(layout);

		// OK Button
		JButton okJButton = new JButton("Download");
		okJButton.setToolTipText("Download");

		okJButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				setSettingUpdated(true);
				dispose();

			}
		});

		// Cancel button
		JButton cancelJButton = new JButton("Cancel");
		cancelJButton.setToolTipText("Cancel");

		cancelJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		Dimension dim = new Dimension(20, 30);
		buttonsJPanel.add(Box.createHorizontalGlue());
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(okJButton);
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(cancelJButton);
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(Box.createHorizontalGlue());

		buttonsJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		return buttonsJPanel;
	}
}
