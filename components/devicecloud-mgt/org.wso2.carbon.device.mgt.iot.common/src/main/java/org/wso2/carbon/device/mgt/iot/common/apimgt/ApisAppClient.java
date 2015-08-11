package org.wso2.carbon.device.mgt.iot.common.apimgt;

import org.wso2.carbon.device.mgt.iot.common.config.devicetype.datasource.IotDeviceTypeConfig;
import java.util.HashMap;
import java.util.List;

public class ApisAppClient {

	private static HashMap<String, String> deviceTypeToApiAppMap = new HashMap<String, String>();
	private static ApisAppClient instance =null;
	public static ApisAppClient getInstance(){

		if(instance==null){
			instance= new ApisAppClient();

		}
		return instance;
	}

	private ApisAppClient() {


	}

	public String getBase64EncodedConsumerKeyAndSecret(String deviceType) {
		return deviceTypeToApiAppMap.get(deviceType);
	}

	public void setBase64EncodedConsumerKeyAndSecret(List<IotDeviceTypeConfig> iotDeviceTypeConfigList) {

		//read from API manager
		//TODO

		for (IotDeviceTypeConfig iotDeviceTypeConfig : iotDeviceTypeConfigList) {
			String deviceType = iotDeviceTypeConfig.getType();
			String deviceTypeApiApplicationName = iotDeviceTypeConfig.getApiApplicationName();
			String base64EncodedString = "";
			deviceTypeToApiAppMap.put(deviceType, base64EncodedString);
		}



	}
}
