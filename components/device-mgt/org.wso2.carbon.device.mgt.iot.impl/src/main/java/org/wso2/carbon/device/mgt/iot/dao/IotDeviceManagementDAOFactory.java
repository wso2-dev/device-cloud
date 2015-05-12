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

package org.wso2.carbon.device.mgt.iot.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.DataSourceNotAvailableException;
import org.wso2.carbon.device.mgt.iot.common.IotDeviceMgtPluginException;
import org.wso2.carbon.device.mgt.iot.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.iot.config.datasource.IotDataSourceConfig;
import org.wso2.carbon.device.mgt.iot.dao.util.IotDeviceManagementDAOUtil;


import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Factory class used to create IotDeviceManagement related DAO objects.
 */
public abstract class IotDeviceManagementDAOFactory implements IotDeviceManagementDAOFactoryInterface {

    private static final Log log = LogFactory.getLog(IotDeviceManagementDAOFactory.class);
    private static Map<String, IotDataSourceConfig> iotDataSourceConfigMap;
    private static Map<String, DataSource> dataSourceMap;
    private static boolean isInitialized;
    private static ThreadLocal<Connection> currentConnection = new ThreadLocal<Connection>();
    protected static DataSource dataSource;

    public static void init() throws IotDeviceMgtPluginException {

        dataSourceMap = new HashMap<String, DataSource>();
        DataSource dataSource;
        for (String pluginType : iotDataSourceConfigMap.keySet()) {
            dataSource = IotDeviceManagementDAOFactory.resolveDataSource(iotDataSourceConfigMap.get
                    (pluginType));
            dataSourceMap.put(pluginType, dataSource);
        }
        isInitialized = true;
    }

    /**
     * Resolve data source from the data source definition.
     *
     * @param config Iot data source configuration
     * @return data source resolved from the data source definition
     */
    protected static DataSource resolveDataSource(IotDataSourceConfig config) {
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

    public static Map<String, IotDataSourceConfig> getIotDataSourceConfigMap() {
        return iotDataSourceConfigMap;
    }

    public static void setIotDataSourceConfigMap(Map<String, IotDataSourceConfig> iotDataSourceConfigMap) {
        IotDeviceManagementDAOFactory.iotDataSourceConfigMap = iotDataSourceConfigMap;
    }

    public static DataSource getDataSource(String type) {
        return dataSourceMap.get(type);
    }

    public static Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    private static void assertDataSourceInitialization() {
        if (!isInitialized) {
            throw new DataSourceNotAvailableException("Iot device management metadata repository datasource " +
                    "is not initialized");
        }
    }

    public static void beginTransaction() throws IotDeviceManagementDAOException {
        try {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            currentConnection.set(conn);
        } catch (SQLException e) {
            throw new IotDeviceManagementDAOException("Error occurred while retrieving datasource connection", e);
        }
    }

    public static Connection getConnection() throws IotDeviceManagementDAOException {
        if (currentConnection.get() == null) {
            try {
                currentConnection.set(dataSource.getConnection());
            } catch (SQLException e) {
                throw new IotDeviceManagementDAOException("Error occurred while retrieving data source connection",
                        e);
            }
        }
        return currentConnection.get();
    }
    public static void commitTransaction() throws IotDeviceManagementDAOException {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.commit();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence commit " +
                            "has not been attempted");
                }
            }
        } catch (SQLException e) {
            throw new IotDeviceManagementDAOException("Error occurred while committing the transaction", e);
        } finally {
          closeConnection();
        }
    }

    public static void closeConnection() throws IotDeviceManagementDAOException {

        Connection con = currentConnection.get();
        try {
            con.close();
        } catch (SQLException e) {
            log.error("Error occurred while close the connection");
        }
        currentConnection.remove();
    }

    public static void rollbackTransaction() throws IotDeviceManagementDAOException {
        try {
            Connection conn = currentConnection.get();
            if (conn != null) {
                conn.rollback();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Datasource connection associated with the current thread is null, hence rollback " +
                            "has not been attempted");
                }
            }
        } catch (SQLException e) {
            throw new IotDeviceManagementDAOException("Error occurred while rollbacking the transaction", e);
        } finally {
            closeConnection();
        }
    }

}