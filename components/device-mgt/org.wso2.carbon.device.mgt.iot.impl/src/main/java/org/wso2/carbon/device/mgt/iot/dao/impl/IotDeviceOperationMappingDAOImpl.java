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
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceOperationMappingDAO;
import org.wso2.carbon.device.mgt.iot.dao.util.IotDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.iot.dto.IotDeviceOperationMapping;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of IotDeviceOperationMappingDAO.
 */
public class IotDeviceOperationMappingDAOImpl implements IotDeviceOperationMappingDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(IotDeviceOperationMappingDAOImpl.class);

	public IotDeviceOperationMappingDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addIotDeviceOperationMapping(IotDeviceOperationMapping iotDeviceOperationMapping)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO ARDUINO_DEVICE_OPERATION_MAPPING (DEVICE_ID, OPERATION_ID, SENT_DATE, " +
					"RECEIVED_DATE, STATUS) VALUES (?, ?, ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, iotDeviceOperationMapping.getDeviceId());
			stmt.setLong(2, iotDeviceOperationMapping.getOperationId());
			stmt.setLong(3, iotDeviceOperationMapping.getSentDate());
			stmt.setLong(4, iotDeviceOperationMapping.getReceivedDate());
			stmt.setString(5, iotDeviceOperationMapping.getStatus().name());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Added a IotDevice-Mapping DeviceId : " + iotDeviceOperationMapping
							.getDeviceId() + ", " +
					          "OperationId : " + iotDeviceOperationMapping.getOperationId() + " to the IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding device id - '" +
			             iotDeviceOperationMapping.getDeviceId() + " and operation id - " +
			             iotDeviceOperationMapping.getOperationId() +
			             " to mapping table AD_DEVICE_OPERATION";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateIotDeviceOperationMapping(IotDeviceOperationMapping iotDeviceOperation)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE ARDUINO_DEVICE_OPERATION_MAPPING SET SENT_DATE = ?, RECEIVED_DATE = ?, " +
					"STATUS = ? WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, iotDeviceOperation.getSentDate());
			stmt.setLong(2, iotDeviceOperation.getReceivedDate());
			stmt.setString(3, iotDeviceOperation.getStatus().name());
			stmt.setString(4, iotDeviceOperation.getDeviceId());
			stmt.setInt(5, iotDeviceOperation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Updated IotDevice-Mapping DeviceId : " + iotDeviceOperation.getDeviceId() + " , " +
					          "OperationId : " + iotDeviceOperation.getOperationId());
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating device id - '" +
			             iotDeviceOperation.getDeviceId() + " and operation id - " +
			             iotDeviceOperation.getOperationId() + " in table IOT_DEVICE_OPERATION";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateIotDeviceOperationMappingToInProgress(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE ARDUINO_DEVICE_OPERATION_MAPPING SET SENT_DATE = ?, STATUS = ? " +
					"WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, new Date().getTime());
			stmt.setString(2, IotDeviceOperationMapping.Status.INPROGRESS.name());
			stmt.setString(3, iotDeviceId);
			stmt.setInt(4, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Updated status of IotDevice-Mapping DeviceId : " + iotDeviceId + " , " +
					          "OperationId : " + operationId + " to In-Progress state");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the Status of operation to in-progress of device id - '" +
					iotDeviceId + " and operation id - " +
					operationId + " in table IOT_DEVICE_OPERATION";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateIotDeviceOperationMappingToCompleted(String iotDeviceId,
	                                                             int operationId)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE ARDUINO_DEVICE_OPERATION_MAPPING SET RECEIVED_DATE = ?, STATUS = ? " +
					"WHERE DEVICE_ID = ? AND OPERATION_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, new Date().getTime());
			stmt.setString(2, IotDeviceOperationMapping.Status.COMPLETED.name());
			stmt.setString(3, iotDeviceId);
			stmt.setInt(4, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Updated status of IotDevice-Mapping DeviceId : " + iotDeviceId + " , " +
					          "OperationId : " + operationId + " to Completed state");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the Status of operation to completed of device id - '" +
					iotDeviceId + " and operation id - " +
					operationId + " in table IOT_DEVICE_OPERATION";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteIotDeviceOperationMapping(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM ARDUINO_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND " +
					"OPERATION_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, iotDeviceId);
			stmt.setInt(2, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Deleted IotDevice-Mapping DeviceId : " + iotDeviceId + " , " +
					          "OperationId : " + operationId + "from IOT database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while deleting the table entry ARDUINO_DEVICE_OPERATION with " +
					" device id - '" + iotDeviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public IotDeviceOperationMapping getIotDeviceOperationMapping(String iotDeviceId,
	                                                                    int operationId)
			throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotDeviceOperationMapping iotDeviceOperation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM " +
					"ARDUINO_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, iotDeviceId);
			stmt.setInt(2, operationId);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				iotDeviceOperation = new IotDeviceOperationMapping();
				iotDeviceOperation.setDeviceId(resultSet.getString(1));
				iotDeviceOperation.setOperationId(resultSet.getInt(2));
				iotDeviceOperation.setSentDate(resultSet.getInt(3));
				iotDeviceOperation.setReceivedDate(resultSet.getInt(4));
				iotDeviceOperation.setStatus(resultSet.getString(5));
				if (log.isDebugEnabled()) {
					log.debug("Fetched IotDevice-Mapping of DeviceId : " + iotDeviceId + " , " +
					          "OperationId : " + operationId );
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching table IOT_DEVICE_OPERATION entry with device id - '" +
					iotDeviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return iotDeviceOperation;
	}

	@Override
	public List<IotDeviceOperationMapping> getAllIotDeviceOperationMappingsOfDevice(
			String iotDeviceId)
			throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotDeviceOperationMapping iotDeviceOperation;
		List<IotDeviceOperationMapping> iotDeviceOperations =
				new ArrayList<IotDeviceOperationMapping>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM " +
					"AD_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, iotDeviceId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				iotDeviceOperation = new IotDeviceOperationMapping();
				iotDeviceOperation.setDeviceId(resultSet.getString(1));
				iotDeviceOperation.setOperationId(resultSet.getInt(2));
				iotDeviceOperation.setSentDate(resultSet.getInt(3));
				iotDeviceOperation.setReceivedDate(resultSet.getInt(4));
				iotDeviceOperation.setStatus(resultSet.getString(5));
				iotDeviceOperations.add(iotDeviceOperation);
			}
			if (log.isDebugEnabled()) {
				log.debug("Fetched all IotDevice-Mappings of DeviceId : " + iotDeviceId);
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table IOT_DEVICE_OPERATION entries of " +
					"device id - '" + iotDeviceId;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return iotDeviceOperations;
	}

	@Override
	public List<IotDeviceOperationMapping> getAllPendingOperationMappingsOfIotDevice(
			String iotDeviceId)
			throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotDeviceOperationMapping iotDeviceOperation = null;
		List<IotDeviceOperationMapping> iotDeviceOperations =
				new ArrayList<IotDeviceOperationMapping>();
		try {

			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM" +
					" ARDUINO_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND STATUS = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, iotDeviceId);
			stmt.setString(2, IotDeviceOperationMapping.Status.NEW.name());
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				iotDeviceOperation = new IotDeviceOperationMapping();
				iotDeviceOperation.setDeviceId(resultSet.getString(1));
				iotDeviceOperation.setOperationId(resultSet.getInt(2));
				iotDeviceOperation.setSentDate(resultSet.getInt(3));
				iotDeviceOperation.setReceivedDate(resultSet.getInt(4));
				iotDeviceOperation.setStatus(resultSet.getString(5));
				iotDeviceOperations.add(iotDeviceOperation);
			}
			if (log.isDebugEnabled()) {
				log.debug("Fetched all pending IotDevice-Mappings of DeviceId : " + iotDeviceId);
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table IOT_DEVICE_OPERATION entries of" +
					" device id - '" + iotDeviceId;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return iotDeviceOperations;
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
