package ejecutable;

import CSV.CaracterizadoDao;
import CSV.PersonaDao;
import model.Caracterizado;
import model.Persona;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.nio.file.*;
import java.util.zip.*;

public class ejecutableDia {
    public static void main(String[] args) throws IOException {
        HashMap<String, Persona> encoladosCache = encoladosCache();
        HashMap<String, Caracterizado> caracterizadosFiscaliaCache = caracterizacionFiscaliaCache();
        HashMap<String, Caracterizado> caraterizadosContraloriaCache = caracterizacionContraloriaCache();
        HashMap<String, Caracterizado> caracterizadosProcaduriaCache = caracterizacionProcaduriaCache();
        HashMap<String, Persona> cotizantesCache = cotizantesCache();
        HashMap<String, Persona> inhabilitadosCache = inhabilitadosCache();


    }

    public static HashMap<String, Persona> encoladosCache () throws IOException {
        // Cargar encolados
        PersonaDao encoladosDao = new PersonaDao("recursos/encolados");
        List<Persona> encolados = encoladosDao.obtenerTodos();
        HashMap<String, Persona> encoladosCache = new HashMap<>();
        for (Persona encolado : encolados){
            encoladosCache.put(encolado.getCedula(),encolado);
        }
        return encoladosCache;
    }
    public static HashMap<String, Caracterizado> caracterizacionFiscaliaCache () throws IOException {
        // Cargar caracterizados por fiscalia
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("recursos/CaracterizacionesEnProceso/fiscalia");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    } public static HashMap<String, Caracterizado> caracterizacionProcaduriaCache () throws IOException {
        // Cargar caracterizados por procaduria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("recursos/CaracterizacionesEnProceso/procaduria");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    } public static HashMap<String, Caracterizado> caracterizacionContraloriaCache () throws IOException {
        // Cargar caracterizados por contraloria
        CaracterizadoDao caracterizadoDao = new CaracterizadoDao("recursos/CaracterizacionesEnProceso/contraloria");
        List<Caracterizado> caracterizados = caracterizadoDao.obtenerTodos();
        HashMap<String, Caracterizado> caracterizadosCache = new HashMap<>();
        for (Caracterizado caracterizado : caracterizados){
            caracterizadosCache.put(caracterizado.getDocumento(),caracterizado);
        }
        return caracterizadosCache;
    } public static HashMap<String, Persona> cotizantesCache () throws IOException {
        // Cargar cotizantes
        PersonaDao cotizantesDao = new PersonaDao("empleado/Base de datos/cotizantes");
        List<Persona> cotizantes = cotizantesDao.obtenerTodos();
        HashMap<String, Persona> cotizantesCache = new HashMap<>();
        for (Persona cotizante : cotizantes){
            cotizantesCache.put(cotizante.getCedula(),cotizante);
        }
        return cotizantesCache;
    }
    public static HashMap<String, Persona> inhabilitadosCache () throws IOException {
        // Cargan encolados
        PersonaDao inhabilitadosDao = new PersonaDao("empleado/Base de datos/inhabilitados");
        List<Persona> inhabilitados = inhabilitadosDao.obtenerTodos();
        HashMap<String, Persona> inhabilitadosCache = new HashMap<>();
        for (Persona inhabilitado : inhabilitados){
            inhabilitadosCache.put(inhabilitado.getCedula(),inhabilitado);
        }
        return inhabilitadosCache;
    }

    public static void moverCaracterizaciones(){
        Path carpetaOrigen = Paths.get("empleado/Caracterizaciones Entrantes");
        Path carpetaDestino = Paths.get("recursos/CaracterizacionesEnProceso");

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

    public static void comprimirCarpeta (){
        Path carpetaOrigen = Paths.get("empleado/Diario/SolicitudesProcesadas_"+fechaDiaAnterior());
        Path carpetaZip = Paths.get("empleado/Diario/SolicitudesProcesadas_"+fechaDiaAnterior()+".zip");

        try {
            // Crear un flujo de salida para el archivo ZIP
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(carpetaZip.toFile()))) {

                // Comprimir la carpeta de origen
                comprimirCarpeta(carpetaOrigen, zos, carpetaOrigen);

                System.out.println("Carpeta comprimida correctamente.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void comprimirCarpeta(Path carpetaOrigen, ZipOutputStream zos, Path carpetaRaiz) throws IOException {
        //Definiendo las direcciones de origen y destino, ademas del flujo de datos del ZIP (el ZipOutputStream)

        // Obtener la lista de archivos y carpetas en la carpeta de origen
        DirectoryStream<Path> stream = Files.newDirectoryStream(carpetaOrigen);

        for (Path archivo : stream) {
            // Si es un directorio, recursivamente comprímelo
            if (Files.isDirectory(archivo)) {
                // Añadir el directorio al archivo ZIP (manteniendo su estructura)
                String nombreDirectorio = carpetaRaiz.relativize(archivo).toString() + "/";
                ZipEntry zipEntry = new ZipEntry(nombreDirectorio);
                zos.putNextEntry(zipEntry);
                zos.closeEntry(); // Solo añadir la entrada del directorio

                // Llamar de forma recursiva para comprimir los contenidos del directorio
                comprimirCarpeta(archivo, zos, carpetaRaiz);
            } else {
                // Si es un archivo, añadirlo al archivo ZIP
                String nombreArchivo = carpetaRaiz.relativize(archivo).toString();
                ZipEntry zipEntry = new ZipEntry(nombreArchivo);
                zos.putNextEntry(zipEntry);

                // Escribir el archivo en el archivo ZIP
                Files.copy(archivo, zos);

                // Cerrar la entrada del archivo en el ZIP
                zos.closeEntry();
            }
        }
    }

    private static String fechaActual(){
        LocalDate localDate = LocalDate.now();
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        return anio+"_"+mes+"_"+dia;
    }
    private static String fechaDiaAnterior(){
        LocalDate localDate = LocalDate.now();
        localDate.minusDays(1);
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        return anio+"_"+mes+"_"+dia;
    }


}
