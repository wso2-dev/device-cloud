/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;

import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.DeviceManagement;
import org.wso2.carbon.device.mgt.iot.common.util.DeviceTypes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class DevicesManagerService {

	//TODO; replace this tenant domain
	private final String SUPER_TENANT = "carbon.super";
	@Context  //injected response proxy supporting multiple thread
	private HttpServletResponse response;

	@Path("/devices/username/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getDevices(@PathParam("username") String username) {

		DeviceManagement deviceManagement = new DeviceManagement(SUPER_TENANT);
		try{
		List<Device> devices = deviceManagement.getDeviceManagementService().getDevicesOfUser(
				username);
		List<Device> activeDevices = new ArrayList<>();
		if (devices != null) {
			for (Device device : devices) {
				if (device.getEnrolmentInfo().getStatus().equals(EnrolmentInfo.Status.ACTIVE)) {
					activeDevices.add(device);
				}
			}
		}
		return activeDevices.toArray(new Device[]{});

		} catch (DeviceManagementException e) {
			response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		} finally {
			deviceManagement.endTenantFlow();
		}
	}

	@Path("/devices/ungrouped/username/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getUnGroupedDevices(@PathParam("username") String username){

		DeviceManagement deviceManagement = new DeviceManagement(SUPER_TENANT);
		try{
		List<Device> devices = deviceManagement.getDeviceManagementService().getUnGroupedDevicesOfUser(
				username);
		List<Device> activeDevices = new ArrayList<>();
		if (devices != null) {
			for (Device device : devices) {
				if (device.getEnrolmentInfo().getStatus().equals(EnrolmentInfo.Status.ACTIVE)) {
					activeDevices.add(device);
				}
			}
		}
		return activeDevices.toArray(new Device[]{});
		} catch (DeviceManagementException e) {
			response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		} finally {
			deviceManagement.endTenantFlow();
		}
	}

	@Path("/devices/count/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public int getDeviceCount(@PathParam("username") String username){

		DeviceManagement deviceManagement = new DeviceManagement(SUPER_TENANT);
		try{
		List<Device> devices = deviceManagement.getDeviceManagementService().getDevicesOfUser(
				username);


		if (devices != null) {
			List<Device> activeDevices = new ArrayList<>();
			for (Device device : devices) {
				if (device.getEnrolmentInfo().getStatus().equals(EnrolmentInfo.Status.ACTIVE)) {
					activeDevices.add(device);
				}
			}
			return activeDevices.size();
		}
		return 0;
		} catch (DeviceManagementException e) {
			response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return 0;
		} finally {
			deviceManagement.endTenantFlow();
		}
	}

	@Path("/devices/{type}/{identifier}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device getDevice(@PathParam("type") String type, @PathParam("identifier") String identifier){

		DeviceManagement deviceManagement = new DeviceManagement(SUPER_TENANT);
		try{
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(identifier);
            deviceIdentifier.setType(type);
			return deviceManagement.getDeviceManagementService().getDevice(deviceIdentifier);
		} catch (DeviceManagementException e) {
			response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return null;
		} finally {
			deviceManagement.endTenantFlow();
		}
	}

	@Path("/devices/types/")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public DeviceTypes[] getDeviceTypes(){

		DeviceManagement deviceManagement = new DeviceManagement(SUPER_TENANT);
		try{
			return deviceManagement.getDeviceTypes();
		} catch (DeviceManagementDAOException e) {
			response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			return null;
		} finally {
			deviceManagement.endTenantFlow();
		}
	}

}
