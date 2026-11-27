package entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    protected Cliente() {
        //JPA quer.
    }

    public Cliente(Usuario.Infos infos) {
        super(infos);
    }

    @Override
    public String getTipo() {
        return "CLIENTE";
    }
}
