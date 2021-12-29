
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pega.gcs.logviewer.systemstate.deserialize.FTSSettingsDeserializer;

@JsonDeserialize(using = FTSSettingsDeserializer.class)
public class FTSSettings {

    private TreeMap<String, String> fieldValueMap;

    public FTSSettings() {
        fieldValueMap = new TreeMap<>();
    }

    public void addField(String key, String value) {
        fieldValueMap.put(key, value);
    }

    public Map<String, String> getFieldValueMap() {
        return Collections.unmodifiableMap(fieldValueMap);
    }

}
