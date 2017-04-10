package vuelos;

public class SalidasRegresos {
    private int precio;
    private boolean vueloDirecto;
    private String fechaSalida;
    private String fechaRegreso;
    private String origenSalida;
    private String destinoSalida;
    private String origenRegreso;
    private String destinoRegreso;
    private String iataCodeOrigenSalida;
    private String iataCodeDestinoSalida;
    private String iataCodeOrigenRegreso;
    private String iataCodeDestinoRegreso;
    private String aerolineaSalida;
    private String aerolineaRegreso;

    public SalidasRegresos(int precio,boolean vueloDirecto, String fechaSalida, String fechaRegreso,
                           String origenSalida,String destinoSalida, String origenRegreso, String destinoRegreso,
                           String iataCodeOrigenSalida, String iataCodeDestinoSalida, String iataCodeOrigenRegreso,
                           String iataCodeDestinoRegreso,String aerolineaSalida, String aerolineaRegreso){
        this.precio = precio;
        this.vueloDirecto = vueloDirecto;
        this.fechaSalida = fechaSalida;
        this.fechaRegreso = fechaRegreso;
        this.origenSalida = origenSalida;
        this.destinoSalida = destinoSalida;
        this.origenRegreso = origenRegreso;
        this.destinoRegreso = destinoRegreso;
        this.iataCodeOrigenSalida = iataCodeOrigenSalida;
        this.iataCodeDestinoSalida = iataCodeDestinoSalida;
        this.iataCodeOrigenRegreso = iataCodeOrigenRegreso;
        this.iataCodeDestinoRegreso = iataCodeDestinoRegreso;
        this.aerolineaSalida = aerolineaSalida;
        this.aerolineaRegreso = aerolineaRegreso;
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

    public String getOrigenSalida() {
        return origenSalida;
    }

    public void setOrigenSalida(String origenSalida) {
        this.origenSalida = origenSalida;
    }

    public String getDestinoSalida() {
        return destinoSalida;
    }

    public void setDestinoSalida(String destinoSalida) {
        this.destinoSalida = destinoSalida;
    }

    public String getOrigenRegreso() {
        return origenRegreso;
    }

    public void setOrigenRegreso(String origenRegreso) {
        this.origenRegreso = origenRegreso;
    }

    public String getDestinoRegreso() {
        return destinoRegreso;
    }

    public void setDestinoRegreso(String destinoRegreso) {
        this.destinoRegreso = destinoRegreso;
    }

    public String getIataCodeOrigenSalida() {
        return iataCodeOrigenSalida;
    }

    public void setIataCodeOrigenSalida(String iataCodeOrigenSalida) {
        this.iataCodeOrigenSalida = iataCodeOrigenSalida;
    }

    public String getIataCodeDestinoSalida() {
        return iataCodeDestinoSalida;
    }

    public void setIataCodeDestinoSalida(String iataCodeDestinoSalida) {
        this.iataCodeDestinoSalida = iataCodeDestinoSalida;
    }

    public String getIataCodeOrigenRegreso() {
        return iataCodeOrigenRegreso;
    }

    public void setIataCodeOrigenRegreso(String iataCodeOrigenRegreso) {
        this.iataCodeOrigenRegreso = iataCodeOrigenRegreso;
    }

    public String getIataCodeDestinoRegreso() {
        return iataCodeDestinoRegreso;
    }

    public void setIataCodeDestinoRegreso(String iataCodeDestinoRegreso) {
        this.iataCodeDestinoRegreso = iataCodeDestinoRegreso;
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
}
