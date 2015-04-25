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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.utils.ResourceFileLoader;
import org.wso2.iot.utils.XmlParser;
import org.xml.sax.SAXException;

public class DBUtils {

	private static Log log = LogFactory.getLog(DBUtils.class);
	private static Connection dbConnection;

	static {
		BasicDataSource ds;
		try {
			ds = getBasicDataSource("DEV-USR-IMPL");
			dbConnection = ds.getConnection();
		} catch (DBFault e) {
			log.error("Cannot acquire DB connection", e);
		} catch (SQLException e) {
			log.error("Cannot acquire DB connection", e);
		}
	}

	public Connection getConnection() {

		return dbConnection;

	}

	private static BasicDataSource getBasicDataSource(String dbName) throws DBFault {

		String file = new ResourceFileLoader("/resources/database/db.xml").getPath();

		XMLConfiguration config;
        try {
	        config = new XMLConfiguration(file);
	        config.setExpressionEngine(new XPathExpressionEngine());

			String xpath = "database[@name='" + dbName + "']";
			String driverClassName = config.getString(xpath + "/driver");
			String url = config.getString(xpath + "/url");
			String username = config.getString(xpath + "/username");
			String password = config.getString(xpath + "/password");
			String validation = config.getString(xpath + "/validation");

			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(driverClassName);
			ds.setUrl(url);
			ds.setUsername(username);
			ds.setPassword(password);
			ds.setValidationQuery(validation);
			return ds;

        } catch (ConfigurationException e) {
        	log.error("Database Connection Failed" + e.getMessage());
			throw new DBFault("Connection Failed");
        }
		
	}

}
