package apresentacao.base;

import apresentacao.navegacao.Navegacao;
import entidades.Administrador;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseControllers<T> implements Navegacao.ControladorComSessao {

    private static final Logger LOG = Logger.getLogger(BaseControllers.class.getName());

    protected enum Filtro { TODOS, ATIVOS, INATIVOS }

    protected SistemaFacade sistema;
    protected Usuario usuario;
    protected Integer idEmEdicao;
    private Consumer<String> navegacao;
    private Runnable sair;
    private Filtro filtro = Filtro.TODOS;
    private List<T> registros = List.of();

    @FXML protected TableView<T> tabela;
    @FXML protected TextField busca;
    @FXML protected Label lblTotal;
    @FXML protected Label lblAtivos; 
    @FXML protected Label lblInativos;
    @FXML protected Label lblResultado; 
    @FXML protected Label tituloFormulario;
    @FXML protected Label lblPerfil;
    @FXML protected Label lblNomeUsuario;
    @FXML protected Label lblAvatar;
    @FXML protected Button btnEditar;
    @FXML protected Button btnDesativar;
    @FXML protected Button btnTodos;
    @FXML protected Button btnAtivos; 
    @FXML protected Button btnInativos; 
    @FXML protected Button btnSalvar;
    @FXML protected VBox formulario;

    protected abstract List<T> listarRegistros();
    protected abstract boolean estaAtivo(T registro);
    protected abstract int idDe(T registro);
    protected abstract String nomeDe(T registro);
    protected abstract String descricaoDe(T registro);
    protected abstract boolean desativarRegistro(T registro);
    protected abstract String nomeEntidade();

    protected void iniciarBase() {
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum registro encontrado."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, ant, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, ant, atual) -> filtrar());
        destacarFiltro();
        ajustarAcoes();
    }

    @Override
    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario) || !usuario.isAtivo()) {
            throw new SecurityException("Somente administrador ou funcionário ativo pode acessar esta tela.");
        }
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de logout é obrigatório");
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank() ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    protected void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Sessão ausente ou sem permissão.");
        }
    }

    @FXML
    protected void atualizar() {
        executar("consultar " + nomeEntidade(), () -> {
            exigirSessao();
            registros = new ArrayList<>(listarRegistros());
            long ativos = registros.stream().filter(this::estaAtivo).count();
            lblTotal.setText(String.valueOf(registros.size()));
            lblAtivos.setText(String.valueOf(ativos));
            lblInativos.setText(String.valueOf(registros.size() - ativos));
            filtrar();
        });
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<T> encontrados = registros.stream()
                .filter(r -> filtro == Filtro.TODOS || (filtro == Filtro.ATIVOS) == estaAtivo(r))
                .filter(r -> termo.isEmpty() || String.valueOf(idDe(r)).equals(termo)
                        || texto(nomeDe(r)).contains(termo) || texto(descricaoDe(r)).contains(termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(encontrados));
        lblResultado.setText("Exibindo " + encontrados.size() + " de " + registros.size());
        ajustarAcoes();
    }

    private String texto(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    @FXML protected void todos()    { mudarFiltro(Filtro.TODOS); }
    @FXML protected void ativos()   { mudarFiltro(Filtro.ATIVOS); }
    @FXML protected void inativos() { mudarFiltro(Filtro.INATIVOS); }

    private void mudarFiltro(Filtro novo) {
        filtro = novo;
        destacarFiltro();
        filtrar();
    }

    private void destacarFiltro() {
        destacar(btnTodos, filtro == Filtro.TODOS);
        destacar(btnAtivos, filtro == Filtro.ATIVOS);
        destacar(btnInativos, filtro == Filtro.INATIVOS);
    }

    private void destacar(Button botao, boolean ativo) {
        if (botao == null) return;
        botao.getStyleClass().remove("filter-active");
        if (ativo) botao.getStyleClass().add("filter-active");
    }

    private void ajustarAcoes() {
        T sel = tabela.getSelectionModel().getSelectedItem();
        if (btnEditar != null) btnEditar.setDisable(sel == null);
        if (btnDesativar != null) btnDesativar.setDisable(sel == null || !estaAtivo(sel));
    }

    @FXML
    protected void cancelar() {
        idEmEdicao = null;
        exibirFormulario(false);
    }

    protected void exibirFormulario(boolean visivel) {
        if (formulario != null) {
            formulario.setVisible(visivel);
            formulario.setManaged(visivel);
        }
    }

    protected T selecionado() {
        T r = tabela.getSelectionModel().getSelectedItem();
        if (r == null) throw new IllegalArgumentException("Selecione um registro na tabela.");
        return r;
    }

    @FXML
    protected void desativar() {
        executar("desativar " + nomeEntidade(), () -> {
            exigirSessao();
            T r = selecionado();
            if (!estaAtivo(r)) throw new IllegalArgumentException("O registro já está inativo.");
            if (!confirmar("Desativar", "Confirma a desativação de " + nomeDe(r) + "?")) return;
            if (!desativarRegistro(r)) throw new IllegalArgumentException("Não foi possível desativar.");
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Registro desativado.");
        });
    }

    protected String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(nome + " é obrigatório(a).");
        String limpo = valor.strip();
        if (limpo.contains(";") || limpo.contains("\n") || limpo.contains("\r"))
            throw new IllegalArgumentException(nome + " não pode conter ponto e vírgula ou quebras de linha (CSV).");
        return limpo;
    }

    @FXML
    private void abrirFornecedores() {
        executar("navegar para fornecedores", () -> {
            exigirSessao();

            if (usuario instanceof Administrador) {
                navegacao.accept("FornecedoresAdministrador");
            } else {
                navegacao.accept("FornecedoresFunccionarios");
            }
        });
    }

    @FXML private void abrirItens() { 
        executar("navegar para itens", () -> { 
            exigirSessao(); 
            if (usuario instanceof entidades.Administrador) {
                navegacao.accept("ItensAdministrador"); 
            } else {
                navegacao.accept("ItensFuncionario"); 
            }
        }); 
    }

    @FXML
    private void abrirCategorias() {
        executar("navegar para categorias", () -> {
            exigirSessao();

            if (usuario instanceof Administrador) {
                navegacao.accept("CategoriasAdministrador");
            } else {
                navegacao.accept("CategoriasFuncionarios");
            }
        });
    }

    @FXML
    protected void abrirMultas() {
        executar("navegar para multas", () -> {
            exigirSessao();

            if (usuario instanceof Administrador) {
                navegacao.accept("Multasadministrador");
            } else {
                navegacao.accept("MultasFuncionario");
            }
        });
    }

    
    @FXML
    protected void abrirRelatorios() {
        executar("navegar para relatórios", () -> {
            exigirSessao();

            if (usuario instanceof Administrador) {
                navegacao.accept("RelatoriosAdministrador");
            } else {
                navegacao.accept("RelatoriosFuncionario");
            }
        });
    }

    
    @FXML protected void encerrarSessao() {
        executar("encerrar sessão", () -> { exigirSessao(); sair.run(); });
    }

    protected void executar(String acao, Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | SecurityException e) {
            alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, e, () -> "Falha ao " + acao);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte o log.");
        }
    }

    protected boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        Optional<ButtonType> resposta = dialogo.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }

    protected void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}