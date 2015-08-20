/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.group.common.DeviceGroup;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.group.common.GroupUser;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@WebService
public class GroupManagerService {

    private static final String DEFAULT_ADMIN_ROLE = "admin";

    private static final String[] DEFAULT_ADMIN_PERMISSIONS = {"/permission/device-mgt/admin/groups", "/permission/device-mgt/user/groups"};

    @Path("/group")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addGroup(@FormParam("name") String name, @FormParam("username") String username, @FormParam("description") String description) {
        DeviceGroup group = new DeviceGroup();
        group.setName(name);
        group.setDescription(description);
        group.setOwner(username);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            return new GroupManagement().getGroupManagementService().createGroup(group, DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_PERMISSIONS);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean updateGroup(@PathParam("groupId") int groupId, @FormParam("name") String name, @FormParam("username") String username, @FormParam("description") String description) {
        try {
            DeviceGroup group = new GroupManagement().getGroupManagementService().getGroupById(groupId);
            group.setName(name);
            group.setDescription(description);
            group.setOwner(username);
            group.setDateOfLastUpdate(new Date().getTime());
            return new GroupManagement().getGroupManagementService().updateGroup(group);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean deleteGroup(@PathParam("groupId") int groupId, @FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().deleteGroup(groupId);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup getGroup(@PathParam("groupId") int groupId, @FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupById(groupId);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/name/{groupName}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public List<DeviceGroup> getGroupByName(@PathParam("groupName") String groupName, @FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupByName(groupName, username);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/user/{username}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup[] getGroupsOfUser(@PathParam("username") String username) {
        try {
            List<DeviceGroup> groups = new GroupManagement().getGroupManagementService().getGroupsOfUser(username);
            DeviceGroup[] groupArray = new DeviceGroup[groups.size()];
            return groups.toArray(groupArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/user/{username}/permission/{permission}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup[] getUserGroupsForPermission(@PathParam("username") String username, @PathParam("permission") String permission) {
        try {
            List<DeviceGroup> groups = new GroupManagement().getGroupManagementService().getUserGroupsForPermission(username, permission);
            DeviceGroup[] groupArray = new DeviceGroup[groups.size()];
            return groups.toArray(groupArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/user/{username}/all/count")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public int getGroupCountOfUser(@PathParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupCountOfUser(username);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/share")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean shareGroup(@FormParam("username") String username, @FormParam("shareUser") String shareUser, @PathParam("groupId") int groupId, @FormParam("role") String sharingRole) {
        try {
            return new GroupManagement().getGroupManagementService().shareGroup(shareUser, groupId, sharingRole);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/unshare")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean unShareGroup(@FormParam("username") String username, @FormParam("unShareUser") String unShareUser, @PathParam("groupId") int groupId, @FormParam("role") String sharingRole) {
        try {
            return new GroupManagement().getGroupManagementService().unShareGroup(unShareUser, groupId, sharingRole);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/role")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addNewSharingRoleForGroup(@FormParam("username") String username, @PathParam("groupId") int groupId, @FormParam("role") String roleName, @FormParam("permissions") String[] permissions) {
        try {
            return new GroupManagement().getGroupManagementService().addNewSharingRoleForGroup(username, groupId, roleName, permissions);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/role")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean removeSharingRoleForGroup(@PathParam("groupId") int groupId, @FormParam("role") String roleName) {
        try {
            return new GroupManagement().getGroupManagementService().removeSharingRoleForGroup(groupId, roleName);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/role/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getAllRolesForGroup(@PathParam("groupId") int groupId) {
        try {
            List<String> roles = new GroupManagement().getGroupManagementService().getAllRolesForGroup(groupId);
            String[] rolesArray = new String[roles.size()];
            return roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/{user}/role/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getGroupRolesForUser(@PathParam("user") String user, @FormParam("username") String username, @PathParam("groupId") int groupId) {
        try {
            List<String> roles = new GroupManagement().getGroupManagementService().getGroupRolesForUser(user, groupId);
            String[] rolesArray = new String[roles.size()];
            return roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/user/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public GroupUser[] getUsersForGroup(@PathParam("groupId") int groupId) {
        try {
            List<GroupUser> users = new GroupManagement().getGroupManagementService().getUsersForGroup(groupId);
            GroupUser[] usersArray = new GroupUser[users.size()];
            return users.toArray(usersArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/device/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Device[] getAllDevicesInGroup(@PathParam("groupId") int groupId) {
        try {
            List<Device> devices = new GroupManagement().getGroupManagementService().getAllDevicesInGroup(groupId);
            Device[] deviceArray = new Device[devices.size()];
            return devices.toArray(deviceArray);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/device/assign")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addDeviceToGroup(@PathParam("groupId") int groupId, @FormParam("deviceId") String deviceId, @FormParam("deviceType") String deviceType) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(deviceId, deviceType);
            return new GroupManagement().getGroupManagementService().addDeviceToGroup(deviceIdentifier, groupId);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/group/id/{groupId}/permissions")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getGroupPermissionsOfUser(@FormParam("username") String username, @PathParam("groupId") int groupId) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupPermissionsOfUser(username, groupId);
        } catch (GroupManagementException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
