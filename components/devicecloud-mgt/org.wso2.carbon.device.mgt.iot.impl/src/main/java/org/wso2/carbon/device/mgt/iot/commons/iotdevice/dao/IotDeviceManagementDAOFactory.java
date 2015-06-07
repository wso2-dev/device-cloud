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

package org.wso2.carbon.device.mgt.iot.commons.iotdevice.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.commons.iotdevice.exception.IotDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.iot.commons.iotdevice.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.iot.commons.iotdevice.config.datasource.IotDataSourceConfig;
import org.wso2.carbon.device.mgt.iot.commons.iotdevice.dao.util.IotDeviceManagementDAOUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Factory class used to create IotDeviceManagement related DAO objects.
 */
public abstract class IotDeviceManagementDAOFactory implements IotDeviceManagementDAOFactoryInterface {

    private static final Log log = LogFactory.getLog(IotDeviceManagementDAOFactory.class);
    private static Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
    private static boolean isInitialized;

    public static void init(Map<String, IotDataSourceConfig> iotDataSourceConfigMap)
            throws IotDeviceMgtPluginException {
        DataSource dataSource;
        for (String pluginType : iotDataSourceConfigMap.keySet()) {
            if (dataSourceMap.get(pluginType) == null) {
                dataSource = IotDeviceManagementDAOFactory.resolveDataSource(iotDataSourceConfigMap.get
                        (pluginType));
                dataSourceMap.put(pluginType, dataSource);
            }
        }
        isInitialized = true;
    }

    public static void init(String key, IotDataSourceConfig iotDataSourceConfig) throws
            IotDeviceMgtPluginException {
        DataSource dataSource = IotDeviceManagementDAOFactory.resolveDataSource(iotDataSourceConfig);
        dataSourceMap.put(key, dataSource);
    }

    /**
     * Resolve data source from the data source definition.
     *
     * @param config Iot data source configuration
     * @return data source resolved from the data source definition
     */
    public static DataSource resolveDataSource(IotDataSourceConfig config) {
        DataSource dataSource = null;
        if (config == null) {
            throw new RuntimeException("Device Management Repository data source configuration " +
                    "is null and thus, is not initialized");
        }
        JNDILookupDefinition jndiConfig = config.getJndiLookupDefinition();
        if (jndiConfig != null) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing Device Management Repository data source using the JNDI " +
                        "Lookup Definition");
            }
            List<JNDILookupDefinition.JNDIProperty> jndiPropertyList =
                    jndiConfig.getJndiProperties();
            if (jndiPropertyList != null) {
                Hashtable<Object, Object> jndiProperties = new Hashtable<Object, Object>();
                for (JNDILookupDefinition.JNDIProperty prop : jndiPropertyList) {
                    jndiProperties.put(prop.getName(), prop.getValue());
                }
                dataSource =
                        IotDeviceManagementDAOUtil
                                .lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
            } else {
                dataSource = IotDeviceManagementDAOUtil
                        .lookupDataSource(jndiConfig.getJndiName(), null);
            }
        }
        return dataSource;
    }

    public static Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }
}