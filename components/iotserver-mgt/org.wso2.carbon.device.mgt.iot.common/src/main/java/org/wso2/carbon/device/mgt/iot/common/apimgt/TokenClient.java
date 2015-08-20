
/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.device.mgt.iot.common.apimgt;


import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;
import org.wso2.carbon.device.mgt.iot.common.config.server.datasource.ApiManagerConfig;
import org.wso2.carbon.device.mgt.iot.common.exception.AccessTokenException;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class TokenClient {

	private static Log log = LogFactory.getLog(TokenClient.class);
	private String tokenURL;
	private String grantType;
	private String scope;
	private String appToken ="";
	private String deviceType;

	public TokenClient(String deviceType) {
		this.deviceType = deviceType;

		ApiManagerConfig apiManagerConfig =DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig().getApiManager();

		tokenURL = apiManagerConfig.getAccessTokenURL();
		grantType = DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig()
				.getApiManager()
				.getDeviceGrantType();
		scope = apiManagerConfig.getDeviceScopes();

		appToken = ApisAppClient.getInstance().getBase64EncodedConsumerKeyAndSecret(deviceType);

	}



	public AccessTokenInfo getAccessToken(String username, String deviceId)
			throws AccessTokenException {

		NameValuePair nameValuePairs[] = new NameValuePair[4];
		nameValuePairs[0] = new NameValuePair("grant_type", grantType);
		nameValuePairs[1] = new NameValuePair("device_id", deviceId + "%?%" + deviceType);
		nameValuePairs[2] = new NameValuePair("username", username);
		nameValuePairs[3] = new NameValuePair("scope", scope);

		return getTokenInfo(nameValuePairs);
	}

	public AccessTokenInfo getAccessToken(String refreshToken) throws AccessTokenException {

		NameValuePair nameValuePairs[] = new NameValuePair[3];
		nameValuePairs[0] = new NameValuePair("grant_type", "refresh_token");
		nameValuePairs[1] = new NameValuePair("refresh_token", refreshToken);
		nameValuePairs[2] = new NameValuePair("scope", scope);
		return getTokenInfo(nameValuePairs);


	}

	private AccessTokenInfo getTokenInfo(NameValuePair[] nameValuePairs)
			throws AccessTokenException {
		SSLContext ctx;
		String response = "";
		try {
			ctx = SSLContext.getInstance("TLS");

			ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()},
					 new SecureRandom());
			SSLContext.setDefault(ctx);

			URL url = new URL(tokenURL);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			//System.out.println(conn.getResponseCode());
			conn.disconnect();

			HttpClient httpClient = new HttpClient();

			PostMethod postMethod = new PostMethod(tokenURL);

			for (NameValuePair nameValuePair : nameValuePairs) {
				postMethod.addParameter(nameValuePair);

			}

			postMethod.addRequestHeader("Authorization",
										"Basic " + appToken);
			postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

			httpClient.executeMethod(postMethod);


			response = postMethod.getResponseBodyAsString();
			if(log.isDebugEnabled()) {
				log.debug(response);
			}
			JSONObject jsonObject = new JSONObject(response);

			AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
			accessTokenInfo.setAccess_token(jsonObject.getString("access_token"));
			accessTokenInfo.setRefresh_token(jsonObject.getString("refresh_token"));
			accessTokenInfo.setExpires_in(jsonObject.getInt("expires_in"));
			accessTokenInfo.setToken_type(jsonObject.getString("token_type"));


			return accessTokenInfo;


		} catch (NoSuchAlgorithmException | KeyManagementException | IOException | JSONException
				e) {
			log.error(e.getMessage());
			throw new AccessTokenException("Configuration Error for Access Token Generation");
		}


	}


	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}



