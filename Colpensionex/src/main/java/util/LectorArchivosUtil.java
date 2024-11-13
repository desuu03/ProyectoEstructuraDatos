package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class LectorArchivosUtil {

    // TODO Consultar el separador csv desde un archivo de propiedades.
    public static final String SEPARADOR_CSV = ";;";

    public static String[] leerPrimeraLineaCsv(String rutaArchivo)
        throws IOException
    {
        File archivo = new File(rutaArchivo);

        try(BufferedReader lector = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))
        ) {
            String primeraLinea = lector.readLine();

            if(primeraLinea != null) {
                return primeraLinea.split(LectorArchivosUtil.SEPARADOR_CSV);
            }
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

        try(BufferedReader lector = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))
        ) {
            String linea;
            boolean esPrimeraLinea = (esSaltarPrimera) ? true: false;
            while( (linea = lector.readLine()) != null  ) {

                if(esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }

                String[] arreglo = linea.split(LectorArchivosUtil.SEPARADOR_CSV);
                lineas.add( arreglo );
            }
        }

        return lineas;
    }
}
