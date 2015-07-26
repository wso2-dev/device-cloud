/*
c * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.iot.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.group.core.providers.GroupManagementServiceProvider;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

public class GroupManagement {

	private static Log log = LogFactory.getLog(GroupManagement.class);

    public GroupManagementServiceProvider getGroupManagementService() {

        GroupManagementServiceProvider groupManagementServiceProvider;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        groupManagementServiceProvider =(GroupManagementServiceProvider) ctx.getOSGiService(GroupManagementServiceProvider.class, null);
        PrivilegedCarbonContext.endTenantFlow();
        return groupManagementServiceProvider;
    }

}