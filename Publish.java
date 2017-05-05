package orquestador;

// CHANGE PACKAGE AND PATH TO uddi.xml

import java.util.List;
import java.io.File;

import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.apache.juddi.api_v3.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;

public class Publish {

	// Clerk guarda las credenciales y las asocia con un nodo UDDI particular
	// Clerks are responsible for mapping stored user credentials to a Node and for automated registration.
	private static UDDIClerk clerk = null;
	private static String Servkey = null;
	private static String Buskey = null;
	
	private static UDDISecurityPortType security = null;
	private static UDDIInquiryPortType inquiry = null;

	/*-------------------------------- CONSTRUCTOR --------------------------------*/

	public Publish() {
		
		Logger.getRootLogger().setLevel(Level.OFF);

		try {
			
			// Create a client and read config file (uddi.xml)
			UDDIClient uddiClient = new UDDIClient("/home/sara/Escritorio/uddi.xml");

			// The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
			Transport transport = uddiClient.getTransport("default");
			
			// Now you create a reference to the UDDI API
			security = transport.getUDDISecurityService();
			inquiry = transport.getUDDIInquiryService();
			
			clerk = uddiClient.getClerk("default");

			if (clerk == null)
				throw new Exception("\n\nThe clerk wasn't found, check the config file (uddi.xml).\n\n");

		} catch (Exception e) {
			System.out.println("Error en la creación del cliente: "+ e.toString());
		}

	}

	
	/*-------------------------------- METODO PUBLISH --------------------------------*/

	public void publish(String servicio, String endpoint) {

		try {
			
			int comp = businessExists(servicio);
			// if comp==1 means that neither the business nor the service exists.
			// if comp==2 means that the businees exists but the service doesn't exist
			// if comp==3 means that both the business and the service exist.
			
			if(comp==1) {
			
				/**
				 * BUSINESS ENTIRY:
				 * 
				 * Contains information about the company itself, including contact information, 
				 * industry categories, business identifiers, and a list of services provided.
				 */

				// System.out.println("\n\nproyecto_C_AST does not exists as a business -> Creating it\n\n");
				
				// Creating the parent business entity that will contain our service
				BusinessEntity myBusEntity = new BusinessEntity();
				Name myBusName = new Name();
				myBusName.setValue("proyecto_C_AST");
				myBusEntity.getName().add(myBusName);

				// Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
				BusinessEntity register = clerk.register(myBusEntity);

				if (register == null) {
					System.out.println("Save failed! - 1");
					System.exit(1);
				}

				Buskey = register.getBusinessKey();
			
			}
			
			// System.out.println("\n\nmyBusiness key:  " + Buskey + "\n\n");

			if(comp==1 || comp==2) {
			
			/**
			 * BUSINESS SERVICE
			 * 
			 * Represents an individual web service provided by the business entity.
			 * 
			 * Its description includes information on how to bind to the web service, what type of web service it is, 
			 * 		and what taxonomical categories it belongs to.
			 */

			// Creating a service to save.
			// Only adding the minimum data: the parent business key retrieved from saving the business above and a single name.
			BusinessService myService = new BusinessService();
			myService.setBusinessKey(Buskey);
			Name myServName = new Name();
			myServName.setValue(servicio); // SERVICE NAME PROVIDED BY MAIN
			myService.getName().add(myServName);

			/**
			 * BINDING TEMPLATES
			 * 
			 * Technical descriptions of the web services represented by the business service structure.
			 * 
			 * A single business service may have multiple binding templates.
			 * 
			 * The binding template represents the actual implementation of the web service.
			 */

			// Add binding templates, access point.
			BindingTemplate myBindingTemplate = new BindingTemplate();
			AccessPoint accessPoint = new AccessPoint();
			accessPoint.setUseType(AccessPointType.END_POINT.toString());
			accessPoint.setValue(endpoint); // ENDPOINT PROVIDED BY MAIN
			myBindingTemplate.setAccessPoint(accessPoint);
			BindingTemplates myBindingTemplates = new BindingTemplates();

			/**
			 * tMODEL
			 * 
			 * A way of describing the various business, service, and template structures stored within the UDDI registry.
			 * 
			 * Any abstract concept can be registered within the UDDI as a tModel.
			 * 
			 * For instance, if you define a new WSDL port type, you can define a tModel that represents that 
			 * 		port type within the UDDI. Then, you can specify that a given business service implements that 
			 * 		port type by associating the tModel with one of that business service's binding templates.
			 */

			// Optional but recommended step, this annotations our binding with all the standard SOAP tModel instance infos
			myBindingTemplate = UDDIClient.addSOAPtModels(myBindingTemplate);
			myBindingTemplates.getBindingTemplate().add(myBindingTemplate);
			myService.setBindingTemplates(myBindingTemplates);

			// Adding the service to the "save" structure, using our publisher's authentication info and saving away.
			BusinessService svc = clerk.register(myService);

			if (svc == null) {
				System.out.println("Save failed! - 2");
				System.exit(1);
			}

			Servkey = svc.getServiceKey();
			
		}
			
		// System.out.println("\n\nmyService key:  " + Servkey + "\n\n");

		clerk.discardAuthToken();

		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
		}

		return;

	}

	
	/*-------------------------------- METODO BUSINESS EXISTS --------------------------------*/
	
