
package com.pega.gcs.logviewer.catalog.model;

import java.util.Comparator;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class HotfixIDComparator implements Comparator<String> {

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixIDComparator.class);

    @Override
    public int compare(String hotfixId1, String hotfixId2) {

        int compareValue = 0;

        try {

            String hotfixId1NumberPart = hotfixId1.split("-", 0)[1];
            String hotfixId2NumberPart = hotfixId2.split("-", 0)[1];

            Integer hotfixId1Number = Integer.parseInt(hotfixId1NumberPart);
            Integer hotfixId2Number = Integer.parseInt(hotfixId2NumberPart);

            compareValue = hotfixId1Number.compareTo(hotfixId2Number);

        } catch (Exception e) {
            compareValue = 0;
            LOG.error("Error comparing Hotfix Ids '" + hotfixId1 + "' and '" + hotfixId2 + "'", e);
        }

        return compareValue;
    }

}
