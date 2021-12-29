/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.pegatdp;

import java.awt.Component;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Map;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.PluginClassloader;

public class FindingAdapterFactory {

    private static final Log4j2Helper LOG = new Log4j2Helper(FindingAdapterFactory.class);

    private static final FindingAdapterFactory _INSTANCE = new FindingAdapterFactory();

    private static boolean initialised;

    private static Class<?> graphFindingClass;

    private static Method swingUtilsGetComponentGraphMethod;

    private FindingAdapterFactory() {

        try {
            initialised = false;

            PluginClassloader pluginClassloader = PluginClassloader.getInstance();

            URLClassLoader urlClassLoader = pluginClassloader.getUrlClassLoader();

            graphFindingClass = Class.forName("com.pega.gcs.logs.threaddumpparser.analysis.graph.GraphFinding", true,
                    urlClassLoader);

            Class<?> swingUtilsClass = Class.forName("com.pega.gcs.logs.threaddumpparser.utils.SwingUtils", true,
                    urlClassLoader);

            swingUtilsGetComponentGraphMethod = swingUtilsClass.getMethod("getComponentGraph", String.class);

            initialised = true;

        } catch (Exception e) {
            LOG.error("Error initialising FindingAdapterFactory", e);
        }
    }

    public static FindingAdapterFactory getInstance() {

        return _INSTANCE;
    }

    private static boolean isInitialised() {
        return initialised;
    }

