package entidades;

public class Administrador extends Usuario {

    public Administrador(int id, String nome, String email, String cpf, String senha) {

        super(id, nome, email, cpf, senha);
    }
    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}