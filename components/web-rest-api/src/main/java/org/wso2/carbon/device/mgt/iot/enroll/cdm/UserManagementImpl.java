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

package org.wso2.carbon.device.mgt.iot.enroll.cdm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.common.IOTAPIException;
import org.wso2.carbon.device.mgt.iot.enroll.UserManagement;
import org.wso2.carbon.device.mgt.iot.enroll.cdm.util.IotApiUtil;
import org.wso2.carbon.device.mgt.user.common.User;
import org.wso2.carbon.device.mgt.user.common.UserManagementException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * @author ayyoobhamza
 *
 */
public class UserManagementImpl implements UserManagement{

	
	private static Log log=LogFactory.getLog(UserManagementImpl.class);
	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#addNewUser(org.wso2.carbon.device.mgt.iot.user.User)
	 */
    @Override
    public boolean addNewUser(User user) {
	    // TODO Auto-generated method stub
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#removeUser(java.lang.String)
	 */
    @Override
    public boolean removeUser(String username) {
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#updateUser(org.wso2.carbon.device.mgt.iot.user.User)
	 */
    @Override
    public boolean updateUser(User user) {
	    // TODO Auto-generated method stub
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#getUser(java.lang.String)
	 */
    @Override
    public User getUser(String username) throws IOTAPIException{
    	try {
    		
	       return IotApiUtil.getUserManagementService().getUser(username, MultitenantConstants.SUPER_TENANT_ID);
        } catch (UserManagementException e) {
        	String error="CDM configuration error on retreving user";
        	if(log.isDebugEnabled()){
	        	
	        	log.debug(error+ e);
	        }
        	throw new IOTAPIException(error);
        }
	   
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#isAuthenticated(java.lang.String, java.lang.String)
	 */
    @Override
    public boolean isAuthenticated(String username, String password) {
	    
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#getAnonymousUserName()
	 */
    @Override
    public String getAnonymousUserName() {
	    // TODO Auto-generated method stub
	    return "org.wso2.iot.anonymous";
    }

	/* (non-Javadoc)
	 * @see org.wso2.carbon.device.mgt.iot.enroll.UserManagement#isExist(java.lang.String)
	 */
    @Override
    public boolean isExist(String username) {
    	
	    return false;
    }

}
