
package com.pega.gcs.logviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.pega.gcs.fringecommon.guiutilities.CheckBoxLabelMenuItem;
import com.pega.gcs.fringecommon.guiutilities.CheckBoxMenuItemPopupEntry;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class LogMessagesLoggerSelectPanel extends JPanel {

    private static final long serialVersionUID = 3225142996534047809L;

    private static final String MATCH_CASE_ON_ACTION = "Match case on";

    private static final String MATCH_CASE_OFF_ACTION = "Match case Off";

    private List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;

    // CheckBoxLabelMenuItem doesnt update the popup entry.
    private List<CheckBoxLabelMenuItem<LogEntryKey>> checkBoxLabelMenuItemList;

    private JPanel checkBoxLabelMenuItemListPanel;

    private JTextField searchTextField;

    private boolean caseSensitive;

    private ImageIcon matchCaseOnIcon;

    private ImageIcon matchCaseOffIcon;

    public LogMessagesLoggerSelectPanel(List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList) {

        super();

        this.loggerColumnEntryList = loggerColumnEntryList;
        this.checkBoxLabelMenuItemList = new ArrayList<>();
        this.checkBoxLabelMenuItemListPanel = null;
        this.caseSensitive = false;

        matchCaseOnIcon = FileUtilities.getImageIcon(this.getClass(), "matchcaseon.png");

        matchCaseOffIcon = FileUtilities.getImageIcon(this.getClass(), "matchcaseoff.png");

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        JPanel headerPanel = getHeaderPanel();
        JComponent checkBoxLabelMenuItemListComponent = getCheckBoxLabelMenuItemListComponent();

        add(headerPanel, gbc1);
        add(checkBoxLabelMenuItemListComponent, gbc2);

    }

    private List<CheckBoxMenuItemPopupEntry<LogEntryKey>> getLoggerColumnEntryList() {
        return loggerColumnEntryList;
    }

    private List<CheckBoxLabelMenuItem<LogEntryKey>> getCheckBoxLabelMenuItemList() {
        return checkBoxLabelMenuItemList;
    }

    private boolean isCaseSensitive() {
        return caseSensitive;
    }

    private void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    private ImageIcon getMatchCaseOnIcon() {
        return matchCaseOnIcon;
    }

    private ImageIcon getMatchCaseOffIcon() {
        return matchCaseOffIcon;
    }

    private JPanel getClearAllButtonPanel() {

        JPanel clearAllButtonPanel = new JPanel();

        clearAllButtonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.CENTER;
        gbc1.insets = new Insets(0, 0, 0, 3);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.CENTER;
        gbc2.insets = new Insets(0, 3, 0, 0);

        JButton selectAllButton = getSelectAllButton();
        JButton clearAllButton = getClearAllButton();

        clearAllButtonPanel.add(selectAllButton, gbc1);
        clearAllButtonPanel.add(clearAllButton, gbc2);

        return clearAllButtonPanel;
    }

    private JButton getSelectAllButton() {
        JButton selectAllButton = new JButton("Select All");

        Dimension size = new Dimension(80, 20);
        selectAllButton.setPreferredSize(size);
        selectAllButton.setMinimumSize(size);
        selectAllButton.setMaximumSize(size);

        selectAllButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                applySelectAll();
            }
        });

        return selectAllButton;
    }

    private JButton getClearAllButton() {
        JButton clearAllButton = new JButton("Clear All");

        Dimension size = new Dimension(80, 20);
        clearAllButton.setPreferredSize(size);
        clearAllButton.setMinimumSize(size);
        clearAllButton.setMaximumSize(size);

        clearAllButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                applyClearAll();
            }
        });

        return clearAllButton;
    }

    private JPanel getSearchPanel() {

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(0, 5, 0, 1);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 1, 0, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 2;
        gbc3.gridy = 0;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(0, 1, 0, 5);

        JLabel searchJLabel = new JLabel("Search");
        JTextField searchJTextField = getSearchTextField();
        JButton caseSensitiveJButton = getCaseSensitiveButton();

        searchPanel.add(searchJLabel, gbc1);
        searchPanel.add(searchJTextField, gbc2);
        searchPanel.add(caseSensitiveJButton, gbc3);

        return searchPanel;
    }

    private JTextField getSearchTextField() {

        if (searchTextField == null) {

            searchTextField = new JTextField();
            searchTextField.setEditable(true);

            searchTextField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    // do nothing
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {

                    if (keyEvent.getSource() instanceof JTextField) {
                        applySearchText();
                    }
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    // do nothing
                }
            });
        }

        return searchTextField;
    }

    private JButton getCaseSensitiveButton() {

        final JButton caseSensitiveButton = new JButton(matchCaseOnIcon);
        caseSensitiveButton.setActionCommand(MATCH_CASE_ON_ACTION);
        caseSensitiveButton.setToolTipText(MATCH_CASE_ON_ACTION);

        Dimension size = new Dimension(25, 20);
        caseSensitiveButton.setPreferredSize(size);

        caseSensitiveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (MATCH_CASE_ON_ACTION.equals(actionEvent.getActionCommand())) {

                    setCaseSensitive(true);

                    caseSensitiveButton.setActionCommand(MATCH_CASE_OFF_ACTION);
                    caseSensitiveButton.setIcon(getMatchCaseOffIcon());
                    caseSensitiveButton.setToolTipText(MATCH_CASE_OFF_ACTION);

                } else {

                    setCaseSensitive(false);

                    caseSensitiveButton.setActionCommand(MATCH_CASE_ON_ACTION);
                    caseSensitiveButton.setIcon(getMatchCaseOnIcon());
                    caseSensitiveButton.setToolTipText(MATCH_CASE_ON_ACTION);
                }

                applySearchText();
            }
        });

        return caseSensitiveButton;
    }

    private JPanel getHeaderPanel() {

        JPanel headerPanel = new JPanel();

        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(5, 0, 2, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 0, 8, 0);

        JPanel clearAllButtonJPanel = getClearAllButtonPanel();
        JPanel searchJPanel = getSearchPanel();

        headerPanel.add(clearAllButtonJPanel, gbc1);
        headerPanel.add(searchJPanel, gbc2);

        headerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return headerPanel;
    }

    private JComponent getCheckBoxLabelMenuItemListComponent() {

        JPanel checkBoxLabelMenuItemListPanel = getCheckBoxLabelMenuItemListPanel();

        JScrollPane scrollPane = new JScrollPane(checkBoxLabelMenuItemListPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();

        verticalScrollBar.setUnitIncrement(14);

        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return scrollPane;
    }

    private JPanel getCheckBoxLabelMenuItemListPanel() {

        if (checkBoxLabelMenuItemListPanel == null) {

            checkBoxLabelMenuItemListPanel = new JPanel();
            checkBoxLabelMenuItemListPanel.setLayout(new GridBagLayout());

            List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;
            loggerColumnEntryList = getLoggerColumnEntryList();

            populateCheckBoxLabelMenuItemListJPanel(loggerColumnEntryList);

        }

        return checkBoxLabelMenuItemListPanel;
    }

    private void syncSelections() {

        List<CheckBoxLabelMenuItem<LogEntryKey>> checkBoxLabelMenuItemList;
        checkBoxLabelMenuItemList = getCheckBoxLabelMenuItemList();

        for (CheckBoxLabelMenuItem<LogEntryKey> cblmi : checkBoxLabelMenuItemList) {
            CheckBoxMenuItemPopupEntry<LogEntryKey> fthcEntry;

            fthcEntry = (CheckBoxMenuItemPopupEntry<LogEntryKey>) cblmi.getFilterTableHeaderPopupEntry();

            boolean selected = cblmi.isSelected();
            fthcEntry.setSelected(selected);
        }
    }

    private void populateCheckBoxLabelMenuItemListJPanel(
            List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList) {

        // sync the previous selections
        syncSelections();

        List<CheckBoxLabelMenuItem<LogEntryKey>> checkBoxLabelMenuItemList;
        checkBoxLabelMenuItemList = getCheckBoxLabelMenuItemList();

        checkBoxLabelMenuItemList.clear();

        JPanel checkBoxLabelMenuItemListJPanel = getCheckBoxLabelMenuItemListPanel();

        checkBoxLabelMenuItemListJPanel.removeAll();

        int index = 0;

        for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : loggerColumnEntryList) {

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridx = 0;
            gbc1.gridy = index;
            gbc1.weightx = 1.0D;
            gbc1.weighty = 0.0D;
            gbc1.fill = GridBagConstraints.BOTH;
            gbc1.anchor = GridBagConstraints.NORTHWEST;
            gbc1.insets = new Insets(0, 5, 0, 0);

            CheckBoxLabelMenuItem<LogEntryKey> cblmi;
            cblmi = new CheckBoxLabelMenuItem<>(loggerColumnEntry, true);

            checkBoxLabelMenuItemList.add(cblmi);
            checkBoxLabelMenuItemListJPanel.add(cblmi, gbc1);

            index++;

        }

        revalidate();
        repaint();
    }

    private void applySelectAll() {

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;
        loggerColumnEntryList = getLoggerColumnEntryList();

        for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : loggerColumnEntryList) {
            loggerColumnEntry.setSelected(true);
        }

        populateCheckBoxLabelMenuItemListJPanel(loggerColumnEntryList);
    }

    private void applyClearAll() {

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;
        loggerColumnEntryList = getLoggerColumnEntryList();

        for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : loggerColumnEntryList) {
            loggerColumnEntry.setSelected(false);
        }

        populateCheckBoxLabelMenuItemListJPanel(loggerColumnEntryList);
    }

    private void applySearchText() {

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> filteredLoggerColumnEntryList = null;

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;
        loggerColumnEntryList = getLoggerColumnEntryList();

        JTextField searchJTextField = getSearchTextField();

        String searchText = searchJTextField.getText().trim();

        if ((searchText != null) && (!"".equals(searchText))) {

            filteredLoggerColumnEntryList = new ArrayList<>();

            for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : loggerColumnEntryList) {

                String entryText = loggerColumnEntry.getItemText();

                if (!isCaseSensitive()) {
                    searchText = searchText.toUpperCase();
                    entryText = entryText.toUpperCase();
                }

                if (entryText.indexOf(searchText) != -1) {
                    filteredLoggerColumnEntryList.add(loggerColumnEntry);
                }

            }

        } else {
            filteredLoggerColumnEntryList = loggerColumnEntryList;
        }

        populateCheckBoxLabelMenuItemListJPanel(filteredLoggerColumnEntryList);
    }

    public List<CheckBoxMenuItemPopupEntry<LogEntryKey>> getSelectedLoggerColumnEntryList() {

        syncSelections();

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> loggerColumnEntryList;
        loggerColumnEntryList = getLoggerColumnEntryList();

        List<CheckBoxMenuItemPopupEntry<LogEntryKey>> selectedLoggerColumnEntryList;
        selectedLoggerColumnEntryList = new ArrayList<>();

        for (CheckBoxMenuItemPopupEntry<LogEntryKey> loggerColumnEntry : loggerColumnEntryList) {

            if (loggerColumnEntry.isSelected()) {
                selectedLoggerColumnEntryList.add(loggerColumnEntry);
            }
        }

        return selectedLoggerColumnEntryList;
    }

}
