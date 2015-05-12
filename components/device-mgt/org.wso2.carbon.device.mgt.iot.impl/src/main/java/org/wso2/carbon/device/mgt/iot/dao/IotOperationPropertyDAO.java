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

import org.wso2.carbon.device.mgt.iot.dto.IotOperationProperty;

import java.util.List;

/**
 *
 * This class represents the key operations associated with persisting iot operation property
 * related information.
 *
 */
public interface IotOperationPropertyDAO {

	/**
	 * Add a new IotOperationProperty to IotOperationProperty table.
	 *
	 * @param iotOperationProperty IotOperationProperty object that holds data related to the
	 *                              operation property to be inserted.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean addIotOperationProperty(IotOperationProperty iotOperationProperty)
			throws IotDeviceManagementDAOException;

	/**
	 * Update a IotOperationProperty in the IotOperationProperty table.
	 *
	 * @param iotOperationProperty IotOperationProperty object that holds data has to be updated.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean updateIotOperationProperty(IotOperationProperty iotOperationProperty)
			throws IotDeviceManagementDAOException;

	/**
	 * Deletes IotOperationProperties of a given operation id from the IotOperationProperty
	 * table.
	 *
	 * @param iotOperationId Operation id of the IotOperationProperty to be deleted.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean deleteIotOperationProperties(int iotOperationId)
			throws IotDeviceManagementDAOException;

	/**
	 * Retrieve a given IotOperationProperty from IotOperationProperty table.
	 *
	 * @param iotOperationId Operation id of the mapping to be retrieved.
	 * @param property    Property of the mapping to be retrieved.
	 * @return IotOperationProperty object that holds data of the IotOperationProperty
	 * represented by mblOperationId and property.
	 * @throws IotDeviceManagementDAOException
	 */
	IotOperationProperty getIotOperationProperty(int iotOperationId, String property)
			throws IotDeviceManagementDAOException;

	/**
	 * Retrieve all the IotOperationProperties related to the a operation id from
	 * IotOperationProperty table.
	 *
	 * @param iotOperationId Operation id of the IotOperationProperty to be retrieved.
	 * @return List of IotOperationProperty objects.
	 * @throws IotDeviceManagementDAOException
	 */
	List<IotOperationProperty> getAllIotOperationPropertiesOfOperation(int iotOperationId)
			throws IotDeviceManagementDAOException;
}
