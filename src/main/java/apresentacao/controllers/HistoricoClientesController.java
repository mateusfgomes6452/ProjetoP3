package apresentacao.controllers;

import apresentacao.base.BaseControllerCliente;
import entidades.ContratoAluguel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Histórico completo limitado ao ID do cliente autenticado; sem seletor de outros clientes. */
public final class HistoricoClientesController extends BaseControllerCliente {

    private enum Filtro { TODOS, ATIVOS, FINALIZADOS, CANCELADOS }

    @FXML private Label lblTotal;
    @FXML private Label lblFinalizados;
    @FXML private Label lblCancelados;
    @FXML private Label lblResultado;
    @FXML private TextField busca;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colPrevista;
    @FXML private TableColumn<ContratoAluguel, String> colDevolucao;
    @FXML private TableColumn<ContratoAluguel, String> colStatus;
    @FXML private TableColumn<ContratoAluguel, String> colValor;
    @FXML private TableColumn<ContratoAluguel, String> colMulta;
    @FXML private Button btnTodos;
    @FXML private Button btnAtivos;
    @FXML private Button btnFinalizados;
    @FXML private Button btnCancelados;
    @FXML private Button btnDetalhes;
    @FXML private VBox painelDetalhes;
    @FXML private Label lblDetalheContrato;
    @FXML private Label lblDetalheItem;
    @FXML private Label lblDetalheRetirada;
    @FXML private Label lblDetalhePrevista;
    @FXML private Label lblDetalheReal;
    @FXML private Label lblDetalheStatus;
    @FXML private Label lblDetalheValor;
    @FXML private Label lblDetalheMulta;

    private final ObservableList<ContratoAluguel> historico = FXCollections.observableArrayList();
    private final FilteredList<ContratoAluguel> visiveis = new FilteredList<>(historico);
    private Filtro filtro = Filtro.TODOS;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> prop("#" + c.getValue().getId()));
        colItem.setCellValueFactory(c -> prop(nomeItem(c.getValue())));
        colRetirada.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataDevolucaoPrevista())));
        colDevolucao.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataDevolucaoReal())));
        colStatus.setCellValueFactory(c -> prop(c.getValue().getStatus()));
        colStatus.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean vazio) {
                super.updateItem(status, vazio);
                setText(null);
                setGraphic(vazio || status == null ? null : etiquetaStatus(status));
            }
        });
        colValor.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getValorTotal())));
        colMulta.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getValorMulta())));
        tabela.setItems(visiveis);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Você ainda não possui contratos registrados."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antes, atual) -> {
            btnDetalhes.setDisable(atual == null);
            fecharDetalhes();
        });
        busca.textProperty().addListener((obs, antes, atual) -> filtrar());
    }

    @Override
    @FXML
    protected void atualizar() {
        executar("atualizar histórico", () -> {
            exigirSessao();
            List<ContratoAluguel> doCliente = sistema.listarHistoricoCliente(cliente.getId()).stream()
                    .filter(c -> c.getCliente() != null && c.getCliente().getId() == cliente.getId())
                    .sorted(Comparator.comparingInt(ContratoAluguel::getId).reversed())
                    .toList();
            historico.setAll(doCliente);
            lblTotal.setText(String.valueOf(historico.size()));
            lblFinalizados.setText(String.valueOf(historico.stream().filter(ContratoAluguel::estaFinalizado).count()));
            lblCancelados.setText(String.valueOf(historico.stream().filter(ContratoAluguel::estaCancelado).count()));
            filtrar();
        });
    }

    @FXML private void filtrarTodos() { alterarFiltro(Filtro.TODOS, btnTodos); }
    @FXML private void filtrarAtivos() { alterarFiltro(Filtro.ATIVOS, btnAtivos); }
    @FXML private void filtrarFinalizados() { alterarFiltro(Filtro.FINALIZADOS, btnFinalizados); }
    @FXML private void filtrarCancelados() { alterarFiltro(Filtro.CANCELADOS, btnCancelados); }

    private void alterarFiltro(Filtro novo, Button selecionado) {
        filtro = novo;
        for (Button b : List.of(btnTodos, btnAtivos, btnFinalizados, btnCancelados)) {
            b.getStyleClass().remove("filter-active");
        }
        selecionado.getStyleClass().add("filter-active");
        filtrar();
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        visiveis.setPredicate(c -> (filtro == Filtro.TODOS || switch (filtro) {
                    case ATIVOS -> c.estaAtivo();
                    case FINALIZADOS -> c.estaFinalizado();
                    case CANCELADOS -> c.estaCancelado();
                    default -> true;
                }) && (termo.isEmpty() || String.valueOf(c.getId()).equals(termo)
                    || nomeItem(c).toLowerCase(Locale.ROOT).contains(termo)));
        lblResultado.setText("Exibindo " + visiveis.size() + " de " + historico.size());
        tabela.getSelectionModel().clearSelection();
    }


    @FXML
    private void verDetalhes() {
        executar("ver detalhes do contrato", () -> {
            exigirSessao();
            ContratoAluguel contrato = tabela.getSelectionModel().getSelectedItem();
            if (contrato == null || contrato.getCliente() == null
                    || contrato.getCliente().getId() != cliente.getId()) {
                throw new SecurityException("Contrato indisponível para a sua conta.");
            }
            lblDetalheContrato.setText("Contrato #" + contrato.getId());
            lblDetalheItem.setText("Item: " + nomeItem(contrato));
            lblDetalheRetirada.setText("Retirada: " + Formatador.data(contrato.getDataRetirada()));
            lblDetalhePrevista.setText("Devolução prevista: " + Formatador.data(contrato.getDataDevolucaoPrevista()));
            lblDetalheReal.setText("Devolução efetiva: " + Formatador.data(contrato.getDataDevolucaoReal()));
            lblDetalheStatus.setText("Status: " + contrato.getStatus());
            lblDetalheValor.setText("Valor do aluguel: " + Formatador.moeda(contrato.getValorTotal()));
            lblDetalheMulta.setText("Multa: " + textoMulta(contrato));
            painelDetalhes.setVisible(true);
            painelDetalhes.setManaged(true);
        });
    }

    @FXML
    private void fecharDetalhes() {
        painelDetalhes.setVisible(false);
        painelDetalhes.setManaged(false);
    }
    private Label etiquetaStatus(String status) {
        return switch (status) {
            case "ATIVO" -> Pilulas.sucesso("Ativo");
            case "FINALIZADO" -> Pilulas.neutra("Finalizado");
            case "CANCELADO" -> Pilulas.perigo("Cancelado");
            default -> Pilulas.neutra(status);
        };
    }

    private String textoMulta(ContratoAluguel contrato) {
        if (contrato.getValorMulta() <= 0) {
            return "Sem multa";
        }
        return Formatador.moeda(contrato.getValorMulta()) + (contrato.isMultaPaga() ? " (paga)" : " (pendente)");
    }

    private String nomeItem(ContratoAluguel contrato) {
        return contrato.getItem() == null ? "Item não encontrado" : contrato.getItem().getNome();
    }

    private ReadOnlyStringWrapper prop(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
    }
}
