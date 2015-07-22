package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.group.common.Group;
import org.wso2.carbon.device.mgt.group.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.GroupManagement;

import javax.ws.rs.*;
import java.util.Date;

public class GroupManagerService {

    @Path("/group/add")
    @PUT
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("owner") String owner) {
        GroupManagement groupManagement = new GroupManagement();
        Group group = new Group();
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            groupManagement.addDeviceGroup(group);
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
            groupManagement.removeDeviceGroup(groupId);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

}
