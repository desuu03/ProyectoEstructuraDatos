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
        PersonaDao cotizantesDao = new PersonaDao("empleado/Base de datos/cotizantes");
        List<Persona> cotizantes = cotizantesDao.obtenerTodos();
        HashMap<String, Persona> cotizantesCache = new HashMap<>();
        for (Persona cotizante : cotizantes){
            cotizantesCache.put(cotizante.getCedula(),cotizante);
        }
        return cotizantesCache;
    }

    private static HashMap<String, Persona> inhabilitadosCache () throws IOException {
        // Cargan encolados
        PersonaDao inhabilitadosDao = new PersonaDao("empleado/Base de datos/inhabilitados");
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

    public static void procesarPorCaracterizacion (Persona solicitante){

    }

    private static boolean procesarSolicitante(Persona solicitante) throws IOException {
        if(inhabilitadosCache.containsKey(solicitante.getCedula())){
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDate fecha = LocalDate.parse(solicitante.getFechaModifacion(),formato);
            if(fecha.plusMonths(6).isBefore(LocalDate.now())){
                inhabilitadosCache.remove(solicitante.getCedula());
                EscritorArchivosUtil.borrarLineaCSV("empleado/Base de datos/Inhabilitados", solicitante.getCedula());
                solicitante.setFechaModifacion(Fecha.fechaActual());
                return procesarSolicitante(solicitante);
            }
        }
      return false;
    }

}
