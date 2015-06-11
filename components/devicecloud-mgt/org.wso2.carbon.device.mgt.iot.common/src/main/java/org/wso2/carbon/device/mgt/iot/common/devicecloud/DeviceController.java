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

package org.wso2.carbon.device.mgt.iot.common.devicecloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementSecurityConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.controlqueue.DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.datastore.DeviceDataStoreConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.controlqueue.ControlQueueConnector;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.datastore.DataStoreConnector;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.DeviceControllerException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.UnauthorizedException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.util.ResourceFileLoader;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.util.HashMap;

public class DeviceController {

    private static final Log log = LogFactory.getLog(DeviceController.class);
    private static DataStoreConnector iotDataStore = null;
    private static ControlQueueConnector iotControlQueue = null;
    private static DeviceDataStoreConfig dataStoreConfig = null;
    private static DeviceControlQueueConfig controlQueueConfig = null;

    static {

        String trustStoreFile = null;
        String trustStorePassword = null;
        File certificateFile = null;

        DeviceCloudManagementConfig config = null;

        try {
            config = DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig();
        } catch (DeviceControllerException ex) {
            log.error(ex.getMessage(), ex);
        }

        if (config != null) {
            /* reading security configurations */
            DeviceCloudManagementSecurityConfig securityConfig = config.getDeviceCloudManagementSecurityConfig();
            trustStoreFile = securityConfig.getClient();
            trustStorePassword = securityConfig.getTrustStorePassword();
            String certificatePath =
                    CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "resources"
                            + File.separator + "security" + File.separator;

            certificateFile = new ResourceFileLoader(certificatePath + trustStoreFile).getFile();
            if (certificateFile.exists()) {
                trustStoreFile = certificateFile.getAbsolutePath();
                log.info("Trust Store Path : " + trustStoreFile);

                System.setProperty("javax.net.ssl.trustStore", trustStoreFile);
                System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
            } else {
                log.error("Trust Store not found in path : " + certificateFile.getAbsolutePath());
            }

            // controller configurations
            DeviceCloudManagementControllerConfig controllerConfig = config.getDeviceCloudManagementControllerConfig();

            // reading data store configurations
            String deviceDataStoreKey = controllerConfig.getDeviceDataStore();
            log.info("Active Data-Store: " + deviceDataStoreKey);

            dataStoreConfig = config.getDataStoresMap().get(deviceDataStoreKey);
            if (dataStoreConfig == null) {
                log.error("Error occurred when trying to read data stores configurations");
            }

            //initialization data store
            try {
                String handlerClass = "";
                if (dataStoreConfig != null) {
                    handlerClass = dataStoreConfig.getHandlerClass().trim();
                }

                Class<?> dataStore = Class.forName(handlerClass);
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
            controlQueueConfig = config.getControlQueuesMap().get(controlQueueKey);
            if (controlQueueConfig == null) {
                log.error("Error occurred when trying to read control queue configurations");
            }

            //initialization control queue
            try {
                String handlerClass = "";
                if (controlQueueConfig != null) {
                    handlerClass = controlQueueConfig.getHandlerClass().trim();
                }

                Class<?> controlQueue = Class.forName(handlerClass);
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

    public static boolean pushData(String owner, String deviceType, String deviceId, Long time, String key,
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

        DeviceIdentifier dId = new DeviceIdentifier(deviceId, deviceType);

        try {
            boolean exists = deviceChecker.isExist(owner, dId);

            if (exists) {

                iotDataStore.publishIoTData(deviceDataMap);
                return true;

            } else {

                throw new UnauthorizedException(
                        "There is no mapping between owner:" + owner + " and device id:" + deviceId);
            }

        } catch (DeviceControllerException e) {

            log.error("Failed to push " + description + " data to dataStore at " +

                    dataStoreConfig.getEndPoint() + ":" + dataStoreConfig.getPort());

            return false;

        } catch (DeviceManagementException e) {

            log.error("Error whilst trying to authenticate the owner with device");
            return false;

        }

    }

    public static boolean setControl(String owner, String deviceType, String deviceId, String key, String value)
            throws UnauthorizedException {
        HashMap<String, String> deviceControlsMap = new HashMap<String, String>();

        deviceControlsMap.put("owner", owner);
        deviceControlsMap.put("deviceType", deviceType);
        deviceControlsMap.put("deviceId", deviceId);
        deviceControlsMap.put("key", key);
        deviceControlsMap.put("value", value);

        DeviceValidator deviceChecker = new DeviceValidator();
        DeviceIdentifier dId = new DeviceIdentifier(deviceId, deviceType);

        try {
            boolean exists = deviceChecker.isExist(owner, dId);

            if (exists) {
                iotControlQueue.enqueueControls(deviceControlsMap);
                return true;
            } else {

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
