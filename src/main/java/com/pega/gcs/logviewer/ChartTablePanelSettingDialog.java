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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.AutoCompleteJComboBox;
import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;

public class ChartTablePanelSettingDialog extends JDialog {

	private static final long serialVersionUID = -4890020854049502839L;

	private String charset;

	private Locale fileLocale;

	private TimeZone timezone;

	private boolean settingUpdated;

	private AutoCompleteJComboBox<String> charsetJComboBox;

	private AutoCompleteJComboBox<Locale> fileLocaleJComboBox;

	private AutoCompleteJComboBox<String> timeZoneJComboBox;

	public ChartTablePanelSettingDialog(String charset, Locale fileLocale, TimeZone timezone, ImageIcon appIcon,
			Component parent) {

		super();

		this.charset = charset;
		this.fileLocale = fileLocale;
		this.timezone = timezone;

		this.settingUpdated = false;

		setIconImage(appIcon.getImage());

		// setPreferredSize(new Dimension(350, 175));
		setTitle("Log file Settings");
		// setResizable(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// setAlwaysOnTop(true);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

		populateSettingsJPanel();

		// setVisible called by caller.
		// setVisible(true);

	}

	protected String getCharset() {
		return charset;
	}

	protected Locale getFileLocale() {
		return fileLocale;
	}

	protected TimeZone getTimezone() {
		return timezone;
	}

	protected void setSettingUpdated(boolean aSettingUpdated) {
		settingUpdated = aSettingUpdated;
	}

	/**
	 * @return the settingUpdated
	 */
	public boolean isSettingUpdated() {
		return settingUpdated;
	}

	/**
	 * @return the charsetJComboBox
	 */
	public AutoCompleteJComboBox<String> getCharsetJComboBox() {

		if (charsetJComboBox == null) {
			charsetJComboBox = GUIUtilities.getCharsetJComboBox();
		}

		return charsetJComboBox;
	}

	/**
	 * @return the fileLocaleJComboBox
	 */
	public AutoCompleteJComboBox<Locale> getFileLocaleJComboBox() {

		if (fileLocaleJComboBox == null) {
			fileLocaleJComboBox = GUIUtilities.getFileLocaleJComboBox();
		}

		return fileLocaleJComboBox;
	}

	/**
	 * @return the timeZoneJComboBox
	 */
	public AutoCompleteJComboBox<String> getTimeZoneJComboBox() {

		if (timeZoneJComboBox == null) {
			timeZoneJComboBox = GUIUtilities.getTimeZoneJComboBox();
		}

		return timeZoneJComboBox;
	}

	private JPanel getMainJPanel() {

		JPanel mainJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(mainJPanel, BoxLayout.Y_AXIS);
		mainJPanel.setLayout(layout);

		JPanel settingsJPanel = getSettingsJPanel();
		JPanel buttonsJPanel = getButtonsJPanel();

		mainJPanel.add(settingsJPanel);
		mainJPanel.add(Box.createRigidArea(new Dimension(4, 2)));
		mainJPanel.add(buttonsJPanel);
		mainJPanel.add(Box.createRigidArea(new Dimension(4, 4)));
		// mainJPanel.add(Box.createHorizontalGlue());

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

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 0;
		gbc3.gridy = 1;
		gbc3.weightx = 1.0D;
		gbc3.weighty = 0.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 1;
		gbc4.gridy = 1;
		gbc4.weightx = 1.0D;
		gbc4.weighty = 0.0D;
		gbc4.fill = GridBagConstraints.BOTH;
		gbc4.anchor = GridBagConstraints.NORTHWEST;
		gbc4.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.gridx = 0;
		gbc5.gridy = 2;
		gbc5.weightx = 1.0D;
		gbc5.weighty = 0.0D;
		gbc5.fill = GridBagConstraints.BOTH;
		gbc5.anchor = GridBagConstraints.NORTHWEST;
		gbc5.insets = new Insets(2, 2, 2, 2);

		GridBagConstraints gbc6 = new GridBagConstraints();
		gbc6.gridx = 1;
		gbc6.gridy = 2;
		gbc6.weightx = 1.0D;
		gbc6.weighty = 0.0D;
		gbc6.fill = GridBagConstraints.BOTH;
		gbc6.anchor = GridBagConstraints.NORTHWEST;
		gbc6.insets = new Insets(2, 2, 2, 2);

		JLabel charsetJLabel = new JLabel("File Encoding");
		JLabel localeJLabel = new JLabel("Locale");
		JLabel timeZoneJLabel = new JLabel("Time Zone");

		AutoCompleteJComboBox<String> charsetJComboBox = getCharsetJComboBox();
		AutoCompleteJComboBox<Locale> fileLocaleJComboBox = getFileLocaleJComboBox();
		AutoCompleteJComboBox<String> timeZoneJComboBox = getTimeZoneJComboBox();

		settingsJPanel.add(charsetJLabel, gbc1);
		settingsJPanel.add(charsetJComboBox, gbc2);
		settingsJPanel.add(localeJLabel, gbc3);
		settingsJPanel.add(fileLocaleJComboBox, gbc4);
		settingsJPanel.add(timeZoneJLabel, gbc5);
		settingsJPanel.add(timeZoneJComboBox, gbc6);

		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		settingsJPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Settings"));

		return settingsJPanel;
	}

