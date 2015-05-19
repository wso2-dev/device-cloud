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

package org.wso2.carbon.device.mgt.iot.web.register;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.user.common.User;
import org.wso2.carbon.device.mgt.user.common.UserManagementException;
import org.wso2.carbon.device.mgt.user.core.UserManager;
import org.wso2.carbon.device.mgt.user.core.UserManagerImpl;
import org.wso2.carbon.device.mgt.user.core.internal.DeviceMgtUserDataHolder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

public class UserManagement {

	private static Log log = LogFactory.getLog(UserManagement.class);

	public User getUser(String username, int tenantId) throws UserManagementException {

		UserManager userManager = new UserManagerImpl();
		return userManager.getUser(username, tenantId);

	}

	public boolean isExist(String username, int tenantId) throws UserStoreException {
		UserStoreManager userStoreManager;
		userStoreManager =
		                   DeviceMgtUserDataHolder.getInstance().getRealmService()
		                                          .getTenantUserRealm(tenantId)
		                                          .getUserStoreManager();

		if (userStoreManager.isExistingUser(username)) {
			return true;
		}
		return false;

	}

}
