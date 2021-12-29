
package com.pega.gcs.logviewer.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.logviewer.model.HazelcastMemberInfo;

public class LogHzMemberInfoParser {

    private static LogHzMemberInfoParser _INSTANCE;

    private Pattern hzMemberInfoPattern;

    public static LogHzMemberInfoParser getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new LogHzMemberInfoParser();
        }

        return _INSTANCE;

    }

    private LogHzMemberInfoParser() {

        String regex = "Member: \\[name=(.*), address=(.*)/(.*), uuid=(.*), mode=(.*)\\]";

        hzMemberInfoPattern = Pattern.compile(regex);

    }

    public HazelcastMemberInfo getHazelcastMemberInfo(String hzMemberInfoMessage) {

        HazelcastMemberInfo hazelcastMemberInfo = null;

        Matcher matcher = hzMemberInfoPattern.matcher(hzMemberInfoMessage);
        boolean matches = matcher.find();

        if (matches) {

            String name = matcher.group(1);
            String hostname = matcher.group(2);
            String clusterAddress = matcher.group(3);
            String uuid = matcher.group(4);
            String operatingMode = matcher.group(5);

            hazelcastMemberInfo = new HazelcastMemberInfo(name, hostname, clusterAddress, uuid, operatingMode);

        }

        return hazelcastMemberInfo;
    }
}
