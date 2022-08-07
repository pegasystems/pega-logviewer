
package com.pega.gcs.logviewer.systemstate.deserialize;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.pega.gcs.logviewer.systemstate.model.Database;
import com.pega.gcs.logviewer.systemstate.model.DatabaseInfo;

/**
 * Some scenarios DatabaseInfo is not an array, therefore requiring a custom deserializer.
 *
 */
public class DatabaseInfoDeserializer extends JsonDeserializer<DatabaseInfo> {

    @Override
    public DatabaseInfo deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        DatabaseInfo databaseInfo;

        ObjectCodec objectCodec = jsonParser.getCodec();

        JsonNode databaseInfoJsonNode = objectCodec.readTree(jsonParser);

        JsonNode dbInfoJsonNode = databaseInfoJsonNode.get("DatabaseInfo");
        JsonNode pzErrorJsonNode = databaseInfoJsonNode.get("PZ_ERROR");

        databaseInfo = new DatabaseInfo();

        if (dbInfoJsonNode != null) {

            Iterator<JsonNode> dbInfoIt = dbInfoJsonNode.elements();

            while (dbInfoIt.hasNext()) {
                JsonNode dbJsonNode = dbInfoIt.next();

                Database database = objectCodec.treeToValue(dbJsonNode, Database.class);
                databaseInfo.addDatabase(database);
            }
        } else if (pzErrorJsonNode != null) {
            String pzError = pzErrorJsonNode.textValue();
            databaseInfo.setPzError(pzError);

        } else {
            Database database = objectCodec.treeToValue(databaseInfoJsonNode, Database.class);
            databaseInfo.addDatabase(database);
        }

        return databaseInfo;
    }

}
