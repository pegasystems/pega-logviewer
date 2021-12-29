/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.hotfixscan.hybrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.hotfixscan.hybrid.HybridHotfixList.HybridHotfix;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class HybridHotfixListProvider {

    private static final Log4j2Helper LOG = new Log4j2Helper(HybridHotfixListProvider.class);

    private static final String HYBRID_HOTFIX_LIST_XML = "/HybridHotfixList.xml";

    private static HybridHotfixListProvider _INSTANCE;

    private Map<Integer, HybridHotfix> hybridHotfixMap;

    private Map<String, List<String>> releaseHotfixIdMap;

    private HybridHotfixListProvider() {

        super();

        hybridHotfixMap = null;
        releaseHotfixIdMap = null;

        String pwd = System.getProperty("user.dir");
        File extAMLFile = new File(pwd, HYBRID_HOTFIX_LIST_XML);
        InputStream amlInputStream = null;

        if (extAMLFile.exists()) {
            try {
                amlInputStream = new FileInputStream(extAMLFile);
            } catch (FileNotFoundException e) {
                LOG.error("File not found: " + extAMLFile, e);
            }
        } else {
            amlInputStream = getClass().getResourceAsStream(HYBRID_HOTFIX_LIST_XML);
        }

        if (amlInputStream != null) {
            try {
                JAXBContext jaxb = JAXBContext.newInstance(HybridHotfixList.class);
                Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                InputStream hhlXsdInputStream = getClass().getResourceAsStream("/HybridHotfixList.xsd");
                Schema hhlSchema = schemaFactory.newSchema(new StreamSource(hhlXsdInputStream));
                unmarshaller.setSchema(hhlSchema);

                HybridHotfixList hybridHotfixList = (HybridHotfixList) unmarshaller.unmarshal(amlInputStream);
                LOG.info("HybridHotfixList size: " + hybridHotfixList.getHybridHotfix().size());

                hybridHotfixMap = new TreeMap<Integer, HybridHotfix>();
                releaseHotfixIdMap = new HashMap<String, List<String>>();

                for (HybridHotfix hybridHotfix : hybridHotfixList.getHybridHotfix()) {

                    Integer id = hybridHotfix.getId();
                    String hotfixId = hybridHotfix.getHotfixId();
                    String pegaVersion = hybridHotfix.getPegaVersion();

                    hybridHotfixMap.put(id, hybridHotfix);

                    List<String> hybridHotfixIdList = releaseHotfixIdMap.get(pegaVersion);

                    if (hybridHotfixIdList == null) {
                        hybridHotfixIdList = new ArrayList<>();
                        releaseHotfixIdMap.put(pegaVersion, hybridHotfixIdList);
                    }

                    hybridHotfixIdList.add(hotfixId);
                }
            } catch (Exception e) {
                LOG.error("Error loading Hybrid Hotfix List", e);
            } finally {
                if (amlInputStream != null) {
                    try {
                        amlInputStream.close();
                    } catch (IOException e) {
                        LOG.error("Error closing InputStream", e);
                    }
                }
            }
        } else {
            LOG.info("HybridHotfixList.xml could not be found");
        }
    }

    /*
     * returns unmodifiableMap use addNewHybridHotfix to append to this map
     */
    public Map<Integer, HybridHotfix> getHybridHotfixMap() {
        return Collections.unmodifiableMap(hybridHotfixMap);
    }

    public Map<String, List<String>> getReleaseHotfixIdMap() {
        return Collections.unmodifiableMap(releaseHotfixIdMap);
    }

    public static HybridHotfixListProvider getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new HybridHotfixListProvider();
        }

        return _INSTANCE;
    }

    public static void main(String[] args) {
        HybridHotfixListProvider.getInstance().getHybridHotfixMap();
    }
}
