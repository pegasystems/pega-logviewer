/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alertpal;

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.pega.gcs.fringecommon.guiutilities.CustomJTable;

public class AlertPALTable extends CustomJTable {

	private static final long serialVersionUID = 2004222974580666838L;

	public AlertPALTable(AlertPALTableModel alertPALTableModel) {
		super(alertPALTableModel);

		setAutoCreateColumnsFromModel(false);

		setRowHeight(20);

		setRowSelectionAllowed(true);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JTableHeader tableHeader = getTableHeader();

		tableHeader.setReorderingAllowed(false);

		// bold the header
		Font existingFont = tableHeader.getFont();
		String existingFontName = existingFont.getName();
		int existFontSize = existingFont.getSize();
		Font newFont = new Font(existingFontName, Font.BOLD, existFontSize);
		tableHeader.setFont(newFont);

		DefaultTableCellRenderer dtcr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);

		if ((dataModel != null) && (dataModel instanceof AlertPALTableModel)) {

			TableColumnModel columnModel = ((AlertPALTableModel) dataModel).getTableColumnModel();
			setColumnModel(columnModel);
		}
	}

	/**
	 * bug report #1027936 Override JTable.getScrollableTracksViewportWidth() to
	 * honor the table's preferred size and show horizontal scrollbars if that
	 * size cannot be honored by the viewport if an auto-resize mode is selected
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		// return getPreferredSize().width < getParent().getWidth();

		if (autoResizeMode != AUTO_RESIZE_OFF) {
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
			}

			return true;
		}

		return false;
	}

	@Override
	public void scrollRowToVisible(int rowIndex) {
		if (!(getParent() instanceof JViewport)) {
			return;
		}
		JViewport viewport = (JViewport) getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle rect = getCellRect(rowIndex, 0, true);

		// The location of the view relative to the table
		Rectangle viewRect = viewport.getViewRect();

		rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

		// Calculate location of rectangle if it were at the center of view
		int centerX = (viewRect.width - rect.width) / 2;
		int centerY = (viewRect.height - rect.height) / 2;

		/*
		 * Fake the location of the cell so that scrollRectToVisible will move
		 * the cell to the center
		 */
		if (rect.x < centerX) {
			centerX = -centerX;
		}

		if (rect.y < centerY) {
			centerY = -centerY;
		}
		rect.translate(centerX, centerY);

		// Scroll the area into view
		viewport.scrollRectToVisible(rect);
	}

	// @Override
	// public void valueChanged(ListSelectionEvent e) {
	// super.valueChanged(e);
	//
	// boolean isAdjusting = e.getValueIsAdjusting();
	//
	// if (isAdjusting) {
	// int selectedRow = e.getFirstIndex();
	//
	// // palJTable.setRowSelectionInterval(selectedRow,
	// // selectedRow);
	//
	// scrollRowToVisible(selectedRow);
	// }
	// }

}
