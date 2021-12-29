
package com.pega.gcs.logviewer.report;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTable;
import com.pega.gcs.logviewer.model.HazelcastMemberInfo;
import com.pega.gcs.logviewer.model.HazelcastMembership;
import com.pega.gcs.logviewer.model.HazelcastMembership.HzMembershipEvent;

public class HazelcastMembershipPanel extends JPanel {

    private static final long serialVersionUID = -5877263326196717203L;

    public HazelcastMembershipPanel(HazelcastMembership hazelcastMembership) {

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
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(0, 0, 0, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 1.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(0, 0, 0, 0);

        JPanel titlePanel = getTitlePanel(hazelcastMembership);

        JPanel hazelcastMemberInfoPanel = getHazelcastMemberInfoPanel(hazelcastMembership.getHazelcastMemberInfo());

        JPanel hazelcastMemberInfoListPanel = getHazelcastMemberInfoListPanel(
                hazelcastMembership.getHazelcastMemberInfoList());

        add(titlePanel, gbc1);
        add(hazelcastMemberInfoPanel, gbc2);
        add(hazelcastMemberInfoListPanel, gbc3);

    }

    private JPanel getTitlePanel(HazelcastMembership hazelcastMembership) {

        JPanel titlePanel = new JPanel();

        LayoutManager layout = new BoxLayout(titlePanel, BoxLayout.X_AXIS);
        titlePanel.setLayout(layout);

        StringBuilder sb = new StringBuilder();

        HzMembershipEvent hzMembershipEvent = hazelcastMembership.getHzMembershipEvent();
        int memberCount = hazelcastMembership.getMemberCount();

        switch (hzMembershipEvent) {
        case MEMBER_ADDED:
            sb.append("Member Added");
            break;
        case MEMBER_LEFT:
            sb.append("Member Left");
            break;
        default:
            break;
        }

        sb.append(" - Members [");
        sb.append(memberCount);
        sb.append("]");

        JLabel titleLabel = new JLabel(sb.toString());
        Font labelFont = titleLabel.getFont();
        Font titleFont = labelFont.deriveFont(Font.BOLD, 14);
        titleLabel.setFont(titleFont);

        Dimension dim = new Dimension(10, 40);

        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());

        return titlePanel;
    }

    private JPanel getHazelcastMemberInfoPanel(HazelcastMemberInfo hazelcastMemberInfo) {

        JPanel hazelcastMemberInfoPanel = new JPanel();

        hazelcastMemberInfoPanel.setLayout(new GridBagLayout());

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

        // 5th Row
        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 0;
        gbc9.gridy = 4;
        gbc9.weightx = 0.0D;
        gbc9.weighty = 0.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc10 = new GridBagConstraints();
        gbc10.gridx = 1;
        gbc10.gridy = 4;
        gbc10.weightx = 1.0D;
        gbc10.weighty = 0.0D;
        gbc10.fill = GridBagConstraints.BOTH;
        gbc10.anchor = GridBagConstraints.NORTHWEST;
        gbc10.insets = new Insets(5, 5, 5, 5);
        JLabel nameLabel = new JLabel("Name");
        JLabel hostnameLabel = new JLabel("Hostname");
        JLabel clusterAddressLabel = new JLabel("Cluster Address");
        JLabel uuidLabel = new JLabel("UUID");
        JLabel operatingModeLabel = new JLabel("Operating Mode");

        String valueText;

        valueText = hazelcastMemberInfo.getName();
        JTextField nameTextField = GUIUtilities.getValueTextField(valueText);

        valueText = hazelcastMemberInfo.getHostname();
        JTextField hostnameTextField = GUIUtilities.getValueTextField(valueText);

        valueText = hazelcastMemberInfo.getClusterAddress();
        JTextField clusterAddressTextField = GUIUtilities.getValueTextField(valueText);

        valueText = hazelcastMemberInfo.getUuid();
        JTextField uuidTextField = GUIUtilities.getValueTextField(valueText);

        valueText = hazelcastMemberInfo.getOperatingMode();
        JTextField operatingModeTextField = GUIUtilities.getValueTextField(valueText);

        // 1st Row
        hazelcastMemberInfoPanel.add(nameLabel, gbc1);
        hazelcastMemberInfoPanel.add(nameTextField, gbc2);

        // 2nd Row
        hazelcastMemberInfoPanel.add(hostnameLabel, gbc3);
        hazelcastMemberInfoPanel.add(hostnameTextField, gbc4);

        // 3rd Row
        hazelcastMemberInfoPanel.add(clusterAddressLabel, gbc5);
        hazelcastMemberInfoPanel.add(clusterAddressTextField, gbc6);

        // 4th Row
        hazelcastMemberInfoPanel.add(uuidLabel, gbc7);
        hazelcastMemberInfoPanel.add(uuidTextField, gbc8);

        // 5th Row
        hazelcastMemberInfoPanel.add(operatingModeLabel, gbc9);
        hazelcastMemberInfoPanel.add(operatingModeTextField, gbc10);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        hazelcastMemberInfoPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, " Event Member "));

        return hazelcastMemberInfoPanel;
    }

    private JPanel getHazelcastMemberInfoListPanel(List<HazelcastMemberInfo> hazelcastMemberInfoList) {

        JPanel hazelcastMemberInfoListPanel = new JPanel();

        hazelcastMemberInfoListPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 1.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(10, 5, 5, 0);

        HazelcastMembershipTableModel hazelcastMembershipTableModel;
        hazelcastMembershipTableModel = new HazelcastMembershipTableModel(hazelcastMemberInfoList);

        DataTable<HazelcastMemberInfo, HazelcastMembershipTableColumn> hazelcastMembershipTable;
        hazelcastMembershipTable = new DataTable<>(hazelcastMembershipTableModel, JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(hazelcastMembershipTable);

        scrollPane.setPreferredSize(hazelcastMembershipTable.getPreferredSize());

        hazelcastMemberInfoListPanel.add(scrollPane, gbc1);

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        hazelcastMemberInfoListPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, " Members List "));

        return hazelcastMemberInfoListPanel;
    }

}
