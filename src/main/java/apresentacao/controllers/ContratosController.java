package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
import entidades.Administrador;
import entidades.ContratoAluguel;
import entidades.Funcionario;
import entidades.Usuario;
import entidades.Cliente;
import entidades.Item;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;


public final class ContratosController implements Navegacao.ControladorComSessao {

    private static final Logger LOG = Logger.getLogger(ContratosController.class.getName());
    
    private static final ZoneId ZONA_LOJA = ZoneId.of("America/Recife");   // mesmo fuso usado em Multas

    
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
    //Registro
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Item> comboItem;
    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label lblDias, lblTaxa, lblValorTotal;
    @FXML private Button btnConfirmarRegistro;
    @FXML private VBox painelFinalizacao;
    @FXML private VBox painelDetalhes;
    //Detalhes
    @FXML private Label lblTituloDetalhes;
    @FXML private Label detCliente, detItem, detRetirada, detPrevista, detReal,
            detStatus, detValor, detMulta, detMultaPaga;


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
        
        prepararRegistro();

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

    
    private void prepararRegistro() {
        // Como cada cliente/item aparece escrito dentro do ComboBox
        comboCliente.setConverter(new StringConverter<>() {
            @Override public String toString(Cliente c) {
                return c == null ? "" : c.getNome() + " (ID " + c.getId() + ")";
            }
            @Override public Cliente fromString(String texto) { return null; }   // não digitamos no ComboBox
        });
        comboItem.setConverter(new StringConverter<>() {
            @Override public String toString(Item i) {
                return i == null ? "" : i.getNome() + " — " + Formatador.moeda(i.getTaxaDiaria()) + "/dia";
            }
            @Override public Item fromString(String texto) { return null; }
        });

        // Sempre que o item ou uma das datas mudar, recalcula o resumo na hora
        comboCliente.valueProperty().addListener((obs, anterior, atual) -> atualizarResumo());
        comboItem.valueProperty().addListener((obs, anterior, atual) -> atualizarResumo());
        dataRetirada.valueProperty().addListener((obs, anterior, atual) -> atualizarResumo());
        dataDevolucao.valueProperty().addListener((obs, anterior, atual) -> atualizarResumo());
    }

    //Botão Registrar Aluguel
    @FXML
    private void abrirRegistro() {
        executar("abrir registro de aluguel", () -> {
            exigirSessao();

            List<Cliente> clientes = sistema.listarUsuarios().stream()
                    .filter(u -> u instanceof Cliente)
                    .map(u -> (Cliente) u)
                    .toList();
            List<Item> disponiveis = sistema.listarItens().stream()
                    .filter(Item::estaDisponivel)
                    .toList();

            if (clientes.isEmpty()) {
                throw new IllegalArgumentException("Não há clientes cadastrados.");
            }
            if (disponiveis.isEmpty()) {
                throw new IllegalArgumentException("Não há itens disponíveis para aluguel.");
            }

            comboCliente.getItems().setAll(clientes);
            comboItem.getItems().setAll(disponiveis);
            comboCliente.setValue(null);
            comboItem.setValue(null);
            dataRetirada.setValue(LocalDate.now(ZONA_LOJA));
            dataDevolucao.setValue(null);
            atualizarResumo();

            esconder(painelFinalizacao);
            esconder(painelDetalhes);
            mostrar(painelRegistro);
        });
    }

    @FXML private void cancelarRegistro()    { esconder(painelRegistro); }

    private long diasDoPeriodo() {
        if (dataRetirada.getValue() == null || dataDevolucao.getValue() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dataRetirada.getValue(), dataDevolucao.getValue());
    }

    private void atualizarResumo() {
        Item item = comboItem.getValue();
        long dias = diasDoPeriodo();
        double taxa = item == null ? 0 : item.getTaxaDiaria();
        double total = dias > 0 ? dias * taxa : 0;   // mesma conta do terminal: dias × taxa diária

        lblDias.setText(Math.max(dias, 0) + " dia(s)");
        lblTaxa.setText(Formatador.moeda(taxa));
        lblValorTotal.setText(Formatador.moeda(total));

        boolean completo = comboCliente.getValue() != null && item != null && dias > 0;
        btnConfirmarRegistro.setDisable(!completo);
    }

