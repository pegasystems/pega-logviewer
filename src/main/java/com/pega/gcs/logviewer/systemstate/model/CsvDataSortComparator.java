
package com.pega.gcs.logviewer.systemstate.model;

import java.util.Comparator;
import java.util.List;

public class CsvDataSortComparator implements Comparator<CsvData> {

    private List<Integer> compareIndexList;
    private boolean ignoreCase;

    public CsvDataSortComparator(List<Integer> compareIndexList, boolean ignoreCase) {
        super();
        this.compareIndexList = compareIndexList;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public int compare(CsvData thisCsvData, CsvData otherCsvData) {

        int compare = 0;

        for (Integer index : compareIndexList) {

            if (index != -1) {

                String thisData = thisCsvData.getDataList().get(index);
                String otherData = otherCsvData.getDataList().get(index);

                if (ignoreCase) {
                    compare = thisData.compareToIgnoreCase(otherData);
                } else {
                    compare = thisData.compareTo(otherData);
                }

                if (compare != 0) {
                    break;
                }
            }
        }

        return compare;
    }

}
