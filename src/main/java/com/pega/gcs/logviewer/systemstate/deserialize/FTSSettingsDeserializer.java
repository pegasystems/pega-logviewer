
package com.pega.gcs.logviewer.systemstate.deserialize;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.pega.gcs.logviewer.systemstate.model.FTSSettings;

public class FTSSettingsDeserializer extends JsonDeserializer<FTSSettings> {

    @Override
    public FTSSettings deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        FTSSettings ftsSettings = new FTSSettings();

        ObjectCodec objectCodec = jsonParser.getCodec();

        JsonNode ftsSettingsJsonNode = objectCodec.readTree(jsonParser);
        Iterator<Map.Entry<String, JsonNode>> ftsSettingsJsonNodeIt = ftsSettingsJsonNode.fields();

        while (ftsSettingsJsonNodeIt.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = ftsSettingsJsonNodeIt.next();

            String key = fieldEntry.getKey();
            String value = fieldEntry.getValue().asText();

            ftsSettings.addField(key, value);
        }

        return ftsSettings;
    }

}
