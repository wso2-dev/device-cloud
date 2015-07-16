package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Group;
import org.wso2.carbon.device.mgt.common.GroupManagementException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.DeviceManagement;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Date;

public class GroupManagerService {

    @Path("/group/add")
    @PUT
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("owner") String owner) {
        DeviceManagement deviceManagement = new DeviceManagement();
        Group group = new Group();
        group.setName(name);
        group.setOwnerId(owner);
        group.setDateOfCreation(new Date().getTime());
        group.setDateOfLastUpdate(new Date().getTime());
        try {
            deviceManagement.addDeviceGroup(group);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Path("/group/delete/{groupId}")
    @PUT
    public boolean deleteGroup(@PathParam("groupId") int groupId) {
        DeviceManagement deviceManagement = new DeviceManagement();
        try {
            deviceManagement.removeDeviceGroup(groupId);
            return true;
        } catch (GroupManagementException e) {
            e.printStackTrace();
            return false;
        }
    }

}
