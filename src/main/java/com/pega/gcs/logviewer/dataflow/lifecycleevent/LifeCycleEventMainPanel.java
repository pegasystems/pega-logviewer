
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.ZoneId;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.pega.gcs.fringecommon.guiutilities.BaseFrame;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.Message.MessageType;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.RecentFileContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkContainer;
import com.pega.gcs.fringecommon.guiutilities.bookmark.BookmarkModel;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.LogTableModel;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.view.LifeCycleEventsTableView;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.view.LifeCycleEventsView;
import com.pega.gcs.logviewer.model.LogViewerSetting;

public class LifeCycleEventMainPanel extends JPanel {

    private static final long serialVersionUID = 1945441476396512183L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventMainPanel.class);

    private LifeCycleEventTableModel lifeCycleEventTableModel;

    private NavigationTableController<LifeCycleEventKey> navigationTableController;

    private JPanel lifeCycleEventViewPanel;

    private JPanel supplementUtilityPanel;

    private JButton overviewButton;

    private LifeCycleEventsReportFrame lifeCycleEventsReportFrame;

    @SuppressWarnings("unchecked")
    public LifeCycleEventMainPanel(File lifeCycleEventFile, RecentFileContainer recentFileContainer,
            LogViewerSetting logViewerSetting) {

        super();

        String charset = logViewerSetting.getCharset();

        SearchData<LifeCycleEventKey> searchData = new SearchData<>(null);

        RecentFile recentFile = recentFileContainer.getRecentFile(lifeCycleEventFile, charset);

        String zoneIdStr = (String) recentFile.getAttribute(LogTableModel.RECENT_KEY_ZONEID);

        ZoneId displayZoneId = (zoneIdStr != null) ? ZoneId.of(zoneIdStr) : null;

        lifeCycleEventTableModel = new LifeCycleEventTableModel(recentFile, searchData, displayZoneId);

        // not moving bookmark loading to end of file load

        BookmarkContainer<LifeCycleEventKey> bookmarkContainer;
        bookmarkContainer = (BookmarkContainer<LifeCycleEventKey>) recentFile.getAttribute(RecentFile.KEY_BOOKMARK);

        if (bookmarkContainer == null) {

            bookmarkContainer = new BookmarkContainer<>();

            recentFile.setAttribute(RecentFile.KEY_BOOKMARK, bookmarkContainer);
        }

        BookmarkModel<LifeCycleEventKey> bookmarkModel = new BookmarkModel<>(bookmarkContainer,
                lifeCycleEventTableModel);

        lifeCycleEventTableModel.setBookmarkModel(bookmarkModel);

        navigationTableController = new NavigationTableController<>(lifeCycleEventTableModel);

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
        JPanel lifeCycleEventViewPanel = getLifeCycleEventViewPanel();

        add(utilityCompositeJPanel, gbc1);
        add(lifeCycleEventViewPanel, gbc2);

        loadFile(lifeCycleEventTableModel);
    }

    private LifeCycleEventTableModel getLifeCycleEventTableModel() {
        return lifeCycleEventTableModel;
    }

    private NavigationTableController<LifeCycleEventKey> getNavigationTableController() {
        return navigationTableController;
    }

    private JPanel getLifeCycleEventViewPanel() {

        if (lifeCycleEventViewPanel == null) {

            LifeCycleEventTableModel lifeCycleEventTableModel = getLifeCycleEventTableModel();

            NavigationTableController<LifeCycleEventKey> navigationTableController = getNavigationTableController();

            JPanel supplementUtilityJPanel = getSupplementUtilityPanel();

            LifeCycleEventsView lifeCycleEventsView;
            lifeCycleEventsView = new LifeCycleEventsTableView(lifeCycleEventTableModel, supplementUtilityJPanel,
                    navigationTableController);

            lifeCycleEventViewPanel = new JPanel(new BorderLayout());

            lifeCycleEventViewPanel.add(lifeCycleEventsView, BorderLayout.CENTER);
        }

        return lifeCycleEventViewPanel;

    }

    protected JPanel getSupplementUtilityPanel() {

        if (supplementUtilityPanel == null) {

            supplementUtilityPanel = new JPanel();
            LayoutManager layout = new BoxLayout(supplementUtilityPanel, BoxLayout.LINE_AXIS);
            supplementUtilityPanel.setLayout(layout);
        }

        return supplementUtilityPanel;
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

        JPanel utilityJPanel = getUtilityJPanel();
        JPanel supplementUtilityJPanel = getSupplementUtilityPanel();

        utilityCompositeJPanel.add(utilityJPanel, gbc1);
        utilityCompositeJPanel.add(supplementUtilityJPanel, gbc2);

        return utilityCompositeJPanel;
    }

    private JPanel getUtilityJPanel() {

        JPanel utilityJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilityJPanel, BoxLayout.LINE_AXIS);
        utilityJPanel.setLayout(layout);

        Dimension spacer = new Dimension(15, 30);
        Dimension endSpacer = new Dimension(10, 30);

        // JLabel scanResultViewModeJLabel = new JLabel("Select view: ");

        // JComboBox<HotfixScanViewMode> hotfixScanViewModeJComboBox = getHotfixScanViewModeComboBox();

        JButton overviewJButton = getOverviewButton();

        utilityJPanel.add(Box.createRigidArea(endSpacer));
        // utilityJPanel.add(scanResultViewModeJLabel);
        utilityJPanel.add(Box.createRigidArea(spacer));
        // utilityJPanel.add(hotfixScanViewModeJComboBox);
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.add(overviewJButton);
        utilityJPanel.add(Box.createRigidArea(spacer));
        utilityJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return utilityJPanel;
    }

    public void loadFile(LifeCycleEventTableModel lifeCycleEventTableModel) {

        RecentFile recentFile = lifeCycleEventTableModel.getRecentFile();

        if (recentFile != null) {

            try {

                String filePath = lifeCycleEventTableModel.getFilePath();

                File lifeCycleEventFile = new File(filePath);

                LifeCycleEventFileParser lifeCycleEventFileParser;
                lifeCycleEventFileParser = new LifeCycleEventFileParser(lifeCycleEventTableModel);

                int processedCount = lifeCycleEventFileParser.processLifeCycleEventfile(lifeCycleEventFile);

                LOG.info("LifeCycleEvent load done: " + filePath + " processedCount:" + processedCount);

            } catch (Exception e) {
                LOG.error("Error in LifeCycleEvent load : ", e);
                lifeCycleEventTableModel.setMessage(
                        new Message(MessageType.ERROR, "Error parsing life cycle events file :" + e.getMessage()));

            }

        } else {
            lifeCycleEventTableModel.setMessage(new Message(MessageType.ERROR, "No file selected for model"));
        }
    }

    private JButton getOverviewButton() {

        if (overviewButton == null) {

            overviewButton = new JButton("Overview");
            Dimension size = new Dimension(90, 20);
            overviewButton.setPreferredSize(size);
            overviewButton.setMaximumSize(size);

            overviewButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                    LifeCycleEventsReportFrame lifeCycleEventsReportFrame;
                    lifeCycleEventsReportFrame = getLifeCycleEventsReportFrame();

                    lifeCycleEventsReportFrame.toFront();
                }
            });
        }

        return overviewButton;

    }

    private LifeCycleEventsReportFrame getLifeCycleEventsReportFrame() {

        if (lifeCycleEventsReportFrame == null) {

            LifeCycleEventTableModel lifeCycleEventTableModel = getLifeCycleEventTableModel();

            lifeCycleEventsReportFrame = new LifeCycleEventsReportFrame(lifeCycleEventTableModel,
                    BaseFrame.getAppIcon(), this);

            lifeCycleEventsReportFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    LifeCycleEventsReportFrame lifeCycleEventsReportFrame;
                    lifeCycleEventsReportFrame = getLifeCycleEventsReportFrame();

                    lifeCycleEventsReportFrame.destroyFrame();

                    setLifeCycleEventsReportFrame(null);
                }

            });
        }

        return lifeCycleEventsReportFrame;
    }

    private void setLifeCycleEventsReportFrame(LifeCycleEventsReportFrame lifeCycleEventsReportFrame) {
        this.lifeCycleEventsReportFrame = lifeCycleEventsReportFrame;
    }
}
