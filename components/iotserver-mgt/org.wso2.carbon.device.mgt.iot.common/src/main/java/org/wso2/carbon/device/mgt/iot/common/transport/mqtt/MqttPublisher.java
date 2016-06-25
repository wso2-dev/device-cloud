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

package org.wso2.carbon.device.mgt.iot.common.transport.mqtt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.carbon.device.mgt.iot.common.exception.DeviceControllerException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This class is used for publishing control signals into MQTT queues.
 */
public class MqttPublisher implements MqttCallback {

    private static final Log log = LogFactory.getLog(MqttPublisher.class);

    private String mqttEndpoint;
    private String mqttUsername;
    private String mqttPassword;
    private boolean mqttEnabled = false;

    public MqttPublisher() {
        initControlQueue();
    }

    private void initControlQueue() {
        MqttConfig mqttConfig = MqttConfig.getInstance();
        mqttEndpoint = mqttConfig.getMqttQueueEndpoint();
        mqttUsername = mqttConfig.getMqttQueueUsername();
        mqttPassword = mqttConfig.getMqttQueuePassword();
        mqttEnabled = mqttConfig.isEnabled();
    }

    public void publish(String publishClientId, String publishTopic, byte[] payload) throws DeviceControllerException {
        if (mqttEnabled) {
            int clientIdLength = publishClientId.length();
            if (clientIdLength > 24) {
                throw new DeviceControllerException(
                        "ClientID '" + publishClientId + "' should be less than 24 characters.");
            }

            MqttConnectOptions options = new MqttConnectOptions();
            MqttClient client = null;
            try {
                client = new MqttClient(mqttEndpoint, publishClientId);
                int qosLevelDeliverOnce = 2;
                options.setWill("iotDevice/clienterrors", "crashed".getBytes(UTF_8), qosLevelDeliverOnce, true);
                client.setCallback(this);
                client.connect(options);
                int qosLevelFireandForget = 0;
                client.publish(publishTopic, payload, qosLevelFireandForget, true);

                if (log.isDebugEnabled()) {
                    log.debug("MQTT client successfully published to topic: " + publishTopic + ", with payload - "
                                      + payload);
                }
            } catch (MqttException ex) {
                throw new DeviceControllerException("MQTT client error occurred while publishing message to client id:"
                                                            + publishClientId + " for the topic: " + publishTopic, ex);
            } finally {
                if (client != null) {
                    try {
                        client.disconnect();
                    } catch (MqttException e) {
                        //do nothing
                    }
                }
            }
        } else {
            log.warn("MQTT <Enabled> set to false in 'devicecloud-config.xml'");
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("Connection to MQTT Endpoint Lost");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken topic) {
        if (log.isDebugEnabled()) {
            log.debug("Published topic: '" + topic.getTopics()[0] + "' successfully to client: '" +
                              topic.getClient().getClientId() + "'");
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("MQTT Message relieved: " + message.toString());
        }
    }
}
