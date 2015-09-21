/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
var format = function(context, data, page, area, meta) {
	context = context();
	context.user = data.user;
	return context;
};

var resources = function(page, meta) {
	return {
		js : ['asset-helpers.js', 'navigation.js', 'popover.js', 'jquery.validate.js', 'search.js'],
		css : ['navigation.css','custom-extensions.css']
	};
};

var currentPage = function(navigation, type, search) {
	var asset;

	for (asset in navigation.assets) {
		if (asset == type) {
			navigation.assets[asset].selected = true;
			break;
		}
	}
	navigation.search = search;
	return navigation;
};

var currentActions = function(context) {
	var constants = require("/modules/constants.js");
	var carbonUser = session.get(constants.USER_SESSION_KEY);

	var links = {
		"users": [],
		"policies": [],
		"profiles": [],
		"device-mgt": [],
		"group-mgt": [],
		"store": [],
		"dashboard": [],
		"analytics" : [],
		"events" : []
	};
	var dashboardLink = {
		title: "Go back to Dashboard",
		icon: "fw-left-arrow",
		url: "/iotserver/dashboard"
	};

	var deviceMgtLink = {
		title: "Go back to My Devices",
		icon: "fw-left-arrow",
		url: "/iotserver/devices"
	};

	var groupMgtLink = {
		title: "Go back to Groups",
		icon: "fw-left-arrow",
		url: "/iotserver/groups"
	};

	var storeLink = {
		title: "Go back to Store",
		icon: "fw-left-arrow",
		url: "/iotserver"
	};

	links.users.push(dashboardLink);
	links.policies.push(dashboardLink);
	links.profiles.push(dashboardLink);
	links.events.push(dashboardLink);

	//links.store.push(dashboardLink);
	links.store.push(storeLink);

	links['group-mgt'].push(dashboardLink);
	var groupId = request.getParameter("groupId");
	if (groupId){
		links.analytics.push(groupMgtLink);
		links['device-mgt'].push(groupMgtLink);
	}else{
		links.analytics.push(deviceMgtLink);
		links['device-mgt'].push(dashboardLink);
	}

	if (!carbonUser) {
		//user is not logged in
	}else{
		var permissions = context.permissions;
		//if (permissions.ADD_USER) {
		//    links.users.push({
		//        title: "Add User",
		//        icon: "fw-add-user",
		//        url: "/iotserver/users/add-user"
		//    });
		//}
		if (permissions.ADD_POLICY) {
			links.policies.push({
				title: "Add Policy",
				icon: "fw-policy",
				url: "/iotserver/policies/add-policy"
			});
		}
		//if (permissions.ADD_USER) {
		//    links.profiles.push({
		//        title: "Add Profile",
		//        icon: "fw-settings",
		//        url: "/iotserver/profiles/add-profile"
		//    });
		//}
		if (permissions.ADD_DEVICE) {
			links["device-mgt"].push({
				title: "Add Device",
				icon: "fw-add",
				url: "/iotserver/devices/add-device"
			});
		}
		if (permissions.ADD_DEVICE) {
			links["device-mgt"].push({
				title: "Add Group",
				icon: "fw-add",
				url: "/iotserver/groups/add-group"
			});
			links["group-mgt"].push({
				title: "Add Group",
				icon: "fw-add",
				url: "/iotserver/groups/add-group"
			});
		}
	}// end-if-user

	context.currentActions = links[context.link];
	return context;
};