package ejecutable;

import CSV.CaracterizadoDao;
import CSV.PersonaDao;
import model.Caracterizado;
import model.Persona;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.nio.file.*;

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
        Path origen = Paths.get("");
        Path destino = Paths.get("C:/ruta/de/destino/archivo.txt");

        try {
            // Mueve el archivo
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("El archivo se ha movido exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al mover el archivo.");
        }
    }

}
