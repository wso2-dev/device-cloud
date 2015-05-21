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

package org.wso2.carbon.device.mgt.iot.utils;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.devicecontroller.ControlQueueConnector;
import org.wso2.carbon.device.mgt.iot.devicecontroller.DataStoreConnector;
import org.wso2.carbon.utils.CarbonUtils;

/**
 * @author smean-MAC
 * 
 */
public class DeviceCloudConfiguration {
	private static Log log = LogFactory.getLog(DeviceCloudConfiguration.class);
	private static DeviceCloudConfiguration deviceCloudConfigurationInstance = null;

	private Class<?> dataStore;
	private Class<?> controlQueue;

	private String activeDataStore = null;
	private String activeControlQueue = null;
	private int deviceCheckerCacheSize = 0;

	private String DC_CONFIGS_FILE_LOCATION = CarbonUtils.getEtcCarbonConfigDirPath() +
	                                          File.separator + "iot-config.xml";

	private DeviceCloudConfiguration() throws ConfigurationException {

		String classTypeToLoad = null;
		String classNameToLoad = null;
		try {
			log.info(DC_CONFIGS_FILE_LOCATION);

			XMLConfiguration config = new XMLConfiguration(DC_CONFIGS_FILE_LOCATION);
			config.setExpressionEngine(new XPathExpressionEngine());

			// read all configurations

			// Read Cache Size of DeviceCheckerCache
			classTypeToLoad = config.getString("Main/DeviceCheckerCache");
			deviceCheckerCacheSize = Integer.parseInt(classTypeToLoad);

			// Load class mapped for data-store from configuration.xml
			classTypeToLoad = config.getString("Main/DeviceController/DeviceDataStore");
			classNameToLoad =
			                  config.getString("DataStores/DataStore/class[@type='" +
			                                   classTypeToLoad + "']");

			dataStore = DeviceCloudConfiguration.class.forName(classNameToLoad);
			activeDataStore = classTypeToLoad;

			log.info("Active Data-Store: " + activeDataStore);
			log.info(dataStore);

			// Load class mapped for control-queue from configuration.xml
			classTypeToLoad = config.getString("Main/DeviceController/DeviceControlQueue");
			classNameToLoad =
			                  config.getString("ControlQueues/ControlQueue/class[@type='" +
			                                   classTypeToLoad + "']");

			controlQueue = DeviceCloudConfiguration.class.forName(classNameToLoad);
			activeControlQueue = classTypeToLoad;

			log.info("Active Control-Queue: " + activeControlQueue);
			log.info(controlQueue);

		} catch (ConfigurationException cex) {
			log.error("Configuration File is missing on path: " + DC_CONFIGS_FILE_LOCATION, cex);
			throw cex;
		} catch (ClassNotFoundException e) {
			log.error("Invalid Class Name: " + classNameToLoad + "  :" + e);
			throw new ConfigurationException("Invalid className: " + classNameToLoad, e);
		}

	}

	public static DeviceCloudConfiguration getInstance() throws ConfigurationException {

		if (deviceCloudConfigurationInstance == null) {
			synchronized (DeviceCloudConfiguration.class) {
				if (deviceCloudConfigurationInstance == null) {
					deviceCloudConfigurationInstance = new DeviceCloudConfiguration();
				}
			}
		}
		return deviceCloudConfigurationInstance;
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

	/**
	 * @return the deviceCheckerCacheSize
	 */
	public int getDeviceCheckerCacheSize() {
		return deviceCheckerCacheSize;
	}

}
