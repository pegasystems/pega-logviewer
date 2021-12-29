
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.model.OSInfo;

public class OSInfoPanel extends JPanel {

    private static final long serialVersionUID = -4581665179104588943L;

    public OSInfoPanel(OSInfo osInfo) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(8, 5, 2, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(8, 0, 2, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 3;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 0;
        gbc9.gridy = 4;
        gbc9.weightx = 0.0D;
        gbc9.weighty = 0.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.gridx = 1;
        gbc10.gridy = 4;
        gbc10.weightx = 1.0D;
        gbc10.weighty = 0.0D;
        gbc10.fill = GridBagConstraints.BOTH;
        gbc10.anchor = GridBagConstraints.NORTHWEST;
        gbc10.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc11 = new GridBagConstraints();
        gbc11.gridx = 0;
        gbc11.gridy = 5;
        gbc11.weightx = 0.0D;
        gbc11.weighty = 0.0D;
        gbc11.fill = GridBagConstraints.BOTH;
        gbc11.anchor = GridBagConstraints.NORTHWEST;
        gbc11.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc12 = new GridBagConstraints();
        gbc12.gridx = 1;
        gbc12.gridy = 5;
        gbc12.weightx = 1.0D;
        gbc12.weighty = 0.0D;
        gbc12.fill = GridBagConstraints.BOTH;
        gbc12.anchor = GridBagConstraints.NORTHWEST;
        gbc12.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc13 = new GridBagConstraints();
        gbc13.gridx = 0;
        gbc13.gridy = 6;
        gbc13.weightx = 0.0D;
        gbc13.weighty = 0.0D;
        gbc13.fill = GridBagConstraints.BOTH;
        gbc13.anchor = GridBagConstraints.NORTHWEST;
        gbc13.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc14 = new GridBagConstraints();
        gbc14.gridx = 1;
        gbc14.gridy = 6;
        gbc14.weightx = 1.0D;
        gbc14.weighty = 0.0D;
        gbc14.fill = GridBagConstraints.BOTH;
        gbc14.anchor = GridBagConstraints.NORTHWEST;
        gbc14.insets = new Insets(2, 0, 2, 0);

        GridBagConstraints gbc15 = new GridBagConstraints();
        gbc15.gridx = 0;
        gbc15.gridy = 7;
        gbc15.weightx = 0.0D;
        gbc15.weighty = 1.0D;
        gbc15.fill = GridBagConstraints.BOTH;
        gbc15.anchor = GridBagConstraints.NORTHWEST;
        gbc15.insets = new Insets(2, 5, 2, 5);
        gbc15.gridwidth = GridBagConstraints.REMAINDER;

        JLabel osArchitectureLabel = new JLabel("OS Architecture");
        JLabel osNameLabel = new JLabel("OS Name");
        JLabel osVersionLabel = new JLabel("OS Version");
        JLabel fileSeparatorLabel = new JLabel("File Separator");
        JLabel fileEncodingLabel = new JLabel("File Encoding");
        JLabel lineSeparatorLabel = new JLabel("Line Separator");
        JLabel pathSeparatorLabel = new JLabel("Path Separator");

        JTextField osArchitectureTextField = GUIUtilities.getValueTextField(osInfo.getOsArchitecture());
        JTextField osNameTextField = GUIUtilities.getValueTextField(osInfo.getOsName());
        JTextField osVersionTextField = GUIUtilities.getValueTextField(osInfo.getOsVersion());
        JTextField fileSeparatorTextField = GUIUtilities.getValueTextField(osInfo.getFileSeparator());
        JTextField fileEncodingTextField = GUIUtilities.getValueTextField(osInfo.getFileEncoding());
        JTextField lineSeparatorTextField = GUIUtilities.getValueTextField(osInfo.getLineSeparator());
        JTextField pathSeparatorTextField = GUIUtilities.getValueTextField(osInfo.getPathSeparator());

        JLabel filler = new JLabel();

        add(osArchitectureLabel, gbc1);
        add(osArchitectureTextField, gbc2);
        add(osNameLabel, gbc3);
        add(osNameTextField, gbc4);
        add(osVersionLabel, gbc5);
        add(osVersionTextField, gbc6);
        add(fileSeparatorLabel, gbc7);
        add(fileSeparatorTextField, gbc8);
        add(fileEncodingLabel, gbc9);
        add(fileEncodingTextField, gbc10);
        add(lineSeparatorLabel, gbc11);
        add(lineSeparatorTextField, gbc12);
        add(pathSeparatorLabel, gbc13);
        add(pathSeparatorTextField, gbc14);
        add(filler, gbc15);

    }

}
