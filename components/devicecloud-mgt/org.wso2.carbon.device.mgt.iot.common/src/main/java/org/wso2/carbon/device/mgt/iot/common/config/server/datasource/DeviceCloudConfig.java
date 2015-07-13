
package org.wso2.carbon.device.mgt.iot.common.config.server.datasource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeviceCloudConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeviceCloudConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DataStores" type="{}DataStoresConfig"/>
 *         &lt;element name="ControlQueues" type="{}ControlQueuesConfig"/>
 *         &lt;element name="Security" type="{}SecurityConfig"/>
 *         &lt;element name="ApiManager" type="{}ApiManagerConfig"/>
 *         &lt;element name="DeviceUserValidatorCacheSize" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceCloudConfig", propOrder = {
    "dataStores",
    "controlQueues",
    "security",
    "apiManager",
    "deviceUserValidatorCacheSize"
})
@XmlRootElement(name = "DeviceCloudConfiguration")
public class DeviceCloudConfig {

    @XmlElement(name = "DataStores", required = true)
    protected DataStoresConfig dataStores;
    @XmlElement(name = "ControlQueues", required = true)
    protected ControlQueuesConfig controlQueues;
    @XmlElement(name = "Security", required = true)
    protected SecurityConfig security;
    @XmlElement(name = "ApiManager", required = true)
    protected ApiManagerConfig apiManager;
    @XmlElement(name = "DeviceUserValidatorCacheSize")
    protected int deviceUserValidatorCacheSize;

    /**
     * Gets the value of the dataStores property.
     * 
     * @return
     *     possible object is
     *     {@link DataStoresConfig }
     *     
     */
    public DataStoresConfig getDataStores() {
        return dataStores;
    }

    /**
     * Sets the value of the dataStores property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataStoresConfig }
     *     
     */
    public void setDataStores(DataStoresConfig value) {
        this.dataStores = value;
    }

    /**
     * Gets the value of the controlQueues property.
     * 
     * @return
     *     possible object is
     *     {@link ControlQueuesConfig }
     *     
     */
    public ControlQueuesConfig getControlQueues() {
        return controlQueues;
    }

    /**
     * Sets the value of the controlQueues property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlQueuesConfig }
     *     
     */
    public void setControlQueues(ControlQueuesConfig value) {
        this.controlQueues = value;
    }

    /**
     * Gets the value of the security property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityConfig }
     *     
     */
    public SecurityConfig getSecurity() {
        return security;
    }

    /**
     * Sets the value of the security property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityConfig }
     *     
     */
    public void setSecurity(SecurityConfig value) {
        this.security = value;
    }

    /**
     * Gets the value of the apiManager property.
     * 
     * @return
     *     possible object is
     *     {@link ApiManagerConfig }
     *     
     */
    public ApiManagerConfig getApiManager() {
        return apiManager;
    }

    /**
     * Sets the value of the apiManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApiManagerConfig }
     *     
     */
    public void setApiManager(ApiManagerConfig value) {
        this.apiManager = value;
    }

    /**
     * Gets the value of the deviceUserValidatorCacheSize property.
     * 
     */
    public int getDeviceUserValidatorCacheSize() {
        return deviceUserValidatorCacheSize;
    }

    /**
     * Sets the value of the deviceUserValidatorCacheSize property.
     * 
     */
    public void setDeviceUserValidatorCacheSize(int value) {
        this.deviceUserValidatorCacheSize = value;
    }

}
