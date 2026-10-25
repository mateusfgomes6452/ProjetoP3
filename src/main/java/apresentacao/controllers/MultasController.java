package apresentacao.controllers;

import entidades.Administrador;
import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Funcionario;
import entidades.Multa;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Versão gráfica de MenuMultas (operadores) e de consultarMultas/pagarMulta
 * de MenuCliente. Usa apenas SistemaFacade, sem nova camada business.
 */
public final class MultasController {
    private static final Logger LOG = Logger.getLogger(MultasController.class.getName());
    private static final ZoneId ZONA_LOJA = ZoneId.of("America/Recife");
    private enum Filtro { TODAS, PENDENTES, PAGAS }

    /** M- = registro manual (entidades.Multa); C- = multa armazenada no contrato. */
    public record LinhaMulta(String referencia, int idContrato, String tipo,
                             String descricao, double valor, String data, boolean paga,
                             Integer idMulta, boolean doContrato) { }

    private SistemaFacade sistema;
    private Usuario usuario;
    private Consumer<String> navegacao;
    private Runnable sair;
    private Filtro filtro = Filtro.TODAS;
    private List<LinhaMulta> todas = List.of();

    @FXML private TableView<LinhaMulta> tabela;
    @FXML private TableColumn<LinhaMulta, String> colRef, colContrato, colTipo, colDescricao, colValor, colData, colStatus;
    @FXML private TextField busca, campoContrato, campoValor;
    @FXML private ComboBox<String> campoTipo;
    @FXML private TextArea campoDescricao;
    @FXML private VBox formulario;
    @FXML private Label lblTotal, lblPendentes, lblValorPendente, lblResultado, lblPerfil, lblNomeUsuario, lblAvatar;
    @FXML private Button btnQuitar, btnTodas, btnPendentes, btnPagas;

    @FXML private void initialize() {
        colRef.setCellValueFactory(x -> propriedade(x.getValue().referencia()));
        colContrato.setCellValueFactory(x -> propriedade(String.valueOf(x.getValue().idContrato())));
        colTipo.setCellValueFactory(x -> propriedade(x.getValue().tipo()));
        colDescricao.setCellValueFactory(x -> propriedade(x.getValue().descricao()));
        colValor.setCellValueFactory(x -> propriedade(moeda(x.getValue().valor())));
        colData.setCellValueFactory(x -> propriedade(x.getValue().data()));
        colStatus.setCellValueFactory(x -> propriedade(x.getValue().paga() ? "Paga" : "Pendente"));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhuma multa encontrada."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, anterior, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, anterior, atual) -> filtrar());
        if (campoTipo != null) {
            campoTipo.getItems().setAll("AVARIA", "OUTRO");
            campoTipo.setValue("AVARIA");
        }
        destacarFiltro();
        ajustarAcoes();
    }

    private ReadOnlyStringWrapper propriedade(String s) { return new ReadOnlyStringWrapper(s == null ? "" : s); }
    private String moeda(double valor) { return String.format(new Locale("pt", "BR"), "R$ %,.2f", valor); }

