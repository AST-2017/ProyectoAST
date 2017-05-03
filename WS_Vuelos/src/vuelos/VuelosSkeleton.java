package vuelos;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ServiceLifeCycle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * VuelosSkeleton java skeleton for the axisService
 *
 * Funcionalidad:
 *
 * Cuando el cliente realiza una peticion, el Orquestador,
 * llamara al servicio web Vuelos, pasandole el aeropuerto
 * de origen, el aeropuerto de destino, la fecha de salida y
 * la fecha de regreso. Acto seguido, el servicio web de
 * Vuelos se encargará de contactar con el servicio REST de
 * SkyScanner para obtener los mejores precios para las fechas
 * indicadas, así mismo, usará la base de datos local para obtener
 * vuelos que SkyScanner no tiene. Posteriormente devevolvera la
 * respuesta de toda la informacion  usando SOAP, dentro de la
 * etiqueta vuelos utilizando JSON.
 *
 *
 *
 * Tratamiento de errores:
 *
 * Los errores, ya sea por algun parámetro incorrecto, por
 * problemas de SkyScanner o porque no hay vuelos Destino-Origen
 * para las fechas seleccionadas, se les notificara al Cliente, por
 * medio también de un mensaje JSON.
 */
@SuppressWarnings("Duplicates")
@WebService
public class VuelosSkeleton implements ServiceLifeCycle{
    private static ArrayList<Salidas> salidasArrayList = new ArrayList<>();
    private static ArrayList<Regresos> regresosArrayList = new ArrayList<>();
    private static ArrayList<SalidasRegresos> salidasRegresosArrayList = new ArrayList<>();
    private static Publish sp = new Publish();

    public void startUp(ConfigurationContext context, AxisService service) {
        String servicio = "Vuelos";
        String interfaceName = "en0";
        String ip="";
        NetworkInterface networkInterface;

        try {
            networkInterface = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
            InetAddress currentAddress;
            while (inetAddress.hasMoreElements()) {
                currentAddress = inetAddress.nextElement();
                if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                    ip = currentAddress.toString();
                    break;
                }
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }

        String endpoint = "http:/"+ip+":8081/axis2/services/Vuelos";
        sp.publish(servicio, endpoint);
    }

    public void shutDown(ConfigurationContext context, AxisService service) {
      sp.unpublish();
    }

    /**
     * Metodo getInfoVuelos:
     * llama al metodo getResponseSkyScanner que devuelve
     * la consulta en formato JSON, se alamcena en la etiqueta
     * <vuelos></vuelos> del mensaje SOAP de respuesta.
     *
     * @return getInfoVuelosResponse
     */
    @WebMethod
    public vuelos.GetInfoVuelosResponse getInfoVuelos(vuelos.GetInfoVuelos getInfoVuelos) throws IOException, JSONException {
        GetInfoVuelosResponse getInfoVuelosResponse = new GetInfoVuelosResponse();
        String originAirport = getInfoVuelos.getOriginAirport();
        String destinationAirport = getInfoVuelos.getDestinationAirport();
        String outboundDate = getInfoVuelos.getOutboundDate();
        String inboundDate = getInfoVuelos.getInboundDate();
        String responseClient;

        String responseSkyScanner = getResponseSkyScanner(originAirport, destinationAirport, outboundDate, inboundDate);

        JSONObject object = new JSONObject(responseSkyScanner);
        if (object.has("ValidationErrors")) responseClient = responseSkyScanner;
        else responseClient = creteJSON(responseSkyScanner);

        getInfoVuelosResponse.setVuelos(responseClient);

        return getInfoVuelosResponse;
    }

