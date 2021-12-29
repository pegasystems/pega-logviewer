/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

public class ThreadDumpThreadInfoV6 extends ThreadDumpThreadInfo {

    private String priority;

    private String groupName;

    public ThreadDumpThreadInfoV6(Long threadID, String threadName, String threadText) {

        super(threadID, threadName, threadText);

        this.priority = null;
        this.groupName = null;

    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public Object getValue(String columnName) {

        Object value = null;

        // THREADNAME
        // ID
        // STACKMETHOD
        // STACKDEPTH
        // PEGATHREAD
        // PRIORITY
        // GROUPNAME

        switch (columnName) {
        case "THREADNAME":
            value = getThreadName();
            break;
        case "ID":
            value = getThreadId();
            break;
        case "STACKMETHOD":
            value = getStackMethod();
            break;
        case "STACKDEPTH":
            value = getStackDepth();
            break;
        case "PEGATHREAD":
            value = isPegaThread();
            break;
        case "PRIORITY":
            value = getPriority();
            break;
        case "GROUPNAME":
            value = getGroupName();
            break;
        default:
            value = null;
        }

        return value;
    }
}
