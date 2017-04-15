import java.util.Scanner;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;


public class cliente_API {

	public static void main(String[] args) {
		
		//Crear cliente
		ServiceClient sc = null;
		
		try {
			sc = new ServiceClient();
		} catch (AxisFault AF) {
			System.out.println("Ha habido un problema al crear el cliente: "+AF.toString());
			System.out.println("Fin del programa.");
			return;
		}
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement valor;
		OMElement respuesta;
		
		//Crear el NameSpace
		OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "NameSpace1"); //Cambiar el ns
		
		//Crear un objeto de opcion
		Options opts = new Options();
		
		//Introducir el EPR
		opts.setTo(new EndpointReference("http://localhost:8080/axis2/services/")); //Nombre del servicio
		
		//Introducir accion
		opts.setAction("urn:Cliente");

		//Introducir la opcion creada dentro del servicio cliente
		sc.setOptions(opts);
		
		//Scaner para la entrada por teclado
		Scanner scan = new Scanner(System.in);
		
		while(true) {

			System.out.println("\n\n\tBIENVENIDO A NUESTRO SERVICIO DE VUELOS NACIONALES\n\nSelecciones una opción:");
			System.out.println("1 - Buscar vuelos");
			System.out.println("2 - Salir");

			System.out.println("Opción escogida: ");
			int opcion = scan.nextInt();
			scan.nextLine();
			
			switch(opcion) {
			
				case 1: //Buscar vuelos
					
					opts.setAction("urn:buscarVuelo");
					System.out.println("Escriba una ciudad de origen: ");
					String origen = scan.nextLine();
					
					System.out.println("Escriba una ciudad de destino: ");
					String destino = scan.nextLine();
					
					
					System.out.println("Buscando vuelos de "+origen+" a "+destino+" ...");
					
					try {
						Thread.sleep(3000); //Dormimos el hilo 3 segunos
					} catch (InterruptedException IE) {
						System.out.println(IE.toString());
					}
					
					OMElement metodo = fac.createOMElement("buscarVuelos", omNs); //CAMBIAR
					
					valor = fac.createOMElement("origen", omNs);
					valor.setText(origen.trim());
					metodo.addChild(valor);
					
					valor = fac.createOMElement("destino", omNs);
					valor.setText(destino.trim());
					metodo.addChild(valor);
					
					try {
						respuesta = sc.sendReceive(metodo);
					} catch (AxisFault AF) {
						System.out.println("No se pudo enviar su petición. Lo sentimos, vuelva a intentarlo."+AF.toString());
						System.out.println("Fin del programa.");
						scan.close();
						return;
					}
					
					String lista = respuesta.getFirstElement().getText();
					
					//SELECCIONAR EL VUELO SEGUN LA LISTA RECIBIDA... HAY QUE MIRAR COMO LO DEVUELVE EL ORQUESTADOR
					
					opts.setAction("urn:pagarVuelo");
					
					System.out.println("Seleccione el número de vuelo que desea: ");
					String vuelo = scan.nextLine();
					valor = fac.createOMElement("vuelo", omNs);
					valor.setText(vuelo.trim());
					metodo.addChild(valor);
					
					System.out.println("Introduzca su email: ");
					String email = scan.nextLine();
					valor = fac.createOMElement("email", omNs);
					valor.setText(email.trim());
					metodo.addChild(valor);
					
					System.out.println("Introduzca su número de tarjeta: ");
					String nro_tarjeta = scan.nextLine();
					valor = fac.createOMElement("nro_tarjeta", omNs);
					valor.setText(nro_tarjeta.trim());
					metodo.addChild(valor);
					
					////////////////////////////BANCO
					
					try {
						respuesta = sc.sendReceive(metodo);
					} catch (AxisFault AF) {
						System.out.println("No se pudo enviar su petición. Lo sentimos, vuelva a intentarlo."+AF.toString());
						System.out.println("Fin del programa.");
						scan.close();
						return;
					}
					
					String pago = respuesta.getFirstElement().getText();
					
					break;
					
				

				case 2: //Salir
					System.out.println("\nFin del programa.\n");
					scan.close();
					return;	

				default:
					
					System.out.println("Esa no es una opción válida.\n");
					break;

			}

		}


	}
	


}
