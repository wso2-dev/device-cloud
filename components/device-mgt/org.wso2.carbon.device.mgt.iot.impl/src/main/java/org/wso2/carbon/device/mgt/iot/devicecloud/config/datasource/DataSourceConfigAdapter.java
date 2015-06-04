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

package org.wso2.carbon.device.mgt.iot.devicecloud.config.datasource;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSourceConfigAdapter
        extends XmlAdapter<IotDataSourceConfigurations, Map<String, IotDataSourceConfig>> {

    @Override
    public Map<String, IotDataSourceConfig> unmarshal(IotDataSourceConfigurations iotDataSourceConfigurations)
            throws Exception {

        Map<String, IotDataSourceConfig> iotDataSourceConfigMap = new HashMap<String, IotDataSourceConfig>();
        for (IotDataSourceConfig iotDataSourceConfig : iotDataSourceConfigurations
                .getIotDataSourceConfigs()) {
            iotDataSourceConfigMap.put(iotDataSourceConfig.getType(), iotDataSourceConfig);
        }
        return iotDataSourceConfigMap;
    }

    @Override
    public IotDataSourceConfigurations marshal(Map<String, IotDataSourceConfig> iotDataSourceConfigMap)
            throws Exception {

        IotDataSourceConfigurations iotDataSourceConfigurations = new IotDataSourceConfigurations();
        iotDataSourceConfigurations.setIotDataSourceConfigs(
                (List<IotDataSourceConfig>) iotDataSourceConfigMap.values());

        return iotDataSourceConfigurations;
    }
}
