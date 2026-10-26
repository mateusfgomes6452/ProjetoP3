package apresentacao.controllers;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import facade.SistemaJavaFX; // Importante para puxar a fábrica do sistema
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginController {

    @FXML
    private BorderPane raiz;

    @FXML
    private TextField campoEmail;

    @FXML
    private PasswordField campoSenha;

    private SistemaFacade sistema = SistemaJavaFX.getSistema();

    @FXML
    private void realizarLogin(ActionEvent event) {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos vazios", "Por favor, preencha o email e a senha.");
            return;
        }

        Usuario usuario = sistema.realizarLogin(email, senha);

        if (usuario == null) {
            mostrarAlerta(AlertType.ERROR, "Falha no Login", "E-mail ou senha inválidos. Tente novamente.");
        } else {
            redirecionarUsuario(usuario);
        }
    }

    private void redirecionarUsuario(Usuario usuario) {
        try {
            String caminhoFxml = "";

            if (usuario instanceof Cliente) {
                caminhoFxml = "/fxml/MenuCliente.fxml"; 
            } else if (usuario instanceof Funcionario) {
                caminhoFxml = "/fxml/MenuFuncionario.fxml"; 
            } else if (usuario instanceof Administrador) {
                caminhoFxml = "/fxml/ItensAdministrador.fxml"; 
            }

            Parent novaTela = FXMLLoader.load(getClass().getResource(caminhoFxml));
            raiz.setCenter(novaTela);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela: " + e.getMessage());
        }
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}