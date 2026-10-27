package apresentacao.base;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import entidades.Administrador;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import apresentacao.controllers.PreviaPdfController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseControllersRelatorios {

    private static final Logger LOG = Logger.getLogger(BaseControllersRelatorios.class.getName());
    protected SistemaFacade sistema;
    protected Usuario usuario;

    protected final void iniciarSessao(SistemaFacade sistema, Usuario usuario) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        exigirSessao();
    }

    protected final void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Sessão ausente ou sem permissão para relatórios.");
        }
    }
    
    protected final void mostrarPreviaSeAdministrador(String titulo, String nomeArquivo) {
        if (!(usuario instanceof Administrador)) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PreviaPdf.fxml"));
            Parent raiz = loader.load();
            PreviaPdfController previa = loader.getController();
            previa.mostrar(titulo, new File("relatorios", nomeArquivo));

            Stage janela = new Stage();
            janela.setTitle("Prévia - " + titulo);
            janela.initModality(Modality.APPLICATION_MODAL);   // trava a tela de trás até fechar
            janela.setScene(new Scene(raiz));
            janela.showAndWait();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível abrir a prévia do PDF.", e);
        }
    }

    protected final void executar(String acao, Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | SecurityException e) {
            alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte os logs.");
        }
    }

    protected final void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
