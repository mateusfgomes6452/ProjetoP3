package facade;

import java.util.List;

import business.interfaces.IGerenciamentoCategoria;
import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoFornecedor;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoMultas;
import business.interfaces.IGerenciamentoUsuarios;
import business.relatorios.IRelatorios;
import entidades.Categoria;
import entidades.ContratoAluguel;
import entidades.Fornecedor;
import entidades.Item;
import entidades.Multa;
import entidades.Usuario;

public class SistemaFacade {

    private IGerenciamentoUsuarios   gerenciamentoUsuarios;
    private IGerenciamentoContratos  gerenciamentoContratos;
    private IGerenciamentoItens      gerenciamentoItens;
    private IGerenciamentoCategoria  gerenciamentoCategoria;
    private IGerenciamentoFornecedor gerenciamentoFornecedor;
    private IGerenciamentoMultas     gerenciamentoMultas;
    private IRelatorios              geradorRelatorios;

    public SistemaFacade(
            IGerenciamentoUsuarios   gerenciamentoUsuarios,
            IGerenciamentoContratos  gerenciamentoContratos,
            IGerenciamentoItens      gerenciamentoItens,
            IGerenciamentoCategoria  gerenciamentoCategoria,
            IGerenciamentoFornecedor gerenciamentoFornecedor,
            IGerenciamentoMultas     gerenciamentoMultas,
            IRelatorios              geradorRelatorios) {

        this.gerenciamentoUsuarios   = gerenciamentoUsuarios;
        this.gerenciamentoContratos  = gerenciamentoContratos;
        this.gerenciamentoItens      = gerenciamentoItens;
        this.gerenciamentoCategoria  = gerenciamentoCategoria;
        this.gerenciamentoFornecedor = gerenciamentoFornecedor;
        this.gerenciamentoMultas     = gerenciamentoMultas;
        this.geradorRelatorios       = geradorRelatorios;
    }

    // =========================================================================
    // USUÁRIOS
    // =========================================================================

    public boolean cadastrarUsuario(Usuario usuario) {
        return gerenciamentoUsuarios.cadastrarUsuario(usuario);
    }

    public Usuario buscarUsuario(int id) {
        return gerenciamentoUsuarios.buscarUsuario(id);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        for (Usuario u : gerenciamentoUsuarios.listarUsuarios()) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return gerenciamentoUsuarios.listarUsuarios();
    }

    public boolean atualizarUsuario(int id, String nome, String email, String senha) {
        return gerenciamentoUsuarios.atualizarUsuario(id, nome, email, senha);
    }

    public boolean desativaUsuario(int id) {
        return gerenciamentoUsuarios.desativaUsuario(id);
    }

    public Usuario realizarLogin(String email, String senha) {
        return gerenciamentoUsuarios.realizarLogin(email, senha);
    }

    public int gerarProximoIdUsuario() {
        return gerenciamentoUsuarios.gerarProximoId();
    }

    // =========================================================================
    // CONTRATOS
    // =========================================================================

    public boolean cadastrarContrato(ContratoAluguel contrato) {
        return gerenciamentoContratos.cadastrarContrato(contrato);
    }

    public ContratoAluguel buscarContrato(int id) {
        return gerenciamentoContratos.buscarContrato(id);
    }

    public List<ContratoAluguel> listarContratos() {
        return gerenciamentoContratos.listarContratos();
    }

    public boolean finalizarContrato(int id) {
        return gerenciamentoContratos.finalizarContrato(id);
    }

    public boolean cancelarContrato(int id) {
        return gerenciamentoContratos.cancelarContrato(id);
    }

    public boolean clientePossuiMultaPendente(int idCliente) {
        return gerenciamentoContratos.clientePossuiMultaPendente(idCliente);
    }

    public boolean clientePossuiHistorico(int idCliente) {
        return gerenciamentoContratos.clientePossuiHistorico(idCliente);
    }

    public int gerarProximoIdContrato() {
        return gerenciamentoContratos.gerarProximoId();
    }
    public boolean quitarMultaContrato(int idContrato) {
        return gerenciamentoContratos.quitarMultaContrato(idContrato);
    }
    public double calcularMulta(long diasAtraso, double taxaDiaria){
        return gerenciamentoContratos.calcularMulta(diasAtraso, taxaDiaria);

    }

