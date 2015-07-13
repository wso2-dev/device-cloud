
package org.wso2.carbon.device.mgt.iot.common.config.server.datasource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ApiManagerConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ApiManagerConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="AccessTokenURL" type="{}http-url"/>
 *         &lt;element name="LoginURL" type="{}http-url"/>
 *         &lt;element name="SubscriptionListURL" type="{}http-url"/>
 *         &lt;element name="DeviceGrantType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DeviceScopes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ApiManagerConfig", propOrder = {
    "enabled",
    "accessTokenURL",
    "loginURL",
    "subscriptionListURL",
    "deviceGrantType",
    "deviceScopes"
})
public class ApiManagerConfig {

    @XmlElement(name = "Enabled")
    protected boolean enabled;
    @XmlElement(name = "AccessTokenURL", required = true)
    protected String accessTokenURL;
    @XmlElement(name = "LoginURL", required = true)
    protected String loginURL;
    @XmlElement(name = "SubscriptionListURL", required = true)
    protected String subscriptionListURL;
    @XmlElement(name = "DeviceGrantType", required = true)
    protected String deviceGrantType;
    @XmlElement(name = "DeviceScopes", required = true)
    protected String deviceScopes;

    /**
     * Gets the value of the enabled property.
     * 
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the accessTokenURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    /**
     * Sets the value of the accessTokenURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessTokenURL(String value) {
        this.accessTokenURL = value;
    }

    /**
     * Gets the value of the loginURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginURL() {
        return loginURL;
    }

    /**
     * Sets the value of the loginURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginURL(String value) {
        this.loginURL = value;
    }

    /**
     * Gets the value of the subscriptionListURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionListURL() {
        return subscriptionListURL;
    }

    /**
     * Sets the value of the subscriptionListURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionListURL(String value) {
        this.subscriptionListURL = value;
    }

    /**
     * Gets the value of the deviceGrantType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceGrantType() {
        return deviceGrantType;
    }

    /**
     * Sets the value of the deviceGrantType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceGrantType(String value) {
        this.deviceGrantType = value;
    }

    /**
     * Gets the value of the deviceScopes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceScopes() {
        return deviceScopes;
    }

    /**
     * Sets the value of the deviceScopes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceScopes(String value) {
        this.deviceScopes = value;
    }

}
