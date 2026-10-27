package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LoginController {

    @FXML
    private BorderPane raiz;

    @FXML
    private TextField login;

    @FXML
    private PasswordField senha;

    private final SistemaFacade sistema = SistemaFacade.getSistema();

    @FXML
    private void entrar(ActionEvent event) {
        String cLogin = login.getText().trim();
        String cSenha = senha.getText();

        if (cLogin.isEmpty() || cSenha.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos vazios", "Por favor, preencha o email e a senha.");
            return;
        }

        Usuario usuario = sistema.realizarLogin(cLogin, cSenha);

        if (usuario == null) {
            mostrarAlerta(AlertType.ERROR, "Falha no Login", "E-mail ou senha inválidos. Tente novamente.");
        } else {
            redirecionarUsuario(usuario);
        }
    }

    private void redirecionarUsuario(Usuario usuario) {
        String arquivo = "";

        if (usuario instanceof Cliente) {
            arquivo = "/fxml/MultasClientes.fxml";
        } else if (usuario instanceof Funcionario) {
            arquivo = "/fxml/UsuariosFuncionario.fxml"; 
        } else if (usuario instanceof Administrador) {
            arquivo = "/fxml/UsuariosAdministrador.fxml"; 
        }

        Navegacao.setUsuarioLogado(usuario);
        Navegacao.irPara(raiz, arquivo);
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}