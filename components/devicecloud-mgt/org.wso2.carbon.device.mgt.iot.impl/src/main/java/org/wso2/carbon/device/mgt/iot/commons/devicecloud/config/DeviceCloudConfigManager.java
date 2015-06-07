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

package org.wso2.carbon.device.mgt.iot.commons.devicecloud.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.iot.commons.devicecloud.exception.DeviceControllerException;
import org.wso2.carbon.device.mgt.iot.commons.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.utils.CarbonUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the iot device manager configuration initialization.
 */
public class DeviceCloudConfigManager {



    private static final Log log = LogFactory.getLog(DeviceCloudConfigManager.class);

    private static final String IOT_DEVICE_CONFIG_XML_NAME = "devicecloud-config.xml";
	private static final String IOT_DC_ROOT_DIRECTORY = "iot";
	private final String CONFIGS_FILE_LOCATION =
			CarbonUtils.getCarbonConfigDirPath() + File.separator +
                    IOT_DC_ROOT_DIRECTORY + File.separator + IOT_DEVICE_CONFIG_XML_NAME;

    private DeviceCloudManagementConfig currentDeviceCloudMgtConfig;
    private static DeviceCloudConfigManager deviceConfigurationManager;

    private DeviceCloudConfigManager() {
    }

    public static DeviceCloudConfigManager getInstance() throws DeviceControllerException {
        if (deviceConfigurationManager == null) {
            synchronized (DeviceCloudConfigManager.class) {
                if (deviceConfigurationManager == null) {
                    DeviceCloudConfigManager result = new DeviceCloudConfigManager();
                    result.initConfig();
                    deviceConfigurationManager = result;
                }
            }

        }
        return deviceConfigurationManager;
    }

    private void initConfig() throws DeviceControllerException {
        try {
            File deviceCloudMgtConfig = new File(CONFIGS_FILE_LOCATION);
            Document doc = IotDeviceManagementUtil.convertToDocument(deviceCloudMgtConfig);
            JAXBContext deviceCloudMgtContext = JAXBContext.newInstance(DeviceCloudManagementConfig.class);
            Unmarshaller unmarshaller = deviceCloudMgtContext.createUnmarshaller();
            this.currentDeviceCloudMgtConfig = (DeviceCloudManagementConfig) unmarshaller.unmarshal(doc);
        } catch (Exception e) {
            String error = "Error occurred while initializing DeviceController configurations";
            log.error(error);
            throw new DeviceControllerException(error, e);
        }
    }

    public DeviceCloudManagementConfig getDeviceCloudMgtConfig() {
        return currentDeviceCloudMgtConfig;
    }

}
