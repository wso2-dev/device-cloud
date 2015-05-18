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
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Feature;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.iot.dto.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.*;

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
			throw new DeviceManagementException("Error occurred while parsing file, while converting " +
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
                for (Map.Entry<String, String> deviceProperty : iotDevice.getDeviceProperties().entrySet()) {
                    propertyList.add(getProperty(deviceProperty.getKey(), deviceProperty.getValue()));
                }
            }

            device.setProperties(propertyList);
			device.setDeviceIdentifier(iotDevice.getIotDeviceId());
		}
		return device;
	}



	

    
}
