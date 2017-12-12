/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.report.alertpal;

import java.io.File;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;

import com.pega.gcs.logviewer.model.AlertLogEntry;

public class AlertPALTableExportTask extends SwingWorker<Void, Integer> {

	private AlertPALTableModel alertPALTableModel;
	private File tsvFile;

	public AlertPALTableExportTask(AlertPALTableModel alertPALTableModel, File tsvFile) {

		this.alertPALTableModel = alertPALTableModel;
		this.tsvFile = tsvFile;
	}

	@Override
	protected Void doInBackground() throws Exception {

		int rowCount = alertPALTableModel.getRowCount();
		int colCount = alertPALTableModel.getColumnCount();

		int batchSize = 50;
		int row = 0;

		publish(row);

		StringBuffer dataRecordBuffer = new StringBuffer();
		boolean firstColumn = true;

		// write headers
		for (int col = 0; col < colCount; col++) {

			String columnName = alertPALTableModel.getColumnName(col);

			if (firstColumn) {
				firstColumn = false;
			} else {
				dataRecordBuffer.append("\t");
			}

			dataRecordBuffer.append(columnName);
		}

		dataRecordBuffer.append(System.getProperty("line.separator"));

		String dataRecord = dataRecordBuffer.toString();

		FileUtils.writeStringToFile(tsvFile, dataRecord, alertPALTableModel.getCharset());

		dataRecordBuffer = new StringBuffer();

		for (row = 0; row < rowCount; row++) {

			if (!isCancelled()) {

				firstColumn = true;

				AlertLogEntry alertLogEntry = (AlertLogEntry) alertPALTableModel.getValueAt(row, 0);

				for (int col = 0; col < colCount; col++) {

					String columnValue = alertPALTableModel.getColumnValueFromModel(alertLogEntry, col);

					if (firstColumn) {
						firstColumn = false;
					} else {
						dataRecordBuffer.append("\t");
					}

					if (columnValue != null) {
						dataRecordBuffer.append(columnValue);
					}
				}

				dataRecordBuffer.append(System.getProperty("line.separator"));

				if (row == batchSize) {
					dataRecord = dataRecordBuffer.toString();
					FileUtils.writeStringToFile(tsvFile, dataRecord, alertPALTableModel.getCharset(), true);
					dataRecordBuffer = new StringBuffer();
					publish(row);
				}
			}
		}

		if (!isCancelled()) {
			dataRecord = dataRecordBuffer.toString();
			FileUtils.writeStringToFile(tsvFile, dataRecord, alertPALTableModel.getCharset(), true);

			publish(row);
		}

		return null;
	}
}
