
package com.pega.gcs.logviewer.systemstate.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.guiutilities.datatable.AbstractDataTableModel;
import com.pega.gcs.logviewer.systemstate.model.RequestorPool;
import com.pega.gcs.logviewer.systemstate.model.RequestorsResult;

public class RequestorPoolTableModel extends AbstractDataTableModel<RequestorPool, RequestorPoolTableColumn> {

    private static final long serialVersionUID = -2043727343347541824L;

    private Map<Integer, RequestorPool> dataMap;

    private List<RequestorPoolTableColumn> columnList;

    public RequestorPoolTableModel(RequestorsResult requestorsResult) {

        columnList = RequestorPoolTableColumn.getColumnList();

        dataMap = new HashMap<>();

        Set<RequestorPool> requestorPoolSet = requestorsResult.getRequestorPoolSet();

        if (requestorPoolSet != null) {
            for (RequestorPool data : requestorPoolSet) {
                dataMap.put(data.getIndex(), data);
            }
        }

        reset();

    }

    @Override
    protected Map<Integer, RequestorPool> getDataMap() {
        return dataMap;
    }

    @Override
    protected List<RequestorPoolTableColumn> getColumnList() {
        return columnList;
    }

    @Override
    protected String getColumnData(RequestorPool data, RequestorPoolTableColumn dataTableColumn) {

        String columndata = null;

        if (dataTableColumn.equals(DSSInfoTableColumn.SNO)) {
            columndata = Integer.toString(data.getIndex());
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.TIMEOUTCOUNT)) {
            Long timeoutCount = data.getTimeoutCount();
            columndata = (timeoutCount != null) ? Long.toString(timeoutCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.IDLECOUNT)) {
            Integer idleCount = data.getIdleCount();
            columndata = (idleCount != null) ? Integer.toString(idleCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.MAXIDLECOUNT)) {
            Integer maxIdleCount = data.getMaxIdleCount();
            columndata = (maxIdleCount != null) ? Integer.toString(maxIdleCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.MOSTIDLECOUNT)) {
            Integer mostIdleCount = data.getMostIdleCount();
            columndata = (mostIdleCount != null) ? Integer.toString(mostIdleCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.MAXWAITTIME)) {
            Long maxWaitTime = data.getMaxWaitTime();
            columndata = (maxWaitTime != null) ? Long.toString(maxWaitTime) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.SERVICEPACKAGEINSNAME)) {
            columndata = data.getServicePackageInsName();
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.ACCESSGROUPNAME)) {
            columndata = data.getAccessGroupName();
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.APPLICATIONINFO)) {
            columndata = data.getApplicationInfo();
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.LONGESTWAITTIME)) {
            Long longestWaitTime = data.getLongestWaitTime();
            columndata = (longestWaitTime != null) ? Long.toString(longestWaitTime) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.MAXACTIVECOUNT)) {
            Integer maxActiveCount = data.getMaxActiveCount();
            columndata = (maxActiveCount != null) ? Integer.toString(maxActiveCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.MOSTACTIVECOUNT)) {
            Integer mostActiveCount = data.getMostActiveCount();
            columndata = (mostActiveCount != null) ? Integer.toString(mostActiveCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.ACTIVECOUNT)) {
            Integer activeCount = data.getActiveCount();
            columndata = (activeCount != null) ? Integer.toString(activeCount) : null;
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.LASTACCESSTIME)) {
            columndata = data.getLastAccessTime();
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.SERVICEPACKAGENAME)) {
            columndata = data.getServicePackageName();
        } else if (dataTableColumn.equals(RequestorPoolTableColumn.ACCESSGROUPLIST)) {
            columndata = data.getAccessGroupList().toString();
        }

        return columndata;
    }
}
