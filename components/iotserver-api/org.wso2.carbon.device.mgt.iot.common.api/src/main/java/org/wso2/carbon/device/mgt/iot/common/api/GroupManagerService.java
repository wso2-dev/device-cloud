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
import org.wso2.carbon.device.mgt.group.core.providers.GroupManagementServiceProvider;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@WebService
public class GroupManagerService {

    @Context  //injected response proxy supporting multiple threads
    private HttpServletResponse response;

    private static final String DEFAULT_ADMIN_ROLE = "admin";
    private static final String DEFAULT_OPERATOR_ROLE = "operator";
    private static final String[] DEFAULT_ADMIN_PERMISSIONS = {"/permission/device-mgt/admin/groups", "/permission/device-mgt/user/groups"};
    private static final String[] DEFAULT_OPERATOR_PERMISSIONS = {"/permission/device-mgt/user/groups"};

    private final String SUPER_TENANT = "carbon.super";

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
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isAdded = false;
        try {
            GroupManagementServiceProvider groupManagementService = groupManagement.getGroupManagementService();
            int groupId = groupManagementService.createGroup(group, DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_PERMISSIONS);
            response.setStatus(Response.Status.OK.getStatusCode());
            isAdded = (groupId > 0) && groupManagementService.addNewSharingRoleForGroup(username, groupId, DEFAULT_OPERATOR_ROLE, DEFAULT_OPERATOR_PERMISSIONS);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isAdded;
    }

    @Path("/group/id/{groupId}")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean updateGroup(@PathParam("groupId") int groupId, @FormParam("name") String name, @FormParam("username") String username, @FormParam("description") String description) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/modify")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isUpdated = false;
        try {
            GroupManagementServiceProvider groupManagementService = groupManagement.getGroupManagementService();
            DeviceGroup group = groupManagementService.getGroupById(groupId);
            group.setName(name);
            group.setDescription(description);
            group.setOwner(username);
            group.setDateOfLastUpdate(new Date().getTime());
            response.setStatus(Response.Status.OK.getStatusCode());
            isUpdated = groupManagementService.updateGroup(group);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isUpdated;
    }

