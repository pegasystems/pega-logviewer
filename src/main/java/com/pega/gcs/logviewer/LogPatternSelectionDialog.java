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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

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
import com.pega.gcs.logviewer.logfile.LogPattern;
import com.pega.gcs.logviewer.parser.LogParser;

public class LogPatternSelectionDialog extends JDialog {

	private static final long serialVersionUID = 889900206663513463L;

	private static final Log4j2Helper LOG = new Log4j2Helper(LogPatternSelectionDialog.class);

	private LogParser logParser;

	private List<String> readLineList;

	private Set<LogPattern> logPatternSet;

	private Locale fileLocale;

	private TimeZone displayTimezone;

	private JTextField logPatternJTextField;

	private JComboBox<String> log4jPatternJComboBox;

	public LogPatternSelectionDialog(List<String> readLineList, Set<LogPattern> logPatternSet, Locale fileLocale,
			TimeZone displayTimezone, ImageIcon appIcon, Component parent) {

		super();

		this.readLineList = readLineList;
		this.logPatternSet = logPatternSet;
		this.fileLocale = fileLocale;
		this.displayTimezone = displayTimezone;

		this.logParser = null;

		setIconImage(appIcon.getImage());

		setPreferredSize(new Dimension(1000, 600));

		setTitle("Select Log Pattern");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		setContentPane(getMainJPanel());

		pack();

		setLocationRelativeTo(parent);

		// setVisible called by caller.
		// setVisible(true);

	}

	public LogParser getLogParser() {
		return logParser;
	}

	protected void setLogParser(LogParser aLogParser) {
		logParser = aLogParser;
	}

	protected Locale getFileLocale() {
		return fileLocale;
	}

	protected TimeZone getDisplayTimezone() {
		return displayTimezone;
	}

	protected List<String> getReadLineList() {
		return readLineList;
	}

	private JPanel getMainJPanel() {

		JPanel mainJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(mainJPanel, BoxLayout.Y_AXIS);
		mainJPanel.setLayout(layout);

		JPanel logPatternJPanel = getLogPatternJPanel();
		JPanel buttonsJPanel = getButtonsJPanel();

		mainJPanel.add(logPatternJPanel);
		mainJPanel.add(Box.createRigidArea(new Dimension(4, 2)));
		mainJPanel.add(buttonsJPanel);
		mainJPanel.add(Box.createRigidArea(new Dimension(4, 4)));
		// mainJPanel.add(Box.createHorizontalGlue());

		return mainJPanel;
	}

	private JPanel getLogPatternJPanel() {

		JPanel logPatternJPanel = new JPanel();

		logPatternJPanel.setLayout(new GridBagLayout());

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

		JLabel logPatternNameJLabel = new JLabel("Name");
		JLabel logPatternStrJLabel = new JLabel("Log4j Pattern");

		JLabel nameInfoJLabel = new JLabel("Enter any name.");
		nameInfoJLabel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHTEST_GRAY, 1));
		nameInfoJLabel.setPreferredSize(new Dimension(30, 30));

		String patternInfoText = "<html>";

		patternInfoText += "<p>Enter ConversionPattern value of PEGA appender from the prlogging.xml, that generated this log file. For ex. copy the red text</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&lt;appender name=&quot;PEGA&quot; class=&quot;com.pega.pegarules.priv.util.FileAppenderPega&quot;&gt;</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param name=&quot;FileNamePattern&quot; value=&quot;'PegaRULES-'yyyy-MMM-dd'.log'&quot;/&gt;</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;layout class=&quot;com.pega.apache.log4j.PatternLayout&quot;&gt;</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;param name=&quot;ConversionPattern&quot; value=&quot;<font color=\"red\">%d{ABSOLUTE} [%20.20t] (%30.30c{3}) %-5p %X{stack} %X{userid} - %m%n</font>&quot;/&gt;</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/layout&gt;</p>";
		patternInfoText += "<p style=\"color:blue\">&nbsp;&nbsp;&nbsp;&nbsp;&lt;/appender&gt;</p>";
		patternInfoText += "</html>";

		JLabel patternInfoJLabel = new JLabel(patternInfoText);
		patternInfoJLabel.setBorder(BorderFactory.createLineBorder(MyColor.LIGHTEST_GRAY, 1));
		patternInfoJLabel.setPreferredSize(new Dimension(30, 30));

		JTextField logPatternJTextField = getLogPatternJTextField();
		JComboBox<String> log4jPatternJComboBox = getLog4jPatternJComboBox();
		JComponent readLineListJComponent = getReadLineListJComponent();

		logPatternJPanel.add(logPatternNameJLabel, gbc1);
		logPatternJPanel.add(logPatternJTextField, gbc2);
		logPatternJPanel.add(logPatternStrJLabel, gbc5);
		logPatternJPanel.add(log4jPatternJComboBox, gbc6);
		logPatternJPanel.add(nameInfoJLabel, gbc7);
		logPatternJPanel.add(patternInfoJLabel, gbc8);
		logPatternJPanel.add(readLineListJComponent, gbc9);

		return logPatternJPanel;
	}

	/**
	 * @return the logPatternJTextField
	 */
	protected JTextField getLogPatternJTextField() {

		if (logPatternJTextField == null) {

			logPatternJTextField = new JTextField();
		}

		return logPatternJTextField;
	}

	protected JComboBox<String> getLog4jPatternJComboBox() {

		if (log4jPatternJComboBox == null) {

			String patternStrArray[] = new String[logPatternSet.size()];

			int index = 0;

			Iterator<LogPattern> logpatternIt = logPatternSet.iterator();

			while (logpatternIt.hasNext()) {

				LogPattern logPattern = logpatternIt.next();
				patternStrArray[index] = logPattern.getLogPatternString();

				index++;
			}

			log4jPatternJComboBox = new JComboBox<String>(patternStrArray);

			log4jPatternJComboBox.setEditable(true);

			log4jPatternJComboBox.setPreferredSize(new Dimension(30, 30));

		}

		return log4jPatternJComboBox;
	}

	private JComponent getReadLineListJComponent() {

		JTextArea readLineListJTextArea = new JTextArea();
		readLineListJTextArea.setEditable(false);

		StringBuffer textSB = new StringBuffer();

		for (String line : readLineList) {
			textSB.append(line);
			textSB.append(System.getProperty("line.separator"));
		}

		readLineListJTextArea.setText(textSB.toString());

		JScrollPane readLineListJComponent = new JScrollPane(readLineListJTextArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

		readLineListJComponent.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Log excerpt"));

		return readLineListJComponent;
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

				try {

					JTextField logPatternJTextField = getLogPatternJTextField();
					JComboBox<String> log4jPatternJComboBox = getLog4jPatternJComboBox();

					String logPatternName = logPatternJTextField.getText();
					String logPatternStr = (String) log4jPatternJComboBox.getSelectedItem();

					if ((logPatternName != null) && (!"".equals(logPatternName)) && (logPatternStr != null)
							&& (!"".equals(logPatternStr))) {

						LogPattern logPattern = new LogPattern(logPatternName, logPatternStr);

						LogParser logParser = LogParser.getLogParser(getReadLineList(), logPattern, getFileLocale(),
								getDisplayTimezone());

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
		JButton cancelJButton = new JButton("Cancel");
		cancelJButton.setToolTipText("Cancel");

		cancelJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setLogParser(null);
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
