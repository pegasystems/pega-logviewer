
package com.pega.gcs.logviewer.catalog.model;

public class CatalogLoadException extends Exception {

    private static final long serialVersionUID = -2922457082120407738L;

    public CatalogLoadException() {
        this("Catalog could not be loaded");
    }

    public CatalogLoadException(String message) {
        super(message);
    }

    public CatalogLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
