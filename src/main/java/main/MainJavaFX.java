package main;

import java.io.IOException;

import facade.SistemaFacade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJavaFX extends Application {

    @Override
    public void start(Stage palco) throws IOException {
        
    	    Main.criarSistema();
    	    
    	    Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
    	    palco.setTitle("Loja que Aluga de Tudo");
    	    palco.setScene(new Scene(raiz));
    	    palco.show();
    	}
    

    public static void main(String[] args) {
        launch(args);
    }
}
