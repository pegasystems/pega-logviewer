
package com.pega.gcs.logviewer.systemstate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvDataMap implements Comparable<CsvDataMap> {

    private String reportCsvName;

    private List<CsvHeader> csvHeaderList;

    private List<CsvData> csvDataList;

    public CsvDataMap(String reportCsvName, List<String> headerList, Map<Integer, List<String>> dataMap,
            CsvDataSortComparator csvDataSortComparator, List<String> filterableHeaderList) {
        super();

        this.reportCsvName = reportCsvName;

        this.csvHeaderList = new ArrayList<>();

        for (String header : headerList) {

            boolean filterable = filterableHeaderList.contains(header);
            CsvHeader csvHeader = new CsvHeader(header, filterable);
            csvHeaderList.add(csvHeader);
        }

        this.csvDataList = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : dataMap.entrySet()) {

            List<String> colDataList = entry.getValue();

            CsvData csvData = new CsvData(colDataList);

            if (csvDataSortComparator == null) {
                Integer index = entry.getKey();
                csvData.setIndex(index);
            }

            csvDataList.add(csvData);
        }

        if (csvDataSortComparator != null) {

            Collections.sort(csvDataList, csvDataSortComparator);

            AtomicInteger indexAi = new AtomicInteger(0);

            for (CsvData csvData : csvDataList) {
                csvData.setIndex(indexAi.incrementAndGet());
            }
        }
    }

    public String getReportCsvName() {
        return reportCsvName;
    }

    public List<CsvHeader> getCsvHeaderList() {
        return csvHeaderList;
    }

    public List<CsvData> getCsvDataList() {
        return csvDataList;
    }

    @Override
    public int compareTo(CsvDataMap other) {
        return getReportCsvName().compareTo(other.getReportCsvName());
    }

}
