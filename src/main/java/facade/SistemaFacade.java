package facade;

import java.time.LocalDate;
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
	
    private static SistemaFacade instancia;

    public static SistemaFacade getSistema() {
        if (instancia == null) {
            throw new IllegalStateException("SistemaFacade não foi inicializado! Certifique-se de instanciá-lo na classe principal.");
        }
        return instancia;
    }
    public static void registrar(SistemaFacade sistema) {
        instancia = sistema;
    }

    private final IGerenciamentoUsuarios   gerenciamentoUsuarios;
    private final IGerenciamentoContratos  gerenciamentoContratos;
    private final IGerenciamentoItens      gerenciamentoItens;
    private final IGerenciamentoCategoria  gerenciamentoCategoria;
    private final IGerenciamentoFornecedor gerenciamentoFornecedor;
    private final IGerenciamentoMultas     gerenciamentoMultas;
    private final IRelatorios              geradorRelatorios;

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

    public Usuario buscarUsuario(Long id) {
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

    public boolean atualizarUsuario(Long id, String nome, String email, String senha) {
        return gerenciamentoUsuarios.atualizarUsuario(id, nome, email, senha);
    }

    public boolean desativaUsuario(Long id) {
        return gerenciamentoUsuarios.desativaUsuario(id);
    }

    public Usuario realizarLogin(String email, String senha) {
        return gerenciamentoUsuarios.realizarLogin(email, senha);
    }


    // =========================================================================
    // CONTRATOS
    // =========================================================================

    public boolean cadastrarContrato(ContratoAluguel contrato) {
        return gerenciamentoContratos.cadastrarContrato(contrato);
    }

    public ContratoAluguel buscarContrato(Long id) {
        return gerenciamentoContratos.buscarContrato(id);
    }

    public List<ContratoAluguel> listarContratos() {
        return gerenciamentoContratos.listarContratos();
    }

    public boolean finalizarContrato(Long id) {
        return gerenciamentoContratos.finalizarContrato(id);
    }

    public boolean cancelarContrato(Long id) {
        return gerenciamentoContratos.cancelarContrato(id);
    }

    public boolean clientePossuiMultaPendente(Long long1) {
        return gerenciamentoContratos.clientePossuiMultaPendente(long1);
    }

    public boolean clientePossuiHistorico(Long idCliente) {
        return gerenciamentoContratos.clientePossuiHistorico(idCliente);
    }

    public boolean quitarMultaContrato(Long idContrato) {
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

    public Item buscarItem(Long id) {
        return gerenciamentoItens.buscarItem(id);
    }

    public List<Item> listarItens() {
        return gerenciamentoItens.listarItens();
    }

    public boolean atualizarItem(Item itemNovo) {
        return gerenciamentoItens.atualizarItem(itemNovo);
    }

    public boolean excluirItem(Long id) {
        return gerenciamentoItens.excluirItem(id);
    }

    // =========================================================================
    // CATEGORIAS
    // =========================================================================

    public boolean cadastrarCategoria(Categoria categoria) {
        return gerenciamentoCategoria.cadastrarCategoria(categoria);
    }

    public Categoria buscarCategoria(Long idEmEdicao) {
        return gerenciamentoCategoria.buscarCategoria(idEmEdicao);
    }

    public List<Categoria> listarCategorias() {
        return gerenciamentoCategoria.listarCategorias();
    }

    public boolean atualizarCategoria(Categoria categoriaNova) {
        return gerenciamentoCategoria.atualizarCategoria(categoriaNova);
    }

    public boolean desativarCategoria(Long id) {
        return gerenciamentoCategoria.desativarCategoria(id);
    }

    // =========================================================================
    // FORNECEDORES
    // =========================================================================

    public boolean cadastrarFornecedor(Fornecedor fornecedor) {
        return gerenciamentoFornecedor.cadastrarFornecedor(fornecedor);
    }

    public Fornecedor buscarFornecedor(Long id) {
        return gerenciamentoFornecedor.buscarFornecedor(id);
    }

    public List<Fornecedor> listarFornecedores() {
        return gerenciamentoFornecedor.listarFornecedores();
    }

    public boolean atualizarFornecedor(Fornecedor fornecedorNovo) {
        return gerenciamentoFornecedor.atualizarFornecedor(fornecedorNovo);
    }

    public boolean desativarFornecedor(Long id) {
        return gerenciamentoFornecedor.desativarFornecedor(id);
    }

    // =========================================================================
    // MULTAS
    // =========================================================================

    public boolean registrarMulta(Multa multa) {
        return gerenciamentoMultas.registrarMulta(multa);
    }

    public Multa buscarMulta(Long id) {
        return gerenciamentoMultas.buscarMulta(id);
    }

    public List<Multa> listarMultas() {
        return gerenciamentoMultas.listarMultas();
    }

    public List<Multa> listarMultasPorContrato(Long idContrato) {
        return gerenciamentoMultas.listarMultasPorContrato(idContrato);
    }

    public List<Multa> listarMultasPendentes() {
        return gerenciamentoMultas.listarMultasPendentes();
    }

    public boolean quitarMulta(Long id) {
        return gerenciamentoMultas.quitarMulta(id);
    }

    // =========================================================================
    // RELATÓRIOS
    // =========================================================================

    public void gerarRelatorioItensDisponiveis() {
        geradorRelatorios.gerarRelatorioItensDisponiveis();
    }

    public void gerarHistoricoCliente(Long idCliente) {
        geradorRelatorios.gerarHistoricoCliente(idCliente);
    }

    public void gerarRelatorioItensAlugados() {
        geradorRelatorios.gerarRelatorioItensAlugados();
    }

    public void gerarRelatorioFaturamento(String dataInicial, String dataFinal) {
        geradorRelatorios.gerarRelatorioFaturamento(dataInicial, dataFinal);
    }

    // Dados dos relatórios (telas JavaFX)

    public List<Item> listarItensDisponiveis() {
        return geradorRelatorios.listarItensDisponiveis();
    }

    public List<ContratoAluguel> listarHistoricoCliente(Long idCliente) {
        return geradorRelatorios.listarHistoricoCliente(idCliente);
    }

    public List<ContratoAluguel> listarContratosAtivos() {
        return geradorRelatorios.listarContratosAtivos();
    }

    public List<ContratoAluguel> listarContratosPorPeriodo(LocalDate inicio, LocalDate fim) {
        return geradorRelatorios.listarContratosPorPeriodo(inicio, fim);
    }

    public boolean contratoEstaAtrasado(ContratoAluguel contrato) {
        return geradorRelatorios.estaAtrasado(contrato);
    }

    public void exportarItensDisponiveis(List<Item> itens) {
        geradorRelatorios.exportarItensDisponiveis(itens);
    }

    public void exportarHistoricoCliente(List<ContratoAluguel> historico) {
        geradorRelatorios.exportarHistoricoCliente(historico);
    }

    public void exportarItensAlugados(List<ContratoAluguel> contratosAtivos) {
        geradorRelatorios.exportarItensAlugados(contratosAtivos);
    }

    public void exportarFaturamento(List<ContratoAluguel> contratos, String dataInicial, String dataFinal) {
        geradorRelatorios.exportarFaturamento(contratos, dataInicial, dataFinal);
    }
}
