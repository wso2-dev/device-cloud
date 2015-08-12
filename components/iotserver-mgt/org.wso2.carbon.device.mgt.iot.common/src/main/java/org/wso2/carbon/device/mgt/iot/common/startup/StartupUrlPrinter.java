package org.wso2.carbon.device.mgt.iot.common.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.device.mgt.iot.common.internal.IotDeviceManagementServiceComponent;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.ConfigurationContextService;
import org.wso2.carbon.utils.NetworkUtils;

public class StartupUrlPrinter implements ServerStartupObserver {
	private static final Log log = LogFactory.getLog(StartupUrlPrinter.class);
	@Override
	public void completingServerStartup() {

	}

	@Override
	public void completedServerStartup() {
		log.info("IoT Server URL : " + this.getUrl());


	}
	private String getUrl() {
		// Hostname
		String hostName = "localhost";
		try {
			hostName = NetworkUtils.getMgtHostName();
		} catch (Exception ignored) {
		}
		// HTTPS port
		String mgtConsoleTransport = CarbonUtils.getManagementTransport();
		ConfigurationContextService configContextService = IotDeviceManagementServiceComponent.configurationContextService;

		int httpsPort = CarbonUtils.getTransportPort(configContextService, mgtConsoleTransport);

		return "https://" + hostName + ":" + httpsPort + "/iotserver";
	}

}
