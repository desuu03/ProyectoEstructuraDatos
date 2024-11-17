package util;

import model.Persona;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class EscritorArchivosUtil {
    public static final String SEPARADOR_CSV = ";;";

    public static void escribirPersona(String rutaArchivo, Persona persona)
            throws IOException
    {
        File direccio = new File(rutaArchivo);
        if (!direccio.exists()){
            String [] direcciones =rutaArchivo.split("/");
            String directorio = (String) rutaArchivo.subSequence(0,rutaArchivo.length()-direcciones[direcciones.length-1].length());
            nuevoCSV(directorio, direcciones[direcciones.length-1], persona.getClass());
        }
        try(BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo, true))

        ) {
            Field[] atributos = persona.getClass().getDeclaredFields();
            atributos[0].setAccessible(true);
            String personaCSV = atributos[0].get(persona)+";;";
            for (int i = 1; i < persona.getClass().getDeclaredFields().length; i++) {
                atributos[i].setAccessible(true);
                personaCSV =personaCSV+";;"+atributos[i].get(persona);
            }

            escritor.write(personaCSV);
            escritor.newLine();
        }catch (IOException e){

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static void nuevoCSV(String ruta, String nombreArchivo, Class persona) throws IOException {
        Field[] atributos = persona.getDeclaredFields();
        String atributosCSV =atributos[0].getName();
        for (int i=1; i< atributos.length;i++){
            atributosCSV = atributosCSV+";;"+atributos[i].getName();
        }

        File archivo = new File(ruta, nombreArchivo);
        try { // Crea los directorios si no existen
            archivo.getParentFile().mkdirs();
            // Crea el archivo
            archivo.createNewFile();
            // Escribe en el archivo
            FileWriter escritor = new FileWriter(archivo);;
            escritor.write(atributosCSV);
            BufferedWriter writer = new BufferedWriter(escritor);
            writer.newLine();
            writer.close();
            escritor.close();
        } catch (IOException e) {
            System.out.println("Ocurrió un error.");
            e.printStackTrace();
        }
    }

    public static void escribirTodasPersonas(LinkedList<Persona> personas, String rutaArchivo) throws IOException {
        for (Persona persona : personas) {
            escribirPersona(rutaArchivo, persona);
        }
    }

    public static void modificarLineaCsv(String archivoCsv, String idBuscar, String nuevoValor, int atributo) throws IOException {
        File archivo = new File(archivoCsv);
        File tempArchivo = new File("temp_" + archivoCsv); // Archivo temporal

        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        BufferedWriter escritor = new BufferedWriter(new FileWriter(tempArchivo));

        String linea;
        boolean encontrado = false;

        while ((linea = lector.readLine()) != null) {
            String[] columnas = linea.split(";;"); // Asumiendo que el archivo CSV está separado por comas

            // Suponiendo que el ID es la primera columna
            if (columnas[0].equals(idBuscar)) {
                columnas[atributo] = nuevoValor; // Modificar el atributo en la posicion (o en la columna correspondiente)
                linea = String.join(";;", columnas); // Reunimos las columnas de nuevo en una línea
                encontrado = true;
            }

            escritor.write(linea);
            escritor.newLine();
        }

        lector.close();
        escritor.close();

        // Si no se encontró el ID, se puede manejar el caso si es necesario
        if (!encontrado) {
            System.out.println("No se encontró el ID especificado.");
        }

        // Si la modificación fue exitosa, reemplazar el archivo original por el archivo temporal
        if (archivo.delete()) {
            if (tempArchivo.renameTo(archivo)) {
                System.out.println("Archivo CSV modificado exitosamente.");
            } else {
                System.out.println("No se pudo renombrar el archivo temporal.");
            }
        } else {
            System.out.println("No se pudo eliminar el archivo original.");
        }
    }

    public static void borrarLineaCSV(String archivoCsv, String idBuscar) throws IOException {
        File archivo = new File(archivoCsv);
        File tempArchivo = new File("temp_" + archivoCsv); // Archivo temporal

        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        BufferedWriter escritor = new BufferedWriter(new FileWriter(tempArchivo));

        String linea;
        boolean encontrado = false;

        while ((linea = lector.readLine()) != null) {
            String[] columnas = linea.split(";;"); // Asumiendo que el archivo CSV está separado por comas

            // Suponiendo que el ID es la primera columna
            if (columnas[0].equals(idBuscar)) {
                encontrado=true;
                continue;
            }
            escritor.write(linea);
            escritor.newLine();
        }

        lector.close();
        escritor.close();

        // Si no se encontró el ID, se puede manejar el caso si es necesario
        if (!encontrado) {
            System.out.println("No se encontró el ID a eliminar.");
        }

        // Si la modificación fue exitosa, reemplazar el archivo original por el archivo temporal
        if (archivo.delete()) {
            if (tempArchivo.renameTo(archivo)) {
                System.out.println("Archivo CSV modificado exitosamente.");
            } else {
                System.out.println("No se pudo renombrar el archivo temporal.");
            }
        } else {
            System.out.println("No se pudo eliminar el archivo original.");
        }
    }




}
