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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

@Path("/FireAlarmDeviceManager")
public class FireAlarmManager {

	private static Log log = LogFactory.getLog(FireAlarmManager.class);

	@Path("/Register")
	@PUT
	public void register(@QueryParam("deviceId") String deviceId, @QueryParam("name") String name,
						 @QueryParam("owner") String owner, @Context HttpServletResponse response)
			throws DeviceManagementException {

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
		boolean added = deviceManagement.addNewDevice(device);

		if (added) {

			response.setStatus(200);
		} else {
			response.setStatus(409);
		}

	}

	@Path("/Remove")
	@DELETE
	public void removeDevice(@QueryParam("deviceId") String deviceId,
							 @Context HttpServletResponse response)
			throws DeviceManagementException {

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
	public void updateDevice(@QueryParam("deviceId") String deviceId,
							 @QueryParam("name") String name, @Context HttpServletResponse response)
			throws DeviceManagementException {

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

		boolean updated = deviceManagement.update(device);
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
							@QueryParam("type") String type, @Context HttpServletResponse response)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(FireAlarmConstants.DEVICE_TYPE);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		if (device == null) {

			response.setStatus(409);
			return null;
		}

		response.setStatus(200);
		return device;

	}

	@Path("/DownloadSketch")
	@GET
	@Produces("application/octet-stream")
	public HttpServletResponse downloadSketch(@QueryParam("type") String type,
											  @Context HttpServletResponse response)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();
		String a = FireAlarmConstants.DEVICE_TYPE;
		String fileName = "README.txt";

		/*  first, get and initialize an engine  */
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		/*  next, get the Template  */
		Template t = ve.getTemplate("repository" + File.separator +
											"resources" + File.separator + fileName);
		/*  create a context and add data */
		VelocityContext context = new VelocityContext();
		context.put("name", "World");

		/* now render the template into a StringWriter */
		Writer writer = null;
		try {
			writer = response.getWriter();
			t.merge(context, writer);
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName +
					"\"");
			writer.flush();
			return response;
		} catch (ResourceNotFoundException ex) {
			String msg = "Resource `" + fileName + "` not found";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		} catch (ParseErrorException ex) {
			String msg = "Resource parsing error for `" + fileName + "`";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		} catch (IOException ex) {
			String msg = "Error occurred while reading `" + fileName + "`";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		} catch (RuntimeException ex) {
			String msg = "Resource merging error for `" + fileName + "`";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		}
	}

}