    @Path("/group/id/{groupId}")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean deleteGroup(@PathParam("groupId") int groupId, @QueryParam("username") String username) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/delete")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isDeleted = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isDeleted = groupManagement.getGroupManagementService().deleteGroup(groupId);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isDeleted;
    }

    @Path("/group/id/{groupId}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup getGroup(@PathParam("groupId") int groupId, @FormParam("username") String username) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        DeviceGroup deviceGroup = null;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            deviceGroup = groupManagement.getGroupManagementService().getGroupById(groupId);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return deviceGroup;
    }

    @Path("/group/name/{groupName}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup[] getGroupsByName(@PathParam("groupName") String groupName, @FormParam("username") String username) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        DeviceGroup[] deviceGroups = null;
        try {
            List<DeviceGroup> groups = groupManagement.getGroupManagementService().getGroupByName(groupName, username);
            deviceGroups = new DeviceGroup[groups.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            groups.toArray(deviceGroups);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return deviceGroups;
    }

    @Path("/group/user/{username}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public DeviceGroup[] getGroupsOfUser(@PathParam("username") String username, @QueryParam("permission") String permission) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        DeviceGroup[] deviceGroups = null;
        try {
            GroupManagementServiceProvider groupManagementService = groupManagement.getGroupManagementService();
            List<DeviceGroup> groups;
            if(permission != null){
                groups = groupManagementService.getUserGroupsForPermission(username, permission);
            }else{
                groups = groupManagementService.getGroupsOfUser(username);
            }
            deviceGroups = new DeviceGroup[groups.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            groups.toArray(deviceGroups);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return deviceGroups;
    }

    @Path("/group/user/{username}/all/count")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public int getGroupCountOfUser(@PathParam("username") String username) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        int count = -1;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            count = groupManagement.getGroupManagementService().getGroupCountOfUser(username);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return count;
    }

    @Path("/group/id/{groupId}/share")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean shareGroup(@FormParam("username") String username, @FormParam("shareUser") String shareUser, @PathParam("groupId") int groupId, @FormParam("role") String sharingRole) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/share")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isShared = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isShared = groupManagement.getGroupManagementService().shareGroup(shareUser, groupId, sharingRole);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isShared;
    }

    @Path("/group/id/{groupId}/unshare")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean unShareGroup(@FormParam("username") String username, @FormParam("unShareUser") String unShareUser, @PathParam("groupId") int groupId, @FormParam("role") String sharingRole) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/share")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isUnShared = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isUnShared = groupManagement.getGroupManagementService().unShareGroup(unShareUser, groupId, sharingRole);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isUnShared;
    }

    @Path("/group/id/{groupId}/role")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addNewSharingRoleForGroup(@FormParam("username") String username, @PathParam("groupId") int groupId, @FormParam("role") String roleName, @FormParam("permissions") String[] permissions) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/share")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isAdded = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isAdded = groupManagement.getGroupManagementService().addNewSharingRoleForGroup(username, groupId, roleName, permissions);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isAdded;
    }

    @Path("/group/id/{groupId}/role/{role}")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean removeSharingRoleForGroup(@QueryParam("username") String username, @PathParam("groupId") int groupId, @PathParam("role") String roleName) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/share")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isRemoved = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isRemoved = groupManagement.getGroupManagementService().removeSharingRoleForGroup(groupId, roleName);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isRemoved;
    }

    @Path("/group/id/{groupId}/role/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getAllRolesForGroup(@PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        String[] rolesArray = null;
        try {
            List<String> roles = groupManagement.getGroupManagementService().getAllRolesForGroup(groupId);
            rolesArray = new String[roles.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return rolesArray;
    }

    @Path("/group/id/{groupId}/{user}/role/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getGroupRolesForUser(@PathParam("user") String user, @FormParam("username") String username, @PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        String[] rolesArray = null;
        try {
            List<String> roles = groupManagement.getGroupManagementService().getGroupRolesForUser(user, groupId);
            rolesArray = new String[roles.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return rolesArray;
    }

    @Path("/group/id/{groupId}/user/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public GroupUser[] getUsersForGroup(@PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        GroupUser[] usersArray = null;
        try {
            List<GroupUser> users = groupManagement.getGroupManagementService().getUsersForGroup(groupId);
            usersArray = new GroupUser[users.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            users.toArray(usersArray);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return usersArray;
    }

    @Path("/group/id/{groupId}/device/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Device[] getAllDevicesInGroup(@PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        Device[] deviceArray = null;
        try {
            List<Device> devices = groupManagement.getGroupManagementService().getAllDevicesInGroup(groupId);
            deviceArray = new Device[devices.size()];
            response.setStatus(Response.Status.OK.getStatusCode());
            devices.toArray(deviceArray);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return deviceArray;
    }

    @Path("/group/id/{groupId}/device/assign")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addDeviceToGroup(@PathParam("groupId") int groupId, @FormParam("deviceId") String deviceId, @FormParam("deviceType") String deviceType, @FormParam("username") String username) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/add_devices")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isAdded = false;
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(deviceId, deviceType);
            response.setStatus(Response.Status.OK.getStatusCode());
            isAdded = groupManagement.getGroupManagementService().addDeviceToGroup(deviceIdentifier, groupId);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isAdded;
    }

    @Path("/group/id/{groupId}/device/assign")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean removeDeviceFromGroup(@PathParam("groupId") int groupId, @FormParam("deviceId") String deviceId, @FormParam("deviceType") String deviceType, @FormParam("username") String username) {
        if (!isAuthorized(username, groupId, "/permission/device-mgt/admin/groups/remove_devices")){
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            return false;
        }
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isRemoved = false;
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(deviceId, deviceType);
            response.setStatus(Response.Status.OK.getStatusCode());
            isRemoved = groupManagement.getGroupManagementService().removeDeviceFromGroup(deviceIdentifier, groupId);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isRemoved;
    }

    @Path("/group/id/{groupId}/user/{username}/permissions")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public String[] getGroupPermissionsOfUser(@PathParam("username") String username, @PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        String[] permissions = null;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            permissions = groupManagement.getGroupManagementService().getGroupPermissionsOfUser(username, groupId);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return permissions;
    }

    @Path("/group/id/{groupId}/user/{username}/authorized")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public boolean isAuthorized(@PathParam("username") String username, @PathParam("groupId") int groupId, @QueryParam("permission") String permission){
        GroupManagement groupManagement = new GroupManagement(SUPER_TENANT);
        boolean isAuthorized = false;
        try {
            response.setStatus(Response.Status.OK.getStatusCode());
            isAuthorized = groupManagement.getGroupManagementService().isAuthorized(username, groupId, permission);
        } catch (GroupManagementException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } finally {
            groupManagement.endTenantFlow();
        }
        return isAuthorized;
    }

}
