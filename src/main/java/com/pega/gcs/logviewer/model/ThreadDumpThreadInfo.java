/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ThreadDumpThreadInfo implements Comparable<ThreadDumpThreadInfo> {

    private long threadId;

    private String threadName;

    private String threadNameText;

    private boolean pegaThread;

    private String stackMethod;

    private int stackDepth;

    private List<String> threadSubLineList;

    public abstract Object getValue(String columnName);

    public ThreadDumpThreadInfo(Long threadId, String threadName, String threadText) {

        this.threadId = threadId;
        this.threadName = threadName;
        this.threadNameText = threadText;

        this.pegaThread = false;
        this.stackMethod = null;

        this.threadSubLineList = new ArrayList<String>();

    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getThreadNameText() {
        return threadNameText;
    }

    public boolean isPegaThread() {
        return pegaThread;
    }

    public void setPegaThread(Boolean pegaThread) {
        this.pegaThread = pegaThread;
    }

    public String getStackMethod() {
        return stackMethod;
    }

    public void setStackMethod(String stackMethod) {
        this.stackMethod = stackMethod;
    }

    public int getStackDepth() {
        return stackDepth;
    }

    public void setStackDepth(int stackDepth) {
        this.stackDepth = stackDepth;
    }

    public List<String> getThreadSubLineList() {
        return threadSubLineList;
    }

    // if case sensitive is false, then the string is passed as UPPER CASE.
    // otherwise the it is passed as it is.
    public boolean search(String searchStr, boolean casesensitive) {

        boolean found = false;

        String threadStr = threadNameText;

        if (!casesensitive) {
            threadStr = threadNameText.toUpperCase();
        }

        if (threadStr.contains(searchStr)) {
            found = true;
        } else {

            for (String threadSubLine : threadSubLineList) {

                threadStr = threadSubLine;

                if (!casesensitive) {
                    threadStr = threadSubLine.toUpperCase();
                }

                if (threadStr.contains(searchStr)) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public String getThreadDumpString() {

        StringBuilder tdsb = new StringBuilder();

        tdsb.append(threadNameText);
        tdsb.append(System.getProperty("line.separator"));

        for (String threadSubLine : threadSubLineList) {
            tdsb.append(threadSubLine);
            tdsb.append(System.getProperty("line.separator"));
        }

        return tdsb.toString();
    }

    @Override
    public String toString() {
        return getThreadName();
    }

    @Override
    public int compareTo(ThreadDumpThreadInfo other) {
        return getThreadName().compareTo(other.getThreadName());
    }

}
