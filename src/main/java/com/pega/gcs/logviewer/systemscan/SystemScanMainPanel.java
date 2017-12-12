/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.systemscan;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.LogViewerSetting;
import com.pega.gcs.logviewer.systemscan.model.ScanResultHotfixEntryKey;
import com.pega.gcs.logviewer.systemscan.v7.SystemScanTaskV7;
import com.pega.gcs.logviewer.systemscan.view.SystemScanCompareTableView;
import com.pega.gcs.logviewer.systemscan.view.SystemScanSingleTableView;
import com.pega.gcs.logviewer.systemscan.view.SystemScanView;
import com.pega.gcs.logviewer.systemscan.view.SystemScanViewMode;

public class SystemScanMainPanel extends JPanel {

	private static final long serialVersionUID = 1172626021990445833L;

	private static final Log4j2Helper LOG = new Log4j2Helper(SystemScanMainPanel.class);

	private RecentFileContainer recentFileContainer;

	private LogViewerSetting logViewerSetting;

	private SystemScanTableModel systemScanTableModel;

	private NavigationTableController<ScanResultHotfixEntryKey> navigationTableController;

	private HashMap<String, SystemScanView> systemScanViewMap;

	private JComboBox<SystemScanViewMode> systemScanViewModeJComboBox;

	private JPanel scanResultViewCardJPanel;

	private JPanel supplementUtilityJPanel;

	public SystemScanMainPanel(File scanResultZipFile, RecentFileContainer recentFileContainer,
			LogViewerSetting logViewerSetting) {

		super();

		this.recentFileContainer = recentFileContainer;
		this.logViewerSetting = logViewerSetting;

		String charset = logViewerSetting.getCharset();
		Locale locale = logViewerSetting.getLocale();

		Map<String, Object> attributes = new HashMap<>();
		attributes.put(RecentFile.KEY_LOCALE, locale);

		RecentFile recentFile = recentFileContainer.getRecentFile(scanResultZipFile, charset, attributes);

		systemScanTableModel = new SystemScanTableModel(recentFile, null);

		BookmarkContainer<ScanResultHotfixEntryKey> bookmarkContainer;
		bookmarkContainer = (BookmarkContainer<ScanResultHotfixEntryKey>) recentFile
				.getAttribute(RecentFile.KEY_BOOKMARK);

		if (bookmarkContainer == null) {

			bookmarkContainer = new BookmarkContainer<ScanResultHotfixEntryKey>();

			recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
		}

		bookmarkContainer = (BookmarkContainer<ScanResultHotfixEntryKey>) recentFile
				.getAttribute(RecentFile.KEY_BOOKMARK);

		BookmarkModel<ScanResultHotfixEntryKey> bookmarkModel = new BookmarkModel<ScanResultHotfixEntryKey>(
				bookmarkContainer, systemScanTableModel);

		systemScanTableModel.setBookmarkModel(bookmarkModel);

		navigationTableController = new NavigationTableController<>(systemScanTableModel);

		systemScanViewMap = new HashMap<String, SystemScanView>();
		setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 1.0D;
		gbc1.weighty = 0.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		JPanel utilityCompositeJPanel = getUtilityCompositeJPanel();
		JPanel scanResultViewCardJPanel = getScanResultViewCardJPanel();

		add(utilityCompositeJPanel, gbc1);
		add(scanResultViewCardJPanel, gbc2);

		// set default view
		JComboBox<SystemScanViewMode> SystemScanViewModeJComboBox = getSystemScanViewModeJComboBox();

		// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4699927
		// tree need to be built once the root node has some child nodes. hence
		// setting the default to single table
		SystemScanViewModeJComboBox.setSelectedItem(SystemScanViewMode.SINGLE_TABLE);

		loadFile(systemScanTableModel, this, false);
	}

	private SystemScanTableModel getSystemScanTableModel() {
		return systemScanTableModel;
	}