    // =========================================================================
    // ITENS
    // =========================================================================

    public boolean cadastrarItem(Item item) {
        return gerenciamentoItens.cadastrarItem(item);
    }

    public Item buscarItem(int id) {
        return gerenciamentoItens.buscarItem(id);
    }

    public List<Item> listarItens() {
        return gerenciamentoItens.listarItens();
    }

    public boolean atualizarItem(Item itemNovo) {
        return gerenciamentoItens.atualizarItem(itemNovo);
    }

    public boolean excluirItem(int id) {
        return gerenciamentoItens.excluirItem(id);
    }

    public int gerarProximoIdItem() {
        return gerenciamentoItens.gerarProximoId();
    }

    // =========================================================================
    // CATEGORIAS
    // =========================================================================

    public boolean cadastrarCategoria(Categoria categoria) {
        return gerenciamentoCategoria.cadastrarCategoria(categoria);
    }

    public Categoria buscarCategoria(int id) {
        return gerenciamentoCategoria.buscarCategoria(id);
    }

    public List<Categoria> listarCategorias() {
        return gerenciamentoCategoria.listarCategorias();
    }

    public boolean atualizarCategoria(Categoria categoriaNova) {
        return gerenciamentoCategoria.atualizarCategoria(categoriaNova);
    }

    public boolean desativarCategoria(int id) {
        return gerenciamentoCategoria.desativarCategoria(id);
    }

    public int gerarProximoIdCategoria() {
        return gerenciamentoCategoria.gerarProximoId();
    }

    // =========================================================================
    // FORNECEDORES
    // =========================================================================

    public boolean cadastrarFornecedor(Fornecedor fornecedor) {
        return gerenciamentoFornecedor.cadastrarFornecedor(fornecedor);
    }

    public Fornecedor buscarFornecedor(int id) {
        return gerenciamentoFornecedor.buscarFornecedor(id);
    }

    public List<Fornecedor> listarFornecedores() {
        return gerenciamentoFornecedor.listarFornecedores();
    }

    public boolean atualizarFornecedor(Fornecedor fornecedorNovo) {
        return gerenciamentoFornecedor.atualizarFornecedor(fornecedorNovo);
    }

    public boolean desativarFornecedor(int id) {
        return gerenciamentoFornecedor.desativarFornecedor(id);
    }

    public int gerarProximoIdFornecedor() {
        return gerenciamentoFornecedor.gerarProximoId();
    }

    // =========================================================================
    // MULTAS
    // =========================================================================

    public boolean registrarMulta(Multa multa) {
        return gerenciamentoMultas.registrarMulta(multa);
    }

    public Multa buscarMulta(int id) {
        return gerenciamentoMultas.buscarMulta(id);
    }

    public List<Multa> listarMultas() {
        return gerenciamentoMultas.listarMultas();
    }

    public List<Multa> listarMultasPorContrato(int idContrato) {
        return gerenciamentoMultas.listarMultasPorContrato(idContrato);
    }

    public List<Multa> listarMultasPendentes() {
        return gerenciamentoMultas.listarMultasPendentes();
    }

    public boolean quitarMulta(int id) {
        return gerenciamentoMultas.quitarMulta(id);
    }

    public int gerarProximoIdMulta() {
        return gerenciamentoMultas.gerarProximoId();
    }

    // =========================================================================
    // RELATÓRIOS
    // =========================================================================

    public void gerarRelatorioItensDisponiveis() {
        geradorRelatorios.gerarRelatorioItensDisponiveis();
    }

    public void gerarHistoricoCliente(int idCliente) {
        geradorRelatorios.gerarHistoricoCliente(idCliente);
    }

    public void gerarRelatorioItensAlugados() {
        geradorRelatorios.gerarRelatorioItensAlugados();
    }

    public void gerarRelatorioFaturamento(String dataInicial, String dataFinal) {
        geradorRelatorios.gerarRelatorioFaturamento(dataInicial, dataFinal);
    }
}