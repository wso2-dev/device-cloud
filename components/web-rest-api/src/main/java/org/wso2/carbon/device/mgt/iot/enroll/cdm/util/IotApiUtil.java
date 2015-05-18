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

package org.wso2.carbon.device.mgt.iot.enroll.cdm.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.iot.common.DeviceCloudException;
import org.wso2.carbon.device.mgt.user.core.service.UserManagementService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.device.mgt.user.core.service.UserManagementService;

public class IotApiUtil {

	private static Log log = LogFactory.getLog(IotApiUtil.class);
	
	public static DeviceManagementService getDeviceManagementService(String tenantDomain) throws DeviceCloudException {
		// until complete login this is use to load super tenant context
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		int tenantId;
		DeviceManagementService dmService;
		if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
			tenantId = MultitenantConstants.SUPER_TENANT_ID;
		} else {
			tenantId = getTenantId(tenantDomain);
		}
		ctx.setTenantDomain(tenantDomain);
		ctx.setTenantId(tenantId);
		dmService =(DeviceManagementService) ctx.getOSGiService(DeviceManagementService.class,
		                                                         null);
		return dmService;
		
	
	}

	public static DeviceManagementService getDeviceManagementService() throws DeviceCloudException {
		getTenantId(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		getUserManagementService();
		return getDeviceManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
	}

	public static int getTenantId(String tenantDomain) throws DeviceCloudException {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
		
		try {
			return realmService.getTenantManager().getTenantId(tenantDomain);
		} catch (UserStoreException e) {
			throw new DeviceCloudException("Error obtaining tenant id from tenant domain " +
			                          tenantDomain);
		}
	}
	

	public static UserManagementService getUserManagementService() throws DeviceCloudException {

		UserManagementService umService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		umService = (UserManagementService) ctx.getOSGiService(UserManagementService.class, null);
		if (umService == null) {
			String msg = "user management service not initialized";
			log.error(msg);
            throw new DeviceCloudException(msg);
		}
		PrivilegedCarbonContext.endTenantFlow();
		return umService;
	}
	
	public static UserStoreManager getUserStoreManagerService() throws DeviceCloudException {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
		
		try {
			return realmService.getTenantUserRealm(MultitenantConstants.SUPER_TENANT_ID).getUserStoreManager();
		} catch (UserStoreException e) {
			throw new DeviceCloudException("Error getting user store manager");
		}
	}

}
