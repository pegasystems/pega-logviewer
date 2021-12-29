
package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.SystemStart;
import com.pega.gcs.logviewer.parser.LogSystemStartParser;

public class SystemStartPanel extends JPanel {

    private static final long serialVersionUID = 3718159068780758903L;

    private LogTableModel logTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private JList<SystemStart> systemStartList;

    private JTable systemStartTable;

    public SystemStartPanel(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController) {

        super();

        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;

        setLayout(new BorderLayout());

        JList<SystemStart> systemStartList = getSystemStartList();
        JTable systemStartTable = getSystemStartTable();

        JScrollPane systemStartListScrollPane = new JScrollPane(systemStartList);
        JScrollPane systemStartTableScrollPane = new JScrollPane(systemStartTable);

        JSplitPane systemStartSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, systemStartListScrollPane,
                systemStartTableScrollPane);

        systemStartSplitPane.setDividerLocation(400);

        add(systemStartSplitPane, BorderLayout.CENTER);
    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
    }

    private NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private JList<SystemStart> getSystemStartList() {

        if (systemStartList == null) {

            LogTableModel logTableModel = getLogTableModel();

            final Log4jLogEntryModel log4jLogEntryModel;
            log4jLogEntryModel = (Log4jLogEntryModel) logTableModel.getLogEntryModel();

            List<SystemStart> ssList = log4jLogEntryModel.getSystemStartList();

            DefaultListModel<SystemStart> dlm = new DefaultListModel<SystemStart>();

            for (SystemStart systemStart : ssList) {
                dlm.addElement(systemStart);
            }

            systemStartList = new JList<SystemStart>(dlm);
            systemStartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            ListSelectionListener lsl = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    JList<SystemStart> systemStartList = getSystemStartList();

                    int selectedIndex = systemStartList.getSelectedIndex();

                    if ((!listSelectionEvent.getValueIsAdjusting()) && (selectedIndex != -1)) {

                        SystemStart systemStart = systemStartList.getSelectedValue();

                        LogSystemStartParser logSystemStartParser = LogSystemStartParser.getInstance();

                        Map<String, String> systemStartMap;
                        systemStartMap = logSystemStartParser.getSystemStartMap(systemStart, log4jLogEntryModel);

                        DefaultTableModel dtm = new DefaultTableModel(new String[] { "Key", "Value" }, 0);

                        for (Map.Entry<String, String> entrySet : systemStartMap.entrySet()) {

                            dtm.addRow(new String[] { entrySet.getKey(), entrySet.getValue() });
                        }

                        JTable systemStartTable = getSystemStartTable();
                        systemStartTable.setModel(dtm);

                        TableColumnModel tcm = systemStartTable.getColumnModel();

                        for (int column = 0; column < dtm.getColumnCount(); column++) {

                            TableColumn tc = tcm.getColumn(column);
                            DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();

                            if (column == 0) {
                                tc.setPreferredWidth(280);
                                tc.setWidth(270);
                            } else {
                                tc.setPreferredWidth(500);
                                tc.setWidth(450);
                            }

                            tc.setCellRenderer(dtcr);
                        }

                        systemStartTable.updateUI();
                    }
                }
            };

            systemStartList.addListSelectionListener(lsl);

            systemStartList.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (mouseEvent.getClickCount() == 2) {

                        JList<SystemStart> systemStartList = getSystemStartList();

                        int clickedIndex = systemStartList.locationToIndex(mouseEvent.getPoint());

                        SystemStart systemStart;
                        systemStart = systemStartList.getModel().getElementAt(clickedIndex);

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

                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                    setBorder(new EmptyBorder(1, 10, 1, 1));

                    SystemStart systemStart = (SystemStart) value;

                    String displayString = systemStart.getDisplayString(log4jLogEntryModel);

                    setText(displayString);
                    setToolTipText(displayString);

                    return this;
                }

            };

            systemStartList.setCellRenderer(dlcr);

            systemStartList.setFixedCellHeight(20);
        }

        return systemStartList;
    }

    private JTable getSystemStartTable() {

        if (systemStartTable == null) {

            DefaultTableModel dtm = new DefaultTableModel();

            systemStartTable = new JTable(dtm);

            systemStartTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            systemStartTable.setRowHeight(20);

            JTableHeader tableHeader = systemStartTable.getTableHeader();

            DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
            dtcr.setHorizontalAlignment(SwingConstants.CENTER);

            Font existingFont = tableHeader.getFont();
            String existingFontName = existingFont.getName();
            int existFontSize = existingFont.getSize();
            Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
            tableHeader.setFont(newFont);

            tableHeader.setReorderingAllowed(false);

        }

        return systemStartTable;
    }

    private DefaultTableCellRenderer getDefaultTableCellRenderer() {

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 7231010655893711987L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(new EmptyBorder(1, 5, 1, 1));

                if (!isSelected) {

                    if ((row % 2) == 0) {
                        setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
                    } else {
                        setBackground(Color.WHITE);
                    }
                }
                return this;
            }

        };

        return dtcr;
    }
}
