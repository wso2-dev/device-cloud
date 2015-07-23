package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;
import org.wso2.carbon.user.core.Permission;

import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

public class GroupManagerService {

    @Path("/group/add")
    @POST
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("owner") String owner) {
        Group group = new Group();
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            return new GroupManagement().getGroupManagementService().createGroup(group);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/update/{groupId}")
    @POST
    public boolean updateGroup(@PathParam("groupId") int groupId, @QueryParam("name") String name, @QueryParam("owner") String owner) {
        Group group = new Group();
        group.setId(groupId);
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            new GroupManagement().getGroupManagementService().updateGroup(group);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/delete/{groupId}")
    @DELETE
    public boolean deleteGroup(@PathParam("groupId") int groupId) {
        try {
            return  new GroupManagement().getGroupManagementService().deleteGroup(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}")
    @GET
    public Group getGroup(@PathParam("groupId") int groupId) {
        try {
            return  new GroupManagement().getGroupManagementService().getGroupById(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/name/{groupName}")
    @GET
    public Group getGroupByName(@PathParam("groupName") String groupName) {
        try {
            return  new GroupManagement().getGroupManagementService().getGroupByName(groupName);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/all")
    @GET
    public Group[] getGroupsOfUser(@QueryParam("username") String username) {
        try {
            List<Group>groups =  new GroupManagement().getGroupManagementService().getGroupListOfUser(username);
            Group[] groupArray = new Group[groups.size()];
            return  groups.toArray(groupArray);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/count")
    @GET
    public int getGroupCountOfUser(@QueryParam("username") String username) {
        try {
            return  new GroupManagement().getGroupManagementService().getGroupCountOfUser(username);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Path("/group/id/{groupId}/share")
    @POST
    public boolean shareGroup(@QueryParam("name") String username, @PathParam("groupId") int groupId, @QueryParam("role")String sharingRole) {
        try {
            return  new GroupManagement().getGroupManagementService().shareGroup(username, groupId, sharingRole);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}/unshare")
    @POST
    public boolean unShareGroup(@QueryParam("name") String username, @PathParam("groupId") int groupId, @QueryParam("role")String sharingRole) {
        try {
            return  new GroupManagement().getGroupManagementService().unShareGroup(username, groupId, sharingRole);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}/role/add")
    @PUT
    public boolean addNewSharingRoleForGroup(@QueryParam("name") String username, @PathParam("groupId") int groupId, @QueryParam("role") String roleName, @QueryParam("permissions") String[] permissions) {
        try {
            Permission[] perms = new Permission[permissions.length];
            for (int i = 0; i < permissions.length; i++){
                perms[i] = new Permission("group-mgt/user", permissions[i]);
            }
            return  new GroupManagement().getGroupManagementService().addNewSharingRoleForGroup(username, groupId, roleName, perms);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Path("/group/id/{groupId}/role/remove")
    @DELETE
    public boolean removeSharingRoleForGroup(@PathParam("groupId") int groupId, @QueryParam("role") String roleName) {
        
        try {
            return  new GroupManagement().getGroupManagementService().removeSharingRoleForGroup(groupId, roleName);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/id/{groupId}/role/all")
    @GET
    public String[] getAllRolesForGroup(@PathParam("groupId") int groupId) {
        try {
            List<String> roles =  new GroupManagement().getGroupManagementService().getAllRolesForGroup(groupId);
            String[] rolesArray = new String[roles.size()];
            return  roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/id/{groupId}/role/all")
    @GET
    public String[] getGroupRolesForUser(@QueryParam("name") String username, @PathParam("groupId") int groupId) {
        try {
            List<String> roles =  new GroupManagement().getGroupManagementService().getGroupRolesForUser(username, groupId);
            String[] rolesArray = new String[roles.size()];
            return  roles.toArray(rolesArray);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/id/{groupId}/user/all")
    @GET
    public String[] getUsersForGroup(@PathParam("groupId") int groupId) {
        try {
            return  new GroupManagement().getGroupManagementService().getUsersForGroup(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Path("/group/id/{groupId}/device/all")
    @GET
    public Device[] getAllDevicesInGroup(@PathParam("groupId") int groupId){
        try {
            List<Device> devices =  new GroupManagement().getGroupManagementService().getAllDevicesInGroup(groupId);
            Device[] deviceArray = new Device[devices.size()];
            return  devices.toArray(deviceArray);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

}
