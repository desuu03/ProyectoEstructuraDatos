package CSV;

import model.Persona;

import java.io.IOException;

public class PersonaDao extends ADao<Persona,String>{
    public PersonaDao(String ruta) throws IOException {
        super(ruta);
    }

}
