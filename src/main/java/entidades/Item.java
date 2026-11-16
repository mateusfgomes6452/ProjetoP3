package entidades;

public class Item {
	
	private static final String STATUS_DISPONIVEL = "DISPONIVEL";
	private static final String STATUS_ALUGADO = "ALUGADO";
	private static final String STATUS_MANUTENCAO = "MANUTENCAO";
	
    private final int id;
    private String nome;
    private String descricao;
    private double taxaDiaria;
    private String estadoConservacao;
    private double valorReposicao;
    private Categoria categoria;
    private Fornecedor fornecedor;
    private String status;
    private boolean ativo;

    public record Infos(String nome, String descricao, String estadoConservacao) {}
    public record ValoresItem(double taxaDiaria, double valorReposicao) {}
    
    public Item(int id, Infos infos, ValoresItem valores, Categoria categoria, Fornecedor fornecedor) {

        this.id = id;
        this.nome = infos.nome();
        this.descricao = infos.descricao();
        this.taxaDiaria = valores.taxaDiaria();
        this.estadoConservacao = infos.estadoConservacao();
        this.valorReposicao = valores.valorReposicao();
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.status = STATUS_DISPONIVEL;
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
    	return ativo && status.equals(STATUS_DISPONIVEL);
    }

    public boolean estaAlugado() {
        return status.equals(STATUS_ALUGADO);
    }

    public boolean estaEmManutencao() {
        return status.equals(STATUS_MANUTENCAO);
    }

    public void alugar() {
        this.status = STATUS_ALUGADO;
    }

    public void devolver() {
        this.status = STATUS_DISPONIVEL;
    }

    public void enviarParaManutencao() {
        this.status = STATUS_MANUTENCAO;
    }
}
