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
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class OrquestadorSkeleton {
    private static boolean fin = false;
    private static String jsonRespAeropuertos = "";
    private static String origen = "";
    private static String destino = "";
    private static final String url = "jdbc:mysql://localhost:3306/cache_iata?useSSL=false";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String user = "ast";
    private static final String password = "Proyectoast2017!";

    /**
     * Implementacion de la llamada asincrona.
     */
    public static class MyCallBack implements  AxisCallback{
        public void onMessage(MessageContext messageContext) {
            OMElement response = messageContext.getEnvelope().getBody().getFirstElement();
            jsonRespAeropuertos = response.getFirstElement().getText();
        }
        public void onFault(MessageContext messageContext) {
            System.out.println("No se ha podido establecer comunicación, vuelva a intentarlo.");
        }
        public void onError(Exception e) {
            System.out.println("No se ha podido establecer comunicación, vuelva a intentarlo.");
        }
        public void onComplete() {
            JSONObject responseAerop = new JSONObject(jsonRespAeropuertos);
            JSONObject aerOrig = responseAerop.getJSONObject("AeropuertosOrigen");
            JSONObject aerDest = responseAerop.getJSONObject("AeropuertosDestino");
            JSONArray arrayAerOrig = aerOrig.getJSONArray("Aeropuertos");
            JSONArray arrayAerDest = aerDest.getJSONArray("Aeropuertos");

            String[] aeropuertosOrigen = new String[arrayAerOrig.length()];
            for (int i = 0; i < arrayAerOrig.length() ; i++) {
                aeropuertosOrigen[i] = arrayAerOrig.get(i).toString();
            }

            String[] aeropuertosDestino = new String[arrayAerDest.length()];
            for (int i = 0; i < arrayAerDest.length(); i++) {
                aeropuertosDestino[i] = arrayAerDest.get(i).toString();
            }

            if (aeropuertosOrigen.length > 0 && aeropuertosDestino.length > 0){
                Statement statement = null;
                Connection connection;
                try {
                    // Register JDBC driver
                    Class.forName(JDBC_DRIVER);

                    // Open connection
                    System.out.println("Connecting to a selected database...");
                    connection = DriverManager.getConnection(url,user,password);
                    System.out.println("Connected database successfully...");

                    //Execute a query
                    statement = connection.createStatement();

                    //Insert into tables
                    //language=MySQL
                    String insertCacheOrigen = "INSERT IGNORE INTO iata(ciudad,codigoIATA) VALUES ('"+origen+"','"+aeropuertosOrigen[0]+"');";
                    //language=MySQL
                    String insertCacheDestino = "INSERT IGNORE INTO iata(ciudad,codigoIATA) VALUES ('"+destino+"','"+aeropuertosDestino[0]+"');";

                    statement.executeUpdate(insertCacheOrigen);
                    statement.executeUpdate(insertCacheDestino);

                    // Clean the environment:
                    connection.close();
                    statement.close();
                    System.out.println("Database closed correctly!");

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("no hay aeropuertos.");
            }

            fin = true;
        }
    }

    public static void main(String[] args) throws InterruptedException, AxisFault {
        Logger.getRootLogger().setLevel(Level.OFF);
        origen ="Asturias";
        destino = "Madrid";
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
        while (!fin) Thread.sleep(100);
    }

    public orquestador.ObtenerOfertasResponse obtenerOfertas(orquestador.ObtenerOfertas obtenerOfertas) throws AxisFault, InterruptedException {
        Logger.getRootLogger().setLevel(Level.OFF);
        ObtenerOfertasResponse obtenerOfertasResponse = new ObtenerOfertasResponse();
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
        servicioAeropuertos.sendReceiveNonBlocking(createPayLoadAeropuertos("Vigo","Madrid"), new MyCallBack());
        while (!fin) Thread.sleep(100);

        return obtenerOfertasResponse;
    }

    public orquestador.ComprarBilleteResponse comprarBillete(orquestador.ComprarBillete comprarBillete) {
        //TODO : fill this with the necessary business logic
        throw new java.lang.UnsupportedOperationException("Please implement " +
                this.getClass().getName() + "#comprarBillete");
    }

    private static String tratamientoErrores(String[] aeropuertosOrigen, String[] aeropuertosDestino,
                                             String ciudadOrigen,String ciudadDestino){
        if (aeropuertosOrigen.length == 0 && aeropuertosDestino.length == 0)
            return "{\n" +
                    "  \"ValidationErrors\": [\n" +
                    "    {\n" +
                    "      \"ParameterName\": \"ciudadOrigen\",\n" +
                    "      \"ParameterValue\": \""+ciudadOrigen+"\",\n" +
                    "      \"Message\": \"No existen aeropuertos en esta ciudad o los datos de esta ciudad estan mal introducidos\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"ParameterName\": \"ciudadDestino\",\n" +
                    "      \"ParameterValue\": \""+ciudadDestino+"\",\n" +
                    "      \"Message\": \"No existen aeropuertos en esta ciudad o los datos de esta ciudad estan mal introducidos\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        else {
            if (aeropuertosOrigen.length == 0){
                return "{\n" +
                        "  \"ValidationErrors\": [\n" +
                        "    {\n" +
                        "      \"ParameterName\": \"ciudadOrigen\",\n" +
                        "      \"ParameterValue\": \""+ciudadOrigen+"\",\n" +
                        "      \"Message\": \"No existen aeropuertos en esta ciudad o los datos de esta ciudad estan mal introducidos\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            }else {
                return "{\n" +
                        "  \"ValidationErrors\": [\n" +
                        "    {\n" +
                        "      \"ParameterName\": \"ciudadDestino\",\n" +
                        "      \"ParameterValue\": \""+ciudadDestino+"\",\n" +
                        "      \"Message\": \"No existen aeropuertos en esta ciudad o los datos de esta ciudad estan mal introducidos\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
            }
        }
    }

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
