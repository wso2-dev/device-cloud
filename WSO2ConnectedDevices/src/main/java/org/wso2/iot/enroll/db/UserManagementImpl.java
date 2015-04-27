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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.enroll.UserManagement;
import org.wso2.iot.user.User;

public class UserManagementImpl implements UserManagement {

	private static Log log = LogFactory.getLog(UserManagementImpl.class);

	@Override
	public boolean addNewUser(User user) {

		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			prepStmt =
			           dbConnection.prepareStatement("INSERT INTO user (firstname,lastname,email,username,password) VALUES ('" +
			                                         user.getFirstname() +
			                                         "','" +
			                                         user.getLastname() +
			                                         "','" +
			                                         user.getEmail() +
			                                         "','" +
			                                         user.getUsername() +
			                                         "','" +
			                                         DigestUtils.shaHex(user.getPassword()) + "' )");
			prepStmt.execute();

			String[] roles = new String[user.getRoles().size()];
			roles = user.getRoles().toArray(roles);

			for (String role : roles) {
				prepStmt =
				           dbConnection.prepareStatement("INSERT INTO user_roles (userid,role) VALUES ((select id from user where username='" +
				                                         user.getUsername() + "'),'" + role + "' )");
				prepStmt.execute();

			}
			log.info("new User added");
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
	public boolean removeUser(String username) {

		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			prepStmt =
			           dbConnection.prepareStatement("Delete from user_roles where userid=(select id from user where username='" +
			                                         username + "' )");

			prepStmt.execute();
			prepStmt =
			           dbConnection.prepareStatement("Delete from user where username='" +
			                                         username + "'");
			prepStmt.execute();

			log.info("User removed");
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
	public boolean updateUser(User user) {
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		try {

			prepStmt =
			           dbConnection.prepareStatement("update user  SET firstname='" +
			                                         user.getFirstname() + "', lastname='" +
			                                         user.getLastname() + "', email='" +
			                                         user.getEmail() + "', password='" +

			                                         DigestUtils.shaHex(user.getPassword()) + "' ");
			prepStmt.execute();

			prepStmt =
			           dbConnection.prepareStatement("Delete from user_roles where userid=(Select id from user where username='" +
			                                         user.getUsername() + "' )");
			prepStmt.execute();

			String[] roles = new String[user.getRoles().size()];
			roles = user.getRoles().toArray(roles);

			for (String role : roles) {
				prepStmt =
				           dbConnection.prepareStatement("INSERT INTO user_roles (userid,role) VALUES ((select id from user where username='" +
				                                         user.getUsername() + "'),'" + role + "' )");
				prepStmt.execute();

			}
			log.info("user updated");
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
	public User getUser(String userName) {

		User result = null;
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		ResultSet rs = null;
		try {
			prepStmt =
			           dbConnection.prepareStatement("SELECT id,firstname,lastname,password,email,username FROM user where  username='" +
			                                         userName + "'");
			rs = prepStmt.executeQuery();

			if (rs.next()) {
				result = new User();
				result.setId(rs.getString(1));
				result.setFirstname(rs.getString(2));
				result.setLastname(rs.getString(3));
				result.setPassword(rs.getString(4));
				result.setEmail(rs.getString(5));
				result.setUsername(rs.getString(6));

				prepStmt =
				           dbConnection.prepareStatement("SELECT role FROM user_roles where  userid='" +
				                                         rs.getString(1) + "'");
				rs = prepStmt.executeQuery();
				while (rs.next()) {
					result.addRole(rs.getString(1));

				}
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

	@Override
	public boolean isAuthenticated(String username, String password) {
		User result = null;
		PreparedStatement prepStmt = null;
		Connection dbConnection = new DBUtils().getConnection();
		ResultSet rs = null;
		try {
			prepStmt =
			           dbConnection.prepareStatement("SELECT * FROM user where  username='" +
			                                         username + "' and password='"+DigestUtils.shaHex(password)+"'");
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
	 * @see org.wso2.iot.enroll.UserManagement#getAnonymousUserName()
	 */
    @Override
    public String getAnonymousUserName() {
	    
	    return "org.wso2.anonymous";
    }

}
