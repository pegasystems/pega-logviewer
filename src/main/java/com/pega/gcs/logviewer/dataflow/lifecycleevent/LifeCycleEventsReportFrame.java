
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class LifeCycleEventsReportFrame extends JFrame implements TableModelListener {

    private static final long serialVersionUID = 3160769471468490455L;

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventsReportFrame.class);

    private LifeCycleEventTableModel lifeCycleEventTableModel;

    private JTabbedPane reportJTabbedPane;

    public LifeCycleEventsReportFrame(LifeCycleEventTableModel lifeCycleEventTableModel, ImageIcon appIcon,
            Component parent) {

        super();

        this.lifeCycleEventTableModel = lifeCycleEventTableModel;

        lifeCycleEventTableModel.addTableModelListener(this);

        setTitle(lifeCycleEventTableModel.getModelName());

        setIconImage(appIcon.getImage());

        // setPreferredSize(new Dimension(1500, 800));

        setExtendedState(Frame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setContentPane(getMainJPanel());

        pack();

        setLocationRelativeTo(parent);

        // visible should be the last step
        setVisible(true);
    }

    private LifeCycleEventTableModel getLifeCycleEventTableModel() {
        return lifeCycleEventTableModel;
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {

        if (tableModelEvent.getType() == TableModelEvent.UPDATE) {
            rebuildOverview();
        }
    }

    private void rebuildOverview() {

        LOG.info("rebuildOverview()");

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();

        reportJTabbedPane.removeAll();

        buildTabs();

        validate();
        repaint();
    }

    public void destroyFrame() {

        LifeCycleEventTableModel lifeCycleEventTableModel;
        lifeCycleEventTableModel = getLifeCycleEventTableModel();

        lifeCycleEventTableModel.removeTableModelListener(this);
        setVisible(false);
    }

    private JTabbedPane getReportJTabbedPane() {

        if (reportJTabbedPane == null) {
            reportJTabbedPane = new JTabbedPane();
        }

        return reportJTabbedPane;
    }

    private JComponent getMainJPanel() {

        JPanel mainJPanel = new JPanel();
        mainJPanel.setLayout(new BorderLayout());

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();
        mainJPanel.add(reportJTabbedPane);

        buildTabs();
        return mainJPanel;
    }

    private void buildTabs() {

        LifeCycleEventTableModel lifeCycleEventTableModel;
        lifeCycleEventTableModel = getLifeCycleEventTableModel();

        PartitionsChartPanel partitionsChartPanel = new PartitionsChartPanel(lifeCycleEventTableModel);

        addTab("Partition Chart", 200, partitionsChartPanel, 0);
    }

    private void addTab(String tabText, int tabWidth, JPanel panel, int tabIndex) {

        JTabbedPane reportJTabbedPane = getReportJTabbedPane();

        JLabel tabLabel = new JLabel(tabText);
        Font labelFont = tabLabel.getFont();
        Font tabFont = labelFont.deriveFont(Font.BOLD, 12);
        Dimension dim = new Dimension(tabWidth, 26);
        tabLabel.setFont(tabFont);
        tabLabel.setSize(dim);
        tabLabel.setPreferredSize(dim);
        tabLabel.setHorizontalAlignment(SwingConstants.CENTER);

        reportJTabbedPane.addTab(tabText, panel);
        reportJTabbedPane.setTabComponentAt(tabIndex, tabLabel);
    }

}