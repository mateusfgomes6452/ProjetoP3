package apresentacao;

import entidades.Usuario;
import facade.SistemaFacade;
import facade.SistemaJavaFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;

public class UsuariosController {
	@FXML
	private TextField campoBusca;
	
	private FilteredList<Usuario> listaFiltrada;
	
    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaID;

    @FXML
    private TableColumn<Usuario, String> colunaNome;

    @FXML
    private TableColumn<Usuario, String> colunaEmail;

    @FXML
    private TableColumn<Usuario, String> colunaCPF;

    @FXML
    private TableColumn<Usuario, String> colunaPerfil;

    @FXML
    private TableColumn<Usuario, String> colunaAtivo;
    
    @FXML
    private TableColumn<Usuario, Void> colunaAcoes;

    private SistemaFacade sistema = SistemaJavaFX.getSistema();

    @FXML
    private void initialize() {
    	ObservableList<Usuario> todos =
    	        FXCollections.observableArrayList(sistema.listarUsuarios());

    	listaFiltrada = new FilteredList<>(todos, u -> true);
    	tabelaUsuarios.setItems(listaFiltrada);

    	campoBusca.textProperty().addListener((obs, antigo, novo) -> filtrar(novo));
    	
        colunaID.setCellValueFactory(
                linha -> new SimpleStringProperty(String.valueOf(linha.getValue().getId())));

        colunaNome.setCellValueFactory(
                linha -> new SimpleStringProperty(linha.getValue().getNome()));

        colunaEmail.setCellValueFactory(
                linha -> new SimpleStringProperty(linha.getValue().getEmail()));

        colunaCPF.setCellValueFactory(
                linha -> new SimpleStringProperty(linha.getValue().getCpf()));

        colunaPerfil.setCellValueFactory(
                linha -> new SimpleStringProperty(linha.getValue().getClass().getSimpleName()));

        colunaAtivo.setCellValueFactory(
                linha -> new SimpleStringProperty(linha.getValue().isAtivo() ? "Sim" : "Não"));
        
        colunaAcoes.setCellFactory(coluna -> new TableCell<>() {

            private final Button btnEditar = new Button("E");
            private final Button btnDesativar = new Button("D");
            private final HBox caixa = new HBox(8, btnEditar, btnDesativar);

            {
                btnEditar.setOnAction(e ->
                        editar(getTableView().getItems().get(getIndex())));

                btnDesativar.setOnAction(e ->
                        desativar(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caixa);
            }
        });
    }
    
    private void desativar(Usuario usuario) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Desativar o usuário " + usuario.getNome() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            sistema.desativaUsuario(usuario.getId());
            atualizarTabela();
        }
    }

    private void editar(Usuario usuario) {
        
    }

    private void atualizarTabela() {
        ObservableList<Usuario> todos =
                FXCollections.observableArrayList(sistema.listarUsuarios());

        listaFiltrada = new FilteredList<>(todos, u -> true);
        tabelaUsuarios.setItems(listaFiltrada);
        filtrar(campoBusca.getText());
    }
    
    private void filtrar(String texto) {
        String busca = texto == null ? "" : texto.trim().toLowerCase();

        listaFiltrada.setPredicate(usuario -> {
            if (busca.isEmpty()) {
                return true;
            }
            return String.valueOf(usuario.getId()).equals(busca)
                    || usuario.getNome().toLowerCase().contains(busca)
                    || usuario.getEmail().toLowerCase().contains(busca);
        });
    }

    @FXML
    private void buscar() {
        filtrar(campoBusca.getText());
    }
}