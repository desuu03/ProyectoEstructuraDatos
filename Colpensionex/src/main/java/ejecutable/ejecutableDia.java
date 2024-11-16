package ejecutable;

import CSV.CaracterizadoDao;
import CSV.PersonaDao;
import model.Caracterizado;
import model.Persona;
import util.EscritorArchivosUtil;
import util.Fecha;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.*;
import java.util.zip.*;

public class ejecutableDia {
    public static void main(String[] args) throws IOException {
       HashMap<String, Persona> encoladosCache = encoladosCache();
       PriorityQueue<Persona> encolados = new PriorityQueue<>();
       encolados.addAll(encoladosCache.values());

        // Inicialización de personas
//        Persona persona1 = new Persona("Juan Pérez", "1234567890", 35, "Masculino", "Bogotá", "Medellín", "asd", false, "Policía Nacional", true, true, false, "Ninguna", "Activo", true,"2000_10_03");
//        Persona persona2 = new Persona("María García", "9876543210", 42, "Femenino", "Cali", "Bogotá", "dsa", true, "INPEC", false, false, true, "Advertencia", "Retirado", false,"2000_10_05");
//        Persona persona3 = new Persona("Pedro Sánchez", "1122334455", 50, "Masculino", "Barranquilla", "Cartagena", "asd", false, "Ejército Nacional", true, true, false, "Ninguna", "Activo", true,"2000_10_01");
//        Persona persona4 = new Persona("Laura Martínez", "5566778899", 28, "Femenino", "Bucaramanga", "Cúcuta", "false", false, "Fiscalía General", false, false, true, "Ninguna", "Activo", false,"2000_10_10");
//        Persona persona5 = new Persona("Carlos Hernández", "2233445566", 45, "Masculino", "Santa Marta", "Medellín", "true", false, "Policía Nacional", true, true, false, "Ninguna", "Activo", true,"2000_10_23");
//        Persona persona6 = new Persona("Andrea Gómez", "7788990011", 32, "Femenino", "Pereira", "Manizales", "less", false, "Defensoría del Pueblo", false, false, false, "Advertencia", "Activo", false, "2000_10_05");
//        Persona persona7 = new Persona("Javier Rodríguez", "1122558899", 39, "Masculino", "Cali", "Palmira", "more", false, "Ejército Nacional", true, true, false, "Ninguna", "Activo", true,"2000_10_09");
//        Persona persona8 = new Persona("Claudia Ramírez", "3344556677", 53, "Femenino", "Neiva", "Ibagué", "catch", true, "INPEC", false, false, true, "Ninguna", "Retirado", false,"2000_10_13");
//        Persona persona9 = new Persona("Luis Fernández", "9988776655", 41, "Masculino", "Medellín", "Bogotá", "try", false, "Policía Nacional", true, true, false, "Advertencia", "Activo", true,"2000_10_29");
//        Persona persona10 = new Persona("Sofía Méndez", "5566443322", 29, "Femenino", "Cartagena", "Barranquilla", "nose", false, "Fiscalía General", false, false, true, "Ninguna", "Activo",false,"2000_10_04");
//        encolados.addAll(List.of(new Persona[]{persona1, persona2, persona3,persona4,persona5
//                                                ,persona6,persona7,persona8,persona9,persona10}));
       procesarEncolados(encolados);
       moverCaracterizaciones();
       comprimirCarpeta();
    }

    private static void procesarEncolados(PriorityQueue<Persona> encolados) throws IOException {
        LinkedList<Persona> encoladosProcesadosDia = new LinkedList<>();

        int aceptados =0;
        while(encolados.size()!=0 && aceptados!=100){
            Persona personaAux = encolados.poll();
            personaAux.setEstado("Aceptado");
            personaAux.setFechaModifacion(Fecha.fechaActual());
            encoladosProcesadosDia.add(personaAux);
            EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Cotizantes",personaAux);
            //System.out.println(personaAux.getNombre()+", edad > "+personaAux.getEdad()+", declararRenta > "+personaAux.isObligadoDeclararRenta());
            aceptados++;
        }
        EscritorArchivosUtil.escribirTodasPersonas(encoladosProcesadosDia, "empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaActual()+"/solicitantesAceptados");
    }

    public static HashMap<String, Persona> encoladosCache () throws IOException {
        // Cargar encolados
        PersonaDao encoladosDao = new PersonaDao("src/main/java/recursos/encolados");
        List<Persona> encolados = encoladosDao.obtenerTodos();
        HashMap<String, Persona> encoladosCache = new HashMap<>();
        for (Persona encolado : encolados){
            encoladosCache.put(encolado.getCedula(),encolado);
        }
        return encoladosCache;
    }

    public static void moverCaracterizaciones(){
        Path carpetaOrigen = Paths.get("empleado/Caracterizaciones Entrantes");
        Path carpetaDestino = Paths.get("src/main/java/recursos/CaracterizacionesEnProceso");

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
        Path carpetaOrigen = Paths.get("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaDiaAnterior());
        Path carpetaZip = Paths.get("empleado/Diario/SolicitudesProcesadas_"+Fecha.fechaDiaAnterior()+".zip");

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


    private static void crearCarpetaDia(){
        Path direccionCarpeta = Paths.get("empleado/Diario/SolicitudesProcesadas_"+ Fecha.fechaActual());
    }

}
