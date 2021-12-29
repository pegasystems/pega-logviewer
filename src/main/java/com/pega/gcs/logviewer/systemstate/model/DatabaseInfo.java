
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pega.gcs.logviewer.systemstate.deserialize.DatabaseInfoDeserializer;

@JsonDeserialize(using = DatabaseInfoDeserializer.class)
public class DatabaseInfo {

    @JsonProperty("DatabaseInfo")
    private TreeSet<Database> databaseSet;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public DatabaseInfo() {
        super();
        databaseSet = new TreeSet<>();
    }

    public Set<Database> getDatabaseSet() {
        return (databaseSet != null) ? Collections.unmodifiableSet(databaseSet) : null;
    }

    public String getPzError() {
        return pzError;
    }

    public void setPzError(String pzError) {
        this.pzError = pzError;
    }

    public boolean addDatabase(Database database) {

        boolean success = false;

        if (database != null) {
            success = databaseSet.add(database);
        }

        return success;
    }

    public void postProcess() {

        AtomicInteger index = new AtomicInteger(0);

        for (Database database : databaseSet) {

            database.setIndex(index.incrementAndGet());
        }
    }
}