	private JPanel getButtonsJPanel() {

		JPanel buttonsJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(buttonsJPanel, BoxLayout.X_AXIS);
		buttonsJPanel.setLayout(layout);

		// OK Button
		JButton okJButton = new JButton("OK");
		okJButton.setToolTipText("OK");

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

		// Reset button
		JButton resetJButton = new JButton("Reset");
		resetJButton.setToolTipText("Reset");

		resetJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {

				AutoCompleteJComboBox<String> charsetJComboBox = getCharsetJComboBox();
				AutoCompleteJComboBox<Locale> localeJComboBox = getFileLocaleJComboBox();
				AutoCompleteJComboBox<String> timeZoneJComboBox = getTimeZoneJComboBox();

				charsetJComboBox.setSelectedItem(getCharset());
				localeJComboBox.setSelectedItem(getFileLocale());
				timeZoneJComboBox.setSelectedItem(getTimezone().getID());
			}
		});

		Dimension dim = new Dimension(20, 30);
		buttonsJPanel.add(Box.createHorizontalGlue());
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(okJButton);
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(cancelJButton);
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(resetJButton);
		buttonsJPanel.add(Box.createRigidArea(dim));
		buttonsJPanel.add(Box.createHorizontalGlue());

		buttonsJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		return buttonsJPanel;
	}

	private void populateSettingsJPanel() {

		AutoCompleteJComboBox<String> charsetJComboBox = getCharsetJComboBox();
		AutoCompleteJComboBox<Locale> localeJComboBox = getFileLocaleJComboBox();
		AutoCompleteJComboBox<String> timeZoneJComboBox = getTimeZoneJComboBox();

		charsetJComboBox.setSelectedItem(charset);
		localeJComboBox.setSelectedItem(fileLocale);
		timeZoneJComboBox.setSelectedItem(timezone.getID());

	}

	public String getSelectedCharset() {
		AutoCompleteJComboBox<String> charsetJComboBox = getCharsetJComboBox();
		String charset = (String) charsetJComboBox.getSelectedItem();

		if ((charset == null) || ("".equals(charset))) {
			charset = this.charset;
		}

		return charset;
	}

	public Locale getSelectedLocale() {
		AutoCompleteJComboBox<Locale> localeJComboBox = getFileLocaleJComboBox();

		Locale locale = (Locale) localeJComboBox.getSelectedItem();

		if (locale == null) {
			locale = this.fileLocale;
		}

		return locale;
	}

	public TimeZone getSelectedTimeZone() {

		AutoCompleteJComboBox<String> timeZoneJComboBox = getTimeZoneJComboBox();

		String tzId = (String) timeZoneJComboBox.getSelectedItem();

		TimeZone timeZone = null;

		if ((tzId == null) || ("".equals(tzId))) {
			timeZone = this.timezone;
		} else {
			timeZone = TimeZone.getTimeZone(tzId);
		}

		return timeZone;
	}
}
