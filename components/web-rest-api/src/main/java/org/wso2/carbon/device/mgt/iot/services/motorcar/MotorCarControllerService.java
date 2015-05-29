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

package org.wso2.carbon.device.mgt.iot.services.motorcar;

import org.apache.log4j.Logger;
import org.wso2.carbon.device.mgt.iot.services.DeviceControllerService;
import org.wso2.carbon.device.mgt.iot.services.firealarm.DeviceJSON;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path(value = "/MotorCarController")
public class MotorCarControllerService {

	private static Logger log = Logger.getLogger(MotorCarControllerService.class);
	private static final String SEP = ":";

	//turnleft turnright moveforward movebackward stop

	@Path("/turnLeft")
	@POST
	public String turnLeft(@QueryParam("owner") String owner,
						   @QueryParam("deviceId") String deviceId,
						   @QueryParam("speed") String speed,
						   @QueryParam("duration") String duration) {
		String result = null;
		String value = "L" + SEP + speed + SEP + duration;
		result = DeviceControllerService.setControl(owner, "MotorCar", deviceId, "CAR", value);
		return result;
	}

	@Path("/turnRight")
	@POST
	public String turnRight(@QueryParam("owner") String owner,
							@QueryParam("deviceId") String deviceId,
							@QueryParam("speed") String speed,
							@QueryParam("duration") String duration) {
		String result = null;
		String value = "R" + SEP + speed + SEP + duration;
		result = DeviceControllerService.setControl(owner, "MotorCar", deviceId, "CAR", value);
		return result;
	}

	@Path("/moveForward")
	@POST
	public String moveForward(@QueryParam("owner") String owner,
							  @QueryParam("deviceId") String deviceId,
							  @QueryParam("speed") String speed,
							  @QueryParam("duration") String duration) {
		String result = null;
		String value = "F" + SEP + speed + SEP + duration;
		result = DeviceControllerService.setControl(owner, "MotorCar", deviceId, "CAR", value);
		return result;
	}

	@Path("/moveBackward")
	@POST
	public String moveBackward(@QueryParam("owner") String owner,
							   @QueryParam("deviceId") String deviceId,
							   @QueryParam("speed") String speed,
							   @QueryParam("duration") String duration) {
		String result = null;
		String value = "B" + SEP + speed + SEP + duration;
		result = DeviceControllerService.setControl(owner, "MotorCar", deviceId, "CAR", value);
		return result;
	}

	@Path("/stop")
	@POST
	public String stop(@QueryParam("owner") String owner, @QueryParam("deviceId") String deviceId,
					   @QueryParam("speed") String speed, @QueryParam("duration") String duration) {
		String result = null;
		String value = "S";
		result = DeviceControllerService.setControl(owner, "MotorCar", deviceId, "CAR", value);
		return result;
	}

	@Path("/pushData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String pushAlarmData(final DeviceJSON dataMsg, @Context HttpServletResponse response) {
		String result = null;
		result = DeviceControllerService.pushData(dataMsg.owner, "MotorCar", dataMsg.deviceId,
												  dataMsg.time, dataMsg.key, dataMsg.value,
												  dataMsg.replyMessage, response);
		return result;
	}

}
