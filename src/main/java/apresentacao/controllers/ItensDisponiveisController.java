package apresentacao.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import apresentacao.base.BaseControllersRelatorios;
import apresentacao.util.Formatador;
import entidades.Item;
import entidades.Usuario;
import facade.SistemaFacade;
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
import javafx.scene.layout.FlowPane;
import apresentacao.util.Mensagens;

/** Aba "Itens disponíveis" (RelatorioItensDisponiveis.fxml): itens com status DISPONIVEL, filtrados por categoria. */
public final class ItensDisponiveisController extends BaseControllersRelatorios {

    private static final String TODAS = "Todas";

    @FXML private Label lblTotal;
    @FXML private Label lblCategorias;
    @FXML private Label lblTaxaMedia;
    @FXML private Label lblResultado;
    @FXML private Label lblAviso;
    @FXML private TextField busca;
    @FXML private FlowPane filtrosCategoria;
    @FXML private TableView<Item> tabela;
    @FXML private TableColumn<Item, String> colCategoria;
    @FXML private TableColumn<Item, String> colId;
    @FXML private TableColumn<Item, String> colNome;
    @FXML private TableColumn<Item, String> colTaxa;

    private final ObservableList<Item> todos = FXCollections.observableArrayList();
    private final FilteredList<Item> visiveis = new FilteredList<>(todos);
    private String categoriaSelecionada = TODAS;
    private static final String FILTER_ACTIVE = "filter-active";

    @FXML
    private void initialize() {
        colCategoria.setCellValueFactory(c -> new ReadOnlyStringWrapper(nomeCategoria(c.getValue())));
        colId.setCellValueFactory(c -> new ReadOnlyStringWrapper("#" + c.getValue().getId()));
        colNome.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNome()));
        colTaxa.setCellValueFactory(c ->
                new ReadOnlyStringWrapper(Formatador.moeda(c.getValue().getTaxaDiaria()) + "/dia"));

        tabela.setItems(visiveis);
        tabela.setPlaceholder(new Label("Nenhum item disponível no momento."));
        busca.textProperty().addListener((obs, antes, depois) -> aplicarFiltro());
        Mensagens.esconder(lblAviso);
    }

    /** Chamado por RelatoriosController.configurar: guarda a sessão e carrega os dados. */
    public void configurar(SistemaFacade sistema, Usuario usuario) {
        iniciarSessao(sistema, usuario);
        atualizar();
    }

    /** Recarrega os itens do back-end (ao abrir a aba e no botão "Atualizar lista"). */
    @FXML
    public void atualizar() {
        executar("consultar itens disponíveis", () -> {
            exigirSessao();
            List<Item> itens = sistema.listarItensDisponiveis().stream()
                    .sorted(Comparator.comparing(this::nomeCategoria, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(Item::getNome, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            todos.setAll(itens);
            montarFiltros(itens);
            aplicarFiltro();
            Mensagens.esconder(lblAviso);
        });
    }

    @FXML
    private void exportar() {
        executar("exportar itens disponíveis", () -> {
            exigirSessao();
            if (visiveis.isEmpty()) {
                throw new IllegalArgumentException("Não há itens para exportar.");
            }
            sistema.exportarItensDisponiveis(List.copyOf(visiveis));
            Mensagens.sucesso(lblAviso, "CSV salvo em relatorios/itens_disponiveis.csv");
        });
    }

    // ---------- filtros ----------

    private void montarFiltros(List<Item> itens) {
        Set<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        itens.forEach(i -> categorias.add(nomeCategoria(i)));

        if (!TODAS.equals(categoriaSelecionada) && !categorias.contains(categoriaSelecionada)) {
            categoriaSelecionada = TODAS;
        }

        filtrosCategoria.getChildren().clear();
        filtrosCategoria.getChildren().add(novoFiltro(TODAS));
        categorias.forEach(c -> filtrosCategoria.getChildren().add(novoFiltro(c)));
    }

    private Button novoFiltro(String categoria) {
        Button botao = new Button(categoria);
        botao.getStyleClass().add("filter-button");
        if (categoria.equals(categoriaSelecionada)) {
            botao.getStyleClass().add(FILTER_ACTIVE);
        }
        botao.setOnAction(e -> {
            categoriaSelecionada = categoria;
            filtrosCategoria.getChildren().forEach(no -> no.getStyleClass().remove(FILTER_ACTIVE));
            botao.getStyleClass().add(FILTER_ACTIVE);
            aplicarFiltro();
        });
        return botao;
    }

    private void aplicarFiltro() {
        String texto = busca.getText() == null ? "" : busca.getText().trim().toLowerCase();
        visiveis.setPredicate(item -> {
            boolean categoriaOk = TODAS.equals(categoriaSelecionada) || nomeCategoria(item).equals(categoriaSelecionada);
            boolean textoOk = texto.isEmpty() || item.getNome().toLowerCase().contains(texto);
            return categoriaOk && textoOk;
        });

        lblTotal.setText(String.valueOf(visiveis.size()));
        lblCategorias.setText(String.valueOf(visiveis.stream().map(this::nomeCategoria).distinct().count()));
        lblTaxaMedia.setText(Formatador.moeda(visiveis.stream().mapToDouble(Item::getTaxaDiaria).average().orElse(0)));
        lblResultado.setText("Exibindo " + visiveis.size() + " de " + todos.size());
    }

    private String nomeCategoria(Item item) {
        return item.getCategoria() == null ? "Sem categoria" : item.getCategoria().getNome();
    }
}
