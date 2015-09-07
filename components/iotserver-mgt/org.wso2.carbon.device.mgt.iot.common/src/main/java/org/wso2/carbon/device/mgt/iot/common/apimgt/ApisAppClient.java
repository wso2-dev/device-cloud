package org.wso2.carbon.device.mgt.iot.common.apimgt;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.device.mgt.iot.common.config.devicetype.datasource.IotDeviceTypeConfig;
import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.config.server.datasource.ApiManagerConfig;
import org.apache.commons.codec.binary.Base64;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ApisAppClient {

	private static HashMap<String, String> deviceTypeToApiAppMap = new HashMap<String, String>();
	private static ApisAppClient instance =null;

	private String loginEndpoint;
	private String subscriptionListEndpoint;
	private static Log log = LogFactory.getLog(ApisAppClient.class);
	private boolean isEnabled;

	public static ApisAppClient getInstance(){

		if(instance==null){
			instance= new ApisAppClient();

		}
		return instance;
	}

	private ApisAppClient() {
		ApiManagerConfig apiManagerConfig =DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig().getApiManager();
		String serverUrl=apiManagerConfig.getServerURL();
		String serverPort=apiManagerConfig.getServerPort();
		isEnabled = apiManagerConfig.isEnabled();

		String loginURL = serverUrl+":"+serverPort+apiManagerConfig.getLoginURL();
		loginEndpoint= loginURL+"?action=login&username="+apiManagerConfig.getUsername()+"&password="+apiManagerConfig.getPassword();

		String subscriptionListUrl=serverUrl+":"+serverPort+apiManagerConfig.getSubscriptionListURL();
		subscriptionListEndpoint=subscriptionListUrl+"?action=getAllSubscriptions";
	}

	public String getBase64EncodedConsumerKeyAndSecret(String deviceType) {
		if(!isEnabled) return null;
		return deviceTypeToApiAppMap.get(deviceType);
	}

	public void setBase64EncodedConsumerKeyAndSecret(List<IotDeviceTypeConfig> iotDeviceTypeConfigList) {
		if(!isEnabled) return;
		HttpClient httpClient = new HttpClient();

		PostMethod postMethod = new PostMethod(loginEndpoint);
		JSONObject apiJsonResponse;
		try {
			httpClient.executeMethod(postMethod);
			String response = postMethod.getResponseBodyAsString();
			if(log.isDebugEnabled()) {
				log.debug(response);
			}
			JSONObject jsonObject = new JSONObject(response);


			boolean apiError = jsonObject.getBoolean("error");
			if(!apiError){
				String cookie = postMethod.getResponseHeader("Set-Cookie").getValue().split(";")[0];
				GetMethod getMethod=new GetMethod(subscriptionListEndpoint);
				getMethod.setRequestHeader("cookie", cookie);
				httpClient.executeMethod(getMethod);
				response = getMethod.getResponseBodyAsString();


				if(log.isDebugEnabled()) {
					log.debug(response);
				}
				apiJsonResponse = new JSONObject(response);
				apiError=apiJsonResponse.getBoolean("error");
				if(apiError){
					log.error("invalid subscription endpoint "+subscriptionListEndpoint);
					return;
				}
			}else{
				log.error("invalid access for login endpoint " +loginEndpoint);
				return;
			}

		} catch (IOException | JSONException
		e) {
			log.error("Invalid Api Endpoint loginEndpoint= "+ loginEndpoint +" and subscription api endpoint= "+subscriptionListEndpoint);
			return;
		}


		JSONArray jsonSubscriptions=apiJsonResponse.getJSONObject("subscriptions").getJSONArray("applications");

		HashMap<String,String> subscriptionMap= new HashMap<>();
		for(int n = 0; n < jsonSubscriptions.length(); n++)
		{

			JSONObject object = jsonSubscriptions.getJSONObject(n);
			String appName =object.getString("name");
			String prodConsumerKey= object.getString("prodConsumerKey");
			String prodConsumerSecret = object.getString("prodConsumerSecret");

			subscriptionMap.put(appName, new String(Base64.encodeBase64((prodConsumerKey + ":" + prodConsumerSecret).getBytes())));

		}

		for (IotDeviceTypeConfig iotDeviceTypeConfig : iotDeviceTypeConfigList) {
			String deviceType = iotDeviceTypeConfig.getType();
			String deviceTypeApiApplicationName = iotDeviceTypeConfig.getApiApplicationName();


			String base64EncodedString = subscriptionMap.get(deviceTypeApiApplicationName);
			if(base64EncodedString!=null&& base64EncodedString.length()!=0){

				deviceTypeToApiAppMap.put(deviceType, base64EncodedString);

			}
		}



	}
}
