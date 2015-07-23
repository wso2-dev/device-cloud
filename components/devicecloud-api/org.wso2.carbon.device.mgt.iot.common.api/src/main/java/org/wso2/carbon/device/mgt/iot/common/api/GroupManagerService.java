package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;

import javax.ws.rs.*;
import java.util.Date;

public class GroupManagerService {

    @Path("/group/add")
    @POST
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("owner") String owner) {
        GroupManagement groupManagement = new GroupManagement();
        Group group = new Group();
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            return groupManagement.createGroup(group);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/update/{groupId}")
    @POST
    public boolean updateGroup(@PathParam("groupId") int groupId, @QueryParam("name") String name, @QueryParam("owner") String owner) {
        GroupManagement groupManagement = new GroupManagement();
        Group group = new Group();
        group.setId(groupId);
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            groupManagement.updateGroup(group);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/delete/{groupId}")
    @DELETE
    public boolean deleteGroup(@PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement();
        try {
            return groupManagement.deleteGroup(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/get/{groupId}")
    @GET
    public Group getGroup(@PathParam("groupId") int groupId) {
        GroupManagement groupManagement = new GroupManagement();
        try {
            return groupManagement.getGroupById(groupId);
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return null;
        }
    }


}
