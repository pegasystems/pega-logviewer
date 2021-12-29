/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pega.gcs.logviewer.model.Log4jLogEntryModel;
import com.pega.gcs.logviewer.model.LogEntry;
import com.pega.gcs.logviewer.model.LogEntryColumn;
import com.pega.gcs.logviewer.model.LogEntryKey;
import com.pega.gcs.logviewer.model.SystemStart;

public class LogSystemStartParser {

    private static LogSystemStartParser logSystemStartParser;

    private Pattern systemStartDatePattern;
    // private Pattern systemBuildVersionPattern;
    // private Pattern buildNamePattern;
    private Pattern systemSourceURLPattern;
    private Pattern jvmVendorPattern;
    private Pattern jvmNamePattern;
    private Pattern osNamePattern;
    private Pattern osArchitecturePattern;
    // private Pattern webTierOnlyDeploymentPattern;
    private Pattern servletContainerPattern;
    private Pattern webServerNamePattern;
    private Pattern webServerEnvironmentPattern;
    private Pattern webTierDefaultEngineBeanNamePattern;
    private Pattern enterpriseServerEnvironmentPattern;
    private Pattern enterpriseTierDefaultEngineBeanNamePattern;
    private Pattern webToEnterpriseTierDelegateTypePattern;
    private Pattern databaseInformationPattern;
    private Pattern databaseNamePattern;
    private Pattern definedByPattern;
    private Pattern dataSourcePattern;
    private Pattern jdbcURLPattern;
    private Pattern minimumConnectionCountPattern;
    private Pattern maximumConnectionCountPattern;
    private Pattern failoverDatabasePattern;
    private Pattern threadDumpJava15Pattern;
    private Pattern threadDumpIBMPattern;
    private Pattern threadDumpDisabledPattern;
    private Pattern deflateStreamsPattern;
    private Pattern pathGeneratedSourcePattern;
    private Pattern pathGeneratedClassesPattern;
    private Pattern pathWebTierRealRootPattern;
    private Pattern pathWebTierTemporaryPattern;
    private Pattern pathPegaRulesRealRootPattern;
    private Pattern pathPegaRulesEffectiveRootPattern;
    private Pattern pathPegaRulesEffectiveTempPattern;
    private Pattern pathGeneratedClassDatabasePattern;
    private Pattern pathGeneratedClassFilesPattern;
    private Pattern pathExternalClassesPattern;
    private Pattern pathExternalJarsPattern;
    private Pattern pathServletClassesPattern;
    private Pattern pathJSPClassesPattern;
    private Pattern pathEJBClassesPattern;
    private Pattern pathJMSClassesPattern;
    // private Pattern staticProbationStrategyPattern;
    private Pattern alertBytesInputPerInteractionErrorPattern;
    private Pattern alertBytesInputPerInteractionWarningPattern;
    private Pattern alertBytesInputPerInteractionDisabledPattern;
    private Pattern databaseProductPattern;
    private Pattern databaseVersionPattern;
    private Pattern databaseDriverPattern;
    private Pattern databaseDriverVersionPattern;
    private Pattern systemMultiTenantModePattern;
    private Pattern systemNodeIdentificationPattern;
    private Pattern systemLibraryVersionDatabaseStoragePattern;
    private Pattern systemExtractMarkerNotFoundPattern;
    private Pattern systemExtractMarkerFoundPattern;

