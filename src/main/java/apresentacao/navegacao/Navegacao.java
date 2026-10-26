package apresentacao.navegacao;

import entidades.Usuario;
import facade.SistemaFacade;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import apresentacao.base.BaseControllers;

public final class Navegacao {
	
	private static final Logger LOG = Logger.getLogger(BaseControllers.class.getName());
    private static Usuario usuarioLogado;

    public interface ControladorComSessao {
        void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair);
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static void irPara(Node origem, String tela) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegacao.class.getResource(tela));
            Parent raiz = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ControladorComSessao c) {
                c.configurar(
                    SistemaFacade.getSistema(), 
                    usuarioLogado, 
                    destino -> irPara(origem, "/fxml/" + destino + ".fxml"),
                    () -> irPara(origem, "/fxml/Login.fxml")
                );
            }

            Scene cena = null;
            if (origem != null && origem.getScene() != null) {
                cena = origem.getScene();
            } else {
                Window janelaAtiva = Window.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
                if (janelaAtiva != null) {
                    cena = janelaAtiva.getScene();
                }
            }

            if (cena != null) {
                cena.setRoot(raiz);
            } else {
                throw new IllegalStateException("Não foi possível encontrar a cena ativa para realizar a navegação.");
            }

        } catch (Exception e) {
        	LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            Alert aviso = new Alert(AlertType.INFORMATION, "Esta tela ainda não está disponível.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
        }
    }

    public static void sair() {
        Platform.exit();
    }
}
