
package com.pega.gcs.logviewer.systemstate;

import java.util.EventObject;

public class DataChangeEvent extends EventObject {

    private static final long serialVersionUID = -66483842522326807L;

    public DataChangeEvent(Object source) {
        super(source);
    }

}
