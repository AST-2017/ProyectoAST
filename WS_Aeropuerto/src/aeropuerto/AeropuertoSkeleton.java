/**
 * AeropuertoSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package aeropuerto;


//iahsdsanzd
public class AeropuertoSkeleton {
    /**
     * getInfoAeropuertoDestinoOperation
     *
     * @param getInfoAeropuertoDestino
     * @return getInfoAeropuertoDestinoResponse
     */
    public aeropuerto.GetInfoAeropuertoDestinoResponse getInfoAeropuertoDestinoOperation(
        aeropuerto.GetInfoAeropuertoDestino getInfoAeropuertoDestino) {
        GetInfoAeropuertoDestinoResponse getInfoAeropuertoDestinoResponse = new GetInfoAeropuertoDestinoResponse();
        getInfoAeropuertoDestinoResponse.setInfoAeropuertoDestino(getInfoAeropuertoDestino.getCiudadDestino() + " respuesta");
        return getInfoAeropuertoDestinoResponse;
    }

    /**
     * getInfoAeropuertoOrigenOperation
     *
     * @param getInfoAeropuertoOrigen
     * @return getInfoAeropuertoOrigenResponse
     */
    public aeropuerto.GetInfoAeropuertoOrigenResponse getInfoAeropuertoOrigenOperation(
        aeropuerto.GetInfoAeropuertoOrigen getInfoAeropuertoOrigen) {
        GetInfoAeropuertoOrigenResponse getInfoAeropuertoOrigenResponse = new GetInfoAeropuertoOrigenResponse();
        getInfoAeropuertoOrigenResponse.setInfoAeropuertoOrigen(getInfoAeropuertoOrigen.getCiudadOrigen());
        return getInfoAeropuertoOrigenResponse;
    }
}
