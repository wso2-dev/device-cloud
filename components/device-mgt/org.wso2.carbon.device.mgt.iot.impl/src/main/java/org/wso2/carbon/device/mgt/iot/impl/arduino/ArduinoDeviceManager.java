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

package org.wso2.carbon.device.mgt.iot.impl.arduino;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.spi.DeviceManager;
import org.wso2.carbon.device.mgt.iot.common.IoTDeviceManagementConstants;
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.iot.dto.IotDevice;
import org.wso2.carbon.device.mgt.iot.impl.arduino.dao.ArduinoDAOFactory;
import org.wso2.carbon.device.mgt.iot.util.IotDeviceManagementUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents the Arduino implementation of DeviceManagerService.
 */
public class ArduinoDeviceManager implements DeviceManager {

    private IotDeviceManagementDAOFactory arduinoDeviceManagementDAOFactory;
    private static final Log log = LogFactory.getLog(ArduinoDeviceManager.class);

    public ArduinoDeviceManager() {
        arduinoDeviceManagementDAOFactory = new ArduinoDAOFactory();
    }

    @Override
    public String getProviderType() {
        return IoTDeviceManagementConstants.IotDeviceTypes.IOT_DEVICE_TYPE_ARDUINO;
    }

    @Override
    public FeatureManager getFeatureManager() {
        return new ArduinoFeatureManager();
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        boolean status;
        IotDevice iotDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enrolling a new arduino device : " + device.getDeviceIdentifier());
            }
            status = arduinoDeviceManagementDAOFactory.getIotDeviceDAO().addIotDevice(
                    iotDevice);
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while enrolling the Arduino device : " +
                    device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        boolean status;
        IotDevice arduinoDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug("Modifying the Arduino device enrollment data");
            }
            status = arduinoDeviceManagementDAOFactory.getIotDeviceDAO()
                    .updateIotDevice(arduinoDevice);
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while updating the enrollment of the Arduino device : " +
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
                log.debug("Dis-enrolling Arduino device : " + deviceId);
            }
            status = arduinoDeviceManagementDAOFactory.getIotDeviceDAO()
                    .deleteIotDevice(deviceId.getId());
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while removing the Arduino device : " + deviceId.getId();
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
                log.debug("Checking the enrollment of Arduino device : " + deviceId.getId());
            }
            IotDevice arduinoDevice =
                    arduinoDeviceManagementDAOFactory.getIotDeviceDAO().getIotDevice(
                            deviceId.getId());
            if (arduinoDevice != null) {
                isEnrolled = true;
            }
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while checking the enrollment status of Arduino device : " +
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
                log.debug("Getting the details of Arduino device : " + deviceId.getId());
            }
            IotDevice iotDevice = arduinoDeviceManagementDAOFactory.getIotDeviceDAO().
                    getIotDevice(deviceId.getId());
            device = IotDeviceManagementUtil.convertToDevice(iotDevice);
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while fetching the Arduino device : " + deviceId.getId();
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

    @Override
    public boolean updateDeviceInfo(Device device) throws DeviceManagementException {
        boolean status;
        IotDevice iotDevice = IotDeviceManagementUtil.convertToIotDevice(device);
        try {
            if (log.isDebugEnabled()) {
                log.debug(
                        "updating the details of Arduino device : " + device.getDeviceIdentifier());
            }
            status = arduinoDeviceManagementDAOFactory.getIotDeviceDAO()
                    .updateIotDevice(iotDevice);
        } catch (IotDeviceManagementDAOException e) {
            String msg =
                    "Error while updating the Arduino device : " + device.getDeviceIdentifier();
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
                log.debug("Fetching the details of all Arduino devices");
            }
            List<IotDevice> iotDevices =
                    arduinoDeviceManagementDAOFactory.getIotDeviceDAO().
                            getAllIotDevices();
            if (iotDevices != null) {
                devices = new ArrayList<Device>();
                for (IotDevice iotDevice : iotDevices) {
                    devices.add(IotDeviceManagementUtil.convertToDevice(iotDevice));
                }
            }
        } catch (IotDeviceManagementDAOException e) {
            String msg = "Error while fetching all Arduino devices.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return devices;
    }

}