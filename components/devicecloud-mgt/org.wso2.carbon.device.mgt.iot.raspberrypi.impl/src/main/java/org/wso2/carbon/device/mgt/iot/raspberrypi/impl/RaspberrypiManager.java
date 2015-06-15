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

package org.wso2.carbon.device.mgt.iot.raspberrypi.impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.device.mgt.iot.common.iotdevice.dao.IotDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.iot.common.iotdevice.dao.IotDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.iot.common.iotdevice.dto.IotDevice;
import org.wso2.carbon.device.mgt.iot.common.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.device.mgt.iot.raspberrypi.constants.RaspberrypiConstants;
import org.wso2.carbon.device.mgt.iot.raspberrypi.impl.dao.RaspberrypiDAO;

import java.util.ArrayList;
import java.util.List;


/**
 * This represents the Raspberrypi implementation of DeviceManagerService.
 */
public class RaspberrypiManager implements DeviceManagementService {

    private static final IotDeviceManagementDAOFactory iotDeviceManagementDAOFactory = new RaspberrypiDAO();
    private static final Log log = LogFactory.getLog(RaspberrypiManager.class);

    @Override
    public String getProviderType() {
        return RaspberrypiConstants.DEVICE_TYPE;
    }

    @Override
    public FeatureManager getFeatureManager() {
        return null;
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        boolean status;
        IotDevice iotDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enrolling a new Raspberrypi device : " + device.getDeviceIdentifier());
            }
            RaspberrypiDAO.beginTransaction();
            status = iotDeviceManagementDAOFactory.getIotDeviceDAO().addIotDevice(
                    iotDevice);
            RaspberrypiDAO.commitTransaction();
        } catch (IotDeviceManagementDAOException e) {
            try {
                RaspberrypiDAO.rollbackTransaction();
            } catch (IotDeviceManagementDAOException iotDAOEx) {
                String msg = "Error occurred while roll back the device enrol transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while enrolling the Raspberrypi device : " + device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        boolean status;
        IotDevice iotDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Modifying the Raspberrypi device enrollment data");
            }
            RaspberrypiDAO.beginTransaction();
            status = iotDeviceManagementDAOFactory.getIotDeviceDAO()
                    .updateIotDevice(iotDevice);
            RaspberrypiDAO.commitTransaction();
        } catch (IotDeviceManagementDAOException e) {
            try {
                RaspberrypiDAO.rollbackTransaction();
            } catch (IotDeviceManagementDAOException iotDAOEx) {
                String msg = "Error occurred while roll back the update device transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while updating the enrollment of the Raspberrypi device : " +
                    device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Dis-enrolling Raspberrypi device : " + deviceId);
            }
            RaspberrypiDAO.beginTransaction();
            status = iotDeviceManagementDAOFactory.getIotDeviceDAO()
                    .deleteIotDevice(deviceId.getId());
            RaspberrypiDAO.commitTransaction();
        } catch (IotDeviceManagementDAOException e) {
            try {
                RaspberrypiDAO.rollbackTransaction();
            } catch (IotDeviceManagementDAOException iotDAOEx) {
                String msg = "Error occurred while roll back the device dis enrol transaction :" + deviceId.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while removing the Raspberrypi device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean isEnrolled = false;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Checking the enrollment of Raspberrypi device : " + deviceId.getId());
            }
            IotDevice iotDevice =
                    iotDeviceManagementDAOFactory.getIotDeviceDAO().getIotDevice(
                            deviceId.getId());
            if (iotDevice != null) {
                isEnrolled = true;
            }
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while checking the enrollment status of Raspberrypi device : " +
                    deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return isEnrolled;
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean setActive(DeviceIdentifier deviceId, boolean status)
            throws DeviceManagementException {
        return true;
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        Device device;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting the details of Raspberrypi device : " + deviceId.getId());
            }
            IotDevice iotDevice = iotDeviceManagementDAOFactory.getIotDeviceDAO().
                    getIotDevice(deviceId.getId());
            device = IotDeviceManagementUtil.convertToDevice(iotDevice);
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while fetching the Raspberrypi device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return device;
    }

    @Override
    public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
            throws DeviceManagementException {
        return true;
    }

    public boolean isClaimable(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean updateDeviceInfo(DeviceIdentifier deviceIdentifier, Device device) throws DeviceManagementException {
        boolean status;
        IotDevice iotDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "updating the details of Raspberrypi device : " + deviceIdentifier);
            }
            RaspberrypiDAO.beginTransaction();
            status = iotDeviceManagementDAOFactory.getIotDeviceDAO()
                    .updateIotDevice(iotDevice);
            RaspberrypiDAO.commitTransaction();
        } catch (IotDeviceManagementDAOException e) {
            try {
                RaspberrypiDAO.rollbackTransaction();
            } catch (IotDeviceManagementDAOException iotDAOEx) {
                String msg = "Error occurred while roll back the update device info transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg =
                    "Error while updating the Raspberrypi device : " + deviceIdentifier;
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public List<Device> getAllDevices() throws DeviceManagementException {
        List<Device> devices = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Fetching the details of all Raspberrypi devices");
            }
            List<IotDevice> iotDevices =
                    iotDeviceManagementDAOFactory.getIotDeviceDAO().getAllIotDevices();
            if (iotDevices != null) {
                devices = new ArrayList<Device>();
                for (IotDevice iotDevice : iotDevices) {
                    devices.add(IotDeviceManagementUtil.convertToDevice(iotDevice));
                }
            }
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while fetching all Raspberrypi devices.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return devices;
    }

    @Override
    public Application[] getApplications(String s, int i, int i1)
            throws ApplicationManagementException {
        return new Application[0];
    }

    @Override
    public void updateApplicationStatus(DeviceIdentifier deviceIdentifier, Application application,
                                        String s) throws ApplicationManagementException {

    }

    @Override
    public String getApplicationStatus(DeviceIdentifier deviceIdentifier, Application application)
            throws ApplicationManagementException {
        return null;
    }

    @Override
    public void installApplication(Operation operation, List<DeviceIdentifier> list)
            throws ApplicationManagementException {

    }

    
}