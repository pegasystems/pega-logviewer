
package com.pega.gcs.logviewer.socketreceiver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.IOUtils;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.fringecommon.utilities.GeneralUtilities;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern;
import com.pega.gcs.logviewer.logfile.Log4jPatternManager;
import com.pega.gcs.logviewer.logfile.LogPatternFactory;

public class SocketReceiverOpenDialog extends JDialog {

    private static final long serialVersionUID = 3821830525611636022L;

    private static final Log4j2Helper LOG = new Log4j2Helper(SocketReceiverOpenDialog.class);

    private int port;

    private AbstractLogPattern abstractLogPattern;

    private JComboBox<AppenderType> appenderTypeComboBox;

    private JTextField portTextField;

    private JTextArea activityJavaCodeTextArea;

    private JButton okButton;

    public SocketReceiverOpenDialog(ImageIcon appIcon, Component parent) {
        super();

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(600, 800));

        setTitle("Select Port and Log file type");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainJPanel());

        pack();

        setLocationRelativeTo(parent);

        // setVisible called by caller.
        // setVisible(true);
    }

    public int getPort() {
        return port;
    }

    public AbstractLogPattern getAbstractLogPattern() {
        return abstractLogPattern;
    }

    private JComboBox<AppenderType> getAppenderTypeComboBox() {

        if (appenderTypeComboBox == null) {

            appenderTypeComboBox = new JComboBox<>(AppenderType.values());

            appenderTypeComboBox.setEditable(false);

            appenderTypeComboBox.setPreferredSize(new Dimension(30, 30));

            appenderTypeComboBox.setSelectedIndex(-1);

            appenderTypeComboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    // set default value for the selection
                    JTextField portTextField = getPortTextField();

                    AppenderType appenderType = (AppenderType) appenderTypeComboBox.getSelectedItem();

                    if (appenderType != null) {
                        portTextField.setText(Integer.toString(appenderType.getDefaultPort()));
                    }

                    // clear data so as to regenerate
                    clearData();
                }
            });

        }

        return appenderTypeComboBox;
    }

    private JTextField getPortTextField() {

        if (portTextField == null) {

            portTextField = new JTextField("");

            portTextField.setPreferredSize(new Dimension(30, 30));

            portTextField.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    clearData();
                }

                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    clearData();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    clearData();
                }
            });
        }

        return portTextField;
    }

    private JTextArea getActivityJavaCodeTextArea() {

        if (activityJavaCodeTextArea == null) {

            activityJavaCodeTextArea = new JTextArea();

            activityJavaCodeTextArea.setEditable(false);
            activityJavaCodeTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }

        return activityJavaCodeTextArea;
    }

    private JButton getOkButton() {

        if (okButton == null) {

            okButton = new JButton("OK");

            okButton.setToolTipText("OK");

            okButton.setEnabled(false);

            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {

                    JTextField portTextField = getPortTextField();
                    JComboBox<AppenderType> appenderTypeComboBox = getAppenderTypeComboBox();

                    String portText = portTextField.getText();

                    if ((portText != null) && (!"".equals(portText))) {

                        try {
                            port = Integer.parseInt(portText);

                            AppenderType appenderType = (AppenderType) appenderTypeComboBox.getSelectedItem();

                            if ((port > 0) && (appenderType != null)) {

                                switch (appenderType) {

                                case PEGA_ALERT:
                                    LogPatternFactory logPatternFactory = LogPatternFactory.getInstance();
                                    abstractLogPattern = logPatternFactory.getAlertLogPattern();
                                    break;
                                case PEGA_RULES:

                                    Log4jPatternManager log4jPatternManager = Log4jPatternManager.getInstance();

                                    abstractLogPattern = log4jPatternManager.getSocketRecieverLog4jPattern();
                                    break;
                                default:
                                    abstractLogPattern = null;
                                    break;

                                }

                                dispose();
                            }
                        } catch (NumberFormatException nfe) {
                            LOG.error("Invalid port value specified: " + portText, nfe);
                        }
                    }
                }
            });
        }

        return okButton;
    }

    private JPanel getMainJPanel() {

        JPanel mainJPanel = new JPanel();

        mainJPanel.setLayout(new GridBagLayout());

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
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 2, 2, 2);

        JPanel dataPanel = getDataPanel();
        JPanel buttonsPanel = getButtonsPanel();
        JPanel activityJavaCodePanel = getActivityJavaCodePanel();

        mainJPanel.add(dataPanel, gbc1);
        mainJPanel.add(buttonsPanel, gbc2);
        mainJPanel.add(activityJavaCodePanel, gbc3);

        return mainJPanel;
    }

    private JPanel getDataPanel() {

        JPanel dataPanel = new JPanel();

        dataPanel.setLayout(new GridBagLayout());

        // Port
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 10, 3, 5);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 3, 10);

        // Log Type
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 10, 3, 5);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 3, 10);

        JLabel logTypeLabel = new JLabel("Log Appender Type");
        JLabel portLabel = new JLabel("Port");

        JComboBox<AppenderType> appenderTypeComboBox = getAppenderTypeComboBox();
        JTextField portTextField = getPortTextField();

        dataPanel.add(logTypeLabel, gbc1);
        dataPanel.add(appenderTypeComboBox, gbc2);
        dataPanel.add(portLabel, gbc3);
        dataPanel.add(portTextField, gbc4);

        dataPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return dataPanel;
    }

    private JPanel getButtonsPanel() {

        JPanel buttonsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
        buttonsPanel.setLayout(layout);

        // generate activity java code
        JButton generateButton = new JButton("Generate Activity Java Code");
        generateButton.setToolTipText("Generate Activity Java Code");

        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                JTextField portTextField = getPortTextField();
                JComboBox<AppenderType> appenderTypeComboBox = getAppenderTypeComboBox();

                String portText = portTextField.getText();

                if ((portText != null) && (!"".equals(portText))) {

                    String activityJavaCode = null;

                    try {

                        int replacementPort = Integer.parseInt(portText);

                        AppenderType appenderType = (AppenderType) appenderTypeComboBox.getSelectedItem();

                        if ((replacementPort > 0) && (appenderType != null)) {

                            activityJavaCode = getActivityJavaCode(appenderType, replacementPort);

                            JButton okButton = getOkButton();
                            okButton.setEnabled(true);
                        }
                    } catch (NumberFormatException nfe) {
                        activityJavaCode = null;
                        LOG.error("Invalid port value specified: " + portText, nfe);
                    }

                    updateSocketAppenderActivityTextArea(activityJavaCode);

                }
            }
        });

        // OK Button
        JButton okButton = getOkButton();

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Cancel");

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                port = 0;
                abstractLogPattern = null;
                dispose();
            }
        });

        Dimension dim = new Dimension(20, 30);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(generateButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(Box.createHorizontalGlue());

        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return buttonsPanel;
    }

    private JPanel getActivityJavaCodePanel() {

        JPanel activityJavaCodePanel = new JPanel();

        activityJavaCodePanel.setLayout(new BorderLayout());

        JTextArea activityJavaCodeTextArea = getActivityJavaCodeTextArea();

        JScrollPane scrollPane = new JScrollPane(activityJavaCodeTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        activityJavaCodePanel.add(scrollPane);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        activityJavaCodePanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Activity Java Code"));

        return activityJavaCodePanel;
    }

    private void clearData() {

        JButton okButton = getOkButton();
        okButton.setEnabled(false);

        updateSocketAppenderActivityTextArea(null);

    }

    private void updateSocketAppenderActivityTextArea(String activityJavaCode) {

        JTextArea socketAppenderActivityTextArea = getActivityJavaCodeTextArea();

        socketAppenderActivityTextArea.setText(activityJavaCode);
        socketAppenderActivityTextArea.setCaretPosition(0);
    }

    private String getActivityJavaCode(AppenderType appenderType, int replacementPort) {

        String activityJavaCode = null;

        int port = replacementPort > 0 ? replacementPort : appenderType.getDefaultPort();

        String hostAddress = GeneralUtilities.getHostAddress();
        String resourceName = appenderType.getResourceName();

        if (resourceName != null) {
            try (InputStream fileInputStream = FileUtilities.getResourceAsStreamFromUserDir(getClass(), resourceName)) {

                activityJavaCode = IOUtils.toString(fileInputStream, "UTF-8");

                if (hostAddress != null) {
                    activityJavaCode = activityJavaCode.replaceAll("HOST_NAME", hostAddress);
                    activityJavaCode = activityJavaCode.replaceAll("HOST_PORT", Integer.toString(port));
                }
            } catch (IOException e) {
                activityJavaCode = "Unable to read activity text resource";
            }
        } else {
            activityJavaCode = "Unknown appender";
        }

        return activityJavaCode;
    }
}
