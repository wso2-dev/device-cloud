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

package org.wso2.carbon.device.mgt.iot.firealarm.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.MQTTSubscriber;
import org.wso2.carbon.device.mgt.iot.firealarm.api.FireAlarmControllerService;
import org.wso2.carbon.device.mgt.iot.firealarm.constants.FireAlarmConstants;

import java.io.File;
import java.util.LinkedList;

public class MQTTFirealarmSubscriber extends MQTTSubscriber {


	private static Log log = LogFactory.getLog(MQTTFirealarmSubscriber.class);
	private static final String subscribetopic =
			"wso2" + File.separator + "iot" + File.separator + "+" + File.separator +
					FireAlarmConstants.DEVICE_TYPE + File.separator + "#";



	private MQTTFirealarmSubscriber() {

		super("Subscriber", FireAlarmConstants.DEVICE_TYPE,
			  FireAlarmControllerService.CONTROL_QUEUE_ENDPOINT, subscribetopic);
	}


	@Override
	public void messageArrived(final String arg0, final MqttMessage arg1) {
		Thread subscriberThread = new Thread() {

			public void run() {

				int lastIndex = arg0.lastIndexOf("/");
				String deviceId = arg0.substring(lastIndex + 1);

				lastIndex = arg1.toString().lastIndexOf(":");
				String msgContext = arg1.toString().substring(lastIndex + 1);

				LinkedList<String> deviceControlList = null;
				LinkedList<String> replyMessageList = null;

				if (msgContext.equals("IN")) {
					log.info("Recieved a control message: ");
					log.info("Control message topic: " + arg0);
					log.info("Control message: " + arg1.toString());
//                    synchronized (FireAlarmControllerService.internalControlsQueue) {
//                        deviceControlList = FireAlarmControllerService.internalControlsQueue.get(deviceId);
					synchronized (FireAlarmControllerService.getInternalControlsQueue()) {
						deviceControlList =
								FireAlarmControllerService.getInternalControlsQueue().get
										(deviceId);
						if (deviceControlList == null) {
//                            FireAlarmControllerService.internalControlsQueue
							FireAlarmControllerService.getInternalControlsQueue()
									.put(deviceId, deviceControlList = new LinkedList<String>());
						}
					}
					deviceControlList.add(arg1.toString());
				} else if (msgContext.equals("OUT")) {
					log.info("Recieved reply from a device: ");
					log.info("Reply message topic: " + arg0);
					log.info("Reply message: " + arg1.toString().substring(0, lastIndex));
//                    synchronized (FireAlarmControllerService.replyMsgQueue) {
//                        replyMessageList = FireAlarmControllerService.replyMsgQueue.get(deviceId);
					synchronized (FireAlarmControllerService.getReplyMsgQueue()) {
						replyMessageList = FireAlarmControllerService.getReplyMsgQueue().get(
								deviceId);
						if (replyMessageList == null) {
//                            FireAlarmControllerService.replyMsgQueue
							FireAlarmControllerService.getReplyMsgQueue()
									.put(deviceId, replyMessageList = new LinkedList<String>());
						}
					}
					replyMessageList.add(arg1.toString());
				}

			}
		};

		subscriberThread.start();

	}
}
