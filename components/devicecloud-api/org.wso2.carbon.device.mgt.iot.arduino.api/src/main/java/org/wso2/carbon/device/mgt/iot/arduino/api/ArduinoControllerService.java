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

package org.wso2.carbon.device.mgt.iot.arduino.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.DeviceController;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.DeviceCloudManagementConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config
        .DeviceCloudManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.config.controlqueue
        .DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.DeviceControllerException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.UnauthorizedException;
import org.wso2.carbon.device.mgt.iot.arduino.api.util.DeviceJSON;
import org.wso2.carbon.device.mgt.iot.arduino.api.util.MQTTArduinoSubscriber;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;


public class ArduinoControllerService {

    private static Log log = LogFactory.getLog(ArduinoControllerService.class);

    public static final String CONTROL_QUEUE_ENDPOINT;
    private static Map<String, LinkedList<String>> replyMsgQueue =new HashMap<>();;
    private static Map<String, LinkedList<String>> internalControlsQueue=new HashMap<>();
    private static MQTTArduinoSubscriber mqttArduinoSubscriber;

    static {

        DeviceCloudManagementConfig config = null;
        try {
            config = DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig();
        } catch (DeviceControllerException ex) {
            log.error(ex.getMessage());
        }

        if (config != null) {
            // controller configurations
            DeviceCloudManagementControllerConfig controllerConfig = config.getDeviceCloudManagementControllerConfig();

            // reading control queue configurations
            String controlQueueKey = controllerConfig.getDeviceControlQueue();
            DeviceControlQueueConfig controlQueueConfig = config.getControlQueuesMap().get(controlQueueKey);
            if (controlQueueConfig == null) {
                log.error("Error occurred when trying to read control queue configurations");
            }

            String mqttUrl = "";
            String mqttPort = "";
            if (controlQueueConfig != null) {
                mqttUrl = controlQueueConfig.getEndPoint();
                mqttPort = controlQueueConfig.getPort();
            }

            CONTROL_QUEUE_ENDPOINT = mqttUrl + ":" + mqttPort;
            log.info("CONTROL_QUEUE_ENDPOINT Successfully initialized.");
        } else {
            CONTROL_QUEUE_ENDPOINT = null;
            log.error("CONTROL_QUEUE_ENDPOINT initialization failed.");
        }

    }


    public  void setMqttArduinoSubscriber(MQTTArduinoSubscriber mqttArduinoSubscriber) {
        ArduinoControllerService.mqttArduinoSubscriber = mqttArduinoSubscriber;
        try {
            mqttArduinoSubscriber.subscribe();
        } catch (DeviceManagementException e) {
            log.error(e.getErrorMessage());
        }
    }

    public  MQTTArduinoSubscriber getMqttArduinoSubscriber() {
        return mqttArduinoSubscriber;
    }

    public static Map<String, LinkedList<String>> getReplyMsgQueue() {
        return Collections.unmodifiableMap(replyMsgQueue);
    }

    public static Map<String, LinkedList<String>> getInternalControlsQueue() {
        return Collections.unmodifiableMap(internalControlsQueue);
    }




    /*    Service to switch arduino bulb (pin 13) between "ON" and "OFF"
               Called by an external client intended to control the Arduino fan */
    @Path("/switch/bulb") @POST public void switchBulb(@HeaderParam("owner") String owner,
            @HeaderParam("deviceId") String deviceId,@HeaderParam("deviceId") String status, @Context HttpServletResponse response) {

        try {
            boolean result = DeviceController.setControl(owner, "Arduino", deviceId, "Bulb", "IN");
            if (result) {
                response.setStatus(HttpStatus.SC_ACCEPTED);

            } else {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);

            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }

    /*    Service to poll the control-queue for the controls sent to the Arduino
               Called by the Arduino device  */
    @Path("/readcontrols/{owner}/{deviceId}") @GET public String readControls(@PathParam("owner") String owner,
            @PathParam("deviceId") String deviceId, @Context HttpServletResponse response) {
        String result;
        LinkedList<String> deviceControlList = internalControlsQueue.get(deviceId);

        if (deviceControlList == null) {
            result = "No controls have been set for device " + deviceId + " of owner " + owner;
            response.setStatus(HttpStatus.SC_NO_CONTENT);
        } else {
            try {
                result = deviceControlList.remove(); //returns the  head value
                response.setStatus(HttpStatus.SC_ACCEPTED);

            } catch (NoSuchElementException ex) {
                result = "There are no more controls for device " + deviceId + " of owner " +
                        owner;
                response.setStatus(HttpStatus.SC_NO_CONTENT);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(result);
        }

        return result;
    }

    /*    Service to send back the replies for the controls sent to the Arduino
           Called by the Arduino device  */
    @Path("/reply") @POST @Consumes(MediaType.APPLICATION_JSON) public void reply(final DeviceJSON replyMsg,
            @Context HttpServletResponse response) {
        try {
            boolean result = DeviceController
                    .setControl(replyMsg.owner, "Arduino", replyMsg.deviceId, replyMsg.reply, "OUT");
            if (result) {
                response.setStatus(HttpStatus.SC_ACCEPTED);

            } else {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);

            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }

    /*    Service to push all the sensor data collected by the Arduino
           Called by the Arduino device  */
    @Path("/pushdata") @POST @Consumes(MediaType.APPLICATION_JSON) public void pushData(
            final DeviceJSON dataMsg, @Context HttpServletResponse response) {
        boolean result;

        String sensorValues = dataMsg.value;
        log.info("Recieved Sensor Data Values: " + sensorValues);

        String sensors[] = sensorValues.split(":");
        try {
            if (sensors.length == 3) {
                String temperature = sensors[0];
                String bulb = sensors[1];
                String fan = sensors[2];

                sensorValues = "Temperature:" + temperature + "C\tBulb Status:" + bulb + "\t\tFan Status:" +
                        fan;
                log.info(sensorValues);

                result = DeviceController
                        .pushData(dataMsg.owner, "Arduino", dataMsg.deviceId, System.currentTimeMillis(),
                                "DeviceData", temperature, "TEMP");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                result = DeviceController
                        .pushData(dataMsg.owner, "Arduino", dataMsg.deviceId, System.currentTimeMillis(),
                                "DeviceData", bulb, "BULB");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                result = DeviceController
                        .pushData(dataMsg.owner, "Arduino", dataMsg.deviceId, System.currentTimeMillis(),
                                "DeviceData", fan, "FAN");

                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }

            } else {
                result = DeviceController
                        .pushData(dataMsg.owner, "Arduino", dataMsg.deviceId, System.currentTimeMillis(),
                                "DeviceData", dataMsg.value, dataMsg.reply);
                if (!result) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                }
            }

        } catch (UnauthorizedException e) {
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);

        }

    }
}
