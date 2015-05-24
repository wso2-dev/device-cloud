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

package org.wso2.carbon.device.mgt.iot.services.firealarm;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.config.FireAlarmConfigurationManager;
import org.wso2.carbon.device.mgt.iot.config.FireAlarmManagementConfig;
import org.wso2.carbon.device.mgt.iot.config.FireAlarmManagementControllerConfig;
import org.wso2.carbon.device.mgt.iot.config.controlqueue.FireAlarmControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.services.DeviceController;
import org.wso2.carbon.device.mgt.iot.services.DeviceDataJSON;
import org.wso2.carbon.device.mgt.iot.services.DeviceReplyJSON;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

@Path(value = "/FireAlarmController")
public class FireAlarmControllerService {

	private static Logger log = Logger.getLogger(FireAlarmControllerService.class);

	public static final String CONTROL_QUEUE_ENDPOINT;
	public static final HashMap<String, LinkedList<String>> replyMsgQueue;
	public static final HashMap<String, LinkedList<String>> internalControlsQueue;
	public static MQTTSubscriber mqttSubscriber;

	static {
		FireAlarmManagementConfig config = null;

		try {
			config = FireAlarmConfigurationManager.getInstance().getFireAlarmMgtConfig();
		} catch (DeviceManagementException ex) {
			log.error("Error occurred when trying to read configurations file: firealarm-config"
							  + ".xml", ex);
		}

		if (config != null) {
			// controller configurations
			FireAlarmManagementControllerConfig controllerConfig = config
					.getFireAlarmManagementControllerConfig();

			// reading control queue configurations
			String controlQueueKey = controllerConfig.getDeviceControlQueue();
			FireAlarmControlQueueConfig controlQueueConfig = config.getControlQueuesMap().get(
					controlQueueKey);
			if (controlQueueConfig == null) {
				log.error("Error occurred when trying to read control queue configurations");
			}

			String mqttUrl = controlQueueConfig.getEndPoint();
			String mqttPort = controlQueueConfig.getPort();
			CONTROL_QUEUE_ENDPOINT = mqttUrl + ":" + mqttPort;
			log.info("CONTROL_QUEUE_ENDPOINT Successfully initialized.");
		} else {
			CONTROL_QUEUE_ENDPOINT = null;
			log.error("CONTROL_QUEUE_ENDPOINT initialization failed.");
		}

		replyMsgQueue = new HashMap<String, LinkedList<String>>();
		internalControlsQueue = new HashMap<String, LinkedList<String>>();
	}

	/**
	 * @param mqttSubscriber
	 *            the mqttSubscriber to set
	 */
	public void setMqttSubscriber(MQTTSubscriber mqttSubscriber) {
		this.mqttSubscriber = mqttSubscriber;
		mqttSubscriber.subscribe();
		Thread subscriberDaemon = new Thread() {

			public void run() {
				while (true) {
					if (!FireAlarmControllerService.mqttSubscriber.isConnected()) {
						log.info("Subscriber reconnecting to queue........");
						FireAlarmControllerService.mqttSubscriber.subscribe();
					}
				}
			}

		};
		subscriberDaemon.setDaemon(true);
		subscriberDaemon.start();
	}

	@Path("/switchbulb")
	@POST
	public String switchBulb(@HeaderParam("owner") String owner,
							 @HeaderParam("deviceId") String deviceId) {
		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "BULB", "IN");
		return result;
	}

	@Path("/readtemperature")
	@GET
	public String readTempearature(@HeaderParam("owner") String owner,
								   @HeaderParam("deviceId") String deviceId) {
		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "TEMPERATURE", "IN");
		return result;
	}

	@Path("/switchfan")
	@POST
	public String switchFan(@HeaderParam("owner") String owner,
							@HeaderParam("deviceId") String deviceId) {
		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "FAN", "IN");
		return result;
	}

	@Path("/readcontrols/{owner}/{deviceId}")
	@GET
	public String readControls(@PathParam("owner") String owner,
							   @PathParam("deviceId") String deviceId,
							   @Context HttpServletResponse response) {
		String result = null;
		LinkedList<String> deviceControlList = internalControlsQueue.get(deviceId);

		if (deviceControlList == null) {
			result = "No controls have been set for device " + deviceId + " of owner " + owner;
			response.setStatus(HttpStatus.SC_NO_CONTENT);
		} else {
			try {
				result = deviceControlList.remove();
				response.setStatus(HttpStatus.SC_ACCEPTED);
			} catch (NoSuchElementException ex) {
				result = "There are no more controls for device " + deviceId + " of owner " + owner;
				response.setStatus(HttpStatus.SC_NO_CONTENT);
			}
		}
		log.info(result);
		return result;
	}

	@Path("/reply")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String reply(final DeviceReplyJSON replyMsg) {
		String result = null;
		result = DeviceController.setControl(replyMsg.owner, "FireAlarm", replyMsg.deviceId,
											 replyMsg.replyMessage, "OUT");
		return result;
	}

	@Path("/pushalarmdata")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String pushAlarmData(final DeviceDataJSON dataMsg,
								@Context HttpServletResponse response) {
		String result = null;
		result = DeviceController.pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId,
										   dataMsg.time, dataMsg.key, dataMsg.value,
										   dataMsg.replyMessage, response);
		return result;
	}
}
