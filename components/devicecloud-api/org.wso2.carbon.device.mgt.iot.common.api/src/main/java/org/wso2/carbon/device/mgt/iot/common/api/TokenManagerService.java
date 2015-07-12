package org.wso2.carbon.device.mgt.iot.common.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

public class TokenManagerService {


	@Path("/{type}")
	@GET
	public String getRefreshToken(@PathParam("type") String deviceType){


		return "";
	}
}
