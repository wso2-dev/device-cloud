package org.wso2.carbon.device.mgt.iot.common.util;

import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.DeviceManagement;
import org.wso2.carbon.device.mgt.iot.common.controlqueue.mqtt.MqttConfig;
import org.wso2.carbon.device.mgt.iot.common.controlqueue.xmpp.XmppConfig;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ZipUtil {

	public ZipArchive downloadSketch(String owner, String deviceType, String deviceId, String
			token, String refreshToken) throws DeviceManagementException {

		if (owner == null || deviceType == null) {
			throw new DeviceManagementException("Invalid parameters for `owner` or `deviceType`");
		}

		String sep = File.separator;
		String sketchFolder = "repository" + sep + "resources" + sep + "sketches";
		String archivesPath = CarbonUtils.getCarbonHome() + sep + sketchFolder + sep + "archives"
				+ sep + deviceId;
		String templateSketchPath = sketchFolder + sep + deviceType;

		Map<String, String> contextParams = new HashMap<String, String>();
		contextParams.put("DEVICE_OWNER", owner);
		contextParams.put("DEVICE_ID", deviceId);

		String endpoint = MqttConfig.getInstance().getMqttQueueEndpoint();
		int indexOfChar = endpoint.indexOf(File.separator);
		if (indexOfChar != -1) {
			endpoint = endpoint.substring((indexOfChar + 1), endpoint.length());
		}
		contextParams.put("MQTT_EP", endpoint);

		endpoint = XmppConfig.getInstance().getXmppEndpoint();
		indexOfChar = endpoint.indexOf(File.separator);
		if (indexOfChar != -1) {
			endpoint = endpoint.substring((indexOfChar + 1), endpoint.length());
		}
		contextParams.put("XMPP_EP", endpoint);

		contextParams.put("DEVICE_TOKEN", token);
		contextParams.put("DEVICE_REFRESH_TOKEN", refreshToken);

		DeviceManagement deviceManagement = new DeviceManagement();
		ZipArchive zipFile = deviceManagement.getSketchArchive(archivesPath, templateSketchPath,
															   contextParams);

		return zipFile;
	}
}
