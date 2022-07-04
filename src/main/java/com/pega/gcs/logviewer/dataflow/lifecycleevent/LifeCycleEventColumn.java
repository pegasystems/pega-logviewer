
package com.pega.gcs.logviewer.dataflow.lifecycleevent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

public enum LifeCycleEventColumn {

    // @formatter:off
    // CHECKSTYLE:OFF
    //               displayName   prefColumnWidth  horizontalAlignment    columnClass         filterable
    MESSAGEID        ("MESSAGE ID"          , 200 , SwingConstants.CENTER , String.class       , false ),
    TIMESTAMP        ("TIMESTAMP"           , 160 , SwingConstants.CENTER , String.class       , false ),
    EVENT_TYPE       ("EVENT TYPE"          , 200 , SwingConstants.CENTER , String.class       , true  ),
    SENDER_NODE_ID   ("SENDER NODE ID"      , 200 , SwingConstants.CENTER , String.class       , true  ),

    RUN_ID           ("RUN ID"              , 160 , SwingConstants.CENTER , String.class       , true  ),
    ORIGINATOR       ("ORIGINATOR"          , 160 , SwingConstants.CENTER , String.class       , true  ),
    REASON           ("REASON"              , 200 , SwingConstants.CENTER , String.class       , true  ),
    PARTITION_STATUS ("PARTITION STATUS"    , 160 , SwingConstants.CENTER , String.class       , true  ),
    PREVIOUS_STATUS  ("PREVIOUS STATUS"     , 160 , SwingConstants.CENTER , String.class       , true  ),

    INTENTION        ("INTENTION"           , 200 , SwingConstants.CENTER , String.class       , true  ),
    PARTITIONS       ("PARTITIONS"          , 200 , SwingConstants.CENTER , String.class       , true  ),
    THREAD_NAME      ("THREAD NAME"         , 300 , SwingConstants.CENTER , String.class       , true  ),
    EVENT            ("EVENT"               , 160 , SwingConstants.CENTER , String.class       , true  );
    // CHECKSTYLE:ON
    // @formatter:on

    private final String name;

    private final int prefColumnWidth;

    private final int horizontalAlignment;

    private final Class<?> columnClass;

    private final boolean filterable;

    private LifeCycleEventColumn(String name, int prefColumnWidth, int horizontalAlignment, Class<?> columnClass,
            boolean filterable) {
        this.name = name;
        this.prefColumnWidth = prefColumnWidth;
        this.horizontalAlignment = horizontalAlignment;
        this.columnClass = columnClass;
        this.filterable = filterable;
    }

    public String getName() {
        return name;
    }

    public int getPrefColumnWidth() {
        return prefColumnWidth;
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public Class<?> getColumnClass() {
        return columnClass;
    }

    public boolean isFilterable() {
        return filterable;
    }

    @Override
    public String toString() {
        return name;
    }

    public static int getColumnNameIndex(LifeCycleEventColumn lifeCycleEventColumn) {

        int index = -1;
        int counter = 0;

        for (LifeCycleEventColumn column : values()) {

            if (column.equals(lifeCycleEventColumn)) {
                index = counter;
                break;
            }

            counter++;
        }

        return index;
    }

    public static List<LifeCycleEventColumn> getLifeCycleEventColumnList() {

        List<LifeCycleEventColumn> lifeCycleEventColumnList = new ArrayList<>();

        lifeCycleEventColumnList.add(MESSAGEID);
        lifeCycleEventColumnList.add(TIMESTAMP);
        lifeCycleEventColumnList.add(EVENT_TYPE);
        lifeCycleEventColumnList.add(SENDER_NODE_ID);
        lifeCycleEventColumnList.add(RUN_ID);
        lifeCycleEventColumnList.add(ORIGINATOR);
        lifeCycleEventColumnList.add(REASON);
        lifeCycleEventColumnList.add(PARTITION_STATUS);
        lifeCycleEventColumnList.add(PREVIOUS_STATUS);
        lifeCycleEventColumnList.add(INTENTION);
        lifeCycleEventColumnList.add(PARTITIONS);
        lifeCycleEventColumnList.add(THREAD_NAME);
        lifeCycleEventColumnList.add(EVENT);

        return lifeCycleEventColumnList;
    }

}
