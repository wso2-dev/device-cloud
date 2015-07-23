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
import org.wso2.carbon.user.core.Permission;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GroupManagement implements GroupManagementService{

	private static Log log = LogFactory.getLog(GroupManagement.class);

    @Override
    public boolean createGroup(Group group) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        return groupManagementService.createGroup(group);
    }

    public void updateGroup(Group group) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        groupManagementService.updateGroup(group);
    }

    @Override
    public boolean deleteGroup(int groupId) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        return groupManagementService.deleteGroup(groupId);
    }

    public Group getGroupById(int groupId) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        return groupManagementService.getGroupById(groupId);
    }

    @Override
    public Group getGroupByName(String groupName) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        return groupManagementService.getGroupByName(groupName);
    }

    @Override
    public List<Group> getGroupListOfUser(String username) throws GroupManagementException {
        GroupManagementService groupManagementService = new GroupManagementServiceImpl();
        return groupManagementService.getGroupListOfUser(username);
    }

    @Override
    public int getGroupCountOfUser(String s) throws GroupManagementException {
        return 0;
    }

    @Override
    public boolean shareGroup(String s, int i, String s1) throws GroupManagementException {
        return false;
    }

    @Override
    public boolean unShareGroup(String s, int i, String s1) throws GroupManagementException {
        return false;
    }

    @Override
    public boolean addNewSharingRoleForGroup(String s, int i, String s1, Permission[] permissions) throws GroupManagementException {
        return false;
    }

    @Override
    public boolean removeSharingRoleForGroup(int i, String s) throws GroupManagementException {
        return false;
    }

    @Override
    public List<String> getAllRolesForGroup(int i) throws GroupManagementException {
        return null;
    }

    @Override
    public List<String> getGroupRolesForUser(String s, int i) throws GroupManagementException {
        return null;
    }

    @Override
    public String[] getUsersForGroup(int i) throws GroupManagementException {
        return new String[0];
    }

    @Override
    public List<Device> getAllDevicesInGroup(int i) throws GroupManagementException {
        return null;
    }

    @Override
    public boolean addDeviceToGroup(DeviceIdentifier deviceIdentifier, int i) throws GroupManagementException {
        return false;
    }

}