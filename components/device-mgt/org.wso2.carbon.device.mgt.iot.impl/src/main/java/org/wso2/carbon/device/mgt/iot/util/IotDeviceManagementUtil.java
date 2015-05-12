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
	private static final String IOT_DEVICE_VENDOR = "vendor";
	private static final String IOT_DEVICE_MODEL = "model";
	private static final String IOT_DEVICE_SERIAL = "serial";

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

	private static String getPropertyValue(Device device, String property) {
		for (Device.Property prop : device.getProperties()) {
			if (property.equals(prop.getName())) {
				return prop.getValue();
			}
		}
		return null;
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
			
			iotDevice.setModel(getPropertyValue(device, IOT_DEVICE_MODEL));
			
			iotDevice.setVendor(getPropertyValue(device, IOT_DEVICE_VENDOR));
			

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

	public static Device convertToDevice(IotDevice mobileDevice) {
		Device device = null;
		if (mobileDevice != null) {
			device = new Device();
			List<Device.Property> propertyList = new ArrayList<Device.Property>();
			
			propertyList.add(getProperty(IOT_DEVICE_MODEL, mobileDevice.getModel()));
			
			propertyList.add(getProperty(IOT_DEVICE_VENDOR, mobileDevice.getVendor()));
			
			propertyList.add(getProperty(IOT_DEVICE_SERIAL, mobileDevice.getSerial()));

            if (mobileDevice.getDeviceProperties() != null) {
                for (Map.Entry<String, String> deviceProperty : mobileDevice.getDeviceProperties().entrySet()) {
                    propertyList.add(getProperty(deviceProperty.getKey(), deviceProperty.getValue()));
                }
            }

            device.setProperties(propertyList);
			device.setDeviceIdentifier(mobileDevice.getIotDeviceId());
		}
		return device;
	}

	public static IotOperation convertToMobileOperation(Operation operation) {
		IotOperation iotOperation = new IotOperation();
		IotOperationProperty operationProperty;
		List<IotOperationProperty> properties = new LinkedList<IotOperationProperty>();
		iotOperation.setFeatureCode(operation.getCode());
		iotOperation.setCreatedDate(new Date().getTime());
		Properties operationProperties = operation.getProperties();
		for (String key : operationProperties.stringPropertyNames()) {
			operationProperty = new IotOperationProperty();
			operationProperty.setProperty(key);
			operationProperty.setValue(operationProperties.getProperty(key));
			properties.add(operationProperty);
		}
		iotOperation.setProperties(properties);
		return iotOperation;
	}

	public static List<Integer> getIotOperationIdsFromMobileDeviceOperations(
			List<IotDeviceOperationMapping> mobileDeviceOperationMappings) {
		List<Integer> mobileOperationIds = new ArrayList<Integer>();
		for (IotDeviceOperationMapping mobileDeviceOperationMapping : mobileDeviceOperationMappings) {
			mobileOperationIds.add(mobileDeviceOperationMapping.getOperationId());
		}
		return mobileOperationIds;
	}

	public static Operation convertIotOperationToOperation(IotOperation iotOperation) {
		Operation operation = new Operation();
		Properties properties = new Properties();
		operation.setCode(iotOperation.getFeatureCode());
		for (IotOperationProperty iotOperationProperty : iotOperation.getProperties()) {
			properties.put(iotOperationProperty.getProperty(), iotOperationProperty.getValue());
		}
		operation.setProperties(properties);
		return operation;
	}

    
}
