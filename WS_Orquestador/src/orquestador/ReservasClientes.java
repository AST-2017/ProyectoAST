package orquestador;


public class ReservasClientes {
    private int precio;
    private boolean vueloDirectoSalida;
    private boolean vueloDirectoRegreso;
    private String fechaSalida;
    private String fechaRegreso;
    private String origen;
    private String destino;
    private String codigoIATAOrigen;
    private String codigoIATADestino;
    private String aerolineaSalida;
    private String aerolineaRegreso;

    public ReservasClientes(int precio, boolean vueloDirectoSalida, boolean vueloDirectoRegreso, String fechaSalida,
                            String fechaRegreso, String origen, String destino, String codigoIATAOrigen,
                            String codigoIATADestino, String aerolineaSalida, String aerolineaRegreso){
        this.precio = precio;
        this.vueloDirectoSalida = vueloDirectoSalida;
        this.vueloDirectoRegreso = vueloDirectoRegreso;
        this.fechaSalida = fechaSalida;
        this.fechaRegreso = fechaRegreso;
        this.origen = origen;
        this.destino = destino;
        this.codigoIATAOrigen = codigoIATAOrigen;
        this.codigoIATADestino = codigoIATADestino;
        this.aerolineaSalida = aerolineaSalida;
        this.aerolineaRegreso = aerolineaRegreso;
    }

    public ReservasClientes(){

    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public boolean getVueloDirectoSalida() {
        return vueloDirectoSalida;
    }

    public void setVueloDirectoSalida(boolean vueloDirectoSalida) {
        this.vueloDirectoSalida = vueloDirectoSalida;
    }

    public boolean getVueloDirectoRegreso() {
        return vueloDirectoRegreso;
    }

    public void setVueloDirectoRegreso(boolean vueloDirectoRegreso) {
        this.vueloDirectoRegreso = vueloDirectoRegreso;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getFechaRegreso() {
        return fechaRegreso;
    }

    public void setFechaRegreso(String fechaRegreso) {
        this.fechaRegreso = fechaRegreso;
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

    public String getCodigoIATAOrigen() {
        return codigoIATAOrigen;
    }

    public void setCodigoIATAOrigen(String codigoIATAOrigen) {
        this.codigoIATAOrigen = codigoIATAOrigen;
    }

    public String getCodigoIATADestino() {
        return codigoIATADestino;
    }

    public void setCodigoIATADestino(String codigoIATADestino) {
        this.codigoIATADestino = codigoIATADestino;
    }

    public String getAerolineaSalida() {
        return aerolineaSalida;
    }

    public void setAerolineaSalida(String aerolineaSalida) {
        this.aerolineaSalida = aerolineaSalida;
    }

    public String getAerolineaRegreso() {
        return aerolineaRegreso;
    }

    public void setAerolineaRegreso(String aerolineaRegreso) {
        this.aerolineaRegreso = aerolineaRegreso;
    }

    public String toString() {
        return super.toString();
    }
}
