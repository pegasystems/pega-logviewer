/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Searchable.SelectedRowPosition;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.fringecommon.utilities.KnuthMorrisPrattAlgorithm;

public class LogEntryPanel extends JPanel {

	private static final long serialVersionUID = 791827616015609624L;

	private static final Log4j2Helper LOG = new Log4j2Helper(LogEntryPanel.class);

	// 1 million characters limit
	private static final int MAX_LOG_TEXT_LENGTH = 1000000;

	private static final String WRAP_ON_ACTION = "Wrap Text On";

	private static final String WRAP_OFF_ACTION = "Wrap Text Off";

	// byte array for highlighting. can store original byte or upper case bytes
	private byte[] logEntryTextBytes;

	// store the original text
	private String logEntryText;

	private String specialMessage;

	private JTextField searchJTextField;

	private JLabel searchResultsJLabel;

	private JCheckBox caseSensitiveSearchJCheckBox;

	private JButton wrapLogEntryTextJButton;

	private JTextArea logEntryArea;

	private static boolean wrapOn;

	private Highlighter.HighlightPainter highlightPainter;

	private Integer totalSearchCount;

	private int searchNavPrevKey;

	private int searchNavKey;

	private HashMap<Integer, TextAreaSearchResult> searchResultsMap;

	private ImageIcon firstImageIcon;

	private ImageIcon lastImageIcon;

	private ImageIcon prevImageIcon;

	private ImageIcon nextImageIcon;

	private JButton searchPrevJButton;

	private JButton searchNextJButton;

	private JButton searchFirstJButton;

	private JButton searchLastJButton;

	public LogEntryPanel(String logEntryText) {

		super();

		assert (logEntryText != null);

		this.specialMessage = null;

		this.highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

		this.totalSearchCount = null;

		this.searchNavKey = 0;

		this.searchNavPrevKey = 0;

		this.searchResultsMap = new HashMap<Integer, TextAreaSearchResult>();

		this.firstImageIcon = FileUtilities.getImageIcon(this.getClass(), "first.png");

		this.lastImageIcon = FileUtilities.getImageIcon(this.getClass(), "last.png");

		this.prevImageIcon = FileUtilities.getImageIcon(this.getClass(), "prev.png");

		this.nextImageIcon = FileUtilities.getImageIcon(this.getClass(), "next.png");

		setLayout(new GridBagLayout());

		int gridy = 0;

		this.logEntryText = logEntryText;

		int logEntryTextLength = logEntryText.length();

		if (logEntryTextLength > MAX_LOG_TEXT_LENGTH) {

			NumberFormat nf = NumberFormat.getInstance();

			String leTextLengthStr = nf.format(logEntryTextLength);
			String maxTextLengthStr = nf.format(MAX_LOG_TEXT_LENGTH);

			this.logEntryText = logEntryText.substring(0, MAX_LOG_TEXT_LENGTH);

			this.specialMessage = String.format(
					"*** The content is truncated to %s characters. Original length was %s. ***", maxTextLengthStr,
					leTextLengthStr);

			GridBagConstraints gbc1 = new GridBagConstraints();
			gbc1.gridx = 0;
			gbc1.gridy = gridy++;
			gbc1.weightx = 1.0D;
			gbc1.weighty = 0.0D;
			gbc1.fill = GridBagConstraints.BOTH;
			gbc1.anchor = GridBagConstraints.NORTHWEST;
			gbc1.insets = new Insets(2, 2, 2, 2);

			JPanel specialMessageJPanel = getSpecialMessageJPanel();
			add(specialMessageJPanel, gbc1);
		}

		// default is case insensitive
		setLogEntryTextBytes(logEntryText.toUpperCase().getBytes());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = gridy++;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = gridy++;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;

		JPanel controlsJPanel = getControlsJPanel();
		JComponent textAreaJComponent = getTextAreaJComponent();

		add(controlsJPanel, gbc1);
		add(textAreaJComponent, gbc2);
	}

	protected byte[] getLogEntryTextBytes() {
		return logEntryTextBytes;
	}

	protected void setLogEntryTextBytes(byte[] aLogEntryTextBytes) {
		logEntryTextBytes = aLogEntryTextBytes;
	}

