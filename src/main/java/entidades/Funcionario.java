package entidades;

public class Funcionario extends Usuario {

    public Funcionario(int id, String nome, String email, String cpf, String senha) {

        super(id, nome, email, cpf, senha);
    }
    @Override
    public String getTipo() {
        return "FUNCIONARIO";
    }

}