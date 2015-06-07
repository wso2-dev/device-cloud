package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import org.wso2.carbon.device.mgt.iot.common.devicecloud.DeviceManagement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

public class DevicesManagerService {

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

	@Path("/getDevicesByType")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getDevicesByType(@QueryParam("type") String deviceType)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<Device> devices = deviceManagement.getDevicesByType(deviceType);

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
