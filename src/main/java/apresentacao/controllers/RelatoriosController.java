package apresentacao.controllers;

import apresentacao.navegacao.Navegacao;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import apresentacao.base.BaseControllersRelatorios;
import entidades.Administrador;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller das telas RelatoriosAdministrador.fxml e RelatoriosFuncionario.fxml:
 * menu lateral, título e as 4 abas. Segue o mesmo padrão de FornecedoresController e MultasController:
 * recebe tudo por {@link #configurar}, valida a sessão e navega pelos callbacks.
 *
 * Cada aba é um FXML próprio (fx:include) com o seu controller, configurado aqui:
 * ItensDisponiveisController, HistoricoClienteController, ItensAlugadosController e FaturamentoController.
 */
public final class RelatoriosController extends BaseControllersRelatorios implements Navegacao.ControladorComSessao {

    private Consumer<String> navegacao;
    private Runnable sair;
    private Runnable recarregarAbaAtiva = () -> { };

    @FXML private Label lblPerfil;
    @FXML private Label lblNomeUsuario;
    @FXML private Label lblAvatar;

    @FXML private Button btnAbaItens;
    @FXML private Button btnAbaHistorico;
    @FXML private Button btnAbaAlugados;
    @FXML private Button btnAbaFaturamento;

    // preenchidos automaticamente pelo fx:include (fx:id = abaItens -> abaItensController)
    @FXML private Node abaItens;
    @FXML private Node abaHistorico;
    @FXML private Node abaAlugados;
    @FXML private Node abaFaturamento;
    @FXML private ItensDisponiveisController abaItensController;
    @FXML private HistoricoClienteController abaHistoricoController;
    @FXML private ItensAlugadosController abaAlugadosController;
    @FXML private FaturamentoController abaFaturamentoController;

    @FXML
    private void initialize() {
        mostrarAba(btnAbaItens, abaItens, () -> abaItensController.atualizar());
    }

    @Override
    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        iniciarSessao(sistema, usuario);
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de saída é obrigatório");
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank() ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));

        abaItensController.configurar(sistema, usuario);
        abaHistoricoController.configurar(sistema, usuario);
        abaAlugadosController.configurar(sistema, usuario);
        abaFaturamentoController.configurar(sistema, usuario);
    }

    // ---------- abas ----------

    @FXML
    private void abrirAbaItens() {
        executar("abrir itens disponíveis", () -> {
            exigirSessao();
            abaItensController.atualizar();
            mostrarAba(btnAbaItens, abaItens, () -> abaItensController.atualizar());
        });
    }

    @FXML
    private void abrirAbaHistorico() {
        executar("abrir histórico do cliente", () -> {
            exigirSessao();
            abaHistoricoController.atualizar();
            mostrarAba(btnAbaHistorico, abaHistorico, () -> abaHistoricoController.atualizar());
        });
    }

    @FXML
    private void abrirAbaAlugados() {
        executar("abrir itens alugados", () -> {
            exigirSessao();
            abaAlugadosController.atualizar();
            mostrarAba(btnAbaAlugados, abaAlugados, () -> abaAlugadosController.atualizar());
        });
    }

    @FXML
    private void abrirAbaFaturamento() {
        executar("abrir faturamento", () -> {
            exigirSessao();
            abaFaturamentoController.atualizar();
            mostrarAba(btnAbaFaturamento, abaFaturamento, () -> abaFaturamentoController.atualizar());
        });
    }

    private void mostrarAba(Button botaoAtivo, Node abaAtiva, Runnable recarregar) {
        for (Button botao : List.of(btnAbaItens, btnAbaHistorico, btnAbaAlugados, btnAbaFaturamento)) {
            botao.getStyleClass().remove("filter-active");
        }
        botaoAtivo.getStyleClass().add("filter-active");

        for (Node aba : List.of(abaItens, abaHistorico, abaAlugados, abaFaturamento)) {
            boolean ativa = aba == abaAtiva;
            aba.setVisible(ativa);
            aba.setManaged(ativa);
        }
        recarregarAbaAtiva = recarregar;
    }

    // ---------- menu lateral ----------

    @FXML
    private void abrirRelatorios() {
        executar("atualizar relatórios", () -> {
            exigirSessao();
            recarregarAbaAtiva.run();
        });
    }

    @FXML
    private void abrirFornecedores() {
        executar("abrir fornecedores", () -> {
            exigirSessao();
            navegacao.accept(usuario instanceof Administrador ? "FornecedoresAdministrador" : "FornecedoresFunccionarios");
        });
    }

    @FXML
    private void abrirMultas() {
        executar("abrir multas", () -> {
            exigirSessao();
            navegacao.accept(usuario instanceof Administrador
                    ? "Multasadministrador"
                    : "MultasFuncionario");
        });
    }

    @FXML
    private void encerrarSessao() {
        executar("encerrar sessão", () -> {
            exigirSessao();
            sair.run();
        });
    }
}