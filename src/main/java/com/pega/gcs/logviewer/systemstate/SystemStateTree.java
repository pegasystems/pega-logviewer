
package com.pega.gcs.logviewer.systemstate;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.pega.gcs.logviewer.systemstate.model.NodeObject;

public class SystemStateTree extends JTree {

    private static final long serialVersionUID = -8302037339007267650L;

    public SystemStateTree(SystemStateTreeModel systemStateTreeModel) {

        super(systemStateTreeModel);

        setRootVisible(true);
        setShowsRootHandles(true);

        DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer() {

            private static final long serialVersionUID = 6967086772869544871L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {

                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                setBorder(new EmptyBorder(1, 5, 1, 1));

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                if (node != null) {

                    Object userObject = node.getUserObject();
                    String displayName = null;

                    if (userObject instanceof NodeObject) {

                        NodeObject nodeObject = (NodeObject) userObject;

                        displayName = nodeObject.getDisplayName();

                    } else {
                        displayName = userObject.toString();
                    }

                    setText(displayName);
                    setToolTipText(displayName);
                }

                return this;
            }
        };

        dtcr.setIcon(null);
        dtcr.setOpenIcon(null);
        dtcr.setClosedIcon(null);
        dtcr.setLeafIcon(null);

        setCellRenderer(dtcr);
    }

}
