package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {
 
	@Id 
    private Long id;
	@Column(nullable = false, unique = true)
	
    private String razaoSocial;
    private String cnpj; //não deve ser modificada (final, não criar set)
    private String email;
    private String telefone;
    private boolean ativo;
    
    protected Fornecedor() {}
 
    public record Infos(String razaoSocial, String cnpj, String email, String telefone) {}
    
    public Fornecedor(Infos infos) {
        this.razaoSocial = infos.razaoSocial();
        this.cnpj = infos.cnpj();
        this.email = infos.email();
        this.telefone = infos.telefone();
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    public void desativar() {
        this.ativo = false;
    }
}
