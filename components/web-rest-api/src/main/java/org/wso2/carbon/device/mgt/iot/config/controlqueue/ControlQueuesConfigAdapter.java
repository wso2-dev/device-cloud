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

package org.wso2.carbon.device.mgt.iot.config.controlqueue;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlQueuesConfigAdapter extends
		XmlAdapter<FireAlarmControlQueueConfigurations, Map<String, FireAlarmControlQueueConfig>> {

	@Override
	public Map<String, FireAlarmControlQueueConfig> unmarshal(
			FireAlarmControlQueueConfigurations fireAlarmControlQueueConfigurations) throws Exception {

		Map<String, FireAlarmControlQueueConfig> fireAlarmControlQueueConfigMap
				= new HashMap<String, FireAlarmControlQueueConfig>();

		for (FireAlarmControlQueueConfig iotControlQueueConfig : fireAlarmControlQueueConfigurations
				.getFireAlarmControlQueueConfigs()) {
			fireAlarmControlQueueConfigMap.put(iotControlQueueConfig.getType(), iotControlQueueConfig);
		}

		return fireAlarmControlQueueConfigMap;
	}

	@Override
	public FireAlarmControlQueueConfigurations marshal(
			Map<String, FireAlarmControlQueueConfig> fireAlarmControlQueueConfigMap) throws Exception {

		FireAlarmControlQueueConfigurations fireAlarmControlQueueConfigurations
				= new FireAlarmControlQueueConfigurations();

		fireAlarmControlQueueConfigurations.setFireAlarmControlQueueConfigs(
				(List<FireAlarmControlQueueConfig>) fireAlarmControlQueueConfigMap.values());

		return fireAlarmControlQueueConfigurations;
	}
}
