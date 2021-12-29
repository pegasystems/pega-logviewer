
package com.pega.gcs.logviewer.systemstate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.logviewer.systemstate.model.ClusterState;
import com.pega.gcs.logviewer.systemstate.model.NodeObject;
import com.pega.gcs.logviewer.systemstate.model.NodeState;
import com.pega.gcs.logviewer.systemstate.model.SystemState;
import com.pega.gcs.logviewer.systemstate.model.SystemStateError;

public final class SystemStateUtil {

    public SystemStateUtil() {
        // TODO Auto-generated constructor stub
    }

    public static SystemState getSystemState(File ssJsonFile) throws JsonProcessingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        // DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        //
        // VisibilityChecker<?> visibilityChecker = deserializationConfig.getDefaultVisibilityChecker()
        // .withFieldVisibility(JsonAutoDetect.Visibility.NONE).withGetterVisibility(JsonAutoDetect.Visibility.ANY)
        // .withIsGetterVisibility(JsonAutoDetect.Visibility.ANY)
        // .withSetterVisibility(JsonAutoDetect.Visibility.ANY);
        //
        // objectMapper.setVisibility(visibilityChecker);

        JsonNode jsonNode = objectMapper.readTree(ssJsonFile);

        SystemState systemState = new SystemState();

        JsonNode statusJsonNode = jsonNode.get("status");
        JsonNode servedByJsonNode = jsonNode.get("servedBy");
        JsonNode errorJsonNode = jsonNode.get("error");
        JsonNode dataJsonNode = jsonNode.get("data");

        systemState.setStatus(statusJsonNode.textValue());
        systemState.setServedBy(servedByJsonNode.textValue());

        JsonNode nodeStateJsonNode = dataJsonNode.get("nodeState");

        if (nodeStateJsonNode.isArray()) {

            // cluster level system state
            JsonNode clusterStateJsonNode = dataJsonNode.get("clusterState");

            ClusterState clusterState = objectMapper.treeToValue(clusterStateJsonNode, ClusterState.class);

            systemState.setClusterState(clusterState);

            Iterator<JsonNode> nodeStateIt = nodeStateJsonNode.elements();

            while (nodeStateIt.hasNext()) {

                JsonNode nsJsonNode = nodeStateIt.next();

                NodeState nodeState = objectMapper.treeToValue(nsJsonNode, NodeState.class);

                systemState.addNodeState(nodeState);
            }
        } else {
            // node system state
            NodeState nodeState = objectMapper.treeToValue(nodeStateJsonNode, NodeState.class);

            systemState.addNodeState(nodeState);
        }

        if (errorJsonNode.isArray()) {

            Iterator<JsonNode> errorIt = errorJsonNode.elements();

            while (errorIt.hasNext()) {

                JsonNode errJsonNode = errorIt.next();

                SystemStateError error = objectMapper.treeToValue(errJsonNode, SystemStateError.class);

                systemState.addError(error);
            }
        }

        systemState.postProcess();

        return systemState;
    }

    public static JPanel getTitlePanel(NodeObject nodeObject) {

        JPanel titlePanel = new JPanel();

        LayoutManager layout = new BoxLayout(titlePanel, BoxLayout.X_AXIS);
        titlePanel.setLayout(layout);

        JLabel titleLabel = new JLabel(nodeObject.getDisplayName());
        Font labelFont = titleLabel.getFont();
        Font titleFont = labelFont.deriveFont(Font.BOLD, 14);
        titleLabel.setFont(titleFont);

        Dimension dim = new Dimension(10, 40);

        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(dim));
        titlePanel.add(Box.createHorizontalGlue());

        titlePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        return titlePanel;
    }

    public static void main(String[] args) {

        try {

            File ssJsonFile = new File("C:\\_DOWNLOADS\\SystemState_Cluster_20191205T060121.000 GMT.json");

            SystemStateUtil.getSystemState(ssJsonFile);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
