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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
@WebService
public class OrquestadorSkeleton implements ServiceLifeCycle{
    private static boolean fin = false;
    private static boolean onFault = false, onError = false;
    private static String jsonRespAeropuertos = "";
    private static String origen = "";
    private static String destino = "";
    private static String iataOrigenBBDD = null;
    private static String iataDestinoBBDD = null;
    private static String iataOrigenCallBack = null;
    private static String iataDestinoCallBack = null;

    // JDBC nombre del driver y url de la base de datos
    private static final String url = "jdbc:mysql://localhost:3306/orquestadorBBDD?useSSL=false";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    // Credenciales para la base de datos
    private static final String user = "ast";
    private static final String password = "ast";

    //Publicar en juddi
    private static Publish sp = new Publish();

    //Buscar en juddi
    private static Browse browse = new Browse();

    public void startUp(ConfigurationContext context, AxisService service) {
        String servicio = "Orquestador";
        String endpointOrquestador = "http://localhost:8081/axis2/services/Orquestador";
        sp.publish(servicio, endpointOrquestador);
    }

    public void shutDown(ConfigurationContext context, AxisService service) {
      sp.unpublish();
    }

    //Llamada asincrona.
    public static class MyCallBack implements  AxisCallback{
        public void onMessage(MessageContext messageContext) {
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

                    //Open connection
                    Connection  connection = DriverManager.getConnection(url,user,password);

                    //language=MySQL
                    String sql = "{CALL introducirIata(?,?)}";
                    CallableStatement introducirIata = connection.prepareCall(sql);
                    introducirIata.setString(1,origen);
                    introducirIata.setString(2,iataOrigenCallBack);
                    introducirIata.executeQuery();

                    introducirIata.setString(1,destino);
                    introducirIata.setString(2,iataDestinoCallBack);
                    introducirIata.executeQuery();

                    // Clean the environment:
                    introducirIata.close();
                    connection.close();

                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (arrayAerOrig.length() > 0) iataOrigenCallBack = arrayAerOrig.get(0).toString();
            if (arrayAerDest.length() > 0) iataDestinoCallBack = arrayAerDest.get(0).toString();
            fin = true;
        }
    }

    private static void limpiarVariables(){
        fin = false;
        onFault = false;
        onError = false;
        jsonRespAeropuertos = "";
        origen = "";
        destino = "";
        iataOrigenBBDD = null;
        iataDestinoBBDD = null;
        iataOrigenCallBack = null;
        iataDestinoCallBack = null;
    }

    /**
     * Método encargado de registrar un cliente en la base de datos.
     *
     * @return true: si el registro se ha realizado de forma satisfactoria.
     *         false: si ya existe un usuario con ese nombre en el sistema.
     */
    @WebMethod
    public orquestador.RegistrarClienteResponse registrarCliente(orquestador.RegistrarCliente registrarCliente)
            throws ClassNotFoundException, SQLException {
        limpiarVariables();
        RegistrarClienteResponse registrarClienteResponse = new RegistrarClienteResponse();
        registrarClienteResponse.setConfirmacion(false);
        String nombre = registrarCliente.getNombre();
        String apellido1 = registrarCliente.getApellido1();
        String apellido2 = registrarCliente.getApellido2();
        String dni = registrarCliente.getDni();
        String email = registrarCliente.getEmail();
        int telefono = Integer.parseInt(registrarCliente.getTelefono());
        String pass = registrarCliente.getPassword();

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open connection.
            Connection connection = DriverManager.getConnection(url,user,password);

            //language=MySQL
            String sql = "{CALL insertarCliente(?,?,?,?,?,?,?)}";
            CallableStatement insetarCliente = connection.prepareCall(sql);
            insetarCliente.setString(1,nombre);
            insetarCliente.setString(2,apellido1);
            insetarCliente.setString(3,apellido2);
            insetarCliente.setString(4,dni);
            insetarCliente.setString(5,email);
            insetarCliente.setInt(6,telefono);
            insetarCliente.setString(7,pass);
            insetarCliente.executeQuery();

            insetarCliente.close();
            registrarClienteResponse.setConfirmacion(true);

            //close connection.
            connection.close();
        } catch (SQLException e) {
            registrarClienteResponse.setConfirmacion(false);
        }

