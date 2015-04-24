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

package org.wso2.iot.utils;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.ThrowsAdvice;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.enroll.UserManagement;

/**
 * @author ayyoobhamza
 * 
 */
public class IOTConfiguration {
	private static Log log = LogFactory.getLog(IOTConfiguration.class);
	private static IOTConfiguration iotInstance = null;

	// configuration variables
	private Class<?> userManagement;
	private Class<?> deviceManagement;

	
    private IOTConfiguration() throws ConfigurationException {
		String fileName = "";
		String enrollClassName = "";
		try {

			fileName = new ResourceFileLoader("/resources/conf/configuration.xml").getPath();
			//log.info(fileName);
//			 fileName =
//			 "/Users/ayyoobhamza/wso2/Iot Git/device-cloud/WSO2ConnectedDevices/src/main/webapp/resources/conf/configuration.xml";

			XMLConfiguration config = new XMLConfiguration(fileName);
			config.setExpressionEngine(new XPathExpressionEngine());

			// read all configurations

			String className = config.getString("main/enroll/device-class-name");
			enrollClassName =
			                  config.getString("device-enroll-endpoint/class[@name='" + className +
			                                   "']");
			
			
			
			deviceManagement = IOTConfiguration.class.forName(enrollClassName);

			className = config.getString("main/enroll/user-class-name");
			enrollClassName =
			                  config.getString("user-enroll-endpoint/class[@name='" + className +
			                                   "']");
			//log.info(enrollClassName);
			userManagement = IOTConfiguration.class.forName(enrollClassName);
			//log.info(userManagement);;
		} catch (ConfigurationException cex) {
			log.error("Configuration File is missing on path: " + fileName, cex);
			throw cex;
		} catch (ClassNotFoundException e) {
			log.error("Invalid Class Name: " + enrollClassName + "  :" + e);
			throw new ConfigurationException("Invalid className: " + enrollClassName, e);
		}

	}

	public static IOTConfiguration getInstance() throws ConfigurationException {

		if (iotInstance == null) {
			synchronized (IOTConfiguration.class) {
				if (iotInstance == null) {
					iotInstance = new IOTConfiguration();
				}
			}
		}
		return iotInstance;
	}

	public UserManagement getUserManagementImpl() throws InstantiationException,
	                                             IllegalAccessException {

		if (UserManagement.class.isAssignableFrom(userManagement)) {
			return (UserManagement) userManagement.newInstance();
		}

		String error =
		               "Invalid class format for usermanagement, Make sure it has implemented UserManagment Interface";
		log.error(error);
		throw new InstantiationException(error);

	}

	public DeviceManagement getDeviceManagementImpl() throws InstantiationException,
	                                                 IllegalAccessException {

		if (DeviceManagement.class.isAssignableFrom(deviceManagement)) {
			return (DeviceManagement) deviceManagement.newInstance();
		}

		String error =
		               "Invalid class format for usermanagement, Make sure it has implemented UserManagment Interface";
		log.error(error);
		throw new InstantiationException(error);

	}

//	public static void main(String args[]) throws ConfigurationException, InstantiationException,
//	                                      IllegalAccessException {
//		 UserManagement
//		user=IOTConfiguration.getInstance().getUserManagementImpl();
//		//IOTConfiguration.getInstance().getUserManagementImpl();
//	}

}
