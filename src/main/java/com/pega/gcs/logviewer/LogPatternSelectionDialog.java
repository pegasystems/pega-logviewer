/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer;

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
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.AbstractLogPattern.LogType;
import com.pega.gcs.logviewer.logfile.Log4jPattern;
import com.pega.gcs.logviewer.logfile.Log4jPatternManager;
import com.pega.gcs.logviewer.logfile.LogPatternFactory;
import com.pega.gcs.logviewer.parser.LogParser;

public class LogPatternSelectionDialog extends JDialog {

    private static final long serialVersionUID = 889900206663513463L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LogPatternSelectionDialog.class);

    private LogParser logParser;

    private List<String> readLineList;

    private Charset charset;

    private Locale locale;

    private ZoneId displayZoneId;

    private JTextField logPatternTextField;

    private JComboBox<String> log4jPatternComboBox;

    public LogPatternSelectionDialog(List<String> readLineList, Charset charset, Locale locale, ZoneId displayZoneId,
            ImageIcon appIcon, Component parent) {

        super();

        this.readLineList = readLineList;
        this.charset = charset;
        this.locale = locale;
        this.displayZoneId = displayZoneId;

        this.logParser = null;

        setIconImage(appIcon.getImage());

        setPreferredSize(new Dimension(1000, 600));

        setTitle("Select Log Pattern");
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainPanel());

        pack();

        setLocationRelativeTo(parent);

        // setVisible called by caller.
        // setVisible(true);

    }

    public LogParser getLogParser() {
        return logParser;
    }

    protected void setLogParser(LogParser logParser) {
        this.logParser = logParser;
    }

    protected List<String> getReadLineList() {
        return readLineList;
    }

    private JPanel getMainPanel() {

        JPanel mainPanel = new JPanel();

        LayoutManager layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(layout);

        JPanel logPatternPanel = getLogPatternPanel();
        JPanel buttonsPanel = getButtonsPanel();

        mainPanel.add(logPatternPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(4, 2)));
        mainPanel.add(buttonsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(4, 4)));
        // mainJPanel.add(Box.createHorizontalGlue());

        return mainPanel;
    }

    private JPanel getLogPatternPanel() {

        JPanel logPatternPanel = new JPanel();

        logPatternPanel.setLayout(new GridBagLayout());

        // name
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

        // // pattern type
        // GridBagConstraints gbc3 = new GridBagConstraints();
        // gbc3.gridx = 0;
        // gbc3.gridy = 1;
        // gbc3.weightx = 0.0D;
        // gbc3.weighty = 0.0D;
        // gbc3.fill = GridBagConstraints.BOTH;
        // gbc3.anchor = GridBagConstraints.NORTHWEST;
        // gbc3.insets = new Insets(5, 10, 3, 5);
        //
        // GridBagConstraints gbc4 = new GridBagConstraints();
        // gbc4.gridx = 1;
        // gbc4.gridy = 1;
        // gbc4.weightx = 1.0D;
        // gbc4.weighty = 0.0D;
        // gbc4.fill = GridBagConstraints.BOTH;
        // gbc4.anchor = GridBagConstraints.NORTHWEST;
        // gbc4.insets = new Insets(5, 5, 3, 10);

        // pattern string
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 1;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 10, 3, 5);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 1;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 5, 3, 10);

        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 1;
        gbc7.gridy = 2;
        gbc7.weightx = 1.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(5, 5, 3, 10);
        gbc7.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(3, 5, 3, 10);
        gbc8.gridwidth = GridBagConstraints.REMAINDER;

        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 0;
        gbc9.gridy = 4;
        gbc9.weightx = 1.0D;
        gbc9.weighty = 1.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(5, 5, 3, 10);
        gbc9.gridwidth = GridBagConstraints.REMAINDER;

        JLabel logPatternNameLabel = new JLabel("Name");
        JLabel logPatternStrLabel = new JLabel("Log4j Pattern");

        JLabel nameInfoLabel = new JLabel("Enter any name.");
        nameInfoLabel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHTEST_GRAY, 1));
        nameInfoLabel.setPreferredSize(new Dimension(30, 30));

        StringBuilder infoTextSB = new StringBuilder();

        infoTextSB.append("<html>");
        infoTextSB.append("<p>Enter ConversionPattern value of PEGA appender from the prlogging.xml, ");
        infoTextSB.append("that generated this log file. For ex. copy the red text</p>");
        infoTextSB.append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&lt;appender name=&quot;PEGA&quot; ");
        infoTextSB.append("class=&quot;com.pega.pegarules.priv.util.FileAppenderPega&quot;&gt;</p>");
        infoTextSB.append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param ");
        infoTextSB.append("name=&quot;FileNamePattern&quot; value=&quot;'PegaRULES-'yyyy-MMM-dd'.log'&quot;/&gt;</p>");
        infoTextSB.append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;");
        infoTextSB.append("layout class=&quot;com.pega.apache.log4j.PatternLayout&quot;&gt;</p>");
        infoTextSB.append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        infoTextSB.append("&nbsp;&lt;param name=&quot;ConversionPattern&quot; value=&quot;<font color=\"red\">");
        infoTextSB.append("%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n</font>&quot;/&gt;</p>");
        infoTextSB
                .append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/layout&gt;</p>");
        infoTextSB.append("<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&lt;/appender&gt;</p>");
        infoTextSB.append("</html>");

        JLabel patternInfoLabel = new JLabel(infoTextSB.toString());
        patternInfoLabel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHTEST_GRAY, 1));
        patternInfoLabel.setPreferredSize(new Dimension(30, 30));

        JTextField logPatternTextField = getLogPatternTextField();
        JComboBox<String> log4jPatternComboBox = getLog4jPatternComboBox();
        JComponent readLineListComponent = getReadLineListComponent();

        logPatternPanel.add(logPatternNameLabel, gbc1);
        logPatternPanel.add(logPatternTextField, gbc2);
        logPatternPanel.add(logPatternStrLabel, gbc5);
        logPatternPanel.add(log4jPatternComboBox, gbc6);
        logPatternPanel.add(nameInfoLabel, gbc7);
        logPatternPanel.add(patternInfoLabel, gbc8);
        logPatternPanel.add(readLineListComponent, gbc9);

        return logPatternPanel;
    }

    protected JTextField getLogPatternTextField() {

        if (logPatternTextField == null) {

            logPatternTextField = new JTextField();
        }

        return logPatternTextField;
    }

    protected JComboBox<String> getLog4jPatternComboBox() {

        if (log4jPatternComboBox == null) {

            Log4jPatternManager log4jPatternManager = Log4jPatternManager.getInstance();
            Set<Log4jPattern> pegaRulesLog4jPatternSet = log4jPatternManager.getDefaultRulesLog4jPatternSet();

            String[] patternStrArray = new String[pegaRulesLog4jPatternSet.size()];

            int index = 0;

            Iterator<Log4jPattern> logpatternIt = pegaRulesLog4jPatternSet.iterator();

            while (logpatternIt.hasNext()) {

                Log4jPattern log4jPattern = logpatternIt.next();
                patternStrArray[index] = log4jPattern.getPatternString();

                index++;
            }

            log4jPatternComboBox = new JComboBox<String>(patternStrArray);

            log4jPatternComboBox.setEditable(true);

            log4jPatternComboBox.setPreferredSize(new Dimension(30, 30));

        }

        return log4jPatternComboBox;
    }

    private JComponent getReadLineListComponent() {

        JTextArea readLineListTextArea = new JTextArea();
        readLineListTextArea.setEditable(false);
        readLineListTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        StringBuilder textSB = new StringBuilder();

        for (String line : readLineList) {
            textSB.append(line);
            textSB.append(System.getProperty("line.separator"));
        }

        readLineListTextArea.setText(textSB.toString());

        JScrollPane readLineListComponent = new JScrollPane(readLineListTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        readLineListComponent.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Log excerpt"));

        return readLineListComponent;
    }

    private JPanel getButtonsPanel() {

        JPanel buttonsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(buttonsPanel, BoxLayout.X_AXIS);
        buttonsPanel.setLayout(layout);

        // OK Button
        JButton okButton = new JButton("OK");
        okButton.setToolTipText("OK");

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {

                try {

                    JTextField logPatternTextField = getLogPatternTextField();
                    JComboBox<String> log4jPatternComboBox = getLog4jPatternComboBox();

                    String logPatternName = logPatternTextField.getText();
                    String logPatternStr = (String) log4jPatternComboBox.getSelectedItem();

                    if ((logPatternName != null) && (!"".equals(logPatternName)) && (logPatternStr != null)
                            && (!"".equals(logPatternStr))) {

                        LogPatternFactory logPatternFactory = LogPatternFactory.getInstance();

                        Log4jPattern log4jPattern = logPatternFactory.getLog4jPattern(LogType.PEGA_RULES,
                                logPatternName, logPatternStr, false);

                        Set<Log4jPattern> log4jPatternSet = new TreeSet<>();
                        log4jPatternSet.add(log4jPattern);

                        LogParser logParser = LogParser.getLog4jParser(getReadLineList(), log4jPatternSet, charset,
                                locale, displayZoneId);

                        if (logParser != null) {
                            setLogParser(logParser);
                            dispose();
                        }
                    }

                } catch (Exception e) {
                    LOG.error("Error getting log parser.", e);
                }
            }
        });

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Cancel");

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                setLogParser(null);
                dispose();
            }
        });

        Dimension dim = new Dimension(20, 30);
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(okButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(Box.createRigidArea(dim));
        buttonsPanel.add(Box.createHorizontalGlue());

        buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return buttonsPanel;
    }
}
