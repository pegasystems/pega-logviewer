
package com.pega.gcs.logviewer.systemstate.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pega.gcs.fringecommon.utilities.KeyValuePair;

public class DSSInfo {

    @JsonProperty("Settings")
    private TreeSet<Setting> settingSet;

    @JsonProperty("PZ_ERROR")
    private String pzError;

    public DSSInfo() {
    }

    public Set<Setting> getSettingSet() {
        return Collections.unmodifiableSet(settingSet);
    }

    public String getPzError() {
        return pzError;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("DSSInfo [");

        boolean first = true;

        for (Setting setting : settingSet) {

            if (!first) {
                stringBuilder.append(",");
            }

            first = false;

            stringBuilder.append(setting);

        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public void postProcess() {

        if (settingSet != null) {

            AtomicInteger index = new AtomicInteger(0);

            for (Setting setting : settingSet) {

                setting.setIndex(index.incrementAndGet());

                setting.postProcess();
            }
        }
    }

    public List<KeyValuePair<String, String>> getPRConfigSettingList() {

        List<KeyValuePair<String, String>> prConfigSettingList = new ArrayList<>();

        if (settingSet != null) {

            String prConfigPrefix = "PRCONFIG/";

            for (Setting setting : settingSet) {

                String settingName = setting.getSettingName();

                if (settingName.startsWith(prConfigPrefix)) {

                    int beginIndex = prConfigPrefix.length();
                    int endIndex = settingName.lastIndexOf("/");
                    String prConfigSettingName = settingName.substring(beginIndex, endIndex);
                    String settingValue = setting.getValue();

                    KeyValuePair<String, String> kvp = new KeyValuePair<String, String>(prConfigSettingName,
                            settingValue);
                    prConfigSettingList.add(kvp);
                }

            }
        }

        return prConfigSettingList;

    }
}
