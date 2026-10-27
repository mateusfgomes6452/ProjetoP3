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
