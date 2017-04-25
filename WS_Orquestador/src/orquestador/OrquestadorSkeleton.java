/**
 * OrquestadorSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package orquestador;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ServiceLifeCycle;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class OrquestadorSkeleton implements ServiceLifeCycle{
    //TODO la fecha debe introducirse de una forma en especial yyyy-mm-dd, hay que comprobar eso.

    private static boolean fin = false;
    private static boolean onFault = false, onError = false, onComplete = false;
    private static String jsonRespAeropuertos = "";
    private static String origen = "";
    private static String destino = "";
    private static String iataOrigenBBDD = null;
    private static String iataDestinoBBDD = null;
    private static String iataOrigenCallBack = null;
    private static String iataDestinoCallBack = null;

    // JDBC nombre del driver y url de la base de datos
    private static final String url = "jdbc:mysql://localhost:3306/cache_iata?useSSL=false";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    // Credenciales para la base de datos
    private static final String user = "ast";
    private static final String password = "Proyectoast2017!";

    // UDDI
    private static Publish sp = new Publish();
    private static String servicio = "Orquestador";
    private static String endpoint = "http://localhost:8080/axis2/services/Orquestador";

    //Llamada asincrona.
    public static class MyCallBack implements  AxisCallback{
        public void onMessage(MessageContext messageContext) {
            //TODO ¿Borrar este punto de control o dejarlos para el catalina.out?
            System.out.println("Llamada asincrona recibida");
            OMElement response = messageContext.getEnvelope().getBody().getFirstElement();
            jsonRespAeropuertos = response.getFirstElement().getText();
        }
        public void onFault(MessageContext messageContext) {
            onFault = true;
        }
        public void onError(Exception e) {
            onError = true;
        }
        public void onComplete() {
            onComplete = true;
            JSONObject responseAerop = new JSONObject(jsonRespAeropuertos);
            JSONObject aerOrig = responseAerop.getJSONObject("AeropuertosOrigen");
            JSONObject aerDest = responseAerop.getJSONObject("AeropuertosDestino");
            JSONArray arrayAerOrig = aerOrig.getJSONArray("Aeropuertos");
            JSONArray arrayAerDest = aerDest.getJSONArray("Aeropuertos");

            if (arrayAerOrig.length() > 0 && arrayAerDest.length() > 0){
                iataOrigenCallBack = arrayAerOrig.get(0).toString();
                iataDestinoCallBack = arrayAerDest.get(0).toString();
                try {
                    // Register JDBC driver
                    Class.forName(JDBC_DRIVER);

                    // Open connection
                    Connection  connection = DriverManager.getConnection(url,user,password);

                    //language=MySQL
                    String sql = "{CALL introducir_iata(?,?)}";
                    CallableStatement callableStatement = connection.prepareCall(sql);
                    callableStatement.setString(1,origen);
                    callableStatement.setString(2,iataOrigenCallBack);
                    callableStatement.executeQuery();

                    callableStatement.setString(1,destino);
                    callableStatement.setString(2,iataDestinoCallBack);
                    callableStatement.executeQuery();

                    // Clean the environment:
                    callableStatement.close();
                    connection.close();

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                if (arrayAerOrig.length() > 0) iataOrigenCallBack = arrayAerOrig.get(0).toString();
                if (arrayAerDest.length() > 0) iataDestinoCallBack = arrayAerDest.get(0).toString();
            }
            fin = true;
        }
    }

    public void startUp(ConfigurationContext configurationContext, AxisService axisService) {
        //sp.publish(servicio,endpoint);
    }

    public void shutDown(ConfigurationContext configurationContext, AxisService axisService) {
        //sp.unpublish();
    }

    /**
     * Metodo que devolverá al cliente, una serie de ofertas,
     * en funcion de la ciudad de origen, destino y fechas de
     * salida y de regreso.
     *
     * @param obtenerOfertas
     * @return
     * @throws AxisFault
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */

    public orquestador.ObtenerOfertasResponse obtenerOfertas(orquestador.ObtenerOfertas obtenerOfertas)
            throws AxisFault, SQLException, ClassNotFoundException, InterruptedException {
        ObtenerOfertasResponse obtenerOfertasResponse = new ObtenerOfertasResponse();
        Logger.getRootLogger().setLevel(Level.OFF);
        origen = obtenerOfertas.getCiudadOrigen();
        destino = obtenerOfertas.getCiudadDestino();
        String fechaSalida = obtenerOfertas.getFechaSalida();
        String fechaRegreso = obtenerOfertas.getFechaRegreso();

        int opcion = obtenerCodigosIATA();
        switch (opcion){
            case 1:
                //uso de BBDD
                //TODO ¿Borrar estos puntos de control o dejarlos para el catalina.out?
                System.out.println("iataOrigenBBDD: " + iataOrigenBBDD);
                System.out.println("iataDestinoBBDD: " + iataDestinoBBDD);

                ServiceClient servicioVuelos = new ServiceClient();
                Options opciones = new Options();
                MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
                HttpConnectionManagerParams params = new HttpConnectionManagerParams();
                params.setDefaultMaxConnectionsPerHost(20);
                params.setMaxTotalConnections(20);
                params.setSoTimeout(20000);
                params.setConnectionTimeout(20000);
                multiThreadedHttpConnectionManager.setParams(params);
                HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
                opciones.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
                opciones.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
                opciones.setTo(new EndpointReference("http://localhost:8080/axis2/services/Vuelos"));
                opciones.setAction("urn:getInfoVuelos");
                servicioVuelos.setOptions(opciones);

                OMElement response = servicioVuelos.sendReceive(
                        createPayLoadVuelos(iataOrigenBBDD, iataDestinoBBDD, fechaSalida, fechaRegreso));

                obtenerOfertasResponse.setOfertas(response.getFirstElement().getText());

                break;
            case 2:
                //uso de WebServicesX
                if (onFault || onError){
                    obtenerOfertasResponse.setOfertas(tratamientoErrores("No se ha podido establecer comunicacion " +
                            "con el servicio web externo, vuelva a intentarlo o revise su conexión a Internet."));
                }else {
                    if (iataOrigenCallBack == null || iataDestinoCallBack == null){
                        if (iataOrigenCallBack == null && iataDestinoCallBack == null)
                            obtenerOfertasResponse.setOfertas(tratamientoErrores("La ciudad de origen: "+origen +
                                    " y la ciudad de destino: "+destino+" no tienen aeropuertos."));
                        else{
                            if (iataOrigenCallBack == null)
                                obtenerOfertasResponse.setOfertas(tratamientoErrores("La ciudad de origen: "+
                                        origen+" no tiene aeropuertos."));
                            else
                                obtenerOfertasResponse.setOfertas(tratamientoErrores("La ciudad de destino: "+
                                        destino+" no tiene aeropuertos."));
                        }
                    }else {
                        //TODO ¿Borrar estos puntos de control o dejarlos para el catalina.out?
                        System.out.println("iataOrigenCallBack: " + iataOrigenCallBack);
                        System.out.println("iataDestinoCallBack: " + iataDestinoCallBack);

                        servicioVuelos = new ServiceClient();
                        opciones = new Options();
                        multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
                        params = new HttpConnectionManagerParams();
                        params.setDefaultMaxConnectionsPerHost(20);
                        params.setMaxTotalConnections(20);
                        params.setSoTimeout(20000);
                        params.setConnectionTimeout(20000);
                        multiThreadedHttpConnectionManager.setParams(params);
                        httpClient = new HttpClient(multiThreadedHttpConnectionManager);
                        opciones.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
                        opciones.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
                        opciones.setTo(new EndpointReference("http://localhost:8080/axis2/services/Vuelos"));
                        opciones.setAction("urn:getInfoVuelos");
                        servicioVuelos.setOptions(opciones);

                        response = servicioVuelos.sendReceive(
                                createPayLoadVuelos(iataOrigenCallBack, iataDestinoCallBack, fechaSalida, fechaRegreso));

                        obtenerOfertasResponse.setOfertas(response.getFirstElement().getText());
                    }
                }
                break;
        }

        return obtenerOfertasResponse;
    }

    /**
     * Metodo que obtiene, si es que existe ya sea desde nuestra BBDD
     * o desde el servicio web externo, los codigosIATA de las ciudades
     * de origen y de destino.
     *
     * @return
     *          -> 1 si se ha usado la base de datos para obtener los codigos iata
     *          -> 2 si se ha usado el servicio externo webservicesX.
     *
     * @throws AxisFault
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static int obtenerCodigosIATA() throws AxisFault, ClassNotFoundException, SQLException, InterruptedException {
        boolean origenExiste = false, destinoExiste = false;

        ServiceClient servicioAeropuertos = new ServiceClient();
        Options opciones = new Options();
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(20);
        params.setMaxTotalConnections(20);
        params.setSoTimeout(20000);
        params.setConnectionTimeout(20000);
        multiThreadedHttpConnectionManager.setParams(params);
        HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
        opciones.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
        opciones.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
        opciones.setTo(new EndpointReference("http://localhost:8080/axis2/services/Aeropuertos"));
        opciones.setAction("urn:getInfoAeropuerto");
        servicioAeropuertos.setOptions(opciones);
        servicioAeropuertos.sendReceiveNonBlocking(createPayLoadAeropuertos(origen,destino), new MyCallBack());

        // Register the JDBC driver:
        Class.forName(JDBC_DRIVER);

        // Open connection:
        Connection connection = DriverManager.getConnection(url,user,password);

        //Execute a CallableStatement:
        //language=MySQL
        String sql = "{CALL obtener_codigoIATA(?)}";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setString(1,origen);
        ResultSet resultSet = callableStatement.executeQuery();
        if (resultSet.next()){
            origenExiste = true;
            iataOrigenBBDD = resultSet.getString("codigoIATA");
        }

        callableStatement.setString(1,destino);
        resultSet = callableStatement.executeQuery();
        if (resultSet.next()){
            destinoExiste = true;
            iataDestinoBBDD = resultSet.getString("codigoIATA");
        }

        // Clean the environment:
        callableStatement.close();
        connection.close();

        if (destinoExiste && origenExiste){
            return 1;
        }else {
            while (!fin) Thread.sleep(100);
            return 2;
        }
    }

    public orquestador.ComprarBilleteResponse comprarBillete(orquestador.ComprarBillete comprarBillete) {
        Logger.getRootLogger().setLevel(Level.OFF);
        ComprarBilleteResponse comprarBilleteResponse = new ComprarBilleteResponse();
        int id_oferta = comprarBillete.getId_oferta();
        String dni = comprarBillete.getDni();
        String token = comprarBillete.getToken();
        String email = comprarBillete.getEmail();
        String cuenta = comprarBillete.getCuenta();
        String cuentaDestino = "12345678";

        // TODO Comprobar en la cache del orquestador el importe del vuelo indicado
        String importe = "1";

        // Creamos mensaje SOAP:
        ServiceClient sc = null;
        try {
            sc = new ServiceClient();
        } catch (AxisFault e1) {
            e1.printStackTrace();
        }
        Options opts = new Options();
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(20);
        params.setMaxTotalConnections(20);
        params.setSoTimeout(20000);
        params.setConnectionTimeout(20000);
        multiThreadedHttpConnectionManager.setParams(params);
        HttpClient httpClient = new HttpClient(multiThreadedHttpConnectionManager);
        opts.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, true);
        opts.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);
        opts.setTo(new EndpointReference("http://localhost:8080/axis2/services/Banco"));
        opts.setAction("urn:pagar");
        sc.setOptions(opts);

        // Anhadimos contenido del cuerpo:
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns4");
        OMElement method = fac.createOMElement("pagar", omNs);
        //Cabecera (cuenta y token)
        OMElement header = fac.createOMElement("tokenCuenta", omNs);
        header.setText(token+"-"+cuenta);
        sc.addHeader(header);
        // Importe
        OMElement im = fac.createOMElement("importe", omNs);
        im.setText(importe);
        method.addChild(im);
        // Cuenta origen
        OMElement co = fac.createOMElement("cuentaOrigen", omNs);
        co.setText(cuenta);
        method.addChild(co);
        // Cuenta destino
        OMElement cd = fac.createOMElement("cuentaDestino", omNs);
        cd.setText(cuentaDestino);
        method.addChild(cd);
        // Cuenta destino
        OMElement des = fac.createOMElement("destinatario", omNs);
        des.setText(email);
        method.addChild(des);

        try {
            sc.sendRobust(method);
        } catch (AxisFault e1) {
            e1.printStackTrace();
        }
        comprarBilleteResponse.setCompra("Se ha enviado la informacion para realizar el pago, " +
                "le llegara una confirmacion al email indicado.");

        return comprarBilleteResponse;
    }

    /**
     * Método usado para crear mensajes JSON de aviso de errores.
     *
     * @param mensaje
     * @return el error en formato JSON.
     */

    private static String tratamientoErrores(String mensaje){
        return "{\n" +
                "  \"ValidationErrors\": [\n" +
                "    {\n" +
                "      \"Message\": \""+mensaje+"\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Metodo usado para la creacion del cuerpo de carga para
     * contactar con el WS_Vuelos
     *
     * @param aeropuertosOrigen
     * @param aeropuertosDestino
     * @param fechaSalida
     * @param fechaRegreso
     * @return
     */
    private static OMElement createPayLoadVuelos(String aeropuertosOrigen, String aeropuertosDestino,
                                                 String fechaSalida, String fechaRegreso){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Vuelos","ns");
        OMElement omElement = factory.createOMElement("getInfoVuelos",omNamespace);
        OMElement originAirport = factory.createOMElement("originAirport",omNamespace);
        OMElement destinationAirport = factory.createOMElement("destinationAirport",omNamespace);
        OMElement outboundDate = factory.createOMElement("outboundDate",omNamespace);
        OMElement inboundDate= factory.createOMElement("inboundDate",omNamespace);
        originAirport.setText(aeropuertosOrigen);
        destinationAirport.setText(aeropuertosDestino);
        outboundDate.setText(fechaSalida);
        inboundDate.setText(fechaRegreso);
        omElement.addChild(originAirport);
        omElement.addChild(destinationAirport);
        omElement.addChild(outboundDate);
        omElement.addChild(inboundDate);

        return omElement;
    }

    /**
     * Metodo usado para la creacion del cuerpo de carga para
     * contactar con el WS_Aeropuertos.
     *
     * @param origen
     * @param destino
     * @return
     */

    private static OMElement createPayLoadAeropuertos(String origen, String destino){
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace omNamespace = factory.createOMNamespace("http://Aeropuertos","ns");
        OMElement omElement = factory.createOMElement("getInfoAeropuerto",omNamespace);
        OMElement ciudadOrigen = factory.createOMElement("ciudadOrigen",omNamespace);
        OMElement ciudadDestino = factory.createOMElement("ciudadDestino",omNamespace);
        ciudadOrigen.setText(origen);
        ciudadDestino.setText(destino);
        omElement.addChild(ciudadOrigen);
        omElement.addChild(ciudadDestino);

        return omElement;
    }
}
