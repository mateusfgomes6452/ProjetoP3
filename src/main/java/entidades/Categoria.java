package entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Categoria {
 
	@Id @GeneratedValue
    private int id;
	
    private String nome;
    private String descricao;
    private boolean ativa;
 
    public record Infos(String nome, String descricao) {}
    
    public Categoria(int id, Infos infos) {
        this.id = id;
        this.nome = infos.nome();
        this.descricao = infos.descricao();
        this.ativa = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public void desativar() {
        this.ativa = false;
    }

}
