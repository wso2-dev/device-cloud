package org.wso2.carbon.device.mgt.iot.common.util;

import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.controlqueue.mqtt.MqttConfig;
import org.wso2.carbon.device.mgt.iot.common.controlqueue.xmpp.XmppConfig;
import org.wso2.carbon.device.mgt.iot.common.util.iotdevice.util.IotDeviceManagementUtil;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ZipUtil {

	public ZipArchive downloadSketch(String owner, String tenantDomain, String deviceType,
	                                 String deviceId, String
			                                 token, String refreshToken)
			throws DeviceManagementException {

		if (owner == null || deviceType == null) {
			throw new DeviceManagementException("Invalid parameters for `owner` or `deviceType`");
		}

		String sep = File.separator;
		String sketchFolder = "repository" + sep + "resources" + sep + "sketches";
		String archivesPath = CarbonUtils.getCarbonHome() + sep + sketchFolder + sep + "archives"
				+ sep + deviceId;
		String templateSketchPath = sketchFolder + sep + deviceType;

		String iotServerIP = System.getProperty("bind.address");
		String iotServerPort = System.getProperty("httpsPort");
		String iotServerServicePort = System.getProperty("httpPort");

		String serverEndPoint = iotServerIP + ":" + iotServerPort;
		String serverServiceEP = iotServerIP + ":" + iotServerServicePort;

		String apimIP =
				DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig().getApiManager()
						.getServerURL();

		int indexOfChar = apimIP.lastIndexOf(File.separator);
		if (indexOfChar != -1) {
			apimIP = apimIP.substring((indexOfChar + 1), apimIP.length());
		}

		String apimGatewayPort =
				DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig().getApiManager()
						.getGatewayPort();

		String apimEndPoint = apimIP + ":" + apimGatewayPort;


		String mqttEndpoint = MqttConfig.getInstance().getMqttQueueEndpoint();
		indexOfChar = mqttEndpoint.lastIndexOf(File.separator);
		if (indexOfChar != -1) {
			mqttEndpoint = mqttEndpoint.substring((indexOfChar + 1), mqttEndpoint.length());
		}

		String xmppEndpoint = XmppConfig.getInstance().getXmppEndpoint();
		indexOfChar = xmppEndpoint.lastIndexOf(File.separator);
		if (indexOfChar != -1) {
			xmppEndpoint = xmppEndpoint.substring((indexOfChar + 1), xmppEndpoint.length());
		}

		indexOfChar = xmppEndpoint.indexOf(":");
		if (indexOfChar != -1) {
			xmppEndpoint = xmppEndpoint.substring(0, indexOfChar);
		}

		xmppEndpoint = xmppEndpoint + ":" + XmppConfig.getInstance().getSERVER_CONNECTION_PORT();

		Map<String, String> contextParams = new HashMap<String, String>();
		contextParams.put("DEVICE_OWNER", owner);
		contextParams.put("DEVICE_ID", deviceId);
		contextParams.put("SERVER_EP", serverEndPoint);
		contextParams.put("SERVER_SERVICE_EP", serverServiceEP);
		contextParams.put("APIM_EP", apimEndPoint);
		contextParams.put("MQTT_EP", mqttEndpoint);
		contextParams.put("XMPP_EP", xmppEndpoint);
		contextParams.put("DEVICE_TOKEN", token);
		contextParams.put("DEVICE_REFRESH_TOKEN", refreshToken);

		ZipArchive zipFile;
		try {
			zipFile = IotDeviceManagementUtil.getSketchArchive(archivesPath, templateSketchPath,
			                                                   contextParams);
		} catch (IOException e) {
			throw new DeviceManagementException("Zip File Creation Failed", e);
		}

		return zipFile;
	}
}
