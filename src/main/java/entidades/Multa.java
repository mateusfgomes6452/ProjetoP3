package entidades;

public class Multa {

    private final int id;
    private final int idContrato;
    private final String tipo;       
    private final String descricao;
    private double valor;
    private boolean paga;
    private final String dataCriacao;

    public Multa(int id, int idContrato, String tipo, String descricao, double valor, String dataCriacao) {
        this.id = id;
        this.idContrato = idContrato;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.paga = false;
        this.dataCriacao = dataCriacao;
    }

   

    public int getId() {
        return id;
    }

    public int getIdContrato() {
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
