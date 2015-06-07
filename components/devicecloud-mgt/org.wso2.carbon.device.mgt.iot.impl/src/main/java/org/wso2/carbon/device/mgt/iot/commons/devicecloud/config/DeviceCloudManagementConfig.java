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

import org.wso2.carbon.device.mgt.iot.commons.devicecloud.config.controlqueue.ControlQueuesConfigAdapter;
import org.wso2.carbon.device.mgt.iot.commons.devicecloud.config.controlqueue.DeviceControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.commons.devicecloud.config.datastore.DataStoresConfigAdapter;
import org.wso2.carbon.device.mgt.iot.commons.devicecloud.config.datastore.DeviceDataStoreConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * Represents Iot Device Mgt configuration.
 */
@XmlRootElement(name = "DeviceCloudMgtConfiguration")
public final class DeviceCloudManagementConfig {

	private DeviceCloudManagementControllerConfig deviceCloudManagementControllerConfig;
	private Map<String, DeviceDataStoreConfig> deviceCloudMgtDeviceDataStoreConfigMap;
	private Map<String, DeviceControlQueueConfig> deviceCloudControlQueueConfigMap;
	private DeviceCloudManagementSecurityConfig deviceCloudManagementSecurityConfig;

	@XmlElement(name = "DeviceController", nillable = false)
	public DeviceCloudManagementControllerConfig getDeviceCloudManagementControllerConfig() {
		return deviceCloudManagementControllerConfig;
	}

	public void setDeviceCloudManagementControllerConfig(
			DeviceCloudManagementControllerConfig deviceCloudMgtDeviceDataStore) {
		this.deviceCloudManagementControllerConfig = deviceCloudMgtDeviceDataStore;
	}

	@XmlElement(name = "DataStores", nillable = false)
	@XmlJavaTypeAdapter(DataStoresConfigAdapter.class)
	public Map<String, DeviceDataStoreConfig> getDataStoresMap() {
		return deviceCloudMgtDeviceDataStoreConfigMap;
	}

	public void setDataStoresMap(
			Map<String, DeviceDataStoreConfig> deviceCloudMgtDeviceDataStoreConfigMap) {
		this.deviceCloudMgtDeviceDataStoreConfigMap = deviceCloudMgtDeviceDataStoreConfigMap;
	}

	@XmlElement(name = "ControlQueues", nillable = false)
	@XmlJavaTypeAdapter(ControlQueuesConfigAdapter.class)
	public Map<String, DeviceControlQueueConfig> getControlQueuesMap() {
		return deviceCloudControlQueueConfigMap;
	}

	public void setControlQueuesMap(
			Map<String, DeviceControlQueueConfig> deviceCloudControlQueueConfigMap) {
		this.deviceCloudControlQueueConfigMap = deviceCloudControlQueueConfigMap;
	}

	@XmlElement(name = "Security", nillable = false)
	public DeviceCloudManagementSecurityConfig getDeviceCloudManagementSecurityConfig() {
		return deviceCloudManagementSecurityConfig;
	}

	public void setDeviceCloudManagementSecurityConfig(DeviceCloudManagementSecurityConfig deviceCloudManagementSecurityConfig) {
		this.deviceCloudManagementSecurityConfig = deviceCloudManagementSecurityConfig;
	}

}
