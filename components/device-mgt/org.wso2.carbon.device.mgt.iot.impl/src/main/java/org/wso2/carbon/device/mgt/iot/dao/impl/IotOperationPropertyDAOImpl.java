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

package org.wso2.carbon.device.mgt.iot.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.iot.dao.IotOperationPropertyDAO;
import org.wso2.carbon.device.mgt.iot.dao.util.IotDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.iot.dto.IotOperationProperty;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of IotOperationPropertyDAO. // TODO
 */
public class IotOperationPropertyDAOImpl implements IotOperationPropertyDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(IotOperationPropertyDAOImpl.class);

	public IotOperationPropertyDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addIotOperationProperty(IotOperationProperty mblOperationProperty)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO AD_OPERATION_PROPERTY(OPERATION_ID, PROPERTY, VALUE) " +
					"VALUES ( ?, ?, ?)";
			stmt = conn.prepareStatement(createDBQuery);
			stmt.setInt(1, mblOperationProperty.getOperationId());
			stmt.setString(2, mblOperationProperty.getProperty());
			stmt.setString(3, mblOperationProperty.getValue());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Added a new MobileOperationProperty " + mblOperationProperty.getProperty() +
					          " to MDM database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while adding mobile operation property to MBL_OPERATION_PROPERTY " +
					"table";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateIotOperationProperty(
			IotOperationProperty mblOperationProperty)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"UPDATE AD_OPERATION_PROPERTY SET VALUE = ? WHERE OPERATION_ID = ? AND " +
					"PROPERTY = ?";
			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, mblOperationProperty.getValue());
			stmt.setInt(2, mblOperationProperty.getOperationId());
			stmt.setString(3, mblOperationProperty.getProperty());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Updated MobileOperationProperty " + mblOperationProperty.getProperty() +
					          " to MDM database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the mobile operation property in" +
					" MBL_OPERATION_PROPERTY table.";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteIotOperationProperties(int mblOperationId)
			throws IotDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM AD_OPERATION_PROPERTY WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setInt(1, mblOperationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
				if (log.isDebugEnabled()) {
					log.debug("Deleted MobileOperationProperties of operation-id " +
					          mblOperationId +
					          " from MDM database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while deleting MBL_OPERATION_PROPERTY entry with operation Id - ";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public IotOperationProperty getIotOperationProperty(int mblOperationId,
	                                                          String property)
			throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotOperationProperty mobileOperationProperty = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, PROPERTY, VALUE FROM AD_OPERATION_PROPERTY WHERE " +
					"OPERATION_ID = ? AND PROPERTY = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, mblOperationId);
			stmt.setString(2, property);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				mobileOperationProperty = new IotOperationProperty();
				mobileOperationProperty.setOperationId(resultSet.getInt(1));
				mobileOperationProperty.setProperty(resultSet.getString(2));
				mobileOperationProperty.setValue(resultSet.getString(3));
				if (log.isDebugEnabled()) {
					log.debug("Fetched MobileOperationProperty of Operation-id : " +
					          mblOperationId +
					          " Property : " + property + " from MDM database.");
				}
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching the mobile operation property of Operation_id : " +
					mblOperationId + " and Property : " + property;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mobileOperationProperty;
	}

	@Override
	public List<IotOperationProperty> getAllIotOperationPropertiesOfOperation(
			int mblOperationId) throws IotDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		IotOperationProperty mobileOperationProperty;
		List<IotOperationProperty> properties = new ArrayList<IotOperationProperty>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, PROPERTY, VALUE FROM AD_OPERATION_PROPERTY WHERE " +
					"OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, mblOperationId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileOperationProperty = new IotOperationProperty();
				mobileOperationProperty.setOperationId(resultSet.getInt(1));
				mobileOperationProperty.setProperty(resultSet.getString(2));
				mobileOperationProperty.setValue(resultSet.getString(3));
				properties.add(mobileOperationProperty);
			}
			if (log.isDebugEnabled()) {
				log.debug("Fetched all MobileOperationProperties of Operation-id : " +
				          mblOperationId +
				          " from MDM database.");
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching the mobile operation properties of Operation_id " +
					mblOperationId;
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		} finally {
			IotDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return properties;
	}

	private Connection getConnection() throws IotDeviceManagementDAOException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			String msg = "Error occurred while obtaining a connection from the mobile device " +
			             "management metadata repository datasource.";
			log.error(msg, e);
			throw new IotDeviceManagementDAOException(msg, e);
		}
	}
}
