package org.wso2.carbon.device.mgt.iot.common.controlqueue.xmpp;

import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.config.server.datasource.ControlQueue;

/**
 * Created by smean-MAC on 7/23/15.
 */
public class XmppConfig {
	private String controlQueueEndpoint;
	private String controlQueueUsername;
	private String controlQueuePassword;
	private boolean isEnabled;

	private static final String XMPP_QUEUE_CONFIG_NAME = "XMPP";
	private ControlQueue xmppControlQueue;

	private static XmppConfig xmppConfig = new XmppConfig();

	public String getControlQueueEndpoint() {
		return controlQueueEndpoint;
	}

	public String getControlQueueUsername() {
		return controlQueueUsername;
	}

	public String getControlQueuePassword() {
		return controlQueuePassword;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public static String getXmppQueueConfigName() {
		return XMPP_QUEUE_CONFIG_NAME;
	}

	private XmppConfig() {
		xmppControlQueue = DeviceCloudConfigManager.getInstance().getControlQueue(
				XMPP_QUEUE_CONFIG_NAME);
		controlQueueEndpoint = xmppControlQueue.getServerURL() + ":" + xmppControlQueue.getPort();
		controlQueueUsername = xmppControlQueue.getUsername();
		controlQueuePassword = xmppControlQueue.getPassword();
		isEnabled = xmppControlQueue.isEnabled();
	}

	public static XmppConfig getInstance() {
		return xmppConfig;
	}
}
