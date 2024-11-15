package model;
public class Persona implements Comparable<Persona>{
    private String nombre;
    private String cedula;
    private int edad;
    private String genero; //Masculino o Femenino
    private String lugarNacimiento; //PAIS-DEPARTAMENTO-CIUDAD
    private String lugarResidencia; //PAIS-DEPARTAMENTO-CIUDAD
    private String institucionPublica; // si no es CIVIL
    private boolean prepensionado;
    private String entidadAnterior;
    private boolean hijosINPEC;
    private boolean condecorado;
    private boolean familiaresPolicias;
    private String observacionesDisciplinarias;
    private String estado; //RECHAZADO APROBADO INHABILITADO EMBARGADO

    private boolean obligadoDeclararRenta;
    public Persona(){}
    public Persona(String nombre, String cedula, int edad, String genero,
                   String lugarNacimiento, String lugarResidencia, String institucionPublica,
                   boolean prepensionado, String entidadAnterior, boolean hijosINPEC,
                   boolean condecorado, boolean familiaresPolicias,
                   String observacionesDisciplinarias, String estado, boolean obligadoDeclararRenta) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
        this.genero = genero;
        this.lugarNacimiento = lugarNacimiento;
        this.lugarResidencia = lugarResidencia;
        this.institucionPublica = institucionPublica;
        this.prepensionado = prepensionado;
        this.entidadAnterior = entidadAnterior;
        this.hijosINPEC = hijosINPEC;
        this.condecorado = condecorado;
        this.familiaresPolicias = familiaresPolicias;
        this.observacionesDisciplinarias = observacionesDisciplinarias;
        this.estado=estado;
        this.obligadoDeclararRenta=obligadoDeclararRenta;
    }

    public boolean isObligadoDeclararRenta() {
        return obligadoDeclararRenta;
    }

    public void setObligadoDeclararRenta(boolean obligadoDeclararRenta) {
        this.obligadoDeclararRenta = obligadoDeclararRenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public void setLugarNacimiento(String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public String getLugarResidencia() {
        return lugarResidencia;
    }

    public void setLugarResidencia(String lugarResidencia) {
        this.lugarResidencia = lugarResidencia;
    }

    public String getInstitucionPublica() {
        return institucionPublica;
    }

    public void setInstitucionPublica(String institucionPublica) {
        this.institucionPublica = institucionPublica;
    }

    public boolean isPrepensionado() {
        return prepensionado;
    }

    public void setPrepensionado(boolean prepensionado) {
        this.prepensionado = prepensionado;
    }

    public String getEntidadAnterior() {
        return entidadAnterior;
    }

    public void setEntidadAnterior(String entidadAnterior) {
        this.entidadAnterior = entidadAnterior;
    }

    public boolean isHijosINPEC() {
        return hijosINPEC;
    }

    public void setHijosINPEC(boolean hijosINPEC) {
        this.hijosINPEC = hijosINPEC;
    }

    public boolean isCondecorado() {
        return condecorado;
    }

    public void setCondecorado(boolean condecorado) {
        this.condecorado = condecorado;
    }

    public boolean isFamiliaresPolicias() {
        return familiaresPolicias;
    }

    public void setFamiliaresPolicias(boolean familiaresPolicias) {
        this.familiaresPolicias = familiaresPolicias;
    }

    public String getObservacionesDisciplinarias() {
        return observacionesDisciplinarias;
    }

    public void setObservacionesDisciplinarias(String observacionesDisciplinarias) {
        this.observacionesDisciplinarias = observacionesDisciplinarias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int compareTo(Persona o) {
        if(Boolean.compare(this.getEdad()<35,o.getEdad()<35)>0){
            return -1;
        }
        if(Boolean.compare(!this.isObligadoDeclararRenta(),!o.isObligadoDeclararRenta())>0){
            return 0;
        }
       return 1;


    }
}