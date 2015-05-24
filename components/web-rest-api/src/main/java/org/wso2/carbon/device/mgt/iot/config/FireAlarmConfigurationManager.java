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

package org.wso2.carbon.device.mgt.iot.config;

import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.util.IotDeviceManagementUtil;
import org.wso2.carbon.device.mgt.iot.utils.ResourceFileLoader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the iot device manager configuration initialization.
 */
public class FireAlarmConfigurationManager {

	private static final String CONFIGS_FILE_LOCATION = "/resources/conf/firealarm-config.xml";
	private FireAlarmManagementConfig currentFireAlarmMgtConfig;
	private static FireAlarmConfigurationManager fireAlarmDeviceConfigManager;

	public static FireAlarmConfigurationManager getInstance() throws DeviceManagementException{
		if (fireAlarmDeviceConfigManager == null) {
			synchronized (FireAlarmConfigurationManager.class) {
				if (fireAlarmDeviceConfigManager == null) {
					fireAlarmDeviceConfigManager = new FireAlarmConfigurationManager();
					fireAlarmDeviceConfigManager.initConfig();
				}
			}
		}
		return fireAlarmDeviceConfigManager;
	}

	private void initConfig() throws DeviceManagementException {
		try {
			File fireAlarmMgtConfig = new ResourceFileLoader(CONFIGS_FILE_LOCATION).getFile();
			Document doc = IotDeviceManagementUtil.convertToDocument(fireAlarmMgtConfig);
			JAXBContext fireAlarmMgtContext = JAXBContext.newInstance(
					FireAlarmManagementConfig.class);
			Unmarshaller unmarshaller = fireAlarmMgtContext.createUnmarshaller();
			this.currentFireAlarmMgtConfig = (FireAlarmManagementConfig) unmarshaller.unmarshal(doc);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while initializing Fire Alarm Device Management config", e);
		}
	}

	public FireAlarmManagementConfig getFireAlarmMgtConfig() {
		return currentFireAlarmMgtConfig;
	}

}
