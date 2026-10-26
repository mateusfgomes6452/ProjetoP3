package main;

import java.io.IOException;

import apresentacao.controllers.FornecedoresController;
import apresentacao.controllers.MultasController;
import apresentacao.controllers.RelatoriosController;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Abre a tela de Relatórios como administrador e liga a navegação entre Relatórios, Fornecedores e Multas
 * usando o mesmo contrato dos controllers: {@code configurar(sistema, usuario, navegacao, sair)}.
 * Enquanto não existe a janela principal com login, esta classe faz o papel de roteador.
 *
 * Execute com "mvn javafx:run" na pasta raiz do projeto (para as pastas "dados" e "relatorios" serem encontradas).
 */
public class MainRelatorios extends Application {

    private static final String EMAIL_ADMINISTRADOR = "jacksonmepassaporfavor@loja.com";

    private Stage palco;
    private SistemaFacade sistema;
    private Usuario usuario;

    @Override
    public void start(Stage palco) {
        this.palco = palco;
        this.sistema = Main.criarSistema();
        this.usuario = sistema.buscarUsuarioPorEmail(EMAIL_ADMINISTRADOR);

        palco.setTitle("Loja que Aluga de Tudo");
        abrir("relatorios");
        palco.show();
    }

    /** Callback de navegação entregue aos controllers: recebe o nome da tela ("relatorios", "fornecedores"...). */
    private void abrir(String tela) {
        String arquivo = switch (tela) {
            case "relatorios" -> "/fxml/RelatoriosAdministrador.fxml";
            case "fornecedores" -> "/fxml/FornecedoresAdministrador.fxml";
            case "multas" -> "/fxml/Multasadministrador.fxml";
            default -> null;
        };
        if (arquivo == null) {
            avisar("Esta tela ainda não está disponível.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(arquivo));
            Parent raiz = loader.load();
            configurar(loader.getController());
            if (palco.getScene() == null) {
                palco.setScene(new Scene(raiz));
            } else {
                palco.getScene().setRoot(raiz);
            }
        } catch (IOException e) {
            avisar("Não foi possível abrir a tela: " + e.getMessage());
        }
    }

    private void configurar(Object controller) {
        if (controller instanceof RelatoriosController relatorios) {
            relatorios.configurar(sistema, usuario, this::abrir, Platform::exit);
        } else if (controller instanceof FornecedoresController fornecedores) {
            fornecedores.configurar(sistema, usuario, this::abrir, Platform::exit);
        } else if (controller instanceof MultasController multas) {
            multas.configurar(sistema, usuario, this::abrir, Platform::exit);
        }
    }

    private void avisar(String texto) {
        Alert aviso = new Alert(Alert.AlertType.INFORMATION, texto);
        aviso.setHeaderText(null);
        aviso.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
