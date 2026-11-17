package entidades;

import jakarta.persistence.*;

@Entity
public class Fornecedor {
 
	@Id @GeneratedValue
    private int id;
	
    private String razaoSocial;
    private final String cnpj;
    private String email;
    private String telefone;
    private boolean ativo;
 
    public record Infos(String razaoSocial, String cnpj, String email, String telefone) {}
    
    public Fornecedor(int id, Infos infos) {
        this.id = id;
        this.razaoSocial = infos.razaoSocial();
        this.cnpj = infos.cnpj();
        this.email = infos.email();
        this.telefone = infos.telefone();
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
