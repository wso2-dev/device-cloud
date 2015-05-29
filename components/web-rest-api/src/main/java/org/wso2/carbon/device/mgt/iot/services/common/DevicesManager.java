package org.wso2.carbon.device.mgt.iot.services.common;

import org.wso2.carbon.device.mgt.common.Device;
import javax.ws.rs.core.GenericEntity;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import org.wso2.carbon.device.mgt.iot.web.register.DeviceManagement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by ayyoobhamza on 5/29/15.
 */
public class DevicesManager {

	@Path("/getDevices")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getDevices(@QueryParam("username") String username)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<Device> devices = deviceManagement.getDevices(username);

		return devices.toArray(new Device[]{});
	}

	@Path("/getDeviceTypes")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public DeviceType[] getDeviceTypes()
			throws DeviceManagementDAOException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<DeviceType> deviceTypes = deviceManagement.getDeviceTypes();


		return deviceTypes.toArray(new DeviceType[]{});


	}
}
