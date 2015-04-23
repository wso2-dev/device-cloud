package org.wso2.iot.services.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.ws.rs.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.DifferentStreamDefinitionAlreadyDefinedException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.StreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.iot.utils.ResourceFileLoader;
import org.wso2.iot.utils.XmlParser;
import org.xml.sax.SAXException;

@Path("/DeviceController")
public class DeviceController {
	private static Log log = LogFactory.getLog(DeviceController.class);
	private static String dataStoreEndpoint = "tcp://localhost:7613";
	private static String dataStoreUsername = "admin";
	private static String dataStorePassword = "admin";

	static {

		File file = new ResourceFileLoader("/resources/conf/configuration.xml").getFile();
		if (file.exists()) {
			XmlParser xml;
			try {
				xml = new XmlParser(file);
				dataStoreEndpoint = xml.getTagValues("IOT/BAM-Endpoint/url")[0];
				dataStoreUsername = xml.getTagValues("IOT/BAM-Endpoint/username")[0];
				dataStorePassword = xml.getTagValues("IOT/BAM-Endpoint/password")[0];

				file =
				       new ResourceFileLoader("/resources/security/" +
				                              xml.getTagValues("IOT/Security/client")[0]).getFile();
				if (file.exists()) {
					String trustStore = file.getAbsolutePath();
					System.setProperty("javax.net.ssl.trustStore", trustStore);
					System.setProperty("javax.net.ssl.trustStorePassword",
					                   xml.getTagValues("IOT/Security/password")[0]);
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				log.error("Error on configuration" + e);
			} catch (XPathExpressionException e) {
				log.error("Error on configuration" + e);
			}
		}

	}

	@Path("/pushdata/{ip}/{owner}/{type}/{mac}/{time}/{pin}/{value}")
	@POST
	// @Produces("application/xml")
	public String pushData(@PathParam("ip") String ipAdd, @PathParam("type") String deviceType,
	                       @PathParam("owner") String owner, @PathParam("mac") String macAddress,
	                       @PathParam("pin") String pin, @PathParam("time") String time,
	                       @PathParam("value") String pinValue,
	                       @HeaderParam("description") String description) {

		DataPublisher dataPublisher;
		try {
			dataPublisher =
			                new DataPublisher(dataStoreEndpoint, dataStoreUsername,
			                                  dataStorePassword);
		} catch (MalformedURLException | AgentException | AuthenticationException
		        | TransportException e) {

			log.error("Error creating DataPublisher for Endpoint: " + dataStoreEndpoint +
			          " with credentials, USERNAME-" + dataStoreUsername + " and PASSWORD-" +
			          dataStorePassword + ": ", e);
			return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
			       "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";

		}

		String devicePinDataStream;
		try {
			devicePinDataStream =
			                      dataPublisher.defineStream("{"
			                                                 + "'name':'org_wso2_iot_statistics_device_pin_data',"
			                                                 + "'version':'1.0.0',"
			                                                 + "'nickName': 'IoT Connected Device Pin Data',"
			                                                 + "'description': 'Pin Data Received',"
			                                                 + "'tags': ['arduino', 'led13'],"
			                                                 + "'metaData':["
			                                                 + "        {'name':'ipAdd','type':'STRING'},"
			                                                 + "        {'name':'deviceType','type':'STRING'},"
			                                                 + "        {'name':'owner','type':'STRING'},"
			                                                 + "		{'name':'requestTime','type':'LONG'}"
			                                                 + "],"
			                                                 + "'payloadData':["
			                                                 + "        {'name':'macAddress','type':'STRING'},"
			                                                 + "        {'name':'pin','type':'STRING'},"
			                                                 + "        {'name':'pinValue','type':'STRING'},"
			                                                 + "        {'name':'description','type':'STRING'}"
			                                                 + "]" + "}");

			log.info("stream definition ID for data from device pin: " + devicePinDataStream);

		} catch (AgentException | MalformedStreamDefinitionException | StreamDefinitionException
		        | DifferentStreamDefinitionAlreadyDefinedException e) {

			log.error("Error in defining stream for data publisher: ", e);
			return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
			       "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";

		}

		try {
			dataPublisher.publish(devicePinDataStream, System.currentTimeMillis(),
			                      new Object[] { ipAdd, deviceType, owner, Long.parseLong(time) },
			                      null, new Object[] { macAddress, pin, pinValue, description });

			log.info("event published to devicePinDataStream");

		} catch (AgentException e) {
			log.error("Error while publishing device pin data", e);
			return "<connect>" + "<pushdata>" + "<pin>" + pin + "</pin>" + "<value>" + pinValue +
			       "</value>" + "<result>" + false + "</result>" + "</pushdata>" + "</connect>";
		}

		return "<connect>" + "\n\t<pushdata>" + "\n\t\t<pin>\n\t\t\t" + pin + "\n\t\t</pin>" +
		       "\n\t\t<value>\n\t\t\t" + pinValue + "\n\t\t</value>" + "\n\t\t<result>\n\t\t\t" +
		       true + "\n\t\t</result>" + "\n\t</pushdata>" + "\n</connect>";
	}

	public static void main(String[] args) {
		log.info("TEst");
		DeviceController TestObject = new DeviceController();

		String out =
		             TestObject.pushData("localhost", "arduino", "smean", "123456", "Today", "13",
		                                 "HIGH", "Test");
		System.out.println("PushData : " + out);

	}
}