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
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.common.PolicyAdministratorPoint;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.PolicyMonitoringTaskException;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceData;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.policy.mgt.core.task.TaskScheduleService;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Collections;

@WebService
@Path("/policies")
public class PolicyManagementService {

    private static Log log = LogFactory.getLog(PolicyManagementService.class);

    @Context  //Injected response proxy supporting multiple thread
    private HttpServletResponse response;

    private PrivilegedCarbonContext startTenantFlow() {
        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(tenantDomain, true);
        if (log.isDebugEnabled()) {
            log.debug("Getting thread local carbon context for tenant domain: " + tenantDomain);
        }
        return ctx;
    }

    private void endTenantFlow() {
        PrivilegedCarbonContext.endTenantFlow();
        if (log.isDebugEnabled()) {
            log.debug("Tenant flow ended");
        }
    }

    @POST
    @Path("/inactive")
    @Produces("application/json")
    public Response addPolicy(Policy policy) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            pap.addPolicy(policy);
            if (log.isDebugEnabled()) {
                log.debug("Policy has been added successfully.");
            }
            return Response.ok().build();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @POST
    @Path("/active")
    @Produces("application/json")
    public Response addActivePolicy(Policy policy) {
        policy.setActive(true);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            pap.addPolicy(policy);
            if (log.isDebugEnabled()) {
                log.debug("Policy has been added successfully.");
            }
            return Response.ok().build();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @GET
    @Produces("application/json")
    @Path("/")
    public Response getAllPolicies() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint policyAdministratorPoint = policyManagerService.getPAP();
            List<Policy> policies = policyAdministratorPoint.getPolicies();
            return Response.serverError().entity(policies).build();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            return Response.serverError().entity(Collections.emptyList()).build();
        } finally {
            endTenantFlow();
        }
    }

    @GET
    @Produces("application/json")
    @Path("/{id}")
    public Response getPolicy(@PathParam("id") int policyId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint policyAdministratorPoint = policyManagerService.getPAP();
            Policy policy = policyAdministratorPoint.getPolicy(policyId);
            if (policy != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Sending policy for ID " + policyId);
                }
                return Response.ok().entity(policy).build();
            } else {
                log.error("Policy for ID " + policyId + " not found.");
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @GET
    @Path("/count")
    public Response getPolicyCount() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint policyAdministratorPoint = policyManagerService.getPAP();
            return Response.ok().entity(policyAdministratorPoint.getPolicyCount()).build();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            response.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return Response.serverError().entity(-1).build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces("application/json")
    public Response updatePolicy(Policy policy, @PathParam("id") int policyId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            org.wso2.carbon.policy.mgt.common.Policy previousPolicy = pap.getPolicy(policyId);
            policy.setProfile(pap.getProfile(previousPolicy.getProfileId()));
            policy.setPolicyName(previousPolicy.getPolicyName());
            pap.updatePolicy(policy);
            if (log.isDebugEnabled()) {
                log.debug("Policy with ID " + policyId + " has been updated successfully.");
            }
            return Response.noContent().build();
        } catch (PolicyManagementException e) {
            String error = "Policy Management related exception";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Path("/priorities")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updatePolicyPriorities(List<Policy> priorityUpdatedPolicies) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            boolean policiesUpdated = pap.updatePolicyPriorities(priorityUpdatedPolicies);
            if (policiesUpdated) {
                if (log.isDebugEnabled()) {
                    log.debug("Policy Priorities successfully updated.");
                }
                return Response.noContent().build();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Policy priorities did not update. Bad Request.");
                }
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (PolicyManagementException e) {
            String error = "Exception in updating policy priorities.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    public Response deletePolicy(@PathParam("id") int policyId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            org.wso2.carbon.policy.mgt.common.Policy policy = pap.getPolicy(policyId);
            boolean policyDeleted = pap.deletePolicy(policy);
            if (policyDeleted) {
                if (log.isDebugEnabled()) {
                    log.debug("Policy by id:" + policyId + " has been successfully deleted.");
                }
                return Response.noContent().build();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Policy by id:" + policyId + " does not exist.");
                }
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (PolicyManagementException e) {
            String error = "Exception in deleting policy by id:" + policyId;
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Produces("application/json")
    @Path("/activate/{id}")
    public Response activatePolicy(@PathParam("id") int policyId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            pap.activatePolicy(policyId);
            if (log.isDebugEnabled()) {
                log.debug("Policy by id:" + policyId + " has been successfully activated.");
            }
            return Response.noContent().build();
        } catch (PolicyManagementException e) {
            String error = "Exception in activating policy by id:" + policyId;
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Produces("application/json")
    @Path("/inactivate/{id}")
    public Response inactivatePolicy(@PathParam("id") int policyId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            pap.inactivatePolicy(policyId);
            if (log.isDebugEnabled()) {
                log.debug("Policy by id:" + policyId + " has been successfully inactivated.");
            }
            return Response.noContent().build();
        } catch (PolicyManagementException e) {
            String error = "Exception in inactivating policy by id:" + policyId;
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Produces("application/json")
    @Path("/publish-changes")
    public Response applyChanges() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            PolicyAdministratorPoint pap = policyManagerService.getPAP();
            pap.publishChanges();
            if (log.isDebugEnabled()) {
                log.debug("Changes have been successfully updated.");
            }
            return Response.noContent().build();
        } catch (PolicyManagementException e) {
            String error = "Exception in applying changes.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @POST
    @Path("/start-task/{milliseconds}")
    public Response startTaskService(@PathParam("milliseconds") int monitoringFrequency) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            TaskScheduleService taskScheduleService = policyManagerService.getTaskScheduleService();
            taskScheduleService.startTask(monitoringFrequency);
            if (log.isDebugEnabled()) {
                log.debug("Policy monitoring service started successfully.");
            }
            return Response.ok().build();
        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @PUT
    @Path("/update-task/{milliseconds}")
    public Response updateTaskService(@PathParam("milliseconds") int monitoringFrequency) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            TaskScheduleService taskScheduleService = policyManagerService.getTaskScheduleService();
            taskScheduleService.updateTask(monitoringFrequency);
            if (log.isDebugEnabled()) {
                log.debug("Policy monitoring service updated successfully.");
            }
            return Response.noContent().build();
        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @GET
    @Path("/stop-task")
    public Response stopTaskService() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            TaskScheduleService taskScheduleService = policyManagerService.getTaskScheduleService();
            taskScheduleService.stopTask();
            if (log.isDebugEnabled()) {
                log.debug("Policy monitoring service stopped successfully.");
            }
            return Response.noContent().build();
        } catch (PolicyMonitoringTaskException e) {
            String error = "Policy Management related exception.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @GET
    @Path("/{type}/{id}")
    public Response getComplianceDataOfDevice(@PathParam("id") String deviceId, @PathParam("type") String deviceType) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
            if (policyManagerService == null) {
                log.error("Policy Management service not initialized");
                return Response.serverError().build();
            }
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setType(deviceType);
            deviceIdentifier.setId(deviceId);
            ComplianceData complianceData = policyManagerService.getDeviceCompliance(deviceIdentifier);
            return Response.ok().entity(complianceData).build();
        } catch (PolicyComplianceException e) {
            String error = "Error occurred while getting the compliance data.";
            log.error(error, e);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }
}
