package aeropuertos;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
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
import org.json.XML;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Servicio Web encargado de recibir dos ciudades distintas
 * y devolver los aeropuertos de dichas ciudades en una
 * respuesta SOAP con cuerpo de respuesta JSON.
 */
@SuppressWarnings("Duplicates")
@WebService
public class AeropuertosSkeleton implements ServiceLifeCycle{
    private static TreeSet<String> aeropuertosOrigen = new TreeSet<>();
    private static TreeSet<String> aeropuertosDestino = new TreeSet<>();
    private static Publish sp = new Publish();

    public void startUp(ConfigurationContext context, AxisService service) {
        String servicio = "Aeropuertos";
        String endpoint = "http://192.168.43.199:8081/axis2/services/Aeropuertos";
        sp.publish(servicio, endpoint);
    }

    public void shutDown(ConfigurationContext context, AxisService service) {
      sp.unpublish();
    }

    /**
     * Se van a redefinir los nombres de algunas ciudades
     * por problemas de compatiblidad con WebServicesX.
     *
     * @return getInfoAeropuertoResponse
     */
    @WebMethod
    public aeropuertos.GetInfoAeropuertoResponse getInfoAeropuerto(aeropuertos.GetInfoAeropuerto getInfoAeropuerto) throws AxisFault {
        Logger.getRootLogger().setLevel(Level.OFF);
        GetInfoAeropuertoResponse getInfoAeropuertoResponse = new GetInfoAeropuertoResponse();

        String ciudadOrigen = getInfoAeropuerto.getCiudadOrigen();
        if (ciudadOrigen.toUpperCase().contains("LA CORUÑA")) ciudadOrigen = "LA CORUNA";
        if (ciudadOrigen.toUpperCase().contains("SEVILLA")) ciudadOrigen = "SEVILLE";
        if (ciudadOrigen.toUpperCase().contains("LA PALMA")) ciudadOrigen = "SANTA CRUZ LA PALMA LA PALMA";
        if (ciudadOrigen.toUpperCase().contains("MADRID")) ciudadOrigen = "MADRID BARAJAS";

        String ciudadDestino = getInfoAeropuerto.getCiudadDestino();
        if (ciudadDestino.toUpperCase().contains("LA CORUÑA")) ciudadDestino = "LA CORUNA";
        if (ciudadDestino.toUpperCase().contains("SEVILLA")) ciudadDestino = "SEVILLE";
        if (ciudadDestino.toUpperCase().contains("LA PALMA")) ciudadDestino = "SANTA CRUZ LA PALMA LA PALMA";
        if (ciudadDestino.toUpperCase().contains("MADRID")) ciudadDestino = "MADRID BARAJAS";

        ServiceClient sc = new ServiceClient();
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
        opts.setTo(new EndpointReference("http://www.webservicex.net/airport.asmx?WSDL"));
        opts.setAction("http://www.webserviceX.NET/GetAirportInformationByCountry");
        sc.setOptions(opts);
        OMElement res = sc.sendReceive(createPayLoadAeropuertos());

        getInfoAeropuertoResponse.setAeropuertos(createJSONResponse(res,ciudadOrigen,ciudadDestino));

        return getInfoAeropuertoResponse;
    }

    /**
     * Metodo que crea el cuerpo del mensaje SOAP para contactar
     * con el servicio externo webservicesX.
     *
     * @return en formato SOAP, todos los aeropuertos de España.
     */

    private static OMElement createPayLoadAeropuertos(){
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.webserviceX.NET", "ns");
        OMElement omElement = fac.createOMElement("GetAirportInformationByCountry", omNs);
        OMElement value = fac.createOMElement("country", omNs);
        value.setText("Spain");
        omElement.addChild(value);

        return omElement;
    }

    /**
     * Método que parsea la respuesta del servicio SOAP externo a los aeropuertos
     * de interés para nuestro servicio y crea la respuesta para el orquestador
     * en formato JSON.
     *
     * @return los aeropuertos de la ciudad de origen y la ciudad de destino
     * en formato JSON.
     */

    private static String createJSONResponse(OMElement res, String ciudadOrigen, String ciudadDestino){
        String json = XML.toJSONObject(res.getFirstElement().getText()).toString();
        JSONObject response = new JSONObject(json);
        JSONObject NewDataSet = response.getJSONObject("NewDataSet");
        JSONArray table = NewDataSet.getJSONArray("Table");
        for (int i = 0; i < table.length(); i++) {
            JSONObject object = table.getJSONObject(i);
            if (object.getString("CityOrAirportName").toUpperCase().contains(ciudadOrigen.toUpperCase())){
                aeropuertosOrigen.add(object.getString("AirportCode"));
            }else {
                if (object.getString("CityOrAirportName").toUpperCase().contains(ciudadDestino.toUpperCase())){
                    aeropuertosDestino.add(object.getString("AirportCode"));
                }
            }
        }

        String jsonResopnse = "{\n" + "  \"AeropuertosOrigen\": {\n" + "    \"Aeropuertos\": [";
        Iterator<String> iterator = aeropuertosOrigen.iterator();
        while (iterator.hasNext()){
            String aeropuertoOrigen = iterator.next();
            if (iterator.hasNext()) jsonResopnse = jsonResopnse + "\"" + aeropuertoOrigen + "\"" + ",";
            else jsonResopnse = jsonResopnse + "\"" + aeropuertoOrigen + "\"";
        }

        jsonResopnse = jsonResopnse + "]\n" + "  },\n" + "  \"AeropuertosDestino\": {\n" + "    \"Aeropuertos\": [";
        Iterator<String> iterator2 = aeropuertosDestino.iterator();
        while (iterator2.hasNext()){
            String aeropuertoDestino = iterator2.next();
            if (iterator2.hasNext()) jsonResopnse = jsonResopnse + "\"" + aeropuertoDestino + "\"" + ",";
            else jsonResopnse = jsonResopnse + "\"" + aeropuertoDestino + "\"";
        }
        jsonResopnse = jsonResopnse + "]\n" + "  }\n" + "}";

        aeropuertosOrigen.clear();
        aeropuertosDestino.clear();

        return jsonResopnse;
    }
}
