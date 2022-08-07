
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.IntentChangedMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.LifeCycleEventMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.PartitionStatusTransitionMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.ProcessedRunConfigUpdateMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.ProcessingThreadLifecycleMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.RetryContextMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.RunStatusTransitionMessage;
import com.pega.gcs.logviewer.dataflow.lifecycleevent.message.TaskRetryMessage;

public class LifeCycleEventMessageParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(LifeCycleEventMessageParser.class);

    private ObjectMapper objectMapper;

    public LifeCycleEventMessageParser() {
        this.objectMapper = new ObjectMapper();
    }

    public LifeCycleEventMessage getLifeCycleEventMessage(String messageJson) {

        LifeCycleEventMessage lifeCycleEventMessage = null;

        try {
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            JsonNode typeJsonNode = jsonNode.get("type");
            String messageType = typeJsonNode.asText();

            switch (messageType) {

            case ".IntentChangedMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, IntentChangedMessage.class);
                break;
            case ".PartitionStatusTransitionMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, PartitionStatusTransitionMessage.class);
                break;
            case ".ProcessingThreadLifecycleMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, ProcessingThreadLifecycleMessage.class);
                break;
            case ".RunStatusTransitionMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, RunStatusTransitionMessage.class);
                break;
            case ".ProcessedRunConfigUpdateMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, ProcessedRunConfigUpdateMessage.class);
                break;
            case ".retry.MaximumRetriesReachedMessage":
            case ".retry.RetriesStoppedMessage":
            case ".retry.RetryOperationMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, RetryContextMessage.class);
                break;
            case ".TaskRetryMessage":
                lifeCycleEventMessage = objectMapper.treeToValue(jsonNode, TaskRetryMessage.class);
                break;
            default:
                LOG.error("Unknown message type: " + messageType);
                break;
            }

        } catch (Exception e) {
            LOG.error("Could not deserialize from json: " + messageJson, e);
        }

        return lifeCycleEventMessage;
    }
}
