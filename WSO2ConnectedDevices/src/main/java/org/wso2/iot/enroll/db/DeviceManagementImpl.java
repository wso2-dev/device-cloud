/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.iot.enroll.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.device.Device;
import org.wso2.iot.enroll.DeviceManagement;
import org.wso2.iot.user.User;

/**
 * @author ayyoobhamza
 * 
 */
public class DeviceManagementImpl implements DeviceManagement {
	private static Log log = LogFactory.getLog(UserManagementImpl.class);

	@Override
	public boolean addNewDevice(Device device) {
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			prepStmt =
			           //dbConnection.prepareStatement("INSERT INTO device (id,name,description,model,owner,token,type) VALUES ('" +
			        		   dbConnection.prepareStatement("INSERT INTO device (id,name,description,model,owner,type) VALUES ('" +

			        		   device.getDeviceId() +
			                                         "','" +
			                                         device.getName() +
			                                         "','" +
			                                         device.getDesciption() +
			                                         "','" +
			                                         device.getModel() +
			                                         "','" +
			                                         device.getOwner() +
//			                                         "','" +
//			                                         device.getToken() +
			                                         "','" +
			                                         device.getType() + "')");
			prepStmt.execute();

			log.info("New Device Added");
			return true;
		} catch (SQLException e) {
			log.error("", e);
			return false;
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}

			} catch (SQLException e) {
				log.error("", e);
			}
		}
	}

	@Override
	public boolean removeDevice(String deviceId) {
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			
			prepStmt =
			           dbConnection.prepareStatement("Delete from device where id='" +
			                                         deviceId + "'");
			prepStmt.execute();

			log.info("device removed");
			return true;
		} catch (SQLException e) {
			log.error("", e);
			return false;
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}

			} catch (SQLException e) {
				log.error("", e);
			}
		}
	}

	@Override
	public boolean update(Device device) {
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			prepStmt =
			           dbConnection.prepareStatement("update device  SET  name='" +
                               device.getName() +
                               "', description='" +
                               device.getDesciption() +
                               "', model='" +
                               device.getModel() +
                               "', owner='" +
                               device.getOwner() +
//                               "', token='" +
//                               device.getToken() +
                               "', type='" +
                               device.getType() + "' where id='"+device.getDeviceId()+"'");
			prepStmt.execute();

			
			log.info("device updated");
			return true;
		} catch (SQLException e) {
			log.error("", e);
			return false;
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}

			} catch (SQLException e) {
				log.error("", e);
			}
		}
	}

	
	@Override
	public Device getDevice(String deviceId) {
		Device result = null;
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		ResultSet rs = null;
		try {
			prepStmt =
//			           dbConnection.prepareStatement("SELECT id,name,type,model,description,owner,token FROM device where  id='" +
//			                                         deviceId + "'");			
	           dbConnection.prepareStatement("SELECT id,name,type,model,description,owner FROM device where  id='" +
                       deviceId + "'");

			rs = prepStmt.executeQuery();

			if (rs.next()) {
				result = new Device();
				result.setDeviceId(rs.getString(1));
				result.setName(rs.getString(2));
				result.setType(rs.getString(3));
				result.setModel(rs.getString(4));
				result.setDesciption(rs.getString(5));
				result.setOwner(rs.getString(6));
	//			result.setToken(rs.getString(7));
				
			}
		} catch (SQLException e) {
			log.error("", e);
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				log.error("", e);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wso2.iot.enroll.DeviceManagement#generateNewToken()
	 */
//	@Override
//	public String generateNewToken() {
//		UUID idOne = UUID.randomUUID();
//
//		return idOne.toString();
//	}

	/* (non-Javadoc)
	 * @see org.wso2.iot.enroll.DeviceManagement#isExist(java.lang.String)
	 */
    @Override
    public boolean isExist(String deviceId) {
    	
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		ResultSet rs = null;
		try {
			prepStmt =
			           dbConnection.prepareStatement("SELECT * FROM device where  id='" +
			                                         deviceId + "'");
			rs = prepStmt.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			log.error("", e);
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				log.error("", e);
			}
		}
		return false;
    }

	/* (non-Javadoc)
	 * @see org.wso2.iot.enroll.DeviceManagement#isExist(java.lang.String, java.lang.String)
	 */
    @Override
    public boolean isExist(String owner, String deviceId) {
    	PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		ResultSet rs = null;
		try {
			prepStmt =
			           dbConnection.prepareStatement("SELECT * FROM device where  id='" +
			                                         deviceId + "' and owner='"+owner+"'");
			rs = prepStmt.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			log.error("", e);
		} finally {
			try {
				if (prepStmt != null) {
					prepStmt.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				log.error("", e);
			}
		}
		return false;
    }

}
