package entidades;

public class Funcionario extends Usuario {

    public Funcionario(String nome, String email, String cpf, String senha) {

    	super(new Usuario.Infos(nome, email, cpf, senha));
    }
    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }

}