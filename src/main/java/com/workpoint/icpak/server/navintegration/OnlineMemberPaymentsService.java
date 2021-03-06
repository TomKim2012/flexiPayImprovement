
package com.workpoint.icpak.server.navintegration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "Online_Member_Payments_Service", targetNamespace = "urn:microsoft-dynamics-schemas/page/online_member_payments", wsdlLocation = "file:/C:/Users/user/Downloads/Online_Member_Payments.xml")
public class OnlineMemberPaymentsService extends Service {

	private final static URL ONLINEMEMBERPAYMENTSSERVICE_WSDL_LOCATION;
	private final static WebServiceException ONLINEMEMBERPAYMENTSSERVICE_EXCEPTION;
	private final static QName ONLINEMEMBERPAYMENTSSERVICE_QNAME = new QName(
			"urn:microsoft-dynamics-schemas/page/online_member_payments", "Online_Member_Payments_Service");

	static {
		URL url = null;
		WebServiceException e = null;
		try {
			url = new URL("file:/C:/Users/user/Downloads/Online_Member_Payments.xml");
		} catch (MalformedURLException ex) {
			e = new WebServiceException(ex);
		}
		ONLINEMEMBERPAYMENTSSERVICE_WSDL_LOCATION = url;
		ONLINEMEMBERPAYMENTSSERVICE_EXCEPTION = e;
	}

	public OnlineMemberPaymentsService() {
		super(__getWsdlLocation(), ONLINEMEMBERPAYMENTSSERVICE_QNAME);
	}

	public OnlineMemberPaymentsService(WebServiceFeature... features) {
		super(__getWsdlLocation(), ONLINEMEMBERPAYMENTSSERVICE_QNAME, features);
	}

	public OnlineMemberPaymentsService(URL wsdlLocation) {
		super(wsdlLocation, ONLINEMEMBERPAYMENTSSERVICE_QNAME);
	}

	public OnlineMemberPaymentsService(URL wsdlLocation, WebServiceFeature... features) {
		super(wsdlLocation, ONLINEMEMBERPAYMENTSSERVICE_QNAME, features);
	}

	public OnlineMemberPaymentsService(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public OnlineMemberPaymentsService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
		super(wsdlLocation, serviceName, features);
	}

	/**
	 * 
	 * @return returns OnlineMemberPaymentsPort
	 */
	@WebEndpoint(name = "Online_Member_Payments_Port")
	public OnlineMemberPaymentsPort getOnlineMemberPaymentsPort() {
		return super.getPort(
				new QName("urn:microsoft-dynamics-schemas/page/online_member_payments", "Online_Member_Payments_Port"),
				OnlineMemberPaymentsPort.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns OnlineMemberPaymentsPort
	 */
	@WebEndpoint(name = "Online_Member_Payments_Port")
	public OnlineMemberPaymentsPort getOnlineMemberPaymentsPort(WebServiceFeature... features) {
		return super.getPort(
				new QName("urn:microsoft-dynamics-schemas/page/online_member_payments", "Online_Member_Payments_Port"),
				OnlineMemberPaymentsPort.class, features);
	}

	private static URL __getWsdlLocation() {
		if (ONLINEMEMBERPAYMENTSSERVICE_EXCEPTION != null) {
			throw ONLINEMEMBERPAYMENTSSERVICE_EXCEPTION;
		}
		return ONLINEMEMBERPAYMENTSSERVICE_WSDL_LOCATION;
	}

}
