
package com.pega.gcs.logviewer.parser;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.logfile.AlertLogPattern;

public class GcpGocAlertLogParser extends AlertLogParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(GcpGocAlertLogParser.class);

    public GcpGocAlertLogParser(AlertLogPattern alertLogPattern, Charset charset, Locale locale, ZoneId displayZoneId) {
        super(alertLogPattern, charset, locale, displayZoneId);
    }

    @Override
    protected void parseV3(String line) {
        Map<String, String> fieldMap = getJsonFieldMap(line);

        if (fieldMap != null) {

            String message = fieldMap.get("message");

            processClouldKMessage(message);

        } else {
            LOG.info("discarding line: " + line);
        }
    }
}
