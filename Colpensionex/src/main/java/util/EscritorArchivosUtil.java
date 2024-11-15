package util;

import model.Persona;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;

public class EscritorArchivosUtil {
    public static final String SEPARADOR_CSV = ";;";

    public static void escribirPersona(String rutaArchivo, Persona persona)
            throws IOException
    {
        try(BufferedWriter escritor = new BufferedWriter(new FileWriter(rutaArchivo, true))
        ) {
            String personaCSV = persona.getNombre()+";;"+persona.getCedula()+";;"+persona.getEdad()+";;"
                                +persona.getGenero()+";;"+persona.getLugarNacimiento()+";;"+persona.getLugarResidencia()+";;"
                                +persona.getInstitucionPublica()+";;"+persona.isPrepensionado()+";;"+persona.getEntidadAnterior()+";;"
                                +persona.isHijosINPEC()+";;"+persona.isCondecorado()+";;"+persona.isFamiliaresPolicias()+";;"
                                +persona.getInstitucionPublica()+";;"+persona.getEstado();

            escritor.write(personaCSV);
            escritor.newLine();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void nuevoCSV(String ruta, String nombreArchivo, LinkedList<Persona> personas) throws IOException {
        Field[] atributos = personas.getClass().getDeclaredFields();
        String atributosCSV =atributos[0]+"";
        for (int i=1; i< atributos.length;i++){
            atributosCSV = atributosCSV+";;"+atributos[i];
        }

        File archivo = new File(ruta, nombreArchivo);
        try { // Crea los directorios si no existen
            archivo.getParentFile().mkdirs();
            // Crea el archivo
            archivo.createNewFile();
            // Escribe en el archivo
            FileWriter escritor = new FileWriter(archivo);
            escritor.write(atributosCSV);
            escritor.close();
        } catch (IOException e) {
            System.out.println("Ocurrió un error.");
            e.printStackTrace();
        }

        escribirTodasPersonas(personas, ruta+"/"+nombreArchivo);
    }

    public static void escribirTodasPersonas(LinkedList<Persona> personas, String rutaArchivo) throws IOException {
        for(Persona persona : personas){
            escribirPersona(rutaArchivo,persona);
        }
    }


}
