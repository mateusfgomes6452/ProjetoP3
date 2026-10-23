package apresentacao.controllers;

import apresentacao.Navegador;
import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import facade.SistemaFacade;
import facade.SistemaJavaFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ItensController {

    @FXML private BorderPane raiz;
    @FXML private Label lblPerfil;
    @FXML private Label lblAvatar;
    @FXML private Label lblNomeUsuario;

    @FXML private VBox formulario;
    @FXML private Label tituloFormulario;
    @FXML private TextField campoNome;
    @FXML private TextField campoTaxa;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoReposicao;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Fornecedor> comboFornecedor;
    @FXML private Button btnSalvar;

    @FXML private Label lblTotal;
    @FXML private Label lblAtivos;
    @FXML private Label lblInativos;
    @FXML private Label lblResultado;
    @FXML private TextField busca;
    @FXML private Button btnTodos;
    @FXML private Button btnAtivos;
    @FXML private Button btnInativos;

    @FXML private TableView<Item> tabela;
    @FXML private TableColumn<Item, String> colId;
    @FXML private TableColumn<Item, String> colNome;
    @FXML private TableColumn<Item, String> colDescricao;
    @FXML private TableColumn<Item, String> colTaxa;
    @FXML private TableColumn<Item, String> colEstado;
    @FXML private TableColumn<Item, String> colValorRep;
    @FXML private TableColumn<Item, String> colStatus;

    private final SistemaFacade sistema = SistemaJavaFX.getSistema();

    private FilteredList<Item> listaFiltrada;
    private Item itemEmEdicao;
    private String filtroStatus = "TODOS";

    @FXML
    private void initialize() {
        colId.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getId())));
        colNome.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getNome()));
        colDescricao.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getDescricao()));
        colTaxa.setCellValueFactory(l -> new SimpleStringProperty(String.format("R$ %.2f", l.getValue().getTaxaDiaria())));
        colEstado.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getEstadoConservacao()));
        colValorRep.setCellValueFactory(l -> new SimpleStringProperty(String.format("R$ %.2f", l.getValue().getValorReposicao())));
        colStatus.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().isAtivo() ? "Ativo" : "Inativo"));

        comboEstado.setItems(FXCollections.observableArrayList("Novo", "Bom", "Regular", "Ruim"));

        comboCategoria.setConverter(new StringConverter<>() {
            @Override public String toString(Categoria c) { return c == null ? "" : c.getNome(); }
            @Override public Categoria fromString(String s) { return null; }
        });
        comboFornecedor.setConverter(new StringConverter<>() {
            @Override public String toString(Fornecedor f) { return f == null ? "" : f.getRazaoSocial(); }
            @Override public Fornecedor fromString(String s) { return null; }
        });

        busca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        atualizar();
    }
    
    @FXML
    private void atualizar() {
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(sistema.listarItens()), i -> true);
        tabela.setItems(listaFiltrada);
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        String texto = busca.getText() == null ? "" : busca.getText().trim().toLowerCase();

        listaFiltrada.setPredicate(item -> {
            boolean passaStatus = filtroStatus.equals("TODOS")
                    || (filtroStatus.equals("ATIVOS") && item.isAtivo())
                    || (filtroStatus.equals("INATIVOS") && !item.isAtivo());

            boolean passaBusca = texto.isEmpty()
                    || String.valueOf(item.getId()).equals(texto)
                    || item.getNome().toLowerCase().contains(texto);

            return passaStatus && passaBusca;
        });

        int total = listaFiltrada.getSource().size();
        long ativos = listaFiltrada.getSource().stream().filter(Item::isAtivo).count();
        lblTotal.setText(String.valueOf(total));
        lblAtivos.setText(String.valueOf(ativos));
        lblInativos.setText(String.valueOf(total - ativos));
        lblResultado.setText("Exibindo " + listaFiltrada.size() + " de " + total);
    }

    @FXML private void todos()    { trocarFiltro("TODOS", btnTodos); }
    @FXML private void ativos()   { trocarFiltro("ATIVOS", btnAtivos); }
    @FXML private void inativos() { trocarFiltro("INATIVOS", btnInativos); }

    private void trocarFiltro(String filtro, Button botao) {
        filtroStatus = filtro;
        for (Button b : new Button[] { btnTodos, btnAtivos, btnInativos }) {
            b.getStyleClass().remove("filter-active");
        }
        botao.getStyleClass().add("filter-active");
        aplicarFiltro();
    }

    @FXML
    private void novo() {
        itemEmEdicao = null;
        tituloFormulario.setText("Cadastrar item");
        btnSalvar.setText("Salvar item");
        limparFormulario();
        carregarCombos();
        mostrarFormulario(true);
    }

    @FXML
    private void editar() {
        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            aviso("Selecione um item na tabela.");
            return;
        }
        itemEmEdicao = selecionado;
        tituloFormulario.setText("Editar item");
        btnSalvar.setText("Salvar alterações");
        carregarCombos();

        campoNome.setText(selecionado.getNome());
        campoDescricao.setText(selecionado.getDescricao());
        campoTaxa.setText(String.valueOf(selecionado.getTaxaDiaria()));
        campoReposicao.setText(String.valueOf(selecionado.getValorReposicao()));
        comboEstado.setValue(selecionado.getEstadoConservacao());
        comboCategoria.setValue(selecionado.getCategoria());
        comboFornecedor.setValue(selecionado.getFornecedor());
        mostrarFormulario(true);
    }

    @FXML
    private void cancelar() {
        mostrarFormulario(false);
    }

    @FXML
    private void salvar() {
        String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText().trim();
        String estado = comboEstado.getValue();
        Categoria categoria = comboCategoria.getValue();
        Fornecedor fornecedor = comboFornecedor.getValue();

        if (nome.isEmpty() || descricao.isEmpty() || estado == null
                || categoria == null || fornecedor == null) {
            aviso("Preencha todos os campos.");
            return;
        }

        double taxa;
        double reposicao;
        try {
            taxa = Double.parseDouble(campoTaxa.getText().trim().replace(",", "."));
            reposicao = Double.parseDouble(campoReposicao.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            aviso("Taxa diária e valor de reposição devem ser números.");
            return;
        }
        if (taxa <= 0 || reposicao <= 0) {
            aviso("Os valores devem ser maiores que zero.");
            return;
        }

        boolean ok;
        if (itemEmEdicao == null) {
            Item novoItem = new Item(sistema.gerarProximoIdItem(), nome, descricao,
                    taxa, estado, reposicao, categoria, fornecedor);
            ok = sistema.cadastrarItem(novoItem);
        } else {
            itemEmEdicao.setNome(nome);
            itemEmEdicao.setDescricao(descricao);
            itemEmEdicao.setTaxaDiaria(taxa);
            itemEmEdicao.setEstadoConservacao(estado);
            itemEmEdicao.setValorReposicao(reposicao);
            itemEmEdicao.setCategoria(categoria);
            itemEmEdicao.setFornecedor(fornecedor);
            ok = sistema.atualizarItem(itemEmEdicao);
        }

        if (!ok) {
            aviso("Não foi possível salvar o item.");
            return;
        }
        mostrarFormulario(false);
        atualizar();
    }

    @FXML
    private void desativar() {
        Item selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            aviso("Selecione um item na tabela.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Desativar o item " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            // AJUSTE: confira se o "desativar" do terminal usa excluirItem
            sistema.excluirItem(selecionado.getId());
            atualizar();
        }
    }

    @FXML
    private void abrirItens() {
        mostrarFormulario(false);
        atualizar();
    }

    @FXML
    private void encerrarSessao() {
        Navegador.trocarTela(raiz, "/telaLogin.fxml");
    }

    private void carregarCombos() {
        comboCategoria.setItems(FXCollections.observableArrayList(sistema.listarCategorias()));
        comboFornecedor.setItems(FXCollections.observableArrayList(sistema.listarFornecedores()));
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDescricao.clear();
        campoTaxa.clear();
        campoReposicao.clear();
        comboEstado.setValue(null);
        comboCategoria.setValue(null);
        comboFornecedor.setValue(null);
    }

    private void mostrarFormulario(boolean mostrar) {
        formulario.setVisible(mostrar);
        formulario.setManaged(mostrar);
    }

    private void aviso(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}