    private LogSystemStartParser() {
        super();

        systemStartDatePattern = Pattern.compile("System Start Date:[ ](.*)");
        // systemBuildVersionPattern =
        // Pattern.compile("PegaRULES Web Tier[ ](.*)");
        // buildNamePattern = Pattern.compile("");
        systemSourceURLPattern = Pattern.compile("http://(.*)");
        jvmVendorPattern = Pattern.compile("Vendor:[ ](.*)");
        jvmNamePattern = Pattern.compile("VM Name:[ ](.*)");
        osNamePattern = Pattern.compile("OS:[ ](.*)");
        osArchitecturePattern = Pattern.compile("Architecture:[ ](.*)");
        // webTierOnlyDeploymentPattern = Pattern.compile("");
        servletContainerPattern = Pattern.compile("Servlet container:[ ](.*)");
        webServerNamePattern = Pattern.compile("Web server:[ ](.*)");
        webServerEnvironmentPattern = Pattern.compile("Web server environment:[ ](.*)");
        webTierDefaultEngineBeanNamePattern = Pattern.compile("Web\\-tier default Engine bean name:[ ](.*)");
        enterpriseServerEnvironmentPattern = Pattern.compile("Enterprise server environment:[ ](.*)");
        enterpriseTierDefaultEngineBeanNamePattern = Pattern
                .compile("Enterprise\\-tier default Engine bean name:[ ](.*)");
        webToEnterpriseTierDelegateTypePattern = Pattern.compile("Web to Enterprise tier delegate type:[ ](.*)");
        databaseInformationPattern = Pattern.compile("Base database information:");
        databaseNamePattern = Pattern.compile("Database name:[\t](.*)");
        definedByPattern = Pattern.compile("Defined by:[\t](.*)");
        dataSourcePattern = Pattern.compile("Data source name:[\t](.*)");
        jdbcURLPattern = Pattern.compile("JDBC URL:[\t](.*)");
        minimumConnectionCountPattern = Pattern.compile("Minimum connection count:[\t](.*)");
        maximumConnectionCountPattern = Pattern.compile("Maximum connection count:[\t](.*)");
        failoverDatabasePattern = Pattern.compile("Failover database:[\t](.*)");
        threadDumpJava15Pattern = Pattern.compile("Using Java 1\\.5 Thread\\.getAllStackTraces\\(\\) for thread dumps");
        threadDumpIBMPattern = Pattern.compile("Using IBM JavaDump for thread dumps");
        threadDumpDisabledPattern = Pattern.compile("Thread dump support is disabled\\.");
        deflateStreamsPattern = Pattern.compile("DeflateStreams is turned[ ](.*)\\.");
        pathGeneratedSourcePattern = Pattern.compile("Generated source[ ]:[ ](.*)");
        pathGeneratedClassesPattern = Pattern.compile("Generated classes:[ ](.*)");
        pathWebTierRealRootPattern = Pattern.compile("Web\\-tier real root path:[ ](.*)");
        pathWebTierTemporaryPattern = Pattern.compile("Web\\-tier temporary path:[ ](.*)");
        pathPegaRulesRealRootPattern = Pattern.compile("PegaRULES real root path:[ ](.*)");
        pathPegaRulesEffectiveRootPattern = Pattern.compile("PegaRULES effective root path:[ ](.*)");
        pathPegaRulesEffectiveTempPattern = Pattern.compile("PegaRULES effective temp path:[ ](.*)");
        pathGeneratedClassDatabasePattern = Pattern.compile("Using  genClassPath \\(database\\):[ ](.*)");
        pathGeneratedClassFilesPattern = Pattern.compile("Using  genClassPath \\(files\\):[ ](.*)");
        pathExternalClassesPattern = Pattern.compile("Using external classes in:[ ](.*)");
        pathExternalJarsPattern = Pattern.compile("Using external jars in:[ ](.*)");
        pathServletClassesPattern = Pattern.compile("Servlet classes: ");
        pathJSPClassesPattern = Pattern.compile("JSP classes:[ ](.*)");
        pathEJBClassesPattern = Pattern.compile("EJB classes:[ ](.*)");
        pathJMSClassesPattern = Pattern.compile("JMS classes:[ ](.*)");
        alertBytesInputPerInteractionErrorPattern = Pattern
                .compile("Bytes input per interaction error threshold:[\t](.*)");
        alertBytesInputPerInteractionWarningPattern = Pattern
                .compile("Bytes input per interaction warning threshold:[\t](.*)");
        alertBytesInputPerInteractionDisabledPattern = Pattern.compile("Bytes input per interaction alert disabled");
        databaseProductPattern = Pattern.compile("Database product: ");
        databaseVersionPattern = Pattern.compile("Database version: ");
        databaseDriverPattern = Pattern.compile("Driver: ");
        databaseDriverVersionPattern = Pattern.compile("Driver version: ");
        systemMultiTenantModePattern = Pattern.compile("Multi\\-Tenant Mode Enabled");
        systemNodeIdentificationPattern = Pattern.compile("Node Identification:[ ](.*)");
        systemLibraryVersionDatabaseStoragePattern = Pattern
                .compile("Library version used for database storage:[ ](.*)");
        systemExtractMarkerNotFoundPattern = Pattern
                .compile("PegaRULES extract marker file not found; resetting local caches");
        systemExtractMarkerFoundPattern = Pattern
                .compile("PegaRULES extract marker file found; re\\-using local caches\\.");
    }

    public static LogSystemStartParser getInstance() {

        if (logSystemStartParser == null) {
            logSystemStartParser = new LogSystemStartParser();
        }

        return logSystemStartParser;
    }

