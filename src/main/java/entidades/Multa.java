package entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "multas")
public class Multa {

	@Id
    private Long id; //não deve ser modificada (final, não criar set)
	@Column(nullable = false, unique = true)
	
    private Long idContrato; //não deve ser modificada (final, não criar set)
    private String tipo; //não deve ser modificada (final, não criar set)
    private String descricao; //não deve ser modificada (final, não criar set)
    private double valor;
    private boolean paga;
    private String dataCriacao; //não deve ser modificada (final, não criar set)
    
    protected Multa() {}

    public Multa(Long idContrato, String tipo, String descricao, double valor, String dataCriacao) {
    	this.idContrato = idContrato;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.paga = false;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public boolean isPaga() {
        return paga;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    
    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setPaga(boolean paga) {
        this.paga = paga;
    }

    public void pagar() {
        this.paga = true;
    }

}
