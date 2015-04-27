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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.Logger;
import org.wso2.iot.devicecontroller.ControlQueueConnector;
import org.wso2.iot.devicecontroller.DataStoreConnector;
import org.wso2.iot.enroll.UserManagement;
import org.wso2.iot.utils.IoTConfiguration;
import org.wso2.iot.utils.ResourceFileLoader;

/**
 * @author smean-MAC
 * 
 */
public class DeviceControlConfigurations {
	Logger log = Logger.getLogger(DeviceControlConfigurations.class);

	private static DeviceControlConfigurations deviceControlConfigs = null;

	private String configsFile = null;

	private Class<?> dataStoreConfigs;
	private Class<?> controlQueueConfigs;

	private DeviceControlConfigurations() throws ConfigurationException, ClassNotFoundException,
	                                     ReflectiveOperationException {
		String activeEndpoint = null;
		String className = null;

		try {
			configsFile =
			              new ResourceFileLoader("resources/conf/device-controls/controller.xml").getPath();

			XMLConfiguration configsXML = new XMLConfiguration(configsFile);
			configsXML.setExpressionEngine(new XPathExpressionEngine());

			activeEndpoint = configsXML.getString("DeviceController/DeviceDataStore");
			className =
			            configsXML.getString("DataStores/DataStore/class[@name='" + activeEndpoint +
			                                 "']");

			dataStoreConfigs = DeviceControlConfigurations.class.forName(className);

			activeEndpoint = configsXML.getString("DeviceController/DeviceControlQueue");
			className =
			            configsXML.getString("ControlQueues/ControlQueue/class[@type='" +
			                                 activeEndpoint + "']");

			controlQueueConfigs = DeviceControlConfigurations.class.forName(className);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectiveOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static DeviceControlConfigurations getInstance() throws ConfigurationException {

		if (deviceControlConfigs == null) {
			synchronized (DeviceControlConfigurations.class) {
				if (deviceControlConfigs == null) {
					try {
						deviceControlConfigs = new DeviceControlConfigurations();
					} catch (ReflectiveOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return deviceControlConfigs;
	}

	public DataStoreConnector getDataStore() throws InstantiationException, IllegalAccessException {

		if (DataStoreConnector.class.isAssignableFrom(dataStoreConfigs)) {
			return (DataStoreConnector) dataStoreConfigs.newInstance();
		}

		String error = "DataStore Error";
		log.error(error);
		throw new InstantiationException(error);

	}

	public ControlQueueConnector getControlQueue() throws InstantiationException,
	                                              IllegalAccessException {

		if (ControlQueueConnector.class.isAssignableFrom(controlQueueConfigs)) {
			return (ControlQueueConnector) controlQueueConfigs.newInstance();
		}

		String error = "Control Queue Error";
		log.error(error);
		throw new InstantiationException(error);

	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
	                                      ConfigurationException {
		DataStoreConnector iotDataStore = DeviceControlConfigurations.getInstance().getDataStore();
		iotDataStore.initDataStore();
	}
}
