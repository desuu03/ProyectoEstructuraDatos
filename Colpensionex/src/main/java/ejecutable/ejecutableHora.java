package ejecutable;

import CSV.CaracterizadoDao;
import CSV.PersonaDao;
import model.Caracterizado;
import model.Persona;
import util.EscritorArchivosUtil;
import util.Fecha;
import util.LectorArchivosUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.time.temporal.ChronoUnit.MONTHS;

public class ejecutableHora {
    static HashMap<String, Caracterizado> caracterizadosFiscaliaCache;
    static HashMap<String, Caracterizado> caraterizadosContraloriaCache;
    static HashMap<String, Caracterizado> caracterizadosProcaduriaCache;
    static HashMap<String, Persona> cotizantesCache;
    static HashMap<String, Persona> inhabilitadosCache;
    static HashMap<String, Persona> solicitudesCache;

    public static void main(String[] args) throws IOException {
        caracterizadosFiscaliaCache = caracterizacionFiscaliaCache();
        caraterizadosContraloriaCache = caracterizacionContraloriaCache();
        caracterizadosProcaduriaCache = caracterizacionProcaduriaCache();
        cotizantesCache = cotizantesCache();
        inhabilitadosCache = inhabilitadosCache();
        solicitudesCache = solicitudesCache();

    }

