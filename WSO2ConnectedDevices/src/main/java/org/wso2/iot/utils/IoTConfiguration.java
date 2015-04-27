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

package org.wso2.iot.utils;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.ThrowsAdvice;
import org.wso2.iot.devicecontroller.ControlQueueConnector;
import org.wso2.iot.devicecontroller.DataStoreConnector;
import org.wso2.iot.devicecontroller.impl.DeviceControlConfigurations;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.enroll.UserManagement;

/**
 * @author ayyoobhamza
 * 
 */
public class IoTConfiguration {
	private static Log log = LogFactory.getLog(IoTConfiguration.class);
	private static IoTConfiguration iotConfigurationsInstance = null;

	// configuration variables
	private Class<?> userManagement;
	private Class<?> deviceManagement;
	private Class<?> dataStoreConfigs;
	private Class<?> controlQueueConfigs;

	private String PLATFORM_CONFIGS_FILEPATH = "resources/conf/configuration.xml";

	private IoTConfiguration() throws ConfigurationException {
		String absolutePathToConfigsFile = null;
		String classTypeToLoad = "";
		String classNameToLoad = "";

		try {

			// absolutePathToConfigsFile = new
			// ResourceFileLoader(PLATFORM_CONFIGS_FILEPATH).getPath();
			absolutePathToConfigsFile =
			                            "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/conf/configuration.xml";;

			XMLConfiguration config = new XMLConfiguration(absolutePathToConfigsFile);
			config.setExpressionEngine(new XPathExpressionEngine());

			// read all configurations

			// load device enrollment end-point configs
			classTypeToLoad = config.getString("Main/Enroll/Device-Class-Type");
			classNameToLoad =
			                  config.getString("Device-Enroll-Endpoint/class[@type='" +
			                                   classTypeToLoad + "']");
			deviceManagement.forName(classNameToLoad);
			System.out.println(classNameToLoad + " " + classTypeToLoad);

			// load user enrollment end-point configs
			classTypeToLoad = config.getString("Main/Enroll/User-Class-Type");
			classNameToLoad =
			                  config.getString("User-Enroll-Endpoint/class[@type='" +
			                                   classTypeToLoad + "']");
			userManagement.forName(classNameToLoad);
			System.out.println(classNameToLoad + " " + classTypeToLoad);

			// load data-store end-point configs
			classTypeToLoad = config.getString("Main/DeviceController/DeviceDataStore");
			classNameToLoad =
			                  config.getString("DataStores/DataStore/class[@type='" +
			                                   classTypeToLoad + "']");
			dataStoreConfigs.forName(classNameToLoad);
			System.out.println(classNameToLoad + " " + classTypeToLoad);
			System.out.println(dataStoreConfigs);
			// load control-queue end-point configs
			classTypeToLoad = config.getString("Main/DeviceController/DeviceControlQueue");
			classNameToLoad =
			                  config.getString("ControlQueues/ControlQueue/class[@type='" +
			                                   classTypeToLoad + "']");
			controlQueueConfigs.forName(classNameToLoad);
			System.out.println(classNameToLoad + " " + classTypeToLoad);

		} catch (ConfigurationException cex) {
			log.error("Configuration File is missing on path" + absolutePathToConfigsFile, cex);
			throw cex;
		} catch (ClassNotFoundException e) {
			log.error("Invalid Class Name: " + classNameToLoad + "  :" + e);
			throw new ConfigurationException("Invalid className: " + classNameToLoad, e);
		}
	}

	public static IoTConfiguration getInstance() throws ConfigurationException {

		if (iotConfigurationsInstance == null) {
			synchronized (IoTConfiguration.class) {
				if (iotConfigurationsInstance == null) {
					iotConfigurationsInstance = new IoTConfiguration();
				}
			}
		}
		return iotConfigurationsInstance;
	}

	public UserManagement getUserManagementImpl() throws InstantiationException,
	                                             IllegalAccessException {

		if (UserManagement.class.isAssignableFrom(userManagement)) {
			return (UserManagement) userManagement.newInstance();
		}

		String error =
		               "Invalid class format for <User-Enroll-Endpoint>, Make sure it has implemented UserManagment Interface correctly";
		log.error(error);
		throw new InstantiationException(error);

	}

	public DeviceManagement getDeviceManagementImpl() throws InstantiationException,
	                                                 IllegalAccessException {

		if (DeviceManagement.class.isAssignableFrom(deviceManagement)) {
			return (DeviceManagement) deviceManagement.newInstance();
		}

		String error =
		               "Invalid class format for <Device-Enroll-Endpoint>, Make sure it has implemented DeviceManagement Interface correctly";

		log.error(error);
		throw new InstantiationException(error);
	}

	public DataStoreConnector getDataStore() throws InstantiationException, IllegalAccessException {

		if (DataStoreConnector.class.isAssignableFrom(dataStoreConfigs)) {
			return (DataStoreConnector) dataStoreConfigs.newInstance();
		}

		String error =
		               "Invalid class format for <DeviceDataStore>, Make sure it has implemented DataStoreConnector Interface correctly";

		log.error(error);
		throw new InstantiationException(error);

	}

	public ControlQueueConnector getControlQueue() throws InstantiationException,
	                                              IllegalAccessException {

		if (ControlQueueConnector.class.isAssignableFrom(controlQueueConfigs)) {
			return (ControlQueueConnector) controlQueueConfigs.newInstance();
		}

		String error =
		               "Invalid class format for <DeviceControlQueue>, Make sure it has implemented ControlQueue Interface correctly";

		log.error(error);
		throw new InstantiationException(error);

	}

	public static void main(String args[]) throws ConfigurationException, InstantiationException,
	                                      IllegalAccessException {
		
//		UserManagement user = IoTConfiguration.getInstance().getUserManagementImpl();
		DataStoreConnector BAMDataStore = IoTConfiguration.getInstance().getDataStore();
		BAMDataStore.initDataStore();
	}

}