	public int businessExists(String servicio) {
		
		int comp = 0;
		
		try {

			String token = GetAuthKey("uddi", "uddi");
			BusinessList findBusiness = GetBusinessList(token);
			comp = PrintBusinessInfo(findBusiness.getBusinessInfos(), servicio);

			security.discardAuthToken(new DiscardAuthToken(token));

			} catch (Exception e) {
				e.printStackTrace();
			}

		return comp;
		
	}
	
	// Gets a UDDI style auth token
	private String GetAuthKey(String username, String password) {

		try {

			GetAuthToken getAuthTokenRoot = new GetAuthToken();
			getAuthTokenRoot.setUserID("uddi");
			getAuthTokenRoot.setCred("uddi");

			// Making API call that retrieves the authentication token for the user.
			AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
			// System.out.println(username + " AUTHTOKEN = (don't log auth tokens!");
					
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


	// Look for the business and the service
	private int PrintBusinessInfo(BusinessInfos businessInfos, String servicio) {

		ServiceInfos serviceInfos;
		
		if (businessInfos == null) {
			// System.out.println("No data returned");
		}
				
		else {
					
			for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
				List<Name> name = businessInfos.getBusinessInfo().get(i).getName();
				
				for (int j = 0; j < name.size(); j++) {
					
					if((name.get(j).getValue()).equals("proyecto_C_AST")) {
						Buskey = businessInfos.getBusinessInfo().get(i).getBusinessKey();
						// System.out.println("\n\nproyecto_C_AST already exists as a business\n\n");
						
						if(businessInfos.getBusinessInfo().get(i).getServiceInfos() == null) {
							// System.out.println("No services found.");
							return 2;
						}
						else {
							serviceInfos = businessInfos.getBusinessInfo().get(i).getServiceInfos();
							for (int k = 0; k < serviceInfos.getServiceInfo().size(); k++) {
								List<Name> name2 = serviceInfos.getServiceInfo().get(k).getName();
								for (int l = 0; l < name2.size(); l++) {
									if((name2.get(l).getValue()).equals(servicio)) {
										Servkey = serviceInfos.getServiceInfo().get(k).getServiceKey();
										// System.out.println("\n\nService "+servicio+" already exists.\n\n");
										return 3;
									}
								}
							}
						}
					}
				}						
			}
		}
		
		return 1;
	}
	
	
	
	/*-------------------------------- METODO UNPUBLISH --------------------------------*/

	public void unpublish() {

		try {

			// Create a client and read config file (uddi.xml)
			UDDIClient uddiClient = new UDDIClient("/home/sara/Escritorio/uddi.xml");

			clerk = uddiClient.getClerk("default");
			
			if (clerk == null)
				throw new Exception("The clerk wasn't found, check the config file!");

		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
		}

		clerk.unRegisterWsdls(); // Unregisters all config defined wsdls
		clerk.unRegisterService(Servkey); // Removes a service by key.

		return;

	}

}
