package org.wso2.carbon.device.mgt.iot.common.api.util;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public class GroupManagerService {

    @Path("/device/register")
    @PUT
    public boolean addGroup(@QueryParam("name") String name, @QueryParam("owner") String owner) {
        return true;
    }
}
