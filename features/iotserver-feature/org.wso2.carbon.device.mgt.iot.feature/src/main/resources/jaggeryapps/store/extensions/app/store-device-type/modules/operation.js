/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var operationModule = function () {
    var log = new Log("modules/operation.js");

    var constants = require("constants.js");
    var utility = require("utility.js").utility;

    var hostname = utility.getIoTServerConfig("IoTMgtHost");
    var carbonHttpsServletTransport = "https://" + hostname + ":9443";

    var server = require('store').server;
    var user = server.current(session);

    var publicMethods = {};
    var privateMethods = {};

    publicMethods.getOperations = function (deviceType) {
        return [{name: "Bulb Status", description: "0:off 1:on", operation: "bulb"}];
    };

    publicMethods.handleOperation = function (deviceType, operation, deviceId, value) {
        //URL: POST https://localhost:9443/devicecloud/group_manager/group
        var endPoint = carbonHttpsServletTransport + "/controller/" + operation + "/" + ((value == 1) ? "ON" : "OFF");
        var header = '{"owner":"' + user.username + '","deviceId":"' + deviceId + '","protocol":"xmpp"}';
        return post(endPoint, {}, JSON.parse(header), "json");
    };

    return publicMethods;
}();
