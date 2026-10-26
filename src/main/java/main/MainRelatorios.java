package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Abre a tela de Relatórios do administrador.
 * Execute com "mvn javafx:run" na pasta raiz do projeto (para as pastas "dados" e "relatorios" serem encontradas).
 */
public class MainRelatorios extends Application {

    @Override
    public void start(Stage palco) throws IOException {
        Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/Relatorios.fxml"));
        palco.setTitle("Loja que Aluga de Tudo — Relatórios");
        palco.setScene(new Scene(raiz));
        palco.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
