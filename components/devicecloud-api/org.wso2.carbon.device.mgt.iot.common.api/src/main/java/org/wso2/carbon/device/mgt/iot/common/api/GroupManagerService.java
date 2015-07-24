package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;
import org.wso2.carbon.user.core.Permission;

import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

public class GroupManagerService {

    @Path("/group")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("username") String username, @QueryParam("description") String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setOwnerId(username);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            return new GroupManagement().getGroupManagementService().createGroup(group);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public boolean updateGroup(@PathParam("groupId") int groupId, @QueryParam("name") String name, @QueryParam("username") String username, @QueryParam("description") String description) {
        Group group = new Group();
        group.setId(groupId);
        group.setName(name);
        group.setDescription(description);
        group.setOwnerId(username);
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            new GroupManagement().getGroupManagementService().updateGroup(group);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean deleteGroup(@PathParam("groupId") int groupId, @QueryParam("username") String username) {
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
    public Group getGroup(@PathParam("groupId") int groupId, @QueryParam("username") String username) {
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
    public Group getGroupByName(@PathParam("groupName") String groupName, @QueryParam("username") String username) {
        try {
            return new GroupManagement().getGroupManagementService().getGroupByName(groupName);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Group[] getGroupsOfUser(@QueryParam("username") String username) {
        try {
            List<Group> groups = new GroupManagement().getGroupManagementService().getGroupListOfUser(username);
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
    public int getGroupCountOfUser(@QueryParam("username") String username) {
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
    public boolean shareGroup(@QueryParam("username") String username, @QueryParam("shareUser") String shareUser, @PathParam("groupId") int groupId, @QueryParam("role") String sharingRole) {
        try {
            return new GroupManagement().getGroupManagementService().shareGroup(shareUser, groupId, sharingRole);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}/share")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean unShareGroup(@QueryParam("username") String username, @QueryParam("unShareUser") String unShareUser, @PathParam("groupId") int groupId, @QueryParam("role") String sharingRole) {
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
    public boolean addNewSharingRoleForGroup(@QueryParam("username") String username, @PathParam("groupId") int groupId, @QueryParam("role") String roleName, @QueryParam("permissions") String[] permissions) {
        try {
            Permission[] perms = new Permission[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                perms[i] = new Permission("group-mgt/user", permissions[i]);
            }
            return new GroupManagement().getGroupManagementService().addNewSharingRoleForGroup(username, groupId, roleName, perms);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}/role")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public boolean removeSharingRoleForGroup(@PathParam("groupId") int groupId, @QueryParam("role") String roleName) {
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
    public String[] getGroupRolesForUser(@PathParam("user") String user, @QueryParam("username") String username, @PathParam("groupId") int groupId) {
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
    public String[] getUsersForGroup(@PathParam("groupId") int groupId) {
        try {
            List<String> users = new GroupManagement().getGroupManagementService().getUsersForGroup(groupId);
            String[] usersArray = new String[users.size()];
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
    public boolean addDeviceToGroup(@PathParam("groupId") int groupId, @QueryParam("deviceId") String deviceId, @QueryParam("deviceType") String deviceType) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier(deviceId,deviceType);
            return new GroupManagement().getGroupManagementService().addDeviceToGroup(deviceIdentifier, groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

}
