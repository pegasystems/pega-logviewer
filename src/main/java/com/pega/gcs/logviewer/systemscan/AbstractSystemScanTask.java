/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Component;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.ProgressTaskInfo;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.systemscan.model.ScanResult;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixChangeEntry;

public abstract class AbstractSystemScanTask extends SwingWorker<Void, ProgressTaskInfo> {

	private static final Log4j2Helper LOG = new Log4j2Helper(AbstractSystemScanTask.class);

	private Component parent;

	private SystemScanTableModel systemScanTableModel;

	private ModalProgressMonitor progressMonitor;

	private boolean wait;

	public abstract Date getCatalogTimestamp();

	public abstract Date getInventoryTimestamp();

	public abstract List<String> getHotfixColumnList();

	public abstract Map<Integer, List<String>> getHotfixDataMap();

	public AbstractSystemScanTask(Component parent, SystemScanTableModel systemScanTableModel,
			ModalProgressMonitor progressMonitor, boolean wait) {

		this.parent = parent;
		this.systemScanTableModel = systemScanTableModel;
		this.progressMonitor = progressMonitor;
		this.wait = wait;
	}

	private Component getParent() {
		return parent;
	}

	protected SystemScanTableModel getSystemScanTableModel() {
		return systemScanTableModel;
	}

	protected ModalProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	@Override
	protected void process(List<ProgressTaskInfo> chunks) {

		if ((isDone()) || (isCancelled()) || (chunks == null) || (chunks.size() == 0)) {
			return;
		}

		Collections.sort(chunks);

		ProgressTaskInfo progressTaskInfo = chunks.get(chunks.size() - 1);

		int progressVal = (int) progressTaskInfo.getCount();
		String note = progressTaskInfo.getNote();

		ModalProgressMonitor progressMonitor = getProgressMonitor();

		if (progressMonitor != null) {
			progressMonitor.setProgress(progressVal);
			progressMonitor.setNote(note);
		}
	}

	protected void setProgressAndNote(int progressVal, String note) {

		ProgressTaskInfo progressTaskInfo = new ProgressTaskInfo(-1, progressVal, note);

		publish(progressTaskInfo);
	}

	@Override
	protected void done() {
		if (!wait) {
			completeLoad();
		}
	}

	public void completeTask() {

		if (wait) {
			completeLoad();
		}
	}

	private void completeLoad() {

		String filePath = systemScanTableModel.getFilePath();

		Message.MessageType messageType = MessageType.INFO;

		StringBuffer messageB = null;

		try {

			get();

			Date catalogTimestamp = getCatalogTimestamp();
			Date inventoryTimestamp = getInventoryTimestamp();

			List<String> hotfixColumnList = getHotfixColumnList();
			Map<Integer, List<String>> hotfixDataMap = getHotfixDataMap();

			ScanResult scanResult = new ScanResult();

			scanResult.setCatalogTimestamp(catalogTimestamp);
			scanResult.setInventoryTimestamp(inventoryTimestamp);

			scanResult.setHotfixColumnList(hotfixColumnList);

			for (Integer index : hotfixDataMap.keySet()) {

				List<String> recordDataList = hotfixDataMap.get(index);

				ScanResultHotfixChangeEntry scanResultHotfixChangeEntry = new ScanResultHotfixChangeEntry(index,
						recordDataList);

				scanResult.addScanResultHotfixChangeEntry(scanResultHotfixChangeEntry);
			}

			scanResult.postProcessScanResultHotfixEntryList();

			systemScanTableModel.setScanResult(scanResult);

			messageB = new StringBuffer();

			messageB.append(filePath);
			messageB.append(". Found ");
			messageB.append(scanResult.getScanResultHotfixEntryKeySet().size());
			messageB.append(" hotfixes.");

		} catch (CancellationException ce) {

			LOG.error("System Scan Task - Cancelled " + filePath);

			messageType = MessageType.ERROR;

			messageB = new StringBuffer();

			messageB.append(filePath);
			messageB.append(" - file loading cancelled.");

		} catch (ExecutionException ee) {

			LOG.error("Execution Error in System Scan Task", ee);

			messageType = MessageType.ERROR;

			messageB = new StringBuffer();

			Component parent = getParent();

			if (ee.getCause() instanceof OutOfMemoryError) {

				messageB.append("Out Of Memory Error has occured while loading ");
				messageB.append(filePath);
				messageB.append(".\nPlease increase the JVM's max heap size (-Xmx) and try again.");

				JOptionPane.showMessageDialog(parent, messageB.toString(), "Out Of Memory Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				messageB.append(ee.getCause().getMessage());
				messageB.append(" has occured while loading ");
				messageB.append(filePath);
				messageB.append(".");

				JOptionPane.showMessageDialog(parent, messageB.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}

		} catch (Exception e) {

			LOG.error("Error loading file: " + filePath, e);

			messageType = MessageType.ERROR;

			messageB = new StringBuffer();

			messageB.append(filePath);
			messageB.append(". Error - ");
			messageB.append(e.getMessage());
		} finally {

			systemScanTableModel.fireTableStructureChanged();

			try {
				progressMonitor.close();
			} catch (Exception e) {
				// close can throw NPE because during compare the same PM is changed into
				// indetermintae which in turn calls close
			}

			Message message = new Message(messageType, messageB.toString());
			systemScanTableModel.setMessage(message);

		}

	}

}
