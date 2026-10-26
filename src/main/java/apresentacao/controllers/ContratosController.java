package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
import entidades.Administrador;
import entidades.ContratoAluguel;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class ContratosController implements Navegacao.ControladorComSessao {

    private static final Logger LOG = Logger.getLogger(ContratosController.class.getName());

    
    private enum Filtro { TODOS, ATIVOS, FINALIZADOS, CANCELADOS }

    private SistemaFacade sistema;
    private Usuario usuario;
    private Consumer<String> navegacao;
    private Runnable sair;

    private Filtro filtro = Filtro.TODOS;
    private List<ContratoAluguel> todos = List.of();   // todos os contratos carregados do sistema

    //fx:id de Contratos Adm
    @FXML private Label lblPerfil;
    @FXML private Label lblNomeUsuario;
    @FXML private Label lblAvatar;
    @FXML private Label lblTotal;
    @FXML private Label lblAtivos;
    @FXML private Label lblAtrasados;
    @FXML private TextField busca;
    @FXML private Label lblResultado;
    @FXML private Button btnTodos, btnAtivos, btnFinalizados, btnCancelados;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId, colCliente, colItem, colRetirada,
            colPrevista, colValor, colStatus;
    @FXML private Button btnDetalhes, btnCancelar, btnFinalizar;
    @FXML private VBox painelRegistro;
    @FXML private VBox painelFinalizacao;
    @FXML private VBox painelDetalhes;


    @FXML
    private void initialize() {
        colId.setCellValueFactory(x -> texto(String.valueOf(x.getValue().getId())));
        colCliente.setCellValueFactory(x -> texto(nomeCliente(x.getValue())));
        colItem.setCellValueFactory(x -> texto(nomeItem(x.getValue())));
        colRetirada.setCellValueFactory(x -> texto(Formatador.data(x.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(x -> texto(Formatador.data(x.getValue().getDataDevolucaoPrevista())));
        colValor.setCellValueFactory(x -> texto(Formatador.moeda(x.getValue().getValorTotal())));
        colStatus.setCellValueFactory(x -> texto(x.getValue().getStatus()));

        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum contrato encontrado."));

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, anterior, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, anterior, atual) -> filtrar());

        esconder(painelRegistro);
        esconder(painelFinalizacao);
        esconder(painelDetalhes);

        destacarFiltro();
        ajustarAcoes();
    }

    @Override
    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de saída é obrigatório");

        exigirSessao();   // "autenticar": só entra quem tem permissão

        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank()
                ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));

        atualizar();
    }


    private void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Somente administrador ou funcionário ativo pode gerenciar contratos.");
        }
    }

    @FXML
    private void atualizar() {
        executar("consultar contratos", () -> {
            exigirSessao();
            // Lógica de MenuContratos.listarContratos()
            List<ContratoAluguel> lista = new ArrayList<>(sistema.listarContratos());
            lista.sort(Comparator.comparingInt(ContratoAluguel::getId));
            todos = List.copyOf(lista);
            atualizarIndicadores();
            filtrar();
        });
    }

    private void atualizarIndicadores() {
        long ativos = todos.stream().filter(ContratoAluguel::estaAtivo).count();
        long atrasados = todos.stream()
                .filter(c -> c.estaAtivo() && sistema.contratoEstaAtrasado(c))
                .count();
        lblTotal.setText(String.valueOf(todos.size()));
        lblAtivos.setText(String.valueOf(ativos));
        lblAtrasados.setText(String.valueOf(atrasados));
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<ContratoAluguel> encontrados = todos.stream()
                .filter(this::passaNoFiltro)
                .filter(c -> termo.isEmpty() || combinaComBusca(c, termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(encontrados));
        lblResultado.setText("Exibindo " + encontrados.size() + " de " + todos.size());
        ajustarAcoes();
    }

    private boolean passaNoFiltro(ContratoAluguel contrato) {
        return switch (filtro) {
            case TODOS -> true;
            case ATIVOS -> contrato.estaAtivo();
            case FINALIZADOS -> contrato.estaFinalizado();
            case CANCELADOS -> contrato.estaCancelado();
        };
    }

    private boolean combinaComBusca(ContratoAluguel contrato, String termo) {
        return String.valueOf(contrato.getId()).equals(termo)
                || nomeCliente(contrato).toLowerCase(Locale.ROOT).contains(termo)
                || nomeItem(contrato).toLowerCase(Locale.ROOT).contains(termo);
    }

    @FXML private void filtrarTodos()       { mudarFiltro(Filtro.TODOS); }
    @FXML private void filtrarAtivos()      { mudarFiltro(Filtro.ATIVOS); }
    @FXML private void filtrarFinalizados() { mudarFiltro(Filtro.FINALIZADOS); }
    @FXML private void filtrarCancelados()  { mudarFiltro(Filtro.CANCELADOS); }

    private void mudarFiltro(Filtro novo) {
        filtro = novo;
        destacarFiltro();
        filtrar();
    }

    private void destacarFiltro() {
        destacar(btnTodos, filtro == Filtro.TODOS);
        destacar(btnAtivos, filtro == Filtro.ATIVOS);
        destacar(btnFinalizados, filtro == Filtro.FINALIZADOS);
        destacar(btnCancelados, filtro == Filtro.CANCELADOS);
    }

    private void destacar(Button botao, boolean ativo) {
        botao.getStyleClass().remove("filter-active");
        if (ativo) {
            botao.getStyleClass().add("filter-active");
        }
    }

    private void ajustarAcoes() {
        ContratoAluguel selecionado = tabela.getSelectionModel().getSelectedItem();
        btnDetalhes.setDisable(selecionado == null);
        // Só contrato ATIVO pode ser finalizado
        btnFinalizar.setDisable(selecionado == null || !selecionado.estaAtivo());
        // Contrato FINALIZADO não pode ser cancelado
        btnCancelar.setDisable(selecionado == null || selecionado.estaFinalizado() || selecionado.estaCancelado());
    }

    //Ações do fxml pra fazer

    @FXML private void abrirRegistro()       { emConstrucao(); }
    @FXML private void registrar()           { emConstrucao(); }
    @FXML private void cancelarRegistro()    { esconder(painelRegistro); }

    @FXML private void abrirFinalizacao()    { emConstrucao(); }
    @FXML private void finalizar()           { emConstrucao(); }
    @FXML private void cancelarFinalizacao() { esconder(painelFinalizacao); }

    @FXML private void verDetalhes()         { emConstrucao(); }
    @FXML private void fecharDetalhes()      { esconder(painelDetalhes); }

    @FXML private void cancelarContrato()    { emConstrucao(); }

    // ---------- Menu lateral (a navegação entre telas será ligada depois) ----------
    @FXML private void abrirFornecedores()   { emConstrucao(); }
    @FXML private void abrirMultas()         { emConstrucao(); }
    @FXML private void encerrarSessao()      { executar("encerrar sessão", () -> sair.run()); }

    

    private ReadOnlyStringWrapper texto(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
    }

    // Evitam erro caso algum contrato do CSV aponte para um cliente ou item que não existe mais
    private String nomeCliente(ContratoAluguel contrato) {
        return contrato.getCliente() == null ? "-" : contrato.getCliente().getNome();
    }

    private String nomeItem(ContratoAluguel contrato) {
        return contrato.getItem() == null ? "-" : contrato.getItem().getNome();
    }

    private void mostrar(VBox painel) {
        painel.setVisible(true);   // aparece
        painel.setManaged(true);   // e passa a ocupar espaço
    }

    private void esconder(VBox painel) {
        painel.setVisible(false);
        painel.setManaged(false);
    }

    private void executar(String acao, Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | SecurityException e) {
            alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte o log.");
        }
    }

    private void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void emConstrucao() {
        alerta(Alert.AlertType.INFORMATION, "Em construção", "Esta ação ainda está sendo implementada.");
    }
}
