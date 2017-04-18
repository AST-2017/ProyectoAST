package publicar;

import java.util.Scanner;

import org.uddi.api_v3.*;
import org.apache.juddi.api_v3.*;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;


public class Publish {

        private static UDDIClerk clerk = null;
        private static String Bsskey = null;
        
        public Publish() {
        	
                try {
                        // create a client and read the config in the archive; 
                        // you can use your config file name
                        UDDIClient uddiClient = new UDDIClient("/home/sara/Escritorio/uddi.xml");
                        //get the clerk
                        clerk = uddiClient.getClerk("default");
                        if (clerk==null)
                                throw new Exception("the clerk wasn't found, check the config file!");
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

   
        public static void main(String args[]) {
            
        	//Scaner para la entrada por teclado
    		Scanner scan = new Scanner(System.in);
        	
        	Publish sp = new Publish();
        	
        	String servicio = "Noticia";
        	String endpoint = "http://localhost:8080/axis2/services/Noticia";
        	
        	String key = sp.publish(servicio, endpoint);
        	
        	Bsskey = key;
        	
        	System.out.println("\n\n-------------------- SEVICIO PUBLICADO --------------------\n\n");
        	
        	int salir = 1;
        	
        	while(salir!=0) {
        		System.out.println("Pulse 0 para 'despublicar' y salir: ");
        		salir = scan.nextInt();
        	}
        	
        	scan.close();
        	
        	sp.unpublish();
        	
        	System.out.println("\n\n-------------------- SEVICIO ELIMINADO --------------------\n\n");
        	System.out.println("-------------------- SALIR --------------------\n\n");
        	
        	return;
        	
        }
        
        
        public String publish(String servicio, String endpoint) {
                try {
                        // Creating the parent business entity that will contain our service.
                        BusinessEntity myBusEntity = new BusinessEntity();
                        Name myBusName = new Name();
                        myBusName.setValue("ast");
                        myBusEntity.getName().add(myBusName);
                        // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                        BusinessEntity register = clerk.register(myBusEntity);
                        if (register == null) {
                                System.out.println("Save failed! - 1");
                                System.exit(1);
                        }
                        String myBusKey = register.getBusinessKey();
                        System.out.println("myBusiness key:  " + myBusKey);

                        // Creating a service to save.  Only adding the minimum data: the parent business key retrieved from saving the business 
                        // above and a single name.
                        BusinessService myService = new BusinessService();
                        myService.setBusinessKey(myBusKey);
                        Name myServName = new Name();
                        myServName.setValue(servicio);
                        myService.getName().add(myServName);

                        // Add binding templates, etc...
                        BindingTemplate myBindingTemplate = new BindingTemplate();
                        AccessPoint accessPoint = new AccessPoint();
                        accessPoint.setUseType(AccessPointType.END_POINT.toString());
                        accessPoint.setValue(endpoint);
                        myBindingTemplate.setAccessPoint(accessPoint);
                        BindingTemplates myBindingTemplates = new BindingTemplates();
                        //optional but recommended step, this annotations our binding with all the standard SOAP tModel instance infos
                        myBindingTemplate = UDDIClient.addSOAPtModels(myBindingTemplate);
                        myBindingTemplates.getBindingTemplate().add(myBindingTemplate);
                        myService.setBindingTemplates(myBindingTemplates);
                        // Adding the service to the "save" structure, using our publisher's authentication info and saving away.
                        BusinessService svc = clerk.register(myService);
                        if (svc==null){
                                System.out.println("Save failed! - 2");
                                System.exit(1);
                        }
                        
                        String myServKey = svc.getServiceKey();
                        System.out.println("myService key:  " + myServKey);

                        clerk.discardAuthToken();
                        // Now you have a business and service via 
                        // the jUDDI API!
                        System.out.println("Success!");
                        
                        return myBusKey;

                } catch (Exception e) {
                        System.out.println("Exception: "+e.toString());
                }
                
				return null;
        }

       
        
        
        public void unpublish() {
    		
        	try {
                // create a client and read the config in the archive; 
                // you can use your config file name
                UDDIClient uddiClient = new UDDIClient("/home/sara/Escritorio/uddi.xml");
                //get the clerk
                clerk = uddiClient.getClerk("default");
                if (clerk==null)
                        throw new Exception("the clerk wasn't found, check the config file!");
        	} catch (Exception e) {
                e.printStackTrace();
        	}
        	
        	clerk.unRegisterWsdls();
        	clerk.unRegisterBusiness(Bsskey);
        	
    		return;
    		
    	}	
    	
    	  	
        
}