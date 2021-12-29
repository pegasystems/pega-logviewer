
package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.HazelcastMembership;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class HazelcastMembershipReportPanel extends JPanel {

    private static final long serialVersionUID = 3718159068780758903L;

    private LogTableModel logTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private JList<HazelcastMembership> hazelcastMembershipList;

    private HashMap<String, JComponent> hazelcastMembershipPanelMap;

    private JPanel hazelcastMembershipCardPanel;

    public HazelcastMembershipReportPanel(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController) {

        super();

        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;
        this.hazelcastMembershipPanelMap = new HashMap<>();

        setLayout(new BorderLayout());

        JList<HazelcastMembership> hazelcastMembershipList = getHazelcastMembershipList();
        JPanel hazelcastMembershipCardPanel = getHazelcastMembershipCardPanel();

        JScrollPane systemStartListScrollPane = new JScrollPane(hazelcastMembershipList);

        JSplitPane systemStartSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, systemStartListScrollPane,
                hazelcastMembershipCardPanel);

        systemStartSplitPane.setDividerLocation(500);

        add(systemStartSplitPane, BorderLayout.CENTER);
    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
    }

    private NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private HashMap<String, JComponent> getHazelcastMembershipPanelMap() {
        return hazelcastMembershipPanelMap;
    }

    private JList<HazelcastMembership> getHazelcastMembershipList() {

        if (hazelcastMembershipList == null) {

            LogTableModel logTableModel = getLogTableModel();

            final Log4jLogEntryModel log4jLogEntryModel;
            log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

            List<HazelcastMembership> hzMembershipList = log4jLogEntryModel.getHazelcastMembershipList();

            DefaultListModel<HazelcastMembership> dlm = new DefaultListModel<HazelcastMembership>();

            for (HazelcastMembership hazelcastMembership : hzMembershipList) {
                dlm.addElement(hazelcastMembership);
            }

            hazelcastMembershipList = new JList<HazelcastMembership>(dlm);
            hazelcastMembershipList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ListSelectionListener lsl = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    JList<HazelcastMembership> hazelcastMembershipList = getHazelcastMembershipList();

                    int selectedIndex = hazelcastMembershipList.getSelectedIndex();

                    if ((!listSelectionEvent.getValueIsAdjusting()) && (selectedIndex != -1)) {

                        HazelcastMembership hazelcastMembership = hazelcastMembershipList.getSelectedValue();

                        switchHazelcastMembershipPanel(hazelcastMembership);

                    }
                }
            };

            hazelcastMembershipList.addListSelectionListener(lsl);

            hazelcastMembershipList.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (mouseEvent.getClickCount() == 2) {

                        JList<HazelcastMembership> hazelcastMembershipList = getHazelcastMembershipList();

                        int clickedIndex = hazelcastMembershipList.locationToIndex(mouseEvent.getPoint());

                        HazelcastMembership systemStart;
                        systemStart = hazelcastMembershipList.getModel().getElementAt(clickedIndex);

                        LogEntryKey logEntryKey = systemStart.getBeginKey();

                        NavigationTableController<LogEntryKey> navigationTableController;
                        navigationTableController = getNavigationTableController();

                        navigationTableController.scrollToKey(logEntryKey);

                    } else {
                        super.mouseClicked(mouseEvent);
                    }
                }

            });

            DefaultListCellRenderer dlcr = new DefaultListCellRenderer() {

                private static final long serialVersionUID = 8776495897305233378L;

                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    HazelcastMembership hazelcastMembership = (HazelcastMembership) value;

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    if (!isSelected) {

                        if (hazelcastMembership.isError()) {
                            setForeground(Color.RED);
                        }
                    }

                    setBorder(new EmptyBorder(1, 10, 1, 1));

                    String displayString = hazelcastMembership.getDisplayString(log4jLogEntryModel);

                    setText(displayString);
                    setToolTipText(displayString);

                    return this;
                }

            };

            hazelcastMembershipList.setCellRenderer(dlcr);

            hazelcastMembershipList.setFixedCellHeight(20);
        }

        return hazelcastMembershipList;
    }

    private JPanel getHazelcastMembershipCardPanel() {

        if (hazelcastMembershipCardPanel == null) {
            hazelcastMembershipCardPanel = new JPanel(new CardLayout());

            hazelcastMembershipCardPanel.add("", new JPanel());
        }

        return hazelcastMembershipCardPanel;
    }

    private void switchHazelcastMembershipPanel(HazelcastMembership hazelcastMembership) {

        JPanel hazelcastMembershipCardPanel = getHazelcastMembershipCardPanel();

        HashMap<String, JComponent> hazelcastMembershipPanelMap;
        hazelcastMembershipPanelMap = getHazelcastMembershipPanelMap();

        Integer index = hazelcastMembership.getIndex();
        String name = index.toString();

        JComponent hazelcastMembershipPanel = hazelcastMembershipPanelMap.get(name);

        if (hazelcastMembershipPanel == null) {

            hazelcastMembershipPanel = new HazelcastMembershipPanel(hazelcastMembership);

            hazelcastMembershipPanelMap.put(name, hazelcastMembershipPanel);
            hazelcastMembershipCardPanel.add(name, hazelcastMembershipPanel);
        }

        CardLayout cardLayout = (CardLayout) (hazelcastMembershipCardPanel.getLayout());

        cardLayout.show(hazelcastMembershipCardPanel, name);
    }
}