    public Map<String, String> getSystemStartMap(SystemStart systemStart, Log4jLogEntryModel log4jLogEntryModel) {

        Map<String, String> systemStartMap = new TreeMap<String, String>();

        MatchObject matchObject = null;
        int ssIndex = 0;

        List<String> leColumnList = log4jLogEntryModel.getLogEntryColumnList();

        int messageColIndex = leColumnList.indexOf(LogEntryColumn.MESSAGE.getColumnId());

        Map<LogEntryKey, LogEntry> logEntryMap = log4jLogEntryModel.getLogEntryMap();

        List<LogEntry> systemStartLogEntryList = new ArrayList<LogEntry>();

        for (LogEntryKey logEntryKey : systemStart.getLogEntryKeyList()) {
            LogEntry logEntry = logEntryMap.get(logEntryKey);

            if (logEntry != null) {

                systemStartLogEntryList.add(logEntry);
            }
        }

        String systemStartDateKey = "System Start Date";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, systemStartDatePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(systemStartDateKey, matchObject.getValue());
        }

        String systemWebTierVersionKey = "System Web Tier Version";
        String systemWebTierVersionValue = getValue(systemStartLogEntryList, messageColIndex, ssIndex);
        if (systemWebTierVersionValue != null) {
            ssIndex++;
            systemStartMap.put(systemWebTierVersionKey, systemWebTierVersionValue);
        }

        String systemWebTierBuildNameKey = "System Web Tier Build Name";
        String buildWebTierNameValue = getValue(systemStartLogEntryList, messageColIndex, ssIndex);
        if (buildWebTierNameValue != null) {
            ssIndex++;
            systemStartMap.put(systemWebTierBuildNameKey, buildWebTierNameValue);
        }

