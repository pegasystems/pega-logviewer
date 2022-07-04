/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/

package com.pega.gcs.logviewer.model.alert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.fringecommon.utilities.FileUtilities;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class AlertMessageListProvider {

    private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageListProvider.class);

    private static final String ALERT_MESSAGE_LIST_XML = "/AlertMessageList.xml";

    // checking for null is not thread safe; static initialisation
    private static AlertMessageListProvider _INSTANCE = new AlertMessageListProvider();

    private Map<Integer, AlertMessage> alertMessageMap;

    // can store additional unknown alert message id's found during parsing
    private AtomicInteger adhocAlertId;

    private Map<String, Integer> messageIdAlertIdMap;

    private List<String> criticalAlertList;

    private Map<String, List<String>> alertMessageTypeMap;

    private AlertMessageListProvider() {
        super();

        alertMessageMap = null;
        messageIdAlertIdMap = null;

        adhocAlertId = new AtomicInteger(10000);

        InputStream amlInputStream = FileUtilities.getResourceAsStreamFromUserDir(getClass(), ALERT_MESSAGE_LIST_XML);

        if (amlInputStream != null) {
            try {
                JAXBContext jaxb = JAXBContext.newInstance(AlertMessageList.class);
                Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                InputStream amlXsdInputStream = getClass().getResourceAsStream("/AlertMessageList.xsd");
                Schema amlSchema = schemaFactory.newSchema(new StreamSource(amlXsdInputStream));
                unmarshaller.setSchema(amlSchema);

                AlertMessageList alertMessageList = (AlertMessageList) unmarshaller.unmarshal(amlInputStream);
                LOG.info("alertMessageList size: " + alertMessageList.getAlertMessage().size());

                alertMessageMap = new TreeMap<Integer, AlertMessage>();
                messageIdAlertIdMap = new HashMap<String, Integer>();
                alertMessageTypeMap = new HashMap<String, List<String>>();

                criticalAlertList = new ArrayList<String>();

                for (AlertMessage alertMessage : alertMessageList.getAlertMessage()) {

                    Integer id = alertMessage.getId();
                    String messageId = alertMessage.getMessageID();
                    Severity severity = alertMessage.getSeverity();
                    String dssValueType = alertMessage.getDssValueType();

                    if (severity.equals(Severity.CRITICAL)) {
                        criticalAlertList.add(messageId);
                    }

                    alertMessageMap.put(id, alertMessage);
                    messageIdAlertIdMap.put(messageId, id);

                    if ((dssValueType == null) || ("".equals(dssValueType))) {
                        dssValueType = "OTHERS";
                    }

                    List<String> alertMessageIdList = alertMessageTypeMap.get(dssValueType);

                    if (alertMessageIdList == null) {
                        alertMessageIdList = new ArrayList<>();
                        alertMessageTypeMap.put(dssValueType, alertMessageIdList);
                    }

                    alertMessageIdList.add(messageId);
                }
            } catch (Exception e) {
                LOG.error("Error loading Alert Message List", e);
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
            LOG.info("AlertMessageList.xml could not be found");
        }
    }

    public Integer getAlertId(String messageId) {

        Integer alertId = messageIdAlertIdMap.get(messageId);

        if (alertId == null) {
            AlertMessage alertMessage = addNewAlertMessage(messageId);
            alertId = alertMessage.getId();
        }

        return alertId;
    }

    public AlertMessage getAlertMessage(String messageId) {

        Integer alertId = getAlertId(messageId);

        AlertMessage alertMessage = alertMessageMap.get(alertId);

        return alertMessage;
    }

    public AlertMessage getAlertMessage(Integer alertId) {

        AlertMessage alertMessage = alertMessageMap.get(alertId);

        return alertMessage;
    }

    private synchronized AlertMessage addNewAlertMessage(String messageId) {

        int id = adhocAlertId.incrementAndGet();
        AlertMessage alertMessage = new AlertMessage();
        alertMessage.setId(id);
        alertMessage.setMessageID(messageId);
        alertMessage.setSeverity(Severity.NORMAL);

        alertMessageMap.put(id, alertMessage);
        messageIdAlertIdMap.put(messageId, id);

        return alertMessage;
    }

    public Map<String, List<String>> getAlertMessageTypeMap() {
        return Collections.unmodifiableMap(alertMessageTypeMap);
    }

    public Set<String> getMessageIdSet() {
        return Collections.unmodifiableSet(messageIdAlertIdMap.keySet());
    }

    public List<String> getCriticalAlertList() {
        return criticalAlertList;
    }

    public static AlertMessageListProvider getInstance() {

        // checking for null is not thread safe; static initialisation
        //
        // if (_INSTANCE == null) {
        // _INSTANCE = new AlertMessageListProvider();
        // }

        return _INSTANCE;
    }

    public static void main(String[] args) {
        AlertMessageListProvider.getInstance().getCriticalAlertList();
    }
}
