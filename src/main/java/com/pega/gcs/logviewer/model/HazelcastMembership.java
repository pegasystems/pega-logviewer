
package com.pega.gcs.logviewer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class HazelcastMembership implements Comparable<HazelcastMembership> {

    public enum HzMembershipEvent {
        MEMBER_ADDED, MEMBER_LEFT
    }

    private Integer index;

    private LogEntryKey beginKey;

    private HzMembershipEvent hzMembershipEvent;

    private HazelcastMemberInfo hazelcastMemberInfo;

    private int memberCount;

    private List<HazelcastMemberInfo> hazelcastMemberInfoList;

    private boolean error;

    public HazelcastMembership(Integer index, LogEntryKey beginKey, HzMembershipEvent hzMembershipEvent,
            HazelcastMemberInfo hazelcastMemberInfo) {
        super();
        this.index = index;
        this.beginKey = beginKey;
        this.hzMembershipEvent = hzMembershipEvent;
        this.hazelcastMemberInfo = hazelcastMemberInfo;

        this.hazelcastMemberInfoList = new ArrayList<>();
        this.error = false;
    }

    public Integer getIndex() {
        return index;
    }

    public LogEntryKey getBeginKey() {
        return beginKey;
    }

    public HzMembershipEvent getHzMembershipEvent() {
        return hzMembershipEvent;
    }

    public HazelcastMemberInfo getHazelcastMemberInfo() {
        return hazelcastMemberInfo;
    }

    public List<HazelcastMemberInfo> getHazelcastMemberInfoList() {
        return hazelcastMemberInfoList;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isError() {
        return error;
    }

    public String getDisplayString(LogEntryModel logEntryModel) {

        String timeText = logEntryModel.getLogEntryTimeDisplayString(beginKey);
        int lineNo = beginKey.getLineNo();

        StringBuilder sb = new StringBuilder();
        sb.append(index);
        sb.append(". Time [");
        sb.append(timeText);
        sb.append("] Line No [");
        sb.append(lineNo);
        sb.append("] Members[");
        sb.append(memberCount);
        sb.append("] [");

        switch (hzMembershipEvent) {
        case MEMBER_ADDED:
            sb.append("Member Added");
            break;
        case MEMBER_LEFT:
            sb.append("Member Left");
            break;
        default:
            break;
        }

        sb.append("]");

        return sb.toString();
    }

    @Override
    public int compareTo(HazelcastMembership other) {

        return getIndex().compareTo(other.getIndex());
    }

    public void addHzMemberInfo(HazelcastMemberInfo hazelcastMemberInfo) {
        hazelcastMemberInfoList.add(hazelcastMemberInfo);
    }

    public void postProcess() {

        if (hazelcastMemberInfoList != null) {

            Collections.sort(hazelcastMemberInfoList);

            Map<String, List<HazelcastMemberInfo>> duplicateMap;
            duplicateMap = new HashMap<>();

            AtomicInteger index = new AtomicInteger(0);

            for (HazelcastMemberInfo hazelcastMemberInfo : hazelcastMemberInfoList) {

                hazelcastMemberInfo.setIndex(index.incrementAndGet());

                // check for duplicate "SERVER@localhost"

                String name = hazelcastMemberInfo.getName().toUpperCase();

                if (name.startsWith("SERVER@LOCALHOST")) {

                    List<HazelcastMemberInfo> hzMemberInfoList;
                    hzMemberInfoList = duplicateMap.get(name);

                    if (hzMemberInfoList == null) {

                        hzMemberInfoList = new ArrayList<>();

                        duplicateMap.put(name, hzMemberInfoList);
                    }

                    hzMemberInfoList.add(hazelcastMemberInfo);
                }
            }

            for (Entry<String, List<HazelcastMemberInfo>> entry : duplicateMap.entrySet()) {

                List<HazelcastMemberInfo> hzMemberInfoList = entry.getValue();

                if (hzMemberInfoList.size() > 1) {

                    for (HazelcastMemberInfo hazelcastMemberInfo : hzMemberInfoList) {
                        hazelcastMemberInfo.setErrorMarker(true);
                    }

                    error = true;
                }

            }
        }
    }
}
