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

package org.wso2.iot.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class DBUtils {

	private static Log log = LogFactory.getLog(DBUtils.class);
	private static Connection dbConnection;

	static {
		BasicDataSource ds;
		try {
			ds = getBasicDataSource();
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

	private static BasicDataSource getBasicDataSource() throws DBFault {

		File file = new ResourceFileLoader("/resources/conf/configuration.xml").getFile();

		if (file.exists()) {

			XmlParser xmlParser;
			try {
				xmlParser = new XmlParser(file);
				try {
					String driverClassName = xmlParser.getTagValues("IOT/Database/driver")[0];
					String url = xmlParser.getTagValues("IOT/Database/url")[0];
					String username = xmlParser.getTagValues("IOT/Database/username")[0];
					String password = xmlParser.getTagValues("IOT/Database/password")[0];
					String validation = xmlParser.getTagValues("IOT/Database/validation")[0];

					BasicDataSource ds = new BasicDataSource();
					ds.setDriverClassName(driverClassName);
					ds.setUrl(url);
					ds.setUsername(username);
					ds.setPassword(password);
					ds.setValidationQuery(validation);
					return ds;

				} catch (XPathExpressionException e) {
					log.error("Database Connection Failed, Invalid Xpath Expression: " +
					          e.getMessage());
					throw new DBFault("Connection Failed");
				} catch (ArrayIndexOutOfBoundsException e) {
					log.error("Database Connection Failed" + e.getMessage());
					throw new DBFault("Connection Failed");

				}
			} catch (ParserConfigurationException e) {
				log.error("Database Connection Failed Parsing Error:" + e.getMessage());
				throw new DBFault("Connection Failed");

			} catch (SAXException e) {
				log.error("Database Connection Failed Parsing Error:" + e.getMessage());
				throw new DBFault("Connection Failed");
			} catch (IOException e) {
				log.error("Database Connection Failed Invalid Operation on File:" + e.getMessage());
				throw new DBFault("Connection Failed");
			}

		} else {
			log.error("Database Connection details are missing");
			throw new DBFault("Database Connection details are missing");

		}

	}

}
