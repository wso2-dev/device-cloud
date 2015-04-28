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

// TODO: Auto-generated Javadoc
/**
 * Description of a device.
 */
public class Device implements Serializable{
	
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
	
	/** The device id. */
	private String deviceId;
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;
	
	/** The model. */
	private String model;
	
	/** The enrolled on. */
	private Long enrolledOn;
	
	/** The desciption. */
	private String desciption;
	
	/** The owner. */
	private String owner;
	
	/** The token. */
	private String token;

	/**
	 * Gets the device id.
	 *
	 * @return the device id
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets the device id.
	 *
	 * @param id the new device id
	 */
	public void setDeviceId(String id) {
		this.deviceId = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Gets the enrolled on.
	 *
	 * @return the enrolled on
	 */
	public Long getEnrolledOn() {
		return enrolledOn;
	}
	
	/**
	 * Sets the enrolled on.
	 *
	 * @param timestamp the new enrolled on
	 */
	public void setEnrolledOn(Long timestamp){
		enrolledOn=timestamp;
	}

	/**
	 * Gets the desciption.
	 *
	 * @return the desciption
	 */
	public String getDesciption() {
		return desciption;
	}

	/**
	 * Sets the desciption.
	 *
	 * @param desciption the new desciption
	 */
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Sets the token.
	 *
	 * @param token the new token
	 */
	public void setToken(String token) {
		this.token=token;
	}

	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {

		return token;
	}

}
