package apresentacao.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/** Troca de tela: substitui o conteúdo da janela por outro arquivo FXML. */
public final class Navegacao {

    private Navegacao() {
    }

    /**
     * @param origem     qualquer componente da tela atual
     * @param caminhoFxml ex.: "/fxml/Multas.fxml"
     */
    public static void irPara(Node origem, String caminhoFxml) {
        try {
            Parent raiz = FXMLLoader.load(Navegacao.class.getResource(caminhoFxml));
            origem.getScene().setRoot(raiz);
        } catch (Exception e) {
            Alert aviso = new Alert(AlertType.INFORMATION, "Esta tela ainda não está disponível.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
        }
    }

    /** "Sair da conta": por enquanto fecha o aplicativo (ainda não existe tela de login em JavaFX). */
    public static void sair() {
        Platform.exit();
    }
}
