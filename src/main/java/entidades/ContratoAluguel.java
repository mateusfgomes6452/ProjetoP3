package entidades;
public class ContratoAluguel {

    private int id;
    private Cliente cliente;
    private Item item;
    private String dataRetirada;
    private String dataDevolucaoPrevista;
    private String dataDevolucaoReal;
    private double valorTotal;
    private String status;
    private boolean ativo;
    private double valorMulta;
    private boolean multaPaga;

    public ContratoAluguel(int id, Cliente cliente, Item item, String dataRetirada, String dataDevolucaoPrevista, double valorTotal) {
        this.id = id;
        this.cliente = cliente;
        this.item = item;
        this.dataRetirada = dataRetirada;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataDevolucaoReal = "";
        this.valorTotal = valorTotal;
        this.status = "ATIVO";
        this.ativo = true;
        this.valorMulta = 0.0;
        this.multaPaga = true;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Item getItem() {
        return item;
    }

    public String getDataRetirada() {
        return dataRetirada;
    }

    public String getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(String dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }
    public String getDataDevolucaoReal(){
        return dataDevolucaoReal;
    }
    public void setDataDevolucaoReal(String dataDevolucaoReal){
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(double valorMulta) {
        this.valorMulta = valorMulta;
    }
    public boolean isMultaPaga() {
    return multaPaga;
    }

    public void setMultaPaga(boolean multaPaga) {
        this.multaPaga = multaPaga;
    }

     public boolean estaAtivo() {
        return status.equals("ATIVO");
    }

    public boolean estaFinalizado() {
        return status.equals("FINALIZADO");
    }

    public boolean estaCancelado() {
        return status.equals("CANCELADO");
    }

    public boolean possuiMulta() {
        if (valorMulta > 0){
            return true;
        }
        return false;
    }


    public void finalizarContrato() {
        this.status = "FINALIZADO";
        this.ativo = false;
    }
    public void cancelarContrato() {
        this.ativo = false;
        this.status = "CANCELADO";
    }

}