    /**
     * Este metodo, contacta con el servicio de SkyScanner,
     * obteniendo los resultados en base a los siguientes
     * parametros:
     *
     * @param originAirport      aeropuerto de origen.
     * @param destinationAirport aeropuerto de destino.
     * @param outboundDate       fecha de salida.
     * @param inboundDate        fecha de regreso.
     * @return respuesta de la consulta en formato JSON.
     */
    private static String getResponseSkyScanner(String originAirport, String destinationAirport, String outboundDate,
                                               String inboundDate){
        Logger.getRootLogger().setLevel(Level.OFF);
        String response = "";
        String aux;
        String request = originAirport + "/" + destinationAirport + "/" + outboundDate + "/" + inboundDate;
        URL url;
        BufferedReader bufferedReader;

        try {
            url = new URL("http://partners.api.skyscanner.net/apiservices/browsequotes/v1.0/ES/eur/es-ES/"
                    + request + "?apikey=prtl6749387986743898559646983194");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            switch (connection.getResponseCode()){
                case 200:
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((aux = bufferedReader.readLine()) != null) response = response + aux;

                    break;
                default:
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    while ((aux = bufferedReader.readLine()) != null) response = response + aux;

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("ValidationErrors");
                    JSONObject object = jsonArray.getJSONObject(0);
                    String mensaje = object.getString("Message");

                    if (mensaje.equals("Date in the past")){
                        switch (jsonArray.length()){
                            case 1:
                                if (object.getString("ParameterName").equals("OutboundDate"))
                                    response = tratamientoErrores("Valor incorrecto en la fecha de salida " +
                                            object.getString("ParameterValue") + " [fecha en el pasado].");
                                else
                                    response = tratamientoErrores("Valor incorrecto en la fecha de regreso " +
                                            object.getString("ParameterValue") + " [fecha en el pasado].");
                                break;
                            default:
                                response = tratamientoErrores("Valores incorrectos en la fecha de salida "
                                        + outboundDate + " y en la fecha de regreso " + inboundDate + " [fechas en el pasado].");
                                break;
                        }
                    }else{
                        if (mensaje.contains("must be later"))
                            response = tratamientoErrores("Valores incorrectos, la fecha de regreso "
                                    +inboundDate+" debe ser posterior a la de salida "+outboundDate+".");
                        else
                            if (mensaje.contains("Incorrect date format"))
                                response = tratamientoErrores("Formato de fecha incorrecta, recuerde [aaaa-mm-dd], ejemplo: 2017-09-10");
                            else
                                response = tratamientoErrores(mensaje);
                    }

                    break;
            }

            connection.disconnect();
        } catch (IOException e) {
            return tratamientoErrores(e.getMessage());
        }

        return response;
    }

    /**
     * Metodo que recibe un string en formato JSON con la respuesta
     * de SkyScanner y devuelve en fromato JSON, los datos necesarios
     * para la respuesta al cliente.
     *
     * @param responseSkyScanner respuesta de SkyScanner.
     * @return respuesta al Orquestador en JSON.
     *
     * Si SkyScanner no devueve en la respuesta ninguna
     * salida o ninguna llegada, solo tomaremos como valida la
     * parte conjunta salidaEntrada.
     * <p>
     * Formato de la respuesta JSON: la espuesta podrá contener
     * un array del siguiente tipo:
     * <p>
     * "Ofertas" con los atributos correspondientes a la clase SalidasRegresos.java
     */

    private static String creteJSON(String responseSkyScanner) throws JSONException, IOException {
        JSONObject object = new JSONObject(responseSkyScanner);
        JSONArray array = object.getJSONArray("Quotes");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object1 = array.getJSONObject(i);
            if (object1.has("OutboundLeg") && object1.has("InboundLeg")) {
                salidasRegresos(object, object1);
            } else {
                if (object1.has("OutboundLeg")) {
                    salidas(object, object1);
                } else {
                    regresos(object, object1);
                }
            }
        }

