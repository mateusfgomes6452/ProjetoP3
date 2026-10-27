package apresentacao.base;

import apresentacao.navegacao.Navegacao;
import entidades.Cliente;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Padrão de sessão, alertas e navegação das telas exclusivas de clientes.
 * Não herda de BaseControllers ou BaseControllersRelatorios: ambas restringem
 * o acesso a administrador/funcionário.
 */
public abstract class BaseControllerCliente implements Navegacao.ControladorComSessao {

    private static final Logger LOG = Logger.getLogger(BaseControllerCliente.class.getName());

    protected SistemaFacade sistema;
    protected Cliente cliente;

    private Consumer<String> navegacao;
    private Runnable sair;

    @FXML protected Label lblPerfil;
    @FXML protected Label lblNomeUsuario;
    @FXML protected Label lblAvatar;

    @Override
    public final void configurar(SistemaFacade sistema, Usuario usuario,
                                 Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatória");
        if (!(usuario instanceof Cliente clienteAtivo) || !usuario.isAtivo()) {
            throw new SecurityException("Somente clientes ativos podem acessar esta tela.");
        }
        this.cliente = clienteAtivo;
        this.navegacao = Objects.requireNonNull(navegacao, "Navegação é obrigatória");
        this.sair = Objects.requireNonNull(sair, "Saída é obrigatória");

        lblPerfil.setText(cliente.getTipo());
        lblNomeUsuario.setText(cliente.getNome());
        lblAvatar.setText(cliente.getNome().isBlank()
                ? "U" : cliente.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    protected final void exigirSessao() {
        if (sistema == null || cliente == null || !cliente.isAtivo()) {
            throw new SecurityException("Sessão de cliente inválida ou encerrada.");
        }
    }

    /** Implementada por cada tela para consultar novamente o repositório via fachada. */
    protected abstract void atualizar();

    @FXML
    protected final void abrirItensDisponiveis() {
        navegar("ItensClientes");
    }

    @FXML
    protected final void abrirMeusAlugueis() {
        navegar("MeusAlugueisClientes");
    }

    @FXML
    protected final void abrirHistorico() {
        navegar("HistoricoClientes");
    }

    @FXML
    protected final void abrirMultas() {
        navegar("MultasClientes");
    }

    @FXML
    protected final void encerrarSessao() {
        executar("encerrar sessão", () -> {
            exigirSessao();
            sair.run();
        });
    }

    private void navegar(String fxmlSemExtensao) {
        executar("abrir tela " + fxmlSemExtensao, () -> {
            exigirSessao();
            navegacao.accept(fxmlSemExtensao);
        });
    }

    protected final void executar(String acao, Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | SecurityException e) {
            alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte o log.");
        }
    }

    protected final void alerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert aviso = new Alert(tipo, mensagem, ButtonType.OK);
        aviso.setTitle(titulo);
        aviso.setHeaderText(null);
        aviso.showAndWait();
    }
}