	protected String getLogEntryText() {
		return logEntryText;
	}

	/**
	 * @return the wrapOn
	 */
	public static boolean isWrapOn() {
		return wrapOn;
	}

	/**
	 * @param wrapOn
	 *            the wrapOn to set
	 */
	public static void setWrapOn(boolean wrapOn) {
		LogEntryPanel.wrapOn = wrapOn;
	}

	protected JTextField getSearchJTextField() {

		if (searchJTextField == null) {
			searchJTextField = new JTextField();
			searchJTextField.setEditable(true);

			Dimension dim = new Dimension(250, 22);
			searchJTextField.setPreferredSize(dim);
			searchJTextField.setMaximumSize(dim);
			searchJTextField.setFocusAccelerator('f');
			searchJTextField.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
					// do nothing
				}

				@Override
				public void keyReleased(KeyEvent e) {

					if (e.getSource() instanceof JTextField) {
						JTextField searchJTextField = (JTextField) e.getSource();
						String searchText = searchJTextField.getText().trim();
						highlight(searchText);
					}
				}

				@Override
				public void keyPressed(KeyEvent e) {
					// do nothing
				}
			});

		}

		return searchJTextField;
	}

	private JLabel getSearchResultsJLabel() {

		if (searchResultsJLabel == null) {

			searchResultsJLabel = new JLabel();

			Dimension dim = new Dimension(100, 22);
			searchResultsJLabel.setPreferredSize(dim);
			searchResultsJLabel.setMaximumSize(dim);

		}

		return searchResultsJLabel;
	}

	private JButton getSearchFirstJButton() {

		if (searchFirstJButton == null) {

			searchFirstJButton = new JButton(firstImageIcon);

			Dimension size = new Dimension(40, 20);
			Dimension minSize = new Dimension(30, 20);

			searchFirstJButton.setPreferredSize(size);
			searchFirstJButton.setMinimumSize(minSize);
			searchFirstJButton.setMaximumSize(size);
			searchFirstJButton.setBorder(BorderFactory.createEmptyBorder());
			searchFirstJButton.setEnabled(false);
			searchFirstJButton.setToolTipText("First search result");
			searchFirstJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					searchNavPrevKey = searchNavKey;
					searchNavKey = 1;
					searchTraverse();
				}
			});
		}

		return searchFirstJButton;

	}

	/**
	 * @return the searchPrevJButton
	 */
	private JButton getSearchPrevJButton() {

		if (searchPrevJButton == null) {

			searchPrevJButton = new JButton(prevImageIcon);

			Dimension size = new Dimension(40, 20);
			Dimension minSize = new Dimension(30, 20);
			searchPrevJButton.setPreferredSize(size);
			searchPrevJButton.setMinimumSize(minSize);
			searchPrevJButton.setMaximumSize(size);
			searchPrevJButton.setBorder(BorderFactory.createEmptyBorder());
			searchPrevJButton.setEnabled(false);
			searchPrevJButton.setToolTipText("Previous search result from current selection");
			searchPrevJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					searchNavPrevKey = searchNavKey;
					searchNavKey--;
					searchTraverse();
				}
			});
		}

		return searchPrevJButton;
	}

	/**
	 * @return the searchNextJButton
	 */
	private JButton getSearchNextJButton() {

		if (searchNextJButton == null) {
			searchNextJButton = new JButton(nextImageIcon);

			Dimension size = new Dimension(40, 20);
			Dimension minSize = new Dimension(30, 20);
			searchNextJButton.setPreferredSize(size);
			searchNextJButton.setMinimumSize(minSize);
			searchNextJButton.setMaximumSize(size);
			searchNextJButton.setBorder(BorderFactory.createEmptyBorder());
			searchNextJButton.setEnabled(false);
			searchNextJButton.setToolTipText("Next search result from current selection");
			searchNextJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					searchNavPrevKey = searchNavKey;
					searchNavKey++;
					searchTraverse();
				}
			});
		}

		return searchNextJButton;
	}

	/**
	 * @return the searchLastJButton
	 */
	private JButton getSearchLastJButton() {

		if (searchLastJButton == null) {

			searchLastJButton = new JButton(lastImageIcon);

			Dimension size = new Dimension(40, 20);
			Dimension minSize = new Dimension(30, 20);
			searchLastJButton.setPreferredSize(size);
			searchLastJButton.setMinimumSize(minSize);
			searchLastJButton.setMaximumSize(size);
			searchLastJButton.setBorder(BorderFactory.createEmptyBorder());
			searchLastJButton.setEnabled(false);
			searchLastJButton.setToolTipText("Last search result");
			searchLastJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					searchNavPrevKey = searchNavKey;
					searchNavKey = totalSearchCount;
					searchTraverse();
				}
			});
		}

		return searchLastJButton;
	}

	private JCheckBox getCaseSensitiveSearchJCheckBox() {

		if (caseSensitiveSearchJCheckBox == null) {

			caseSensitiveSearchJCheckBox = new JCheckBox("Case Sensitive");

			caseSensitiveSearchJCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					String logEntryText = getLogEntryText();
					byte[] aLogEntryTextBytes = null;

					if (e.getStateChange() == ItemEvent.SELECTED) {
						aLogEntryTextBytes = logEntryText.getBytes();
					} else {
						aLogEntryTextBytes = logEntryText.toUpperCase().getBytes();
					}

					setLogEntryTextBytes(aLogEntryTextBytes);

					// retry the search
					JTextField searchJTextField = getSearchJTextField();

					String searchText = searchJTextField.getText().trim();

					highlight(searchText);
				}
			});
		}

		return caseSensitiveSearchJCheckBox;
	}

	/**
	 * @return the wrapLogEntryTextJButton
	 */
	protected JButton getWrapLogEntryTextJButton() {

		if (wrapLogEntryTextJButton == null) {

			wrapLogEntryTextJButton = new JButton();

			Dimension size = new Dimension(120, 20);
			wrapLogEntryTextJButton.setPreferredSize(size);
			wrapLogEntryTextJButton.setMaximumSize(size);
			wrapLogEntryTextJButton.setBorder(BorderFactory.createEmptyBorder());
			wrapLogEntryTextJButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					JButton wrapLogEntryTextJButton = getWrapLogEntryTextJButton();

					if (WRAP_ON_ACTION.equals(e.getActionCommand())) {

						wrapLogEntryAreaText(true);

						wrapLogEntryTextJButton.setText(WRAP_OFF_ACTION);
						wrapLogEntryTextJButton.setActionCommand(WRAP_OFF_ACTION);

						setWrapOn(true);
					} else {

						wrapLogEntryAreaText(false);

						wrapLogEntryTextJButton.setText(WRAP_ON_ACTION);
						wrapLogEntryTextJButton.setActionCommand(WRAP_ON_ACTION);

						setWrapOn(false);
					}
				}
			});

			boolean wrapOn = isWrapOn();

			if (wrapOn) {
				wrapLogEntryTextJButton.setText(WRAP_OFF_ACTION);
				wrapLogEntryTextJButton.setToolTipText(WRAP_OFF_ACTION);
			} else {
				wrapLogEntryTextJButton.setText(WRAP_ON_ACTION);
				wrapLogEntryTextJButton.setToolTipText(WRAP_ON_ACTION);
			}
		}

		return wrapLogEntryTextJButton;
	}

	/**
	 * @return the logEntryArea
	 */
	private JTextArea getLogEntryArea() {

		if (logEntryArea == null) {
			logEntryArea = new JTextArea();
			logEntryArea.setText(logEntryText);
			logEntryArea.setCaretPosition(0);

			boolean wrapOn = isWrapOn();

			if (wrapOn) {
				logEntryArea.setWrapStyleWord(true);
				logEntryArea.setLineWrap(true);
			} else {
				logEntryArea.setWrapStyleWord(false);
				logEntryArea.setLineWrap(false);
			}

			logEntryArea.setEditable(false);
		}

		return logEntryArea;
	}

	private JPanel getSpecialMessageJPanel() {

		JPanel specialMessageJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(specialMessageJPanel, BoxLayout.X_AXIS);
		specialMessageJPanel.setLayout(layout);

		JLabel specialMessageJLabel = new JLabel(specialMessage);

		Font labelFont = specialMessageJLabel.getFont();
		labelFont = labelFont.deriveFont(Font.BOLD, 11);
		specialMessageJLabel.setFont(labelFont);
		specialMessageJLabel.setForeground(Color.RED);

		Dimension dim = new Dimension(100, 30);

		specialMessageJPanel.add(Box.createHorizontalGlue());
		specialMessageJPanel.add(Box.createRigidArea(dim));
		specialMessageJPanel.add(specialMessageJLabel);
		specialMessageJPanel.add(Box.createRigidArea(dim));
		specialMessageJPanel.add(Box.createHorizontalGlue());

		// specialMessageJPanel.setBorder(BorderFactory.createLineBorder(
		// MyColor.GRAY, 1));

		return specialMessageJPanel;
	}

	private JPanel getControlsJPanel() {

		JPanel controlsJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(controlsJPanel, BoxLayout.X_AXIS);
		controlsJPanel.setLayout(layout);

		JPanel searchJPanel = getSearchJPanel();
		JPanel wrapLogEntryTextJPanel = getWrapLogEntryTextJPanel();

		controlsJPanel.add(searchJPanel);
		controlsJPanel.add(wrapLogEntryTextJPanel);

		return controlsJPanel;
	}

	private JPanel getSearchJPanel() {

		JPanel searchJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(searchJPanel, BoxLayout.X_AXIS);
		searchJPanel.setLayout(layout);

		JLabel searchJLabel = new JLabel("Search");

		JLabel resultsLabel = new JLabel("Results:");
		Dimension size = new Dimension(40, 20);
		resultsLabel.setPreferredSize(size);
		resultsLabel.setMinimumSize(size);
		resultsLabel.setMaximumSize(size);

		JTextField searchJTextField = getSearchJTextField();

		JButton searchFirstJButton = getSearchFirstJButton();
		JButton searchPrevJButton = getSearchPrevJButton();
		JLabel searchResultsJLabel = getSearchResultsJLabel();
		JButton searchNextJButton = getSearchNextJButton();
		JButton searchLastJButton = getSearchLastJButton();

		JCheckBox caseSensitiveSearchJCheckBox = getCaseSensitiveSearchJCheckBox();

		int height = 30;
		Dimension startDim = new Dimension(20, height);
		Dimension dim = new Dimension(10, height);

		searchJPanel.add(Box.createRigidArea(startDim));
		searchJPanel.add(searchJLabel);
		searchJPanel.add(Box.createRigidArea(dim));
		searchJPanel.add(searchJTextField);
		searchJPanel.add(Box.createRigidArea(dim));
		searchJPanel.add(searchFirstJButton);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));
		searchJPanel.add(searchPrevJButton);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));
		searchJPanel.add(resultsLabel);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));
		searchJPanel.add(searchResultsJLabel);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));
		searchJPanel.add(searchNextJButton);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));
		searchJPanel.add(searchLastJButton);
		searchJPanel.add(Box.createRigidArea(new Dimension(4, height)));

		searchJPanel.add(Box.createRigidArea(dim));
		searchJPanel.add(caseSensitiveSearchJCheckBox);
		searchJPanel.add(Box.createRigidArea(dim));
		searchJPanel.add(Box.createHorizontalGlue());

		searchJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		return searchJPanel;
	}

	private JPanel getWrapLogEntryTextJPanel() {

		JPanel wrapLogEntryTextJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(wrapLogEntryTextJPanel, BoxLayout.X_AXIS);
		wrapLogEntryTextJPanel.setLayout(layout);

		JButton wrapLogEntryTextJButton = getWrapLogEntryTextJButton();

		Dimension dim = new Dimension(10, 30);

		wrapLogEntryTextJPanel.add(Box.createHorizontalGlue());
		wrapLogEntryTextJPanel.add(Box.createRigidArea(dim));
		wrapLogEntryTextJPanel.add(wrapLogEntryTextJButton);
		wrapLogEntryTextJPanel.add(Box.createRigidArea(dim));
		wrapLogEntryTextJPanel.add(Box.createHorizontalGlue());

		wrapLogEntryTextJPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		return wrapLogEntryTextJPanel;
	}

	private JComponent getTextAreaJComponent() {

		JTextArea logEntryArea = getLogEntryArea();

		JScrollPane jScrollPane = new JScrollPane(logEntryArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		jScrollPane.getVerticalScrollBar().setUnitIncrement(16);

		return jScrollPane;
	}

	protected void wrapLogEntryAreaText(boolean wrap) {

		JTextArea logEntryArea = getLogEntryArea();

		if (wrap) {
			logEntryArea.setWrapStyleWord(true);
			logEntryArea.setLineWrap(true);
		} else {
			logEntryArea.setWrapStyleWord(false);
			logEntryArea.setLineWrap(false);
		}
	}

	protected void highlight(String searchStr) {

		resetSearch();

		if ((searchStr != null) && (!"".equals(searchStr))) {

			JTextArea logEntryArea = getLogEntryArea();
			Highlighter highlighter = logEntryArea.getHighlighter();

			totalSearchCount = new Integer(0);

			JCheckBox caseSensitiveSearchJCheckBox = getCaseSensitiveSearchJCheckBox();
			boolean caseSensitive = caseSensitiveSearchJCheckBox.isSelected();

			String searchText = searchStr;

			if (!caseSensitive) {
				searchText = searchText.toUpperCase();
			}

			byte[] searchTextbytes = searchText.getBytes();

			try {

				int index = KnuthMorrisPrattAlgorithm.indexOf(logEntryTextBytes, searchTextbytes);

				while (index != -1) {

					int endPos = index + searchTextbytes.length;

					Highlight highlight = (Highlight) highlighter.addHighlight(index, endPos, highlightPainter);

					TextAreaSearchResult tasr = new TextAreaSearchResult(index, endPos, highlight);

					searchResultsMap.put(++totalSearchCount, tasr);

					index = KnuthMorrisPrattAlgorithm.indexOf(logEntryTextBytes, searchTextbytes, endPos);
				}

			} catch (Exception e) {
				LOG.error("Error highlighting search str: " + searchStr, e);
			}

			updateSearchNavIndexDetails();

		}
	}

	private void resetSearch() {

		removeHighlights();

		totalSearchCount = null;
		searchNavKey = 0;
		searchNavPrevKey = 0;
		searchResultsMap.clear();

		updateSearchNavIndexDetails();

	}

	private void removeHighlights() {

		JTextArea logEntryArea = getLogEntryArea();
		Highlighter highlighter = logEntryArea.getHighlighter();

		highlighter.removeAllHighlights();
	}

	public void updateSearchNavIndexDetails() {

		SelectedRowPosition selectedRowPosition = getSelectedSearchPosition();

		updateSearchPageButtons(selectedRowPosition);

	}

	private SelectedRowPosition getSelectedSearchPosition() {

		SelectedRowPosition selectedRowPosition = SelectedRowPosition.NONE;

		if ((totalSearchCount != null) && (totalSearchCount > 0)) {

			if ((searchNavKey > 1) && (searchNavKey < totalSearchCount)) {
				selectedRowPosition = SelectedRowPosition.BETWEEN;
			} else if (searchNavKey == totalSearchCount) {
				selectedRowPosition = SelectedRowPosition.LAST;
			} else if (searchNavKey <= 1) {
				selectedRowPosition = SelectedRowPosition.FIRST;
			} else {
				selectedRowPosition = SelectedRowPosition.NONE;
			}

		}

		return selectedRowPosition;

	}

	private void updateSearchPageButtons(SelectedRowPosition selectedRowPosition) {

		JButton searchFirstJButton = getSearchFirstJButton();
		JButton searchPrevJButton = getSearchPrevJButton();
		JButton searchNextJButton = getSearchNextJButton();
		JButton searchLastJButton = getSearchLastJButton();

		switch (selectedRowPosition) {

		case FIRST:
			searchFirstJButton.setEnabled(false);
			searchPrevJButton.setEnabled(false);
			searchNextJButton.setEnabled(true);
			searchLastJButton.setEnabled(true);
			break;

		case LAST:
			searchFirstJButton.setEnabled(true);
			searchPrevJButton.setEnabled(true);
			searchNextJButton.setEnabled(false);
			searchLastJButton.setEnabled(false);
			break;

		case BETWEEN:
			searchFirstJButton.setEnabled(true);
			searchPrevJButton.setEnabled(true);
			searchNextJButton.setEnabled(true);
			searchLastJButton.setEnabled(true);
			break;

		case NONE:
			searchFirstJButton.setEnabled(false);
			searchPrevJButton.setEnabled(false);
			searchNextJButton.setEnabled(false);
			searchLastJButton.setEnabled(false);
			break;

		default:
			break;
		}

		updateSearchResultsJLabel();

	}

	private void updateSearchResultsJLabel() {

		String searchResultStr = null;

		JLabel searchResultsJLabel = getSearchResultsJLabel();

		if (totalSearchCount == null) {
			searchResultsJLabel.setText("");
		} else {

			String labelStr = null;

			if (searchNavKey <= 0) {
				searchResultStr = "%d results found.";
				labelStr = String.format(searchResultStr, totalSearchCount);
			} else {
				searchResultStr = "%d of %d results";
				labelStr = String.format(searchResultStr, searchNavKey, totalSearchCount);
			}

			searchResultsJLabel.setText(labelStr);
		}

	}

	private void searchTraverse() {

		JTextArea logEntryArea = getLogEntryArea();

		// Focus the text area, otherwise the highlighting won't show up
		logEntryArea.requestFocusInWindow();

		// repaint the previous selected text to search highlight painter.
		// then move on to highlight the new entry.
		if ((searchNavPrevKey > 0) && (searchNavKey != searchNavPrevKey)) {

			TextAreaSearchResult tasr = searchResultsMap.get(searchNavPrevKey);

			int beginPos = tasr.getBeginPos();
			int endPos = tasr.getEndPos();
			Highlight highlight = tasr.getHighlight();

			Highlighter highlighter = logEntryArea.getHighlighter();

			try {
				Highlight newHighlight = (Highlight) highlighter.addHighlight(beginPos, endPos, highlight.getPainter());

				// update the tasr with new highlight instance.
				tasr.setHighlight(newHighlight);

			} catch (Exception e) {
				LOG.error("Error highlighting TextAreaSearchResult: " + tasr, e);
			}
		}

		// searchNavIndex is already positioned to new index.
		TextAreaSearchResult tasr = searchResultsMap.get(searchNavKey);

		if (tasr != null) {

			int beginPos = tasr.getBeginPos();
			int endPos = tasr.getEndPos();
			Highlight highlight = tasr.getHighlight();

			try {

				logEntryArea.getHighlighter().removeHighlight(highlight);

				Rectangle viewRect = logEntryArea.modelToView(endPos);

				if (logEntryArea.getParent() instanceof JViewport) {

					JViewport viewport = (JViewport) logEntryArea.getParent();
					GUIUtilities.scrollRectangleToVisible(viewport, viewRect);
				} else {
					logEntryArea.scrollRectToVisible(viewRect);
				}

			} catch (Exception e) {
				LOG.error("Error removing highlighting TextAreaSearchResult: " + tasr, e);
			}

			logEntryArea.setCaretPosition(endPos);
			logEntryArea.moveCaretPosition(beginPos);

		}

		updateSearchNavIndexDetails();
	}

	private class TextAreaSearchResult {

		private int beginPos;

		private int endPos;

		private Highlight highlight;

		protected TextAreaSearchResult(int beginPos, int endPos, Highlight highlight) {
			super();
			this.beginPos = beginPos;
			this.endPos = endPos;
			this.highlight = highlight;
		}

		protected int getBeginPos() {
			return beginPos;
		}

		protected int getEndPos() {
			return endPos;
		}

		protected Highlight getHighlight() {
			return highlight;
		}

		protected void setHighlight(Highlight highlight) {
			this.highlight = highlight;
		}

		@Override
		public String toString() {
			return "beginPos=" + beginPos + ", endPos=" + endPos;
		}

	}
}
