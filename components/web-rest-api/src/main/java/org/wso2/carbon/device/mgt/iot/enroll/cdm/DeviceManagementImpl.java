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

package org.wso2.carbon.device.mgt.iot.enroll.cdm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.iot.common.DeviceCloudException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.iot.enroll.DeviceManagement;
import org.wso2.carbon.device.mgt.iot.enroll.cdm.util.IotApiUtil;


public class DeviceManagementImpl implements DeviceManagement {
	private static Log log = LogFactory.getLog(DeviceManagementImpl.class);

	@Override
	public boolean addNewDevice(Device device) throws DeviceCloudException {
		DeviceManagementService dmService= IotApiUtil.getDeviceManagementService();
		
		boolean status;
        try {
	        status = dmService.enrollDevice(device);
	       // if(log.isDebugEnabled()){
	        log.info("device enroll: "+device.getDeviceIdentifier()+" "+status);
	        //}
        } catch (DeviceManagementException e) {
	        String error="CDM configuration error on device enrolling";
        	if(log.isDebugEnabled()){
	        	
	        	log.debug(error+ e);
	        }
        	throw new DeviceCloudException(error);
        }
		return status;

	}


	@Override
	public boolean removeDevice(DeviceIdentifier deviceIdentifier) throws DeviceCloudException {

		boolean status = false;
		try {
			DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();
			status = dmService.disenrollDevice(deviceIdentifier);
		} catch (DeviceManagementException e) {
			String error = "CDM Configuration error on device removal";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wso2.carbon.device.mgt.iot.enroll.DeviceManagement#update(org.wso2
	 * .carbon.device.mgt.iot.device.Device)
	 */
	@Override
	public boolean update(Device device) throws DeviceCloudException {

		boolean status = false;

		try {
			DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();
			status = dmService.updateDeviceInfo(device);
		} catch (DeviceManagementException e) {
			String error = "CDM Configuration Error on Update";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}

		return status;
	}


	@Override
	public Device getDevice(DeviceIdentifier deviceIdentifier) throws DeviceCloudException {

		DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();

		try {
			return dmService.getDevice(deviceIdentifier);
		} catch (DeviceManagementException e) {
			String error = "CDM Configuration error on device retreival ";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}

	}

	@Override
	public boolean isExist(DeviceIdentifier deviceIdentifier) throws DeviceCloudException {

		DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();

		try {
			return dmService.isEnrolled(deviceIdentifier);
		} catch (DeviceManagementException e) {
			String error = "CDM Configuration error on device retreival ";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}
	}


	@Override
	public boolean isExist(String owner, DeviceIdentifier deviceIdentifier) throws DeviceCloudException {

		DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();
		try {
			if (dmService.isEnrolled(deviceIdentifier)) {
				List<Device> deviceList = dmService.getDeviceListOfUser(owner);
				for (Device device : deviceList) {
					if (device.getDeviceIdentifier().equals(deviceIdentifier.getId())) {
						return true;

					}

				}

			}
		} catch (DeviceManagementException e) {
			String error = "DeviceManagementException on get device ";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}
		return false;
	}
	
	@Override
	public List<Device> getDevices(String user) throws DeviceCloudException{
		DeviceManagementService dmService = IotApiUtil.getDeviceManagementService();
		
		try {
			return dmService.getAllDevicesOfUser(user);
		} catch (DeviceManagementException e) {
			String error = "DeviceManagementException on get device ";
			if (log.isDebugEnabled()) {

				log.debug(error + e);
			}
			throw new DeviceCloudException(error);
		}
		
	}


	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.DeviceManagement#getDeviceTypes()
	 */
    @Override
    public List<String> getDeviceTypes() throws DeviceCloudException {
	   //temp solution have to get access to core deviceTypes.
    	ArrayList<String> list=new ArrayList<String>();
    	list.add("arduino");
    	list.add("firealarm");
	    return null;
    }
	

}
