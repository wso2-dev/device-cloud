package org.wso2.carbon.device.mgt.iot.common.controlqueue.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.common.controlqueue.ControlQueueConnector;
import org.wso2.carbon.device.mgt.iot.common.exception.DeviceControllerException;

import java.util.HashMap;

/**
 * Created by smean-MAC on 7/23/15.
 */
public class XmppServerClient implements ControlQueueConnector {

	private static final Log log = LogFactory.getLog(XmppServerClient.class);

	private String xmppEndpoint;
	private String xmppUsername;
	private String xmppPassword;
	private boolean xmppEnabled = false;

	public XmppServerClient() {
	}

	@Override
	public void initControlQueue() throws DeviceControllerException {
		xmppEndpoint = XmppConfig.getInstance().getControlQueueEndpoint();
		xmppUsername = XmppConfig.getInstance().getControlQueueUsername();
		xmppPassword = XmppConfig.getInstance().getControlQueuePassword();
		xmppEnabled = XmppConfig.getInstance().isEnabled();
	}

	@Override
	public void enqueueControls(HashMap<String, String> deviceControls)
			throws DeviceControllerException {
		if (xmppEnabled) {

		} else {
			log.warn("XMPP <Enabled> set to false in 'devicecloud-config.xml'");
		}
	}
}