        String systemSourceURLKey = "System Source URL";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, systemSourceURLPattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(systemSourceURLKey, matchObject.getValue());
        }

        String jvmVendorKey = "JVM Vendor";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, jvmVendorPattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(jvmVendorKey, matchObject.getValue());
        }

        String jvmNameKey = "JVM Name";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, jvmNamePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(jvmNameKey, matchObject.getValue());
        }

        String osNameKey = "OS Name";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, osNamePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(osNameKey, matchObject.getValue());
        }

        String osArchitectureKey = "OS Architecture";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, osArchitecturePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(osArchitectureKey, matchObject.getValue());
        }

        String systemEnterpriseTierVersionKey = "System Enterprise Tier Version";
        String systemEnterpriseTierVersionValue = getValue(systemStartLogEntryList, messageColIndex, ssIndex);
        if (systemEnterpriseTierVersionValue != null) {
            ssIndex++;
            systemStartMap.put(systemEnterpriseTierVersionKey, systemEnterpriseTierVersionValue);
        }

        String systemEnterpriseTierBuildNameKey = "System Enterprise Tier Build Name";
        String buildEnterpriseTierNameValue = getValue(systemStartLogEntryList, messageColIndex, ssIndex);
        if (buildEnterpriseTierNameValue != null) {
            ssIndex++;
            systemStartMap.put(systemEnterpriseTierBuildNameKey, buildEnterpriseTierNameValue);
        }

        String webTierOnlyDeploymentKey = "System Deployment";
        String webTierOnlyDeploymentValue = getValue(systemStartLogEntryList, messageColIndex, ssIndex);
        if (webTierOnlyDeploymentValue != null) {
            ssIndex++;
            systemStartMap.put(webTierOnlyDeploymentKey, webTierOnlyDeploymentValue);
        }

        String servletContainerKey = "Servlet Container";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, servletContainerPattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(servletContainerKey, matchObject.getValue());
        }

        String webServerNameKey = "Web Server Name";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, webServerNamePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(webServerNameKey, matchObject.getValue());
        }

        String webServerEnvironmentKey = "Web Server Environment";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, webServerEnvironmentPattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(webServerEnvironmentKey, matchObject.getValue());
        }

        String webTierDefaultEngineBeanNameKey = "Web Tier Default Engine Bean Name";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                webTierDefaultEngineBeanNamePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(webTierDefaultEngineBeanNameKey, matchObject.getValue());
        }

        if ((webTierOnlyDeploymentValue != null) && !("Deployed in web tier only".equals(webTierOnlyDeploymentValue))) {

            String enterpriseServerEnvironmentKey = "Enterprise Server Environment";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                    enterpriseServerEnvironmentPattern);
            if (matchObject != null) {
                ssIndex = matchObject.getIndex();
                ssIndex++;
                systemStartMap.put(enterpriseServerEnvironmentKey, matchObject.getValue());
            }

            String enterpriseTierDefaultEngineBeanNameKey = "Enterprise Tier Default Engine Bean Name";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                    enterpriseTierDefaultEngineBeanNamePattern);
            if (matchObject != null) {
                ssIndex = matchObject.getIndex();
                ssIndex++;
                systemStartMap.put(enterpriseTierDefaultEngineBeanNameKey, matchObject.getValue());
            }
        }
        String webToEnterpriseTierDelegateTypeKey = "Web To Enterprise Tier Delegate Type";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                webToEnterpriseTierDelegateTypePattern);
        if (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;
            systemStartMap.put(webToEnterpriseTierDelegateTypeKey, matchObject.getValue());
        }

        int databaseCounter = 1;

        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseInformationPattern);

        while (matchObject != null) {
            ssIndex = matchObject.getIndex();
            ssIndex++;

            String preStr = "Base Database-" + databaseCounter + " ";

            String databaseNameKey = preStr + "Name";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseNamePattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(databaseNameKey, matchObject.getValue());
            }

            String definedByKey = preStr + "Defined By";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, definedByPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(definedByKey, matchObject.getValue());
            }

            String dataSourceKey = preStr + "Data Source";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, dataSourcePattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(dataSourceKey, matchObject.getValue());
            }

            String jdbcURLNameKey = preStr + "JDBC URL";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, jdbcURLPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(jdbcURLNameKey, matchObject.getValue());
            }

            String minimumConnectionCountKey = preStr + "Minimum Connection Count";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                    minimumConnectionCountPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(minimumConnectionCountKey, matchObject.getValue());
            }

            String maximumConnectionCountKey = preStr + "Maximum Connection Count";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                    maximumConnectionCountPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(maximumConnectionCountKey, matchObject.getValue());
            }

            String failoverDatabaseKey = preStr + "Failover Database";
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, failoverDatabasePattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(failoverDatabaseKey, matchObject.getValue());
            }

            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseInformationPattern);
            databaseCounter++;
        }

        String threadDumpMechanismKey = "Thread Dump Mechanism";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, threadDumpJava15Pattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(threadDumpMechanismKey, matchObject.getValue());
        } else {
            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, threadDumpIBMPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(threadDumpMechanismKey, matchObject.getValue());
            } else {
                matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                        threadDumpDisabledPattern);
                if (matchObject != null) {
                    ssIndex++;
                    systemStartMap.put(threadDumpMechanismKey, matchObject.getValue());
                } else {
                    String text = "Thread dump support is unavailable because no suitable mechanism could be found.";
                    ssIndex++;
                    systemStartMap.put(threadDumpMechanismKey, text);
                }
            }
        }

        String deflateStreamsKey = "Deflate Streams";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, deflateStreamsPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(deflateStreamsKey, matchObject.getValue());
        }

        String pathGeneratedSourceKey = "Path - Generated Source";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathGeneratedSourcePattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathGeneratedSourceKey, matchObject.getValue());
        }

        String pathGeneratedClassesKey = "Path - Generated Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathGeneratedClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathGeneratedClassesKey, matchObject.getValue());
        }

        String pathWebTierRealRootKey = "Path - Web Tier Real Root";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathWebTierRealRootPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathWebTierRealRootKey, matchObject.getValue());
        }

        String pathWebTierTemporaryKey = "Path - Web Tier Temporary";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathWebTierTemporaryPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathWebTierTemporaryKey, matchObject.getValue());
        }

        String pathPegaRulesRealRootKey = "Path - PegaRULES Real Root";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathPegaRulesRealRootPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathPegaRulesRealRootKey, matchObject.getValue());
        }

        String pathPegaRulesEffectiveRootKey = "Path - PegaRULES Effective Root";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                pathPegaRulesEffectiveRootPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathPegaRulesEffectiveRootKey, matchObject.getValue());
        }

        String pathPegaRulesEffectiveTempKey = "Path - PegaRULES Effective Temp";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                pathPegaRulesEffectiveTempPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathPegaRulesEffectiveTempKey, matchObject.getValue());
        }

        String pathGeneratedClassDatabaseKey = "Path - Generated Class(Database)";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                pathGeneratedClassDatabasePattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathGeneratedClassDatabaseKey, matchObject.getValue());
        }

        String pathGeneratedClassFilesKey = "Path - Generated Class(Files)";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathGeneratedClassFilesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathGeneratedClassFilesKey, matchObject.getValue());
        }

        String pathExternalClassesKey = "Path - External Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathExternalClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathExternalClassesKey, matchObject.getValue());
        }

        String pathExternalJarsKey = "Path - External Jars";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathExternalJarsPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathExternalJarsKey, matchObject.getValue());
        }

        String pathServletClassesKey = "Path - Servlet Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathServletClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathServletClassesKey, matchObject.getValue());
        }

        String pathJSPClassesKey = "Path - JSP Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathJSPClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathJSPClassesKey, matchObject.getValue());
        }

        String pathEJBClassesKey = "Path - EJB Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathEJBClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathEJBClassesKey, matchObject.getValue());
        }

        String pathJMSClassesKey = "Path - JMS Classes";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, pathJMSClassesPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(pathJMSClassesKey, matchObject.getValue());
        }

        String alertBytesInputPerInteractionErrorKey = "Alert - Bytes Input Per Interaction Error Threshold";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                alertBytesInputPerInteractionErrorPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(alertBytesInputPerInteractionErrorKey, matchObject.getValue());
        }

        String alertBytesInputPerInteractionWarningKey = "Alert - Bytes Input Per Interaction Warning Threshold";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                alertBytesInputPerInteractionWarningPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(alertBytesInputPerInteractionWarningKey, matchObject.getValue());
        }

        String alertBytesInputPerInteractionDisabledKey = "Alert - Bytes Input Per Interaction Disabled";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                alertBytesInputPerInteractionDisabledPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(alertBytesInputPerInteractionDisabledKey, matchObject.getValue());
        }

        String databaseProductKey = "databaseProduct";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseProductPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(databaseProductKey, matchObject.getValue());
        }

        String databaseVersionKey = "databaseVersion";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseVersionPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(databaseVersionKey, matchObject.getValue());
        }

        String databaseDriverKey = "databaseDriver";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseDriverPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(databaseDriverKey, matchObject.getValue());
        }

        String databaseDriverVersionKey = "databaseDriverVersion";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, databaseDriverVersionPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(databaseDriverVersionKey, matchObject.getValue());
        }

        String systemMultiTenantModeKey = "System Multi-Tenant Mode";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, systemMultiTenantModePattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(systemMultiTenantModeKey, matchObject.getValue());
        }

        String systemNodeIdentificationKey = "System Node Identification";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex, systemNodeIdentificationPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(systemNodeIdentificationKey, matchObject.getValue());
        }

        String systemLibraryVersionDatabaseStorageKey = "System Library Version Used For Database Storage";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                systemLibraryVersionDatabaseStoragePattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(systemLibraryVersionDatabaseStorageKey, matchObject.getValue());
        }

        String systemExtractMarkerKey = "System Extract Marker";
        matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                systemExtractMarkerNotFoundPattern);
        if (matchObject != null) {
            ssIndex++;
            systemStartMap.put(systemExtractMarkerKey, matchObject.getValue());
        } else {

            matchObject = applyPattern(systemStartLogEntryList, messageColIndex, ssIndex,
                    systemExtractMarkerFoundPattern);
            if (matchObject != null) {
                ssIndex++;
                systemStartMap.put(systemExtractMarkerKey, matchObject.getValue());
            }
        }

        // for (Map.Entry<String, String> entrySet : systemStartMap.entrySet())
        // {
        //
        // LOG.info(entrySet.getKey() + "\t=\t"
        // + entrySet.getValue());
        // }

        return systemStartMap;
    }

    // TODO: extract getLogEntryValueList only once
    private MatchObject applyPattern(List<LogEntry> systemStartLogEntryList, int messageColIndex, int startIndex,
            Pattern pattern) {

        MatchObject matchObject = null;

        int size = systemStartLogEntryList.size();

        for (int index = startIndex; index < size; index++) {

            LogEntry logEntry = systemStartLogEntryList.get(index);

            ArrayList<String> logEntryValueList = logEntry.getLogEntryValueList();

            String messageStr = logEntryValueList.get(messageColIndex);
            messageStr = messageStr.trim();

            Matcher matcher = pattern.matcher(messageStr);

            if (matcher.matches()) {

                String matchText;

                if (matcher.groupCount() > 0) {
                    matchText = matcher.group(1);

                } else {
                    matchText = pattern.pattern();
                    matchText = matchText.replaceAll("\\\\", "");
                }

                matchObject = new MatchObject(index, matchText);
                break;
            }
        }

        return matchObject;
    }

    private String getValue(List<LogEntry> systemStartLogEntryList, int messageColIndex, int index) {

        String value = null;

        LogEntry logEntry = systemStartLogEntryList.get(index);

        ArrayList<String> logEntryValueList = logEntry.getLogEntryValueList();

        value = logEntryValueList.get(messageColIndex);

        return value.trim();
    }

    private class MatchObject {

        private int index;

        private String value;

        protected MatchObject(int index, String value) {
            super();
            this.index = index;
            this.value = value;
        }

        protected int getIndex() {
            return index;
        }

        protected String getValue() {
            return value;
        }

    }

}
