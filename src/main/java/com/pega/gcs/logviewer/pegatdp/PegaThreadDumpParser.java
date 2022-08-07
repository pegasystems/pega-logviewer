/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.pegatdp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.PluginClassloader;

public class PegaThreadDumpParser {

    private static final Log4j2Helper LOG = new Log4j2Helper(PegaThreadDumpParser.class);

    private static final PegaThreadDumpParser _INSTANCE = new PegaThreadDumpParser();

    private boolean initialised;

    private Class<?> pegaThreadDumpParserClass;

    private Class<?> threadDumpClass;

    private Class<?> htmlReportProducerFactoryProviderClass;

    private String version;

    private PegaThreadDumpParser() {

        try {

            initialised = false;

            String pwd = System.getProperty("user.dir");
            Properties props = System.getProperties();
            props.setProperty("tdp.home", pwd);

            PluginClassloader pluginClassloader = PluginClassloader.getInstance();

            URLClassLoader urlClassLoader = pluginClassloader.getUrlClassLoader();

            pegaThreadDumpParserClass = Class.forName("com.pega.gcs.logs.threaddumpparser.PegaThreadDumpParser", true,
                    urlClassLoader);
            threadDumpClass = Class.forName("com.pega.gcs.logs.threaddumpparser.ThreadDump", true, urlClassLoader);

            htmlReportProducerFactoryProviderClass = Class.forName(
                    "com.pega.gcs.logs.threaddumpparser.cli.HTMLReportProducerFactoryProvider", true, urlClassLoader);

            version = pegaThreadDumpParserClass.getPackage().getImplementationVersion();

            LOG.info("Using Thread dump Parser version: " + version);

            initialised = true;

        } catch (Exception e) {
            LOG.error("Error initialising PegaThreadDumpParser", e);
        }

    }

    public static PegaThreadDumpParser getInstance() {
        return _INSTANCE;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public String getVersion() {
        return version;
    }

    public Object getThreadDumpObject(String threadDumpText) {

        Object threadDump = null;

        if (isInitialised()) {

            try {

                Constructor<?> pegaThreadDumpParserConstructor = pegaThreadDumpParserClass
                        .getDeclaredConstructor(new Class[0]);
                pegaThreadDumpParserConstructor.setAccessible(true);

                Object pegaThreadDumpParser = pegaThreadDumpParserConstructor.newInstance();

                Method parseMethod = pegaThreadDumpParserClass.getDeclaredMethod("parse", String.class);

                threadDump = parseMethod.invoke(pegaThreadDumpParser, threadDumpText);
            } catch (InvocationTargetException ite) {
                LOG.error("InvocationTargetException Error getting ThreadDumpObject", ite.getTargetException());
            } catch (Exception e) {
                LOG.error("Error getting ThreadDumpObject", e);
            }
        }

        return threadDump;
    }

    public String getHtmlReport(Object threadDump) {
        String htmlReport = null;

        if (isInitialised()) {
            try {

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    Constructor<?> htmlReportProducerFactoryProviderConstructor = htmlReportProducerFactoryProviderClass
                            .getConstructor();
                    Object htmlReportProducerFactoryProvider = htmlReportProducerFactoryProviderConstructor
                            .newInstance();
                    Method providerMethod = htmlReportProducerFactoryProviderClass.getMethod("provide");

                    Object htmlReportProducerFactory = providerMethod.invoke(htmlReportProducerFactoryProvider);

                    Method getHtmlReportProducerMethod = htmlReportProducerFactory.getClass()
                            .getMethod("getHTMLReportProducer");

                    Object htmlReportProducer = getHtmlReportProducerMethod.invoke(htmlReportProducerFactory);

                    Method produceMethod = htmlReportProducer.getClass().getDeclaredMethod("produce", threadDumpClass,
                            OutputStream.class);
                    produceMethod.setAccessible(true);

                    // get the html report
                    produceMethod.invoke(htmlReportProducer, threadDump, baos);

                    htmlReport = baos.toString();
                }

            } catch (InvocationTargetException ite) {
                LOG.error("InvocationTargetException Error getting html report", ite.getTargetException());
            } catch (Exception e) {
                LOG.error("Error getting html report", e);
            }
        }

        return htmlReport;
    }

    public List<FindingAdapter> getFindingAdapterList(Object threadDump) {

        List<FindingAdapter> findingAdapterList = new ArrayList<>();

        if ((isInitialised()) && (threadDump != null)) {

            try {

                Method getFindingsMethod = threadDumpClass.getDeclaredMethod("getFindings");

                Collection<?> findings = (Collection<?>) getFindingsMethod.invoke(threadDump, (Object[]) null);

                for (Object finding : findings) {

                    FindingAdapter findingAdapter = FindingAdapterFactory.getInstance().getFindingAdapter(finding);
                    findingAdapterList.add(findingAdapter);

                }

            } catch (Exception e) {
                LOG.error("Error getting FindingAdapterList", e);
            }
        }

        return findingAdapterList;
    }

}
