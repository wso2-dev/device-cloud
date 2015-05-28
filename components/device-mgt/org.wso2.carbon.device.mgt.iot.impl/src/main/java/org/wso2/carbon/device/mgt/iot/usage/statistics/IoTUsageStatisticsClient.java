package org.wso2.carbon.device.mgt.iot.usage.statistics;/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
import org.wso2.carbon.device.mgt.iot.usage.statistics.dto.DeviceUsageDTO;

//ToDo: Create correct package structure for statistics and move this class. Demonstration purpose only
public class IoTUsageStatisticsClient {

    private static final Log log = LogFactory.getLog(IoTUsageStatisticsClient.class);

    private static final String DATA_SOURCE_NAME = "jdbc/WSO2IOT_STATS_DB";

    private static volatile DataSource dataSource = null;

    public static final String DEVICE_TEMPERATURE_SUMMARY = "DEVICE_TEMPERATURE_SUMMARY";

    public static final String DEVICE_FAN_USAGE_SUMMARY = "DEVICE_FAN_USAGE_SUMMARY";

    public static final String DEVICE_BULB_USAGE_SUMMARY = "DEVICE_BULB_USAGE_SUMMARY";

    public static void initializeDataSource() throws IoTUsageStatisticsException {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(DATA_SOURCE_NAME);
        } catch (NamingException e) {
            throw new IoTUsageStatisticsException("Error while looking up the data " +
                    "source: " + DATA_SOURCE_NAME);
        }
    }

    public List<DeviceUsageDTO> getTemperatureData(String user, String deviceId, String fromDate, String toDate) throws IoTUsageStatisticsException{
    	
    	log.debug(String.format("Fetching temperature data. user : %s, deviceId : %s, from : %s, to : %s", user, deviceId, fromDate,  toDate));
    	return getDeviceStats("DEVICE_TEMPERATURE_SUMMARY", "TEMPERATURE", user, deviceId, fromDate, toDate);
    }
    
    public List<DeviceUsageDTO> getBulbStatusData(String user, String deviceId, String fromDate, String toDate) throws IoTUsageStatisticsException{
    	
    	log.debug(String.format("Fetching bulb status data. user : %s, deviceId : %s, from : %s, to : %s", user, deviceId, fromDate,  toDate));
    	return getDeviceStats("DEVICE_BULB_USAGE_SUMMARY", "STATUS", user, deviceId, fromDate, toDate);
    }
    
    public List<DeviceUsageDTO> getFanStatusData(String user, String deviceId, String fromDate, String toDate) throws IoTUsageStatisticsException{
    	
    	log.debug(String.format("Fetching fan status data. user : %s, deviceId : %s, from : %s, to : %s", user, deviceId, fromDate,  toDate));
    	return getDeviceStats("DEVICE_FAN_USAGE_SUMMARY", "STATUS", user, deviceId, fromDate, toDate);
    }

    private List<DeviceUsageDTO> getDeviceStats(String table, String valueColumn, String owner, String deviceId, String fromDate, String toDate)
            throws IoTUsageStatisticsException {

        if (dataSource == null) {
            throw new IoTUsageStatisticsException("BAM data source hasn't been initialized. Ensure " +
                                                             "that the data source is properly configured in the APIUsageTracker configuration.");
        }

        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            String query = null;

            if (fromDate != null && toDate != null) {
                query = String.format("SELECT * FROM %s WHERE owner = '%s' AND deviceid = '%s' AND `time` BETWEEN '%s' AND '%s'", table, owner, deviceId, fromDate, toDate);
            } else if(fromDate == null) {
                query = String.format("SELECT * FROM %s WHERE owner = '%s' AND deviceid = '%s' AND `time` >= '%s'", table, owner, deviceId, fromDate);
            } else if(toDate == null){
            	query = String.format("SELECT * FROM %s WHERE owner = '%s' AND deviceid = '%s' AND `time` <= '%s'", table, owner, deviceId, toDate);
            }

            if(query == null){
            	return null;
            }
            
            List<DeviceUsageDTO> deviceUsageDTOs = new ArrayList<DeviceUsageDTO>();
            rs = statement.executeQuery(query);
            while (rs.next()) {
            	DeviceUsageDTO deviceUsageDTO = new DeviceUsageDTO();
            	deviceUsageDTO.setTime(rs.getString("TIME"));
            	deviceUsageDTO.setValue(rs.getString(valueColumn));
            	
            	deviceUsageDTOs.add(deviceUsageDTO);
            	
            }
            
            return deviceUsageDTOs;

        } catch (Exception e) {
            throw new IoTUsageStatisticsException("Error occurred while querying from JDBC database", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignore) {

                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignore) {

                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignore) {

                }
            }
        }
    }
    
    
}
