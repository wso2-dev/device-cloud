package org.wso2.carbon.device.mgt.iot.services.firealarm;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {})
@XmlRootElement(name="owner")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceJSON {
    @XmlElement(name = "owner", required = true) public String owner;
    @XmlElement(name = "deviceId", required = true) public String deviceId;
    @XmlElement(name = "reply", required = true) public String reply;
    @XmlElement(name = "time", required = false) public Long time;
    @XmlElement(name = "key", required = false) public String key;
    @XmlElement(name = "value", required = false) public String value;
}
