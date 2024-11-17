package ejecutable;
import com.github.javafaker.*;
import com.github.javafaker.Country;
import com.github.javafaker.Faker;
import model.Persona;
import util.EscritorArchivosUtil;

import java.io.IOException;
<<<<<<< HEAD
import java.time.LocalDateTime;
=======
import java.util.Date;
>>>>>>> 7a0fc6060e7e401546a6aa71103fe5882c87739f
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class diezMilArchivos {

    public static void main(String[] args) throws IOException, InterruptedException {
        int cantidadArchivos = 50;
        int cantidadPersonas = 100;
        ExecutorService ejecutadorArchivos = Executors.newFixedThreadPool(3);
        ExecutorService ejecutadorPersonas = Executors.newFixedThreadPool(12);
        CountDownLatch contadorArchivos = new CountDownLatch(cantidadArchivos);
        double tiempoInicio = System.currentTimeMillis();
        String ruta = "empleado/Base de datos/Solicitudes/";
        for (int i = 0; i < cantidadArchivos; i++) {
<<<<<<< HEAD
            CountDownLatch contadorPersonas = new CountDownLatch(cantidadPersonas);
=======
>>>>>>> 7a0fc6060e7e401546a6aa71103fe5882c87739f

            int finalI = i+1;
            ejecutadorArchivos.execute(()-> {
                System.out.println("Escribiendo archivo CSV # "+finalI);
                //proceso a hacer
                String nombreArchivo = "csv"+ finalI;
                try {
                    EscritorArchivosUtil.nuevoCSV(ruta, nombreArchivo, Persona.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (int j = 0; j < cantidadPersonas; j++) {

                    int finalJ = j;

                    ejecutadorPersonas.execute(()-> {
                        //proceso a hacer
                        try {
                            EscritorArchivosUtil.escribirPersona(ruta+ nombreArchivo,crearPersona());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        contadorPersonas.countDown();
                    });

                }
                try {
                    contadorPersonas.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                contadorArchivos.countDown();
            });

        }
        contadorArchivos.await();
        ejecutadorPersonas.shutdown();
        ejecutadorArchivos.shutdown();
        System.out.println("Terminó el proceso, se crearon "+ cantidadArchivos+ " archivos con "+cantidadPersonas+" personas cada uno.");
        double tiempoFin = System.currentTimeMillis();
        double tiempoTotalMinutos = (tiempoFin - tiempoInicio) / (1000 * 60);

        System.out.println("El proceso demoró " + tiempoTotalMinutos + " minutos.");
    }


    public static Persona crearPersona(){
        Faker faker = new Faker(new Locale("es", "CO"));

        String nombre = faker.name().fullName();
        String cedula = faker.numerify("##########"); // Cédula de 10 dígitos
        int edad = faker.number().numberBetween(12, 90);
        String genero = faker.options().option("Masculino", "Femenino");

        Address nac= faker.address();
        Address res = faker.address();

        String lugarNacimiento = nac.country() + "-" + nac.state() + "-" + nac.city();
        String lugarResidencia = res.country() + "-" + res.state() + "-" + res.city();

        String institucionPublica = faker.options().option("Mininterior", "Policía", "Minsalud", "INPEC", "Armada", "Civil");
        boolean prepensionado = faker.bool().bool();
        String entidadAnterior = faker.options().option("Porvenir", "Proteccion", "Colfondos", "Old mutual", "Fondo extranjero");

        int semanasEntidadAnterior = faker.number().numberBetween(0, 1000);
        boolean hijosINPEC = faker.bool().bool();
        boolean condecorado = faker.bool().bool();
        boolean familiaresPolicias = faker.bool().bool();

        String observacionesDisciplinarias = faker.options().option("Ninguna", "Faltas leves", "Sanción administrativa");
        String estado = faker.options().option("RECHAZADO", "APROBADO", "INHABILITADO", "EMBARGADO","GENERADO");

        String fechaModificacion = "";

        boolean obligadoDeclararRenta = faker.bool().bool();

        return new Persona(nombre, cedula, edad, genero, lugarNacimiento, lugarResidencia,
                institucionPublica, prepensionado, entidadAnterior, semanasEntidadAnterior,
                hijosINPEC, condecorado, familiaresPolicias, observacionesDisciplinarias,
                estado, fechaModificacion, obligadoDeclararRenta);

    }
}
