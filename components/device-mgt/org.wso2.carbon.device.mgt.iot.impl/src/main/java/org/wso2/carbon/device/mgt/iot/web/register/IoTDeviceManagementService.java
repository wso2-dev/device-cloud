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

package org.wso2.carbon.device.mgt.iot.web.register;

import java.util.Date;
import java.util.List;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.iot.web.register.util.DeviceManagement;


public class IoTDeviceManagementService {

	public boolean deviceEnroll(String deviceId, String type, String name, String owner)
	                                                                                    throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(type);

		if (deviceManagement.isExist(deviceIdentifier)) {

			return false;

		}
		Device device = new Device();
		device.setDeviceIdentifier(deviceId);

		device.setDateOfEnrolment(new Date().getTime());
		device.setDateOfLastUpdate(new Date().getTime());
		device.setStatus(true);

		device.setName(name);
		device.setType(type);
		device.setDeviceTypeId(1);
		device.setOwner(owner);
		return true;

	}

	public boolean removeDevice(String deviceId, String type) throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(type);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		if (device == null) {

			return false;
		}

		return deviceManagement.removeDevice(deviceIdentifier);

	}

	public boolean updateDevice(String deviceId, String name, String type)
	                                                                      throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(type);

		Device device = deviceManagement.getDevice(deviceIdentifier);
		if (device == null) {

			return false;
		}

		device.setDeviceIdentifier(deviceId);

		// device.setDeviceTypeId(deviceTypeId);
		device.setDateOfLastUpdate(new Date().getTime());

		device.setName(name);
		device.setType(type);

		return deviceManagement.update(device);

	}

	public Device getDevice(String deviceId, String type) throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(deviceId);
		deviceIdentifier.setType(type);

		return deviceManagement.getDevice(deviceIdentifier);

	}

	public List<Device> getAllDevice(String username) throws DeviceManagementException {

		DeviceManagement deviceManagement =new DeviceManagement();

		List<Device> devices = deviceManagement.getDevices(username);
		return devices;

	}
	
	public List<DeviceType> getDeviceTypes() throws DeviceManagementDAOException{
		return new DeviceManagement().getDeviceTypes();
		
	}
}
