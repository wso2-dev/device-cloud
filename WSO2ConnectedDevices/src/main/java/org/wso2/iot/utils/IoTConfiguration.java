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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.devicecontroller.ControlQueueConnector;
import org.wso2.iot.devicecontroller.DataStoreConnector;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.enroll.UserManagement;

/**
 * @author ayyoobhamza
 * 
 */
public class IoTConfiguration {
	private static Log log = LogFactory.getLog(IoTConfiguration.class);
	private static IoTConfiguration iotConfigurationInstance = null;

	// configuration variables
	private Class<?> userManagement;
	private Class<?> deviceManagement;
	private Class<?> dataStore;
	private Class<?> controlQueue;

	private String activeDataStore = null;
	private String activeControlQueue = null;
	private int deviceCheckerCacheSize=0;

	private String CONFIGS_FILE_LOCATION = "/resources/conf/configuration.xml";

	private IoTConfiguration() throws ConfigurationException {
		String absolutePathToConfigsFile = null;
		String classTypeToLoad = null;
		String classNameToLoad = null;
		try {

			absolutePathToConfigsFile = new ResourceFileLoader(CONFIGS_FILE_LOCATION).getPath();
			// absolutePathToConfigsFile =
			// "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/conf/configuration.xml";
			log.info(absolutePathToConfigsFile);

			XMLConfiguration config = new XMLConfiguration(absolutePathToConfigsFile);
			config.setExpressionEngine(new XPathExpressionEngine());

			// read all configurations

			//Read Cache Size of DeviceCheckerCache
			classTypeToLoad = config.getString("Main/DeviceCheckerCache");
			deviceCheckerCacheSize=Integer.parseInt(classTypeToLoad);
			
			
			
			// Load class mapped for device management from configuration.xml
			classTypeToLoad = config.getString("Main/Enroll/Device-Class-Type");
			classNameToLoad =
			                  config.getString("Device-Enroll-Endpoint/class[@type='" +
			                                   classTypeToLoad + "']");

			deviceManagement = IoTConfiguration.class.forName(classNameToLoad);
			log.info(deviceManagement);

			// Load class mapped for user management from configuration.xml
			classTypeToLoad = config.getString("Main/Enroll/User-Class-Type");
			classNameToLoad =
			                  config.getString("User-Enroll-Endpoint/class[@type='" +
			                                   classTypeToLoad + "']");

			userManagement = IoTConfiguration.class.forName(classNameToLoad);
			log.info(userManagement);

			// Load class mapped for data-store from configuration.xml
			classTypeToLoad = config.getString("Main/DeviceController/DeviceDataStore");
			classNameToLoad =
			                  config.getString("DataStores/DataStore/class[@type='" +
			                                   classTypeToLoad + "']");

			dataStore = IoTConfiguration.class.forName(classNameToLoad);
			activeDataStore = classTypeToLoad;

			log.info("Active Data-Store: " + activeDataStore);
			log.info(dataStore);

			// Load class mapped for control-queue from configuration.xml
			classTypeToLoad = config.getString("Main/DeviceController/DeviceControlQueue");
			classNameToLoad =
			                  config.getString("ControlQueues/ControlQueue/class[@type='" +
			                                   classTypeToLoad + "']");

			controlQueue = IoTConfiguration.class.forName(classNameToLoad);
			activeControlQueue = classTypeToLoad;

			log.info("Active Control-Queue: " + activeControlQueue);
			log.info(controlQueue);

		} catch (ConfigurationException cex) {
			log.error("Configuration File is missing on path: " + absolutePathToConfigsFile, cex);
			throw cex;
		} catch (ClassNotFoundException e) {
			log.error("Invalid Class Name: " + classNameToLoad + "  :" + e);
			throw new ConfigurationException("Invalid className: " + classNameToLoad, e);
		}

	}

	public static IoTConfiguration getInstance() throws ConfigurationException {

		if (iotConfigurationInstance == null) {
			synchronized (IoTConfiguration.class) {
				if (iotConfigurationInstance == null) {
					iotConfigurationInstance = new IoTConfiguration();
				}
			}
		}
		return iotConfigurationInstance;
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

		if (DataStoreConnector.class.isAssignableFrom(dataStore)) {
			return (DataStoreConnector) dataStore.newInstance();
		}

		String error =
		               "Invalid class format for <DataStore>, Make sure it has implemented DataStoreConnector Interface correctly";
		log.error(error);
		throw new InstantiationException(error);

	}

	public ControlQueueConnector getControlQueue() throws InstantiationException,
	                                              IllegalAccessException {

		if (ControlQueueConnector.class.isAssignableFrom(controlQueue)) {
			return (ControlQueueConnector) controlQueue.newInstance();
		}

		String error =
		               "Invalid class format for <ControlQueue>, Make sure it has implemented ControlQueueConnector Interface correctly";
		log.error(error);
		throw new InstantiationException(error);

	}

	/**
	 * @return the activeDataStore
	 */
	public String getActiveDataStore() {
		return activeDataStore;
	}

	/**
	 * @return the activeControlQueue
	 */
	public String getActiveControlQueue() {
		return activeControlQueue;
	}
	
	public int getDeviceCheckerCacheSize(){
		
		return deviceCheckerCacheSize;
	}

	// public static void main(String args[]) throws ConfigurationException,
	// InstantiationException,
	// IllegalAccessException {
	//
	// DataStoreConnector BAMDataStore =
	// IoTConfiguration.getInstance().getDataStore();
	// BAMDataStore.initDataStore();
	// }

}
