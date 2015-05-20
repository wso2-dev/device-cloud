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

import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.iot.services.DeviceController;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path(value = "/FireAlarmController")
public class FireAlarmController {

	private static Logger log = Logger.getLogger(FireAlarmController.class);

	@Path("/switchBulb")
	@POST
	public String setControl(@HeaderParam("owner") String owner,
							 @HeaderParam("id") String deviceId) {

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
	public String switchFan(@HeaderParam("owner") String owner,
							@HeaderParam("id") String deviceId) {

		String result = null;
		result = DeviceController.setControl(owner, "FireAlarm", deviceId, "FAN", "IN");
		return result;
	}

	@Path("/subscribe/{owner}/{id}")
	@POST
	public String subscribe(@PathParam("owner") String owner, @PathParam("id") String deviceId) {

		//		Runnable r = new MQTTSubscriber(owner, deviceId);
		//		new Thread(r).start();

		MQTTSubscriber mySubscriber = new MQTTSubscriber(owner, deviceId);

		return "Subscribed";
	}

}
