
package com.pega.gcs.logviewer.systemstate;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.pega.gcs.fringecommon.guiutilities.NavigationController;

public class SystemStateTreeNavigationController implements NavigationController<String> {

    private SystemStateTree systemStateTree;

    private SystemStateTreeModel systemStateTreeModel;

    public SystemStateTreeNavigationController(SystemStateTreeModel systemStateTreeModel) {
        this.systemStateTreeModel = systemStateTreeModel;
    }

    private SystemStateTreeModel getSystemStateTreeModel() {
        return systemStateTreeModel;
    }

    public SystemStateTree getSystemStateTree() {
        return systemStateTree;
    }

    public void setSystemStateTree(SystemStateTree systemStateTree) {
        this.systemStateTree = systemStateTree;
    }

    @Override
    public void navigateToRow(int startRowIndex, int endRowIndex) {
        // TODO Auto-generated method stub
    }

    @Override
    public void scrollToKey(String key) {

        SystemStateTree systemStateTree = getSystemStateTree();

        if (systemStateTree != null) {

            SystemStateTreeModel systemStateTreeModel = getSystemStateTreeModel();

            DefaultMutableTreeNode defaultMutableTreeNode = systemStateTreeModel.getSystemStateNode(key);

            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());

            systemStateTree.setSelectionPath(treePath);
            systemStateTree.scrollPathToVisible(treePath);

        }
    }

}
