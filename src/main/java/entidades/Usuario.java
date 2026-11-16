package entidades;

public abstract class Usuario {

    private int id;
    private String nome;
    private final String cpf;
    private String email;
    private String senha;
    private boolean ativo;
    
    public record Infos(String nome, String email, String cpf, String senha) {}

    protected Usuario(int id, Infos infos) {
        this.id = id;
        this.nome = infos.nome();
        this.email = infos.email();
        this.cpf = infos.cpf();
        this.senha = infos.senha();
        this.ativo = true;
    }

    public abstract String getTipo();
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
