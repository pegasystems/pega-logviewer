
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.logviewer.systemstate.model.PRLogging;

public class PRLoggingPanel extends JPanel {

    private static final long serialVersionUID = -4581665179104588943L;

    public PRLoggingPanel(PRLogging prLogging) {

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
        gbc5.weighty = 1.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(2, 5, 2, 5);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 1.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(2, 0, 2, 0);

        JLabel fileNameLabel = new JLabel("File Name");
        JLabel filePathLabel = new JLabel("File Path");
        JLabel contentLabel = new JLabel("Content");

        JTextField fileNameTextField = GUIUtilities.getValueTextField(prLogging.getFileName());
        JTextField filePathTextField = GUIUtilities.getValueTextField(prLogging.getFilePath());
        JScrollPane contentPane = getContentPane(prLogging.getContent());

        add(fileNameLabel, gbc1);
        add(fileNameTextField, gbc2);
        add(filePathLabel, gbc3);
        add(filePathTextField, gbc4);
        add(contentLabel, gbc5);
        add(contentPane, gbc6);

    }

    private JScrollPane getContentPane(String xmlContent) {

        JTextArea contentArea = new JTextArea();
        contentArea.setText(xmlContent);
        contentArea.setCaretPosition(0);
        contentArea.setEditable(false);
        contentArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        JScrollPane scrollPane = new JScrollPane(contentArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setPreferredSize(contentArea.getPreferredSize());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

}
