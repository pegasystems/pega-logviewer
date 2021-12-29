
package com.pega.gcs.logviewer.systemstate;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.pega.gcs.fringecommon.guiutilities.Message;
import com.pega.gcs.fringecommon.guiutilities.ModalProgressMonitor;
import com.pega.gcs.fringecommon.guiutilities.search.SearchData;
import com.pega.gcs.fringecommon.guiutilities.search.SearchModel;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.model.DatastoreMetadata;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.SearchState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;

public class SystemStateTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 5558755763791993018L;

    private SystemState systemState;

    // used for tree navigation
    private Map<String, DefaultMutableTreeNode> systemStateTreeNodeMap;

    private SearchModel<String> searchModel;

    private Message message;

    private PropertyChangeSupport propertyChangeSupport;

    public SystemStateTreeModel() {

        super(new DefaultMutableTreeNode(SystemState.SYSTEMSTATE_ROOT_NODE_NAME));

        systemState = null;

        systemStateTreeNodeMap = new HashMap<>();

        propertyChangeSupport = new PropertyChangeSupport(this);

        resetModel(systemState);
    }

    public SystemState getSystemState() {
        return systemState;
    }

    private PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {

        this.message = message;

        PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        propertyChangeSupport.firePropertyChange("message", null, message);
    }

    public void resetModel(SystemState systemState) {

        systemStateTreeNodeMap.clear();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.removeAllChildren();

        if (systemState != null) {

            this.systemState = systemState;

            root.setUserObject(systemState);

            systemStateTreeNodeMap.put(systemState.getDisplayName(), root);

            ClusterState clusterState = systemState.getClusterState();

            if (clusterState != null) {

                DefaultMutableTreeNode clusterStateNode = new DefaultMutableTreeNode(clusterState);

                root.add(clusterStateNode);

                systemStateTreeNodeMap.put(clusterState.getDisplayName(), clusterStateNode);
            }

            SearchState searchState = systemState.getSearchState();

            if (searchState != null) {

                DefaultMutableTreeNode searchStateNode = new DefaultMutableTreeNode(searchState);

                root.add(searchStateNode);

                systemStateTreeNodeMap.put(searchState.getDisplayName(), searchStateNode);
            }

            DatastoreMetadata datastoreMetadata = systemState.getDatastoreMetadata();

            if (datastoreMetadata != null) {

                DefaultMutableTreeNode datastoreMetadataNode = new DefaultMutableTreeNode(datastoreMetadata);

                root.add(datastoreMetadataNode);

                systemStateTreeNodeMap.put(datastoreMetadata.getDisplayName(), datastoreMetadataNode);
            }

            for (NodeState nodeState : systemState.getNodeStateCollection()) {

                DefaultMutableTreeNode nodeStateNode = new DefaultMutableTreeNode(nodeState);

                root.add(nodeStateNode);

                systemStateTreeNodeMap.put(nodeState.getDisplayName(), nodeStateNode);
            }

        }

        nodeStructureChanged(root);

    }

    public DefaultMutableTreeNode getSystemStateNode(String nodeKey) {
        return systemStateTreeNodeMap.get(nodeKey);
    }

    public SearchModel<String> getSearchModel() {

        if (searchModel == null) {

            SearchData<String> searchData = new SearchData<>(null);

            searchModel = new SearchModel<String>(searchData) {

                @Override
                public void resetResults(boolean clearResults) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void searchInEvents(Object searchStrObj, ModalProgressMonitor modalProgressMonitor) {
                    // TODO Auto-generated method stub

                }
            };
        }
        return searchModel;
    }

}
