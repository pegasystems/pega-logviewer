/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.logviewer.model.alert;

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
import java.util.Set;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.pega.gcs.fringecommon.log4j2.Log4j2Helper;
import com.pega.gcs.logviewer.model.alert.AlertMessageList.AlertMessage;

public class AlertMessageListProvider {

	private static final Log4j2Helper LOG = new Log4j2Helper(AlertMessageListProvider.class);

	private static final String ALERT_MESSAGE_LIST_XML = "/AlertMessageList.xml";

	private static AlertMessageListProvider _INSTANCE;

	private Map<Integer, AlertMessage> alertMessageMap;

	// can store additional unknown alert message id's found during parsing
	private static int adhocAlertId;

	private Map<String, Integer> messageIdAlertIdMap;

	private List<String> criticalAlertList;

	private Map<String, List<String>> alertMessageTypeMap;

	private AlertMessageListProvider() {
		super();

		alertMessageMap = null;
		messageIdAlertIdMap = null;

		String pwd = System.getProperty("user.dir");
		File extAMLFile = new File(pwd, ALERT_MESSAGE_LIST_XML);
		InputStream amlInputStream = null;

		if (extAMLFile.exists()) {
			try {
				amlInputStream = new FileInputStream(extAMLFile);
			} catch (FileNotFoundException e) {
				LOG.error("File not found: " + extAMLFile, e);
			}
		} else {
			amlInputStream = getClass().getResourceAsStream(ALERT_MESSAGE_LIST_XML);
		}

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
					String dssValueType = alertMessage.getDSSValueType();

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

		adhocAlertId = 10000;

	}

	/*
	 * returns unmodifiableMap use addNewAlertMessage to append to this map
	 */
	public Map<Integer, AlertMessage> getAlertMessageMap() {
		return Collections.unmodifiableMap(alertMessageMap);
	}

	/*
	 * returns unmodifiableMap use addNewAlertMessage to append to this map
	 */
	public Map<String, Integer> getMessageIdAlertIdMap() {
		return Collections.unmodifiableMap(messageIdAlertIdMap);
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
		if (_INSTANCE == null) {
			_INSTANCE = new AlertMessageListProvider();
		}

		return _INSTANCE;
	}

	public int getNextAdhocAlertId() {
		return adhocAlertId++;
	}

	public void addNewAlertMessage(AlertMessage alertMessage) {
		Integer id = alertMessage.getId();
		String messageId = alertMessage.getMessageID();

		alertMessageMap.put(id, alertMessage);
		messageIdAlertIdMap.put(messageId, id);
	}

	public static void main(String[] args) {
		AlertMessageListProvider.getInstance().getCriticalAlertList();
	}
}
