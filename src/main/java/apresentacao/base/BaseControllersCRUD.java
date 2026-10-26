package apresentacao.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public abstract class BaseControllersCRUD<T> {

    protected static final String TODOS = "TODOS";
    protected static final String ATIVOS = "ATIVOS";
    protected static final String INATIVOS = "INATIVOS";

    @FXML protected VBox formulario;
    @FXML protected Label tituloFormulario;
    @FXML protected Button btnSalvar;

    @FXML protected Label lblTotal;
    @FXML protected Label lblAtivos;
    @FXML protected Label lblInativos;
    @FXML protected Label lblResultado;
    @FXML protected TextField busca;
    @FXML protected Button btnTodos;
    @FXML protected Button btnAtivos;
    @FXML protected Button btnInativos;

    @FXML protected TableView<T> tabela;

    protected FilteredList<T> listaFiltrada;
    protected String filtroStatus = TODOS;

    protected abstract List<T> listar();
    protected abstract boolean estaAtivo(T registro);
    protected abstract boolean combinaComBusca(T registro, String texto);
    protected abstract String nomeDe(T registro);
    protected abstract void desativarRegistro(T registro);

    protected void iniciarBase() {
        busca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro());
        atualizar();
    }

    @FXML
    protected void atualizar() {
        listaFiltrada = new FilteredList<>(FXCollections.observableArrayList(listar()), r -> true);
        tabela.setItems(listaFiltrada);
        aplicarFiltro();
    }

    protected void aplicarFiltro() {
        String texto = busca.getText() == null ? "" : busca.getText().trim().toLowerCase();

        listaFiltrada.setPredicate(registro -> {
            boolean passaStatus = filtroStatus.equals(TODOS)
                    || (filtroStatus.equals(ATIVOS) && estaAtivo(registro))
                    || (filtroStatus.equals(INATIVOS) && !estaAtivo(registro));
            boolean passaBusca = texto.isEmpty() || combinaComBusca(registro, texto);
            return passaStatus && passaBusca;
        });

        int total = listaFiltrada.getSource().size();
        long ativos = listaFiltrada.getSource().stream().filter(this::estaAtivo).count();
        lblTotal.setText(String.valueOf(total));
        lblAtivos.setText(String.valueOf(ativos));
        lblInativos.setText(String.valueOf(total - ativos));
        lblResultado.setText("Exibindo " + listaFiltrada.size() + " de " + total);
    }

    @FXML protected void todos()    { trocarFiltro(TODOS, btnTodos); }
    @FXML protected void ativos()   { trocarFiltro(ATIVOS, btnAtivos); }
    @FXML protected void inativos() { trocarFiltro(INATIVOS, btnInativos); }

    private void trocarFiltro(String filtro, Button botao) {
        filtroStatus = filtro;
        for (Button b : new Button[] { btnTodos, btnAtivos, btnInativos }) {
            b.getStyleClass().remove("filter-active");
        }
        botao.getStyleClass().add("filter-active");
        aplicarFiltro();
    }

    @FXML
    protected void cancelar() {
        mostrarFormulario(false);
    }

    @FXML
    protected void desativar() {
        T selecionado = selecionado("Selecione um registro na tabela.");
        if (selecionado == null) {
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Desativar " + nomeDe(selecionado) + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText(null);

        if (confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            desativarRegistro(selecionado);
            atualizar();
        }
    }

    protected T selecionado(String mensagemSeVazio) {
        T registro = tabela.getSelectionModel().getSelectedItem();
        if (registro == null) {
            aviso(mensagemSeVazio);
        }
        return registro;
    }

    protected void abrirFormulario(String titulo, String textoBotao) {
        tituloFormulario.setText(titulo);
        btnSalvar.setText(textoBotao);
        mostrarFormulario(true);
    }

    protected void voltarParaLista() {
        mostrarFormulario(false);
        atualizar();
    }

    protected void mostrarFormulario(boolean mostrar) {
        formulario.setVisible(mostrar);
        formulario.setManaged(mostrar);
    }

    protected void aviso(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}