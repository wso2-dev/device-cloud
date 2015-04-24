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

package org.wso2.iot.enroll;

import org.wso2.iot.device.Device;
import org.wso2.iot.user.User;

/**
 * @author ayyoobhamza
 *
 */
public class DeviceManagementImpl implements DeviceManagement {

	
    @Override
    public void addNewDevice(Device device, User user) {
	   	
	    
    }

	
    @Override
    public void removeDevice(Device device) {
	    // TODO Auto-generated method stub
	    
    }

	
    @Override
    public void updateToken(Device device, String token) {
	    // TODO Auto-generated method stub
	    
    }

	
    @Override
    public String getToken(Device device, User user) {
	    // TODO Auto-generated method stub
	    return null;
    }



    @Override
    public Device getDevice(String deviceId) {
	    // TODO Auto-generated method stub
	    return null;
    }

	
}
