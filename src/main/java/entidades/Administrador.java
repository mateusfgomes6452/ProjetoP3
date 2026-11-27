package entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {

    protected Administrador() {
        //JPA quer.
    }

    public Administrador(Usuario.Infos infos) {
        super(infos);
    }

    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}
