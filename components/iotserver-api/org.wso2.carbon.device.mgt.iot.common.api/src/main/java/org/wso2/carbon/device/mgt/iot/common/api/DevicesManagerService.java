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
import org.wso2.carbon.device.mgt.iot.common.exception.DeviceControllerException;
import org.wso2.carbon.device.mgt.iot.common.sensormgt.SensorDataManager;
import org.wso2.carbon.device.mgt.iot.common.sensormgt.SensorRecord;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebService
public class DevicesManagerService {

    private static Log log = LogFactory.getLog(DevicesManagerService.class);

    @Context  //injected response proxy supporting multiple thread
    private HttpServletResponse response;

    private PrivilegedCarbonContext startTenantFlow() {
        String tenantDomain = CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext()
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

    private List<Device> getActiveDevices(List<Device> devices) {
        List<Device> activeDevices = new ArrayList<>();
        if (devices != null) {
            for (Device device : devices) {
                if (device.getEnrolmentInfo().getStatus().equals(EnrolmentInfo.Status.ACTIVE)) {
                    activeDevices.add(device);
                }
            }
        }
        return activeDevices;
    }

    @Path("/device/user/{username}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDevicesOfUser(@PathParam("username") String username) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getDevicesOfUser(username);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving devices with username:" + username);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/user/{username}/ungrouped")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getUnGroupedDevices(@PathParam("username") String username) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getUnGroupedDevices(username);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving ungrouped devices with username:" + username);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/user/{username}/all/count")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDeviceCount(@PathParam("username") String username) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getDevicesOfUser(username);
            if (devices != null) {
                List<Device> activeDevices = new ArrayList<>();
                for (Device device : devices) {
                    if (device.getEnrolmentInfo().getStatus().equals(EnrolmentInfo.Status.ACTIVE)) {
                        activeDevices.add(device);
                    }
                }
                return Response.ok().entity(activeDevices.size()).build();
            }
            return Response.ok().entity(0).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving device count for username:" + username);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDevice(@PathParam("type") String type, @PathParam("identifier") String identifier) {

        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(identifier);
            deviceIdentifier.setType(type);
            return Response.ok().entity(deviceManagementProviderService.getDevice(deviceIdentifier)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving device with identifier:" + identifier + " of device-type:"
                              + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDeviceTypes() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<DeviceType> deviceTypes = deviceManagementProviderService.getDeviceTypes();
            return Response.ok().entity(deviceTypes).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving all devices-types");
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAllDevices(@PathParam("type") String type) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getAllDevices(type);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving all devices by device-type:" + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAllDevices() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getAllDevices();
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/enrollment/invitation")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response sendEnrolmentInvitation(@FormParam("messageBody") String messageBody,
                                            @FormParam("mailTo") String[] mailTo, @FormParam("ccList") String[] ccList,
                                            @FormParam("bccList") String[] bccList,
                                            @FormParam("subject") String subject,
                                            @FormParam("firstName") String firstName,
                                            @FormParam("enrolmentUrl") String enrolmentUrl,
                                            @FormParam("title") String title, @FormParam("password") String password,
                                            @FormParam("userName") String userName) {
        EmailMessageProperties config = new EmailMessageProperties();
        config.setMessageBody(messageBody);
        config.setMailTo(mailTo);
        config.setCcList(ccList);
        config.setBccList(bccList);
        config.setSubject(subject);
        config.setFirstName(firstName);
        config.setEnrolmentUrl(enrolmentUrl);
        config.setTitle(title);
        config.setUserName(userName);
        config.setPassword(password);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            deviceManagementProviderService.sendEnrolmentInvitation(config);
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while sending enrollment invitation  for user-name:" + userName + " with email: "
                              + mailTo);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/registration/invitation")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response sendRegistrationEmail(@FormParam("messageBody") String messageBody,
                                          @FormParam("mailTo") String[] mailTo, @FormParam("ccList") String[] ccList,
                                          @FormParam("bccList") String[] bccList, @FormParam("subject") String subject,
                                          @FormParam("firstName") String firstName,
                                          @FormParam("enrolmentUrl") String enrolmentUrl,
                                          @FormParam("title") String title, @FormParam("password") String password,
                                          @FormParam("userName") String userName) {
        EmailMessageProperties config = new EmailMessageProperties();
        config.setMessageBody(messageBody);
        config.setMailTo(mailTo);
        config.setCcList(ccList);
        config.setBccList(bccList);
        config.setSubject(subject);
        config.setFirstName(firstName);
        config.setEnrolmentUrl(enrolmentUrl);
        config.setTitle(title);
        config.setUserName(userName);
        config.setPassword(password);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            deviceManagementProviderService.sendRegistrationEmail(config);
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while sending registration email for user-name:" + userName + " with email: " +
                              mailTo);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/config")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getConfiguration(@PathParam("type") String type) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.getConfiguration(type)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving configuration for device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/group/{groupId}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDevices(@PathParam("groupId") int groupId) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getDevices(groupId);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while retrieving all devices with group-id: " + groupId);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/role/{role}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAllDevicesOfRole(@PathParam("role") String roleName) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getAllDevicesOfRole(roleName);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading all devices by role-name:" + roleName);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/name/{name}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDevicesByName(@PathParam("name") String name) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getDevicesByName(name);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading devices with name: " + name);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/status")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateDeviceEnrolmentInfo(@PathParam("type") String type,
                                              @PathParam("identifier") String identifier,
                                              @FormParam("status") EnrolmentInfo.Status status) {
        PrivilegedCarbonContext ctx = startTenantFlow();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            log.error("DeviceManagementProviderService is not initialized.");
            return Response.serverError().build();
        }
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            Device device = deviceManagementProviderService.getDevice(deviceIdentifier);
            deviceManagementProviderService.updateDeviceEnrolmentInfo(device, status);
            return Response.noContent().build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while updating enrollment information with device identifier: " + identifier
                              + " of deviceType: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/status/{status}/all")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getDevicesByStatus(@PathParam("status") EnrolmentInfo.Status status) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            List<Device> devices = deviceManagementProviderService.getDevicesByStatus(status);
            return Response.ok().entity(getActiveDevices(devices)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading devices with status:" + status);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/license")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getLicense(@PathParam("type") String type, @QueryParam("languageCode") String languageCode) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.getLicense(type, languageCode)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading license of device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/license")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addLicense(@PathParam("type") String type, @FormParam("provider") String provider,
                               @FormParam("name") String name, @FormParam("version") String version,
                               @FormParam("language") String language, @FormParam("validFrom") Date validFrom,
                               @FormParam("validTo") Date validTo, @FormParam("text") String text) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            License license = new License();
            license.setProvider(provider);
            license.setName(name);
            license.setVersion(version);
            license.setLanguage(language);
            license.setValidFrom(validFrom);
            license.setValidTo(validTo);
            license.setText(text);
            deviceManagementProviderService.addLicense(type, license);
            return Response.ok().build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while setting device license of device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response modifyEnrollment(@PathParam("type") String type, @PathParam("identifier") String identifier,
                                     @FormParam("name") String name, @FormParam("description") String description,
                                     @FormParam("groupId") int groupId, @FormParam("enrollmentId") int enrollmentId,
                                     @FormParam("dateOfEnrolment") long dateOfEnrolment,
                                     @FormParam("dateOfLastUpdate") long dateOfLastUpdate,
                                     @FormParam("ownership") EnrolmentInfo.OwnerShip ownership,
                                     @FormParam("status") EnrolmentInfo.Status status,
                                     @FormParam("owner") String owner) {

        EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
        enrolmentInfo.setId(enrollmentId);
        enrolmentInfo.setDateOfEnrolment(dateOfEnrolment);
        enrolmentInfo.setDateOfLastUpdate(dateOfLastUpdate);
        enrolmentInfo.setOwnership(ownership);
        enrolmentInfo.setStatus(status);
        enrolmentInfo.setOwner(owner);

        Device device = new Device();
        device.setType(type);
        device.setDeviceIdentifier(identifier);
        device.setName(name);
        device.setDescription(description);
        device.setGroupId(groupId);
        device.setEnrolmentInfo(enrolmentInfo);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok(deviceManagementProviderService.modifyEnrollment(device)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while modifying enrollment in device identifier: " + identifier
                              + " of device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response enrollDevice(@FormParam("type") String type, @FormParam("identifier") String identifier,
                                 @FormParam("name") String name, @FormParam("description") String description,
                                 @FormParam("groupId") int groupId, @FormParam("enrollmentId") int enrollmentId,
                                 @FormParam("dateOfEnrolment") long dateOfEnrolment,
                                 @FormParam("dateOfLastUpdate") long dateOfLastUpdate,
                                 @FormParam("ownership") EnrolmentInfo.OwnerShip ownership,
                                 @FormParam("status") EnrolmentInfo.Status status,
                                 @FormParam("owner") String owner) {

        EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
        enrolmentInfo.setId(enrollmentId);
        enrolmentInfo.setDateOfEnrolment(dateOfEnrolment);
        enrolmentInfo.setDateOfLastUpdate(dateOfLastUpdate);
        enrolmentInfo.setOwnership(ownership);
        enrolmentInfo.setStatus(status);
        enrolmentInfo.setOwner(owner);

        Device device = new Device();
        device.setType(type);
        device.setDeviceIdentifier(identifier);
        device.setName(name);
        device.setDescription(description);
        device.setGroupId(groupId);
        device.setEnrolmentInfo(enrolmentInfo);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.enrollDevice(device)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while enrolling device with device identifier: " + identifier + " of device-type:"
                              + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/tenantconfiguration")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getConfiguration() {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.getConfiguration()).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading tenant configuration.");
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/tenantconfiguration")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response saveConfiguration(@FormParam("tenantConfiguration") TenantConfiguration tenantConfiguration) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.saveConfiguration(tenantConfiguration)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while saving tenant configuration.");
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}")
    @DELETE
    @Consumes("application/json")
    @Produces("application/json")
    public Response disenrollDevice(@PathParam("type") String type, @PathParam("identifier") String identifier) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.disenrollDevice(deviceIdentifier)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while disenrolling device with identifier: " + identifier + " of device-type: "
                              + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/enrolled")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response isEnrolled(@PathParam("type") String type, @PathParam("identifier") String identifier) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.isEnrolled(deviceIdentifier)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading is-enrolled with device identifier: " + identifier
                              + " of device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/active")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response isActive(@PathParam("type") String type, @PathParam("identifier") String identifier) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.isActive(deviceIdentifier)).build();
        } catch (DeviceManagementException e) {
            log.error(
                    "Error occurred while reading is-active with device identifier: " + identifier + " of device-type: "
                            + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/active")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response setActive(@PathParam("type") String type, @PathParam("identifier") String identifier,
                              @FormParam("status") boolean status) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.setActive(deviceIdentifier, status)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while setting active with device identifier: " + identifier + " of device-type: "
                              + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/ownership")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response setOwnership(@PathParam("type") String type, @PathParam("identifier") String identifier,
                                 @FormParam("ownership") String ownership) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.setOwnership(deviceIdentifier, ownership))
                    .build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while setting ownership with device identifier: " + identifier +
                              " of deviceType: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/status")
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public Response setStatus(@PathParam("type") String type, @PathParam("identifier") String identifier,
                              @FormParam("owner") String owner, @FormParam("status") EnrolmentInfo.Status status) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.setStatus(deviceIdentifier, owner, status))
                    .build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while setting status with device identifier: " + identifier + " of device-type: "
                              + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/claimable")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response isClaimable(@PathParam("type") String type, @PathParam("identifier") String identifier) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(identifier);
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(deviceManagementProviderService.isClaimable(deviceIdentifier)).build();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while reading claimable with device identifier: " + identifier
                              + " of device-type: " + type);
            return Response.serverError().build();
        } finally {
            endTenantFlow();
        }
    }


    @Path("/device/type/{type}/identifier/{identifier}/sensor/{sensorName}")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response setSensorValue(@PathParam("type") String type, @PathParam("identifier") String deviceId,
                                   @PathParam("sensorName") String sensorName,
                                   @HeaderParam("sensorValue") String sensorValue) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            boolean result = SensorDataManager.getInstance().setSensorRecord(deviceId, sensorName, sensorValue,
                                                                        timeInMillis);
            return Response.ok().entity(result).build();
        } finally {
            endTenantFlow();
        }
    }

    @Path("/device/type/{type}/identifier/{identifier}/sensor/{sensorName}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getSensorValue(@PathParam("type") String type, @PathParam("identifier") String deviceId,
                                   @PathParam("sensorName") String sensorName,
                                   @HeaderParam("defaultValue") String defaultValue) {
        try {
            PrivilegedCarbonContext ctx = startTenantFlow();
            DeviceManagementProviderService deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
            if (deviceManagementProviderService == null) {
                log.error("DeviceManagementProviderService is not initialized.");
                return Response.serverError().build();
            }
            return Response.ok().entity(SensorDataManager.getInstance().getSensorRecord(deviceId, sensorName)).build();
        } catch (DeviceControllerException e) {
            log.error("Error on reading sensor value: " + e.getMessage());
            if (defaultValue != null) {
                long timeInMillis = Calendar.getInstance().getTimeInMillis();
                return Response.ok().entity(new SensorRecord(defaultValue, timeInMillis)).build();
            } else {
                log.error("Error on retrieving sensor data on sensor-name:" + sensorName +
                                  " for device with identifier:" + deviceId + " of device-type:" + type);
                return Response.serverError().build();
            }
        } finally {
            endTenantFlow();
        }
    }
}