    /** Botão "Registrar aluguel" do painel: mesmas checagens e mesma sequência do terminal. */
    @FXML
    private void registrar() {
        executar("registrar aluguel", () -> {
            exigirSessao();

            Cliente cliente = comboCliente.getValue();
            Item item = comboItem.getValue();
            if (cliente == null) {
                throw new IllegalArgumentException("Selecione o cliente.");
            }
            if (sistema.clientePossuiMultaPendente(cliente.getId())) {
                throw new IllegalArgumentException(
                        "Cliente possui multa pendente e não pode realizar novos aluguéis.");
            }
            if (item == null) {
                throw new IllegalArgumentException("Selecione um item.");
            }
            if (!item.estaDisponivel()) {
                throw new IllegalArgumentException(
                        "Item indisponível para aluguel. Status atual: " + item.getStatus());
            }
            if (dataRetirada.getValue() == null || dataDevolucao.getValue() == null) {
                throw new IllegalArgumentException("Informe a data de retirada e a de devolução prevista.");
            }
            long dias = diasDoPeriodo();
            if (dias <= 0) {
                throw new IllegalArgumentException("A data de devolução deve ser posterior à data de retirada.");
            }

            double valorTotal = dias * item.getTaxaDiaria();
            int idContrato = sistema.gerarProximoIdContrato();
            ContratoAluguel contrato = new ContratoAluguel(idContrato, cliente, item,
                    dataRetirada.getValue().toString(), dataDevolucao.getValue().toString(), valorTotal);

            if (!sistema.cadastrarContrato(contrato)) {
                throw new IllegalArgumentException("Erro ao registrar aluguel.");
            }
            item.alugar(); 

            esconder(painelRegistro);
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Aluguel registrado",
                    "Contrato #" + idContrato + " registrado com sucesso.\n"
                    + "Cliente: " + cliente.getNome() + "\n"
                    + "Item: " + item.getNome() + "\n"
                    + "Período: " + dias + " dia(s)\n"
                    + "Valor total: " + Formatador.moeda(valorTotal));
        });
    }
  //Ações do fxml pra fazer

    @FXML private void abrirFinalizacao()    { emConstrucao(); }
    @FXML private void finalizar()           { emConstrucao(); }
    @FXML private void cancelarFinalizacao() { esconder(painelFinalizacao); }

    @FXML private void fecharDetalhes()      { esconder(painelDetalhes); }

    // ---------- Ver detalhes (MenuContratos.buscarContrato + exibirDetalhesContrato) ----------

    @FXML
    private void verDetalhes() {
        executar("ver detalhes do contrato", () -> {
            exigirSessao();
            ContratoAluguel contrato = buscarSelecionadoNoSistema();

            lblTituloDetalhes.setText("Contrato #" + contrato.getId());
            detCliente.setText(nomeCliente(contrato));
            detItem.setText(nomeItem(contrato));
            detRetirada.setText(Formatador.data(contrato.getDataRetirada()));
            detPrevista.setText(Formatador.data(contrato.getDataDevolucaoPrevista()));
            detReal.setText(Formatador.data(contrato.getDataDevolucaoReal()));   // vazio vira "—"
            detStatus.setText(contrato.getStatus());
            detValor.setText(Formatador.moeda(contrato.getValorTotal()));
            detMulta.setText(Formatador.moeda(contrato.getValorMulta()));
            detMultaPaga.setText(textoMultaPaga(contrato));

            esconder(painelRegistro);
            esconder(painelFinalizacao);
            mostrar(painelDetalhes);
        });
    }

    private String textoMultaPaga(ContratoAluguel contrato) {
        if (contrato.getValorMulta() <= 0) {
            return "Sem multa";
        }
        return contrato.isMultaPaga() ? "Sim" : "Não";
    }



    @FXML
    private void cancelarContrato() {
        executar("cancelar contrato", () -> {
            exigirSessao();
            ContratoAluguel contrato = buscarSelecionadoNoSistema();

            if (contrato.estaFinalizado()) {
                throw new IllegalArgumentException("Contrato finalizado não pode ser cancelado.");
            }
            if (contrato.estaCancelado()) {
                throw new IllegalArgumentException("Este contrato já está cancelado.");
            }

            if (!confirmar("Cancelar contrato", "Cancelar o contrato #" + contrato.getId()
                    + " de " + nomeCliente(contrato) + "? O item volta para o estoque.")) {
                return;   // usuário clicou em "Não"
            }

            if (!sistema.cancelarContrato(contrato.getId())) {
                throw new IllegalArgumentException("Erro ao cancelar contrato.");
            }
            if (contrato.getItem() != null) {
                contrato.getItem().devolver();
            }

            esconder(painelDetalhes);
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Contrato cancelado",
                    "Contrato cancelado. Item devolvido ao estoque.");
        });
    }

    private ContratoAluguel buscarSelecionadoNoSistema() {
        ContratoAluguel selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            throw new IllegalArgumentException("Selecione um contrato na tabela.");
        }
        ContratoAluguel contrato = sistema.buscarContrato(selecionado.getId());
        if (contrato == null) {
            throw new IllegalArgumentException("Contrato não encontrado.");
        }
        return contrato;
    }

    // ---------- Menu lateral (a navegação entre telas será ligada depois) ----------
    @FXML private void abrirFornecedores()   { emConstrucao(); }
    @FXML private void abrirMultas()         { emConstrucao(); }
    @FXML private void encerrarSessao()      { executar("encerrar sessão", () -> sair.run()); }

    
    private boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        return dialogo.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private ReadOnlyStringWrapper texto(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
    }

    private String nomeCliente(ContratoAluguel contrato) {
        return contrato.getCliente() == null ? "-" : contrato.getCliente().getNome();
    }

    private String nomeItem(ContratoAluguel contrato) {
        return contrato.getItem() == null ? "-" : contrato.getItem().getNome();
    }

    private void mostrar(VBox painel) {
        painel.setVisible(true);
        painel.setManaged(true);
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
