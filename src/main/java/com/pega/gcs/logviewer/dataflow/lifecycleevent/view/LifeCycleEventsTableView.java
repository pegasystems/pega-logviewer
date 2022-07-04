
package com.pega.gcs.logviewer.dataflow.lifecycleevent.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumnModel;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.NavigationTableController;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventKey;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventTable;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.LifeCycleEventTableModel;

public class LifeCycleEventsTableView extends LifeCycleEventsView {

    private static final long serialVersionUID = -1554390231575103390L;

    private LifeCycleEventTable lifeCycleEventTable;

    private JTextField statusBar;

    private SearchPanel<LifeCycleEventKey> searchPanel;

    public LifeCycleEventsTableView(LifeCycleEventTableModel lifeCycleEventTableModel, JPanel supplementUtilityJPanel,
            NavigationTableController<LifeCycleEventKey> navigationTableController) {

        super(lifeCycleEventTableModel, supplementUtilityJPanel, navigationTableController);

        LifeCycleEventTable lifeCycleEventTable = getLifeCycleEventTable();

        navigationTableController.addCustomJTable(lifeCycleEventTable);

        searchPanel = new SearchPanel<>(navigationTableController, lifeCycleEventTableModel.getSearchModel());

        setLayout(new BorderLayout());

        JPanel utilityPanel = getUtilityPanel();
        JPanel lifeCycleEventsDataPanel = getLifeCycleEventsDataPanel();
        JPanel statusBarPanel = getStatusBarPanel();

        add(utilityPanel, BorderLayout.NORTH);
        add(lifeCycleEventsDataPanel, BorderLayout.CENTER);
        add(statusBarPanel, BorderLayout.SOUTH);

        lifeCycleEventTableModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {
                    Message message = (Message) evt.getNewValue();
                    GUIUtilities.setMessage(statusBar, message);
                }

            }
        });

    }

    private LifeCycleEventTable getLifeCycleEventTable() {

        if (lifeCycleEventTable == null) {

            LifeCycleEventTableModel lifeCycleEventTableModel = getLifeCycleEventTableModel();
            lifeCycleEventTable = new LifeCycleEventTable(lifeCycleEventTableModel);

            lifeCycleEventTable.setFillsViewportHeight(true);

            // HotfixTableMouseListener hotfixTableMouseListener = new HotfixTableMouseListener(this);
            //
            // lifeCycleEventTable.addMouseListener(hotfixTableMouseListener);
        }

        return lifeCycleEventTable;
    }

    private JTextField getStatusBar() {

        if (statusBar == null) {
            statusBar = new JTextField();
            statusBar.setEditable(false);
            statusBar.setBackground(null);
            statusBar.setBorder(null);
        }

        return statusBar;
    }

    @Override
    protected void updateSupplementUtilityPanel() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void performComponentResized(Rectangle oldBounds, Rectangle newBounds) {

        LifeCycleEventTable lifeCycleEventTable = getLifeCycleEventTable();

        TableColumnModel tableColumnModel = lifeCycleEventTable.getColumnModel();

        GUIUtilities.applyTableColumnResize(tableColumnModel, oldBounds, newBounds);

    }

    private JPanel getUtilityPanel() {

        JPanel utilityPanel = new JPanel();

        utilityPanel.setLayout(new GridBagLayout());

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

        JPanel utilsPanel = getUtilsPanel();

        utilityPanel.add(searchPanel, gbc1);
        utilityPanel.add(utilsPanel, gbc2);

        return utilityPanel;

    }

    private JPanel getLifeCycleEventsDataPanel() {

        JPanel lifeCycleEventsDataPanel = new JPanel();
        lifeCycleEventsDataPanel.setLayout(new BorderLayout());

        LifeCycleEventTable lifeCycleEventTable = getLifeCycleEventTable();

        JScrollPane scrollpane = new JScrollPane(lifeCycleEventTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        lifeCycleEventTable.setPreferredScrollableViewportSize(scrollpane.getPreferredSize());

        LifeCycleEventTableModel lifeCycleEventTableModel = getLifeCycleEventTableModel();
        JPanel markerBarPanel = getMarkerBarPanel(lifeCycleEventTableModel);

        lifeCycleEventsDataPanel.add(scrollpane, BorderLayout.CENTER);
        lifeCycleEventsDataPanel.add(markerBarPanel, BorderLayout.EAST);

        return lifeCycleEventsDataPanel;

    }

    private JPanel getStatusBarPanel() {

        JPanel statusBarJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(statusBarJPanel, BoxLayout.LINE_AXIS);
        statusBarJPanel.setLayout(layout);

        Dimension spacer = new Dimension(5, 20);

        JTextField statusBar = getStatusBar();

        statusBarJPanel.add(Box.createRigidArea(spacer));
        statusBarJPanel.add(statusBar);
        statusBarJPanel.add(Box.createRigidArea(spacer));

        statusBarJPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return statusBarJPanel;
    }

    private JPanel getUtilsPanel() {

        JPanel utilsPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilsPanel, BoxLayout.LINE_AXIS);
        utilsPanel.setLayout(layout);

        Dimension dim = new Dimension(5, 40);

        utilsPanel.add(Box.createHorizontalGlue());
        utilsPanel.add(Box.createRigidArea(dim));

        utilsPanel.add(Box.createHorizontalGlue());

        utilsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return utilsPanel;
    }
}
