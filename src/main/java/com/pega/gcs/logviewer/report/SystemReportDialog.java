/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.FilterTableModel;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainerPanel;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;

public abstract class SystemReportDialog extends JFrame implements TableModelListener {

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemReportDialog.class);

	private static final long serialVersionUID = 8438579888122942639L;

	private LogTableModel logTableModel;

	private NavigationTableController<Integer> navigationTableController;

	private JTabbedPane reportJTabbedPane;

	protected abstract void buildTabs();

	public SystemReportDialog(String title, LogTableModel logTableModel,
			NavigationTableController<Integer> navigationTableController, ImageIcon appIcon, Component parent) {

		super();

		this.logTableModel = logTableModel;
		this.navigationTableController = navigationTableController;

		logTableModel.addTableModelListener(this);

		setIconImage(appIcon.getImage());

		// setPreferredSize(new Dimension(1200, 600));

		setTitle(title);
		// setResizable(true);
		// setModalityType(ModalityType.MODELESS);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// setAlwaysOnTop(true);

		// visible should be the last step
		setVisible(false);
	}

	@Override
	public void tableChanged(TableModelEvent e) {

		if (e.getType() == TableModelEvent.UPDATE) {
			LOG.info("SystemReportDialog tableChanged");
			JTabbedPane reportJTabbedPane = getReportJTabbedPane();
			reportJTabbedPane.removeAll();
			buildTabs();

			// revalidate();
			validate();
			repaint();
		}
	}

	public void destroyFrame() {
		LogTableModel logTableModel = getLogTableModel();
		logTableModel.removeTableModelListener(this);
		setVisible(false);
	}

	protected JTabbedPane getReportJTabbedPane() {

		if (reportJTabbedPane == null) {
			reportJTabbedPane = new JTabbedPane();
		}

		return reportJTabbedPane;
	}

	protected LogTableModel getLogTableModel() {
		return logTableModel;
	}

	protected NavigationTableController<Integer> getNavigationTableController() {
		return navigationTableController;
	}

	protected JComponent getMainJPanel() {

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		JTabbedPane reportJTabbedPane = getReportJTabbedPane();
		mainJPanel.add(reportJTabbedPane);

		buildTabs();
		return mainJPanel;
	}

	protected void addTab(JComponent tabJComponent, String tabLabelText, Dimension labelDim) {

		JLabel tabLabel = new JLabel(tabLabelText);
		Font labelFont = tabLabel.getFont();
		Font tabFont = labelFont.deriveFont(Font.BOLD, 12);

		tabLabel.setFont(tabFont);
		tabLabel.setSize(labelDim);
		tabLabel.setPreferredSize(labelDim);
		tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JTabbedPane reportJTabbedPane = getReportJTabbedPane();
		reportJTabbedPane.addTab(tabLabelText, tabJComponent);

		int tabIndex = reportJTabbedPane.getTabCount() - 1;
		reportJTabbedPane.setTabComponentAt(tabIndex, tabLabel);
	}

	protected void addDefaultTabs() {
		LogTableModel logTableModel = getLogTableModel();

		BookmarkModel<Integer> bookmarkModel = logTableModel.getBookmarkModel();
		Object searchStrObj = logTableModel.getSearchModel().getSearchStrObj();

		boolean containsBookmark = bookmarkModel.getMarkerCount() > 0;
		boolean containsSearch = (searchStrObj != null) ? true : false;

		if (containsSearch) {
			JPanel searchEventJPanel = getSearchEventJPanel();
			String tabLabelText = "Search Results";
			Dimension labelDim = new Dimension(120, 26);
			addTab(searchEventJPanel, tabLabelText, labelDim);
		}

		if (containsBookmark) {

			JPanel bookmarkContainerPanel = getBookmarkContainerPanel();

			String tabLabelText = "Bookmarks";

			Dimension labelDim = new Dimension(70, 22);

			addTab(bookmarkContainerPanel, tabLabelText, labelDim);

		}
	}

	private JPanel getLabelJPanel(String text) {

		JPanel labelJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(labelJPanel, BoxLayout.LINE_AXIS);
		labelJPanel.setLayout(layout);

		JLabel label = new JLabel(text);

		int height = 30;

		Dimension spacer = new Dimension(10, height);
		labelJPanel.add(Box.createRigidArea(spacer));
		labelJPanel.add(label);
		labelJPanel.add(Box.createHorizontalGlue());

		labelJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		return labelJPanel;

	}

	private JPanel getSearchEventJPanel() {

		JPanel searchEventJPanel = new JPanel(new BorderLayout());

		String text = "List of current search results. Select an entry to select the record on the main table.";
		JPanel labelJPanel = getLabelJPanel(text);

		JTable searchEventJTable = getSearchEventJTable();

		JScrollPane searchEventJTableScrollPane = new JScrollPane(searchEventJTable);

		searchEventJPanel.add(labelJPanel, BorderLayout.NORTH);
		searchEventJPanel.add(searchEventJTableScrollPane, BorderLayout.CENTER);

		return searchEventJPanel;
	}

	private JTable getSearchEventJTable() {

		LogTableModel logTableModel = getLogTableModel();

		SearchModel<Integer> searchModel = logTableModel.getSearchModel();
		Object searchStrObj = searchModel.getSearchStrObj();
		List<Integer> searchEventList = searchModel.getSearchResultList(searchStrObj);

		JTable searchEventJTable = getLogReportTable(searchEventList);

		return searchEventJTable;
	}

	private JTable getLogReportTable(List<Integer> logKeyList) {

		LogTableModel logTableModel = getLogTableModel();

		LogReportTableModel logReportTableModel;
		logReportTableModel = new LogReportTableModel(logKeyList, logTableModel);

		JTable logReportTable = new JTable(logReportTableModel);

		logReportTable.setRowHeight(20);
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
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {

					int row = logReportTable.getSelectedRow();

					LogReportTableModel logReportTableModel;
					logReportTableModel = (LogReportTableModel) logReportTable.getModel();

					Integer logEntryKey = logReportTableModel.getLogEntryKey(row);

					NavigationTableController<Integer> navigationTableController;
					navigationTableController = getNavigationTableController();

					navigationTableController.scrollToKey(logEntryKey);
				}

			}
		});

		return logReportTable;
	}

	private TableColumnModel getLogReportTableColumnModel(LogReportTableModel logReportTableModel) {

		TableColumnModel tableColumnModel = new DefaultTableColumnModel();

		for (int i = 0; i < logReportTableModel.getColumnCount(); i++) {

			TableColumn tableColumn = new TableColumn(i);

			String text = logReportTableModel.getColumnName(i);

			tableColumn.setHeaderValue(text);

			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {

				private static final long serialVersionUID = 5731474707446644101L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {

					String text = null;

					if ((value != null) && (value instanceof LogEntry)) {

						LogEntry le = (LogEntry) value;

						LogReportTableModel logReportTableModel;

						logReportTableModel = (LogReportTableModel) table.getModel();

						text = logReportTableModel.getColumnValue(le, column);

						if (!table.isRowSelected(row)) {

							Color foregroundColor = Color.BLACK;
							Color backgroundColor = null;

							foregroundColor = le.getForegroundColor();
							backgroundColor = le.getBackgroundColor();

							setForeground(foregroundColor);
							setBackground(backgroundColor);
						}

						setBorder(new EmptyBorder(1, 3, 1, 1));

						setToolTipText(text);

						setHorizontalAlignment(CENTER);
					}

					super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);

					return this;
				}

			};

			dtcr.setBorder(new EmptyBorder(1, 3, 1, 1));

			tableColumn.setCellRenderer(dtcr);

			LogEntryColumn lec = logReportTableModel.getColumn(i);

			int colWidth = lec.getPrefColumnWidth();
			tableColumn.setPreferredWidth(colWidth);
			tableColumn.setMinWidth(colWidth);
			tableColumn.setWidth(colWidth);
			tableColumn.setResizable(true);

			tableColumnModel.addColumn(tableColumn);
		}

		return tableColumnModel;
	}

	private BookmarkContainerPanel<Integer> getBookmarkContainerPanel() {

		final LogTableModel logTableModel = getLogTableModel();

		BookmarkModel<Integer> bookmarkModel = logTableModel.getBookmarkModel();

		BookmarkContainerPanel<Integer> bookmarkContainerPanel;

		bookmarkContainerPanel = new BookmarkContainerPanel<Integer>(bookmarkModel, navigationTableController) {

			private static final long serialVersionUID = 5672957295689747776L;

			@Override
			public FilterTableModel<Integer> getFilterTableModel() {
				return logTableModel;
			}
		};

		return bookmarkContainerPanel;
	}

}
