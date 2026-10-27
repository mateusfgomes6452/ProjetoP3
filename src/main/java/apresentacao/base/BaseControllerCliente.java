package apresentacao.base;

import apresentacao.navegacao.Navegacao;
import entidades.Cliente;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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

    // Componentes que as 3 telas do cliente têm (mesmo fx:id em todos os FXML)
    @FXML protected TextField busca;
    @FXML protected Label lblResultado;
    @FXML protected Button btnDetalhes;
    @FXML protected VBox painelDetalhes;

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

 
    protected final <T> void prepararTabela(TableView<T> tabela, FilteredList<T> visiveis,
                                            String textoTabelaVazia, Runnable filtrar) {
        tabela.setItems(visiveis);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label(textoTabelaVazia));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antes, atual) -> {
            btnDetalhes.setDisable(atual == null);
            fecharDetalhes();
        });
        busca.textProperty().addListener((obs, antes, atual) -> filtrar.run());
    }

    protected final String termoBusca() {
        return busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
    }

    protected final void destacarFiltro(Button selecionado, Button... todos) {
        for (Button botao : todos) {
            botao.getStyleClass().remove("filter-active");
        }
        selecionado.getStyleClass().add("filter-active");
    }

    protected final void mostrarDetalhes() {
        painelDetalhes.setVisible(true);
        painelDetalhes.setManaged(true);
    }

    @FXML
    protected final void fecharDetalhes() {
        painelDetalhes.setVisible(false);
        painelDetalhes.setManaged(false);
    }

    protected final ReadOnlyStringWrapper prop(String valor) {
        return new ReadOnlyStringWrapper(valor == null ? "" : valor);
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
