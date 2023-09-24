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
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

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

    private String charsetName;

    private Locale fileLocale;

    private ZoneId zoneId;

    private boolean settingUpdated;

    private AutoCompleteJComboBox<String> charsetComboBox;

    private AutoCompleteJComboBox<Locale> fileLocaleComboBox;

    private AutoCompleteJComboBox<String> zoneIdComboBox;

    public ChartTablePanelSettingDialog(String charsetName, Locale fileLocale, ZoneId zoneId, ImageIcon appIcon,
            Component parent) {

        super();

        this.charsetName = charsetName;
        this.fileLocale = fileLocale;
        this.zoneId = zoneId;

        this.settingUpdated = false;

        setIconImage(appIcon.getImage());

        setTitle("Log file Settings");

        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainPanel());

        pack();

        setLocationRelativeTo(parent);

        populateSettingsJPanel();

        // visible should be the last step
        setVisible(true);

    }

    private String getCharsetName() {
        return charsetName;
    }

    private Locale getFileLocale() {
        return fileLocale;
    }

    private ZoneId getZoneId() {
        return zoneId;
    }

    public boolean isSettingUpdated() {
        return settingUpdated;
    }

    private void setSettingUpdated(boolean settingUpdated) {
        this.settingUpdated = settingUpdated;
    }

    public AutoCompleteJComboBox<String> getCharsetComboBox() {

        if (charsetComboBox == null) {
            charsetComboBox = GUIUtilities.getCharsetJComboBox();
        }

        return charsetComboBox;
    }

    public AutoCompleteJComboBox<Locale> getFileLocaleComboBox() {

        if (fileLocaleComboBox == null) {
            fileLocaleComboBox = GUIUtilities.getFileLocaleJComboBox();
        }

        return fileLocaleComboBox;
    }

    public AutoCompleteJComboBox<String> getZoneIdComboBox() {

        if (zoneIdComboBox == null) {

            Set<String> zoneIdSet = new TreeSet<>(ZoneId.getAvailableZoneIds());

            String[] timeZoneArray = zoneIdSet.toArray(new String[zoneIdSet.size()]);

            zoneIdComboBox = new AutoCompleteJComboBox<String>(timeZoneArray);

        }

        return zoneIdComboBox;
    }

    private JPanel getMainPanel() {

        JPanel mainPanel = new JPanel();

        LayoutManager layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(layout);

        JPanel settingsPanel = getSettingsPanel();
        JPanel buttonsPanel = getButtonsPanel();

        mainPanel.add(settingsPanel);
        mainPanel.add(buttonsPanel);

        return mainPanel;
    }

    private JPanel getSettingsPanel() {

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 10, 3, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 2, 3, 10);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(3, 10, 3, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(3, 2, 3, 10);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 1.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(3, 10, 10, 2);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(3, 2, 10, 10);

        JLabel charsetJLabel = new JLabel("File Encoding");
        JLabel localeJLabel = new JLabel("Locale");
        JLabel timeZoneJLabel = new JLabel("Time Zone");

        AutoCompleteJComboBox<String> charsetComboBox = getCharsetComboBox();
        AutoCompleteJComboBox<Locale> fileLocaleComboBox = getFileLocaleComboBox();
        AutoCompleteJComboBox<String> zoneIdComboBox = getZoneIdComboBox();

        settingsPanel.add(charsetJLabel, gbc1);
        settingsPanel.add(charsetComboBox, gbc2);
        settingsPanel.add(localeJLabel, gbc3);
        settingsPanel.add(fileLocaleComboBox, gbc4);
        settingsPanel.add(timeZoneJLabel, gbc5);
        settingsPanel.add(zoneIdComboBox, gbc6);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        settingsPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Settings"));

        return settingsPanel;
    }

    private JPanel getButtonsPanel() {

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

                AutoCompleteJComboBox<String> charsetComboBox = getCharsetComboBox();
                AutoCompleteJComboBox<Locale> localeComboBox = getFileLocaleComboBox();
                AutoCompleteJComboBox<String> zoneIdComboBox = getZoneIdComboBox();

                charsetComboBox.setSelectedItem(getCharsetName());
                localeComboBox.setSelectedItem(getFileLocale());
                zoneIdComboBox.setSelectedItem(getZoneId().getId());
            }
        });

        Dimension dim = new Dimension(20, 40);
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

        AutoCompleteJComboBox<String> charsetJComboBox = getCharsetComboBox();
        AutoCompleteJComboBox<Locale> localeJComboBox = getFileLocaleComboBox();
        AutoCompleteJComboBox<String> zoneIdComboBox = getZoneIdComboBox();

        charsetJComboBox.setSelectedItem(getCharsetName());
        localeJComboBox.setSelectedItem(getFileLocale());
        zoneIdComboBox.setSelectedItem(getZoneId().getId());

    }

    public String getSelectedCharsetName() {

        AutoCompleteJComboBox<String> charsetJComboBox = getCharsetComboBox();

        String selectedCharsetName = (String) charsetJComboBox.getSelectedItem();

        if ((selectedCharsetName == null) || ("".equals(selectedCharsetName))) {
            selectedCharsetName = this.charsetName;
        }

        return selectedCharsetName;
    }

    public Locale getSelectedLocale() {

        AutoCompleteJComboBox<Locale> localeJComboBox = getFileLocaleComboBox();

        Locale locale = (Locale) localeJComboBox.getSelectedItem();

        if (locale == null) {
            locale = this.fileLocale;
        }

        return locale;
    }

    public ZoneId getSelectedZoneId() {

        AutoCompleteJComboBox<String> zoneIdComboBox = getZoneIdComboBox();

        String zoneIdStr = (String) zoneIdComboBox.getSelectedItem();

        ZoneId zoneId = null;

        if ((zoneIdStr == null) || ("".equals(zoneIdStr))) {
            zoneId = this.zoneId;
        } else {
            zoneId = ZoneId.of(zoneIdStr);
        }

        return zoneId;
    }

}
