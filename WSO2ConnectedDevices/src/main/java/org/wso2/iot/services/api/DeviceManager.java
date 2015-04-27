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

package org.wso2.iot.services.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.commons.configuration.ConfigurationException;
import org.wso2.iot.device.Device;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.enroll.UserManagement;
import org.wso2.iot.user.User;
import org.wso2.iot.utils.IOTConfiguration;

@Path("/DeviceManager")
public class DeviceManager {

	@Path("/DeviceAnonymousRegister")
	@PUT
	public void Register(String deviceId,@Context HttpServletRequest request,
	                     @Context HttpServletResponse response) throws InstantiationException,
	                                                           IllegalAccessException,
	                                                           ConfigurationException {

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();
		DeviceManagement deviceManagement =IOTConfiguration.getInstance().getDeviceManagementImpl();
		Device device = new Device();
		device.setDeviceId(deviceId);

		String token = deviceManagement.generateNewToken();
		device.setToken(token);
		boolean added = false;
		

		device.setOwner(userManagement.getAnonymousUserName());
		added = deviceManagement.addNewDevice(device);

		if (added) {
			setDeviceToken(device);
			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}
	
	
	@Path("/DeviceRegister")
	@PUT
	public void Register(String deviceId, String model, String type, String name,
	                     String description, @Context HttpServletRequest request,
	                     @Context HttpServletResponse response) throws InstantiationException,
	                                                           IllegalAccessException,
	                                                           ConfigurationException {

		UserManagement userManagement = IOTConfiguration.getInstance().getUserManagementImpl();
		DeviceManagement deviceManagement =
		                                    IOTConfiguration.getInstance()
		                                                    .getDeviceManagementImpl();
		Device device = new Device();
		device.setDeviceId(deviceId);
		device.setDesciption(description);
		device.setModel(model);
		device.setName(name);
		device.setType(type);

		String token = deviceManagement.generateNewToken();
		device.setToken(token);
		boolean added = false;

		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {

			response.setStatus(403);
			return;
		} else {

			device.setOwner(user.getUsername());
			added = deviceManagement.addNewDevice(device);

		}
		if (added) {
			setDeviceToken(device);
			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	public String getDeviceToken(@Context HttpServletRequest request) {
		// get Device Token with security
		return "";
	}

	private void setDeviceToken(Device device) {
		// add into localstore

	}

	private boolean isAvailableOnLocalDataStore() {
		return false;

	}

}
