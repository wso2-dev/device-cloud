/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.dto.IotDevice;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Provides utility methods required by the mobile device management bundle.
 */
public class IotDeviceManagementUtil {

	private static final Log log = LogFactory.getLog(IotDeviceManagementUtil.class);

	public static Document convertToDocument(File file) throws DeviceManagementException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			return docBuilder.parse(file);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while parsing file, while converting " +
							"to a org.w3c.dom.Document : " + e.getMessage(), e);
		}
	}

	private static Device.Property getProperty(String property, String value) {
		if (property != null) {
			Device.Property prop = new Device.Property();
			prop.setName(property);
			prop.setValue(value);
			return prop;
		}
		return null;
	}

	public static IotDevice convertToIotDevice(Device device) {
		IotDevice iotDevice = null;
		if (device != null) {
			iotDevice = new IotDevice();

			if (device.getProperties() != null) {
				Map<String, String> deviceProperties = new HashMap<String, String>();
				for (Device.Property deviceProperty : device.getProperties()) {
					deviceProperties.put(deviceProperty.getName(), deviceProperty.getValue());
				}

				iotDevice.setDeviceProperties(deviceProperties);
			} else {
				iotDevice.setDeviceProperties(new HashMap<String, String>());
			}
		}
		return iotDevice;
	}

	public static Device convertToDevice(IotDevice iotDevice) {
		Device device = null;
		if (iotDevice != null) {
			device = new Device();
			List<Device.Property> propertyList = new ArrayList<Device.Property>();

			if (iotDevice.getDeviceProperties() != null) {
				for (Map.Entry<String, String> deviceProperty : iotDevice.getDeviceProperties()
						.entrySet()) {
					propertyList.add(getProperty(deviceProperty.getKey(),
												 deviceProperty.getValue()));
				}
			}

			device.setProperties(propertyList);
			device.setDeviceIdentifier(iotDevice.getIotDeviceId());
		}
		return device;
	}

	public static File getSketchArchive(String archivesPath, String templateSketchPath,
										Map contextParams) throws DeviceManagementException {
		/*  create a context and add data */
		VelocityContext context = new VelocityContext(contextParams);

		String sep = File.separator;
		String sketchPath = CarbonUtils.getCarbonHome() + sep + templateSketchPath;
		//
		deleteDir(new File(archivesPath));//clear directory
		deleteDir(new File(archivesPath + ".zip"));//clear zip
		new File(archivesPath).mkdirs();//new dir

		try {
			parseTemplate(templateSketchPath + sep + "FireAlarmAgent.h",
						  archivesPath + sep + "FireAlarmAgent.h", context);
			copyFile(sketchPath + sep + "Connect.ino", archivesPath + sep + "Connect.ino");
			copyFile(sketchPath + sep + "FireAlarmAgent.ino",
					 archivesPath + sep + "FireAlarmAgent.ino");
			copyFile(sketchPath + sep + "MQTTConnect.ino", archivesPath + sep + "MQTTConnect.ino");
			copyFile(sketchPath + sep + "PushData.ino", archivesPath + sep + "PushData.ino");
			createZipArchive(archivesPath);
			deleteDir(new File(archivesPath));//clear folder
		} catch (IOException ex) {
			String msg = "Error occurred while creating archive file";
			log.error(msg);
			throw new DeviceManagementException(msg, ex);
		}
		/* now get the zip file */
		File zip = new File(archivesPath + ".zip");
		return zip;
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

	private static boolean createZipArchive(String srcFolder) {
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
