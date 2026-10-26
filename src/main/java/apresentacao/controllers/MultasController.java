package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
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


public final class MultasController implements Navegacao.ControladorComSessao {

    private static final Logger LOG = Logger.getLogger(MultasController.class.getName());
    private static final ZoneId ZONA_LOJA = ZoneId.of("America/Recife");
    private static final String TIPO_AVARIA = "AVARIA";
    private static final String TIPO_OUTRO = "OUTRO";

    private enum Filtro { TODAS, PENDENTES, PAGAS }

    // Uma representação visual para as duas origens de multa.
    public record LinhaMulta(
            String referencia,
            int idContrato,
            String tipo,
            String descricao,
            double valor,
            String data,
            boolean paga,
            Integer idMulta,
            boolean doContrato
    ) { }

    private SistemaFacade sistema;
    private Usuario usuario;
    private Consumer<String> navegacao;
    private Runnable sair;
    private Filtro filtro = Filtro.TODAS;
    private List<LinhaMulta> todas = List.of();

    @FXML private TableView<LinhaMulta> tabela;
    @FXML private TableColumn<LinhaMulta, String> colRef;
    @FXML private TableColumn<LinhaMulta, String> colContrato;
    @FXML private TableColumn<LinhaMulta, String> colTipo;
    @FXML private TableColumn<LinhaMulta, String> colDescricao;
    @FXML private TableColumn<LinhaMulta, String> colValor;
    @FXML private TableColumn<LinhaMulta, String> colData;
    @FXML private TableColumn<LinhaMulta, String> colStatus;
    @FXML private TextField busca;
    @FXML private TextField campoContrato;
    @FXML private TextField campoValor;
    @FXML private ComboBox<String> campoTipo;
    @FXML private TextArea campoDescricao;
    @FXML private VBox formulario;
    @FXML private Label lblTotal;
    @FXML private Label lblPendentes;
    @FXML private Label lblValorPendente;
    @FXML private Label lblResultado;
    @FXML private Label lblPerfil;
    @FXML private Label lblNomeUsuario;
    @FXML private Label lblAvatar;
    @FXML private Button btnQuitar;
    @FXML private Button btnTodas;
    @FXML private Button btnPendentes;
    @FXML private Button btnPagas;

