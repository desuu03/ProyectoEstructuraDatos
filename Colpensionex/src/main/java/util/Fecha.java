package util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Fecha {
    public static void guardarRegistroLog(String mensajeLog, int nivel, String accion)
    {
        String log = "";
        Logger LOGGER = Logger.getLogger(accion);
        FileHandler fileHandler =  null;
        try {
            fileHandler = new FileHandler("empleado/log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            switch (nivel) {
                case 1:
                    LOGGER.log(Level.INFO,accion+","+mensajeLog+","+fechaActual()) ;
                    break;

                case 2:
                    LOGGER.log(Level.WARNING,accion+","+mensajeLog+","+fechaActual()) ;
                    break;

                case 3:
                    LOGGER.log(Level.SEVERE,accion+","+mensajeLog+","+fechaActual()) ;
                    break;

                default:
                    break;
            }

        } catch (SecurityException e) {

            LOGGER.log(Level.SEVERE,e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE,e.getMessage());
            e.printStackTrace();
        }
        finally {

            fileHandler.close();
        }
    }

    public static String fechaActual(){
        LocalDate localDate = LocalDate.now();
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        //System.out.println(anio+"_"+mes+"_"+dia);
        return anio+"_"+mes+"_"+dia;
    }
    public static String fechaDiaAnterior(){
        LocalDate localDate = LocalDate.now().minusDays(1);
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        //System.out.println(anio+"_"+mes+"_"+dia);
        return anio+"_"+mes+"_"+dia;
    }
}
