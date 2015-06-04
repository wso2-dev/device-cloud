/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.iot.iotdevice.config;

import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the iot device manager configuration initialization.
 */
public class IotDeviceConfigurationManager {

	private static final String IOT_DEVICE_CONFIG_XML_NAME = "iot-config.xml";
	private static final String IOT_DC_ROOT_DIRECTORY = "iot";
	private IotDeviceManagementConfig currentIoTDeviceConfig;
	private static IotDeviceConfigurationManager iotDeviceConfigManager;

	private final String iotDeviceMgtConfigXMLPath = CarbonUtils.getCarbonConfigDirPath()
			+ File.separator +
			IOT_DC_ROOT_DIRECTORY + File.separator + IOT_DEVICE_CONFIG_XML_NAME;

	public static IotDeviceConfigurationManager getInstance() {
		if (iotDeviceConfigManager == null) {
			synchronized (IotDeviceConfigurationManager.class) {
				if (iotDeviceConfigManager == null) {
					iotDeviceConfigManager = new IotDeviceConfigurationManager();
				}
			}
		}
		return iotDeviceConfigManager;
	}

	public synchronized void initConfig() throws DeviceManagementException {
		try {
			File iotDeviceMgtConfig = new File(iotDeviceMgtConfigXMLPath);
			Document doc = IotDeviceManagementUtil.convertToDocument(iotDeviceMgtConfig);
			JAXBContext iotDeviceMgmtContext = JAXBContext.newInstance(
					IotDeviceManagementConfig.class);
			Unmarshaller unmarshaller = iotDeviceMgmtContext.createUnmarshaller();
			this.currentIoTDeviceConfig = (IotDeviceManagementConfig) unmarshaller.unmarshal(doc);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while initializing iot Device Management config", e);
		}
	}

	public IotDeviceManagementConfig getIotDeviceManagementConfig() {
		return currentIoTDeviceConfig;
	}

}
