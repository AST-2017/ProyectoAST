/**
 * AeropuertosSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package aeropuertos;

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
import org.json.XML;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Servicio Web encargado de recibir dos ciudades distintas
 * y devolver los aeropuertos de dichas ciudades en una
 * respuesta SOAP con cuerpo de respuesta JSON.
 */

public class AeropuertosSkeleton {
    private  TreeSet<String> aeropuertosOrigen = new TreeSet<>();
    private  TreeSet<String> aeropuertosDestino = new TreeSet<>();

    /**
     * @param getInfoAeropuerto
     * @return getInfoAeropuertoResponse
     */
    public aeropuertos.GetInfoAeropuertoResponse getInfoAeropuerto(aeropuertos.GetInfoAeropuerto getInfoAeropuerto) throws AxisFault {
        GetInfoAeropuertoResponse getInfoAeropuertoResponse = new GetInfoAeropuertoResponse();
        String ciudadOrigen = getInfoAeropuerto.getCiudadOrigen();
        String ciudadDestino = getInfoAeropuerto.getCiudadDestino();
        Logger.getRootLogger().setLevel(Level.OFF);

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

        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://www.webserviceX.NET", "ns");
        OMElement method = fac.createOMElement("GetAirportInformationByCountry", omNs);
        OMElement value = fac.createOMElement("country", omNs);
        value.setText("Spain");
        method.addChild(value);
        OMElement res = sc.sendReceive(method);

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

        getInfoAeropuertoResponse.setAeropuertos(jsonResopnse);

        return getInfoAeropuertoResponse;
    }
}
