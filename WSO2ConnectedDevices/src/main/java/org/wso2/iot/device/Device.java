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

package org.wso2.iot.device;

import java.io.Serializable;

/**
 * Description of a device
 */
public class Device implements Serializable{
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
	private String deviceId;
	private String name;
	private String type;
	private String model;
	private Long enrolledOn;
	private String desciption;
	private String owner;
	private String token;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String id) {
		this.deviceId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Long getEnrolledOn() {
		return enrolledOn;
	}
	
	public void setEnrolledOn(Long timestamp){
		enrolledOn=timestamp;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setToken(String token) {
		this.token=token;
	}

	public String getToken() {

		return token;
	}

}
