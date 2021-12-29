/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.utilities.KeyValuePair;

public class MasterAgentSystemPattern {

    private Pattern pattern;

    private LinkedHashMap<String, Integer> fieldNameGroupIndexMap;

    public MasterAgentSystemPattern(Pattern pattern, LinkedHashMap<String, Integer> fieldNameGroupCountMap) {
        super();
        this.pattern = pattern;
        this.fieldNameGroupIndexMap = fieldNameGroupCountMap;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public LinkedHashMap<String, Integer> getFieldNameGroupIndexMap() {
        return fieldNameGroupIndexMap;
    }

    @SuppressWarnings("unchecked")
    public static List<MasterAgentSystemPattern> getDefaultMasterAgentSystemPatternList() {

        List<MasterAgentSystemPattern> defaultMasterAgentSystemPatternList = new ArrayList<>();

        MasterAgentSystemPattern masterAgentSystemPattern;

        String patternStr;
        KeyValuePair<String, Integer> kvp1;
        KeyValuePair<String, Integer> kvp2;
        KeyValuePair<String, Integer> kvp3;
        KeyValuePair<String, Integer> kvp4;
        KeyValuePair<String, Integer> kvp5;

        // group count start from 1. 0 is all.
        // @formatter:off
        // CHECKSTYLE:OFF
        patternStr = "System date:[ ]+(.*)[ ]+Total[ ]+memory:[ ]+(.*)[ ]+Free[ ]+memory:[ ]+(.*)[ ]+Total[ ]+cpu[ ]+seconds:[ ]+(.*)[ ]+Requestor[ ]+Count:[ ]+(.*)[ ]+Shared[ ]+Pages[ ]+memory[ ]+usage:[ ]+(.*)%";
        // CHECKSTYLE:ON
        // @formatter:on

        kvp1 = new KeyValuePair<>(Log4jLogEntryModel.TS_TOTAL_MEMORY, 2);
        kvp2 = new KeyValuePair<>(Log4jLogEntryModel.TS_FREE_MEMORY, 3);
        kvp3 = new KeyValuePair<>(Log4jLogEntryModel.TS_TOTAL_CPU_SECONDS, 4);
        kvp4 = new KeyValuePair<>(Log4jLogEntryModel.TS_REQUESTOR_COUNT, 5);
        kvp5 = new KeyValuePair<>(Log4jLogEntryModel.TS_SHARED_PAGE_MEMORY, 6);

        masterAgentSystemPattern = buildMasterAgentSystemPattern(patternStr, kvp1, kvp2, kvp3, kvp4, kvp5);
        defaultMasterAgentSystemPatternList.add(masterAgentSystemPattern);

        // @formatter:off
        // CHECKSTYLE:OFF
        // System date: Wed Mar 29 00:21:09 EDT 2017 Total memory: 25,662,455,808 Free memory: 24,707,612,952 Requestor Count: 15 Shared Pages memory usage: 0% Current number of threads: 87
        patternStr = "System date:[ ]+(.*)[ ]+Total memory:[ ]+(.*)[ ]+Free memory:[ ]+(.*)[ ]+Requestor Count:[ ]+(.*)[ ]+Shared Pages memory usage:[ ]+(.*)%[ ]+Current number of threads:[ ]+(.*?)(\\s.*)?";
        // CHECKSTYLE:ON
        // @formatter:on

        kvp1 = new KeyValuePair<>(Log4jLogEntryModel.TS_TOTAL_MEMORY, 2);
        kvp2 = new KeyValuePair<>(Log4jLogEntryModel.TS_FREE_MEMORY, 3);
        kvp3 = new KeyValuePair<>(Log4jLogEntryModel.TS_REQUESTOR_COUNT, 4);
        kvp4 = new KeyValuePair<>(Log4jLogEntryModel.TS_SHARED_PAGE_MEMORY, 5);
        kvp5 = new KeyValuePair<>(Log4jLogEntryModel.TS_NUMBER_OF_THREADS, 6);

        masterAgentSystemPattern = buildMasterAgentSystemPattern(patternStr, kvp1, kvp2, kvp3, kvp4, kvp5);
        defaultMasterAgentSystemPatternList.add(masterAgentSystemPattern);

        // @formatter:off
        // CHECKSTYLE:OFF
        // System date: Fri Jul 29 14:17:48 BST 2016 Total memory: 541,065,216 Free memory: 312,151,184 Requestor Count: 8 Shared Pages memory usage: 0%
        patternStr = "System date:[ ]+(.*)[ ]+Total memory:[ ]+(.*)[ ]+Free memory:[ ]+(.*)[ ]+Requestor Count:[ ]+(.*)[ ]+Shared Pages memory usage:[ ]+(.*)%.*";
        // CHECKSTYLE:ON
        // @formatter:on

        kvp1 = new KeyValuePair<>(Log4jLogEntryModel.TS_TOTAL_MEMORY, 2);
        kvp2 = new KeyValuePair<>(Log4jLogEntryModel.TS_FREE_MEMORY, 3);
        kvp3 = new KeyValuePair<>(Log4jLogEntryModel.TS_REQUESTOR_COUNT, 4);
        kvp4 = new KeyValuePair<>(Log4jLogEntryModel.TS_SHARED_PAGE_MEMORY, 5);

        masterAgentSystemPattern = buildMasterAgentSystemPattern(patternStr, kvp1, kvp2, kvp3, kvp4);

        defaultMasterAgentSystemPatternList.add(masterAgentSystemPattern);

        return defaultMasterAgentSystemPatternList;
    }

    @SuppressWarnings("unchecked")
    private static MasterAgentSystemPattern buildMasterAgentSystemPattern(String patternStr,
            KeyValuePair<String, Integer>... keyValuePairs) {

        Pattern pattern = Pattern.compile(patternStr);

        LinkedHashMap<String, Integer> fieldNameGroupIndexMap = new LinkedHashMap<>();

        for (KeyValuePair<String, Integer> keyValuePair : keyValuePairs) {

            String key = keyValuePair.getKey();
            Integer value = keyValuePair.getValue();

            fieldNameGroupIndexMap.put(key, value);
        }

        MasterAgentSystemPattern masterAgentSystemPattern;
        masterAgentSystemPattern = new MasterAgentSystemPattern(pattern, fieldNameGroupIndexMap);

        return masterAgentSystemPattern;
    }

    public static MasterAgentSystemPattern getMasterAgentSystemPattern(String systemStr) {

        MasterAgentSystemPattern masterAgentSystemPattern = null;

        List<MasterAgentSystemPattern> defaultMasterAgentSystemPatternList;
        defaultMasterAgentSystemPatternList = getDefaultMasterAgentSystemPatternList();

        for (MasterAgentSystemPattern maSystemPattern : defaultMasterAgentSystemPatternList) {

            Pattern masterAgentLogPattern = maSystemPattern.getPattern();

            Matcher matcher = masterAgentLogPattern.matcher(systemStr);

            if (matcher.matches()) {
                masterAgentSystemPattern = maSystemPattern;
                break;
            }
        }

        return masterAgentSystemPattern;
    }

    @Override
    public String toString() {
        return "MasterAgentSystemPattern [pattern=" + pattern + ", fieldNameGroupIndexMap=" + fieldNameGroupIndexMap
                + "]";
    }

    public static void main(String[] args) {

        // @formatter:off
        // CHECKSTYLE:OFF
        String sysString1 = "System date: Fri Jul 29 14:17:48 BST 2016 Total memory: 541,065,216 Free memory: 312,151,184 Requestor Count: 8 Shared Pages memory usage: 0%";
        String sysString2 = "System date: Wed Mar 29 00:21:09 EDT 2017 Total memory: 25,662,455,808 Free memory: 24,707,612,952 Requestor Count: 15 Shared Pages memory usage: 0% Current number of threads: 87";
        String sysString3 = "System date: Wed Mar 29 00:21:09 EDT 2017 Total memory: 25,662,455,808 Free memory: 24,707,612,952 Requestor Count: 15 Shared Pages memory usage: 0% Current number of threads: 87 Custom field: 23232";
        // CHECKSTYLE:ON
        // @formatter:on

        MasterAgentSystemPattern masterAgentSystemPattern = getMasterAgentSystemPattern(sysString1);
        Pattern pattern = masterAgentSystemPattern.getPattern();
        Matcher matcher = pattern.matcher(sysString1);

        if (matcher.matches()) {
            Map<String, Integer> fieldNameGroupIndexMap;
            fieldNameGroupIndexMap = masterAgentSystemPattern.getFieldNameGroupIndexMap();

            List<String> fieldNameList = new ArrayList<>(fieldNameGroupIndexMap.keySet());
            List<String> valueStrList = new ArrayList<>();

            for (String fieldName : fieldNameList) {

                int groupIndex = fieldNameGroupIndexMap.get(fieldName);
                String valueStr = matcher.group(groupIndex);
                valueStrList.add(valueStr);
            }

            System.out.println("fieldNameList: " + fieldNameList);
            System.out.println("valueStrList: " + valueStrList);
        }

        masterAgentSystemPattern = getMasterAgentSystemPattern(sysString2);
        pattern = masterAgentSystemPattern.getPattern();
        matcher = pattern.matcher(sysString2);

        if (matcher.matches()) {
            Map<String, Integer> fieldNameGroupIndexMap;
            fieldNameGroupIndexMap = masterAgentSystemPattern.getFieldNameGroupIndexMap();

            List<String> fieldNameList = new ArrayList<>(fieldNameGroupIndexMap.keySet());
            List<String> valueStrList = new ArrayList<>();

            for (String fieldName : fieldNameList) {

                int groupIndex = fieldNameGroupIndexMap.get(fieldName);
                String valueStr = matcher.group(groupIndex);
                valueStrList.add(valueStr);
            }

            System.out.println("fieldNameList: " + fieldNameList);
            System.out.println("valueStrList: " + valueStrList);
        }

        masterAgentSystemPattern = getMasterAgentSystemPattern(sysString3);
        pattern = masterAgentSystemPattern.getPattern();
        matcher = pattern.matcher(sysString3);

        if (matcher.matches()) {
            Map<String, Integer> fieldNameGroupIndexMap;
            fieldNameGroupIndexMap = masterAgentSystemPattern.getFieldNameGroupIndexMap();

            List<String> fieldNameList = new ArrayList<>(fieldNameGroupIndexMap.keySet());
            List<String> valueStrList = new ArrayList<>();

            for (String fieldName : fieldNameList) {

                int groupIndex = fieldNameGroupIndexMap.get(fieldName);
                String valueStr = matcher.group(groupIndex);
                valueStrList.add(valueStr);
            }

            System.out.println("fieldNameList: " + fieldNameList);
            System.out.println("valueStrList: " + valueStrList);
        }
    }
}
