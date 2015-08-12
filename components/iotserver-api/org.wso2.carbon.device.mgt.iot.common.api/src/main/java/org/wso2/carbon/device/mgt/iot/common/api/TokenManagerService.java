package org.wso2.carbon.device.mgt.iot.common.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.common.apimgt.AccessTokenInfo;
import org.wso2.carbon.device.mgt.iot.common.apimgt.TokenClient;
import org.wso2.carbon.device.mgt.iot.common.exception.AccessTokenException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public class TokenManagerService {
	private static Log log = LogFactory.getLog(TokenManagerService.class);

	@Path("/{device_type}/token")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public AccessTokenInfo getRefreshToken(@QueryParam("refresh_token") String refreshToken,
										   @PathParam("device_type") String deviceType) {

		TokenClient tokenClient = new TokenClient(deviceType);
		try {
			return tokenClient.getAccessToken(refreshToken);

		} catch (AccessTokenException e) {
			log.error(e.getMessage());
		}
		return null;

	}

	public String revokeToken() {


		return null;
	}
}
