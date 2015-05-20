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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.wso2.carbon.device.mgt.iot.utils.DefaultDeviceControlConfigs;

public class MQTTSubscriber implements MqttCallback, Runnable {

	private static Logger log = Logger.getLogger(MQTTSubscriber.class);
	private static String CONTROL_QUEUE_ENDPOINT = null;
	private MqttDefaultFilePersistence persistance = new MqttDefaultFilePersistence("MQTTQueue");
	private MqttClient client = null;
	private MqttConnectOptions options;
	private String clientId = "out:";
	private String subscribeTopic = "wso2/iot/";

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

	public MQTTSubscriber(String owner, String deviceId) {
		this.clientId += owner + ":" + deviceId;
		this.subscribeTopic += owner + "/" + "FireAlarm" + "/" + deviceId;
		this.subscribe();
	}

	private void subscribe() {
		try {
			this.client = new MqttClient(CONTROL_QUEUE_ENDPOINT, clientId, persistance);
			this.options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setWill("iotDevice/clienterrors", "crashed".getBytes(), 2, true);
			client.setCallback(this);
			if (!client.isConnected()) {
				log.info("SUBSCRIBING WITH ID : " + clientId);
				client.connect(options);
				client.subscribe(subscribeTopic, 0);
			}

		} catch (MqttException me) {
			log.error("MQTT Client Error");
			log.error("Reason:  " + me.getReasonCode());
			log.error("Message: " + me.getMessage());
			log.error("LocalMsg: " + me.getLocalizedMessage());
			log.error("Cause: " + me.getCause());
			log.error("Exception: " + me);
			me.printStackTrace();
		}
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
		log.info("Got Something: ");
		log.info("Arg0: " + arg0);
		log.info("Arg1: " + arg1);

		persistance.open(this.clientId, CONTROL_QUEUE_ENDPOINT);
		persistance.put("" + arg1, null);

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
