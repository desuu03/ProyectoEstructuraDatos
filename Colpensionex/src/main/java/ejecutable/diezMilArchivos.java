package ejecutable;
import com.github.javafaker.Faker;
import model.Persona;
import util.EscritorArchivosUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class diezMilArchivos {

    public static void main(String[] args) throws IOException, InterruptedException {
        int cantidadArchivos = 1;
        int cantidadPersonas = 100;
        ExecutorService ejecutadorArchivos = Executors.newFixedThreadPool(10);
        ExecutorService ejecutadorPersonas = Executors.newFixedThreadPool(5);
        CountDownLatch contadorArchivos = new CountDownLatch(cantidadArchivos);
        CountDownLatch contadorPersonas = new CountDownLatch(cantidadPersonas);
        String ruta = "empleado/Base de datos/Solicitudes/";
        for (int i = 0; i < 1; i++) {

            int finalI = i+1;
            ejecutadorArchivos.execute(()-> {
                System.out.println("Hilo "+finalI+"para archivo");
                //proceso a hacer
                String nombreArchivo = "csv"+ finalI;
                try {
                    EscritorArchivosUtil.nuevoCSV(ruta, nombreArchivo, Persona.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (int j = 0; j < 100; j++) {

                    int finalJ = j;
                    ejecutadorPersonas.execute(()-> {
                        System.out.println("Hilo "+ finalJ +" para persona");
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
                ejecutadorPersonas.shutdown();
                contadorArchivos.countDown();
            });

        }
        contadorArchivos.await();
        ejecutadorArchivos.shutdown();

    }


    public static Persona crearPersona(){
        com.github.javafaker.Faker faker = new com.github.javafaker.Faker(new Locale("es", "CO"));

        String nombre = faker.name().fullName();
        String cedula = faker.numerify("##########"); // Cédula de 10 dígitos
        int edad = faker.number().numberBetween(12, 90);
        String genero = faker.options().option("Masculino", "Femenino");

        String lugarNacimiento = faker.address().country() + "-" + faker.address().state() + "-" + faker.address().city();
        String lugarResidencia = faker.address().country() + "-" + faker.address().state() + "-" + faker.address().city();

        String institucionPublica = faker.options().option("Mininterior", "Policía", "Minsalud", "INPEC", "Armada", "Civil");
        boolean prepensionado = faker.bool().bool();
        String entidadAnterior = faker.options().option("Porvenir", "Proteccion", "Colfondos", "Old mutual", "Fondo extranjero");

        int semanasEntidadAnterior = faker.number().numberBetween(0, 1000);
        boolean hijosINPEC = faker.bool().bool();
        boolean condecorado = faker.bool().bool();
        boolean familiaresPolicias = faker.bool().bool();

        String observacionesDisciplinarias = faker.options().option("Ninguna", "Faltas leves", "Sanción administrativa");
        String estado = faker.options().option("RECHAZADO", "APROBADO", "INHABILITADO", "EMBARGADO");

        String fechaModificacion = "";
        boolean obligadoDeclararRenta = faker.bool().bool();

        return new Persona(nombre, cedula, edad, genero, lugarNacimiento, lugarResidencia,
                institucionPublica, prepensionado, entidadAnterior, semanasEntidadAnterior,
                hijosINPEC, condecorado, familiaresPolicias, observacionesDisciplinarias,
                estado, fechaModificacion, obligadoDeclararRenta);

    }
}
