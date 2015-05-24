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

import org.wso2.carbon.device.mgt.iot.config.controlqueue.ControlQueuesConfigAdapter;
import org.wso2.carbon.device.mgt.iot.config.controlqueue.FireAlarmControlQueueConfig;
import org.wso2.carbon.device.mgt.iot.config.datastore.DataStoresConfigAdapter;
import org.wso2.carbon.device.mgt.iot.config.datastore.FireAlarmDataStoreConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * Represents Iot Device Mgt configuration.
 */
@XmlRootElement(name = "FireAlarmDeviceMgtConfiguration")
public final class FireAlarmManagementConfig {

	private FireAlarmManagementControllerConfig fireAlarmManagementControllerConfig;
	private Map<String, FireAlarmDataStoreConfig> fireAlarmMgtDeviceDataStoreConfigMap;
	private Map<String, FireAlarmControlQueueConfig> fireAlarmControlQueueConfigMap;
	private FireAlarmManagementSecurityConfig fireAlarmManagementSecurityConfig;

	@XmlElement(name = "DeviceController", nillable = false)
	public FireAlarmManagementControllerConfig getFireAlarmManagementControllerConfig() {
		return fireAlarmManagementControllerConfig;
	}

	public void setFireAlarmManagementControllerConfig(FireAlarmManagementControllerConfig fireAlarmMgtDeviceDataStore) {
		this.fireAlarmManagementControllerConfig = fireAlarmMgtDeviceDataStore;
	}

	@XmlElement(name = "DataStores", nillable = false)
	@XmlJavaTypeAdapter(DataStoresConfigAdapter.class)
	public Map<String, FireAlarmDataStoreConfig> getDataStoresMap() {
		return fireAlarmMgtDeviceDataStoreConfigMap;
	}

	public void setDataStoresMap(
			Map<String, FireAlarmDataStoreConfig> fireAlarmMgtDeviceDataStoreConfigMap) {
		this.fireAlarmMgtDeviceDataStoreConfigMap = fireAlarmMgtDeviceDataStoreConfigMap;
	}

	@XmlElement(name = "ControlQueues", nillable = false)
	@XmlJavaTypeAdapter(ControlQueuesConfigAdapter.class)
	public Map<String, FireAlarmControlQueueConfig> getControlQueuesMap() {
		return fireAlarmControlQueueConfigMap;
	}

	public void setControlQueuesMap(
			Map<String, FireAlarmControlQueueConfig> fireAlarmControlQueueConfigMap) {
		this.fireAlarmControlQueueConfigMap = fireAlarmControlQueueConfigMap;
	}

	@XmlElement(name = "Security", nillable = false)
	public FireAlarmManagementSecurityConfig getFireAlarmManagementSecurityConfig() {
		return fireAlarmManagementSecurityConfig;
	}

	public void setFireAlarmManagementSecurityConfig(FireAlarmManagementSecurityConfig fireAlarmManagementSecurityConfig) {
		this.fireAlarmManagementSecurityConfig = fireAlarmManagementSecurityConfig;
	}

}
