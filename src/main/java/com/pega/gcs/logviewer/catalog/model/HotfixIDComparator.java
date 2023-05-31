
package com.pega.gcs.logviewer.catalog.model;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;

public class HotfixIDComparator implements Comparator<String> {

    private static final Log4j2Helper LOG = new Log4j2Helper(HotfixIDComparator.class);

    private Pattern hotfixIdPattern = Pattern.compile(".*-([a-zA-Z])?(\\d+)$");

    private Pattern getHotfixIdPattern() {
        return hotfixIdPattern;
    }

    @Override
    public int compare(String hotfixId1, String hotfixId2) {

        int compareValue = 0;

        try {

            Pattern hotfixIdPattern = getHotfixIdPattern();

            Matcher hotfixId1Matcher = hotfixIdPattern.matcher(hotfixId1);
            Matcher hotfixId2Matcher = hotfixIdPattern.matcher(hotfixId2);

            boolean hotfixId1Matches = hotfixId1Matcher.find();
            boolean hotfixId2Matches = hotfixId2Matcher.find();

            if (hotfixId1Matches && hotfixId2Matches) {

                String hotfixId1AlphaPart = hotfixId1Matcher.group(1);
                String hotfixId1NumPart = hotfixId1Matcher.group(2);

                String hotfixId2AlphaPart = hotfixId2Matcher.group(1);
                String hotfixId2NumPart = hotfixId2Matcher.group(2);

                hotfixId1AlphaPart = hotfixId1AlphaPart != null ? hotfixId1AlphaPart : "";
                hotfixId2AlphaPart = hotfixId2AlphaPart != null ? hotfixId2AlphaPart : "";

                compareValue = hotfixId1AlphaPart.compareTo(hotfixId2AlphaPart);

                if (compareValue == 0) {

                    int hotfixId1Num = Integer.parseInt(hotfixId1NumPart);
                    int hotfixId2Num = Integer.parseInt(hotfixId2NumPart);

                    compareValue = Integer.compare(hotfixId1Num, hotfixId2Num);
                }
            } else {
                compareValue = hotfixId1.compareTo(hotfixId2);
            }

        } catch (Exception e) {
            compareValue = 0;
            LOG.error("Error comparing Hotfix Ids '" + hotfixId1 + "' and '" + hotfixId2 + "'", e);
        }

        return compareValue;
    }

}
