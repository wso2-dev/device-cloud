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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.iot.services.DeviceController;
import org.wso2.carbon.device.mgt.iot.services.DeviceDataJSON;
import org.wso2.carbon.device.mgt.iot.services.DeviceReplyJSON;
import org.wso2.carbon.device.mgt.iot.utils.DefaultDeviceControlConfigs;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path(value = "/FireAlarmController")
public class FireAlarmController {
	private static Logger log = Logger.getLogger(FireAlarmController.class);

	public static final String CONTROL_QUEUE_ENDPOINT;
	public static final HashMap<String, LinkedList<String>> replyMsgQueue;
	public static final HashMap<String, LinkedList<String>> internalControlsQueue;
	public static MQTTSubscriber mqttSubscriber;

	static {
		String tmp = null;
		try {
			String mqttUrl = DefaultDeviceControlConfigs.getInstance().getControlQueueUrl();
			String mqttPort = DefaultDeviceControlConfigs.getInstance().getControlQueuePort();
			tmp = mqttUrl + ":" + mqttPort;
			log.info("CONTROL_QUEUE_ENDPOINT Successfully initialized.");
		} catch (ConfigurationException e) {
			log.error("Error occurred when retreiving configs for ControlQueue from controller.xml"
			          + ": ", e);
		} finally {
			CONTROL_QUEUE_ENDPOINT = tmp;
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
					if (!FireAlarmController.mqttSubscriber.isConnected()) {
						log.info("Subscriber reconnecting to queue........");
						FireAlarmController.mqttSubscriber.subscribe();
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
			response.setStatus(HttpStatus.SC_NOT_MODIFIED);
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
		result =
		         DeviceController.setControl(replyMsg.owner, "FireAlarm", replyMsg.deviceId,
		                                     replyMsg.replyMessage, "OUT");
		return result;
	}

	@Path("/pushalarmdata")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String pushAlarmData(final DeviceDataJSON dataMsg, @Context HttpServletResponse response) {
		String result = null;
		result =
		         DeviceController.pushData(dataMsg.owner, "FireAlarm", dataMsg.deviceId,
		                                   dataMsg.time, dataMsg.key, dataMsg.value,
		                                   dataMsg.replyMessage, response);
		return result;
	}
}
