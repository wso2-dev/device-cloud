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


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;

@Path("/FireAlarmDeviceManager")
public class FireAlarmManager {

	private static Log log = LogFactory.getLog(FireAlarmManager.class);

	@Path("/Register")
	@PUT
	public void register(@QueryParam("deviceId") String deviceId,
	                     @QueryParam("name") String name,@QueryParam("owner") String owner,
	                     @Context HttpServletResponse response) throws DeviceManagementException
	                                                                                                 {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		if (deviceManagement.isExist(deviceIdentifier)) {

			response.setStatus(409);

		}
		Device device = new Device();
		device.setDeviceIdentifier(deviceId);

		device.setDateOfEnrolment(new Date().getTime());
		device.setDateOfLastUpdate(new Date().getTime());
		device.setStatus(true);

		device.setName(name);
		device.setType(FireAlarmConstants.DEVICE_TYPE);
		device.setDeviceTypeId(1);
		device.setOwner(owner);
		boolean added=deviceManagement.addNewDevice(device);

if (added) {

			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/Remove")
	@DELETE
	public void removeDevice(@QueryParam("deviceId") String deviceId,
	                         @Context HttpServletResponse response) throws DeviceManagementException {
		

		DeviceManagement deviceManagement = new DeviceManagement();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		

		
		boolean removed = deviceManagement.removeDevice(deviceIdentifier);
		if (removed) {

			response.setStatus(200);
		} else {
			response.setStatus(409);

		}

	}

	@Path("/Update")
	@POST
	public void updateDevice(@QueryParam("deviceId") String deviceId, @QueryParam("name") String name,
	                         @Context HttpServletResponse response) throws DeviceManagementException {

		

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		device.setDeviceIdentifier(deviceId);

		// device.setDeviceTypeId(deviceTypeId);
		device.setDateOfLastUpdate(new Date().getTime());

		device.setName(name);
		device.setType(FireAlarmConstants.DEVICE_TYPE);

		boolean updated= deviceManagement.update(device);
		if (updated) {
			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/Get")
	@GET
	@Consumes("application/json")
	public Device getDevice(@QueryParam("deviceId") String deviceId,
	                        @QueryParam("type") String type,
	                        @Context HttpServletResponse response) throws DeviceManagementException {

		


		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		Device device= deviceManagement.getDevice(deviceIdentifier);
if (device == null) {

			response.setStatus(409);
			return null;
		}

		response.setStatus(200);
		return device;

	}

}
