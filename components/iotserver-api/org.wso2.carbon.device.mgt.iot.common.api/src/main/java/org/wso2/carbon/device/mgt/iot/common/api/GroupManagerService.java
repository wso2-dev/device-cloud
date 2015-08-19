package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.group.common.GroupUser;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;

import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

public class GroupManagerService {

    private static final String DEFAULT_ADMIN_ROLE = "admin";

    private static final String[] DEFAULT_ADMIN_PERMISSIONS = { "/permission/device-mgt/admin/groups", "/permission/device-mgt/user/groups"};

    @Path("/group")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addGroup(@FormParam("name") String name, @FormParam("username") String username, @FormParam("description") String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setOwner(username);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            return new GroupManagement().getGroupManagementService().createGroup(group, DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_PERMISSIONS);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean updateGroup(@PathParam("groupId") int groupId, @FormParam("name") String name, @FormParam("username") String username, @FormParam("description") String description) {
        try {
            Group group = new GroupManagement().getGroupManagementService().getGroupById(groupId);
            group.setName(name);
            group.setDescription(description);
            group.setOwner(username);
            group.setDateOfLastUpdate(new Date().getTime());
            return new GroupManagement().getGroupManagementService().updateGroup(group);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Group getGroup(@PathParam("groupId") int groupId, @FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupById(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/name/{groupName}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public List<Group> getGroupByName(@PathParam("groupName") String groupName, @FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupByName(groupName, username);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Group[] getGroupsOfUser(@FormParam("username") String username) {
        try {
            List<Group> groups = new GroupManagement().getGroupManagementService().getGroupsOfUser(username);
            Group[] groupArray = new Group[groups.size()];
            return groups.toArray(groupArray);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/all/count")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public int getGroupCountOfUser(@FormParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupCountOfUser(username);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return 0;
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
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return null;
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
            e.printStackTrace();
            return false;
        }
    }

}
