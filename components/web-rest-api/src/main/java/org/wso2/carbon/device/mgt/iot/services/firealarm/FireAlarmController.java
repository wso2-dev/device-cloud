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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.device.mgt.iot.services.DeviceController;
import org.wso2.carbon.device.mgt.iot.utils.DefaultDeviceControlConfigs;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

@Path(value = "/FireAlarmController")
public class FireAlarmController {

private static Logger log = Logger.getLogger(FireAlarmController.class);
	
	private HashMap<String, MQTTSubscriber> subscriptions = new HashMap<String, MQTTSubscriber>();
	static final MqttDefaultFilePersistence persistance = new MqttDefaultFilePersistence("MQTTQueue");
	static String CONTROL_QUEUE_ENDPOINT = null;
	
	private MQTTSubscriber mqttSubscriber;
	
	/**
	 * @param mqttSubscriber the mqttSubscriber to set
	 */
	public void setMqttSubscriber(MQTTSubscriber mqttSubscriber) {
		this.mqttSubscriber = mqttSubscriber;
	}

	static {
		try {
			String mqttUrl = DefaultDeviceControlConfigs.getInstance().getControlQueueUrl();
			String mqttPort = DefaultDeviceControlConfigs.getInstance().getControlQueuePort();

			CONTROL_QUEUE_ENDPOINT = mqttUrl + ":" + mqttPort;

			log.info("CONTROL_QUEUE_ENDPOINT : " + CONTROL_QUEUE_ENDPOINT);
		} catch (ConfigurationException e) {
			log.error("Error occured when retreiving configs for ControlQueue from controller.xml"
			          + ": ", e);
		}
	}
	
	@Path("/switchBulb")
	@POST
	public String setControl(@HeaderParam("owner") String owner,
	                         @HeaderParam("uuid") String deviceUuid) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceUuid, "BULB", "IN");
		return result;
	}

	@Path("/readTemperature")
	@POST
	public String readTempearature(@HeaderParam("owner") String owner,
	                               @HeaderParam("uuid") String deviceUuid) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceUuid, "TEMPERATURE", "IN");
		return result;
	}

	@Path("/switchFan")
	@POST
	public String switchFan(@HeaderParam("owner") String owner,
	                        @HeaderParam("uuid") String deviceUuid) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceUuid, "FAN", "IN");
		return result;
	}

	@Path("/subscribe/{owner}/{uuid}")
	@POST
	public String subscribe(@PathParam("owner") String owner, @PathParam("uuid") String deviceUuid,
	                        @Context HttpServletResponse response) {

//		MQTTSubscriber subscriber = subscriptions.get(deviceUuid);
//
//		if (subscriber != null) {
//			response.setStatus(HttpStatus.SC_CONFLICT);
//			return "Already subscribed";
//		}
//
//		subscriber = new MQTTSubscriber(owner, deviceUuid);
//		subscriber.subscribe();
//		subscriptions.put(deviceUuid, subscriber);
//		response.setStatus(HttpStatus.SC_CREATED);
		return "Successfully subscribed";
	}

	@Path("/readControls/{owner}/{uuid}")
	@POST
	public String readControls(@PathParam("owner") String owner,
	                           @PathParam("uuid") String deviceUuid,
	                           @Context HttpServletResponse response) {

		String clientId = "out:" + owner + ":" + deviceUuid;

		return "True";

	}

}