    public FindingAdapter getFindingAdapter(Object finding) {

        FindingAdapter findingAdapter = null;

        if (isInitialised()) {
            try {

                if (finding.getClass().isAssignableFrom(graphFindingClass)) {
                    findingAdapter = getGraphFindingAdapter(finding);
                } else {

                    findingAdapter = new FindingAdapter() {

                        @Override
                        public Integer getId() {

                            Integer id = null;

                            try {
                                Method method = graphFindingClass.getMethod("getId", (Class<?>[]) null);
                                method.setAccessible(true);
                                id = (Integer) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getId.", e);
                            }

                            return id;
                        }

                        @Override
                        public String getName() {

                            String name = null;

                            try {
                                Method method = graphFindingClass.getMethod("getName", (Class<?>[]) null);
                                method.setAccessible(true);
                                name = (String) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getName.", e);
                            }

                            return name;
                        }

                        @Override
                        public String getCategory() {

                            String category = null;

                            try {
                                Method method = graphFindingClass.getMethod("getCategory", (Class<?>[]) null);
                                method.setAccessible(true);
                                category = (String) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getCategory.", e);
                            }

                            return category;
                        }

                        @Override
                        public String[] getSymptoms() {

                            String[] symptoms = null;

                            try {
                                Method method = graphFindingClass.getMethod("getSymptoms", (Class<?>[]) null);
                                method.setAccessible(true);
                                symptoms = (String[]) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getSymptoms.", e);
                            }

                            return symptoms;
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public Map<String, String> getApplyTo() {

                            Map<String, String> applyTo = null;

                            try {
                                Method method = graphFindingClass.getMethod("getApplyTo", (Class<?>[]) null);
                                method.setAccessible(true);
                                applyTo = (Map<String, String>) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getApplyTo.", e);
                            }

                            return applyTo;
                        }

                        @Override
                        public String getDescription() {

                            String description = null;

                            try {
                                Method method = graphFindingClass.getMethod("getDescription", (Class<?>[]) null);
                                method.setAccessible(true);
                                description = (String) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getDescription.", e);
                            }

                            return description;
                        }

                        @Override
                        public Enum<?> getSeverity() {

                            Enum<?> severity = null;

                            try {
                                Method method = graphFindingClass.getMethod("getSeverity", (Class<?>[]) null);
                                method.setAccessible(true);
                                severity = (Enum<?>) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getSeverity.", e);
                            }

                            return severity;

                        }

                        @Override
                        public String getAdvice() {

                            String advice = null;

                            try {
                                Method method = graphFindingClass.getMethod("getAdvice", (Class<?>[]) null);
                                method.setAccessible(true);
                                advice = (String) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getAdvice.", e);
                            }

                            return advice;
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public Map<String, Object> getDetails() {

                            Map<String, Object> details = null;

                            try {
                                Method method = graphFindingClass.getMethod("getDetails", (Class<?>[]) null);
                                method.setAccessible(true);
                                details = (Map<String, Object>) method.invoke(finding, (Object[]) null);
                            } catch (Exception e) {
                                LOG.error("Error invoking getDetails.", e);
                            }

                            return details;
                        }
                    };
                }

            } catch (Exception e) {
                LOG.error("Error getting FindingAdapter", e);
            }
        }

        return findingAdapter;

    }

    private GraphFindingAdapter getGraphFindingAdapter(Object graphFinding) {

        GraphFindingAdapter graphFindingAdapter = null;

        if (isInitialised()) {
            try {

                graphFindingAdapter = new GraphFindingAdapter() {

                    @Override
                    public Integer getId() {

                        Integer id = null;

                        try {
                            Method method = graphFindingClass.getMethod("getId", (Class<?>[]) null);
                            method.setAccessible(true);
                            id = (Integer) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getId.", e);
                        }

                        return id;
                    }

                    @Override
                    public String getName() {

                        String name = null;

                        try {
                            Method method = graphFindingClass.getMethod("getName", (Class<?>[]) null);
                            method.setAccessible(true);
                            name = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getName.", e);
                        }

                        return name;
                    }

                    @Override
                    public String getCategory() {
                        String category = null;

                        try {
                            Method method = graphFindingClass.getMethod("getCategory", (Class<?>[]) null);
                            method.setAccessible(true);
                            category = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getCategory.", e);
                        }

                        return category;
                    }

                    @Override
                    public String[] getSymptoms() {

                        String[] symptoms = null;

                        try {
                            Method method = graphFindingClass.getMethod("getSymptoms", (Class<?>[]) null);
                            method.setAccessible(true);
                            symptoms = (String[]) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getSymptoms.", e);
                        }

                        return symptoms;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, String> getApplyTo() {

                        Map<String, String> applyTo = null;

                        try {
                            Method method = graphFindingClass.getMethod("getApplyTo", (Class<?>[]) null);
                            method.setAccessible(true);
                            applyTo = (Map<String, String>) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getApplyTo.", e);
                        }

                        return applyTo;
                    }

                    @Override
                    public String getDescription() {

                        String description = null;

                        try {
                            Method method = graphFindingClass.getMethod("getDescription", (Class<?>[]) null);
                            method.setAccessible(true);
                            description = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getDescription.", e);
                        }

                        return description;
                    }

                    @Override
                    public Enum<?> getSeverity() {

                        Enum<?> severity = null;

                        try {
                            Method method = graphFindingClass.getMethod("getSeverity", (Class<?>[]) null);
                            method.setAccessible(true);
                            severity = (Enum<?>) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getSeverity.", e);
                        }

                        return severity;
                    }

                    @Override
                    public String getAdvice() {

                        String advice = null;

                        try {
                            Method method = graphFindingClass.getMethod("getAdvice", (Class<?>[]) null);
                            method.setAccessible(true);
                            advice = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getAdvice.", e);
                        }

                        return advice;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public Map<String, Object> getDetails() {

                        Map<String, Object> details = null;

                        try {
                            Method method = graphFindingClass.getMethod("getDetails", (Class<?>[]) null);
                            method.setAccessible(true);
                            details = (Map<String, Object>) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getDetails.", e);
                        }

                        return details;
                    }

                    @Override
                    public Boolean isCyclic() {

                        Boolean cyclic = null;

                        try {
                            Method method = graphFindingClass.getMethod("isCyclic", (Class<?>[]) null);
                            method.setAccessible(true);
                            cyclic = (boolean) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking isCyclic.", e);
                        }

                        return cyclic;
                    }

                    @Override
                    public String getCyclicPath() {

                        String cyclicPath = null;

                        try {
                            Method method = graphFindingClass.getMethod("getCyclicPath", (Class<?>[]) null);
                            method.setAccessible(true);
                            cyclicPath = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getCyclicPath.", e);
                        }

                        return cyclicPath;
                    }

                    @Override
                    public Integer getThreadsCount() {

                        Integer threadsCount = null;

                        try {
                            Method method = graphFindingClass.getMethod("getThreadsCount", (Class<?>[]) null);
                            method.setAccessible(true);
                            threadsCount = (int) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getThreadsCount.", e);
                        }

                        return threadsCount;
                    }

                    @Override
                    public String getWaitForGraph() {

                        String waitForGraph = null;

                        try {
                            Method method = graphFindingClass.getMethod("getWaitForGraph", (Class<?>[]) null);
                            method.setAccessible(true);
                            waitForGraph = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getWaitForGraph.", e);
                        }

                        return waitForGraph;
                    }

                    @Override
                    public String getResourceAllocationGraph() {

                        String resourceAllocationGraph = null;

                        try {
                            Method method = graphFindingClass.getMethod("getResourceAllocationGraph",
                                    (Class<?>[]) null);
                            method.setAccessible(true);
                            resourceAllocationGraph = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getResourceAllocationGraph.", e);
                        }

                        return resourceAllocationGraph;
                    }

                    @Override
                    public String getRootName() {

                        String rootName = null;

                        try {
                            Method method = graphFindingClass.getMethod("getRootName", (Class<?>[]) null);
                            method.setAccessible(true);
                            rootName = (String) method.invoke(graphFinding, (Object[]) null);
                        } catch (Exception e) {
                            LOG.error("Error invoking getRootName.", e);
                        }

                        return rootName;
                    }

                    @Override
                    public Component getWaitForGraphComponent() {

                        Component waitForGraphComponent = null;

                        try {
                            String graphInDOTFormat = getWaitForGraph();

                            if (graphInDOTFormat != null) {
                                waitForGraphComponent = (Component) swingUtilsGetComponentGraphMethod.invoke(null,
                                        graphInDOTFormat);
                            }
                        } catch (Exception e) {
                            LOG.error("Error invoking swingUtilsGetComponentGraphMethod.", e);
                        }

                        return waitForGraphComponent;
                    }

                    @Override
                    public Component getResourceAllocationGraphComponent() {

                        Component resourceAllocationGraphComponent = null;

                        try {
                            String graphInDOTFormat = getResourceAllocationGraph();

                            if (graphInDOTFormat != null) {
                                resourceAllocationGraphComponent = (Component) swingUtilsGetComponentGraphMethod
                                        .invoke(null, graphInDOTFormat);
                            }
                        } catch (Exception e) {
                            LOG.error("Error invoking swingUtilsGetComponentGraphMethod.", e);
                        }

                        return resourceAllocationGraphComponent;
                    }
                };
            } catch (Exception e) {
                LOG.error("Error getting GraphFindingAdapter", e);
            }
        }

        return graphFindingAdapter;

    }

}