    private static HashMap<String, Caracterizado> caracterizacionFiscaliaCache () throws IOException {
        // Cargar caracterizados por fiscalia
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("src/main/java/recursos/CaracterizacionesEnProceso/fiscalia");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    }
    private static HashMap<String, Caracterizado> caracterizacionProcaduriaCache () throws IOException {
        // Cargar caracterizados por procaduria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("src/main/java/recursos/CaracterizacionesEnProceso/procaduria");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    }
    private static HashMap<String, Caracterizado> caracterizacionContraloriaCache () throws IOException {
        // Cargar caracterizados por contraloria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("src/main/java/recursos/CaracterizacionesEnProceso/contraloria");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    }
    private static HashMap<String, Persona> cotizantesCache () throws IOException {
        // Cargar cotizantes
        PersonaDao cotizantesDao = new PersonaDao("empleado/Base de datos/Cotizantes");
        List<Persona> cotizantes = cotizantesDao.obtenerTodos();
        HashMap<String, Persona> cotizantesCache = new HashMap<>();
        for (Persona cotizante : cotizantes){
            cotizantesCache.put(cotizante.getCedula(),cotizante);
        }
        return cotizantesCache;
    }

    private static HashMap<String, Persona> inhabilitadosCache () throws IOException {
        // Cargan encolados
        PersonaDao inhabilitadosDao = new PersonaDao("empleado/Base de datos/Inhabilitados");
        List<Persona> inhabilitados = inhabilitadosDao.obtenerTodos();
        HashMap<String, Persona> inhabilitadosCache = new HashMap<>();
        for (Persona inhabilitado : inhabilitados){
            inhabilitadosCache.put(inhabilitado.getCedula(),inhabilitado);
        }
        return inhabilitadosCache;
    }

    private static HashMap<String, Persona> solicitudesCache () throws IOException {

        String rutaDirectorio = "src/main/java/recursos/SolicitudesEnProceso";
        List<Persona> solicitudes = new ArrayList<>();
        // Crea un objeto File para el directorio
        File directorio = new File(rutaDirectorio);
        // Verifica si el directorio existe
        if (directorio.exists() && directorio.isDirectory()) {
            // Lista todos los archivos en el directorio
            File[] listaArchivos = directorio.listFiles();
            if (listaArchivos != null) {
                for (File archivo : listaArchivos) {

                    // CREO QUE HIRIA UN HILO POR ACA (creacion de hilos, para cada archivo)
                    PersonaDao solicutdesDao = new PersonaDao(archivo.getAbsolutePath());
                    solicitudes.addAll(solicutdesDao.obtenerTodos());

                }
            }
        }

        // Cargan solicitudes, creo que lleva un hilo para cargar rapido todos
        //con las solicitudes compartidas e igual la cache para procesar rapido
        HashMap<String, Persona> solicitudesCache = new HashMap<>();
        for (Persona solicitante : solicitudes) {
            solicitudesCache.put(solicitante.getCedula(), solicitante);
        }
        return solicitudesCache;
    }

    private static void moverSolicitudes(){
        Path carpetaOrigen = Paths.get("empleado/Solicitudes Entrantes");
        Path carpetaDestino = Paths.get("src/main/java/recursos/SolicitudesEnProceso");

        try {
            // Obtener todos los archivos de la carpeta de origen
            DirectoryStream<Path> stream = Files.newDirectoryStream(carpetaOrigen);

            // Iterar sobre cada archivo en la carpeta de origen
            for (Path archivo : stream) {
                // Crear la ruta de destino para cada archivo
                Path destino = carpetaDestino.resolve(archivo.getFileName());

                // Mover el archivo a la carpeta de destino
                // Si ya existe un archivo con el mismo nombre en el destino, se sobrescribirá
                Files.move(archivo, destino, StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void procesarSolicitante(Persona solicitante) throws IOException {
        procesarPorCaracterizacion(solicitante);
        if(solicitante.getEstado().equalsIgnoreCase("Inhabilitado") ||
                solicitante.getEstado().equalsIgnoreCase("Embargado")){
            System.out.println("Ya fue pasado a lista");
            return;
        }else{
            boolean aceptado= procesarPorColpensionex(solicitante);
            if(aceptado){
                solicitante.setEstado("Encolado");
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Encolados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Encolados",solicitante);
            }else{
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Rechazados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Rechazados",solicitante);
            }
        }


    }

    public static void procesarPorCaracterizacion (Persona solicitante) throws IOException {
        if(caracterizadosFiscaliaCache.containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizadosFiscaliaCache.get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                solicitante.setEstado("Inhabilitado");
                solicitante.setFechaModifacion(Fecha.fechaActual());
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Inhabilitados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Inhabilitados",solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
                solicitante.setEstado("Embargado");
                EscritorArchivosUtil.escribirPersona("src/main/java/recursos/encolados", solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Embargados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Embargados",solicitante);
            }
        }
        if(caracterizadosProcaduriaCache.containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizadosProcaduriaCache.get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                solicitante.setEstado("Inhabilitado");
                solicitante.setFechaModifacion(Fecha.fechaActual());
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Inhabilitados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Inhabilitados",solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
                solicitante.setEstado("Embargado");
                EscritorArchivosUtil.escribirPersona("src/main/java/recursos/encolados", solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Embargados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Embargados",solicitante);
            }
        }
        if(caracterizacionContraloriaCache().containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizacionContraloriaCache().get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                solicitante.setEstado("Inhabilitado");
                solicitante.setFechaModifacion(Fecha.fechaActual());
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Inhabilitados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Inhabilitados",solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
                solicitante.setEstado("Embargado");
                EscritorArchivosUtil.escribirPersona("src/main/java/recursos/encolados", solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Embargados",solicitante);
                EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Embargados",solicitante);
            }
        }
    }

    private static boolean procesarPorColpensionex(Persona solicitante) throws IOException {
        String cedulaSolicitante = solicitante.getCedula();
        if(solicitante.getEstado().equalsIgnoreCase("Inhabilitado") || cotizantesCache().containsKey(cedulaSolicitante)
            || solicitante.getEstado().equalsIgnoreCase("Embargado")){
            return false;
        }
        if(inhabilitadosCache.containsKey(cedulaSolicitante)){
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDate fecha = LocalDate.parse(solicitante.getFechaModifacion(),formato);
            if(fecha.plusMonths(6).isBefore(LocalDate.now())){
                inhabilitadosCache.remove(solicitante.getCedula());
                EscritorArchivosUtil.borrarLineaCSV("empleado/Base de datos/Inhabilitados", solicitante.getCedula());
                solicitante.setFechaModifacion(Fecha.fechaActual());
                return procesarPorColpensionex(solicitante);
            }
        }
        if(solicitante.isPrepensionado()){
            return false;
        }
        String institucionPublica = solicitante.getInstitucionPublica();
        if(institucionPublica.equals("Civil")){
            String [] lugarNacimiento = solicitante.getLugarNacimiento().split("-");
            String [] lugarResidencia = solicitante.getLugarResidencia().split("-");
            if(lugarNacimiento[2].equalsIgnoreCase("Bogota") && lugarResidencia[2].equalsIgnoreCase("Bogota")){
                return false;
            }
            if(lugarNacimiento[2].equalsIgnoreCase("Medellin") && lugarResidencia[2].equalsIgnoreCase("Medellin")){
                return false;
            }
            if(lugarNacimiento[2].equalsIgnoreCase("Cali") && lugarResidencia[2].equalsIgnoreCase("Cali")){
                return false;
            }
            if(lugarNacimiento[0].contains("tan") || lugarNacimiento[1].contains("tan") || lugarNacimiento[2].contains("tan")){
                return false;
            }
            //validacion para ver si alcanza edad para aplicar regimen de prima media que es a la edad de
            if(solicitante.getGenero().equalsIgnoreCase("Femenino") && solicitante.getEdad()>57){
                return false;
            }
            if(solicitante.getGenero().equalsIgnoreCase("Masculino") && solicitante.getEdad()>62){
                return false;
            }
            String entidadAnterior = solicitante.getEntidadAnterior();
            //
            //VER LO DE SEMANAS
            //
            if(entidadAnterior.equalsIgnoreCase("Provenir")){

            }
            if(entidadAnterior.equalsIgnoreCase("Proteccion")){

            }
            if(entidadAnterior.equalsIgnoreCase("Colfondos")){

            }
            if(entidadAnterior.equalsIgnoreCase("Old Mutual")){

            }
            return true;

        }else{
            if(institucionPublica.equalsIgnoreCase("Armada")){
                if(solicitante.isCondecorado()){
                    return true;
                }
                solicitante.setInstitucionPublica("Civil");
                return procesarPorColpensionex(solicitante);
            }
            if(institucionPublica.equalsIgnoreCase("Inpec")){
                if(solicitante.isHijosINPEC()){
                    return true;
                }
                solicitante.setInstitucionPublica("Civil");
                return procesarPorColpensionex(solicitante);
            }
            if(institucionPublica.equalsIgnoreCase("Policia")){
                if(solicitante.isFamiliaresPolicias() && solicitante.getEdad()>18){
                    return true;
                }
                solicitante.setInstitucionPublica("Civil");
                return procesarPorColpensionex(solicitante);
            }
            if(institucionPublica.equalsIgnoreCase("Minsalud")){
                if(solicitante.getObservacionesDisciplinarias().equalsIgnoreCase("Ninguna")){
                    return true;
                }
                ////
                ////ALGO DE RECHAZAR
                ////
                return false;

            }
            if(institucionPublica.equalsIgnoreCase("Mininterior")){
                if(solicitante.getObservacionesDisciplinarias().equalsIgnoreCase("Ninguna")){
                    return true;
                }
                ////
                ////ALGO DE RECHAZAR
                ////
                return false;
            }
        }
      return false;
    }

}
