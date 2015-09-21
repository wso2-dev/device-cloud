/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.iot.common.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.iot.common.analytics.statistics.IoTUsageStatisticsClient;
import org.wso2.carbon.device.mgt.iot.common.analytics.statistics.IoTUsageStatisticsException;
import org.wso2.carbon.device.mgt.iot.common.analytics.statistics.dto.DeviceUsageDTO;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebService public class StatsManagerService {

    private static Log log = LogFactory.getLog(StatsManagerService.class);

	@Context  //injected response proxy supporting multiple thread
	private HttpServletResponse response;

    private PrivilegedCarbonContext ctx;

    private IoTUsageStatisticsClient getServiceProvider() {
        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        PrivilegedCarbonContext.startTenantFlow();
        ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(tenantDomain, true);
        if (log.isDebugEnabled()) {
            log.debug("Getting thread local carbon context for tenant domain: " + tenantDomain);
        }
        return (IoTUsageStatisticsClient) ctx.getOSGiService(IoTUsageStatisticsClient.class, null);
    }

    private void endTenantFlow() {
        PrivilegedCarbonContext.endTenantFlow();
        ctx = null;
        if (log.isDebugEnabled()) {
            log.debug("Tenant flow ended");
        }
    }

    @Path("/stats/device/type/{type}/identifier/{identifier}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public DeviceUsageDTO[] getDeviceStats(@PathParam("type") String type, @PathParam("identifier") String identifier,
            @FormParam("table") String table, @FormParam("column") String column, @FormParam("username")  String user,
            @FormParam("from") long from, @FormParam("to") long to) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setType(type);
            deviceIdentifier.setId(identifier);
            List<DeviceUsageDTO> stats = this.getServiceProvider().getDeviceStats(table, column, user, identifier,
                    String.valueOf(from), String.valueOf(to));
            return stats.toArray(new DeviceUsageDTO[stats.size()]);
        } catch (IoTUsageStatisticsException e) {
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return null;
        } finally {
            this.endTenantFlow();
        }
    }

}
