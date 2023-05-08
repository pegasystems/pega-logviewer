
package com.pega.gcs.logviewer.alert;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.AlertLogEntry;
import com.pega.gcs.logviewer.model.AlertLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;
import com.pega.gcs.logviewer.model.alert.AlertMessageListProvider;

public class AlertLogEntryPanelUtil {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertLogEntryPanelUtil.class);

    private static AlertLogEntryPanelUtil _INSTANCE;

    private ObjectMapper objectMapper;

    private Pattern pega0062Pattern;

    private AlertLogEntryPanelUtil() {

        this.objectMapper = JsonMapper.builder().disable(MapperFeature.ALLOW_COERCION_OF_SCALARS).build();
    }

    private ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private Pattern getPega0062Pattern() {

        if (pega0062Pattern == null) {

            String metricsRegex = "Data flow metrics are:(.*?)\\. Node data flow metrics:(.*)";

            pega0062Pattern = Pattern.compile(metricsRegex);
        }

        return pega0062Pattern;
    }

    public static AlertLogEntryPanelUtil getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new AlertLogEntryPanelUtil();
        }

        return _INSTANCE;
    }

    public Map<String, JComponent> getAdditionalAlertTabs(AlertLogEntryModel alertLogEntryModel,
            AlertLogEntry alertLogEntry) {

        Map<String, JComponent> additionalAlertTabs = null;

        int alertId = alertLogEntry.getAlertId();

        AlertMessage alertMessage = AlertMessageListProvider.getInstance().getAlertMessage(alertId);

        String messageId = alertMessage.getMessageID();

        switch (messageId) {

        case "PEGA0062":
            additionalAlertTabs = getPega0062Tabs(alertLogEntryModel, alertLogEntry);
            break;

        default:
            break;
        }

        return additionalAlertTabs;
    }

    public Map<String, JComponent> getPega0062Tabs(AlertLogEntryModel alertLogEntryModel, AlertLogEntry alertLogEntry) {

        LinkedHashMap<String, JComponent> tabMap = new LinkedHashMap<>();

        Pattern pattern = getPega0062Pattern();

        int messageIndex = alertLogEntryModel.getLogEntryColumnIndex(LogEntryColumn.MESSAGE);

        ArrayList<String> logEntryValueList = alertLogEntry.getLogEntryValueList();

        String message = logEntryValueList.get(messageIndex);

        Matcher patternMatcher = pattern.matcher(message);
        boolean matches = patternMatcher.find();

        if (matches) {

            String dfMetricTitle = "Dataflow Metrics";
            String dfMetricData = patternMatcher.group(1).trim();
            String nodeMetricTitle = "Node Metrics";
            String nodeMetricData = patternMatcher.group(2).trim();

            DateFormat displayDateFormat = alertLogEntryModel.getDisplayDateFormat();

            JComponent dfMetricComponent = getPega0062Component(dfMetricData, displayDateFormat);

            JComponent nodeMetricComponent = getPega0062Component(nodeMetricData, displayDateFormat);

            tabMap.put(dfMetricTitle, dfMetricComponent);

            tabMap.put(nodeMetricTitle, nodeMetricComponent);
        }

        return tabMap;
    }

    private JComponent getPega0062Component(String metricData, DateFormat displayDateFormat) {

        JComponent component = null;

        ObjectMapper objectMapper = getObjectMapper();

        try {

            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
            };

            Map<String, Object> fieldMap = objectMapper.readValue(metricData, typeRef);

            Pega0062Tab pega0062Tab = new Pega0062Tab(fieldMap, displayDateFormat);

            JScrollPane scrollPane = new JScrollPane(pega0062Tab, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            scrollPane.getVerticalScrollBar().setUnitIncrement(16);

            component = scrollPane;

        } catch (Exception e) {
            LOG.error("Error parsing PEGA0062 data", e);
        }

        return component;
    }

}
