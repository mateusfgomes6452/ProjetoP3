package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {
 
	@Id
    private Long id;
	@Column(nullable = false, unique = true)
	
    private String nome;
    private String descricao;
    private boolean ativa;
    
    protected Categoria() {
    	//JPA quer. 
    }
 
    public record Infos(String nome, String descricao) {}
    
    public Categoria(Infos infos) {
        this.nome = infos.nome();
        this.descricao = infos.descricao();
        this.ativa = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
