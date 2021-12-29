
package com.pega.gcs.logviewer.socketreceiver;

public enum AppenderType {

    // @formatter:off
    // CHECKSTYLE:OFF
    PEGA_RULES        ("/PegaRULESSocketAppender.txt",  12121),
    PEGA_ALERT        ("/PegaALERTSocketAppender.txt",  13131);
    /* Future - CLUSTER, BIX ...*/
    // CHECKSTYLE:ON
    // @formatter:on

    private final String resourceName;

    private final int defaultPort;

    private AppenderType(String resourceName, int defaultPort) {
        this.resourceName = resourceName;
        this.defaultPort = defaultPort;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getDefaultPort() {
        return defaultPort;
    }
}