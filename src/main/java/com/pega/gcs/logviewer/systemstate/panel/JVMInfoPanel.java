
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.model.JVMInfo;

public class JVMInfoPanel extends JPanel {

    private static final long serialVersionUID = -4581665179104588943L;

    public JVMInfoPanel(JVMInfo jvmInfo) {

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
        gbc9.weighty = 1.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.gridx = 1;
        gbc10.gridy = 4;
        gbc10.weightx = 1.0D;
        gbc10.weighty = 1.0D;
        gbc10.fill = GridBagConstraints.BOTH;
        gbc10.anchor = GridBagConstraints.NORTHWEST;
        gbc10.insets = new Insets(2, 0, 2, 0);

        JLabel vmNameLabel = new JLabel("VM Name");
        JLabel vmVendorLabel = new JLabel("VM Vendor");
        JLabel vmVersionLabel = new JLabel("VM Version");
        JLabel tempDirLabel = new JLabel("Temp Dir");
        JLabel vmArgumentsLabel = new JLabel("VM Arguments");

        JTextField vmNameTextField = GUIUtilities.getValueTextField(jvmInfo.getVmName());
        JTextField vmVendorTextField = GUIUtilities.getValueTextField(jvmInfo.getVmVendor());
        JTextField vmVersionTextField = GUIUtilities.getValueTextField(jvmInfo.getVmVersion());
        JTextField tempDirTextField = GUIUtilities.getValueTextField(jvmInfo.getTempDir());
        JScrollPane vmArgumentsPane = getVMArgumentsPane(jvmInfo.getVmArguments());

        add(vmNameLabel, gbc1);
        add(vmNameTextField, gbc2);
        add(vmVendorLabel, gbc3);
        add(vmVendorTextField, gbc4);
        add(vmVersionLabel, gbc5);
        add(vmVersionTextField, gbc6);
        add(tempDirLabel, gbc7);
        add(tempDirTextField, gbc8);
        add(vmArgumentsLabel, gbc9);
        add(vmArgumentsPane, gbc10);

    }

    private JScrollPane getVMArgumentsPane(List<String> vmArguments) {

        StringBuilder vmArgumentsSb = new StringBuilder();

        if (vmArguments != null) {
            for (String vmArg : vmArguments) {
                vmArgumentsSb.append(vmArg);
                vmArgumentsSb.append(System.getProperty("line.separator"));
            }
        }

        JTextArea vmArgumentsContentArea = new JTextArea();
        vmArgumentsContentArea.setText(vmArgumentsSb.toString());
        vmArgumentsContentArea.setCaretPosition(0);
        vmArgumentsContentArea.setEditable(false);
        vmArgumentsContentArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        JScrollPane scrollPane = new JScrollPane(vmArgumentsContentArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(vmArgumentsContentArea.getPreferredSize());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }
}
