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

package org.wso2.carbon.device.mgt.iot.devicecloud.config.datastore;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStoresConfigAdapter extends
		XmlAdapter<DeviceDataStoreConfigurations, Map<String, DeviceDataStoreConfig>> {

	@Override
	public Map<String, DeviceDataStoreConfig> unmarshal(
			DeviceDataStoreConfigurations deviceDataStoreConfigurations) throws Exception {

		Map<String, DeviceDataStoreConfig> fireAlarmDataStoreConfigMap
				= new HashMap<String, DeviceDataStoreConfig>();

		for (DeviceDataStoreConfig iotDataSourceConfig : deviceDataStoreConfigurations
				.getIotDataSourceConfigs()) {
			fireAlarmDataStoreConfigMap.put(iotDataSourceConfig.getType(), iotDataSourceConfig);
		}

		return fireAlarmDataStoreConfigMap;
	}

	@Override
	public DeviceDataStoreConfigurations marshal(
			Map<String, DeviceDataStoreConfig> fireAlarmDataStoreConfigMap) throws Exception {

		DeviceDataStoreConfigurations deviceDataStoreConfigurations
				= new DeviceDataStoreConfigurations();

		deviceDataStoreConfigurations.setIotDataSourceConfigs(
				(List<DeviceDataStoreConfig>) fireAlarmDataStoreConfigMap.values());

		return deviceDataStoreConfigurations;
	}
}
