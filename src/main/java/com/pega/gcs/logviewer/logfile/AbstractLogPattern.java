
package com.pega.gcs.logviewer.logfile;

public abstract class AbstractLogPattern {

    public enum LogType {
        PEGA_ALERT, PEGA_RULES/* Future - , WAS, WLS, JBOSS */
    }

    private LogType logType;

    private String name;

    private int groupCount;

    private boolean isCW;

    public AbstractLogPattern() {

    }

    public AbstractLogPattern(LogType logType, String name, int groupCount, boolean isCW) {

        super();

        this.logType = logType;
        this.name = name;
        this.groupCount = groupCount;
        this.isCW = isCW;
    }

    public LogType getLogType() {
        return logType;
    }

    public String getName() {
        return name;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public boolean isCW() {
        return isCW;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(" [ GroupCount(");
        builder.append(groupCount);
        builder.append(") CloudWatch(");
        builder.append(isCW);
        builder.append(")]");
        return builder.toString();
    }

}
