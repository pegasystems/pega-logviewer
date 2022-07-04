
package com.pega.gcs.logviewer.systemstate.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.datatable.DataTablePanel;
import com.pega.gcs.logviewer.systemstate.model.Link;
import com.pega.gcs.logviewer.systemstate.model.RequestorsResult;
import com.pega.gcs.logviewer.systemstate.table.RequestorPoolTableModel;

public class RequestorResultPanel extends JPanel {

    private static final long serialVersionUID = 3642081217750700205L;

    public RequestorResultPanel(RequestorsResult requestorsResult) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 1.0D;
        gbc1.weighty = 0.0D;
        gbc1.fill = GridBagConstraints.BOTH;
        gbc1.anchor = GridBagConstraints.NORTHWEST;
        gbc1.insets = new Insets(2, 5, 2, 0);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        gbc2.weightx = 1.0D;
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 5, 2, 0);

        JPanel linksPanel = getLinksJPanel(requestorsResult);

        RequestorPoolTableModel requestorPoolTableModel;
        requestorPoolTableModel = new RequestorPoolTableModel(requestorsResult);

        DataTablePanel requestorPoolTablePanel = new DataTablePanel(requestorPoolTableModel, false, "RequestorPool",
                this);

        add(linksPanel, gbc1);
        add(requestorPoolTablePanel, gbc2);
    }

    private JPanel getLinksJPanel(RequestorsResult requestorsResult) {

        JPanel linksPanel = new JPanel();

        linksPanel.setLayout(new GridBagLayout());

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
        gbc2.weightx = 0.0D;
        gbc2.weighty = 0.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(10, 5, 5, 0);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 2;
        gbc3.gridy = 0;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(10, 5, 5, 0);

        // 2nd Row
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 1;
        gbc4.weightx = 0.0D;
        gbc4.weighty = 0.0D;
        gbc4.fill = GridBagConstraints.BOTH;
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.gridx = 1;
        gbc5.gridy = 1;
        gbc5.weightx = 0.0D;
        gbc5.weighty = 0.0D;
        gbc5.fill = GridBagConstraints.BOTH;
        gbc5.anchor = GridBagConstraints.NORTHWEST;
        gbc5.insets = new Insets(5, 5, 5, 0);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.gridx = 2;
        gbc6.gridy = 1;
        gbc6.weightx = 1.0D;
        gbc6.weighty = 0.0D;
        gbc6.fill = GridBagConstraints.BOTH;
        gbc6.anchor = GridBagConstraints.NORTHWEST;
        gbc6.insets = new Insets(5, 5, 5, 0);

        // 3rd Row
        GridBagConstraints gbc7 = new GridBagConstraints();
        gbc7.gridx = 0;
        gbc7.gridy = 2;
        gbc7.weightx = 0.0D;
        gbc7.weighty = 0.0D;
        gbc7.fill = GridBagConstraints.BOTH;
        gbc7.anchor = GridBagConstraints.NORTHWEST;
        gbc7.insets = new Insets(5, 5, 10, 0);

        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.gridx = 1;
        gbc8.gridy = 2;
        gbc8.weightx = 0.0D;
        gbc8.weighty = 0.0D;
        gbc8.fill = GridBagConstraints.BOTH;
        gbc8.anchor = GridBagConstraints.NORTHWEST;
        gbc8.insets = new Insets(5, 5, 10, 0);

        GridBagConstraints gbc9 = new GridBagConstraints();
        gbc9.gridx = 2;
        gbc9.gridy = 2;
        gbc9.weightx = 1.0D;
        gbc9.weighty = 0.0D;
        gbc9.fill = GridBagConstraints.BOTH;
        gbc9.anchor = GridBagConstraints.NORTHWEST;
        gbc9.insets = new Insets(5, 5, 10, 0);

        Link selfLink = requestorsResult.getSelfLink();
        Link clearASinglePoolLink = requestorsResult.getClearASinglePoolLink();
        Link clearAllPoolsLink = requestorsResult.getClearAllPoolsLink();

        JLabel selfLinkLabel = new JLabel(selfLink.getName());
        JTextField selfLinkMethodTextField = GUIUtilities.getValueTextField(selfLink.getMethod());
        JTextField selfLinkHrefTextField = GUIUtilities.getValueTextField(selfLink.getHref());

        JLabel clearASinglePoolLinkLabel = new JLabel(clearASinglePoolLink.getName());
        JTextField clearASinglePoolLinkMethodTextField;
        clearASinglePoolLinkMethodTextField = GUIUtilities.getValueTextField(clearASinglePoolLink.getMethod());
        JTextField clearASinglePoolLinkHrefTextField;
        clearASinglePoolLinkHrefTextField = GUIUtilities.getValueTextField(clearASinglePoolLink.getHref());

        JLabel clearAllPoolsLinkLabel = new JLabel(clearAllPoolsLink.getName());
        JTextField clearAllPoolsLinkMethodTextField = GUIUtilities.getValueTextField(clearAllPoolsLink.getMethod());
        JTextField clearAllPoolsLinkHrefTextField = GUIUtilities.getValueTextField(clearAllPoolsLink.getHref());

        // 1st Row
        linksPanel.add(selfLinkLabel, gbc1);
        linksPanel.add(selfLinkMethodTextField, gbc2);
        linksPanel.add(selfLinkHrefTextField, gbc3);

        // 2nd Row
        linksPanel.add(clearASinglePoolLinkLabel, gbc4);
        linksPanel.add(clearASinglePoolLinkMethodTextField, gbc5);
        linksPanel.add(clearASinglePoolLinkHrefTextField, gbc6);

        // 3rd Row
        linksPanel.add(clearAllPoolsLinkLabel, gbc7);
        linksPanel.add(clearAllPoolsLinkMethodTextField, gbc8);
        linksPanel.add(clearAllPoolsLinkHrefTextField, gbc9);

        linksPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return linksPanel;

    }

}
