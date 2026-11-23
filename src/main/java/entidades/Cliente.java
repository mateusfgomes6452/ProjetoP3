package entidades;

public class Cliente extends Usuario {

    public Cliente(String nome, String email, String cpf, String senha) {

    	super(new Usuario.Infos(nome, email, cpf, senha));
    }
    @Override
    public String getTipo() {
        return "CLIENTE";
    }

}