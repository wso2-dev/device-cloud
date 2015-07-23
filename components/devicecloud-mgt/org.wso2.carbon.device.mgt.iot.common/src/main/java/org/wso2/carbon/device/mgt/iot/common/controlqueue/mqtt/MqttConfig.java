package org.wso2.carbon.device.mgt.iot.common.controlqueue.mqtt;

import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.config.server.datasource.ControlQueue;

public class MqttConfig {

	private String controlQueueEndpoint;
	private String controlQueueUsername;
	private String controlQueuePassword;
	private boolean isEnabled;

	private static final String MQTT_QUEUE_CONFIG_NAME = "MQTT";
	private ControlQueue mqttControlQueue;

	private static MqttConfig mqttConfig = new MqttConfig();

	public String getControlQueueUsername() {
		return controlQueueUsername;
	}

	public String getControlQueueEndpoint() {
		return controlQueueEndpoint;
	}

	public String getControlQueuePassword() {
		return controlQueuePassword;
	}

	public boolean isEnabled() {
		return isEnabled;
	}


	private MqttConfig() {

		mqttControlQueue = DeviceCloudConfigManager.getInstance().getControlQueue(
				MQTT_QUEUE_CONFIG_NAME);
		controlQueueEndpoint = mqttControlQueue.getServerURL() + ":" + mqttControlQueue.getPort();
		controlQueueUsername = mqttControlQueue.getUsername();
		controlQueuePassword = mqttControlQueue.getPassword();
		isEnabled = mqttControlQueue.isEnabled();
	}

	public static MqttConfig getInstance() {
		return mqttConfig;
	}

}
