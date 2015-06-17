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

package org.wso2.carbon.device.mgt.iot.common.devicecloud.datastore.bam;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.*;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.datastore.DeviceDataStoreConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.datastore.DataStoreConnector;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.DeviceControllerException;

import java.net.MalformedURLException;
import java.util.HashMap;


public class BAMDataStore implements DataStoreConnector {




    private static final Log log = LogFactory.getLog(BAMDataStore.class);
    private String DATASTORE_ENDPOINT = "";
    private String DATASTORE_USERNAME = "";
    private String DATASTORE_PASSWORD = "";

    private DataPublisher BAM_DATA_PUBLISHER = null;
    private String DEVICE_DATA_STREAM = null;

    public BAMDataStore() {
    }

    /*
     * (non-Javadoc)
     *
     * @see org.wso2.iot.device.controller.DataStoreConnector#initDataStore()
     */
    @Override public void initDataStore() throws DeviceControllerException {
        String dataStore = null;
        String bamUrl = "";
        String bamPort = "";

        DeviceCloudManagementConfig config = null;
        try {
            config = DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig();
            // controller configurations
            DeviceCloudManagementControllerConfig controllerConfig = config.getDeviceCloudManagementControllerConfig();
            dataStore = controllerConfig.getDeviceDataStore();

            DeviceDataStoreConfig dataStoreConfig = config.getDataStoresMap().get(dataStore);

            bamUrl = dataStoreConfig.getEndPoint();
            bamPort = dataStoreConfig.getPort();

            DATASTORE_ENDPOINT = bamUrl + ":" + bamPort;
            DATASTORE_USERNAME = dataStoreConfig.getUserName();
            DATASTORE_PASSWORD = dataStoreConfig.getPassword();

            log.info("DATASTORE_ENDPOINT : " + DATASTORE_ENDPOINT);
        } catch (DeviceControllerException ex) {
            String error = "Error occurred when trying to read configurations file: firealarm-config.xml";
            log.error(error, ex);
            throw new DeviceControllerException(error, ex);
        }

        try {
            BAM_DATA_PUBLISHER = new DataPublisher(DATASTORE_ENDPOINT, DATASTORE_USERNAME, DATASTORE_PASSWORD);
            log.info("DATA PUBLISHER created for endpoint " + DATASTORE_ENDPOINT);
        } catch (MalformedURLException | AgentException | AuthenticationException
                | TransportException e) {
            String error = "Error creating DataPublisher for Endpoint: " + DATASTORE_ENDPOINT +
                    " with credentials, USERNAME-" + DATASTORE_USERNAME + " and PASSWORD-" +
                    DATASTORE_PASSWORD + ": ";
            log.error(error, e);
            throw new DeviceControllerException(error, e);
        }

        try {
            DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(
                    "{" + "'name':'org_wso2_iot_devices_data'," + "'version':'1.0.0',"
                            + "'nickName': 'IoT Connected Device Data'," + "'description': 'Data Received from Device',"
                            + "'tags': ['iot', 'embeddedDevice']," + "'metaData':["
                            + "        {'name':'owner','type':'STRING'},"
                            + "        {'name':'deviceType','type':'STRING'},"
                            + "        {'name':'deviceId','type':'STRING'}," + "		{'name':'requestTime','type':'LONG'}"
                            + "]," + "'payloadData':[" + "        {'name':'key','type':'STRING'},"
                            + "        {'name':'value','type':'STRING'},"
                            + "        {'name':'description','type':'STRING'}" + "]" + "}");

            log.info("stream definition ID for data from device pin: " + DEVICE_DATA_STREAM);

        } catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
                | DifferentStreamDefinitionAlreadyDefinedException e) {
            String error = "Error in defining default stream for data publisher";
            log.error(error, e);
            throw new DeviceControllerException(error, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.wso2.iot.device.controller.DataStoreConnector#publishIoTData(java
     * .util.HashMap)
     */
    @Override public void publishIoTData(HashMap<String, String> deviceData) throws
                                                                             DeviceControllerException {

        String logMsg = "";
        String owner = deviceData.get("owner");
        String deviceType = deviceData.get("deviceType");
        String deviceId = deviceData.get("deviceId");
        String time = deviceData.get("time");
        String key = deviceData.get("key");
        String value = deviceData.get("value");
        String description = deviceData.get("description");

        try {
            switch (description) {
            case BAMStreamDefinitions.StreamConstants.TEMPERATURE:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Temperature");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.TEMPERATURE_STREAM_DEFINITION);
                break;
            case BAMStreamDefinitions.StreamConstants.MOTION:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Motion (PIR)");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.MOTION_STREAM_DEFINITION);
                break;
            case BAMStreamDefinitions.StreamConstants.SONAR:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Sonar");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.SONAR_STREAM_DEFINITION);
                break;
            case BAMStreamDefinitions.StreamConstants.LIGHT:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Light");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.LIGHT_STREAM_DEFINITION);
                break;
            case BAMStreamDefinitions.StreamConstants.BULB:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Bulb Status");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.BULB_STREAM_DEFINITION);
                break;
            case  BAMStreamDefinitions.StreamConstants.FAN:
                if (log.isDebugEnabled()) {
                    log.info("Stream definition set to Fan Status");
                }
                DEVICE_DATA_STREAM = BAM_DATA_PUBLISHER.defineStream(BAMStreamDefinitions.FAN_STREAM_DEFINITION);
                break;
            default:
                //  will leave the stream definition to be the default one
                break;
            }
        } catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
                | DifferentStreamDefinitionAlreadyDefinedException e) {
            String error = "Error in defining fire-alarm specific streams for data publisher";
            log.error(error, e);
            throw new DeviceControllerException(error, e);
        }

        try {
            //if (deviceType.equalsIgnoreCase("FireAlarm") || deviceType.equalsIgnoreCase("SenseBot")|| deviceType.equalsIgnoreCase("sensebot")) {
                if (log.isDebugEnabled()) {
                    log.info("Publishing FireAlarm specific data");
                }
                BAM_DATA_PUBLISHER.publish(DEVICE_DATA_STREAM, System.currentTimeMillis(),
                        new Object[] { owner, deviceType, deviceId, Long.parseLong(time) }, null,
                        new Object[] { value });

                logMsg =
                        "event published to devicePinDataStream\n" + "\tOwner: " + owner + "\tDeviceType: " + deviceType
                                + "\n" + "\tDeviceId: " + deviceId + "\tTime: " + time + "\n" + "\tDescription: "
                                + description + "\n" + "\tKey: " + key + "\tValue: " + value + "\n";

//            } else {
//                if (log.isDebugEnabled()) {
//                    log.info("Publishing exception device specific data");
//                }
//                BAM_DATA_PUBLISHER.publish(DEVICE_DATA_STREAM, System.currentTimeMillis(),
//                        new Object[] { owner, deviceType, deviceId, Long.parseLong(time) }, null,
//                        new Object[] { key, value, description });
//
//                logMsg =
//                        "event published to devicePinDataStream\n" + "\tOwner: " + owner + "\tDeviceType: " + deviceType
//                                + "\n" + "\tDeviceId: " + deviceId + "\tTime: " + time + "\n" + "\tDescription: "
//                                + description + "\n" + "\tKey: " + key + "\tValue: " + value + "\n";
//
//            }

            if (log.isDebugEnabled()) {
                log.info(logMsg);
            }

        } catch (AgentException e) {
            String error = "Error while publishing device pin data";
            log.error(error, e);
            throw new DeviceControllerException(error, e);
        }
    }

	/*
     *
	 * ==========================================================
	 * // Have to define the stream definition in the BAM tbox
	 * 
	 * ==========================================================
	 */

    // public static void main(String[] args) {
    //
    // File file =
    // new File(
    // "/Users/smean-MAC/Documents/WSO2Git/device-cloud/devicecloud-api/src/main/webapp/resources/security/client-truststore.jks");
    // System.out.println(file);
    //
    // if (file.exists()) {
    // String trustStore = file.getAbsolutePath();
    // System.out.println(trustStore);
    // System.setProperty("javax.net.ssl.trustStore", trustStore);
    // System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
    // }
    //
    // HashMap<String, String> myMap = new HashMap<String, String>();
    // myMap.put("ipAdd", "192.168.1.216");
    // myMap.put("deviceType", "Arduino");
    // myMap.put("owner", "Smeansbeer");
    // myMap.put("macAddress", "123456");
    // myMap.put("time", "" + System.nanoTime());
    // myMap.put("key", "TempSensor");
    // myMap.put("value", "123");
    // myMap.put("description", "TetsCase");
    //
    // BAMDataStore newinst = new BAMDataStore();
    // System.out.println(newinst.initDataStore());
    // System.out.println(newinst.publishIoTData(myMap));
    // }
}
