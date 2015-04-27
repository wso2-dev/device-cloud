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
import org.wso2.iot.utils.IoTConfiguration;



@Path("/DeviceManager")
public class DeviceManager {
	
	
	@Path("/DeviceRegister")
	@PUT
	public String Register(String deviceId, boolean anonymous,@Context HttpServletRequest request, @Context HttpServletResponse response) throws InstantiationException, IllegalAccessException, ConfigurationException {
		
		
		UserManagement userManagement = IoTConfiguration.getInstance().getUserManagementImpl();
		DeviceManagement deviceManagement= IoTConfiguration.getInstance().getDeviceManagementImpl();
		boolean added=false;
		if(anonymous){
			
			
			Device device = new Device();
			device.setDeviceId(deviceId);
			String token = deviceManagement.generateNewToken();
			device.setToken(token);
			device.setOwner(userManagement.getAnonymousUserName());
			
			added=deviceManagement.addNewDevice(device);
			
			
			
		}else{
			Device device = new Device();
			device.setDeviceId(deviceId);
			String token = deviceManagement.generateNewToken();
			device.setToken(token);
			device.setOwner(userManagement.getAnonymousUserName());
			
			added=deviceManagement.addNewDevice(device);
			
			
		}
		
		if (added) {
			response.setStatus(200);
		} else {
			response.setStatus(409);
		}
		
	
		return "";

	}
	
	

	public String getDeviceToken() {
		//get Device Token with security
		return "";
	}
	
	public String generateDeviceToken(){
		
		return "";
	}
}
