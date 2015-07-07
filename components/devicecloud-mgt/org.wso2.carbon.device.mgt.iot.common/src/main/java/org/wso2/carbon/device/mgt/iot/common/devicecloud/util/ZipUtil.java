package org.wso2.carbon.device.mgt.iot.common.devicecloud.util;

import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.devicecloud.DeviceManagement;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ZipUtil {

	public ZipArchive downloadSketch(String owner, String deviceType, String deviceId, String token,String refreshToken)
			throws DeviceManagementException {

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
		contextParams.put("DEVICE_TOKEN", token);
		contextParams.put("DEVICE_REFRESH_TOKEN", refreshToken);

		DeviceManagement deviceManagement = new DeviceManagement();
		ZipArchive zipFile = deviceManagement.getSketchArchive(archivesPath, templateSketchPath,
															   contextParams);

		return zipFile;
	}
}
