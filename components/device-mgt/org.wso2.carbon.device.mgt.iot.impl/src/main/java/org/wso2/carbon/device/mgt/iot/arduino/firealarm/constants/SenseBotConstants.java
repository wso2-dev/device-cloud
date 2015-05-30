package org.wso2.carbon.device.mgt.iot.arduino.firealarm.constants;

/**
 * Created by smean-MAC on 5/30/15.
 */
public class SenseBotConstants {
    public final static String DEVICE_TYPE = "sensebot";
    public final static String DEVICE_PLUGIN_DEVICE_NAME = "DEVICE_NAME";
    public final static String DEVICE_PLUGIN_DEVICE_ID = "SENSEBOT_DEVICE_ID";
    public final static String TEMPERATURE_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_temperature',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Temperature Data',"
                    + "     'description': 'Temperature data received from the Device',"
                    + "     'tags': ['iot', 'temperature'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'temperature','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String SONAR_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_sonar',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Sonar Data',"
                    + "     'description': 'Sonar data received from the Device',"
                    + "     'tags': ['iot', 'sonar'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'sonar','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String MOTION_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_motion',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'MOTION Data',"
                    + "     'description': 'Motion data received from the Device',"
                    + "     'tags': ['iot', 'motion'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'motion','type':'STRING'}"
                    + "                     ]"
                    + "}";

    public final static String LIGHT_STREAM_DEFINITION =
            "{"
                    + "     'name':'org_wso2_iot_devices_light',"
                    + "     'version':'1.0.0',"
                    + "     'nickName': 'Light Data',"
                    + "     'description': 'Light data received from the Device',"
                    + "     'tags': ['iot', 'light'],"
                    + "     'metaData': ["
                    + "                     {'name':'owner','type':'STRING'},"
                    + "                     {'name':'deviceType','type':'STRING'},"
                    + "                     {'name':'deviceId','type':'STRING'},"
                    + "                     {'name':'time','type':'LONG'}"
                    + "                 ]," + "     "
                    + "     'payloadData':  ["
                    + "                     {'name':'light','type':'STRING'}"
                    + "                     ]"
                    + "}";
}
