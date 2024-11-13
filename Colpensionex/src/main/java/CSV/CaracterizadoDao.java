package CSV;

import model.Caracterizado;

import java.io.IOException;

public class CaracterizadoDao extends ADao<Caracterizado,String> {
    public CaracterizadoDao(String ruta) throws IOException {
        super(ruta);
    }
}
