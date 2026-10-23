package apresentacao;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Navegador {

    private Navegador() {
    }

    public static void trocarTela(Node peca, String arquivoFxml) {
        try {
            Parent root = FXMLLoader.load(Navegador.class.getResource(arquivoFxml));
            Stage stage = (Stage) peca.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}