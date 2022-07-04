
package com.pega.gcs.logviewer.systemstate.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pega.gcs.fringecommon.guiutilities.GUIUtilities;
import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.RecentFile;
import com.pega.gcs.fringecommon.guiutilities.search.SearchPanel;
import com.pega.gcs.logviewer.systemstate.SystemStateTree;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeModel;
import com.pega.gcs.logviewer.systemstate.SystemStateTreeNavigationController;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarker;
import com.pega.gcs.logviewer.systemstate.model.AnalysisMarkerListNodeMap;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.model.DatastoreMetadata;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.SearchState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.panel.ClusterStatePanel;
import com.pega.gcs.logviewer.systemstate.panel.DatastoreMetadataPanel;
import com.pega.gcs.logviewer.systemstate.panel.NodeStatePanel;
import com.pega.gcs.logviewer.systemstate.panel.SearchStatePanel;
import com.pega.gcs.logviewer.systemstate.panel.SystemStatePanel;

public class SystemStateFormView extends JPanel {

    private static final long serialVersionUID = 4626064032955144067L;

    private SystemStateTree systemStateTree;

    private JPanel stateCardPanel;

    private JTextField statusBar;

    private SystemStateTreeModel systemStateTreeModel;

    private SystemStateTreeNavigationController systemStateTreeNavigationController;

    private HashMap<String, JComponent> statePanelMap;

    private SearchPanel<String> searchPanel;

