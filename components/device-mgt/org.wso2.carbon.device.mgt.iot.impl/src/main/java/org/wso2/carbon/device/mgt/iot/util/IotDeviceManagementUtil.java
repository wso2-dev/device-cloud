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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants.FireAlarmConstants;
import org.wso2.carbon.device.mgt.iot.dto.IotDevice;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Provides utility methods required by the mobile device management bundle.
 */
public class IotDeviceManagementUtil {

	private static final Log log = LogFactory.getLog(IotDeviceManagementUtil.class.getName());

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
			iotDevice.setIotDeviceId(device.getDeviceIdentifier());
			Map<String, String> deviceProperties = new HashMap<String, String>();
			deviceProperties.put(FireAlarmConstants.DEVICE_PLUGIN_DEVICE_NAME,device.getName());

			if (device.getProperties() != null) {

				for (Device.Property deviceProperty : device.getProperties()) {
					deviceProperties.put(deviceProperty.getName(), deviceProperty.getValue());
				}

				iotDevice.setDeviceProperties(deviceProperties);
			} else {
				iotDevice.setDeviceProperties(deviceProperties);
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
			device.setName(iotDevice.getDeviceProperties().get(FireAlarmConstants.DEVICE_PLUGIN_DEVICE_NAME));
			device.setDeviceIdentifier(iotDevice.getIotDeviceId());
		}
		return device;
	}

	public static File getSketchArchive(String archivesPath, String templateSketchPath,
										Map contextParams) throws DeviceManagementException {

		String sep = File.separator;
		String sketchPath = CarbonUtils.getCarbonHome() + sep + templateSketchPath;
		//
		deleteDir(new File(archivesPath));//clear directory
		deleteDir(new File(archivesPath + ".zip"));//clear zip
		new File(archivesPath).mkdirs();//new dir

		try {
			List<String> templateFiles = getTemplates(sketchPath + sep + "sketch.properties");
			for (String templateFile : templateFiles) {
				parseTemplate(templateSketchPath + sep + templateFile,
							  archivesPath + sep + templateFile, contextParams);
			}
			copyFolder(new File(sketchPath), new File(archivesPath), templateFiles);

		} catch (IOException ex) {
			throw new DeviceManagementException(
					"Error occurred when trying to read property " + "file sketch.properties", ex);
		}

		createZipArchive(archivesPath);
		deleteDir(new File(archivesPath));//clear folder

		/* now get the zip file */
		File zip = new File(archivesPath + ".zip");
		return zip;
	}

	private static List getTemplates(String propertyFilePath) throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(propertyFilePath);

			// load a properties file
			prop.load(input);
			String templates = prop.getProperty("templates");
			List<String> list = new ArrayList<String>(Arrays.asList(templates.split(",")));
			return list;

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void parseTemplate(String srcFile, String dstFile, Map contextParams)
			throws IOException {
		//TODO add velocity 1.7, currently commented
		//TODO conflicting when calling in CXF environment with the opensaml orbit

		//		/*  create a context and add data */
		//		VelocityContext context = new VelocityContext(contextParams);
		//
		//		/*  first, get and initialize an engine  */
		//		VelocityEngine ve = new VelocityEngine();
		//		ve.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		//						"org.apache.velocity.runtime.log.Log4JLogChute" );
		//		ve.setProperty("runtime.log.logsystem.log4j.logger", IotDeviceManagementUtil.class.getName());
		//		ve.init();
		//
		//		String sep = File.separator;
		//		Template t = ve.getTemplate(srcFile);
		//		FileWriter writer = null;
		//		try {
		//			writer = new FileWriter(dstFile);
		//			t.merge(context, writer);
		//		} finally {
		//			if (writer != null) {
		//				writer.flush();
		//				writer.close();
		//			}
		//		}

		String encoding = "UTF-8";
		//read from file
		FileInputStream inputStream = new FileInputStream(srcFile);
		String content = IOUtils.toString(inputStream, encoding);
		Iterator iterator = contextParams.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			content = content.replaceAll("\\$\\{" + mapEntry.getKey() + "\\}",
										 mapEntry.getValue().toString());
		}
		if (inputStream != null) {
			inputStream.close();
		}
		//write to file
		FileOutputStream outputStream = new FileOutputStream(dstFile);
		IOUtils.write(content, outputStream, encoding);
		if (outputStream != null) {
			outputStream.close();
		}
	}

	private static void copyFolder(File src, File dest, List<String> excludeFileNames)
			throws IOException {

		if (src.isDirectory()) {

			//if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile, destFile, excludeFileNames);
			}

		} else {
			for (String fileName : excludeFileNames) {
				if (src.getName().equals(fileName)) {
					return;
				}
			}
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
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
