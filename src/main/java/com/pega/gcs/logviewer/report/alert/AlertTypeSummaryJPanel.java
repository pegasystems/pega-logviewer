/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.report.alert;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.text.WordUtils;

import com.pega.gcs.fringecommon.guiutilities.MyColor;
import com.pega.gcs.fringecommon.guiutilities.NoteJPanel;
import com.pega.gcs.fringecommon.guiutilities.RightClickMenuItem;
import com.pega.gcs.logviewer.model.LogSeriesCollection;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertTypeSummaryJPanel extends JPanel {

    private static final long serialVersionUID = 8546540684238760817L;

    private AlertBoxAndWhiskerTableModel alertBoxAndWhiskerTableModel;

    private List<JCheckBox> alertTypeJCheckBoxList;

    private JTable alertTypeJTable;

    private JTabbedPane reportJTabbedPane;

    private Map<String, JPanel> alertMessageTabComponentMap;

    public AlertTypeSummaryJPanel(Set<LogSeriesCollection> logSeriesCollectionSet, NumberFormat numberFormat,
            JTabbedPane reportJTabbedPane, Map<String, JPanel> alertMessageTabComponentMap) {

        super();

        this.reportJTabbedPane = reportJTabbedPane;
        this.alertMessageTabComponentMap = alertMessageTabComponentMap;

        alertBoxAndWhiskerTableModel = new AlertBoxAndWhiskerTableModel(logSeriesCollectionSet, numberFormat);

        setLayout(new GridBagLayout());

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
        gbc2.weighty = 1.0D;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.insets = new Insets(2, 2, 2, 2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        gbc3.weightx = 1.0D;
        gbc3.weighty = 0.0D;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.insets = new Insets(2, 2, 2, 2);

        JPanel controlsJPanel = getControlsJPanel();

        JTable alertTypeJTable = getAlertTypeJTable();
        JScrollPane alertTypeJTableJScrollPane = new JScrollPane(alertTypeJTable);

        String noteText = "Right click on a row to open specific alert tab.";
        NoteJPanel noteJPanel = new NoteJPanel(noteText, 1);

        add(controlsJPanel, gbc1);
        add(alertTypeJTableJScrollPane, gbc2);
        add(noteJPanel, gbc3);
    }

    private List<JCheckBox> getAlertTypeJCheckBoxList() {

        if (alertTypeJCheckBoxList == null) {
            alertTypeJCheckBoxList = new ArrayList<>();

            Set<String> alertMessageTypeSet = alertBoxAndWhiskerTableModel.getAlertMessageTypeSet();

            for (String alertMessageType : alertMessageTypeSet) {

                JCheckBox alertTypeJCheckBox = getAlertTypeJCheckBox(alertMessageType);

                alertTypeJCheckBoxList.add(alertTypeJCheckBox);
            }
        }

        return alertTypeJCheckBoxList;
    }

    private JCheckBox getAlertTypeJCheckBox(String alertMessageType) {

        String text = WordUtils.capitalizeFully(alertMessageType);

        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setActionCommand(alertMessageType);

        checkBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent itemEvent) {

                List<JCheckBox> alertTypeJCheckBoxList = getAlertTypeJCheckBoxList();

                List<String> selectedAlertTypeList = new ArrayList<String>();

                for (JCheckBox alertTypeJCheckBox : alertTypeJCheckBoxList) {

                    if (alertTypeJCheckBox.isSelected()) {

                        String alertType = alertTypeJCheckBox.getActionCommand();
                        selectedAlertTypeList.add(alertType);
                    }
                }

                JTable alertTypeJTable = getAlertTypeJTable();

                AlertBoxAndWhiskerTableModel alertBoxAndWhiskerTableModel;
                alertBoxAndWhiskerTableModel = (AlertBoxAndWhiskerTableModel) alertTypeJTable.getModel();

                alertBoxAndWhiskerTableModel.applyFilter(selectedAlertTypeList);
            }
        });

        return checkBox;
    }

    private JTable getAlertTypeJTable() {

        if (alertTypeJTable == null) {

            alertTypeJTable = new JTable(alertBoxAndWhiskerTableModel);

            alertTypeJTable.setRowHeight(24);
            alertTypeJTable.setFillsViewportHeight(true);
            alertTypeJTable.setRowSelectionAllowed(true);
            alertTypeJTable.setAutoCreateColumnsFromModel(false);
            // alertTypeJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            alertTypeJTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

            TableColumnModel tableColumnModel = getTableColumnModel(alertBoxAndWhiskerTableModel);

            alertTypeJTable.setColumnModel(tableColumnModel);

            // setup header
            JTableHeader tableHeader = alertTypeJTable.getTableHeader();

            // tableHeader.setReorderingAllowed(false);

            final TableCellRenderer origTableCellRenderer = tableHeader.getDefaultRenderer();

            DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

                private static final long serialVersionUID = -5411641633512120668L;

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {

                    JLabel origComponent = (JLabel) origTableCellRenderer.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);

                    origComponent.setHorizontalAlignment(CENTER);

                    // set header height
                    Dimension dim = origComponent.getPreferredSize();
                    dim.setSize(dim.getWidth(), 30);
                    origComponent.setPreferredSize(dim);

                    return origComponent;
                }

            };

            tableHeader.setDefaultRenderer(dtcr);

            // bold the header
            Font existingFont = tableHeader.getFont();
            String existingFontName = existingFont.getName();
            int existFontSize = existingFont.getSize();
            Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
            tableHeader.setFont(newFont);

            alertTypeJTable.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {

                    if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                        Point point = mouseEvent.getPoint();

                        final JTable source = (JTable) mouseEvent.getSource();

                        final int selectedRow = source.rowAtPoint(point);

                        if (selectedRow != -1) {

                            // select the row first
                            source.setRowSelectionInterval(selectedRow, selectedRow);

                            AlertBoxAndWhiskerTableModel alertBoxAndWhiskerTableModel;
                            alertBoxAndWhiskerTableModel = (AlertBoxAndWhiskerTableModel) source.getModel();

                            String alertId = (String) alertBoxAndWhiskerTableModel.getValueAt(selectedRow, 0);

                            final JPopupMenu popupMenu = new JPopupMenu();
                            String menuItemStr = "Open '" + alertId + "' alert tab";

                            final RightClickMenuItem openAlertMenuItem = new RightClickMenuItem(menuItemStr);

                            openAlertMenuItem.addActionListener(new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent actionEvent) {

                                    JPanel tabComponent = alertMessageTabComponentMap.get(alertId);

                                    if (tabComponent != null) {
                                        reportJTabbedPane.setSelectedComponent(tabComponent);
                                    }

                                    popupMenu.setVisible(false);

                                }
                            });

                            popupMenu.add(openAlertMenuItem);

                            popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                        }

                    } else {
                        super.mouseClicked(mouseEvent);
                    }
                }
            });
        }

        return alertTypeJTable;
    }

    private TableColumnModel getTableColumnModel(AlertBoxAndWhiskerTableModel alertBoxAndWhiskerTableModel) {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        for (int i = 0; i < alertBoxAndWhiskerTableModel.getColumnCount(); i++) {

            AlertBoxAndWhiskerReportColumn alertBoxAndWhiskerReportColumn = alertBoxAndWhiskerTableModel.getColumn(i);

            TableColumn tableColumn = new TableColumn(i);

            DefaultTableCellRenderer dtcr = getDefaultTableCellRenderer();
            dtcr.setHorizontalAlignment(alertBoxAndWhiskerReportColumn.getHorizontalAlignment());

            int prefColumnWidth = alertBoxAndWhiskerReportColumn.getPrefColumnWidth();

            tableColumn.setHeaderValue(alertBoxAndWhiskerReportColumn.getDisplayName());
            tableColumn.setCellRenderer(dtcr);
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(prefColumnWidth);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }

    private DefaultTableCellRenderer getDefaultTableCellRenderer() {

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

            private static final long serialVersionUID = 1504347306097747771L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(new EmptyBorder(1, 8, 1, 10));

                if (!isSelected) {

                    AlertBoxAndWhiskerTableModel alertBoxAndWhiskerTableModel;
                    alertBoxAndWhiskerTableModel = (AlertBoxAndWhiskerTableModel) table.getModel();

                    String messageIdStr = (String) alertBoxAndWhiskerTableModel.getValueAt(row, 0);

                    AlertMessageListProvider alertMessageListProvider = AlertMessageListProvider.getInstance();
                    List<String> criticalAlertList = alertMessageListProvider.getCriticalAlertList();

                    if (criticalAlertList.contains(messageIdStr)) {
                        setForeground(Color.WHITE);
                        setBackground(MyColor.ERROR);
                    } else {
                        setForeground(Color.BLACK);
                        setBackground(MyColor.LIGHTEST_LIGHT_GRAY);
                    }

                }
                return this;
            }

        };

        return dtcr;
    }

    private JPanel getControlsJPanel() {

        JPanel controlsJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(controlsJPanel, BoxLayout.X_AXIS);
        controlsJPanel.setLayout(layout);

        Dimension dim = new Dimension(10, 40);

        // controlsJPanel.add(Box.createHorizontalGlue());
        controlsJPanel.add(Box.createRigidArea(dim));

        controlsJPanel.add(Box.createRigidArea(dim));

        List<JCheckBox> alertTypeJCheckBoxList = getAlertTypeJCheckBoxList();

        for (JCheckBox checkBox : alertTypeJCheckBoxList) {
            controlsJPanel.add(Box.createRigidArea(dim));
            controlsJPanel.add(checkBox);
        }

        controlsJPanel.add(Box.createRigidArea(dim));

        controlsJPanel.add(Box.createHorizontalGlue());

        Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        controlsJPanel.setBorder(BorderFactory.createTitledBorder(loweredEtched, "Filter"));

        return controlsJPanel;
    }
}
