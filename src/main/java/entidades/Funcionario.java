package entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Usuario {

    protected Funcionario() {
        //JPA quer.
    }

    public Funcionario(Usuario.Infos infos) {
        super(infos);
    }

    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }
}
