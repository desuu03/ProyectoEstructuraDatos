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
import java.security.spec.RSAOtherPrimeInfo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MONTHS;

public class ejecutableHora {
    static HashMap<String, Caracterizado> caracterizadosFiscaliaCache;
    static HashMap<String, Caracterizado> caraterizadosContraloriaCache;
    static HashMap<String, Caracterizado> caracterizadosProcaduriaCache;
    static HashMap<String, Persona> cotizantesCache;
    static HashMap<String, Persona> inhabilitadosCache;
    static HashMap<String, Persona> solicitudesCache;
    static HashMap<String, Persona> rechazadosCache;

    static HashMap<String, Persona> encoladosCache;
    public static void main(String[] args) throws IOException, InterruptedException {
        caracterizadosFiscaliaCache = caracterizacionFiscaliaCache();
        System.out.println("Cargo fiscalia");
        caraterizadosContraloriaCache = caracterizacionContraloriaCache();
        System.out.println("Cargo contraloria");
        caracterizadosProcaduriaCache = caracterizacionProcaduriaCache();
        System.out.println("Cargo procaduria");
        cotizantesCache = cotizantesCache();
        System.out.println("Cargo cotizantes");
        inhabilitadosCache = inhabilitadosCache();
        System.out.println("Paso inhabilitados");
        rechazadosCache = rechazadosCache();
        System.out.println("Cargo rechazados");
        encoladosCache = encoladosCache();
        System.out.println("Cargo encolados");
        moverSolicitudes();
        System.out.println("Movio solicitudes");
        solicitudesCache = solicitudesCache();
        System.out.println("Cargo solicitantes");
        procesarSolicitantes();
        System.out.println("Proceso solicitantes");


    }
    private static HashMap<String, Persona> rechazadosCache() throws IOException {
        PersonaDao rechazadosDao = new PersonaDao("empleado/Base de datos/Rechazados");
        List<Persona> rechazados = rechazadosDao.obtenerTodos();
        HashMap<String, Persona> rechazadosCache = new HashMap<>();
        for (Persona rechazado : rechazados){
            rechazadosCache.put(rechazado.getCedula(),rechazado);
        }
        return rechazadosCache;
    }
    private static HashMap<String, Persona> encoladosCache() throws IOException {
        PersonaDao encoladosDao = new PersonaDao("empleado/Base de datos/Encolados");
        List<Persona> encolados = encoladosDao.obtenerTodos();
        HashMap<String, Persona> encoladosCache = new HashMap<>();
        for (Persona encolado : encolados){
            encoladosCache.put(encolado.getCedula(),encolado);
        }
        return encoladosCache;
    }

