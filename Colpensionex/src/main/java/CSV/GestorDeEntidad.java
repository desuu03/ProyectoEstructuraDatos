package CSV;

import util.LectorArchivosUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Componente intermediario entre la estructura del archivo CSV y los
 * atributos del modelo.
 *
 * Se utiliza como mediador que traduce los datos que se encuentran en
 * un archivo csv hacia los atributos de una clase definida en el modelo.
 */
public class GestorDeEntidad {


    /**
     * Contiene la asociación entre el nombre de una columna del archivo csv
     * y la posición en la que se encuentra esa columna. Por ejemplo:
     *
     * El archivo csv de países tiene la siguiente estructura:
     *     idPais,nombre
     *
     * Por lo tanto la columna idPais se encuentra en la posición 0 y el nombre
     * se encuentra en la posición 1 de modo que el Mapa quedará construido de la
     * siguiente forma:
     *
     *     0 => "idPais"
     *     1 => "nombre"
     */
    private Map<Integer, String> mapeoColumnas;

    /**
     * Ruta del archivo en el que se encuentran colocados los nombres de las
     * columnas del archivo csv, dichos nombres de columnas se encuentran allí
     * ordenados en la forma en que el diseñador de la base de datos lo decida.
     */
    protected String rutaArchivo;

    /**
     * Metodo constructor.
     *
     * @param rutaArchivo Ruta del archivo en el que se encuentran colocados
     * los nombres de las columnas del archivo csv.
     */
    public GestorDeEntidad(String rutaArchivo) throws IOException {
        this.rutaArchivo = rutaArchivo;
        construirMapeo();
    }

    /**
     * Constructor del mapeo.
     *
     * Construye el mapeo de la siguiente forma:
     * - Abre el archivo CSV.
     * - Lee los nombres de las columnas.
     * - Guarda los nombres de las columnas en un mapa cuya clave es la posición
     *   en que encontró dicha columna en el archivo, y el valor es el nombre de
     *   la columna.
     */
    public void construirMapeo() throws IOException {
        this.mapeoColumnas = new HashMap<>();
        String[] primeraLinea = LectorArchivosUtil.leerPrimeraLineaCsv(this.rutaArchivo);

        for(int i = 0; i < primeraLinea.length; i++) {
            this.mapeoColumnas.put(i, primeraLinea[i]);
        }
    }

    /**
     * Buscador de todos los elementos en el archivo CSV.
     *
     * Se encarga de obtener las líneas escritas en el archivo CSV y con los datos
     * de cada línea construir una instancia de la entidad o clase indicada por
     * el parámetro denominado TipoEntidad.
     *
     * @param claseEntidad El tipo de entidad a gestionar. Es el tipo genérico que se espera
     *                    que sea parametrizado al invocar este metodo.
     * @return Una lista con los items encontrados en el CSV. Cada elemento de la lista es
     * del tipo indicado por el parámetro TipoEntidad.
     *
     * @param <ClaseEntidad> Es el tipo genérico manejado por este metodo, este tipo es
     *                     definido en el momento en que este metodo sea invocado.
     */
    public <ClaseEntidad> List<ClaseEntidad> obtenerTodos(Class<ClaseEntidad> claseEntidad) {

        LinkedList<ClaseEntidad> entidades = new LinkedList<>();

        try {
            LinkedList<String[]> lineas = LectorArchivosUtil.leerTodasLasLineasCsv(this.rutaArchivo);

            for (String[] linea : lineas) {
                ClaseEntidad instancia = claseEntidad.getDeclaredConstructor().newInstance();

                //VALIDACION DE QUE SI ESTEN BIEN LOS FORMATOS


                    //
                    for (Map.Entry<Integer, String> itemMapa : this.mapeoColumnas.entrySet()) {

                        Field atributo = claseEntidad.getDeclaredField(itemMapa.getValue());
                        atributo.setAccessible(true);
                        String value = linea[itemMapa.getKey()];

                        if (atributo.getType().equals(Integer.class) || atributo.getType().equals(int.class)) {
                            atributo.set(instancia, Integer.parseInt(value));
                        } else if (atributo.getType().equals(Double.class) || atributo.getType().equals(double.class)) {
                            atributo.set(instancia, Double.parseDouble(value));
                        } else if (atributo.getType().equals(Boolean.class) || atributo.getType().equals(boolean.class)) {
                            atributo.set(instancia, Boolean.parseBoolean(value));
                        } else if (atributo.getType().equals(Long.class) || atributo.getType().equals(long.class)) {
                            atributo.set(instancia, Long.parseLong(value));
                        } else {
                            atributo.set(instancia, value);
                        }
                    }

                    entidades.add(instancia);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entidades;
    }
}
