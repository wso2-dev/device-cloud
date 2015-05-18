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

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.device.mgt.iot.services.DeviceController;
import org.wso2.carbon.device.mgt.iot.utils.DefaultDeviceControlConfigs;

/**
 * @author smean-MAC
 * 
 */
@Path(value = "/FireAlarmController")
public class FireAlarmController implements MqttCallback {
	private static Logger log = Logger.getLogger(FireAlarmController.class);
	private static String CONTROL_QUEUE_ENDPOINT = null;

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

	private MqttClient client;
	private MqttConnectOptions options;
	private MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence();

	@Path("/switchBulb")
	@POST
	public String setControl(@HeaderParam("owner") String owner, @HeaderParam("id") String deviceId) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "BULB", "IN");
		return result;
	}

	@Path("/readTemperature")
	@POST
	public String readTempearature(@HeaderParam("owner") String owner,
	                               @HeaderParam("id") String deviceId) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "TEMPERATURE", "IN");
		return result;
	}

	@Path("/switchFan")
	@POST
	public String switchFan(@HeaderParam("owner") String owner, @HeaderParam("id") String deviceId) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "FAN", "IN");
		return result;
	}

	@Path("/readControls/{owner}/{id}")
	@POST
	public String readControls(@PathParam("owner") String owner, @PathParam("id") String deviceId) {

		String clientId = owner + ":" + deviceId;
		String subscribeTopic = "wso2/iot/" + owner + "/" + "FireAlarm" + "/" + deviceId;

		try {
			client = new MqttClient(CONTROL_QUEUE_ENDPOINT, clientId);
			options = new MqttConnectOptions();
			options.setWill("iotDevice/clienterrors", "crashed".getBytes(), 2, true);
			options.setCleanSession(false);
			client.setCallback(this);
			client.connect(options);
			client.subscribe(subscribeTopic);
			// Thread.sleep(1000000);
			// System.exit(1);
			// client.disconnect();
		} catch (MqttException me) {
			log.error("MQTT Client Error");
			log.error("Reason:  " + me.getReasonCode());
			log.error("Message: " + me.getMessage());
			log.error("LocalMsg: " + me.getLocalizedMessage());
			log.error("Cause: " + me.getCause());
			log.error("Exception: " + me);
			me.printStackTrace();
		}

		return "True";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.
	 * Throwable)
	 */
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse
	 * .paho.client.mqttv3.IMqttDeliveryToken)
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.
	 * String, org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		log.info("Got Something: ");
		log.info("Arg0: " + arg0);
		log.info("Arg1: " + arg1.getPayload().toString());

	}

}