    private static HashMap<String, Caracterizado> caracterizacionFiscaliaCache () throws IOException {
        // Cargar caracterizados por fiscalia
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("Colpensionex\\src\\main\\java\\recursos\\CaracterizacionesEnProceso\\fiscalia");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    }
    private static HashMap<String, Caracterizado> caracterizacionProcaduriaCache () throws IOException {
        // Cargar caracterizados por procaduria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("Colpensionex\\src\\main\\java\\recursos\\CaracterizacionesEnProceso\\procaduria");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    }
    private static HashMap<String, Caracterizado> caracterizacionContraloriaCache () throws IOException {
        // Cargar caracterizados por contraloria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("Colpensionex\\src\\main\\java\\recursos\\CaracterizacionesEnProceso\\contraluria");
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

    private static HashMap<String, Persona> solicitudesCache () throws IOException, InterruptedException {

        String rutaDirectorio = "Colpensionex\\src\\main\\java\\recursos\\SolicitudesEnProceso";
        HashMap<String, Persona> solicitudesCache = new HashMap<>();
        // Crea un objeto File para el directorio
        File directorio = new File(rutaDirectorio);
        if(!directorio.exists()){directorio.mkdirs();}
        // Verifica si el directorio existe
        if (directorio.exists() && directorio.isDirectory()) {
            // Lista todos los archivos en el directorio
            File[] listaArchivos = directorio.listFiles();
            if (listaArchivos != null) {
                //usamos un pool de 10 hilos
                ExecutorService ejecutador = Executors.newFixedThreadPool(10);
                CountDownLatch contador = new CountDownLatch(listaArchivos.length);
                for (File archivo : listaArchivos) {
                    ejecutador.execute(()-> {
                        List<Persona> solicitudes = new ArrayList<>();
                        //proceso a hacer
                        System.out.println("Hilo para el archivo : "+archivo.getName());
                        PersonaDao solicutdesDao = null;
                        try {
                            solicutdesDao = new PersonaDao(archivo.getAbsolutePath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        solicitudes.addAll(solicutdesDao.obtenerTodos());
                        archivo.deleteOnExit();
                        for (Persona solicitante : solicitudes) {
                            solicitudesCache.put(solicitante.getCedula(), solicitante);
                        }
                        System.out.println("Cargo solicitantes del archivo : "+archivo.getName());
                        contador.countDown();
                    });
                }
                contador.await();
                ejecutador.shutdown();
            }
        }

        // Cargan solicitudes, creo que lleva un hilo para cargar rapido todos
        //con las solicitudes compartidas e igual la cache para procesar rapido

        return solicitudesCache;
    }

    private static void moverSolicitudes(){
        Path carpetaOrigen = Paths.get("empleado/Solicitudes Entrantes");
        Path carpetaDestino = Paths.get("Colpensionex\\src\\main\\java\\recursos\\SolicitudesEnProceso");
        File directorioOrigen = new File(String.valueOf(carpetaOrigen));
        File directorioDestino = new File(String.valueOf(carpetaDestino));
        if(!directorioOrigen.exists()){
            directorioOrigen.mkdirs();
        }
        if(!directorioDestino.exists()){
            directorioDestino.mkdirs();
        }
        try {
            // Obtener todos los archivos de la carpeta de origen
            DirectoryStream<Path> stream = Files.newDirectoryStream(carpetaOrigen);
            // Lista todos los archivos en el directorio
            File[] listaArchivos = directorioOrigen.listFiles();

            ExecutorService ejecutador = Executors.newFixedThreadPool(10);
            CountDownLatch contador = new CountDownLatch(listaArchivos.length);
            // Iterar sobre cada archivo en la carpeta de origen
            for (Path archivo : stream) {
                ejecutador.execute(()-> {

                    System.out.println("Hilo para el archivo : "+archivo.toString());
                    // Crear la ruta de destino para cada archivo
                    Path destino = carpetaDestino.resolve(archivo.getFileName());

                    // Mover el archivo a la carpeta de destino
                    // Si ya existe un archivo con el mismo nombre en el destino, se sobrescribirá
                    try {
                        Files.move(archivo, destino, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    contador.countDown();
                });
            }
            contador.await();
            ejecutador.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void procesarSolicitantes () throws InterruptedException {
        ExecutorService ejecutador = Executors.newFixedThreadPool(10);
        CountDownLatch contador = new CountDownLatch(solicitudesCache.size());
        for (Map.Entry<String, Persona> entry : solicitudesCache.entrySet()) {
            ejecutador.execute(()-> {
                //proceso a hacer
                System.out.println("Hilo para la persona : "+entry.getValue().getNombre());
                try {
                    procesarSolicitante(entry.getValue());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                contador.countDown();
            });
        }
        ejecutador.shutdown();
    }

    public static void procesarSolicitante(Persona solicitante) throws IOException {
        if (!procesarPreProceso(solicitante)) {
            procesarPorCaracterizacion(solicitante);
            if (solicitante.getEstado().equalsIgnoreCase("Inhabilitado") ||
                    solicitante.getEstado().equalsIgnoreCase("Embargado")) {
                System.out.println("Ya fue pasado a lista");
                return;
            } else {

                if (procesarPorColpensionex(solicitante)) {
                    solicitante.setEstado("Encolado");
                    EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Encolados", solicitante);
                    EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_" + Fecha.fechaActual() + "/Encolados", solicitante);
                } else {
                    if(!solicitante.getEstado().equalsIgnoreCase("INHABILITADO") &&
                        !solicitante.getEstado().equalsIgnoreCase("RECHAZADO")) {
                        solicitante.setEstado("Rechazado");
                        EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Rechazados", solicitante);
                        EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_" + Fecha.fechaActual() + "/Rechazados", solicitante);
                    }
                }
            }

        }
    }

    private static boolean procesarPreProceso(Persona solicitante) throws IOException {
        String estado = solicitante.getEstado();
        String cedula = solicitante.getCedula();
        if(cotizantesCache.containsKey(cedula) || rechazadosCache.containsKey(cedula) ||
                inhabilitadosCache.containsKey(cedula) || encoladosCache.containsKey(cedula)){
            return true;
        }
        if(estado.equalsIgnoreCase("APROBADO")){
            cotizantesCache.put(cedula,solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Cotizantes",solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Pre-Aprobados",solicitante);
            return true;
        }
        if(estado.equalsIgnoreCase("RECHAZADO")){
            rechazadosCache.put(cedula,solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Rechazados",solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Pre-Rechazados",solicitante);
            return true;
        }
        if(estado.equalsIgnoreCase("EMBARGADO") ){
            embargarSolicitante(solicitante);
        }
        if(estado.equalsIgnoreCase("INHABILITADO")){
            if(solicitante.getFechaModifacion().isBlank() || solicitante.getFechaModifacion().isEmpty()
                || solicitante.getFechaModifacion()==null){
                solicitante.setFechaModifacion(Fecha.fechaActual());
            }
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Inhabilitados",solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Pre-Inhabilitados",solicitante);
            return true;
        }
        return false;
    }
    public static void inhabilitarSolicitante(Persona solicitante)throws IOException{
        inhabilitadosCache.put(solicitante.getCedula(),solicitante);
        solicitante.setEstado("Inhabilitado");
        solicitante.setFechaModifacion(Fecha.fechaActual());
        EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Inhabilitados",solicitante);
        EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/Inhabilitados",solicitante);
    }
    public static void embargarSolicitante(Persona solicitante) throws IOException {
        if(!encoladosCache.containsKey(solicitante.getCedula())) {
            solicitante.setEstado("Embargado");
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Encolados", solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_" + Fecha.fechaActual() + "/Encolados", solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Embargados", solicitante);
            EscritorArchivosUtil.escribirPersona("empleado/Diario/SolicitudesProcesadas_" + Fecha.fechaActual() + "/Embargados", solicitante);
        }

    }

    public static void procesarPorCaracterizacion (Persona solicitante) throws IOException {
        String estado= solicitante.getEstado();
        if(estado.equalsIgnoreCase("INHABILITADO") ||
            estado.equalsIgnoreCase("EMBARGADO") ||
            estado.equalsIgnoreCase("RECHAZADO") ||
            estado.equalsIgnoreCase("APROBADO")){
            return;
        }
        if(caracterizadosFiscaliaCache.containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizadosFiscaliaCache.get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                inhabilitarSolicitante(solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
               embargarSolicitante(solicitante);
            }
            return;
        }
        if(caracterizadosProcaduriaCache.containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizadosProcaduriaCache.get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                inhabilitarSolicitante(solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
                embargarSolicitante(solicitante);
            }
            return;
        }
        if(caraterizadosContraloriaCache.containsKey(solicitante.getCedula())){
            String tipoCaracterizacio = caracterizacionContraloriaCache().get(solicitante.getCedula()).getCaracterizacion();
            if(tipoCaracterizacio.equals("INHABILITAR")){
                inhabilitarSolicitante(solicitante);
            }
            if(tipoCaracterizacio.equals("EMBARGAR")){
                embargarSolicitante(solicitante);
            }
            return;
        }
    }

    private static boolean procesarPorColpensionex(Persona solicitante) throws IOException {
        String cedulaSolicitante = solicitante.getCedula();
        String estado= solicitante.getEstado();
        if(estado.equalsIgnoreCase("EMBARGADO") ||
                estado.equalsIgnoreCase("RECHAZADO") ||
                estado.equalsIgnoreCase("APROBADO")){
            return false;
        }
        if(solicitante.getEstado().equalsIgnoreCase("INHABILITADO") && inhabilitadosCache.containsKey(cedulaSolicitante)){
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDate fecha = LocalDate.parse(solicitante.getFechaModifacion(),formato);
            if(fecha.plusMonths(6).isBefore(LocalDate.now())){
                inhabilitadosCache.remove(solicitante.getCedula());
                EscritorArchivosUtil.borrarLineaCSV("empleado/Base de datos/Inhabilitados", solicitante.getCedula());
                solicitante.setFechaModifacion(Fecha.fechaActual());
                solicitante.setEstado("Activo");
                return procesarPorColpensionex(solicitante);
            }
        }
        if(solicitante.isPrepensionado()){
            return false;
        }
        String institucionPublica = solicitante.getInstitucionPublica();
        if(institucionPublica.equals("Civil")){
            // PAIS-DEPARTAMENTO-CIUDAD
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
            //ARREGLAR que termine en tán .... como afganistán
            if(lugarNacimiento[0].endsWith("tan") || lugarNacimiento[1].endsWith("tan") || lugarNacimiento[2].endsWith("tan")){
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
            //VER LO DE SEMANASx
            //
            if(entidadAnterior.equalsIgnoreCase("Provenir")){
                return solicitante.getSemanasEntidadEnterior() < 800;
            }
            if(entidadAnterior.equalsIgnoreCase("Proteccion")){
                return solicitante.getSemanasEntidadEnterior() < 590;
            }
            if(entidadAnterior.equalsIgnoreCase("Colfondos")){
                return solicitante.getSemanasEntidadEnterior() < 300;
            }
            if(entidadAnterior.equalsIgnoreCase("Old Mutual")){
                return solicitante.getSemanasEntidadEnterior() < 100;
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
                }else{
                    if(solicitante.isCondecorado()){
                        return true;
                    }
                    solicitante.setInstitucionPublica("Civil");
                    return procesarPorColpensionex(solicitante);
                }

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
                ////ALGO DE RECHAZAR (INHABILITAR)
                ////
                inhabilitarSolicitante(solicitante);
                return false;

            }
            if(institucionPublica.equalsIgnoreCase("Mininterior")){
                if(solicitante.getObservacionesDisciplinarias().equalsIgnoreCase("Ninguna")){
                    return true;
                }
                ////
                ////ALGO DE RECHAZAR (INHABILITAR)
                ////
                inhabilitarSolicitante(solicitante);
                return false;
            }
        }
      return false;
    }

}
