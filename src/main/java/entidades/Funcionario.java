package entidades;

import jakarta.persistence.Entity;

@Entity
public class Funcionario extends Usuario {

	protected Funcionario() {}

    public Funcionario(Usuario.Infos infos) {
        super(infos);
    }
    
    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }

}