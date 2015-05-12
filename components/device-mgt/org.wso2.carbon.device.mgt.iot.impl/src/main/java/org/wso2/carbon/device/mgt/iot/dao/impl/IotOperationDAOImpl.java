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

package org.wso2.carbon.device.mgt.iot.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.iot.dao.IotOperationDAO;
import org.wso2.carbon.device.mgt.iot.dao.util.IotDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.iot.dto.IotOperation;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of IotOperationDAO.
 */
public class IotOperationDAOImpl implements IotOperationDAO {

	public static final String COLUMN_OPERATION_ID = "OPERATION_ID";
	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(IotOperationDAOImpl.class);

	public IotOperationDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int addIotOperation(IotOperation iotOperation)
			throws IotDeviceManagementDAOException {
		int status = -1;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO ARDUINO_OPERATION(FEATURE_CODE, CREATED_DATE) VALUES ( ?, ?)";
			stmt = conn.prepareStatement(createDBQuery, new String[] { COLUMN_OPERATION_ID });
			stmt.setString(1, iotOperation.getFeatureCode());
			stmt.setLong(2, iotOperation.getCreatedDate());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs != null && rs.next()) {
					status = rs.getInt(1);
				}
				if (log.isDebugEnabled()) {
					log.debug("Added a new IotOperation " + iotOperation.getFeatureCode() +
					          " to IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding the operation - '" +
			             iotOperation.getFeatureCode() + "' to IOT_OPERATION table";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateIotOperation(IotOperation iotOperation)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE AD_OPERATION SET FEATURE_CODE = ?, CREATED_DATE = ? WHERE " +
					"OPERATION_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setString(1, iotOperation.getFeatureCode());
			stmt.setLong(2, iotOperation.getCreatedDate());
			stmt.setInt(3, iotOperation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Updated IotOperation " + iotOperation.getFeatureCode() +
					          " to IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the IOT_OPERATION table entry with operation id - '" +
					iotOperation.getOperationId() + "'";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteIotOperation(int iotOperationId)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM AD_OPERATION WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setInt(1, iotOperationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Deleted a new IotOperation " + iotOperationId +
					          " from IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while deleting IOT_OPERATION entry with operation Id - ";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public IotOperation getIotOperation(int iotOperationId)
			throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotOperation operation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, FEATURE_CODE, CREATED_DATE FROM AD_OPERATION WHERE " +
					"OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, iotOperationId);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				operation = new IotOperation();
				operation.setOperationId(resultSet.getInt(1));
				operation.setFeatureCode(resultSet.getString(2));
				operation.setCreatedDate(resultSet.getLong(3));
				if (log.isDebugEnabled()) {
					log.debug("Fetched IotOperation " + operation.getFeatureCode() +
					          " from IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching operationId - '" +
			             iotOperationId + "' from IOT_OPERATION";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return operation;
	}

	private Connection getConnection() throws IotDeviceManagementDAOException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			String msg = "Error occurred while obtaining a connection from the iot device " +
			             "management metadata repository datasource.";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		}
	}
}