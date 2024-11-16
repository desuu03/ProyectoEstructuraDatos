package util;

import java.time.LocalDate;

public class Fecha {
    public static String fechaActual(){
        LocalDate localDate = LocalDate.now();
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        System.out.println(anio+"_"+mes+"_"+dia);
        return anio+"_"+mes+"_"+dia;
    }
    public static String fechaDiaAnterior(){
        LocalDate localDate = LocalDate.now().minusDays(1);
        int anio = localDate.getYear();
        int mes = localDate.getMonthValue();
        int dia = localDate.getDayOfMonth();
        System.out.println(anio+"_"+mes+"_"+dia);
        return anio+"_"+mes+"_"+dia;
    }
}
