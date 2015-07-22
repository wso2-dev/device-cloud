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
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.group.core.service.GroupManagementService;
import org.wso2.carbon.device.mgt.group.core.service.GroupManagementServiceImpl;
import org.wso2.carbon.device.mgt.iot.common.util.ZipArchive;
import org.wso2.carbon.device.mgt.iot.common.util.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GroupManagement {

	private static Log log = LogFactory.getLog(GroupManagement.class);

    public void addDeviceGroup(Group group) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        groupManagementService.createGroup(group);
    }

    public void removeDeviceGroup(int groupId) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        groupManagementService.deleteGroup(groupId);
    }

}