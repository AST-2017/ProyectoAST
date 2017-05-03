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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Cliente {
    private static String dniCliente;
    private static String password;
    private static TreeMap<Integer,String> mapaOfertas = new TreeMap<>();
    private static String endpoint;
    private static Scanner scan = new Scanner(System.in);
    private static Browse sp = new Browse();

    public static void main(String[] args) {
        // Desactivar DEBUG
        Logger.getRootLogger().setLevel(Level.OFF);

        // Buscamos al orquestador en UDDI
        endpoint = sp.browseService("Orquestador");
        if(endpoint == null) {
            System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
            System.exit(1);
        }

        int opcion;
        while (true){
            System.out.println("\n\n\tBIENVENIDO A NUESTRO SERVICIO DE VUELOS NACIONALES\n\n");
            System.out.println("\t1.- Iniciar sesion.");
            System.out.println("\t2.- Registrarse.");
            System.out.println("\n\nSeleccione una opcion: ");
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
                    System.out.println("\n\n\tOpcion no valida, volviendo al inicio...");
                    break;
            }
        }
    }

    private static boolean iniciarSesion(){
        System.out.println("\n\n\tBIENVENIDO AL INICIO DE SESION DE NUESTRA PLATAFORMA.");
        System.out.println("\n\tINSERTE LOS SIGUIENTES DATOS:");

        System.out.println("\tUsuario(dni): ");
        dniCliente = scan.nextLine();
        System.out.println("\tContrasena: ");
        password = scan.nextLine();

        if (dniCliente.length() == 9){
            if (comprobarRegistroCliente())
                return true;
            else
                System.out.println("\n\n\tEl usuario no esta registrado en la plataforma, volviendo al inicio...");
        }else
            System.out.println("\n\n\tDNI no valido, volviendo al inicio...");

        return false;
    }

    private static void registrarse(){
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
        System.out.println("\tCorreo electronico: ");
        String emailTemp = scan.nextLine();
        System.out.println("\tTelefono de contacto: ");
        String telefonoTemp = scan.nextLine();
        System.out.println("\tContrasena: ");
        String passTemp = scan.nextLine();

        if (dniTemp.length() == 9){
            if (registrarCliente(nombreTemp,apellido1Temp,apellido2Temp,dniTemp,emailTemp,telefonoTemp,passTemp)){
                System.out.println("\n\n\tCliente registrado con exito, volviendo al inicio...");
            }else {
                System.out.println("\n\n\tYa existe un cliente registrado con ese dni, volviendo al inicio...");
            }
        }else System.out.println("\n\n\tDNI no valido, volviendo al inicio...");
    }

    private static void menuPrincipal(){
        int opcion;
        while (true){
            System.out.println("\n\n\tBIENVENIDO A NUESTRO SERVICIO DE VUELOS NACIONALES\n\n");
            System.out.println("\t1.- Buscar vuelos.");
            System.out.println("\t2.- Ver reservas.");
            System.out.println("\t3.- Cerrar sesion");
            System.out.println("\n\n\tSeleccione una opcion: ");

            opcion = Integer.parseInt(scan.nextLine());
            switch (opcion){
                case 1: //Buscar vuelos.
                    buscarComprar();
                    break;
                case 2: //Ver las reservas del cliente.
                    verReservasCliente();
                    break;
                case 3: //Cerrar sesion.
                    System.out.println("\n\n\tCerrando sesi�n... ");
                    scan.close();
                    System.exit(0);
                    break;
                default://Opcion no valida.
                    System.out.println("\n\n\tOpcion no valida, volviendo al inicio...");
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
            options.setTo(new EndpointReference(endpoint));
            options.setAction("urn:comprobarClienteRegistrado");
            serviceClient.setOptions(options);

            //Create payload and get the response.
            if(sp.browseService("Orquestador") != null){
                OMElement response = serviceClient.sendReceive(createPayLoadComprobarClienteRegistrado());
                return Boolean.valueOf(response.getFirstElement().getText());
            }else{
                System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
                System.exit(1);
            }
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
            options.setTo(new EndpointReference(endpoint));
            options.setAction("urn:registrarCliente");
            serviceClient.setOptions(options);

            //Create payload and get the response.
            if(sp.browseService("Orquestador") != null){
                OMElement response = serviceClient.sendReceive(createPayLoadRegistrarCliente(
                        nombreCliente,
                        apellido1Cliente,
                        apellido2Cliente,
                        dniCliente,
                        emailCliente,
                        telefonoCliente,
                        passwordCliente));
                return Boolean.valueOf(response.getFirstElement().getText());
            }else{
                System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
                System.exit(1);
            }

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }
        return false;

    }

    private static void verReservasCliente(){
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
            options.setTo(new EndpointReference(endpoint));
            options.setAction("urn:verReservasCliente");
            serviceClient.setOptions(options);

            if(sp.browseService("Orquestador") != null){

                OMElement response = serviceClient.sendReceive(createPayLoadVerReservasCliente());

                JSONObject object = new JSONObject(response.getFirstElement().getText());
                if (object.has("ValidationErrors")){
                    JSONArray array = object.getJSONArray("ValidationErrors");
                    System.out.println("\n\n\t" + array.getJSONObject(0).getString("Message"));
                }else {
                    if (object.has("Reservas")){
                        JSONArray array = object.getJSONArray("Reservas");
                        if (array.length() == 0) System.out.println("\n\n\t" + "Usted no tiene reservas a su nombre.");
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

                            System.out.println(reservas);
                        }
                    }else
                        System.out.println("\n\n\tError desconocido.");
                }
            } else{
                System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
                System.exit(1);
            }
        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
        }

        System.out.println("\n\n\tPulse ENTER para continuar.");
        scan.nextLine();
    }

    private static void buscarComprar(){
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
            System.out.println("\n\n\tDesea comprar algun vuelo?");
            System.out.println("\t1. Si.");
            System.out.println("\t2. No");
            System.out.println("\n\n\tOpcion: ");
            switch (Integer.parseInt(scan.nextLine())){
                case 1:
                    System.out.println("\n\n\tSeleccione la opcion que desea comprar:");
                    int opcion = Integer.parseInt(scan.nextLine());
                    if (opcion > 0 && opcion <= mapaOfertas.size()){
                        System.out.println("A seleccionado la opcion de compra: " + opcion);
                        System.out.println("Se va a proceder a la compra...");
                        System.out.println("\n\nIngrese su IBAN: ");
                        String iban = scan.nextLine();
                        System.out.println("Ingrese su codigo de seguridad: ");
                        String token = scan.nextLine();

                        comprarBillete(opcion,iban,token);
                    }else System.out.println("\n\n\tOpci�n no v�lida, volviendo al menu principal...");
                    break;
                case 2:
                    System.out.println("\n\n\tVolviendo al menu principal...");
                    break;
                default:
                    System.out.println("\n\n\tOpcion no valida, volviendo al menu principal...");
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
            options.setTo(new EndpointReference(endpoint));
            options.setAction("urn:obtenerOfertas");
            serviceClient.setOptions(options);

            if(sp.browseService("Orquestador") != null){

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
            }else{
                System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
                System.exit(1);
            }

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
            return false;
        }
        return false;
    }

    private static void comprarBillete(int oferta, String iban, String token){
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
            options.setTo(new EndpointReference(endpoint));
            options.setAction("urn:comprarBillete");
            serviceClient.setOptions(options);

            if(sp.browseService("Orquestador") != null){
                OMElement response = serviceClient.sendReceive(createPayLoadComprarBillete(oferta,iban,token));
                JSONObject object = new JSONObject(response.getFirstElement().getText());
                JSONArray array = object.getJSONArray("ValidationErrors");
                System.out.println("\n\n\t"+ array.getJSONObject(0).getString("Message"));
            }else{
                System.out.println("Error. El servicio web Orquestador no se encuentra disponible.");
                System.exit(1);
            }

        } catch (AxisFault axisFault) {
            System.out.println(axisFault.getMessage());
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

    private static OMElement createPayLoadComprarBillete(int ofertaCliente, String ibanCliente, String tokenCliente){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Orquestador","ns");
        OMElement omElement = factory.createOMElement("comprarBillete",omNamespace);
        OMElement oferta = factory.createOMElement("id_oferta",omNamespace);
        OMElement dni = factory.createOMElement("dni",omNamespace);
        OMElement iban = factory.createOMElement("iban",omNamespace);
        OMElement token = factory.createOMElement("token",omNamespace);

        oferta.setText(String.valueOf(ofertaCliente));
        dni.setText(dniCliente);
        iban.setText(ibanCliente);
        token.setText(tokenCliente);

        omElement.addChild(oferta);
        omElement.addChild(dni);
        omElement.addChild(iban);
        omElement.addChild(token);

        return omElement;
    }

}
