package apresentacao;

import java.io.IOException;

import entidades.Administrador;
import entidades.Usuario;
import facade.SistemaFacade;
import facade.SistemaJavaFX;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField login;

    @FXML
    private PasswordField senha;

    private SistemaFacade sistema = SistemaJavaFX.getSistema();

    @FXML
    private void entrar() {

        String loginUser = login.getText().trim();
        String senhaUser = senha.getText().trim();

        Usuario usuario = sistema.realizarLogin(loginUser, senhaUser);

        if (usuario == null) {
            mostrarAviso("Email ou senha inválidos. Tente novamente");
            return;
        }
        
        if (usuario instanceof Administrador) {
        	trocarTela("/telaInicialAdmin.fxml");
        }
    }

    private void mostrarAviso(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

	private void trocarTela(String arquivoFxml) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivoFxml));
	        Parent root = loader.load();
	
	        Stage stage = (Stage) login.getScene().getWindow();
	        stage.setScene(new Scene(root));
	        stage.show();
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}	