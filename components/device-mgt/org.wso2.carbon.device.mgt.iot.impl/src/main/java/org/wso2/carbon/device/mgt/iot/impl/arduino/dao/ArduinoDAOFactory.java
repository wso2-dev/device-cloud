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

package org.wso2.carbon.device.mgt.iot.impl.arduino.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.config.datasource.IotDataSourceConfig;
import org.wso2.carbon.device.mgt.iot.dao.*;
import org.wso2.carbon.device.mgt.iot.impl.arduino.dao.impl.ArduinoDeviceDAOImpl;

import javax.sql.DataSource;

public class ArduinoDAOFactory extends IotDeviceManagementDAOFactory
        implements IotDeviceManagementDAOFactoryInterface {

    private static final Log log = LogFactory.getLog(ArduinoDAOFactory.class);
    private static DataSource dataSource;

    public static void init(IotDataSourceConfig config) {
        dataSource = resolveDataSource(config);
    }

    @Override
    public IotDeviceDAO getIotDeviceDAO() {
        return new ArduinoDeviceDAOImpl(dataSource);
    }

    @Override
    public IotOperationDAO getIotOperationDAO() {
        return null;
    }

    @Override
    public IotOperationPropertyDAO getIotOperationPropertyDAO() {
        return null;
    }

    @Override
    public IotDeviceOperationMappingDAO getIotDeviceOperationDAO() {
        return null;
    }



}