/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;

import com.pega.gcs.logviewer.logfile.LogPattern;

public class LogViewerSetting implements Serializable {

    private static final long serialVersionUID = -1881754960326903255L;

    // force reset, in case any default value changes
    private static final int SETTING_VERSION = 3;

    // for kryo obj persistence
    private final int objVersion;

    private int recentItemsCount;

    private String charset;

    private boolean tailLogFile;

    private boolean reloadPreviousFiles;

    private Locale locale;

    private Set<LogPattern> pegaRuleslog4jPatternSet;

    private Set<LogPattern> pegaClusterlog4jPatternSet;

    public LogViewerSetting() {

        super();

        this.objVersion = SETTING_VERSION;

        setDefault();
    }

    public static int getSettingVersion() {
        return SETTING_VERSION;
    }

    public int getObjVersion() {
        return objVersion;
    }

    public void setDefault() {
        recentItemsCount = 20;
        charset = "UTF-8";
        tailLogFile = false;
        reloadPreviousFiles = false;
        locale = Locale.getDefault();
        pegaRuleslog4jPatternSet = LogPattern.getDefaultPegaRulesLog4jPatternSet();
        pegaClusterlog4jPatternSet = LogPattern.getDefaultPegaClusterLog4jPatternSet();
    }

    public int getRecentItemsCount() {
        return recentItemsCount;
    }

    public void setRecentItemsCount(int recentItemsCount) {
        this.recentItemsCount = recentItemsCount;
    }

    public String getCharset() {

        if (charset == null) {
            charset = Charset.defaultCharset().name();
        }

        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isTailLogFile() {
        return tailLogFile;
    }

    public void setTailLogFile(boolean tailLogFile) {
        this.tailLogFile = tailLogFile;
    }

    public boolean isReloadPreviousFiles() {
        return reloadPreviousFiles;
    }

    public void setReloadPreviousFiles(boolean reloadPreviousFiles) {
        this.reloadPreviousFiles = reloadPreviousFiles;
    }

    public Locale getLocale() {

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Set<LogPattern> getPegaRuleslog4jPatternSet() {

        if (pegaRuleslog4jPatternSet == null) {
            pegaRuleslog4jPatternSet = LogPattern.getDefaultPegaRulesLog4jPatternSet();
        }

        return pegaRuleslog4jPatternSet;
    }

    public Set<LogPattern> getPegaClusterlog4jPatternSet() {

        if (pegaClusterlog4jPatternSet == null) {
            pegaClusterlog4jPatternSet = LogPattern.getDefaultPegaClusterLog4jPatternSet();
        }

        return pegaClusterlog4jPatternSet;
    }

}
