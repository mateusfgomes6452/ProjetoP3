package entidades;

import jakarta.persistence.Entity;

@Entity
public class Cliente extends Usuario {

	protected Cliente() {}

    public Cliente(Usuario.Infos infos) {
        super(infos);
    }
    
    @Override
    public String getTipo() {
        return "CLIENTE";
    }

}