        if (!salidasArrayList.isEmpty() && !regresosArrayList.isEmpty()) {
            for (int i = 0; i < salidasArrayList.size(); i++) {
                for (int j = 0; j < regresosArrayList.size(); j++) {
                    SalidasRegresos salidasRegresos = new SalidasRegresos(salidasRegresosArrayList.size() + 1,
                            salidasArrayList.get(i).getPrecio() + regresosArrayList.get(j).getPrecio(),
                            salidasArrayList.get(i).getVueloDirecto(),
                            regresosArrayList.get(j).getVueloDirecto(),
                            salidasArrayList.get(i).getFecha(),
                            regresosArrayList.get(j).getFecha(),
                            salidasArrayList.get(i).getOrigen(),
                            salidasArrayList.get(i).getDestino(),
                            regresosArrayList.get(j).getOrigen(),
                            regresosArrayList.get(j).getDestino(),
                            salidasArrayList.get(i).getIataCodeOrigen(),
                            salidasArrayList.get(i).getIataCodeDestino(),
                            regresosArrayList.get(j).getIataCodeOrigen(),
                            regresosArrayList.get(j).getIataCodeDestino(),
                            salidasArrayList.get(i).getAerolinea(),
                            regresosArrayList.get(j).getAerolinea()
                    );
                    salidasRegresosArrayList.add(salidasRegresos);
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        String json;
        if (salidasRegresosArrayList.size() > 0) {
            json = "{\n\"Ofertas\": [";
            for (int i = 0; i < salidasRegresosArrayList.size(); i++) {
                json = json + "\n" + mapper.writeValueAsString(salidasRegresosArrayList.get(i));
                if (i < salidasRegresosArrayList.size() - 1) json = json + ",";
            }
            json = json + "]\n}";
        } else {
            json = "{\n" +
                    "  \"ValidationErrors\": [\n" +
                    "    {\n" +
                    "      \"Message\": \"No hay vuelos disponibles en esa fecha para los destinos seleccionados\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
        }
        salidasArrayList.clear();
        regresosArrayList.clear();
        salidasRegresosArrayList.clear();

        return json;
    }

    /**
     * Este metodo, se encarga de parsear la respuesta de la parte de
     * salidas (outboundleg) obtenida por SkyScanner y convertirla a un
     * formato JSON con los atributos que son necesarios.
     *
     * @param object la respuesta JSON completa.
     * @param quote  la parte de presupuestos de la respuesta JSON
     */

    private static void salidas(JSONObject object, JSONObject quote) throws JSONException {
        int precio = quote.getInt("MinPrice");
        boolean directo = quote.getBoolean("Direct");

        JSONObject outboundObject = quote.getJSONObject("OutboundLeg");
        JSONArray carrierIds = outboundObject.getJSONArray("CarrierIds");
        int[] idsAerolinea = new int[carrierIds.length()];
        for (int i = 0; i < carrierIds.length(); i++) idsAerolinea[i] = carrierIds.getInt(i);
        int idOrigen = outboundObject.getInt("OriginId");
        int idDestino = outboundObject.getInt("DestinationId");
        String fecha = outboundObject.getString("DepartureDate");
        String[] split = fecha.trim().split("T");
        fecha = split[0];

        String[] infociudadOrigen = getInfoCiudad(object, idOrigen);
        String origen = infociudadOrigen[0];
        String iataCodeOrigen = infociudadOrigen[1];

        String[] infociudadDestino = getInfoCiudad(object, idDestino);
        String destino = infociudadDestino[0];
        String iataCodeDestino = infociudadDestino[1];

        for (int i = 0; i < idsAerolinea.length; i++) {
            String aerolinea = getCarrier(object, idsAerolinea[i]);
            Salidas salidas = new Salidas(precio, directo, fecha, origen, destino, iataCodeOrigen, iataCodeDestino, aerolinea);
            salidasArrayList.add(salidas);
        }
    }

    /**
     * Este metodo, se encarga de parsear la respuesta de la parte de
     * regresos (inboundleg) obtenida por SkyScanner y convertirla a un
     * formato JSON con los atributos que son necesarios.
     *
     * @param object la respuesta JSON completa.
     * @param quote  la parte de presupuestos de la respuesta JSON
     */

    private static void regresos(JSONObject object, JSONObject quote) throws JSONException {
        int precio = quote.getInt("MinPrice");
        boolean directo = quote.getBoolean("Direct");

        JSONObject inboundObject = quote.getJSONObject("InboundLeg");
        JSONArray carrierIds = inboundObject.getJSONArray("CarrierIds");
        int[] idsAerolinea = new int[carrierIds.length()];
        for (int i = 0; i < carrierIds.length(); i++) idsAerolinea[i] = carrierIds.getInt(i);
        int idOrigen = inboundObject.getInt("OriginId");
        int idDestino = inboundObject.getInt("DestinationId");
        String fecha = inboundObject.getString("DepartureDate");
        String[] split = fecha.trim().split("T");
        fecha = split[0];

        String[] infociudadOrigen = getInfoCiudad(object, idOrigen);
        String origen = infociudadOrigen[0];
        String iataCodeOrigen = infociudadOrigen[1];

        String[] infociudadDestino = getInfoCiudad(object, idDestino);
        String destino = infociudadDestino[0];
        String iataCodeDestino = infociudadDestino[1];

        for (int i = 0; i < idsAerolinea.length; i++) {
            String aerolinea = getCarrier(object, idsAerolinea[i]);
            Regresos regresos = new Regresos(precio, directo, fecha, origen, destino, iataCodeOrigen, iataCodeDestino, aerolinea);
            regresosArrayList.add(regresos);
        }
    }

    /**
     * Este metodo, se encarga de parsear la respuesta de la parte de
     * regresos y llegadas conjuntas obtenida por SkyScanner y convertirla a un
     * formato JSON con los atributos que son necesarios.
     *
     * @param object la respuesta JSON completa.
     * @param quote  la parte de presupuestos de la respuesta JSON
     */

    private static void salidasRegresos(JSONObject object, JSONObject quote) throws JSONException {
        int precio = quote.getInt("MinPrice");
        boolean directo = quote.getBoolean("Direct");

        JSONObject outboundObject = quote.getJSONObject("OutboundLeg");
        JSONArray carrierIdsSalida = outboundObject.getJSONArray("CarrierIds");
        int[] idsAerolineaSalida = new int[carrierIdsSalida.length()];
        for (int i = 0; i < carrierIdsSalida.length(); i++) idsAerolineaSalida[i] = carrierIdsSalida.getInt(i);
        int idOrigenSalida = outboundObject.getInt("OriginId");
        int idDestinoSalida = outboundObject.getInt("DestinationId");
        String fechaSalida = outboundObject.getString("DepartureDate");
        String[] split = fechaSalida.trim().split("T");
        fechaSalida = split[0];

        JSONObject inboundObject = quote.getJSONObject("InboundLeg");
        JSONArray carrierIdsRegreso = inboundObject.getJSONArray("CarrierIds");
        int[] idsAerolineaRegreso = new int[carrierIdsRegreso.length()];
        for (int i = 0; i < carrierIdsRegreso.length(); i++) idsAerolineaRegreso[i] = carrierIdsRegreso.getInt(i);
        String fechaRegreso = inboundObject.getString("DepartureDate");
        String[] split2 = fechaRegreso.trim().split("T");
        fechaRegreso = split2[0];


        String[] infociudadOrigenSalida = getInfoCiudad(object, idOrigenSalida);
        String origenSalida = infociudadOrigenSalida[0];
        String iataCodeOrigenSalida = infociudadOrigenSalida[1];

        String[] infociudadDestinoSalida = getInfoCiudad(object, idDestinoSalida);
        String destinoSalida = infociudadDestinoSalida[0];
        String iataCodeDestinoSalida = infociudadDestinoSalida[1];

        String[] aerolineasSalida = new String[idsAerolineaSalida.length];
        for (int i = 0; i < idsAerolineaSalida.length; i++) {
            aerolineasSalida[i] = getCarrier(object, idsAerolineaSalida[i]);
        }

        String[] aerolinesRegreso = new String[idsAerolineaRegreso.length];
        for (int i = 0; i < idsAerolineaRegreso.length; i++) {
            aerolinesRegreso[i] = getCarrier(object, idsAerolineaRegreso[i]);
        }

        for (int i = 0; i < aerolineasSalida.length; i++) {
            for (int j = 0; j < aerolinesRegreso.length; j++) {
                String aerolineaSalida = aerolineasSalida[i];
                String aerolineaRegreso = aerolinesRegreso[i];
                SalidasRegresos salidasRegresos = new SalidasRegresos(salidasRegresosArrayList.size() + 1,precio, directo, directo, fechaSalida, fechaRegreso,
                        origenSalida, destinoSalida, destinoSalida, origenSalida, iataCodeOrigenSalida,
                        iataCodeDestinoSalida, iataCodeDestinoSalida, iataCodeOrigenSalida, aerolineaSalida,
                        aerolineaRegreso);
                salidasRegresosArrayList.add(salidasRegresos);
            }
        }
    }

    /**
     * Método que devuelve un array de longitud 2, conteniendo el nombre
     * y codigo iata del aeropuerto de la ciudad pedida.
     *
     * @param object   necesario el documento JSON completo
     * @param idCiudad identificador, para encontrar la ciudad y codigo iata.
     * @return (NombreCiudad,iataCode)
     */
    private static String[] getInfoCiudad(JSONObject object, int idCiudad) throws JSONException {
        JSONArray array = object.getJSONArray("Places");
        String[] infociudad = new String[2];
        for (int i = 0; i < array.length(); i++) {
            JSONObject object1 = array.getJSONObject(i);
            if (object1.getInt("PlaceId") == idCiudad) {
                infociudad[0] = object1.getString("CityName");
                infociudad[1] = object1.getString("IataCode");
            }
        }

        return infociudad;
    }

    /**
     * Método que devuelve un String con el nombre de la aerolinea
     * en funcin del carrierID dado.
     *
     * @param object    necesario el documento JSON completo
     * @param carrierID identificador, para encontrar la aerolinea
     * @return (nombre aerolinea)
     */

    private static String getCarrier(JSONObject object, int carrierID) throws JSONException {
        String aerolinea = null;
        JSONArray array = object.getJSONArray("Carriers");
        for (int i = 0; i < array.length(); i++) {
            JSONObject object1 = array.getJSONObject(i);
            if (object1.getInt("CarrierId") == carrierID) {
                aerolinea = object1.getString("Name");
            }
        }

        return aerolinea;
    }

    /**
     * Método usado para crear mensajes JSON de aviso de errores.
     *
     * @param mensaje mensaje de error personalizaco.
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
}
