package apresentacao.controllers;

import apresentacao.Navegador;
import entidades.Categoria;
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

public class CategoriasController {

    @FXML private BorderPane raiz;
    @FXML private Label lblPerfil;
    @FXML private Label lblAvatar;
    @FXML private Label lblNomeUsuario;

    @FXML private VBox formulario;
    @FXML private Label tituloFormulario;
    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;
    @FXML private Button btnSalvar;

    @FXML private Label lblTotal;
    @FXML private Label lblAtivos;
    @FXML private Label lblInativos;
    @FXML private Label lblResultado;
    @FXML private TextField busca;
    @FXML private Button btnTodos;
    @FXML private Button btnAtivos;
    @FXML private Button btnInativos;

    @FXML private TableView<Categoria> tabela;
    @FXML private TableColumn<Categoria, String> colId;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, String> colStatus;

    private final SistemaFacade sistema = SistemaJavaFX.getSistema();

    private FilteredList<Categoria> listaFiltrada;
    private Categoria categoriaEmEdicao;
    private String filtroStatus = "TODOS";

    @FXML
    private void initialize() {
        colId.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getId())));
        colNome.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getNome()));
        colDescricao.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getDescricao()));
        colStatus.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().isAtiva() ? "Ativo" : "Inativo"));
        
        busca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        atualizar();
    }
    
    @FXML
    private void atualizar() {
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(sistema.listarCategorias()), i -> true);
        tabela.setItems(listaFiltrada);
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        String texto = busca.getText() == null ? "" : busca.getText().trim().toLowerCase();

        listaFiltrada.setPredicate(categoria -> {
            boolean passaStatus = filtroStatus.equals("TODOS")
                    || (filtroStatus.equals("ATIVOS") && categoria.isAtiva())
                    || (filtroStatus.equals("INATIVOS") && !categoria.isAtiva());

            boolean passaBusca = texto.isEmpty()
                    || String.valueOf(categoria.getId()).equals(texto)
                    || categoria.getNome().toLowerCase().contains(texto);

            return passaStatus && passaBusca;
        });

        int total = listaFiltrada.getSource().size();
        long ativos = listaFiltrada.getSource().stream().filter(Categoria::isAtiva).count();
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
        categoriaEmEdicao = null;
        tituloFormulario.setText("Cadastrar categoria");
        btnSalvar.setText("Salvar categoria");
        limparFormulario();
        mostrarFormulario(true);
    }

    @FXML
    private void editar() {
        Categoria selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            aviso("Selecione uma categoria na tabela.");
            return;
        }
        categoriaEmEdicao = selecionado;
        tituloFormulario.setText("Editar categoria");
        btnSalvar.setText("Salvar alterações");

        campoNome.setText(selecionado.getNome());
        campoDescricao.setText(selecionado.getDescricao());
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

        boolean ok;
        if (categoriaEmEdicao == null) {
            Categoria novaCategoria = new Categoria(sistema.gerarProximoIdCategoria(), nome, descricao);
            ok = sistema.cadastrarCategoria(novaCategoria);
        } else {
            categoriaEmEdicao.setNome(nome);
            categoriaEmEdicao.setDescricao(descricao);
            ok = sistema.atualizarCategoria(categoriaEmEdicao);
        }

        if (!ok) {
            aviso("Não foi possível salvar a categoria.");
            return;
        }
        mostrarFormulario(false);
        atualizar();
    }

    @FXML
    private void desativar() {
        Categoria selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            aviso("Selecione uma categoria na tabela.");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Desativar a categoria " + selecionado.getNome() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            sistema.excluirItem(selecionado.getId());
            atualizar();
        }
    }

    @FXML
    private void abrirCategorias() {
        mostrarFormulario(false);
        atualizar();
    }

    @FXML
    private void encerrarSessao() {
        Navegador.trocarTela(raiz, "/telaLogin.fxml");
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDescricao.clear();
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