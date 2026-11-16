package entidades;

public class Cliente extends Usuario {

    public Cliente(int id, String nome, String email, String cpf, String senha) {

    	super(id, new Usuario.Infos(nome, email, cpf, senha));
    }
    @Override
    public String getTipo() {
        return "CLIENTE";
    }

}