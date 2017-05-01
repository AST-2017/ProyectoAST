import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    private static String dniCliente;
    private static String password;
    private static TreeMap<Integer,String> mapaOfertas = new TreeMap<>();

    public static void main(String[] args) throws AxisFault {
        Scanner scan = new Scanner(System.in);
        int opcion;

        while (true){
            System.out.println("\n\n\tBIENVENIDO A NUESTRO SERVICIO DE VUELOS NACIONALES\n\n");
            System.out.println("\t1.- Iniciar sesión.");
            System.out.println("\t2.- Registrarse.");
            System.out.println("\n\nSeleccione una opción: ");
            opcion = Integer.parseInt(scan.nextLine());
            switch (opcion){
                case 1: //Iniciar sesion.
                    if (iniciarSesion())
                        menuPrincipal();
                    break;
                case 2: //Registrarse.
                    registrarse();
                    break;
                default: //Opcion no valida.
                    System.out.println("\n\n\tOpción no válida, volviendo al inicio...");
                    break;
            }
       }
    }

    private static boolean iniciarSesion(){
        Scanner scan = new Scanner(System.in);

        System.out.println("\n\n\tBIENVENIDO AL INICIO DE SESION DE NUESTRA PLATAFORMA.");
        System.out.println("\n\tINSERTE LOS SIGUIENTES DATOS:");

        System.out.println("\tUsuario(dni): ");
        dniCliente = scan.nextLine();
        System.out.println("\tContraseña: ");
        password = scan.nextLine();

        if (dniCliente.length() == 9){
            if (comprobarRegistroCliente())
                return true;
            else
                System.out.println("\n\n\tEl usuario no está registrado en la plataforma, volviendo al inicio...");
        }else
            System.out.println("\n\n\tDNI no válido, volviendo al inicio...");

        scan.close();

        return false;
    }

    private static void registrarse(){
        Scanner scan = new Scanner(System.in);

        System.out.println("\n\n\tBIENVENIDO AL REGISTRO DE NUESTRA PLATAFORMA.");
        System.out.println("\n\tINSERTE LOS SIGUIENTES DATOS:");

        System.out.println("\tNombre: ");
        String nombreTemp = scan.nextLine();
        System.out.println("\tPrimer apellido: ");
        String apellido1Temp = scan.nextLine();
        System.out.println("\tSegundo apellido: ");
        String apellido2Temp = scan.nextLine();
        System.out.println("\tDNI: ");
        String dniTemp = scan.nextLine();
        System.out.println("\tCorreo electrónico: ");
        String emailTemp = scan.nextLine();
        System.out.println("\tTeléfono de contacto: ");
        String telefonoTemp = scan.nextLine();
        System.out.println("\tContraseña: ");
        String passTemp = scan.nextLine();

        if (dniTemp.length() == 9){
            if (registrarCliente(nombreTemp,apellido1Temp,apellido2Temp,dniTemp,emailTemp,telefonoTemp,passTemp)){
                System.out.println("\n\n\tCliente registrado con éxito.");
            }else {
                System.out.println("\n\n\tYa existe un cliente registrado con ese dni.");
            }
        }else System.out.println("\n\n\tDNI no válido, volviendo al inicio...");

        scan.close();
    }

    private static void menuPrincipal(){
        int opcion;
        Scanner scan = new Scanner(System.in);
        while (true){
            System.out.println("\n\n\tBIENVENIDO A NUESTRO SERVICIO DE VUELOS NACIONALES\n\n");
            System.out.println("\t1.- Buscar vuelos.");
            System.out.println("\t2.- Ver reservas.");
            System.out.println("\t3.- Cerrar sesión");
            System.out.println("\n\n\tSeleccione una opción: ");

            opcion = Integer.parseInt(scan.nextLine());
            switch (opcion){
                case 1: //Buscar vuelos.
                    buscarComprar();
                    System.out.println("\n\n\tPulse ENTER para continuar.");
                    scan.nextLine();
                    break;
                case 2: //Ver las reservas del cliente.
                    System.out.println(verReservasCliente());
                    System.out.println("\n\n\tPulse ENTER para continuar.");
                    scan.nextLine();
                    break;
                case 3: //Cerrar sesion.
                    System.out.println("\n\n\tCerrando sesión... ");
                    System.exit(0);
                    break;
                default://Opcion no valida.
                    System.out.println("\n\n\tOpción no válida, volviendo al inicio...");
                    break;
            }
        }
    }


    private static boolean comprobarRegistroCliente(){
        try {
            //New ServiceClient.
            ServiceClient serviceClient = new ServiceClient();
            Options options = new Options();
            MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(20);
            params.setMaxTotalConnections(20);
            params.setSoTimeout(20000);
            params.setConnectionTimeout(20000);
            multiThreadedHttpConnectionManager.setParams(params);
            HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            options.setTo(new EndpointReference("http://localhost:8080/axis2/services/Orquestador"));
            options.setAction("urn:comprobarClienteRegistrado");
            serviceClient.setOptions(options);

            //Create payload and get the response.
            OMElement response = serviceClient.sendReceive(createPayLoadComprobarClienteRegistrado());

            return Boolean.valueOf(response.getFirstElement().getText());

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }
        return false;
    }

    private static boolean registrarCliente(String nombreCliente, String apellido1Cliente, String apellido2Cliente,
                                            String dniCliente, String emailCliente, String telefonoCliente,
                                            String passwordCliente){
        ServiceClient serviceClient;
        try {
            //New ServiceClient.
            serviceClient = new ServiceClient();
            Options options = new Options();
            MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(20);
            params.setMaxTotalConnections(20);
            params.setSoTimeout(20000);
            params.setConnectionTimeout(20000);
            multiThreadedHttpConnectionManager.setParams(params);
            HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            options.setTo(new EndpointReference("http://localhost:8080/axis2/services/Orquestador"));
            options.setAction("urn:registrarCliente");
            serviceClient.setOptions(options);

            //Create payload and get the response.
            OMElement response = serviceClient.sendReceive(createPayLoadRegistrarCliente(
                    nombreCliente,
                    apellido1Cliente,
                    apellido2Cliente,
                    dniCliente,
                    emailCliente,
                    telefonoCliente,
                    passwordCliente));

            return Boolean.valueOf(response.getFirstElement().getText());

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }
        return false;

    }

    private static String verReservasCliente(){
        //New ServiceClient.
        ServiceClient serviceClient;
        try {
            serviceClient = new ServiceClient();
            Options options = new Options();
            MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(20);
            params.setMaxTotalConnections(20);
            params.setSoTimeout(20000);
            params.setConnectionTimeout(20000);
            multiThreadedHttpConnectionManager.setParams(params);
            HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            options.setTo(new EndpointReference("http://localhost:8080/axis2/services/Orquestador"));
            options.setAction("urn:verReservasCliente");
            serviceClient.setOptions(options);

            OMElement response = serviceClient.sendReceive(createPayLoadVerReservasCliente());

            JSONObject object = new JSONObject(response.getFirstElement().getText());
            if (object.has("ValidationErrors")){
                JSONArray array = object.getJSONArray("ValidationErrors");
                return "\n\n\t" + array.getJSONObject(0).getString("Message");
            }else {
                if (object.has("Reservas")){
                    JSONArray array = object.getJSONArray("Reservas");
                    if (array.length() == 0) return "\n\n\t" + "Usted no tiene reservas a su nombre.";
                    else {
                        String reservas = "\n\n\t" +"Reservas: \n\n\n";
                        int precio;
                        String vueloDirectoSalida;
                        String vueloDirectoRegreso;
                        String fechaSalida;
                        String fechaRegreso;
                        String origen;
                        String destino;
                        String codigoIATAOrigen;
                        String codigoIATADestino;
                        String aerolineaSalida;
                        String aerolineaRegreso;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject objectAux = array.getJSONObject(i);
                            precio = objectAux.getInt("precio");
                            boolean vueloDirectoS = objectAux.getBoolean("vueloDirectoSalida");
                            if (vueloDirectoS) vueloDirectoSalida = "Si";
                            else vueloDirectoSalida = "No";
                            boolean vueloDirectoR = objectAux.getBoolean("vueloDirectoRegreso");
                            if (vueloDirectoR) vueloDirectoRegreso = "Si";
                            else vueloDirectoRegreso = "No";
                            fechaSalida = objectAux.getString("fechaSalida");
                            fechaRegreso = objectAux.getString("fechaRegreso");
                            origen = objectAux.getString("origen");
                            destino = objectAux.getString("destino");
                            codigoIATAOrigen = objectAux.getString("codigoIATAOrigen");
                            codigoIATADestino = objectAux.getString("codigoIATADestino");
                            aerolineaSalida = objectAux.getString("aerolineaSalida");
                            aerolineaRegreso = objectAux.getString("aerolineaRegreso");

                            reservas = reservas + "\t[Fecha de salida: " + fechaSalida + "], [Fecha regreso: " + fechaRegreso + "]\n";
                            reservas = reservas + "\t-> Origen: " + origen + " [" + codigoIATAOrigen + "].\n";
                            reservas = reservas + "\t-> Destino: " + destino + " [" + codigoIATADestino + "].\n";
                            reservas = reservas + "\t-> Precio: " + precio + ".\n";
                            reservas = reservas + "\t-> Aerolinea de ida: " + aerolineaSalida + ", vuelo directo: " + vueloDirectoSalida + ".\n";
                            reservas = reservas + "\t-> Aerolinea de regreso: " + aerolineaRegreso + ", vuelo directo: " + vueloDirectoRegreso + ".\n";
                            reservas = reservas + "\n\n";
                        }

                        return reservas;
                    }
                }else
                    return "\n\n\tError desconocido.";
            }

        } catch (AxisFault axisFault) {
            return axisFault.getMessage();
        }
    }

    private static void buscarComprar(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\n\n\tBIENVENIDO A NUESTRO SISTEMA DE BUSQUEDA DE VUELOS NACIONALES\n\n");
        System.out.println("Ciudad de origen: ");
        String ciudadOrigen = scan.nextLine();
        System.out.println("Ciudad de destino: ");
        String ciudadDestino = scan.nextLine();
        System.out.println("Fecha de salida[aaaa-mm-dd]: ");
        String fechaSalida = scan.nextLine();
        System.out.println("Fecha de regreso[aaaa-mm-dd]: ");
        String fechaRegreso = scan.nextLine();

        if(buscarOfertas(ciudadOrigen,ciudadDestino,fechaSalida,fechaRegreso)){
            //tenemos ofertas
            for (Map.Entry<Integer,String> entry : mapaOfertas.entrySet()) {
                System.out.println(entry.getValue() + "\n\n");
            }
            System.out.println("\n\n\t¿Desea comprar algun vuelo?");
            System.out.println("\t1. Si.");
            System.out.println("\t2. No");
            System.out.println("\n\n\tOpcion: ");
            switch (Integer.parseInt(scan.nextLine())){
                case 1:
                    System.out.println("\n\n\tSeleccione la opcion que desea comprar:");
                    int opcion = Integer.parseInt(scan.nextLine());
                    if (opcion > 0 && opcion < mapaOfertas.size()){
                        System.out.println("A seleccionado la opcion de compra: " + opcion);
                        System.out.println("Se va a proceder a la compra...");
                    }else System.out.println("\n\n\tOpción no válida, volviendo al menu principal...");
                    break;
                case 2:
                    System.out.println("\n\n\tVolviendo al menu principal...");
                    break;
                default:
                    System.out.println("\n\n\tOpción no válida, volviendo al menu principal...");
                    break;
            }
        }

        mapaOfertas.clear();
    }

    private static boolean buscarOfertas(String ciudadOrigen, String ciudadDestino, String fechaSalida, String fechaRegreso){
        //New ServiceClient.
        ServiceClient serviceClient = null;
        try {
            serviceClient = new ServiceClient();
            Options options = new Options();
            MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
            HttpConnectionManagerParams params = new HttpConnectionManagerParams();
            params.setDefaultMaxConnectionsPerHost(20);
            params.setMaxTotalConnections(20);
            params.setSoTimeout(20000);
            params.setConnectionTimeout(20000);
            multiThreadedHttpConnectionManager.setParams(params);
            HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
            options.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
            options.setTo(new EndpointReference("http://localhost:8080/axis2/services/Orquestador"));
            options.setAction("urn:obtenerOfertas");
            serviceClient.setOptions(options);

            OMElement response = serviceClient.sendReceive(
                    createPayLoadObtenerOfertas(ciudadOrigen,ciudadDestino,fechaSalida,fechaRegreso));

            JSONObject object = new JSONObject(response.getFirstElement().getText());

            if (object.has("ValidationErrors")){
                JSONArray array = object.getJSONArray("ValidationErrors");
                System.out.println("\n\n\t"+ array.getJSONObject(0).getString("Message"));

                return false;
            }else {
                JSONArray array = object.getJSONArray("Ofertas");

                if (array.length() == 0){
                    System.out.println("\n\n\tNo hay vuelos disponibles para la fecha de salida: " + fechaSalida +
                            " y la fecha de regreso: " + fechaRegreso + ", para las ciudades de origen: " + ciudadOrigen +
                            " y la ciudad de destino: " + ciudadDestino);

                    return false;
                }
                else {
                    int idOfertaR;
                    int precioR;
                    String vueloDirectoSalidaR;
                    String vueloDirectoRegresoR;
                    String fechaSalidaR;
                    String fechaRegresoR;
                    String origenR;
                    String destinoR;
                    String codigoIATAOrigenR;
                    String codigoIATADestinoR;
                    String aerolineaSalidaR;
                    String aerolineaRegresoR;
                    String oferta;

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject objectAux = array.getJSONObject(i);

                        idOfertaR = objectAux.getInt("idOferta");
                        precioR = objectAux.getInt("precio");
                        if (objectAux.getBoolean("vueloDirectoSalida")) vueloDirectoSalidaR = "Si";
                        else vueloDirectoSalidaR = "No";
                        if (objectAux.getBoolean("vueloDirectoRegreso")) vueloDirectoRegresoR = "Si";
                        else vueloDirectoRegresoR = "No";
                        fechaSalidaR = objectAux.getString("fechaSalida");
                        fechaRegresoR = objectAux.getString("fechaRegreso");
                        origenR = objectAux.getString("origenSalida");
                        destinoR = objectAux.getString("destinoSalida");
                        codigoIATAOrigenR = objectAux.getString("iataCodeOrigenSalida");
                        codigoIATADestinoR = objectAux.getString("iataCodeDestinoSalida");
                        aerolineaSalidaR = objectAux.getString("aerolineaSalida");
                        aerolineaRegresoR = objectAux.getString("aerolineaRegreso");

                        oferta = "\tOpcion "+idOfertaR+".- [Fecha de salida: " + fechaSalidaR + "], [Fecha regreso: " + fechaRegresoR + "]\n";
                        oferta = oferta + "\t-> Origen: " + origenR + " [" + codigoIATAOrigenR + "].\n";
                        oferta = oferta + "\t-> Destino: " + destinoR + " [" + codigoIATADestinoR + "].\n";
                        oferta = oferta + "\t-> Precio: " + precioR + ".\n";
                        oferta = oferta + "\t-> Aerolinea de ida: " + aerolineaSalidaR + ", vuelo directo: " + vueloDirectoSalidaR + ".\n";
                        oferta = oferta+ "\t-> Aerolinea de regreso: " + aerolineaRegresoR + ", vuelo directo: " + vueloDirectoRegresoR + ".\n";

                        mapaOfertas.put(idOfertaR,oferta);
                    }

                    return true;
                }
            }

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
            return false;
        }
    }

    private static OMElement createPayLoadComprobarClienteRegistrado(){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Orquestador","ns");
        OMElement omElement = factory.createOMElement("comprobarClienteRegistrado",omNamespace);
        OMElement dni = factory.createOMElement("dni",omNamespace);
        OMElement pass = factory.createOMElement("password",omNamespace);
        dni.setText(dniCliente);
        pass.setText(password);
        omElement.addChild(dni);
        omElement.addChild(pass);

        return omElement;
    }

    private static OMElement createPayLoadRegistrarCliente(String nombreCliente, String apellido1Cliente,
                                                           String apellido2Cliente, String dniCliente,
                                                           String emailCliente, String telefonoCliente,
                                                           String passwordCliente){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Orquestador","ns");
        OMElement omElement = factory.createOMElement("registrarCliente",omNamespace);
        OMElement nombre = factory.createOMElement("nombre",omNamespace);
        OMElement apellido1 = factory.createOMElement("apellido1",omNamespace);
        OMElement apellido2 = factory.createOMElement("apellido2",omNamespace);
        OMElement dni = factory.createOMElement("dni",omNamespace);
        OMElement email = factory.createOMElement("email",omNamespace);
        OMElement telefono = factory.createOMElement("telefono",omNamespace);
        OMElement password = factory.createOMElement("password",omNamespace);

        nombre.setText(nombreCliente);
        apellido1.setText(apellido1Cliente);
        apellido2.setText(apellido2Cliente);
        dni.setText(dniCliente);
        email.setText(emailCliente);
        telefono.setText(telefonoCliente);
        password.setText(passwordCliente);

        omElement.addChild(nombre);
        omElement.addChild(apellido1);
        omElement.addChild(apellido2);
        omElement.addChild(dni);
        omElement.addChild(email);
        omElement.addChild(telefono);
        omElement.addChild(password);

        return omElement;
    }

    private static OMElement createPayLoadVerReservasCliente(){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Orquestador","ns");
        OMElement omElement = factory.createOMElement("verReservasCliente",omNamespace);
        OMElement dni = factory.createOMElement("dni",omNamespace);

        dni.setText(dniCliente);
        omElement.addChild(dni);

        return omElement;
    }

    private static OMElement createPayLoadObtenerOfertas(
            String ciudadOrigen, String ciudadDestino, String fechaSalida, String fechaRegreso){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Orquestador","ns");
        OMElement omElement = factory.createOMElement("obtenerOfertas",omNamespace);
        OMElement ciudadO = factory.createOMElement("ciudadOrigen",omNamespace);
        OMElement ciudadD = factory.createOMElement("ciudadDestino",omNamespace);
        OMElement fechaS = factory.createOMElement("fechaSalida",omNamespace);
        OMElement fechaR = factory.createOMElement("fechaRegreso",omNamespace);
        OMElement dni = factory.createOMElement("dni",omNamespace);

        ciudadO.setText(ciudadOrigen);
        ciudadD.setText(ciudadDestino);
        fechaS.setText(fechaSalida);
        fechaR.setText(fechaRegreso);
        dni.setText(dniCliente);

        omElement.addChild(ciudadO);
        omElement.addChild(ciudadD);
        omElement.addChild(fechaS);
        omElement.addChild(fechaR);
        omElement.addChild(dni);

        return omElement;
    }

}
