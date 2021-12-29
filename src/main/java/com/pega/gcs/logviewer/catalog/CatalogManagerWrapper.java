
package com.pega.gcs.logviewer.catalog;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.PluginClassloader;
import com.pega.gcs.logviewer.PluginWrapper;

public class CatalogManagerWrapper extends PluginWrapper {

    private static final Log4j2Helper LOG = new Log4j2Helper(CatalogManagerWrapper.class);

    private static CatalogManagerWrapper _INSTANCE;

    private Class<?> catalogManagerClass;

    private Object catalogManager;

    private CatalogManagerWrapper() {

        super("com.pega.gcs.logviewer.catalog.CatalogViewerFrame");

        this.catalogManager = null;

        try {

            PluginClassloader pluginClassloader = PluginClassloader.getInstance();

            URLClassLoader urlClassLoader = pluginClassloader.getUrlClassLoader();

            catalogManagerClass = Class.forName("com.pega.gcs.logviewer.hotfix.CatalogManager", true, urlClassLoader);

            Method getInstanceMethod = catalogManagerClass.getDeclaredMethod("getInstance");

            catalogManager = getInstanceMethod.invoke(null);

        } catch (Exception e) {
            LOG.error("Error initialising CatalogManagerWrapper", e);
        }
    }

    public static CatalogManagerWrapper getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new CatalogManagerWrapper();
        }

        return _INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getCatalogProductNameSet(String hotfixProductName) {

        Set<String> catalogProductNameSet = null;

        if (isInitialised()) {

            try {

                Method getCatalogProductNameListMethod = catalogManagerClass
                        .getDeclaredMethod("getCatalogProductNameSet", String.class);

                catalogProductNameSet = (Set<String>) getCatalogProductNameListMethod.invoke(catalogManager,
                        hotfixProductName);

            } catch (Exception e) {
                LOG.error("Error getting getCatalogProductNameSet", e);
            }
        }

        return catalogProductNameSet;
    }

    public boolean isCatalogFileAvailable() {

        boolean isCatalogFileAvailable = false;

        if (isInitialised()) {

            try {

                Method isCatalogFileAvailableMethod = catalogManagerClass.getDeclaredMethod("isCatalogFileAvailable");

                isCatalogFileAvailable = (Boolean) isCatalogFileAvailableMethod.invoke(catalogManager);

            } catch (Exception e) {
                LOG.error("Error getting Catalog file", e);
            }
        }

        return isCatalogFileAvailable;
    }

    public Date getLastUpdateDate() {

        Date lastUpdateDate = null;

        if (isInitialised()) {

            try {

                Method getLastUpdateDateMethod = catalogManagerClass.getDeclaredMethod("getLastUpdateDate");

                lastUpdateDate = (Date) getLastUpdateDateMethod.invoke(catalogManager);

            } catch (Exception e) {
                LOG.error("Error getting lastUpdateDate", e);
            }
        }

        return lastUpdateDate;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getProductReleaseMap() {

        Map<String, List<String>> productReleaseMap = null;

        if (isInitialised()) {

            try {

                Method getProductReleaseMapMethod = catalogManagerClass.getDeclaredMethod("getProductReleaseMap");

                productReleaseMap = (Map<String, List<String>>) getProductReleaseMapMethod.invoke(catalogManager);

            } catch (Exception e) {
                LOG.error("Error getting productReleaseMap", e);
            }
        }

        return productReleaseMap;
    }

    /**
     * Get hotfix enty list for a product and release combination.
     * 
     * @param productName - Product Name
     * @param releaseName - Release name for the Product
     * @return - Map of hotfixId, list of hotfix entries.
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<List<String>>> getHotfixEntryDataListMapForProductRelease(String productName,
            String releaseName) {

        Map<String, List<List<String>>> hotfixEntryDataListMapForProductRelease = null;

        if (isInitialised()) {

            try {

                Method getHotfixEntryDataListMapForProductReleaseMethod = catalogManagerClass
                        .getDeclaredMethod("getHotfixEntryDataListMapForProductRelease", String.class, String.class);

                hotfixEntryDataListMapForProductRelease = (Map<String, List<List<String>>>) getHotfixEntryDataListMapForProductReleaseMethod
                        .invoke(catalogManager, productName, releaseName);

            } catch (Exception e) {
                LOG.error("Error getting hotfixEntryDataListMapForProductRelease productName:" + productName
                        + " releaseName:" + releaseName, e);
            }
        }

        return hotfixEntryDataListMapForProductRelease;
    }

}