        return registrarClienteResponse;
    }

    /**
     * Método para comprarBillete.
     *
     * @return booleano
     */
    @WebMethod
    public orquestador.ComprarBilleteResponse comprarBillete(orquestador.ComprarBillete comprarBillete) throws AxisFault{
        limpiarVariables();
        ComprarBilleteResponse comprarBilleteResponse = new ComprarBilleteResponse();
        comprarBilleteResponse.setConfirmacion(false);
        int id_oferta = comprarBillete.getId_oferta();
        String dni = comprarBillete.getDni();
        String iban = comprarBillete.getIban();
        //TODO token.
        String token = "qwerty";
        String email = null;
        String cuentaDestino = "12345678";
        int importe = 0;
        String endpointBanco = browse.browseService("Banco");

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open connection.
            Connection connection = DriverManager.getConnection(url,user,password);

            //obtener el precio, para enviar mensaje a Banco.
            //language=MySQL
            String sql = "{CALL obtenerPrecioOferta(?,?)}";
            CallableStatement obtenerPrecioOferta = connection.prepareCall(sql);
            obtenerPrecioOferta.setString(1,dni);
            obtenerPrecioOferta.setInt(2,id_oferta);
            ResultSet resultSet = obtenerPrecioOferta.executeQuery();
            while (resultSet.next()) importe = resultSet.getInt("precio");
            obtenerPrecioOferta.close();
            
            sql = "{CALL obtenerEmail(?)}";
            CallableStatement obtenerEmail = connection.prepareCall(sql);
            obtenerEmail.setString(1,dni);
            resultSet = obtenerEmail.executeQuery();
            while (resultSet.next()) email = resultSet.getString("email");
            obtenerEmail.close();

            //TODO insertar aquí la comunicación y posterior respuesta del servicio Banco.
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
            opciones.setTo(new EndpointReference(endpointBanco));
            opciones.setAction("urn:pagar");
            servicioVuelos.setOptions(opciones);

            //Cabecera (cuenta y token)
            OMFactory fac = OMAbstractFactory.getOMFactory();
            OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns");
            OMElement header = fac.createOMElement("tokenCuenta", omNs);
            header.setText(token+"-"+iban);
            servicioVuelos.addHeader(header);

            OMElement response = servicioVuelos.sendReceive(createPayLoadBanco(String.valueOf(importe),iban,cuentaDestino,email));

            // Si el pago se realizo correctamente
            if(Boolean.valueOf(response.getFirstElement().getText())){
              //insertar reserva en base de datos.
              //language=MySQL
              sql = "{CALL insertarReserva(?,?)}";
              CallableStatement insertarReserva = connection.prepareCall(sql);
              insertarReserva.setString(1,dni);
              insertarReserva.setInt(2,id_oferta);
              insertarReserva.executeQuery();

              insertarReserva.close();
              connection.close();
              comprarBilleteResponse.setConfirmacion(true);
            }
          }catch (SQLException e){
              System.out.println(e.getMessage());
          } catch (ClassNotFoundException e) {
              e.printStackTrace();
          }

        return comprarBilleteResponse;
    }

    /**
     * Metodo para devolver al cliente todas sus reservas.
     *
     * @return un String con alguna de las siguientes opciones:
     *          a) Las reservas del cliente, en formato JSON.
     *          b) Mensaje informando de que el cliente no tiene reservas, en formato JSON.
     *          C) Mensaje informando de que no existe ningun cliente registrado con ese DNI, en formato JSON.
     *          c) Mensaje de error de sistema, en formato JSON.
     */
    @WebMethod
    public orquestador.VerReservasClienteResponse verReservasCliente(orquestador.VerReservasCliente verReservasCliente)
            throws ClassNotFoundException, IOException {
        limpiarVariables();
        VerReservasClienteResponse verReservasClienteResponse = new VerReservasClienteResponse();
        String dni = verReservasCliente.getDni();
        int precio;
        boolean vueloDirectoSalida;
        boolean vueloDirectoRegreso;
        String fechaSalida;
        String fechaRegreso;
        String origen;
        String destino;
        String codigoIATAOrigen;
        String codigoIATADestino;
        String aerolineaSalida;
        String aerolineaRegreso;
        ArrayList<ReservasClientes> reservasClientesArrayList = new ArrayList<>();

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open connection.
            Connection connection = DriverManager.getConnection(url,user,password);

            //language=MySQL
            String sql = "{CALL verReservasCliente(?)}";
            CallableStatement verReservas = connection.prepareCall(sql);
            verReservas.setString(1,dni);
            ResultSet resultSet = verReservas.executeQuery();
            while (resultSet.next()){
                precio = resultSet.getInt("precio");
                vueloDirectoSalida = resultSet.getBoolean("vueloDirectoSalida");
                vueloDirectoRegreso = resultSet.getBoolean("vueloDirectoRegreso");
                fechaSalida = resultSet.getString("fechaSalida");
                fechaRegreso = resultSet.getString("fechaRegreso");
                origen = resultSet.getString("origen");
                destino = resultSet.getString("destino");
                codigoIATAOrigen = resultSet.getString("codigoIATAOrigen");
                codigoIATADestino = resultSet.getString("codigoIATADestino");
                aerolineaSalida = resultSet.getString("aerolineaSalida");
                aerolineaRegreso = resultSet.getString("aerolineaRegreso");

                ReservasClientes reservasClientes = new ReservasClientes(
                        precio,
                        vueloDirectoSalida,
                        vueloDirectoRegreso,
                        fechaSalida,
                        fechaRegreso,
                        origen,
                        destino,
                        codigoIATAOrigen,
                        codigoIATADestino,
                        aerolineaSalida,
                        aerolineaRegreso);

                reservasClientesArrayList.add(reservasClientes);
            }
            verReservas.close();
            connection.close();

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
            String json = "{\n\"Reservas\": [";
            for (int i = 0; i < reservasClientesArrayList.size(); i++) {
                json = json + "\n" + mapper.writeValueAsString(reservasClientesArrayList.get(i));
                if (i < reservasClientesArrayList.size() - 1) json = json + ",";
            }
            json = json + "]\n}";

            verReservasClienteResponse.setReservasCliente(json);
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == 1644){
                verReservasClienteResponse.setReservasCliente(tratamientoErrores(e.getMessage()));
            }else {
                verReservasClienteResponse.setReservasCliente(
                        tratamientoErrores("Error en el sistema al intentar ver sus reservas."));
            }
        }

        reservasClientesArrayList.clear();

        return verReservasClienteResponse;
    }


    /**
     * Metodo que comprueba si existe un cliente o si la tupla(dni,password) no existe en la base de datos.
     *
     * @return:
     *           true: si el cliente esta registrado en la base de datos [tupla(dni,password)]
     *           false: si el cliente o bien no está registrado, o bien la tupla(dni,password)
     *           no es correcta.
     */
    @WebMethod
    public orquestador.ComprobarClienteRegistradoResponse comprobarClienteRegistrado(
            orquestador.ComprobarClienteRegistrado comprobarClienteRegistrado) throws SQLException, ClassNotFoundException {
        limpiarVariables();
        ComprobarClienteRegistradoResponse comprobarClienteRegistradoResponse = new ComprobarClienteRegistradoResponse();
        comprobarClienteRegistradoResponse.setConfirmacion(false);
        String dni = comprobarClienteRegistrado.getDni();
        String pass = comprobarClienteRegistrado.getPassword();

        // Register JDBC driver
        Class.forName(JDBC_DRIVER);

        // Open connection
        Connection connection = DriverManager.getConnection(url,user,password);

        //language=MySQL
        String sql = "{CALL comprobarRegistro(?,?)}";
        CallableStatement comprobarRegistro = connection.prepareCall(sql);
        comprobarRegistro.setString(1,dni);
        comprobarRegistro.setString(2,pass);
        ResultSet resultSet = comprobarRegistro.executeQuery();
        while (resultSet.next()) comprobarClienteRegistradoResponse.setConfirmacion(true);

        comprobarRegistro.close();
        connection.close();

        return comprobarClienteRegistradoResponse;
    }

    /**
     * Metodo que devolverá al cliente, una serie de ofertas,
     * en funcion de la ciudad de origen, destino y fechas de
     * salida y de regreso.
     *
     */
    @WebMethod
    public orquestador.ObtenerOfertasResponse obtenerOfertas(orquestador.ObtenerOfertas obtenerOfertas)
            throws AxisFault, SQLException, ClassNotFoundException, InterruptedException {
        limpiarVariables();
        ObtenerOfertasResponse obtenerOfertasResponse = new ObtenerOfertasResponse();
        Logger.getRootLogger().setLevel(Level.OFF);
        origen = obtenerOfertas.getCiudadOrigen();
        destino = obtenerOfertas.getCiudadDestino();
        String fechaSalida = obtenerOfertas.getFechaSalida();
        String fechaRegreso = obtenerOfertas.getFechaRegreso();
        String dni = obtenerOfertas.getDni();
        String endpointVuelos = browse.browseService("Vuelos");

        int opcion = obtenerCodigosIATA();
        switch (opcion){
            case 1:
                //uso de BBDD

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
                //opciones.setTo(new EndpointReference(endpointVuelos));
                opciones.setTo(new EndpointReference("http://localhost:8081/axis2/services/Vuelos"));
                opciones.setAction("urn:getInfoVuelos");
                servicioVuelos.setOptions(opciones);
                OMElement response = servicioVuelos.sendReceive(
                        createPayLoadVuelos(iataOrigenBBDD, iataDestinoBBDD, fechaSalida, fechaRegreso));

                obtenerOfertasResponse.setOfertas(response.getFirstElement().getText());
                insertarOfertas(response.getFirstElement().getText(),dni);

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
                        //opciones.setTo(new EndpointReference(endpointVuelos));
                        opciones.setTo(new EndpointReference("http://localhost:8081/axis2/services/Vuelos"));
                        opciones.setAction("urn:getInfoVuelos");
                        servicioVuelos.setOptions(opciones);

                        response = servicioVuelos.sendReceive(
                                createPayLoadVuelos(iataOrigenCallBack, iataDestinoCallBack, fechaSalida, fechaRegreso));

                        obtenerOfertasResponse.setOfertas(response.getFirstElement().getText());
                        insertarOfertas(response.getFirstElement().getText(),dni);
                    }
                }
                break;
        }

        return obtenerOfertasResponse;
    }

    /**
     * Método que recibe las ofertas en formato JSON y un dni y se encarga del parseo
     * de la información.
     */

    private static void insertarOfertas(String ofertasJSON, String dni){
        int idOfertaVuelo;
        int precio;
        boolean vueloDirectoSalida;
        boolean vueloDirectoRegreso;
        String fechaSalida;
        String fechaRegreso;
        String origen;
        String destino;
        String codigoIATAOrigen;
        String codigoIATADestino;
        String aerolineaSalida;
        String aerolineaRegreso;

        JSONObject object = new JSONObject(ofertasJSON);
        if (!object.has("ValidationErrors")){
            try {
                // Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //Open connection
                Connection connection = DriverManager.getConnection(url,user,password);

                //language=MySQL
                String sql = "{CALL borrarOfertas(?)}";
                CallableStatement borrarOfertas = connection.prepareCall(sql);
                borrarOfertas.setString(1,dni);
                borrarOfertas.executeQuery();

                borrarOfertas.close();
                connection.close();

                JSONArray jsonArray = object.getJSONArray("Ofertas");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objectAux = jsonArray.getJSONObject(i);

                    idOfertaVuelo = objectAux.getInt("idOferta");
                    precio = objectAux.getInt("precio");
                    vueloDirectoSalida = objectAux.getBoolean("vueloDirectoSalida");
                    vueloDirectoRegreso = objectAux.getBoolean("vueloDirectoRegreso");
                    fechaSalida = objectAux.getString("fechaSalida");
                    fechaRegreso = objectAux.getString("fechaRegreso");
                    origen = objectAux.getString("origenSalida");
                    destino = objectAux.getString("destinoSalida");
                    codigoIATAOrigen = objectAux.getString("iataCodeOrigenSalida");
                    codigoIATADestino = objectAux.getString("iataCodeDestinoSalida");
                    aerolineaSalida = objectAux.getString("aerolineaSalida");
                    aerolineaRegreso = objectAux.getString("aerolineaRegreso");

                    insertarOfertaEnBBDD(dni,idOfertaVuelo,precio,vueloDirectoSalida,vueloDirectoRegreso,
                            fechaSalida,fechaRegreso,origen,destino,codigoIATAOrigen,codigoIATADestino,
                            aerolineaSalida,aerolineaRegreso);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para insertar ofertas en la base de datos del orquestador.
     *
     */
    private static void insertarOfertaEnBBDD(String dni,int idOfertaVuelo,int precio, boolean vueloDirectoSalida,
                                             boolean vueloDirectoRegreso, String fechaSalida, String fechaRegreso,
                                             String origen,String destino,String codigoIATAOrigen,
                                             String codigoIATADestino, String aerolineaSalida, String aerolineaRegreso){

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //Open connection
            Connection connection = DriverManager.getConnection(url,user,password);

            //language=MySQL
            String sql = "{CALL insertarOferta(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            CallableStatement insertarOfertas = connection.prepareCall(sql);
            insertarOfertas.setString(1,dni);
            insertarOfertas.setInt(2,idOfertaVuelo);
            insertarOfertas.setInt(3,precio);
            insertarOfertas.setBoolean(4,vueloDirectoSalida);
            insertarOfertas.setBoolean(5,vueloDirectoRegreso);
            insertarOfertas.setString(6,fechaSalida);
            insertarOfertas.setString(7,fechaRegreso);
            insertarOfertas.setString(8,origen);
            insertarOfertas.setString(9,destino);
            insertarOfertas.setString(10,codigoIATAOrigen);
            insertarOfertas.setString(11,codigoIATADestino);
            insertarOfertas.setString(12,aerolineaSalida);
            insertarOfertas.setString(13,aerolineaRegreso);

            insertarOfertas.executeQuery();

            insertarOfertas.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
    * Metodo que obtiene, si es que existe ya sea desde nuestra BBDD
    * o desde el servicio web externo, los codigosIATA de las ciudades
    * de origen y de destino.
    *
    * @return
    *          -> 1 si se ha usado la base de datos para obtener los codigos iata
    *          -> 2 si se ha usado el servicio externo webservicesX.
    */
   private static int obtenerCodigosIATA() throws AxisFault, ClassNotFoundException, SQLException, InterruptedException {

       String endpointAeropuertos = browse.browseService("Aeropuertos");

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
       opciones.setTo(new EndpointReference("http://localhost:8081/axis2/services/Aeropuertos"));
       //opciones.setTo(new EndpointReference(endpointAeropuertos));
       opciones.setAction("urn:getInfoAeropuerto");
       servicioAeropuertos.setOptions(opciones);
       servicioAeropuertos.sendReceiveNonBlocking(createPayLoadAeropuertos(origen,destino), new MyCallBack());

       // Register JDBC driver
       Class.forName(JDBC_DRIVER);

       // Open connection:
       Connection connection = DriverManager.getConnection(url,user,password);

       //Execute a CallableStatement:
       //language=MySQL
       String sql = "{CALL obtenerCodigoIATA(?)}";

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

    /**
     * Metodo usado para la creacion del cuerpo de carga para
     * contactar con el WS_Vuelos
     *
     * @return el cuerpo del mensaje SOAP
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
     * @return el cuerpo del mensaje SOAP
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

    /**
     * Metodo usado para la creacion del cuerpo de carga para
     * contactar con el WS_Banco.
     *
     * @return el cuerpo del mensaje SOAP
     */

    private static OMElement createPayLoadBanco(String importe,String iban, String cuentaDestino,String email){
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://ws.apache.org/axis2", "ns");
        OMElement method = fac.createOMElement("pagar", omNs);

        // Importe
        OMElement im = fac.createOMElement("importe", omNs);
        im.setText(importe);
        method.addChild(im);
        // Cuenta origen
        OMElement co = fac.createOMElement("cuentaOrigen", omNs);
        co.setText(iban);
        method.addChild(co);
        // Cuenta destino
        OMElement cd = fac.createOMElement("cuentaDestino", omNs);
        cd.setText(cuentaDestino);
        method.addChild(cd);
        // Email
        OMElement des = fac.createOMElement("destinatario", omNs);
        des.setText(email);
        method.addChild(des);

        return method;
    }
}
