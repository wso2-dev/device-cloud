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

package org.wso2.carbon.device.mgt.iot.devicecloud;

import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.DeviceManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.DeviceManagementSecurityConfig;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.controlqueue.DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.datastore.DeviceDataStoreConfig;
import org.wso2.carbon.device.mgt.iot.devicecloud.exception.DeviceControllerException;
import org.wso2.carbon.device.mgt.iot.devicecloud.exception.UnauthorizedException;
import org.wso2.carbon.device.mgt.iot.devicecloud.controlqueue.ControlQueueConnector;
import org.wso2.carbon.device.mgt.iot.devicecloud.config.datastore.DataStoreConnector;
import org.wso2.carbon.device.mgt.iot.devicecloud.util.ResourceFileLoader;
import org.wso2.carbon.device.mgt.iot.common.DeviceValidator;

import java.io.File;
import java.util.HashMap;

public class DeviceController {

	private static Logger log = Logger.getLogger(DeviceController.class);
	private static DataStoreConnector iotDataStore = null;
	private static ControlQueueConnector iotControlQueue = null;
	private static DeviceDataStoreConfig dataStoreConfig = null;
	private static DeviceControlQueueConfig controlQueueConfig = null;

	static {

		String trustStoreFile = null;
		String trustStorePassword = null;
		File certificateFile = null;

		DeviceManagementConfig config = null;

		try {
			config = DeviceConfigurationManager.getInstance().getFireAlarmMgtConfig();
		} catch (DeviceControllerException ex) {
			log.error(ex.getMessage(), ex);
		}

		if (config != null) {
			/* reading security configurations */
			DeviceManagementSecurityConfig securityConfig =
					config.getDeviceManagementSecurityConfig();
			trustStoreFile = securityConfig.getClient();
			trustStorePassword = securityConfig.getTrustStorePassword();
			certificateFile = new ResourceFileLoader("/resources/security/" + trustStoreFile)
					.getFile();

			if (certificateFile.exists()) {
				trustStoreFile = certificateFile.getAbsolutePath();
				log.info("Trust Store Path : " + trustStoreFile);

				System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
				System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
			} else {
				log.error("Trust Store not found in path : " + trustStoreFile);
			}

			// controller configurations
			DeviceManagementControllerConfig controllerConfig =
					config.getFireAlarmManagementControllerConfig();

			// reading data store configurations
			String deviceDataStoreKey = controllerConfig.getDeviceDataStore();
			log.info("Active Data-Store: " + deviceDataStoreKey);

			dataStoreConfig = (DeviceDataStoreConfig) config.getDataStoresMap().get(
					deviceDataStoreKey);
			if (dataStoreConfig == null) {
				log.error("Error occurred when trying to read data stores configurations");
			}

			//initialization data store
			try {
				String handlerClass = dataStoreConfig.getHandlerClass().trim();
				Class<?> dataStore = DeviceController.class.forName(handlerClass);
				if (DataStoreConnector.class.isAssignableFrom(dataStore)) {
					iotDataStore = (DataStoreConnector) dataStore.newInstance();
					iotDataStore.initDataStore();
				}
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
				log.error("Error occurred when trying to initiate data store", ex);
			} catch (DeviceControllerException ex) {
				log.error(ex.getMessage(), ex);
			}

			// reading control queue configurations
			String controlQueueKey = controllerConfig.getDeviceControlQueue();
			controlQueueConfig = (DeviceControlQueueConfig) config.getControlQueuesMap().get(
					controlQueueKey);
			if (controlQueueConfig == null) {
				log.error("Error occurred when trying to read control queue configurations");
			}

			//initialization control queue
			try {
				String handlerClass = controlQueueConfig.getHandlerClass().trim();
				Class<?> controlQueue = DeviceController.class.forName(handlerClass);
				if (ControlQueueConnector.class.isAssignableFrom(controlQueue)) {
					iotControlQueue = (ControlQueueConnector) controlQueue.newInstance();
					iotControlQueue.initControlQueue();
				}
			} catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
				log.error("Error occurred when trying to initiate control queue", ex);
			} catch (DeviceControllerException ex) {
				log.error(ex.getMessage(), ex);
			}
		}
	}


	public static boolean pushData(String owner, String deviceType,
								   String deviceId, Long time, String key,
								   String value, String description) throws UnauthorizedException {

		HashMap<String, String> deviceDataMap = new HashMap<String, String>();

		deviceDataMap.put("owner", owner);
		deviceDataMap.put("deviceType", deviceType);
		deviceDataMap.put("deviceId", deviceId);
		deviceDataMap.put("time", "" + time);
		deviceDataMap.put("key", key);
		deviceDataMap.put("value", value);
		deviceDataMap.put("description", description);

		DeviceValidator deviceChecker = new DeviceValidator();

		DeviceIdentifier dId = new DeviceIdentifier(deviceId,deviceType);


		try {
			boolean exists = deviceChecker.isExist(owner, dId);

			if (exists) {

				iotDataStore.publishIoTData(deviceDataMap);
				return true;

			}else{

				throw new UnauthorizedException(
						"There is no mapping between owner:" + owner + " and device id:" + deviceId);
			}

		} catch (DeviceControllerException e) {

				log.error(
						"Failed to push " + description + " data to dataStore at " +

								dataStoreConfig.getEndPoint() + ":"
								+ dataStoreConfig.getPort());

			return false;

		} catch (DeviceManagementException e) {

			log.error("Error whilst trying to authenticate the owner with device");
			return false;


		}

	}

	public static boolean setControl(String owner, String deviceType, String deviceId, String key,
									 String value) throws UnauthorizedException{
		HashMap<String, String> deviceControlsMap = new HashMap<String, String>();

		deviceControlsMap.put("owner", owner);
		deviceControlsMap.put("deviceType", deviceType);
		deviceControlsMap.put("deviceId", deviceId);
		deviceControlsMap.put("key", key);
		deviceControlsMap.put("value", value);

		DeviceValidator deviceChecker = new DeviceValidator();
		DeviceIdentifier dId = new DeviceIdentifier(deviceId,deviceType);


		try {
			boolean exists = deviceChecker.isExist(owner, dId);

			if (exists) {
				iotControlQueue.enqueueControls(deviceControlsMap);
				return true;
			}else{

				throw new UnauthorizedException(
						"There is no mapping between owner:" + owner + " and device id:" + deviceId);

			}
		} catch (DeviceControllerException e) {

				log.error("Failed to enqueue data to queue at " +
								  controlQueueConfig.getEndPoint() + ":" +
								  controlQueueConfig.getPort());




			return false;
		} catch (DeviceManagementException e) {

			log.error("Error whilst trying to authenticate the owner with device");
			return false;


		}

	}


}
