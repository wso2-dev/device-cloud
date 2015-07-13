package org.wso2.carbon.device.mgt.iot.common.api;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import org.wso2.carbon.device.mgt.iot.common.api.util.DeviceTypes;
import org.wso2.carbon.device.mgt.iot.common.DeviceManagement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

public class DevicesManagerService {

	@Path("/devices/username/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getDevices(@PathParam("username") String username)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<Device> devices = deviceManagement.getDevices(username);

		return devices.toArray(new Device[]{});
	}

	@Path("/devices/count/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public int getDeviceCount(@PathParam("username") String username)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<Device> devices = deviceManagement.getDevices(username);

		if(devices!=null){
			return devices.size();
		}
		return 0;
	}


	@Path("/devices/types/{type}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Device[] getDevicesByType(@PathParam("type") String deviceType)
			throws DeviceManagementException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<Device> devices = deviceManagement.getDevicesByType(deviceType);

		return devices.toArray(new Device[]{});
	}

	@Path("/devices/types/")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public DeviceTypes[] getDeviceTypes()
			throws DeviceManagementDAOException {

		DeviceManagement deviceManagement = new DeviceManagement();

		List<DeviceType> deviceTypes = deviceManagement.getDeviceTypes();
		DeviceTypes dTypes[]= new DeviceTypes[deviceTypes.size()];
		int iter=0;
		for(DeviceType type: deviceTypes){

			DeviceTypes dt =new DeviceTypes();
			dt.setName(type.getName());
			dTypes[iter]=dt;
			iter++;

		}
		return dTypes;


	}


}
