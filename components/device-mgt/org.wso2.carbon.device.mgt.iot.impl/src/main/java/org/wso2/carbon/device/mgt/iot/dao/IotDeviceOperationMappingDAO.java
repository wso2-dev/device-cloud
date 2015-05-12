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

import org.wso2.carbon.device.mgt.iot.dto.IotDeviceOperationMapping;

import java.util.List;

/**
 * This class represents the mapping between mobile device and operations.
 */
public interface IotDeviceOperationMappingDAO {

	/**
	 * Adds a new mobile device operation mapping to the table.
	 *
	 * @param iotDeviceOperationMapping MobileDeviceOperationMapping object that holds data related
	 *                                  to the MobileDeviceOperationMapping to be inserted.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean addIotDeviceOperationMapping(IotDeviceOperationMapping iotDeviceOperationMapping)
			throws IotDeviceManagementDAOException;

	/**
	 * Updates a MobileDeviceOperationMapping in MobileDeviceOperationMapping table.
	 *
	 * @param iotDeviceOperation MobileDeviceOperationMapping object that holds data has to be updated.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean updateIotDeviceOperationMapping(IotDeviceOperationMapping iotDeviceOperation)
			throws IotDeviceManagementDAOException;

	/**
	 * Updates a MobileDeviceOperationMapping to In-Progress state in MobileDeviceOperationMapping
	 * table.
	 *
	 * @param iotDeviceId MobileDevice id of the mappings to be updated.
	 * @param operationId Operation id of the mapping to be updated.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean updateIotDeviceOperationMappingToInProgress(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException;

	/**
	 * Updates a MobileDeviceOperationMapping to completed state in MobileDeviceOperationMapping
	 * table.
	 *
	 * @param iotDeviceId MobileDevice id of the mappings to be updated.
	 * @param operationId Operation id of the mapping to be updated.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean updateIotDeviceOperationMappingToCompleted(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException;

	/**
	 * Delete a given MobileDeviceOperationMapping from MobileDeviceOperationMapping table.
	 *
	 * @param iotDeviceId MobileDevice id of the mappings to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean deleteIotDeviceOperationMapping(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException;

	/**
	 * Retrieves a given MobileDeviceOperationMapping object from the MobileDeviceOperationMapping
	 * table.
	 *
	 * @param iotDeviceId Device id of the mapping to be retrieved.
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @return MobileDeviceOperation object that holds data of the device operation mapping
	 * represented by deviceId and operationId.
	 * @throws IotDeviceManagementDAOException
	 */
	IotDeviceOperationMapping getIotDeviceOperationMapping(String iotDeviceId, int operationId)
			throws IotDeviceManagementDAOException;

	/**
	 * Retrieves all the of MobileDeviceOperationMappings relevant to a given mobile device.
	 *
	 * @param iotDeviceId MobileDevice id of the mappings to be retrieved.
	 * @return MobileDeviceOperationMapping object list.
	 * @throws IotDeviceManagementDAOException
	 */
	List<IotDeviceOperationMapping> getAllIotDeviceOperationMappingsOfDevice(String iotDeviceId)
			throws IotDeviceManagementDAOException;

	/**
	 * Retrieves all the pending MobileDeviceOperationMappings of a mobile device.
	 *
	 * @param iotDeviceId MobileDevice id of the mappings to be retrieved.
	 * @return MobileDeviceOperationMapping object list.
	 * @throws IotDeviceManagementDAOException
	 */
	List<IotDeviceOperationMapping> getAllPendingOperationMappingsOfIotDevice(String iotDeviceId)
			throws IotDeviceManagementDAOException;
}
