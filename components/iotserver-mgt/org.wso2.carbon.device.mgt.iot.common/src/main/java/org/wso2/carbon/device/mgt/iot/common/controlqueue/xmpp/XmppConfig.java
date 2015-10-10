package org.wso2.carbon.device.mgt.iot.common.controlqueue.xmpp;

import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.config.server.datasource.ControlQueue;

/**
 * Created by smean-MAC on 7/23/15.
 */
public class XmppConfig {
	private String xmppEndpoint;
	private String xmppUsername;
	private String xmppPassword;
	private boolean isEnabled;

	private static final String XMPP_QUEUE_CONFIG_NAME = "XMPP";
	private final String SERVER_CONNECTION_PORT = "5222";

	private ControlQueue xmppControlQueue;

	private static XmppConfig xmppConfig = new XmppConfig();

	public String getXmppEndpoint() {
		return xmppEndpoint;
	}

	public String getXmppUsername() {
		return xmppUsername;
	}

	public String getXmppPassword() {
		return xmppPassword;
	}

	public ControlQueue getXmppControlQueue() {
		return xmppControlQueue;
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
		xmppEndpoint = xmppControlQueue.getServerURL() + ":" + xmppControlQueue.getPort();
		xmppUsername = xmppControlQueue.getUsername();
		xmppPassword = xmppControlQueue.getPassword();
		isEnabled = xmppControlQueue.isEnabled();
	}

	public static XmppConfig getInstance() {
		return xmppConfig;
	}

	public String getSERVER_CONNECTION_PORT() {
		return SERVER_CONNECTION_PORT;
	}
}
