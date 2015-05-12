/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.*;
import org.wso2.carbon.device.mgt.common.spi.DeviceManager;
import org.wso2.carbon.device.mgt.iot.DataSourceListener;
import org.wso2.carbon.device.mgt.iot.config.IotDeviceConfigurationManager;
import org.wso2.carbon.device.mgt.iot.config.IotDeviceManagementConfig;
import org.wso2.carbon.device.mgt.iot.config.datasource.IotDataSourceConfig;
import org.wso2.carbon.device.mgt.iot.dao.IotDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.iot.impl.arduino.ArduinoDeviceManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BundleActivator of IOTDeviceManagement component.
 */
public class IoTDeviceManagementBundleActivator implements BundleActivator, BundleListener {

	private ServiceRegistration arduinoServiceRegRef;
	//private ServiceRegistration raspberryServiceRegRef;
	

	private static List<DataSourceListener> dataSourceListeners =
			new ArrayList<DataSourceListener>();

	private static final String SYMBOLIC_NAME_DATA_SOURCE_COMPONENT =
			"org.wso2.carbon.ndatasource.core";
	private static final Log log = LogFactory.getLog(IoTDeviceManagementBundleActivator.class);

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Activating Mobile Device Management Service bundle");
			}
			bundleContext.addBundleListener(this);

            /* Initialize the data source configuration */
            IotDeviceConfigurationManager.getInstance().initConfig();
            IotDeviceManagementConfig config = IotDeviceConfigurationManager.getInstance()
                    .getIotDeviceManagementConfig();
            Map<String, IotDataSourceConfig> iotDataSourceConfigMap =
                    config.getIotDeviceMgtRepository().getIotDataSourceConfigMap();
            IotDeviceManagementDAOFactory.setIotDataSourceConfigMap(iotDataSourceConfigMap);

            arduinoServiceRegRef =
                    bundleContext.registerService(DeviceManager.class.getName(),
                            new ArduinoDeviceManager(), null);
            
            

			if (log.isDebugEnabled()) {
				log.debug("IOT Device Management Service bundle is activated");
			}
		} catch (Throwable e) {
			log.error("Error occurred while activating Iot Device Management bundle", e);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Deactivating Iot Device Management Service");
		}
		try {
			arduinoServiceRegRef.unregister();
			
			

			bundleContext.removeBundleListener(this);
		} catch (Throwable e) {
			log.error("Error occurred while de-activating Iot Device Management bundle", e);
		}
	}

	@Override
	public void bundleChanged(BundleEvent bundleEvent) {
		int eventType = bundleEvent.getType();
		String bundleSymbolicName = bundleEvent.getBundle().getSymbolicName();

		if (SYMBOLIC_NAME_DATA_SOURCE_COMPONENT.equals(bundleSymbolicName) &&
		    eventType == BundleEvent.STARTED) {
			for (DataSourceListener listener : this.getDataSourceListeners()) {
				listener.notifyObserver();
			}
		}
	}

	public static void registerDataSourceListener(DataSourceListener listener) {
		dataSourceListeners.add(listener);
	}

	private List<DataSourceListener> getDataSourceListeners() {
		return dataSourceListeners;
	}


}
