/*
c * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.iot.common.devicecloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.group.core.service.GroupManagementService;
import org.wso2.carbon.device.mgt.group.core.service.GroupManagementServiceImpl;
import org.wso2.carbon.device.mgt.iot.common.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementServiceImpl;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.util.ZipArchive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DeviceManagement {

	private static Log log = LogFactory.getLog(DeviceManagement.class);

	public boolean addNewDevice(Device device) throws DeviceManagementException  {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		boolean status = dmService.enrollDevice(device);
		return status;

	}

	public boolean removeDevice(DeviceIdentifier deviceIdentifier)
			throws DeviceManagementException {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();

		boolean status = dmService.disenrollDevice(deviceIdentifier);
		return status;
	}

	public boolean update(Device device) throws DeviceManagementException {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		boolean status = dmService.modifyEnrollment(device);

		return status;
	}

	public Device getDevice(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		return dmService.getDevice(deviceIdentifier);

	}

	public boolean isExist(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();

		return dmService.isEnrolled(deviceIdentifier);

	}

	public boolean isExist(String owner, DeviceIdentifier deviceIdentifier)
			throws DeviceManagementException {

		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		if (dmService.isEnrolled(deviceIdentifier)) {
			Device device=dmService.getDevice(deviceIdentifier);

				if (device.getOwner().equals(owner)) {
					return true;

				}

		}

		return false;
	}

	public List<Device> getDevices(String user) throws DeviceManagementException {
		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		return dmService.getAllDevicesOfUser(user);

	}

	public List<Device> getDevices(int groupId) throws DeviceManagementException {
		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		throw new DeviceManagementException("Method not implemented", new Exception());
	}

	public List<Device> getDevicesByType(String deviceType) throws DeviceManagementException{
		DeviceManagementService dmService = new DeviceManagementServiceImpl();
		return dmService.getAllDevices(deviceType);
	}

	public List<DeviceType> getDeviceTypes() throws DeviceManagementDAOException {

		return DeviceManagementDAOFactory.getDeviceTypeDAO().getDeviceTypes();

	}

	public ZipArchive getSketchArchive(String archivesPath, String templateSketchPath, Map contextParams)
			throws DeviceManagementException {
		/*  create a context and add data */

		try {
			return IotDeviceManagementUtil.getSketchArchive(archivesPath, templateSketchPath,
															contextParams);
		} catch (IOException e) {
			throw new DeviceManagementException("Zip File Creation Failed",e);
		}
	}

	public void addDeviceGroup(Group group) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        groupManagementService.createGroup(group);
	}

	public void removeDeviceGroup(int groupId) throws GroupManagementException {
		GroupManagementService groupManagementService = new GroupManagementServiceImpl();
		groupManagementService.deleteGroup(groupId);
	}

}
