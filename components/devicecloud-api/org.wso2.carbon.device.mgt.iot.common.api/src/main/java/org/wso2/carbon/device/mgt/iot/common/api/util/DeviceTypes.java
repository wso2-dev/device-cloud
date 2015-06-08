package org.wso2.carbon.device.mgt.iot.common.api.util;

import org.wso2.carbon.device.mgt.common.Feature;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class DeviceTypes {


	private String name;


	public DeviceTypes() {
	}

	@XmlElement
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
