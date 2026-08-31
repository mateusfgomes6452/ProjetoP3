package entidades;

public class Item {

    private int id;
    private String nome;
    private String descricao;
    private double taxaDiaria;
    private String estadoConservacao;
    private double valorReposicao;
    private Categoria categoria;
    private Fornecedor fornecedor;
    private String status;
    private boolean ativo;

    public Item(int id, String nome, String descricao, double taxaDiaria,
                String estadoConservacao, double valorReposicao,
                Categoria categoria, Fornecedor fornecedor) {

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.taxaDiaria = taxaDiaria;
        this.estadoConservacao = estadoConservacao;
        this.valorReposicao = valorReposicao;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.status = "DISPONIVEL";
        this.ativo = true;
    }

    public int getId() {
        return id;
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

    public double getTaxaDiaria() {
        return taxaDiaria;
    }

    public void setTaxaDiaria(double taxaDiaria) {
        this.taxaDiaria = taxaDiaria;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public double getValorReposicao() {
        return valorReposicao;
    }

    public void setValorReposicao(double valorReposicao) {
        this.valorReposicao = valorReposicao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.toUpperCase();
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

    public boolean estaDisponivel() {
        if (ativo && status.equals("DISPONIVEL")){
            return true;
        }
        return false;
    }

    public boolean estaAlugado() {
        return status.equals("ALUGADO");
    }

    public boolean estaEmManutencao() {
        return status.equals("MANUTENCAO");
    }

    public void alugar() {
        this.status = "ALUGADO";
    }

    public void devolver() {
        this.status = "DISPONIVEL";
    }

    public void enviarParaManutencao() {
        this.status = "MANUTENCAO";
    }
}