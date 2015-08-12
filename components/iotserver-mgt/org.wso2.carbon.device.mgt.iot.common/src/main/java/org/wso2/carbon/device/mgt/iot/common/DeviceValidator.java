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

package org.wso2.carbon.device.mgt.iot.common;

import org.apache.commons.collections.map.LRUMap;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.iot.common.config.server.DeviceCloudConfigManager;

public class DeviceValidator {

    private static LRUMap cache;

    // private static Log log = LogFactory.getLog(DeviceValidator.class);
    static {

        int cacheSize = DeviceCloudConfigManager.getInstance().getDeviceCloudMgtConfig().getDeviceUserValidator().getCacheSize();
        cache = new LRUMap(cacheSize);

    }

    public boolean isExist(String owner, DeviceIdentifier deviceId)
            throws  DeviceManagementException {
        return true;
        //TODO check cache impl
        //return cacheCheck(owner, deviceId);
    }

    private boolean cacheCheck(String owner, DeviceIdentifier deviceId) throws DeviceManagementException{

        String value = (String) cache.get(deviceId);

        if (value != null && !value.isEmpty()) {

            return value.equals(owner);

        }else{
            DeviceManagement deviceManagement = new DeviceManagement();
            boolean status = deviceManagement.isExist(owner, deviceId);
            if (status) {
                addToCache(owner, deviceId );

            }
            return status;

        }


    }

    private void addToCache(String owner, DeviceIdentifier deviceId) {

        cache.put(deviceId, owner);
    }
}
