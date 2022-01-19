
package com.pega.gcs.logviewer.logfile;

public class AlertLogPattern extends AbstractLogPattern {

    private AlertLogPattern() {
        // for kyro
    }

    public AlertLogPattern(String name, int groupCount, boolean isCW) {
        super(LogType.PEGA_ALERT, name, groupCount, isCW);
    }

}
