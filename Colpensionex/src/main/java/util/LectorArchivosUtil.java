package util;

import model.Persona;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static util.EscritorArchivosUtil.nuevoCSV;

public class LectorArchivosUtil {

    // TODO Consultar el separador csv desde un archivo de propiedades.
    public static final String SEPARADOR_CSV = ";;";

    public static String[] leerPrimeraLineaCsv(String rutaArchivo)
        throws IOException
    {
        File archivo = new File(rutaArchivo);
        if(archivo.exists()) {
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))
            ) {
                String primeraLinea = lector.readLine();

                if (primeraLinea != null) {
                    return primeraLinea.split(LectorArchivosUtil.SEPARADOR_CSV);
                }
            }catch (Exception e){

            }
        }else{
            Fecha.guardarRegistroLog("el archivo a leer no existe con direccion : "+rutaArchivo,2, "NO EXISTE ARCHIVO");
            String [] direcciones =rutaArchivo.split("/");
            String directorio = (String) rutaArchivo.subSequence(0,rutaArchivo.length()-direcciones[direcciones.length-1].length());
            nuevoCSV(directorio, direcciones[direcciones.length-1], new Persona().getClass());
        }
        return null;
    }

    public static LinkedList<String[]> leerTodasLasLineasCsv(String rutaArchivo)
            throws IOException {
        return leerTodasLasLineasCsv(rutaArchivo,true);
    }

    public static LinkedList<String[]> leerTodasLasLineasCsv(
            String rutaArchivo, boolean esSaltarPrimera
    ) throws IOException {

        File archivo = new File(rutaArchivo);
        LinkedList<String[]> lineas = new LinkedList<>();
        if(archivo.exists()) {
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))
            ) {
                String linea;
                boolean esPrimeraLinea = (esSaltarPrimera) ? true : false;
                while ((linea = lector.readLine()) != null) {

                    if (esPrimeraLinea) {
                        esPrimeraLinea = false;
                        continue;
                    }

                    String[] arreglo = linea.split(LectorArchivosUtil.SEPARADOR_CSV);
                    lineas.add(arreglo);
                }
            }
        }else {
            throw new IOException("EL ARCHIVO NO EXISTE O EL DIRECCION INCORRECTA");
        }

        return lineas;
    }



}
