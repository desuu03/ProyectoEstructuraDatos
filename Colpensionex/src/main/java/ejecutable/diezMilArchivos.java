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

    public static void main(String[] args) {
        ExecutorService ejecutadorArchivos = Executors.newFixedThreadPool(10);
        ExecutorService ejecutadorPersonas = Executors.newFixedThreadPool(10);
        String ruta = "empleado/Base de datos/Rechazados"
        for (int i = 0; i < 10000; i++) {

        }
        CountDownLatch contador = new CountDownLatch();
        for (Map.Entry<String, Persona> entry : solicitudesCache.entrySet()) {
            ejecutador.execute(()-> {
                //proceso a hacer
                EscritorArchivosUtil.escribirPersona("empleado/Base de datos/Rechazados",solicitante);

                contador.countDown();
            });
        }
        contador.await();
        ejecutador.shutdown();
    }


    public static Persona crearPersona(){
        Faker faker = new Faker(new Locale("es", "CO"));

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
