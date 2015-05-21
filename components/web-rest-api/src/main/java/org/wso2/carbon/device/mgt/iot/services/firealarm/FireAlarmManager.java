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
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;
import org.wso2.carbon.utils.CarbonUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	public Response downloadSketch(@QueryParam("owner") String owner,
								   @QueryParam("type") String type)
			throws DeviceManagementException {

		if (owner == null || type == null) {
			return Response.status(400).build();//bad request
		}

		DeviceManagement deviceManagement = new DeviceManagement();
		String a = FireAlarmConstants.DEVICE_TYPE;

		/*  create a context and add data */
		VelocityContext context = new VelocityContext();
		String deviceId = UUID.randomUUID().toString();//create new device id
		context.put("DEVICE_OWNER", owner);
		context.put("DEVICE_ID", deviceId);

		String sep = File.separator;
		String archivesPath = CarbonUtils.getCarbonHome() + sep + "repository" +
				sep + "resources" + sep + "sketches" + sep + "archives" + sep + deviceId;
		String tSketchPath = "repository" + sep + "resources" + sep + "sketches" + sep +
				type;
		String sketchPath = CarbonUtils.getCarbonHome() + sep + tSketchPath;
		//
		deleteDir(new File(archivesPath));//clear directory
		deleteDir(new File(archivesPath + ".zip"));//clear zip
		new File(archivesPath).mkdir();//new dir

		try {
			parseTemplate(tSketchPath + sep + "IoTArduinoAgent.h",
						  archivesPath + sep + "IoTArduinoAgent.h", context);
			copyFile(sketchPath + sep + "Connect.ino", archivesPath + sep + "Connect.ino");
			copyFile(sketchPath + sep + "IoTArduinoAgent.ino",
					 archivesPath + sep + "IoTArduinoAgent.ino");
			copyFile(sketchPath + sep + "MQTTConnect.ino", archivesPath + sep + "MQTTConnect.ino");
			copyFile(sketchPath + sep + "PushData.ino", archivesPath + sep + "PushData.ino");
			createZipArchive(archivesPath);
			deleteDir(new File(archivesPath));//clear folder
		} catch (IOException ex) {
			String msg = "Error occurred while creating archive file";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		}

		/* now output the zip file */
		File file = new File(archivesPath + ".zip");
		Response.ResponseBuilder rb = Response.ok((Object) file);
		rb.header("Content-Disposition", "attachment; filename=\"IoTArduinoAgent.zip\"");
		return rb.build();
	}

	private static void parseTemplate(String srcFile, String dstFile,
									  org.apache.velocity.context.Context context)
			throws IOException {
		/*  first, get and initialize an engine  */
		VelocityEngine ve = new VelocityEngine();
		ve.init();

		String sep = File.separator;
		Template t = ve.getTemplate(srcFile);
		FileWriter writer = null;
		try {
			writer = new FileWriter(dstFile);
			t.merge(context, writer);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private static void copyFile(String srcFile, String dstFile) throws IOException {
		File sourceFile = new File(srcFile);
		File destFile = new File(dstFile);

		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static boolean createZipArchive(String srcFolder) {
		try {
			final int BUFFER = 2048;
			BufferedInputStream origin = null;

			FileOutputStream dest = new FileOutputStream(new File(srcFolder + ".zip"));

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];

			File subDir = new File(srcFolder);
			String subdirList[] = subDir.list();
			for (String sd : subdirList) {
				// get a list of files from current directory
				File f = new File(srcFolder + "/" + sd);
				if (f.isDirectory()) {
					String files[] = f.list();

					for (int i = 0; i < files.length; i++) {
						FileInputStream fi = new FileInputStream(
								srcFolder + "/" + sd + "/" + files[i]);
						origin = new BufferedInputStream(fi, BUFFER);
						ZipEntry entry = new ZipEntry(sd + "/" + files[i]);
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0, BUFFER)) != -1) {
							out.write(data, 0, count);
							out.flush();
						}

					}
				} else //it is just a file
				{
					FileInputStream fi = new FileInputStream(f);
					origin = new BufferedInputStream(fi, BUFFER);
					ZipEntry entry = new ZipEntry(sd);
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER)) != -1) {
						out.write(data, 0, count);
						out.flush();
					}

				}
			}
			origin.close();
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("createZipArchive threw exception: " + e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty or this is a file so delete it
		return dir.delete();
	}

}
