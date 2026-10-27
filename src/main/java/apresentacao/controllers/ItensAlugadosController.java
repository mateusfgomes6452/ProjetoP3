package apresentacao.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.ZoneId;

import apresentacao.base.BaseControllersRelatorios;
import apresentacao.util.Formatador;
import entidades.ContratoAluguel;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import apresentacao.util.Mensagens;
import apresentacao.util.Pilulas;

/** Aba "Itens alugados" (RelatorioItensAlugados.fxml): contratos ativos, com destaque para devoluções atrasadas. */
public final class ItensAlugadosController extends BaseControllersRelatorios {

    private enum Filtro { TODOS, EM_DIA, ATRASADOS }

    @FXML private Button btnTodos;
    @FXML private Button btnEmDia;
    @FXML private Button btnAtrasados;
    @FXML private Button btnExportar;
    @FXML private Label lblTotalAlugados;
    @FXML private Label lblTotalEmDia;
    @FXML private Label lblTotalAtrasados;
    @FXML private Label lblAviso;
    @FXML private TableView<ContratoAluguel> tabela;
    @FXML private TableColumn<ContratoAluguel, String> colId;
    @FXML private TableColumn<ContratoAluguel, String> colCliente;
    @FXML private TableColumn<ContratoAluguel, String> colItem;
    @FXML private TableColumn<ContratoAluguel, String> colRetirada;
    @FXML private TableColumn<ContratoAluguel, String> colPrevista;
    @FXML private TableColumn<ContratoAluguel, ContratoAluguel> colSituacao;
    @FXML private TableColumn<ContratoAluguel, String> colAtraso;

    private final ObservableList<ContratoAluguel> ativos = FXCollections.observableArrayList();
    private final FilteredList<ContratoAluguel> visiveis = new FilteredList<>(ativos);
    private Filtro filtro = Filtro.TODOS;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper("#" + c.getValue().getId()));
        colCliente.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCliente().getNome()));
        colItem.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getItem().getNome()));
        colRetirada.setCellValueFactory(c -> new ReadOnlyStringWrapper(Formatador.data(c.getValue().getDataRetirada())));
        colPrevista.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Formatador.data(c.getValue().getDataDevolucaoPrevista())));
        colAtraso.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(textoAtraso(diasDeAtraso(c.getValue()))));

        colSituacao.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
        colSituacao.setCellFactory(coluna -> new TableCell<ContratoAluguel, ContratoAluguel>() {
            @Override
            protected void updateItem(ContratoAluguel contrato, boolean vazio) {
                super.updateItem(contrato, vazio);
                setText(null);
                if (vazio || contrato == null) {
                    setGraphic(null);
                } else {
                    setGraphic(atrasado(contrato) ? Pilulas.perigo("Atrasado") : Pilulas.sucesso("Em dia"));
                }
            }
        });

        tabela.setItems(visiveis);
        Mensagens.esconder(lblAviso);
    }

    /** Chamado por RelatoriosController.configurar: guarda a sessão e carrega os dados. */
    public void configurar(SistemaFacade sistema, Usuario usuario) {
        iniciarSessao(sistema, usuario);
        atualizar();
    }

    /** Recarrega os contratos ativos do back-end. */
    @FXML
    public void atualizar() {
        executar("consultar itens alugados", () -> {
            exigirSessao();
            ativos.setAll(sistema.listarContratosAtivos());

            long atrasados = ativos.stream().filter(this::atrasado).count();
            lblTotalAlugados.setText(String.valueOf(ativos.size()));
            lblTotalAtrasados.setText(String.valueOf(atrasados));
            lblTotalEmDia.setText(String.valueOf(ativos.size() - atrasados));

            aplicarFiltro();
            Mensagens.esconder(lblAviso);
        });
    }

    @FXML
    private void filtrarTodos() {
        trocarFiltro(Filtro.TODOS, btnTodos);
    }

    @FXML
    private void filtrarEmDia() {
        trocarFiltro(Filtro.EM_DIA, btnEmDia);
    }

    @FXML
    private void filtrarAtrasados() {
        trocarFiltro(Filtro.ATRASADOS, btnAtrasados);
    }

    @FXML
    private void exportar() {
        executar("exportar itens alugados", () -> {
            exigirSessao();
            if (visiveis.isEmpty()) {
                throw new IllegalArgumentException("Não há contratos para exportar.");
            }
            sistema.exportarItensAlugados(visiveis.stream().toList());
            Mensagens.sucesso(lblAviso, "CSV salvo em relatorios/itens_alugados.csv");
        });
    }

    // ---------- regras de exibição ----------

    private void trocarFiltro(Filtro novo, Button botaoAtivo) {
        filtro = novo;
        for (Button botao : List.of(btnTodos, btnEmDia, btnAtrasados)) {
            botao.getStyleClass().remove("filter-active");
        }
        botaoAtivo.getStyleClass().add("filter-active");
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        visiveis.setPredicate(c -> {
            switch (filtro) {
                case EM_DIA:
                    return !atrasado(c);
                case ATRASADOS:
                    return atrasado(c);
                default:
                    return true;
            }
        });
        btnExportar.setDisable(visiveis.isEmpty());
        tabela.setPlaceholder(new Label(filtro == Filtro.ATRASADOS
                ? "Nenhuma devolução atrasada." : "Não existem contratos ativos."));
    }

    private boolean atrasado(ContratoAluguel contrato) {
        return sistema.contratoEstaAtrasado(contrato);
    }

    private long diasDeAtraso(ContratoAluguel contrato) {
        if (!atrasado(contrato)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.parse(contrato.getDataDevolucaoPrevista()), LocalDate.now(ZoneId.systemDefault()));
    }

    private String textoAtraso(long dias) {
        if (dias == 0) {
            return "—";
        }
        if (dias == 1) {
            return "1 dia";
        }
        return dias + " dias";
    }
}
