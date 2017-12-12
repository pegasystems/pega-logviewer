/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.DateTimeUtilities;
import com.pega.gcs.logviewer.systemscan.model.ScanResult;

public class CatalogPanel extends JPanel implements TableModelListener {

	private static final long serialVersionUID = 1299515354777833396L;

	private static final Log4j2Helper LOG = new Log4j2Helper(CatalogPanel.class);

	private SystemScanTableModel systemScanTableModel;

	private Component parent;

	private JLabel catalogTSValueLabel;

	private JButton downloadCatalogButton;

	private Class<?> catalogManagerClass;

	private DateFormat displayDateFormat;

	public CatalogPanel(SystemScanTableModel systemScanTableModel, Component parent) {

		super();

		this.systemScanTableModel = systemScanTableModel;
		this.parent = parent;

		displayDateFormat = new SimpleDateFormat(DateTimeUtilities.DATEFORMAT_ISO8601);

		systemScanTableModel.addTableModelListener(this);

		LayoutManager layout = new BoxLayout(this, BoxLayout.LINE_AXIS);

		setLayout(layout);

		InventoryVersion inventoryVersion = systemScanTableModel.getInventoryVersion();

		boolean v6Support = false;

		try {
			catalogManagerClass = Class.forName("com.pega.gcs.logviewer.systemscan.v6.CatalogManager");
			v6Support = true;
		} catch (Exception e) {
			LOG.error("Error initialising CatalogManager", e);
			v6Support = false;
		}

		if ((v6Support) && (inventoryVersion.equals(InventoryVersion.INVENTORY_VERSION_6))) {

			JLabel catalogTSNameLabel = new JLabel("Catalog Timestamp:");

			JLabel catalogTSValueLabel = getCatalogTSValueLabel();
			JButton downloadCatalogButton = getDownloadCatalogButton();

			int height = 30;

			add(Box.createRigidArea(new Dimension(10, height)));
			add(catalogTSNameLabel);
			add(Box.createRigidArea(new Dimension(4, height)));
			add(catalogTSValueLabel);
			add(Box.createRigidArea(new Dimension(10, height)));
			add(downloadCatalogButton);
			add(Box.createRigidArea(new Dimension(4, height)));
//			add(Box.createHorizontalGlue());
		}

		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
	}

	private JLabel getCatalogTSValueLabel() {

		if (catalogTSValueLabel == null) {

			catalogTSValueLabel = new JLabel();

			Dimension size = new Dimension(200, 20);
			catalogTSValueLabel.setPreferredSize(size);
			catalogTSValueLabel.setMinimumSize(size);
			catalogTSValueLabel.setMaximumSize(size);
			catalogTSValueLabel.setForeground(Color.BLUE);
		}

		return catalogTSValueLabel;
	}

	private JButton getDownloadCatalogButton() {

		if (downloadCatalogButton == null) {

			downloadCatalogButton = new JButton("Download latest Catalog");

			Dimension size = new Dimension(150, 20);
			Dimension minSize = new Dimension(100, 20);
			downloadCatalogButton.setPreferredSize(size);
			downloadCatalogButton.setMinimumSize(minSize);
			downloadCatalogButton.setMaximumSize(size);
			downloadCatalogButton.setBorder(BorderFactory.createEmptyBorder());
			downloadCatalogButton.setToolTipText("Download latest Catalog");
			downloadCatalogButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					CatalogDownloadDialog catalogDownloadDialog;
					catalogDownloadDialog = new CatalogDownloadDialog(BaseFrame.getAppIcon(), parent);

					catalogDownloadDialog.setVisible(true);

					boolean settingUpdated = catalogDownloadDialog.isSettingUpdated();

					if (settingUpdated) {

						downloadLatestCatalogZip();

						try {

							Method getInstanceMethod = catalogManagerClass.getDeclaredMethod("getInstance");

							Object catalogManager = getInstanceMethod.invoke(null);

							Method forceReloadMethod = catalogManagerClass.getDeclaredMethod("forceReload");

							forceReloadMethod.invoke(catalogManager);

							boolean rescanInventory = catalogDownloadDialog.isRescanInventory();

							if (rescanInventory) {
								SystemScanMainPanel.loadFile(systemScanTableModel, parent, false);
							}
						} catch (Exception ex) {
							LOG.error("Error downloading Catalog", ex);
						}
					}
				}
			});
		}

		return downloadCatalogButton;
	}

	private void downloadLatestCatalogZip() {

		UIManager.put("ModalProgressMonitor.progressText", "Downloading Catalog file");

		final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "",
				"Downloading Catalog file (0 MB)");

		CatalogDownloadTask catalogDownloadTask = new CatalogDownloadTask(progressMonitor) {

			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					LOG.error("Error downloading Catalog file", e);
				} finally {
					progressMonitor.close();
				}
			}

		};

		catalogDownloadTask.execute();

		progressMonitor.show();

	}

	@Override
	public void tableChanged(TableModelEvent e) {

		ScanResult scanResult = systemScanTableModel.getScanResult();

		if (scanResult != null) {

			Date catalogTimestamp = scanResult.getCatalogTimestamp();

			if (catalogTimestamp != null) {
				
				String timestamp = displayDateFormat.format(catalogTimestamp);

				JLabel catalogTSValueLabel = getCatalogTSValueLabel();
				catalogTSValueLabel.setText(timestamp);
			}
		}
	}

}