    public SystemStateFormView(SystemStateTreeModel systemStateTreeModel,
            SystemStateTreeNavigationController systemStateTreeNavigationController) {

        this.systemStateTreeModel = systemStateTreeModel;
        this.systemStateTreeNavigationController = systemStateTreeNavigationController;

        this.statePanelMap = new HashMap<>();

        searchPanel = new SearchPanel<>(null, systemStateTreeModel.getSearchModel());

        setLayout(new BorderLayout());

        JPanel utilityJPanel = getUtilityJPanel();
        JPanel systemStateDataPanel = getSystemStateDataPanel();
        JPanel statusBarJPanel = getStatusBarJPanel();

        add(utilityJPanel, BorderLayout.NORTH);
        add(systemStateDataPanel, BorderLayout.CENTER);
        add(statusBarJPanel, BorderLayout.SOUTH);

        systemStateTreeModel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                String propertyName = evt.getPropertyName();

                if ("message".equals(propertyName)) {
                    Message message = (Message) evt.getNewValue();
                    GUIUtilities.setMessage(statusBar, message);
                }

            }
        });

        // JTree systemStateTree = getSystemStateTree();
        // JScrollPane scrollPane = new JScrollPane(systemStateTree);
        // scrollPane.setPreferredSize(systemStateTree.getPreferredSize());
        //
        // JPanel stateCardPanel = getStateCardPanel();
        //
        // JSplitPane systemStateSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stateCardPanel);
        //
        // systemStateSplitPane.setDividerLocation(400);
        //
        // add(systemStateSplitPane, BorderLayout.CENTER);
    }

    private SystemStateTree getSystemStateTree() {

        if (systemStateTree == null) {

            systemStateTree = new SystemStateTree(systemStateTreeModel);

            systemStateTreeNavigationController.setSystemStateTree(systemStateTree);

            TreeSelectionListener tsl = new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

                    JTree systemStateTree = getSystemStateTree();
                    SystemStateTreeModel systemStateTreeModel = (SystemStateTreeModel) systemStateTree.getModel();

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) systemStateTree
                            .getLastSelectedPathComponent();

                    if (node != null) {

                        Object userObject = node.getUserObject();

                        if (userObject != null) {

                            switchStatePanel(systemStateTreeModel, userObject);
                        }
                    }
                }
            };

            systemStateTree.addTreeSelectionListener(tsl);

            TreeSelectionModel tsm = systemStateTree.getSelectionModel();

            tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

            systemStateTree.setRowHeight(20);

            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) systemStateTreeModel.getRoot();

            TreePath treePath = new TreePath(rootNode.getPath());

            systemStateTree.scrollPathToVisible(treePath);
            systemStateTree.setSelectionPath(treePath);
        }

        return systemStateTree;
    }

    private JPanel getStateCardPanel() {

        if (stateCardPanel == null) {
            stateCardPanel = new JPanel(new CardLayout());
        }

        return stateCardPanel;
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

    private JPanel getUtilityJPanel() {

        JPanel utilityJPanel = new JPanel();

        LayoutManager layout = new BoxLayout(utilityJPanel, BoxLayout.LINE_AXIS);
        utilityJPanel.setLayout(layout);

        // utilityJPanel.add(searchPanel);

        return utilityJPanel;
    }

    private JPanel getSystemStateDataPanel() {

        JTree systemStateTree = getSystemStateTree();
        JScrollPane scrollPane = new JScrollPane(systemStateTree);
        scrollPane.setPreferredSize(systemStateTree.getPreferredSize());

        JPanel stateCardPanel = getStateCardPanel();

        JSplitPane systemStateSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, stateCardPanel);

        systemStateSplitPane.setDividerLocation(400);

        JPanel systemStateDataPanel = new JPanel();
        systemStateDataPanel.setLayout(new BorderLayout());

        systemStateDataPanel.add(systemStateSplitPane, BorderLayout.CENTER);

        return systemStateDataPanel;
    }

    private JPanel getStatusBarJPanel() {

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

    private HashMap<String, JComponent> getStatePanelMap() {
        return statePanelMap;
    }

    private void switchStatePanel(SystemStateTreeModel systemStateTreeModel, Object userObject) {

        RecentFile recentFile = systemStateTreeModel.getRecentFile();

        JPanel stateCardPanel = getStateCardPanel();

        HashMap<String, JComponent> statePanelMap = getStatePanelMap();

        String state = null;
        JComponent statePanel = null;

        if (userObject instanceof SystemState) {

            SystemState systemState = (SystemState) userObject;

            state = systemState.getDisplayName();

            statePanel = statePanelMap.get(state);

            if (statePanel == null) {

                statePanel = new SystemStatePanel(systemStateTreeNavigationController, systemState);

                statePanelMap.put(state, statePanel);
                stateCardPanel.add(state, statePanel);
            }

        } else if (userObject instanceof ClusterState) {

            ClusterState clusterState = (ClusterState) userObject;

            state = clusterState.getDisplayName();

            statePanel = statePanelMap.get(state);

            if (statePanel == null) {

                statePanel = new ClusterStatePanel(clusterState, recentFile);

                statePanelMap.put(state, statePanel);
                stateCardPanel.add(state, statePanel);
            }

        } else if (userObject instanceof SearchState) {

            SearchState searchState = (SearchState) userObject;

            state = searchState.getDisplayName();

            statePanel = statePanelMap.get(state);

            if (statePanel == null) {

                statePanel = new SearchStatePanel(searchState, recentFile);

                statePanelMap.put(state, statePanel);
                stateCardPanel.add(state, statePanel);
            }

        } else if (userObject instanceof DatastoreMetadata) {

            DatastoreMetadata datastoreMetadata = (DatastoreMetadata) userObject;

            state = datastoreMetadata.getDisplayName();

            statePanel = statePanelMap.get(state);

            if (statePanel == null) {

                statePanel = new DatastoreMetadataPanel(datastoreMetadata);

                statePanelMap.put(state, statePanel);
                stateCardPanel.add(state, statePanel);
            }

        } else if (userObject instanceof NodeState) {

            NodeState nodeState = (NodeState) userObject;

            state = nodeState.getDisplayName();

            statePanel = statePanelMap.get(state);

            if (statePanel == null) {

                List<AnalysisMarker> analysisMarkerList = null;

                SystemState systemState = systemStateTreeModel.getSystemState();

                if (systemState != null) {

                    AnalysisMarkerListNodeMap analysisMarkerListNodeMap;
                    analysisMarkerListNodeMap = systemState.getAnalysisMarkerListNodeMap();

                    if (analysisMarkerListNodeMap != null) {

                        String nodeId = nodeState.getNodeId();
                        analysisMarkerList = analysisMarkerListNodeMap.getAnalysisMarkerList(nodeId);
                    }
                }

                statePanel = new NodeStatePanel(nodeState, analysisMarkerList);

                statePanelMap.put(state, statePanel);
                stateCardPanel.add(state, statePanel);
            }
        }

        CardLayout cardLayout = (CardLayout) (stateCardPanel.getLayout());

        cardLayout.show(stateCardPanel, state);

    }
}
