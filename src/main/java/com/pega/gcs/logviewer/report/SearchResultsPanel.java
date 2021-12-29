
package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;

public class SearchResultsPanel extends JPanel {

    private static final long serialVersionUID = 2264229776786716687L;

    private LogTableModel logTableModel;

    private LogReportTableModel logReportTableModel;

    private NavigationTableController<LogEntryKey> navigationTableController;

    private Component parent;

    private JButton exportToTsvButton;

    private JTable logReportTable;

    public SearchResultsPanel(LogTableModel logTableModel,
            NavigationTableController<LogEntryKey> navigationTableController, Component parent) {

        this.logTableModel = logTableModel;
        this.navigationTableController = navigationTableController;
        this.parent = parent;

        SearchModel<LogEntryKey> searchModel = logTableModel.getSearchModel();
        Object searchStrObj = searchModel.getSearchStrObj();
        List<LogEntryKey> logKeyList = searchModel.getSearchResultList(searchStrObj);

        logReportTableModel = new LogReportTableModel(logKeyList, logTableModel);

        setLayout(new BorderLayout());

        JPanel controlsPanel = getControlsPanel();

        JTable logReportTable = getLogReportTable();

        JScrollPane scrollpane = new JScrollPane(logReportTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(controlsPanel, BorderLayout.NORTH);

        add(scrollpane, BorderLayout.CENTER);
    }

    private LogTableModel getLogTableModel() {
        return logTableModel;
    }

    private NavigationTableController<LogEntryKey> getNavigationTableController() {
        return navigationTableController;
    }

    private JButton getExportToTsvButton() {

        if (exportToTsvButton == null) {
            exportToTsvButton = new JButton("Export table as TSV");

            Dimension size = new Dimension(200, 26);
            exportToTsvButton.setPreferredSize(size);

            exportToTsvButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LogTableModel logTableModel = getLogTableModel();

                    JTable logReportTable = getLogReportTable();

                    LogReportTableModel logReportTableModel;
                    logReportTableModel = (LogReportTableModel) logReportTable.getModel();

                    LogReportExportDialog logReportExportDialog;
                    logReportExportDialog = new LogReportExportDialog(logTableModel, logReportTableModel,
                            "SearchResults", BaseFrame.getAppIcon(), parent);
                    logReportExportDialog.setVisible(true);
                }
            });

        }

        return exportToTsvButton;
    }

    private JTable getLogReportTable() {

        if (logReportTable == null) {

            logReportTable = new JTable(logReportTableModel);

            logReportTable.setRowHeight(22);
            logReportTable.setFillsViewportHeight(true);
            logReportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            logReportTable.setRowSelectionAllowed(true);
            logReportTable.setAutoCreateColumnsFromModel(false);

            TableColumnModel tableColumnModel = getLogReportTableColumnModel(logReportTableModel);

            logReportTable.setColumnModel(tableColumnModel);

            // setup header
            JTableHeader tableHeader = logReportTable.getTableHeader();

            tableHeader.setReorderingAllowed(false);

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

            ListSelectionModel listSelectionModel = logReportTable.getSelectionModel();
            listSelectionModel.addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent listSelectionEvent) {

                    if (!listSelectionEvent.getValueIsAdjusting()) {

                        int row = logReportTable.getSelectedRow();

                        LogReportTableModel logReportTableModel;
                        logReportTableModel = (LogReportTableModel) logReportTable.getModel();

                        LogEntryKey logEntryKey = logReportTableModel.getLogEntryKey(row);

                        NavigationTableController<LogEntryKey> navigationTableController;
                        navigationTableController = getNavigationTableController();

                        navigationTableController.scrollToKey(logEntryKey);
                    }

                }
            });

            logReportTable.setTransferHandler(new TransferHandler() {

                private static final long serialVersionUID = 2038719975478536824L;

                @Override
                protected Transferable createTransferable(JComponent component) {

                    int[] selectedRows = logReportTable.getSelectedRows();

                    StringBuilder dataSB = new StringBuilder();

                    if (selectedRows != null) {

                        LogReportTableModel logReportTableModel;
                        logReportTableModel = (LogReportTableModel) logReportTable.getModel();

                        int columnCount = logReportTableModel.getColumnCount();

                        for (int selectedRow : selectedRows) {

                            LogEntry logEntry = (LogEntry) logReportTableModel.getValueAt(selectedRow, 0);

                            if (logEntry != null) {

                                for (int column = 0; column < columnCount; column++) {

                                    String selectedRowData = logReportTableModel.getColumnValue(logEntry, selectedRow,
                                            column);
                                    dataSB.append(selectedRowData);
                                    dataSB.append("\t");
                                }
                            }

                            dataSB.append(System.getProperty("line.separator"));

                        }
                    }

                    return new StringSelection(dataSB.toString());
                }

                @Override
                public int getSourceActions(JComponent component) {
                    return TransferHandler.COPY;
                }

            });
        }

        return logReportTable;
    }

    private JPanel getControlsPanel() {

        JPanel controlsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(controlsPanel, BoxLayout.X_AXIS);
        controlsPanel.setLayout(layout);

        String text = "List of current search results. Select an entry to select the record on the main table.";
        JPanel labelJPanel = getLabelPanel(text);

        JButton exportToTsvButton = getExportToTsvButton();

        Dimension startDim = new Dimension(20, 40);

        controlsPanel.add(Box.createRigidArea(startDim));
        controlsPanel.add(labelJPanel);
        controlsPanel.add(Box.createRigidArea(startDim));
        controlsPanel.add(exportToTsvButton);
        controlsPanel.add(Box.createRigidArea(startDim));
        controlsPanel.add(Box.createHorizontalGlue());
        controlsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        return controlsPanel;
    }

    private JPanel getLabelPanel(String text) {

        JPanel labelPanel = new JPanel();

        LayoutManager layout = new BoxLayout(labelPanel, BoxLayout.LINE_AXIS);
        labelPanel.setLayout(layout);

        JLabel label = new JLabel(text);

        int height = 40;

        Dimension spacer = new Dimension(10, height);
        labelPanel.add(Box.createRigidArea(spacer));
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(spacer));

        return labelPanel;

    }

    private TableColumnModel getLogReportTableColumnModel(LogReportTableModel logReportTableModel) {

        TableColumnModel tableColumnModel = new DefaultTableColumnModel();

        for (int i = 0; i < logReportTableModel.getColumnCount(); i++) {

            TableColumn tableColumn = new TableColumn(i);

            LogEntryColumn logEntryColumn = logReportTableModel.getColumn(i);

            int horizontalAlignment = logEntryColumn.getHorizontalAlignment();
            int prefColumnWidth = logEntryColumn.getPrefColumnWidth();

            tableColumn.setHeaderValue(logEntryColumn.getDisplayName());
            tableColumn.setPreferredWidth(prefColumnWidth);
            tableColumn.setWidth(prefColumnWidth);

            DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

                private static final long serialVersionUID = 5731474707446644101L;

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {

                    String text = null;
                    Color foregroundColor = Color.BLACK;
                    Color backgroundColor = null;

                    if ((value != null) && (value instanceof LogEntry)) {

                        LogEntry le = (LogEntry) value;

                        LogReportTableModel logReportTableModel;

                        logReportTableModel = (LogReportTableModel) table.getModel();

                        // passing row, for delta calculation
                        text = logReportTableModel.getColumnValue(le, row, column);

                        foregroundColor = le.getForegroundColor();
                        backgroundColor = le.getBackgroundColor();

                    }

                    super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

                    if (!table.isRowSelected(row)) {
                        setForeground(foregroundColor);
                        setBackground(backgroundColor);
                    }

                    setBorder(new EmptyBorder(1, 5, 1, 5));

                    setToolTipText(text);

                    return this;
                }

            };

            dtcr.setBorder(new EmptyBorder(1, 3, 1, 1));
            dtcr.setHorizontalAlignment(horizontalAlignment);

            tableColumn.setCellRenderer(dtcr);
            tableColumn.setResizable(true);

            tableColumnModel.addColumn(tableColumn);
        }

        return tableColumnModel;
    }
}
