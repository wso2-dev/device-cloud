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
import org.apache.log4j.Logger;

/**
 * @author smean-MAC
 * 
 */
public class DefaultDeviceControlConfigs {
	Logger log = Logger.getLogger(DefaultDeviceControlConfigs.class);
	private static DefaultDeviceControlConfigs deviceControllerConfigsInstance = null;

	private final String CONFIGS_FILE_LOCATION = "/resources/conf/device-controls/controller.xml";

	private String dataStore;
	private String dataStoreUrl;
	private String dataStorePort;
	private String dataStoreUsername;
	private String dataStorePassword;

	private String controlQueue;
	private String controlQueueUrl;
	private String controlQueuePort;
	private String controlQueueUsername;
	private String controlQueuePassword;

	private String trustStoreFile;
	private String trustStorePassword;

	private DefaultDeviceControlConfigs() throws ConfigurationException {
		this.dataStore = IoTConfiguration.getInstance().getActiveDataStore();
		this.controlQueue = IoTConfiguration.getInstance().getActiveControlQueue();
		initConfigs();
	}

	public static DefaultDeviceControlConfigs getInstance() throws ConfigurationException {

		if (deviceControllerConfigsInstance == null) {
			synchronized (DefaultDeviceControlConfigs.class) {
				if (deviceControllerConfigsInstance == null) {
					deviceControllerConfigsInstance = new DefaultDeviceControlConfigs();
				}
			}
		}
		return deviceControllerConfigsInstance;
	}

	// public DefaultDeviceControlConfigs(String dataStoreName, String
	// controlQueueName)
	// throws ConfigurationException {
	// this.DATASTORE = dataStoreName;
	// this.CONTROLQUEUE = controlQueueName;
	// initConfigs();
	// }
	//
	// public DefaultDeviceControlConfigs(String pathToConfigs, String
	// dataStoreName,
	// String controlQueueName) throws ConfigurationException {
	// this.CONFIGS_FILE_LOCATION = pathToConfigs;
	// this.DATASTORE = dataStoreName;
	// this.CONTROLQUEUE = controlQueueName;
	// initConfigs();
	// }

	/**
	 * @param controlQueueName
	 * @param dataStoreName
	 * @throws ConfigurationException
	 * 
	 */
	private void initConfigs() throws ConfigurationException {
		String absolutePathToConfigsFile = null;

		absolutePathToConfigsFile = new ResourceFileLoader(CONFIGS_FILE_LOCATION).getPath();
		// absolutePathToConfigsFile =
		// "/Users/smean-MAC/Documents/WSO2Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/conf/device-controls/controller.xml";
		log.info(absolutePathToConfigsFile);

		XMLConfiguration configsXML = null;
		try {
			configsXML = new XMLConfiguration(absolutePathToConfigsFile);
		} catch (ConfigurationException e) {
			log.error("Configuration File is missing on path: " + absolutePathToConfigsFile, e);
			throw e;
		}

		configsXML.setExpressionEngine(new XPathExpressionEngine());

		dataStoreUrl =
		               configsXML.getString("DataStores/DataStore[class[@type='" + dataStore +
		                                    "']]/endpoint");

		dataStorePort =
		                configsXML.getString("DataStores/DataStore[class[@type='" + dataStore +
		                                     "']]/port");

		dataStoreUsername =
		                    configsXML.getString("DataStores/DataStore[class[@type='" + dataStore +
		                                         "']]/username");

		dataStorePassword =
		                    configsXML.getString("DataStores/DataStore[class[@type='" + dataStore +
		                                         "']]/password");

		log.info("DATASTORE_ENDPOINT_URL: " + dataStoreUrl);
		log.info("DATASTORE_ENDPOINT_PORT: " + dataStorePort);
		log.info("DATASTORE_USERNAME: " + dataStoreUsername);
		log.info("DATASTORE_PASSWORD: " + dataStorePassword);

		controlQueueUrl =
		                  configsXML.getString("ControlQueues/ControlQueue[class[@type='" +
		                                       controlQueue + "']]/endpoint");

		controlQueuePort =
		                   configsXML.getString("ControlQueues/ControlQueue[class[@type='" +
		                                        controlQueue + "']]/port");

		controlQueueUsername =
		                       configsXML.getString("ControlQueues/ControlQueue[class[@type='" +
		                                            controlQueue + "']]/username");

		controlQueuePassword =
		                       configsXML.getString("ControlQueues/ControlQueue[class[@type='" +
		                                            controlQueue + "']]/password");

		log.info("CONTROLQUEUE_ENDPOINT_URL: " + controlQueueUrl);
		log.info("CONTROLQUEUE_ENDPOINT_PORT: " + controlQueuePort);
		log.info("CONTROLQUEUE_USERNAME: " + controlQueueUsername);
		log.info("CONTROLQUEUE_PASSWORD: " + controlQueuePassword);

		trustStoreFile = configsXML.getString("Security/client");
		trustStorePassword = configsXML.getString("Security/password");

		log.info("TRUST_STORE_CERTIFICATE: " + trustStoreFile);
		log.info("TRUST_STORE_PASSWORD: " + trustStorePassword);
	}

	/**
	 * @return the trustStoreFile
	 */
	public String getTrustStoreFile() {
		return trustStoreFile;
	}

	/**
	 * @return the trustStorePassword
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	/**
	 * @return the dataStoreUrl
	 */
	public String getDataStoreUrl() {
		return dataStoreUrl;
	}

	/**
	 * @return the dataStorePort
	 */
	public String getDataStorePort() {
		return dataStorePort;
	}

	/**
	 * @return the dataStoreUsername
	 */
	public String getDataStoreUsername() {
		return dataStoreUsername;
	}

	/**
	 * @return the dataStorePassword
	 */
	public String getDataStorePassword() {
		return dataStorePassword;
	}

	/**
	 * @return the controlQueueUrl
	 */
	public String getControlQueueUrl() {
		return controlQueueUrl;
	}

	/**
	 * @return the controlQueuePort
	 */
	public String getControlQueuePort() {
		return controlQueuePort;
	}

	/**
	 * @return the controlQueueUsername
	 */
	public String getControlQueueUsername() {
		return controlQueueUsername;
	}

	/**
	 * @return the controlQueuePassword
	 */
	public String getControlQueuePassword() {
		return controlQueuePassword;
	}

	/**
	 * @return the dataStore
	 */
	public String getDataStore() {
		return dataStore;
	}

	/**
	 * @return the controlQueue
	 */
	public String getControlQueue() {
		return controlQueue;
	}

	// public static void main(String args[]) throws ConfigurationException {
	// DefaultDeviceControlConfigs myDeviceControls =
	// new DefaultDeviceControlConfigs("WSO2-BAM",
	// "AMQ");
	//
	// }

}