    /**
     * Chamar após FXMLLoader.load(), com o MESMO SistemaFacade e o usuário
     * retornado pelo login. A tela de cliente nunca pode usar FXML de operador.
     */
    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de saída é obrigatório");
        exigirSessao();
        // Confere o perfil esperado pelo layout, e não apenas botões visíveis.
        if ((usuario instanceof Cliente) != (formulario == null)) {
            throw new SecurityException("Layout incompatível com o perfil autenticado.");
        }
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank() ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    private void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario || usuario instanceof Cliente)) {
            throw new SecurityException("Sessão ausente ou sem acesso às multas.");
        }
    }
    private void exigirOperador() {
        exigirSessao();
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Clientes não podem registrar multas.");
        }
    }

    @FXML private void atualizar() {
        executar("consultar multas", () -> {
            exigirSessao();
            List<ContratoAluguel> contratos = new ArrayList<>(sistema.listarContratos());
            Map<Integer, ContratoAluguel> porId = contratos.stream()
                    .collect(Collectors.toMap(ContratoAluguel::getId, Function.identity(), (a, b) -> a));
            List<LinhaMulta> linhas = new ArrayList<>();

            // Registros de multas manuais: corresponde a MenuMultas.listarMultas().
            for (Multa multa : sistema.listarMultas()) {
                ContratoAluguel contrato = porId.get(multa.getIdContrato());
                if (usuario instanceof Cliente && !pertenceAoCliente(contrato)) continue;
                linhas.add(new LinhaMulta("M-" + multa.getId(), multa.getIdContrato(),
                        multa.getTipo(), multa.getDescricao(), multa.getValor(), multa.getDataCriacao(),
                        multa.isPaga(), multa.getId(), false));
            }

            // Multas vinculadas ao próprio contrato: como em MenuCliente.
            for (ContratoAluguel contrato : contratos) {
                if (usuario instanceof Cliente && !pertenceAoCliente(contrato)) continue;
                if (contrato.getValorMulta() > 0) {
                    linhas.add(new LinhaMulta("C-" + contrato.getId(), contrato.getId(), "CONTRATO",
                            "Multa do contrato" + (contrato.getItem() == null ? "" : " - " + contrato.getItem().getNome()),
                            contrato.getValorMulta(), "-", contrato.isMultaPaga(), null, true));
                }
            }
            linhas.sort(Comparator.comparing(LinhaMulta::referencia));
            todas = List.copyOf(linhas);
            lblTotal.setText(String.valueOf(todas.size()));
            long pendentes = todas.stream().filter(x -> !x.paga()).count();
            lblPendentes.setText(String.valueOf(pendentes));
            double total = todas.stream().filter(x -> !x.paga()).mapToDouble(LinhaMulta::valor).sum();
            lblValorPendente.setText(moeda(total));
            filtrar();
        });
    }

    private boolean pertenceAoCliente(ContratoAluguel contrato) {
        return contrato != null && contrato.getCliente() != null && contrato.getCliente().getId() == usuario.getId();
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<LinhaMulta> linhas = todas.stream()
                .filter(x -> filtro == Filtro.TODAS || (filtro == Filtro.PAGAS) == x.paga())
                .filter(x -> termo.isEmpty()
                        || x.referencia().toLowerCase(Locale.ROOT).contains(termo)
                        || String.valueOf(x.idContrato()).equals(termo)
                        || x.tipo().toLowerCase(Locale.ROOT).contains(termo)
                        || x.descricao().toLowerCase(Locale.ROOT).contains(termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(linhas));
        lblResultado.setText("Exibindo " + linhas.size() + " de " + todas.size());
        ajustarAcoes();
    }
    @FXML private void filtrarTodas() { mudarFiltro(Filtro.TODAS); }
    @FXML private void filtrarPendentes() { mudarFiltro(Filtro.PENDENTES); }
    @FXML private void filtrarPagas() { mudarFiltro(Filtro.PAGAS); }
    private void mudarFiltro(Filtro novo) { filtro = novo; destacarFiltro(); filtrar(); }
    private void destacarFiltro() {
        destacar(btnTodas, filtro == Filtro.TODAS);
        destacar(btnPendentes, filtro == Filtro.PENDENTES);
        destacar(btnPagas, filtro == Filtro.PAGAS);
    }
    private void destacar(Button botao, boolean ativo) {
        botao.getStyleClass().remove("filter-active");
        if (ativo) botao.getStyleClass().add("filter-active");
    }
    private void ajustarAcoes() {
        LinhaMulta linha = tabela.getSelectionModel().getSelectedItem();
        btnQuitar.setDisable(linha == null || linha.paga());
    }

    @FXML private void mostrarFormulario() {
        executar("abrir registro", () -> {
            exigirOperador();
            if (formulario == null) throw new SecurityException("Tela não possui cadastro de multas.");
            campoContrato.clear(); campoDescricao.clear(); campoValor.clear(); campoTipo.setValue("AVARIA");
            exibirFormulario(true);
        });
    }
    @FXML private void cancelarFormulario() { exibirFormulario(false); }
    private void exibirFormulario(boolean visivel) {
        if (formulario == null) return;
        formulario.setVisible(visivel); formulario.setManaged(visivel);
        if (visivel) campoContrato.requestFocus();
    }
    @FXML private void registrar() {
        executar("registrar multa", () -> {
            exigirOperador();
            int idContrato = inteiroPositivo(campoContrato.getText(), "ID do contrato");
            if (sistema.buscarContrato(idContrato) == null)
                throw new IllegalArgumentException("Contrato não encontrado.");
            String tipo = campoTipo.getValue();
            if (!"AVARIA".equals(tipo) && !"OUTRO".equals(tipo))
                throw new IllegalArgumentException("Selecione AVARIA ou OUTRO.");
            String descricao = textoObrigatorio(campoDescricao.getText(), "Descrição");
            double valor = valorPositivo(campoValor.getText());
            String hoje = LocalDate.now(ZONA_LOJA).toString(); // igual a MenuMultas
            Multa multa = new Multa(sistema.gerarProximoIdMulta(), idContrato, tipo, descricao, valor, hoje);
            if (!sistema.registrarMulta(multa))
                throw new IllegalArgumentException("Não foi possível registrar a multa (verifique ID duplicado).");
            exibirFormulario(false);
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Multa registrada", "Registro M-" + multa.getId() + " criado.");
        });
    }

    @FXML private void quitar() {
        executar("quitar multa", () -> {
            exigirSessao();
            LinhaMulta linha = tabela.getSelectionModel().getSelectedItem();
            if (linha == null) throw new IllegalArgumentException("Selecione uma multa.");
            // Revalida o contrato e a titularidade antes de enviar a operação à fachada.
            ContratoAluguel contrato = sistema.buscarContrato(linha.idContrato());
            if (usuario instanceof Cliente && !pertenceAoCliente(contrato))
                throw new SecurityException("Você só pode quitar suas próprias multas.");
            if (!confirmar("Confirmar quitação", "Marcar " + linha.referencia()
                    + " como quitada? Esta ação não processa pagamento bancário.")) return;
            boolean sucesso;
            if (linha.doContrato()) {
                if (contrato == null || contrato.getValorMulta() <= 0 || contrato.isMultaPaga())
                    throw new IllegalArgumentException("Essa multa contratual não está pendente.");
                sucesso = sistema.quitarMultaContrato(linha.idContrato());
            } else {
                Multa registro = sistema.buscarMulta(linha.idMulta());
                if (registro == null || registro.isPaga() || registro.getIdContrato() != linha.idContrato())
                    throw new IllegalArgumentException("Essa multa não está mais pendente.");
                sucesso = sistema.quitarMulta(linha.idMulta());
            }
            if (!sucesso) throw new IllegalArgumentException("A quitação não foi confirmada pelo sistema.");
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Concluído", "Multa marcada como quitada.");
        });
    }

    private int inteiroPositivo(String texto, String campo) {
        try {
            int id = Integer.parseInt(texto == null ? "" : texto.strip());
            if (id > 0) return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número inteiro positivo.", e);
        }
        throw new IllegalArgumentException(campo + " deve ser maior que zero.");
    }
    private double valorPositivo(String texto) {
        try {
            double valor = Double.parseDouble(texto == null ? "" : texto.strip().replace(',', '.'));
            if (Double.isFinite(valor) && valor > 0) return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor numérico válido (ex.: 45,90).", e);
        }
        throw new IllegalArgumentException("O valor da multa deve ser positivo e finito.");
    }
    private String textoObrigatorio(String entrada, String campo) {
        if (entrada == null || entrada.isBlank()) throw new IllegalArgumentException(campo + " é obrigatória.");
        String limpa = entrada.strip();
        if (limpa.contains(";") || limpa.contains("\n") || limpa.contains("\r"))
            throw new IllegalArgumentException(campo + " não pode conter ponto e vírgula ou quebra de linha (CSV).");
        return limpa;
    }
    @FXML private void abrirMultas() { atualizar(); }
    @FXML private void abrirFornecedores() {
        executar("abrir fornecedores", () -> { exigirOperador(); navegacao.accept("fornecedores"); });
    }
    @FXML private void encerrarSessao() {
        executar("encerrar sessão", () -> { exigirSessao(); sair.run(); });
    }

    private void executar(String acao, Runnable bloco) {
        try { bloco.run(); }
        catch (IllegalArgumentException | SecurityException e) { alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage()); }
        catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Falha ao " + acao, e);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte os logs.");
        }
    }
    private boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo); dialogo.setHeaderText(null);
        Optional<ButtonType> resposta = dialogo.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }
    private void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.showAndWait();
    }
}