/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model.measure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.alert.AlertMessageList;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class Log4jMeasureProvider {

    private static final Log4j2Helper LOG = new Log4j2Helper(Log4jMeasureProvider.class);

    private static final String LOG4J_MEASURE_PATTERN_LIST_XML = "/Log4jMeasurePatternList.xml";

    private static Log4jMeasureProvider _INSTANCE;

    private Log4JMeasurePatternList log4jMeasurePatternList;

    private Log4jMeasureProvider() {

        super();

        String pwd = System.getProperty("user.dir");
        File extlmplFile = new File(pwd, LOG4J_MEASURE_PATTERN_LIST_XML);
        InputStream inputStream = null;

        if (extlmplFile.exists()) {
            try {
                inputStream = new FileInputStream(extlmplFile);
            } catch (FileNotFoundException e) {
                LOG.error("File not found: " + extlmplFile, e);
            }
        } else {
            inputStream = getClass().getResourceAsStream(LOG4J_MEASURE_PATTERN_LIST_XML);
        }

        if (inputStream != null) {
            try {
                JAXBContext jaxb = JAXBContext.newInstance(AlertMessageList.class);
                Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                InputStream xsdInputStream = getClass().getResourceAsStream("/Log4jMeasurePatternList.xsd");
                Schema amlSchema = schemaFactory.newSchema(new StreamSource(xsdInputStream));
                unmarshaller.setSchema(amlSchema);

                log4jMeasurePatternList = (Log4JMeasurePatternList) unmarshaller.unmarshal(inputStream);
                LOG.info("Log4JMeasurePatternList size: " + log4jMeasurePatternList.getLog4JMeasurePattern().size());
            } catch (Exception e) {
                LOG.error("Error loading Log4J Measure Pattern List", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOG.error("Error closing InputStream", e);
                    }
                }
            }
        } else {
            LOG.info("Log4jMeasurePatternList.xml could not be found");
        }

    }

    public static Log4jMeasureProvider getInstance() {

        if (_INSTANCE == null) {
            _INSTANCE = new Log4jMeasureProvider();
        }

        return _INSTANCE;
    }

    public Log4JMeasurePatternList getLog4jMeasurePatternList() {
        return log4jMeasurePatternList;
    }

}
