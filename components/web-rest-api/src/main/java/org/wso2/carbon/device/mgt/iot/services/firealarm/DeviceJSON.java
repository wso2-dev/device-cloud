package org.wso2.carbon.device.mgt.iot.services.firealarm;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceJSON {
    @XmlElement public String owner;
    @XmlElement public String deviceId;
    @XmlElement public String replyMessage;
    @XmlElement public Long time;
    @XmlElement public String key;
    @XmlElement public String value;
}
