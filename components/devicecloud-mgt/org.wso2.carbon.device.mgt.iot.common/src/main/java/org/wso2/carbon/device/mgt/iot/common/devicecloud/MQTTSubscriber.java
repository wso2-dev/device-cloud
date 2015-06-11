package org.wso2.carbon.device.mgt.iot.common.devicecloud;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.eclipse.paho.client.mqttv3.*;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.exception.NotImplementedException;

import java.io.File;
import java.nio.charset.StandardCharsets;


public abstract class MQTTSubscriber implements MqttCallback {

	private static Log log = LogFactory.getLog(MQTTSubscriber.class);

	private MqttClient client;
	private String clientId;
	private MqttConnectOptions options;
	private String subscribeTopic;
	private String clientWillTopic;
	// topic needs to be set from outside
	private String controlQueueEndpoint;

	protected MQTTSubscriber(String owner, String deviceType, String controlQueueEndpoint,String subscribeTopic){
	    this.clientId = owner + ":" + deviceType;
		this.subscribeTopic = subscribeTopic;
		this.clientWillTopic = deviceType + File.separator + "disconnection";
		this.controlQueueEndpoint = controlQueueEndpoint;
		this.initSubscriber(controlQueueEndpoint);
	}




	private void initSubscriber(String controlQueueEndpoint) {
		try {
			client = new MqttClient(controlQueueEndpoint, clientId, null);
			log.info("MQTT subscriber was created with ClientID : " + clientId);
		} catch (MqttException ex) {
			String errorMsg = "MQTT Client Error\n" + "\tReason:  " + ex.getReasonCode() +
					"\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
					ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
					"\n\tException: " + ex;
			log.error(errorMsg);
		}

		options = new MqttConnectOptions();
		options.setCleanSession(false);
		options.setWill(clientWillTopic, "connection crashed".getBytes(StandardCharsets.UTF_8), 2, true);
		client.setCallback(this);
	}


	/**
	 * @return the whether subscriber is connected to queue
	 */
	public boolean isConnected() {
		return client.isConnected();
	}

	public void subscribe() throws DeviceManagementException {
		try {
			client.connect(options);
			log.info("Subscriber connected to queue at: " + controlQueueEndpoint);
		} catch (MqttSecurityException ex) {
			String errorMsg = "MQTT Security Exception when connecting to queue\n" + "\tReason:  " +
					ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
					"\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
					ex.getCause() + "\n\tException: " + ex; //throw
			if (log.isDebugEnabled()) {
				log.debug(errorMsg);
			}
			throw new DeviceManagementException(errorMsg, ex);

		} catch (MqttException ex) {
			String errorMsg = "MQTT Exception when connecting to queue\n" + "\tReason:  " +
					ex.getReasonCode() + "\n\tMessage: " + ex.getMessage() +
					"\n\tLocalMsg: " + ex.getLocalizedMessage() + "\n\tCause: " +
					ex.getCause() + "\n\tException: " + ex; //throw
			if (log.isDebugEnabled()) {
				log.debug(errorMsg);
			}
			throw new DeviceManagementException(errorMsg, ex);
		}

		try {
			client.subscribe(subscribeTopic, 0);

			log.info("Subscribed with client id: " + clientId);
			log.info("Subscribed to topic: " + subscribeTopic);
		} catch (MqttException ex) {
			String errorMsg = "MQTT Exception when trying to subscribe to topic: " +
					subscribeTopic + "\n\tReason:  " + ex.getReasonCode() +
					"\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
					ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
					"\n\tException: " + ex;
			if (log.isDebugEnabled()) {
				log.debug(errorMsg);
			}
		}
	}



	@Override public void connectionLost(Throwable arg0) {
		log.warn("Lost Connection for client: " + this.clientId + " to " + controlQueueEndpoint);
		Thread subscriberDaemon = new Thread() {

			public void run() {
				while (true) {
					if (!isConnected()) {
						if (log.isDebugEnabled()) {
							log.debug("Subscriber reconnecting to queue........");
						}
						try {
							subscribe();
						} catch (DeviceManagementException e) {
							if (log.isDebugEnabled()) {
								log.debug("Could not reconnect and subscribe to ControlQueue.");
							}
						}
					} else {
						return;
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						log.error("An Interrupted Exception in Subscriber thread.");
					}
				}
			}
		};
		subscriberDaemon.setDaemon(true);
		subscriberDaemon.start();
	}

	@Override public void deliveryComplete(IMqttDeliveryToken arg0) {
		log.info("Message for client " + this.clientId + "delivered successfully.");
	}

	@Override public void messageArrived(final String topic, final MqttMessage message){
		postMessageArrived(topic,message);
	}

	/*
	This method is used for post processing of a message.
	 */
	protected abstract void postMessageArrived(String topic,MqttMessage message);





}
