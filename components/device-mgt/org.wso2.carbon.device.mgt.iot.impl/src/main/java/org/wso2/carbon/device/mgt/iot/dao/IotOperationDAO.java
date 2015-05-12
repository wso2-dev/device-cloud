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

import org.wso2.carbon.device.mgt.iot.dto.IotOperation;

/**
 * This class represents the key operations associated with persisting iot operation related
 * information.
 */
public interface IotOperationDAO {

	/**
	 * Adds a new Iot operation to the IotOperation table.
	 * @param iotOperation IotOperation object that holds data related to the operation to be
	 *                        inserted.
	 * @return The id of the inserted record, if the insertion was unsuccessful -1 is returned.
	 * @throws IotDeviceManagementDAOException
	 */
	int addIotOperation(IotOperation iotOperation) throws IotDeviceManagementDAOException;

	/**
	 * Updates a Iot operation in the IotOperation table.
	 * @param iotOperation IotOperation object that holds data has to be updated.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean updateIotOperation(IotOperation iotOperation) throws IotDeviceManagementDAOException;

	/**
	 * Deletes a given IotOperation from IotOperation table.
	 * @param iotOperationId Operation code of the IotOperation to be deleted.
	 * @return The status of the operation.
	 * @throws IotDeviceManagementDAOException
	 */
	boolean deleteIotOperation(int iotOperationId) throws IotDeviceManagementDAOException;

	/**
	 * Retrieve a IotOperation from IotOperation table.
	 * @param iotOperationId Operation id of the IotOperation to be retrieved.
	 * @return IotOperation object that holds data of IotOperation represented by operationId.
	 * @throws IotDeviceManagementDAOException
	 */
	IotOperation getIotOperation(int iotOperationId) throws IotDeviceManagementDAOException;

}
