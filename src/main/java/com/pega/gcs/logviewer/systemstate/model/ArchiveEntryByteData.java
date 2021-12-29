
package com.pega.gcs.logviewer.systemstate.model;

public class ArchiveEntryByteData {

    private String name;

    private byte[] databytes;

    public ArchiveEntryByteData(String name, byte[] databytes) {
        super();
        this.name = name;
        this.databytes = databytes;
    }

    public String getName() {
        return name;
    }

    public byte[] getDatabytes() {
        return databytes;
    }

}
