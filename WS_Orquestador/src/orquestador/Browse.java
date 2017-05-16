package orquestador;

import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

import java.util.List;

/**
 * AuthToken
 * BindingTemplates
 * BusinessDetail
 * BusinessInfos
 * BusinessList
 * BusinessService
 * CategoryBag
 * Contacts
 * Description
 * DiscardAuthToken
 * FindBusiness
 * GetAuthToken
 * GetBusinessDetail
 * GetServiceDetail
 * KeyedReference
 * Name
 * ServiceDetail
 * ServiceInfos
 */


public class Browse {

	private static UDDISecurityPortType security = null;
	private static UDDIInquiryPortType inquiry = null;
	private static String ServiceName = null;
	private static String endpoint = null;



	/* --------------------------------------- CONSTRUCTOR --------------------------------------- */

	public Browse() {

		try {

			// Create a client and read config file (uddi.xml)
			UDDIClient client = new UDDIClient("/Users/ruben/Tomcat/uddi/uddi.xml");
			// A UDDIClient can be a client to multiple UDDI nodes, so supply the nodeName (defined in your uddi.xml).

			// The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
			Transport transport = client.getTransport("default");

			// Now you create a reference to the UDDI API
			security = transport.getUDDISecurityService();
			inquiry = transport.getUDDIInquiryService();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/* --------------------------------------- METODOS --------------------------------------- */


	public String browseService(String service) {

		ServiceName = service;
		endpoint = null;

		try {

			String token = GetAuthKey("uddi", "uddi");
			BusinessList findBusiness = GetBusinessList(token);
			PrintBusinessInfo(findBusiness.getBusinessInfos());
			PrintServiceDetailsByBusiness(findBusiness.getBusinessInfos(), token);

			security.discardAuthToken(new DiscardAuthToken(token));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return endpoint;

	}



	// Gets a UDDI style auth token
	private String GetAuthKey(String username, String password) {

		try {

			GetAuthToken getAuthTokenRoot = new GetAuthToken();
			getAuthTokenRoot.setUserID(username);
			getAuthTokenRoot.setCred(password);

			// Making API call that retrieves the authentication token for the user.
			AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
			System.out.println(username + " AUTHTOKEN = (don't log auth tokens!");

			return rootAuthToken.getAuthInfo();

		} catch (Exception ex) {
			System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
		}

		return null;
	}



	// Search for business
	private BusinessList GetBusinessList(String token) throws Exception {

		FindBusiness fb = new FindBusiness();
		fb.setAuthInfo(token);
		org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
		fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);

		fb.setFindQualifiers(fq);
		Name searchname = new Name();
		searchname.setValue(UDDIConstants.WILDCARD);
		fb.getName().add(searchname);
		BusinessList findBusiness = inquiry.findBusiness(fb);
		return findBusiness;

	}



	// Only information about he business
	private void PrintBusinessInfo(BusinessInfos businessInfos) {

		if (businessInfos == null) {
			System.out.println("No data returned");
		}

		else {

			for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
				System.out.println("\n===========================================================");
				System.out.println("Business Name: " + ListToString(businessInfos.getBusinessInfo().get(i).getName()));
				System.out.println("Business Key: "+ businessInfos.getBusinessInfo().get(i).getBusinessKey());
				System.out.println("Services:");

				if(businessInfos.getBusinessInfo().get(i).getServiceInfos() == null)
					System.out.println("No services found.");
				else
					PrintServiceInfo(businessInfos.getBusinessInfo().get(i).getServiceInfos());

			}
		}
	}



	// Print List Service Name and Service Key
	private void PrintServiceInfo(ServiceInfos serviceInfos) {

		for (int i = 0; i < serviceInfos.getServiceInfo().size(); i++) {
			System.out.println("-------------------------------------------");
			System.out.println("Name: " + ListToString(serviceInfos.getServiceInfo().get(i).getName()));
			System.out.println("Service Key: " + serviceInfos.getServiceInfo().get(i).getServiceKey());

		}
	}



	// Print service details for each business
	private void PrintServiceDetailsByBusiness(BusinessInfos businessInfos, String token) throws Exception {


		for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
			GetServiceDetail gsd = new GetServiceDetail();

			if(businessInfos.getBusinessInfo().get(i).getServiceInfos() != null) {

				for (int k = 0; k < businessInfos.getBusinessInfo().get(i).getServiceInfos().getServiceInfo().size(); k++)
					gsd.getServiceKey().add(businessInfos.getBusinessInfo().get(i).getServiceInfos().getServiceInfo().get(k).getServiceKey());

				gsd.setAuthInfo(token);
				System.out.println("Fetching data for business " + businessInfos.getBusinessInfo().get(i).getBusinessKey()+"\n");
				ServiceDetail serviceDetail = inquiry.getServiceDetail(gsd);

				for (int k = 0; k < serviceDetail.getBusinessService().size(); k++) {
					PrintServiceDetail(serviceDetail.getBusinessService().get(k));
				}

				System.out.println("................");

			}

		}
	}



	// Print service detail
	private void PrintServiceDetail(BusinessService get) {

		if (get == null) {
			return;
		}

		System.out.println("\nService Name: " + ListToString(get.getName()));
		for (int i = 0; i < get.getName().size(); i++) {

			if((get.getName().get(i).getValue()).equals(ServiceName))
				PrintBindingTemplates(get.getBindingTemplates());

		}
	}



	// Translate UDDI's somewhat complex data format to something that is more useful -- DESCRPTION
	private void PrintBindingTemplates(BindingTemplates bindingTemplates) {

		if (bindingTemplates == null) {
			return;
		}

		for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
			// An access point could be a URL, a reference to another UDDI binding key, a hosting redirector (which is
			// essentially a pointer to another UDDI registry) or a WSDL Deployment
			// From an end client's perspective, all you really want is the endpoint.
			// So if you have a wsdlDeployment useType, fetch the WSDL and parse for the invocation URL
			// If its hosting director, you'll have to fetch that data from UDDI recursively until the leaf node is found

			if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {

				if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType() != null) {

					if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType().equalsIgnoreCase(AccessPointType.END_POINT.toString()))
						endpoint = bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue();

				}
			}
		}
	}



	// AUX -- Print List to String
	private String ListToString(List<Name> name) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < name.size(); i++) {
			sb.append(name.get(i).getValue()).append(" ");
		}

		return sb.toString();
	}

}
