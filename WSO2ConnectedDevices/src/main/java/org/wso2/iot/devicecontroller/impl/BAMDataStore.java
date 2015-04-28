/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.iot.devicecontroller.impl;

import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.iot.devicecontroller.DataStoreConnector;
import org.wso2.iot.utils.DefaultDeviceControlConfigs;

/**
 * @author smean-MAC
 * 
 */
public class BAMDataStore implements DataStoreConnector {
	Logger log = Logger.getLogger(BAMDataStore.class);

	private String DATASTORE_ENDPOINT = "";
	private String DATASTORE_USERNAME = "";
	private String DATASTORE_PASSWORD = "";

	private DataPublisher BAM_DATA_PUBLISHER = null;
	private String DEVICE_DATA_STREAM = null;

	private String httpReply = "%d - %s";

	public BAMDataStore() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wso2.iot.device.controller.DataStoreConnector#initDataStore()
	 */
	@Override
	public String initDataStore() {
		String dataStore = null;
		String bamUrl = "";
		String bamPort = "";

		try {
			dataStore = DefaultDeviceControlConfigs.getInstance().getDataStore();

			bamUrl = DefaultDeviceControlConfigs.getInstance().getDataStoreUrl();
			bamPort = DefaultDeviceControlConfigs.getInstance().getDataStorePort();

			DATASTORE_ENDPOINT = bamUrl + ":" + bamPort;
			DATASTORE_USERNAME = DefaultDeviceControlConfigs.getInstance().getDataStoreUsername();
			DATASTORE_PASSWORD = DefaultDeviceControlConfigs.getInstance().getDataStorePassword();

			log.info("DATASTORE_ENDPOINT : " + DATASTORE_ENDPOINT);

		} catch (ConfigurationException e) {
			log.error("Error occured when retreiving configs for DataStore - " + dataStore +
			          " from controller.xml" + ": ", e);
		}

		try {
			BAM_DATA_PUBLISHER =
			                     new DataPublisher(DATASTORE_ENDPOINT, DATASTORE_USERNAME,
			                                       DATASTORE_PASSWORD);
		} catch (MalformedURLException | AgentException | AuthenticationException
		        | TransportException e) {
			log.error("Error creating DataPublisher for Endpoint: " + DATASTORE_ENDPOINT +
			          " with credentials, USERNAME-" + DATASTORE_USERNAME + " and PASSWORD-" +
			          DATASTORE_PASSWORD + ": ", e);

			return String.format(httpReply, HttpStatus.SC_BAD_GATEWAY,
			                     HttpStatus.getStatusText(HttpStatus.SC_BAD_GATEWAY));
		}

		try {
			DEVICE_DATA_STREAM =
			                     BAM_DATA_PUBLISHER.defineStream("{"
			                                                     + "'name':'org_wso2_iot_statistics_device_data',"
			                                                     + "'version':'1.0.0',"
			                                                     + "'nickName': 'IoT Connected Device Pin Data',"
			                                                     + "'description': 'Pin Data Received',"
			                                                     + "'tags': ['arduino', 'led13'],"
			                                                     + "'metaData':["
			                                                     + "        {'name':'ipAdd','type':'STRING'},"
			                                                     + "        {'name':'deviceType','type':'STRING'},"
			                                                     + "        {'name':'owner','type':'STRING'},"
			                                                     + "		{'name':'requestTime','type':'LONG'}"
			                                                     + "],"
			                                                     + "'payloadData':["
			                                                     + "        {'name':'macAddress','type':'STRING'},"
			                                                     + "        {'name':'key','type':'STRING'},"
			                                                     + "        {'name':'value','type':'STRING'},"
			                                                     + "        {'name':'description','type':'STRING'}"
			                                                     + "]" + "}");

			log.info("stream definition ID for data from device pin: " + DEVICE_DATA_STREAM);

		} catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
		        | DifferentStreamDefinitionAlreadyDefinedException e) {

			log.error("Error in defining stream for data publisher: ", e);
			return String.format(httpReply, HttpStatus.SC_INTERNAL_SERVER_ERROR,
			                     HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR));

		}

		return String.format(httpReply, HttpStatus.SC_OK,
		                     HttpStatus.getStatusText(HttpStatus.SC_OK));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wso2.iot.device.controller.DataStoreConnector#publishIoTData(java
	 * .util.HashMap)
	 */
	@Override
	public String publishIoTData(HashMap<String, String> deviceData) {

		String ipAdd = deviceData.get("ipAdd");
		String deviceType = deviceData.get("deviceType");
		String owner = deviceData.get("owner");
		String macAddress = deviceData.get("macAddress");
		String time = deviceData.get("time");
		String key = deviceData.get("key");
		String value = deviceData.get("value");
		String description = deviceData.get("description");

		try {
			BAM_DATA_PUBLISHER.publish(DEVICE_DATA_STREAM,
			                           System.currentTimeMillis(),
			                           new Object[] { ipAdd, deviceType, owner,
			                                         Long.parseLong(time) }, null,
			                           new Object[] { macAddress, key, value, description });

			log.info("event published to devicePinDataStream");

		} catch (AgentException e) {
			log.error("Error while publishing device pin data", e);
			return String.format(httpReply, HttpStatus.SC_INTERNAL_SERVER_ERROR,
			                     HttpStatus.getStatusText(HttpStatus.SC_INTERNAL_SERVER_ERROR));
		}

		return String.format(httpReply, HttpStatus.SC_ACCEPTED,
		                     HttpStatus.getStatusText(HttpStatus.SC_ACCEPTED));
	}

	/*
	 * 
	 * ==========================================================
	 * // Have to define the stream definition in the BAM tbox
	 * 
	 * ==========================================================
	 */

//	public static void main(String[] args) {
//
//		File file =
//		            new File(
//		                     "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/security/client-truststore.jks");
//		System.out.println(file);
//
//		if (file.exists()) {
//			String trustStore = file.getAbsolutePath();
//			System.out.println(trustStore);
//			System.setProperty("javax.net.ssl.trustStore", trustStore);
//			System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
//		}
//
//		HashMap<String, String> myMap = new HashMap<String, String>();
//		myMap.put("ipAdd", "192.168.1.216");
//		myMap.put("deviceType", "Arduino");
//		myMap.put("owner", "Smeansbeer");
//		myMap.put("macAddress", "123456");
//		myMap.put("time", "" + System.nanoTime());
//		myMap.put("key", "TempSensor");
//		myMap.put("value", "123");
//		myMap.put("description", "TetsCase");
//
//		BAMDataStore newinst = new BAMDataStore();
//		System.out.println(newinst.initDataStore());
//		System.out.println(newinst.publishIoTData(myMap));
//	}
}
