package apresentacao.controllers;

import apresentacao.base.BaseControllerCliente;
import entidades.Item;
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
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/** Catálogo público de itens disponíveis, em modo de consulta para clientes. */
public final class ItensClientesController extends BaseControllerCliente {

    private static final String TODAS = "Todas";

    @FXML private Label lblTotal;
    @FXML private Label lblCategorias;
    @FXML private Label lblTaxaMedia;
    @FXML private Label lblResultado;
    @FXML private TextField busca;
    @FXML private FlowPane filtrosCategoria;
    @FXML private TableView<Item> tabela;
    @FXML private TableColumn<Item, String> colId;
    @FXML private TableColumn<Item, String> colNome;
    @FXML private TableColumn<Item, String> colCategoria;
    @FXML private TableColumn<Item, String> colDescricao;
    @FXML private TableColumn<Item, String> colEstado;
    @FXML private TableColumn<Item, String> colTaxa;
    @FXML private Button btnDetalhes;
    @FXML private VBox painelDetalhes;
    @FXML private Label lblDetalheNome;
    @FXML private Label lblDetalheDescricao;
    @FXML private Label lblDetalheCategoria;
    @FXML private Label lblDetalheEstado;
    @FXML private Label lblDetalheTaxa;
    @FXML private Label lblDetalheReposicao;

    private final ObservableList<Item> itens = FXCollections.observableArrayList();
    private final FilteredList<Item> visiveis = new FilteredList<>(itens);
    private String categoriaAtiva = TODAS;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(c -> prop("#" + c.getValue().getId()));
        colNome.setCellValueFactory(c -> prop(c.getValue().getNome()));
        colCategoria.setCellValueFactory(c -> prop(nomeCategoria(c.getValue())));
        colDescricao.setCellValueFactory(c -> prop(c.getValue().getDescricao()));
        colEstado.setCellValueFactory(c -> prop(c.getValue().getEstadoConservacao()));
        colTaxa.setCellValueFactory(c -> prop(Formatador.moeda(c.getValue().getTaxaDiaria())));

        tabela.setItems(visiveis);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum item disponível no momento."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antes, atual) -> {
            btnDetalhes.setDisable(atual == null);
            fecharDetalhes();
        });
        busca.textProperty().addListener((obs, antes, atual) -> filtrar());
    }

    @Override
    @FXML
    protected void atualizar() {
        executar("atualizar catálogo", () -> {
            exigirSessao();
            List<Item> resultado = sistema.listarItensDisponiveis().stream()
                    .filter(Item::estaDisponivel)
                    .sorted(Comparator.comparing(Item::getNome, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            itens.setAll(resultado);
            lblTotal.setText(String.valueOf(itens.size()));
            lblCategorias.setText(String.valueOf(itens.stream().map(this::nomeCategoria).distinct().count()));
            lblTaxaMedia.setText(Formatador.moeda(itens.stream().mapToDouble(Item::getTaxaDiaria).average().orElse(0)));
            montarFiltros(resultado);
            filtrar();
            tabela.getSelectionModel().clearSelection();
        });
    }

    @FXML
    private void limparBusca() {
        busca.clear();
        categoriaAtiva = TODAS;
        montarFiltros(itens);
        filtrar();
    }

    @FXML
    private void verDetalhes() {
        executar("ver detalhes do item", () -> {
            exigirSessao();
            Item item = tabela.getSelectionModel().getSelectedItem();
            if (item == null) {
                throw new IllegalArgumentException("Selecione um item para consultar.");
            }
            lblDetalheNome.setText(item.getNome() + "  ·  #" + item.getId());
            lblDetalheDescricao.setText("Descrição: " + (item.getDescricao() == null ? "-" : item.getDescricao()));
            lblDetalheCategoria.setText("Categoria: " + nomeCategoria(item));
            lblDetalheEstado.setText("Estado de conservação: " + item.getEstadoConservacao());
            lblDetalheTaxa.setText("Taxa diária: " + Formatador.moeda(item.getTaxaDiaria()));
            painelDetalhes.setManaged(true);
            painelDetalhes.setVisible(true);
        });
    }

    @FXML
    private void fecharDetalhes() {
        painelDetalhes.setVisible(false);
        painelDetalhes.setManaged(false);
    }

    private void montarFiltros(List<Item> resultado) {
        Set<String> categorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        resultado.forEach(i -> categorias.add(nomeCategoria(i)));
        if (!TODAS.equals(categoriaAtiva) && !categorias.contains(categoriaAtiva)) {
            categoriaAtiva = TODAS;
        }
        filtrosCategoria.getChildren().clear();
        filtrosCategoria.getChildren().add(criarFiltro(TODAS));
        categorias.forEach(c -> filtrosCategoria.getChildren().add(criarFiltro(c)));
    }

    private Button criarFiltro(String categoria) {
        Button botao = new Button(categoria);
        botao.getStyleClass().add("filter-button");
        if (categoria.equals(categoriaAtiva)) {
            botao.getStyleClass().add("filter-active");
        }
        botao.setOnAction(e -> {
            categoriaAtiva = categoria;
            filtrosCategoria.getChildren().forEach(no -> no.getStyleClass().remove("filter-active"));
            botao.getStyleClass().add("filter-active");
            filtrar();
        });
        return botao;
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        visiveis.setPredicate(i -> (TODAS.equals(categoriaAtiva) || nomeCategoria(i).equalsIgnoreCase(categoriaAtiva))
                && (termo.isEmpty()
                    || String.valueOf(i.getId()).equals(termo)
                    || texto(i.getNome()).contains(termo)
                    || texto(i.getDescricao()).contains(termo)
                    || texto(nomeCategoria(i)).contains(termo)));
        lblResultado.setText("Exibindo " + visiveis.size() + " de " + itens.size());
        tabela.getSelectionModel().clearSelection();
    }

    private String nomeCategoria(Item item) {
        return item.getCategoria() == null ? "Sem categoria" : item.getCategoria().getNome();
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT);
    }

    private ReadOnlyStringWrapper prop(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
    }
}
