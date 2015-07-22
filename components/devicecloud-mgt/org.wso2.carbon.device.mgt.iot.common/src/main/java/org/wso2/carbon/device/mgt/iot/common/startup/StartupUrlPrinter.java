package org.wso2.carbon.device.mgt.iot.common.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagementDataHolder;
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
		ConfigurationContextService configContextService =
				DeviceManagementDataHolder.getInstance().getConfigurationContextService();
		int httpsPort = CarbonUtils.getTransportPort(configContextService, mgtConsoleTransport);
		int httpsProxyPort =
				CarbonUtils.getTransportProxyPort(configContextService.getServerConfigContext(),
												  mgtConsoleTransport);
		return "https://" + hostName + ":" + httpsPort + "/iotserver";
	}

}
