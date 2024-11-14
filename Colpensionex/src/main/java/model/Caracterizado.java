package model;

public class Caracterizado {
    private String tipoDocumento;
    private String documento;
    private String nombre;
    private String caracterizacion; // INHABILITAR o EMBARGAR prueba

    public Caracterizado(){}

    public Caracterizado(String tipoDocumento, String documento,
                         String nombre, String caracterizacion) {
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.nombre = nombre;
        this.caracterizacion = caracterizacion;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCaracterizacion() {
        return caracterizacion;
    }

    public void setCaracterizacion(String caracterizacion) {
        this.caracterizacion = caracterizacion;
    }
}