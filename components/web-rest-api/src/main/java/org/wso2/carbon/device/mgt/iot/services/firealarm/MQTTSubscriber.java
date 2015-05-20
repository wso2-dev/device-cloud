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

public class MQTTSubscriber implements MqttCallback {

	private static Logger log = Logger.getLogger(MQTTSubscriber.class);

	private MqttClient client;
	private MqttConnectOptions options;
	private int msgCounter = 0;
	private String clientId = "out:";
	private String subscribeTopic = "wso2/iot/+/FireAlarm/#";

	public MQTTSubscriber(String owner, String deviceUuid) {
		this.clientId += owner + ":" + deviceUuid;
		// this.subscribeTopic += owner + "/" + "FireAlarm" + "/" + deviceUuid;
		this.subscribe();
	}

	private void subscribe() {
		try {
			client = new MqttClient(FireAlarmController.CONTROL_QUEUE_ENDPOINT, clientId, null);
			options = new MqttConnectOptions();
			options.setCleanSession(false);
			options.setWill("fireAlarm/disconnection", "crashed".getBytes(), 2, true);
			client.setCallback(this);
			if (!client.isConnected()) {
				log.info("SUBSCRIBING WITH ID : " + clientId);
				client.connect(options);
				client.subscribe(subscribeTopic, 0);
			}

		} catch (MqttException ex) {
			String errorMsg =
			                  "MQTT Client Error\n" + "\tReason:  " + ex.getReasonCode() +
			                          "\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
			                          ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
			                          "\n\tException: " + ex;
			log.error(errorMsg);
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
		log.info("Lost Connection for client: " + this.clientId);
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
		log.info("Message for client " + this.clientId + "delivered successfully.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.
	 * String, org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	@Override
	public void messageArrived(String arg0, MqttMessage arg1) {
		log.info("Got Something: ");
		log.info("Arg0: " + arg0);
		log.info("Arg1: " + arg1);

//		Thread thread = new Thread() {
//			public void run() {
//				try {
//					FireAlarmController.persistance.open(clientId,
//					                                     FireAlarmController.CONTROL_QUEUE_ENDPOINT);
//					FireAlarmController.persistance.put("" + msgCounter++,
//					                                    new MqttPersistentData(
//					                                                           "TEST",
//					                                                           "".getBytes(),
//					                                                           0,
//					                                                           0,
//					                                                           "PAYLOAD".getBytes(),
//					                                                           0, 0));
//					FireAlarmController.persistance.close();
//					log.info("Closed File");
//				} catch (MqttPersistenceException e) {
//					log.info("Exception: " + e);
//
//				}
//			}
//		};
//
//		thread.start();
	}

}
