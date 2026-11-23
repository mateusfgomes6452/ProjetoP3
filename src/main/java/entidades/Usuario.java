package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public abstract class Usuario {

	@Id 
    private Long id; 
	@Column(nullable = false, unique = true)
	
    private String nome;
    private String cpf; //não deve ser modificada (final, não criar set)
    private String email;
    private String senha;
    private boolean ativo;
    
    protected Usuario() {}
    
    public record Infos(String nome, String email, String cpf, String senha) {}

    protected Usuario(Infos infos) {
        this.nome = infos.nome();
        this.email = infos.email();
        this.cpf = infos.cpf();
        this.senha = infos.senha();
        this.ativo = true;
    }

    public abstract String getTipo();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
