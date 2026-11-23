package entidades;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

	protected Administrador() {}

    public Administrador(Usuario.Infos infos) {
        super(infos);
    }

	@Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}