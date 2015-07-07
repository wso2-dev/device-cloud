package org.wso2.carbon.device.mgt.iot.common.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public class UsersManagerService {

	private static Log log = LogFactory.getLog(UsersManagerService.class);

	@Path("/users/count")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public int getUserCount() {

		try {
			String[] users = getUserStoreManager().listUsers("", -1);
			if (users == null) {
				return 0;
			}
			return users.length;
		} catch (UserStoreException e) {
			String msg = "Error occurred while retrieving the list of users that exist within the current tenant";
			log.error(msg, e);
			return 0;
		}
	}


	private static UserStoreManager getUserStoreManager() throws UserStoreException{

		RealmService realmService;
		UserStoreManager userStoreManager;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

			if (realmService == null) {
				String msg = "Realm service not initialized";
				log.error(msg);
				throw new UserStoreException(msg);
			}
			int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
			userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
		} catch (UserStoreException e) {
			String msg = "Error occurred while retrieving current user store manager";
			log.error(msg, e);
			throw new UserStoreException(msg, e);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		return userStoreManager;
	}
}
