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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class MeusAlugueisClientesController extends BaseControllerCliente {

    private enum Filtro { TODOS, EM_DIA, ATRASADOS }
    private static final String ATRASADO = "Atrasado";
    private static final String EM_DIA = "Em dia";

    @FXML private Label lblAtivos;
    @FXML private Label lblEmDia;
    @FXML private Label lblAtrasados;
    @FXML private Label lblResultado;
    @FXML private TextField busca;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colPrevista;
    @FXML private TableColumn<ContratoAluguel, String> colSituacao;
    @FXML private TableColumn<ContratoAluguel, String> colValor;
    @FXML private Button btnTodos;
    @FXML private Button btnEmDia;
    @FXML private Button btnAtrasados;
    @FXML private Button btnDetalhes;
    @FXML private VBox painelDetalhes;
    @FXML private Label lblDetalheContrato;
    @FXML private Label lblDetalheItem;
    @FXML private Label lblDetalheRetirada;
    @FXML private Label lblDetalhePrevista;
    @FXML private Label lblDetalheSituacao;
    @FXML private Label lblDetalheValor;

    private final ObservableList<ContratoAluguel> ativos = FXCollections.observableArrayList();
    private final FilteredList<ContratoAluguel> visiveis = new FilteredList<>(ativos);
    private Filtro filtro = Filtro.TODOS;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> prop("#" + c.getValue().getId()));
        colItem.setCellValueFactory(c -> prop(nomeItem(c.getValue())));
        colRetirada.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(c -> prop(Formatador.data(c.getValue().getDataDevolucaoPrevista())));
        colSituacao.setCellValueFactory(c -> prop(situacao(c.getValue())));
        colSituacao.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String situacao, boolean vazio) {
                super.updateItem(situacao, vazio);
                setText(null);
                if (vazio || situacao == null) {
                    setGraphic(null);
                } else {
                    setGraphic(ATRASADO.equals(situacao) ? Pilulas.perigo(situacao) : Pilulas.sucesso(situacao));
                }
            }
        });
        colValor.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getValorTotal())));

        tabela.setItems(visiveis);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Você não possui aluguéis ativos."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antes, atual) -> {
            btnDetalhes.setDisable(atual == null);
            fecharDetalhes();
        });
        busca.textProperty().addListener((obs, antes, atual) -> filtrar());
    }

    @Override
    @FXML
    protected void atualizar() {
        executar("atualizar meus aluguéis", () -> {
            exigirSessao();
            List<ContratoAluguel> contratos = sistema.listarContratosAtivos().stream()
                    .filter(c -> c.estaAtivo() && c.getCliente() != null
                            && c.getCliente().getId() == cliente.getId())
                    .sorted(Comparator.comparingInt(ContratoAluguel::getId).reversed())
                    .toList();
            ativos.setAll(contratos);
            long atrasados = ativos.stream().filter(sistema::contratoEstaAtrasado).count();
            lblAtivos.setText(String.valueOf(ativos.size()));
            lblAtrasados.setText(String.valueOf(atrasados));
            lblEmDia.setText(String.valueOf(ativos.size() - atrasados));
            filtrar();
        });
    }

    @FXML private void filtrarTodos() { alterarFiltro(Filtro.TODOS, btnTodos); }
    @FXML private void filtrarEmDia() { alterarFiltro(Filtro.EM_DIA, btnEmDia); }
    @FXML private void filtrarAtrasados() { alterarFiltro(Filtro.ATRASADOS, btnAtrasados); }

    private void alterarFiltro(Filtro novo, Button selecionado) {
        filtro = novo;
        for (Button b : List.of(btnTodos, btnEmDia, btnAtrasados)) {
            b.getStyleClass().remove("filter-active");
        }
        selecionado.getStyleClass().add("filter-active");
        filtrar();
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        visiveis.setPredicate(c -> (filtro == Filtro.TODOS
                    || (filtro == Filtro.ATRASADOS) == sistema.contratoEstaAtrasado(c))
                && (termo.isEmpty() || String.valueOf(c.getId()).equals(termo)
                    || nomeItem(c).toLowerCase(Locale.ROOT).contains(termo)));
        lblResultado.setText("Exibindo " + visiveis.size() + " de " + ativos.size());
        tabela.getSelectionModel().clearSelection();
    }

    @FXML
    private void verDetalhes() {
        executar("ver detalhes do aluguel", () -> {
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
            lblDetalheSituacao.setText("Situação: " + textoSituacaoDetalhada(contrato));
            lblDetalheValor.setText("Valor do aluguel: " + Formatador.moeda(contrato.getValorTotal()));
            painelDetalhes.setVisible(true);
            painelDetalhes.setManaged(true);
        });
    }

    @FXML
    private void fecharDetalhes() {
        painelDetalhes.setVisible(false);
        painelDetalhes.setManaged(false);
    }

    private String situacao(ContratoAluguel c) {
        return sistema != null && sistema.contratoEstaAtrasado(c) ? ATRASADO : EM_DIA;
    }

    private long diasDeAtraso(ContratoAluguel c) {
        if (!sistema.contratoEstaAtrasado(c)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.parse(c.getDataDevolucaoPrevista()), LocalDate.now());
    }

    private String textoSituacaoDetalhada(ContratoAluguel c) {
        long dias = diasDeAtraso(c);
        if (dias == 0) {
            return EM_DIA;
        }
        String texto = ATRASADO + " há " + dias + " dia(s).";
        if (c.getItem() != null) {
            double multa = sistema.calcularMulta(dias, c.getItem().getTaxaDiaria());
            texto += " Se devolvido hoje, a multa será de " + Formatador.moeda(multa) + ".";
        }
        return texto;
    }

    private String nomeItem(ContratoAluguel c) {
        return c.getItem() == null ? "Item não encontrado" : c.getItem().getNome();
    }

    private ReadOnlyStringWrapper prop(String texto) {
        return new ReadOnlyStringWrapper(texto == null ? "" : texto);
    }
}
