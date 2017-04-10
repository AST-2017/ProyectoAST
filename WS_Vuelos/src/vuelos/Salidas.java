package vuelos;

public class Salidas {
    private int precio;
    private boolean vueloDirecto;
    private String fecha;
    private String origen;
    private String destino;
    private String iataCodeOrigen;
    private String iataCodeDestino;
    private String aerolinea;

    public Salidas(int precio, boolean vueloDirecto, String fecha, String origen, String destino,
                   String iataCodeOrigen, String iataCodeDestino, String aerolinea){
        this.precio = precio;
        this.vueloDirecto = vueloDirecto;
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
        this.iataCodeOrigen = iataCodeOrigen;
        this.iataCodeDestino = iataCodeDestino;
        this.aerolinea = aerolinea;
    }

    public Salidas(){

    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public boolean getVueloDirecto() {
        return vueloDirecto;
    }

    public void setVueloDirecto(boolean vueloDirecto) {
        this.vueloDirecto = vueloDirecto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getIataCodeOrigen() {
        return iataCodeOrigen;
    }

    public void setIataCodeOrigen(String codigoIataOrigen) {
        this.iataCodeOrigen = codigoIataOrigen;
    }

    public String getIataCodeDestino() {
        return iataCodeDestino;
    }

    public void setIataCodeDestino(String iataCodeDestino) {
        this.iataCodeDestino = iataCodeDestino;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public String toString() {
        return super.toString();
    }
}
