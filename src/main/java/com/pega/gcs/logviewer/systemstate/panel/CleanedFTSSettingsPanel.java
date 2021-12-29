
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.systemstate.model.AutomatedSearchAlertsSettings;
import com.pega.gcs.logviewer.systemstate.model.CleanedFTSSettings;
import com.pega.gcs.logviewer.systemstate.model.HostNodeSetting;
import com.pega.gcs.logviewer.systemstate.model.NotificationSettings;
import com.pega.gcs.logviewer.systemstate.model.QuerySettings;
import com.pega.gcs.logviewer.systemstate.model.SecuritySettings;
import com.pega.gcs.logviewer.systemstate.table.HostNodeSettingTableColumn;
import com.pega.gcs.logviewer.systemstate.table.HostNodeSettingTableModel;

public class CleanedFTSSettingsPanel extends JPanel {

    private static final long serialVersionUID = 2412415813960579386L;

    public CleanedFTSSettingsPanel(CleanedFTSSettings cleanedFTSSettings) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 2, 2);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 2, 2, 5);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 2, 2);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 2, 5);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 1.0D;
        gbc5.weighty = 1.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 2, 5);
        gbc5.gridwidth = GridBagConstraints.REMAINDER;

        JPanel automatedSearchAlertsSettingsPanel = getAutomatedSearchAlertsSettingsPanel(
                cleanedFTSSettings.getAutomatedSearchAlertsSettings());
        JPanel notificationSettingsPanel = getNotificationSettingsPanel(cleanedFTSSettings.getNotificationSettings());
        JPanel querySettingsPanel = getQuerySettingsPanel(cleanedFTSSettings.getQuerySettings());
        JPanel securitySettingsPanel = getSecuritySettingsPanel(cleanedFTSSettings.getSecuritySettings());
        JPanel searchIndexHostNodeSettingSetPanel = getSearchIndexHostNodeSettingSetPanel(
                cleanedFTSSettings.getSearchIndexHostNodeSettingSet());

        add(automatedSearchAlertsSettingsPanel, gbc1);
        add(notificationSettingsPanel, gbc2);
        add(querySettingsPanel, gbc3);
        add(securitySettingsPanel, gbc4);
        add(searchIndexHostNodeSettingSetPanel, gbc5);

    }

    private JPanel getAutomatedSearchAlertsSettingsPanel(AutomatedSearchAlertsSettings automatedSearchAlertsSettings) {

        JPanel automatedSearchAlertsSettingsPanel = new JPanel();

        automatedSearchAlertsSettingsPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 5);

        // 2nd Row
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        JLabel automaticallyMonitorFilesLabel = new JLabel("Automatically Monitor Files");
        JLabel monitoringFrequencyLabel = new JLabel("Monitoring Frequency");

        String valueText;

        Boolean automaticallyMonitorFiles;
        automaticallyMonitorFiles = automatedSearchAlertsSettings.getAutomaticallyMonitorFiles();
        valueText = (automaticallyMonitorFiles != null) ? Boolean.toString(automaticallyMonitorFiles) : null;
        JTextField automaticallyMonitorFilesTextField = GUIUtilities.getValueTextField(valueText);

        valueText = automatedSearchAlertsSettings.getMonitoringFrequency();
        JTextField monitoringFrequencyTextField = GUIUtilities.getValueTextField(valueText);

        // 1st Row
        automatedSearchAlertsSettingsPanel.add(automaticallyMonitorFilesLabel, gbc1);
        automatedSearchAlertsSettingsPanel.add(automaticallyMonitorFilesTextField, gbc2);

        // 2nd Row
        automatedSearchAlertsSettingsPanel.add(monitoringFrequencyLabel, gbc3);
        automatedSearchAlertsSettingsPanel.add(monitoringFrequencyTextField, gbc4);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        automatedSearchAlertsSettingsPanel
                .setBorder(BorderFactory.createTitledBorder(loweredEtched, "Automated Search Alerts Settings"));

        return automatedSearchAlertsSettingsPanel;
    }

    private JPanel getNotificationSettingsPanel(NotificationSettings notificationSettings) {

        JPanel notificationSettingsPanel = new JPanel();

        notificationSettingsPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 5);

        // 2nd Row
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        JLabel notifyWhenProcessesAreDoneLabel = new JLabel("Notify When Processes Are Done");
        JLabel notifyOnChangeOfSearchHostNodesLabel = new JLabel("Notify On Change Of Search Host Nodes");

        String valueText;

        Boolean notifyWhenProcessesAreDone;
        notifyWhenProcessesAreDone = notificationSettings.getNotifyWhenProcessesAreDone();
        valueText = (notifyWhenProcessesAreDone != null) ? Boolean.toString(notifyWhenProcessesAreDone) : null;
        JTextField notifyWhenProcessesAreDoneTextField = GUIUtilities.getValueTextField(valueText);

        Boolean notifyOnChangeOfSearchHostNodes;
        notifyOnChangeOfSearchHostNodes = notificationSettings.getNotifyOnChangeOfSearchHostNodes();
        valueText = (notifyOnChangeOfSearchHostNodes != null) ? Boolean.toString(notifyOnChangeOfSearchHostNodes)
                : null;
        JTextField notifyOnChangeOfSearchHostNodesTextField = GUIUtilities.getValueTextField(valueText);

        // 1st Row
        notificationSettingsPanel.add(notifyWhenProcessesAreDoneLabel, gbc1);
        notificationSettingsPanel.add(notifyWhenProcessesAreDoneTextField, gbc2);

        // 2nd Row
        notificationSettingsPanel.add(notifyOnChangeOfSearchHostNodesLabel, gbc3);
        notificationSettingsPanel.add(notifyOnChangeOfSearchHostNodesTextField, gbc4);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        notificationSettingsPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Notification Settings"));

        return notificationSettingsPanel;
    }

    private JPanel getQuerySettingsPanel(QuerySettings querySettings) {

        JPanel querySettingsPanel = new JPanel();

        querySettingsPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 5);

        // 2nd Row
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        // 3rd Row
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 5, 5, 5);

        // 4th Row
        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 3;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 3;
        gbc8.weightx = 1.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(5, 5, 5, 5);

        JLabel fuzzySearchEnabledLabel = new JLabel("Fuzzy Search Enabled");
        JLabel degreeOfFuzzinessLabel = new JLabel("Degree Of Fuzziness");
        JLabel fuzzyPrefixLengthLabel = new JLabel("Fuzzy Prefix Length");
        JLabel maxExpansionTermsLabel = new JLabel("Max Expansion Terms");

        String valueText;

        Boolean fuzzySearchEnabled;
        fuzzySearchEnabled = querySettings.getFuzzySearchEnabled();
        valueText = (fuzzySearchEnabled != null) ? Boolean.toString(fuzzySearchEnabled) : null;
        JTextField fuzzySearchEnabledTextField = GUIUtilities.getValueTextField(valueText);

        valueText = querySettings.getDegreeOfFuzziness();
        JTextField degreeOfFuzzinessTextField = GUIUtilities.getValueTextField(valueText);

        Integer fuzzyPrefixLength;
        fuzzyPrefixLength = querySettings.getFuzzyPrefixLength();
        valueText = (fuzzyPrefixLength != null) ? Integer.toString(fuzzyPrefixLength) : null;
        JTextField fuzzyPrefixLengthTextField = GUIUtilities.getValueTextField(valueText);

        Integer maxExpansionTerms;
        maxExpansionTerms = querySettings.getMaxExpansionTerms();
        valueText = (maxExpansionTerms != null) ? Integer.toString(maxExpansionTerms) : null;
        JTextField maxExpansionTermsTextField = GUIUtilities.getValueTextField(valueText);

        // 1st Row
        querySettingsPanel.add(fuzzySearchEnabledLabel, gbc1);
        querySettingsPanel.add(fuzzySearchEnabledTextField, gbc2);

        // 2nd Row
        querySettingsPanel.add(degreeOfFuzzinessLabel, gbc3);
        querySettingsPanel.add(degreeOfFuzzinessTextField, gbc4);

        // 3rd Row
        querySettingsPanel.add(fuzzyPrefixLengthLabel, gbc5);
        querySettingsPanel.add(fuzzyPrefixLengthTextField, gbc6);

        // 4th Row
        querySettingsPanel.add(maxExpansionTermsLabel, gbc7);
        querySettingsPanel.add(maxExpansionTermsTextField, gbc8);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        querySettingsPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Query Settings"));

        return querySettingsPanel;
    }

    private JPanel getSecuritySettingsPanel(SecuritySettings securitySettings) {

        JPanel securitySettingsPanel = new JPanel();

        securitySettingsPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 0.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 5);

        // 2nd Row
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 1;
        gbc3.weightx = 0.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 1;
        gbc4.gridy = 1;
        gbc4.weightx = 1.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 5);

        // 3rd Row
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 0;
        gbc5.gridy = 2;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 1;
        gbc6.gridy = 2;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 5, 5, 5);

        JLabel encryptionSearchInterNodeCommunicationsLabel = new JLabel("Encryption Search Inter Node Communications");
        JLabel displayPropertiesWithAccessControlPoliciesLabel = new JLabel(
                "Display Properties With Access Control Policies");
        JLabel customizeFullTextSearchLabel = new JLabel("Customize Full Text Search");

        String valueText;

        Boolean encryptionSearchInterNodeCommunications;
        encryptionSearchInterNodeCommunications = securitySettings.getEncryptionSearchInterNodeCommunications();
        valueText = (encryptionSearchInterNodeCommunications != null)
                ? Boolean.toString(encryptionSearchInterNodeCommunications)
                : null;
        JTextField encryptionSearchInterNodeCommunicationsTextField = GUIUtilities.getValueTextField(valueText);

        Boolean displayPropertiesWithAccessControlPolicies;
        displayPropertiesWithAccessControlPolicies = securitySettings.getDisplayPropertiesWithAccessControlPolicies();
        valueText = (displayPropertiesWithAccessControlPolicies != null)
                ? Boolean.toString(displayPropertiesWithAccessControlPolicies)
                : null;
        JTextField displayPropertiesWithAccessControlPoliciesTextField = GUIUtilities.getValueTextField(valueText);

        Boolean customizeFullTextSearch;
        customizeFullTextSearch = securitySettings.getCustomizeFullTextSearch();
        valueText = (customizeFullTextSearch != null) ? Boolean.toString(customizeFullTextSearch) : null;
        JTextField customizeFullTextSearchTextField = GUIUtilities.getValueTextField(valueText);

        // 1st Row
        securitySettingsPanel.add(encryptionSearchInterNodeCommunicationsLabel, gbc1);
        securitySettingsPanel.add(encryptionSearchInterNodeCommunicationsTextField, gbc2);

        // 2nd Row
        securitySettingsPanel.add(displayPropertiesWithAccessControlPoliciesLabel, gbc3);
        securitySettingsPanel.add(displayPropertiesWithAccessControlPoliciesTextField, gbc4);

        // 3rd Row
        securitySettingsPanel.add(customizeFullTextSearchLabel, gbc5);
        securitySettingsPanel.add(customizeFullTextSearchTextField, gbc6);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        securitySettingsPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Security Settings"));

        return securitySettingsPanel;
    }

    private JPanel getSearchIndexHostNodeSettingSetPanel(TreeSet<HostNodeSetting> searchIndexHostNodeSettingSet) {

        JPanel searchIndexHostNodeSettingSetPanel = new JPanel();

        searchIndexHostNodeSettingSetPanel.setLayout(new GridBagLayout());

        // 1st Row
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        HostNodeSettingTableModel hostNodeSettingTableModel;
        hostNodeSettingTableModel = new HostNodeSettingTableModel(searchIndexHostNodeSettingSet);

        DataTable<HostNodeSetting, HostNodeSettingTableColumn> hostNodeSettingTable;
        hostNodeSettingTable = new DataTable<>(hostNodeSettingTableModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(hostNodeSettingTable);

        scrollPane.setPreferredSize(hostNodeSettingTable.getPreferredSize());

        searchIndexHostNodeSettingSetPanel.add(scrollPane, gbc1);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        searchIndexHostNodeSettingSetPanel
                .setBorder(BorderFactory.createTitledBorder(loweredEtched, "Search Index Host Node Setting"));

        return searchIndexHostNodeSettingSetPanel;
    }
}
