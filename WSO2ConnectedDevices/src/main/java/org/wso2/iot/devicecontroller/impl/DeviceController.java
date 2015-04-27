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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.wso2.iot.devicecontroller.ControlQueueConnector;
import org.wso2.iot.devicecontroller.DataStoreConnector;
import org.wso2.iot.utils.IoTConfiguration;
import org.wso2.iot.utils.XmlParser;
import org.xml.sax.SAXException;

/**
 * @author smean-MAC
 * 
 */
@Path(value = "/DeviceController")
public class DeviceController {
	private static Logger log = Logger.getLogger(DeviceController.class);

	private static DataStoreConnector iotDataStore = null;
	private static ControlQueueConnector iotControlQueue = null;

	static {

		// File file = new
		// ResourceFileLoader("/resources/conf/device-controls/controller.xml").getFile();
		File file =
		            new File(
		                     "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/security/client-truststore.jks");
System.out.println(file);
		if (file.exists()) {
			XmlParser xml;
//			try {
//				xml = new XmlParser(file);
				// file =
				// new ResourceFileLoader("/resources/security/" +
				// xml.getTagValues("IoTDeviceController/Security/client")[0]).getFile();
				if (file.exists()) {
					String trustStore = file.getAbsolutePath();
					System.out.println(trustStore);
					System.setProperty("javax.net.ssl.trustStore", trustStore);
					// System.setProperty("javax.net.ssl.trustStorePassword",
					// xml.getTagValues("IoTDeviceController/Security/password")[0]);
					System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
				}
//			} catch (ParserConfigurationException | SAXException | IOException e) {
//				log.error("Error on configuration" + e);

				// } catch (XPathExpressionException e) {
				// log.error("Error on configuration" + e);
//			}
		}

		try {
			iotDataStore = IoTConfiguration.getInstance().getDataStore();
			iotControlQueue = IoTConfiguration.getInstance().getControlQueue();
		} catch (InstantiationException | IllegalAccessException | ConfigurationException e) {
			log.error("Error creating DataStore or ControlQueue objects");
		}

	}

	@Path("/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{key}/{value}")
	@POST
	// @Produces("application/xml")
	public String pushData(@PathParam("ip") String ipAdd, @PathParam("type") String deviceType,
	                       @PathParam("owner") String owner, @PathParam("mac") String macAddress,
	                       @PathParam("time") Long time, @PathParam("key") String key,
	                       @PathParam("value") String value,
	                       @HeaderParam("description") String description) {

		HashMap<String, String> deviceDataMap = new HashMap<String, String>();

		deviceDataMap.put("ipAdd", ipAdd);
		deviceDataMap.put("deviceType", deviceType);
		deviceDataMap.put("owner", owner);
		deviceDataMap.put("macAddress", macAddress);
		deviceDataMap.put("time", "" + time);
		deviceDataMap.put("key", key);
		deviceDataMap.put("value", value);
		deviceDataMap.put("description", description);

		String result = iotDataStore.publishIoTData(deviceDataMap);
		return result;
	}

	@Path("/setcontrol/{owner}/{type}/{mac}/{key}/{value}")
	@POST
	public String setControl(@PathParam("owner") String owner,
	                         @PathParam("type") String deviceType,
	                         @PathParam("mac") String macAddress, @PathParam("key") String key,
	                         @PathParam("value") String value) {
		HashMap<String, String> deviceControlsMap = new HashMap<String, String>();

		deviceControlsMap.put("owner", owner);
		deviceControlsMap.put("deviceType", deviceType);
		deviceControlsMap.put("macAddress", macAddress);
		deviceControlsMap.put("key", key);
		deviceControlsMap.put("value", value);

		String result = iotControlQueue.enqueueControls(deviceControlsMap);
		return result;
	}

	public static void main(String[] args) {
		
		DeviceController myController = new DeviceController();
		String pushOut =
		                 myController.pushData("10.100.7.38", "Arduino", "Shabirmean", "123456",
		                                       Long.parseLong("234890"), "Sensor", "23", "Testing");

		String setOut = myController.setControl("Shabirmean", "Arduino", "123456", "13", "HIGH");

		System.out.println("---------------------------------------");
		System.out.println("PUSH : " + pushOut);
		System.out.println("---------------------------------------");
		System.out.println("SET : " + setOut);
	}
}