	private NavigationTableController<ScanResultHotfixEntryKey> getNavigationTableController() {
		return navigationTableController;
	}

	private JComboBox<SystemScanViewMode> getSystemScanViewModeJComboBox() {

		if (systemScanViewModeJComboBox == null) {

			systemScanViewModeJComboBox = new JComboBox<SystemScanViewMode>(SystemScanViewMode.values());

			Dimension size = new Dimension(200, 20);
			systemScanViewModeJComboBox.setPreferredSize(size);
			// systemScanViewModeJComboBox.setMinimumSize(size);
			systemScanViewModeJComboBox.setMaximumSize(size);

			systemScanViewModeJComboBox.addActionListener(new ActionListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void actionPerformed(ActionEvent e) {

					JComboBox<SystemScanViewMode> systemScanViewModeJComboBox;
					systemScanViewModeJComboBox = (JComboBox<SystemScanViewMode>) e.getSource();

					SystemScanViewMode systemScanViewMode;
					systemScanViewMode = (SystemScanViewMode) systemScanViewModeJComboBox.getSelectedItem();

					switchSystemScanViewMode(systemScanViewMode);
				}
			});

		}

		return systemScanViewModeJComboBox;
	}

	private JPanel getScanResultViewCardJPanel() {

		if (scanResultViewCardJPanel == null) {

			SystemScanTableModel systemScanTableModel = getSystemScanTableModel();

			NavigationTableController<ScanResultHotfixEntryKey> navigationTableController = getNavigationTableController();

			JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

			scanResultViewCardJPanel = new JPanel(new CardLayout());

			for (SystemScanViewMode systemScanViewMode : SystemScanViewMode.values()) {

				SystemScanView systemScanView;

				switch (systemScanViewMode) {

				case SINGLE_TABLE:
					systemScanView = new SystemScanSingleTableView(systemScanTableModel, supplementUtilityJPanel,
							navigationTableController);
					break;

				case COMPARE_TABLE:
					systemScanView = new SystemScanCompareTableView(systemScanTableModel, supplementUtilityJPanel,
							navigationTableController, recentFileContainer, logViewerSetting);
					break;

				default:
					systemScanView = new SystemScanSingleTableView(systemScanTableModel, supplementUtilityJPanel,
							navigationTableController);
					break;
				}

				String scanResultViewModeName = systemScanViewMode.name();

				systemScanViewMap.put(scanResultViewModeName, systemScanView);

				scanResultViewCardJPanel.add(systemScanView, scanResultViewModeName);
			}
		}

		return scanResultViewCardJPanel;
	}

	protected JPanel getSupplementUtilityJPanel() {

		if (supplementUtilityJPanel == null) {

			supplementUtilityJPanel = new JPanel();
			LayoutManager layout = new BoxLayout(supplementUtilityJPanel, BoxLayout.LINE_AXIS);
			supplementUtilityJPanel.setLayout(layout);
		}

		return supplementUtilityJPanel;
	}

	private JPanel getUtilityCompositeJPanel() {

		JPanel utilityCompositeJPanel = new JPanel();

		utilityCompositeJPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.weightx = 0.0D;
		gbc1.weighty = 1.0D;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		gbc2.weightx = 1.0D;
		gbc2.weighty = 1.0D;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		gbc2.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		gbc3.weightx = 0.0D;
		gbc3.weighty = 1.0D;
		gbc3.fill = GridBagConstraints.BOTH;
		gbc3.anchor = GridBagConstraints.NORTHWEST;
		gbc3.insets = new Insets(0, 0, 0, 0);

		JPanel utilityJPanel = getUtilityJPanel();
		CatalogPanel catalogPanel = new CatalogPanel(systemScanTableModel, this);
		JPanel supplementUtilityJPanel = getSupplementUtilityJPanel();

		utilityCompositeJPanel.add(utilityJPanel, gbc1);
		utilityCompositeJPanel.add(catalogPanel, gbc2);
		utilityCompositeJPanel.add(supplementUtilityJPanel, gbc3);

		return utilityCompositeJPanel;
	}

	private JPanel getUtilityJPanel() {

		JPanel utilityJPanel = new JPanel();

		LayoutManager layout = new BoxLayout(utilityJPanel, BoxLayout.LINE_AXIS);
		utilityJPanel.setLayout(layout);

		Dimension spacer = new Dimension(15, 30);
		Dimension endSpacer = new Dimension(10, 30);

		JLabel scanResultViewModeJLabel = new JLabel("Select view: ");

		JComboBox<SystemScanViewMode> systemScanViewModeJComboBox = getSystemScanViewModeJComboBox();

		utilityJPanel.add(Box.createRigidArea(endSpacer));
		utilityJPanel.add(scanResultViewModeJLabel);
		utilityJPanel.add(Box.createRigidArea(spacer));
		utilityJPanel.add(systemScanViewModeJComboBox);
		utilityJPanel.add(Box.createRigidArea(spacer));

		utilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		return utilityJPanel;
	}

	private void switchSystemScanViewMode(SystemScanViewMode systemScanViewMode) {

		String scanResultViewModeName = systemScanViewMode.name();

		SystemScanView systemScanView = systemScanViewMap.get(scanResultViewModeName);

		if (systemScanView != null) {

			systemScanView.switchToFront();

			JPanel scanResultViewCardJPanel = getScanResultViewCardJPanel();
			CardLayout cardLayout = (CardLayout) (scanResultViewCardJPanel.getLayout());

			cardLayout.show(scanResultViewCardJPanel, scanResultViewModeName);

		}
	}

	public static void loadFile(SystemScanTableModel systemScanTableModel, Component parent, boolean wait) {
		UIManager.put("ModalProgressMonitor.progressText", "Loading system scan file");

		final ModalProgressMonitor progressMonitor = new ModalProgressMonitor(parent, "", "", 0, 100);

		progressMonitor.setMillisToDecideToPopup(0);
		progressMonitor.setMillisToPopup(0);

		loadFile(parent, systemScanTableModel, progressMonitor, wait);
	}

	public static void loadFile(Component parent, SystemScanTableModel systemScanTableModel, ModalProgressMonitor progressMonitor,
			boolean wait) {

		RecentFile recentFile = systemScanTableModel.getRecentFile();

		if (recentFile != null) {

			try {

				AbstractSystemScanTask abstractSystemScanTask = null;

				InventoryVersion inventoryVersion = systemScanTableModel.getInventoryVersion();

				switch (inventoryVersion) {

				case INVENTORY_VERSION_6:
					try {
						Class<?> systemScanTaskV6Class = Class
								.forName("com.pega.gcs.logviewer.systemscan.v6.SystemScanTaskV6");

						Constructor<?> systemScanTaskV6Constructor = systemScanTaskV6Class.getDeclaredConstructor(
								Component.class, SystemScanTableModel.class, ModalProgressMonitor.class, boolean.class);
						systemScanTaskV6Constructor.setAccessible(true);

						abstractSystemScanTask = (AbstractSystemScanTask) systemScanTaskV6Constructor
								.newInstance(parent, systemScanTableModel, progressMonitor, wait);

					} catch (Exception e) {
						LOG.error("Error initialising SystemScanV6", e);
					}
					break;
				case INVENTORY_VERSION_7:
					abstractSystemScanTask = new SystemScanTaskV7(parent, systemScanTableModel, progressMonitor, wait);
					break;

				default:
					break;
				}

				if (abstractSystemScanTask != null) {

					abstractSystemScanTask.execute();

					abstractSystemScanTask.completeTask();
				}

			} catch (Exception e) {
				LOG.error("Error in SystemScanDecompressTask: ", e);
			}

		} else {
			systemScanTableModel.setMessage(new Message(MessageType.ERROR, "No file selected for model"));
		}
	}
}