    @FXML
    private void initialize() {
        colRef.setCellValueFactory(x -> propriedade(x.getValue().referencia()));
        colContrato.setCellValueFactory(x -> propriedade(String.valueOf(x.getValue().idContrato())));
        colTipo.setCellValueFactory(x -> propriedade(x.getValue().tipo()));
        colDescricao.setCellValueFactory(x -> propriedade(x.getValue().descricao()));
        colValor.setCellValueFactory(x -> propriedade(Formatador.moeda(x.getValue().valor())));
        colData.setCellValueFactory(x -> propriedade(Formatador.data(x.getValue().data())));
        colStatus.setCellValueFactory(x -> propriedade(x.getValue().paga() ? "Paga" : "Pendente"));

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhuma multa encontrada."));
        tabela.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, anterior, atual) -> filtrar());

        // Os campos de cadastro não existem no layout do cliente
        if (campoTipo != null) {
            campoTipo.getItems().setAll(TIPO_AVARIA, TIPO_OUTRO);
            campoTipo.setValue(TIPO_AVARIA);
        }
        destacarFiltro();
        ajustarAcoes();
    }

    private ReadOnlyStringWrapper propriedade(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
    }

    @Override
    public void configurar(SistemaFacade sistema, Usuario usuario,
                           Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de saída é obrigatório");

        exigirSessao();
        validarLayoutDoPerfil();
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank()
                ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    private void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador
                || usuario instanceof Funcionario || usuario instanceof Cliente)) {
            throw new SecurityException("Sessão ausente ou sem acesso às multas.");
        }
    }

    private void exigirOperador() {
        exigirSessao();
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Somente administrador ou funcionário pode registrar multas.");
        }
    }

    private void validarLayoutDoPerfil() {
        boolean layoutCliente = formulario == null;
        if ((usuario instanceof Cliente) != layoutCliente) {
            throw new SecurityException("Layout de multas incompatível com o perfil autenticado.");
        }
    }

    @FXML
    private void atualizar() {
        executar("consultar multas", () -> {
            exigirSessao();
            List<ContratoAluguel> contratos = new ArrayList<>(sistema.listarContratos());
            List<LinhaMulta> linhas = carregarLinhas(contratos);
            linhas.sort(Comparator.comparingInt(LinhaMulta::idContrato)
                    .thenComparing(LinhaMulta::referencia));
            todas = List.copyOf(linhas);
            atualizarIndicadores();
            filtrar();
        });
    }

    private List<LinhaMulta> carregarLinhas(List<ContratoAluguel> contratos) {
        List<LinhaMulta> linhas = new ArrayList<>();

        // O terminal do cliente consulta/quita multas contratuais.
        // As multas manuais continuam acessíveis para administração e funcionários.
        if (!(usuario instanceof Cliente)) {
            Map<Integer, ContratoAluguel> porId = contratos.stream()
                    .collect(Collectors.toMap(ContratoAluguel::getId,
                            Function.identity(), (primeiro, segundo) -> primeiro));
            adicionarMultasManuais(linhas, porId);
        }
        adicionarMultasContratuais(linhas, contratos);
        return linhas;
    }

    private void adicionarMultasManuais(List<LinhaMulta> linhas,
                                       Map<Integer, ContratoAluguel> contratosPorId) {
        for (Multa multa : sistema.listarMultas()) {
            // A referência ao contrato é mantida mesmo se o contrato não estiver disponível
            // Só operadores acessam registros manuais, não é expostos a clientes
            ContratoAluguel contrato = contratosPorId.get(multa.getIdContrato());
            if (contrato == null) {
                LOG.warning(() -> "Contrato " + multa.getIdContrato()
                        + " não encontrado para a multa manual " + multa.getId());
            }
            linhas.add(new LinhaMulta(
                    "M-" + multa.getId(), multa.getIdContrato(), multa.getTipo(),
                    multa.getDescricao(), multa.getValor(), multa.getDataCriacao(),
                    multa.isPaga(), multa.getId(), false));
        }
    }

    private void adicionarMultasContratuais(List<LinhaMulta> linhas,
                                           List<ContratoAluguel> contratos) {
        for (ContratoAluguel contrato : contratos) {
            if (usuario instanceof Cliente && !pertenceAoCliente(contrato)) {
                continue;
            }
            if (contrato.getValorMulta() <= 0) {
                continue;
            }
            String nomeItem = contrato.getItem() == null
                    ? "" : " - " + contrato.getItem().getNome();
            linhas.add(new LinhaMulta(
                    "C-" + contrato.getId(), contrato.getId(), "CONTRATO",
                    "Multa do contrato" + nomeItem, contrato.getValorMulta(),
                    null, contrato.isMultaPaga(), null, true));
        }
    }

    private boolean pertenceAoCliente(ContratoAluguel contrato) {
        return contrato != null && contrato.getCliente() != null
                && usuario != null && contrato.getCliente().getId() == usuario.getId();
    }

    private void atualizarIndicadores() {
        lblTotal.setText(String.valueOf(todas.size()));
        long pendentes = todas.stream().filter(multa -> !multa.paga()).count();
        lblPendentes.setText(String.valueOf(pendentes));
        double totalPendente = todas.stream().filter(multa -> !multa.paga())
                .mapToDouble(LinhaMulta::valor).sum();
        lblValorPendente.setText(Formatador.moeda(totalPendente));
    }

    private void filtrar() {
        String termo = busca.getText() == null
                ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<LinhaMulta> filtradas = todas.stream()
                .filter(multa -> filtro == Filtro.TODAS
                        || (filtro == Filtro.PAGAS) == multa.paga())
                .filter(multa -> termo.isEmpty()
                        || multa.referencia().toLowerCase(Locale.ROOT).contains(termo)
                        || String.valueOf(multa.idContrato()).equals(termo)
                        || multa.tipo().toLowerCase(Locale.ROOT).contains(termo)
                        || multa.descricao().toLowerCase(Locale.ROOT).contains(termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(filtradas));
        lblResultado.setText("Exibindo " + filtradas.size() + " de " + todas.size());
        ajustarAcoes();
    }

    @FXML private void filtrarTodas() { mudarFiltro(Filtro.TODAS); }
    @FXML private void filtrarPendentes() { mudarFiltro(Filtro.PENDENTES); }
    @FXML private void filtrarPagas() { mudarFiltro(Filtro.PAGAS); }

    private void mudarFiltro(Filtro novo) {
        filtro = novo;
        destacarFiltro();
        filtrar();
    }

    private void destacarFiltro() {
        destacar(btnTodas, filtro == Filtro.TODAS);
        destacar(btnPendentes, filtro == Filtro.PENDENTES);
        destacar(btnPagas, filtro == Filtro.PAGAS);
    }

    private void destacar(Button botao, boolean ativo) {
        if (botao == null) {
            return;
        }
        botao.getStyleClass().remove("filter-active");
        if (ativo) {
            botao.getStyleClass().add("filter-active");
        }
    }

    private void ajustarAcoes() {
        LinhaMulta selecionada = tabela.getSelectionModel().getSelectedItem();
        btnQuitar.setDisable(selecionada == null || selecionada.paga()
                || (usuario instanceof Cliente && !selecionada.doContrato()));
    }

    @FXML
    private void mostrarFormulario() {
        executar("abrir cadastro de multa", () -> {
            exigirOperador();
            if (formulario == null) {
                throw new SecurityException("Este layout não permite registrar multas.");
            }
            campoContrato.clear();
            campoDescricao.clear();
            campoValor.clear();
            campoTipo.setValue(TIPO_AVARIA);
            exibirFormulario(true);
        });
    }

    @FXML private void cancelarFormulario() { exibirFormulario(false); }

    private void exibirFormulario(boolean visivel) {
        if (formulario != null) {
            formulario.setVisible(visivel);
            formulario.setManaged(visivel);
            if (visivel) {
                campoContrato.requestFocus();
            }
        }
    }

    @FXML
    private void registrar() {
        executar("registrar multa", () -> {
            exigirOperador();
            int idContrato = inteiroPositivo(campoContrato.getText(), "ID do contrato");
            if (sistema.buscarContrato(idContrato) == null) {
                throw new IllegalArgumentException("Contrato não encontrado.");
            }
            String tipo = campoTipo.getValue();
            if (!TIPO_AVARIA.equals(tipo) && !TIPO_OUTRO.equals(tipo)) {
                throw new IllegalArgumentException("Selecione AVARIA ou OUTRO.");
            }
            String descricao = textoObrigatorio(campoDescricao.getText(), "Descrição");
            double valor = valorPositivo(campoValor.getText());
            String data = LocalDate.now(ZONA_LOJA).toString();
            Multa multa = new Multa(
                    sistema.gerarProximoIdMulta(), idContrato, tipo, descricao, valor, data);
            if (!sistema.registrarMulta(multa)) {
                throw new IllegalArgumentException("Não foi possível registrar a multa. Verifique o ID.");
            }
            exibirFormulario(false);
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Multa registrada",
                    "Registro M-" + multa.getId() + " criado.");
        });
    }

    @FXML
    private void quitar() {
        executar("quitar multa", this::processarQuitacao);
    }

    private void processarQuitacao() {
        exigirSessao();
        LinhaMulta linha = obterMultaSelecionada();
        if (linha.paga()) {
            throw new IllegalArgumentException("Esta multa já está quitada.");
        }
        if (usuario instanceof Cliente && !linha.doContrato()) {
            throw new SecurityException("Clientes só podem quitar suas multas contratuais.");
        }

        ContratoAluguel contrato = sistema.buscarContrato(linha.idContrato());
        if (usuario instanceof Cliente && !pertenceAoCliente(contrato)) {
            throw new SecurityException("Você só pode quitar multas dos seus próprios contratos.");
        }

        if (!confirmar("Confirmar quitação", "Marcar " + linha.referencia()
                + " como quitada? Esta ação não processa pagamento bancário.")) {
            return;
        }

        boolean sucesso = linha.doContrato()
                ? quitarMultaContratual(linha, contrato)
                : quitarMultaManual(linha);
        if (!sucesso) {
            throw new IllegalArgumentException("A quitação não foi confirmada pelo sistema.");
        }
        atualizar();
        alerta(Alert.AlertType.INFORMATION, "Concluído", "Multa marcada como quitada.");
    }

    private LinhaMulta obterMultaSelecionada() {
        LinhaMulta selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            throw new IllegalArgumentException("Selecione uma multa na tabela.");
        }
        return selecionada;
    }

    private boolean quitarMultaContratual(LinhaMulta linha, ContratoAluguel contrato) {
        if (contrato == null || contrato.getValorMulta() <= 0 || contrato.isMultaPaga()) {
            throw new IllegalArgumentException("Esta multa contratual não está pendente.");
        }
        return sistema.quitarMultaContrato(linha.idContrato());
    }

    private boolean quitarMultaManual(LinhaMulta linha) {
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("A quitação de multas manuais exige perfil de operador.");
        }
        if (linha.idMulta() == null) {
            throw new IllegalArgumentException("Identificador da multa manual inválido.");
        }
        Multa registro = sistema.buscarMulta(linha.idMulta());
        if (registro == null || registro.isPaga()
                || registro.getIdContrato() != linha.idContrato()) {
            throw new IllegalArgumentException("Esta multa manual não está mais pendente.");
        }
        return sistema.quitarMulta(linha.idMulta());
    }

    private int inteiroPositivo(String texto, String campo) {
        try {
            int numero = Integer.parseInt(texto == null ? "" : texto.strip());
            if (numero > 0) {
                return numero;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um inteiro positivo.", e);
        }
        throw new IllegalArgumentException(campo + " deve ser maior que zero.");
    }

    private double valorPositivo(String texto) {
        try {
            double valor = Double.parseDouble(
                    texto == null ? "" : texto.strip().replace(',', '.'));
            if (Double.isFinite(valor) && valor > 0) {
                return valor;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numérico inválido (ex.: 45,90).", e);
        }
        throw new IllegalArgumentException("O valor da multa deve ser positivo.");
    }

    private String textoObrigatorio(String entrada, String campo) {
        if (entrada == null || entrada.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatória.");
        }
        String texto = entrada.strip();
        if (texto.contains(";") || texto.contains("\n") || texto.contains("\r")) {
            throw new IllegalArgumentException(
                    campo + " não pode conter ponto e vírgula ou quebra de linha (CSV).");
        }
        return texto;
    }

    @FXML private void abrirMultas() { atualizar(); }

    @FXML
    private void abrirFornecedores() {
        executar("abrir fornecedores", () -> {
            exigirOperador();
            navegacao.accept(usuario instanceof Administrador
                    ? "FornecedoresAdministrador" : "FornecedoresFunccionarios");
        });
    }

    @FXML
    private void encerrarSessao() {
        executar("encerrar sessão", () -> {
            exigirSessao();
            Navegacao.setUsuarioLogado(null);
            sair.run();
        });
    }

    private void executar(String acao, Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | SecurityException e) {
            alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            alerta(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível " + acao + ". Consulte os logs.");
        }
    }

    private boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION,
                mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        Optional<ButtonType> resposta = dialogo.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert aviso = new Alert(tipo, mensagem, ButtonType.OK);
        aviso.setTitle(titulo);
        aviso.setHeaderText(null);
        aviso.showAndWait();
    }
}