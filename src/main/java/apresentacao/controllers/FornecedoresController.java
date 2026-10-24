package apresentacao.controllers;

import entidades.Administrador;
import entidades.Fornecedor;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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
import java.util.regex.Pattern;

public final class FornecedoresController {
    private static final Logger LOG = Logger.getLogger(FornecedoresController.class.getName());
    private static final Pattern CNPJ = Pattern.compile("[A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-[A-Z0-9]{2}");
    private static final Pattern CELULAR = Pattern.compile("\\(\\d{2}\\) 9 \\d{4}-\\d{4}");
    private static final Pattern FIXO = Pattern.compile("\\(\\d{2}\\) \\d{4}-\\d{4}");

    private enum Filtro { TODOS, ATIVOS, INATIVOS }
    private SistemaFacade sistema;
    private Usuario usuario;
    private Consumer<String> navegacao;
    private Runnable sair;
    private Filtro filtro = Filtro.TODOS;
    private Integer idEmEdicao;
    private List<Fornecedor> fornecedores = List.of();

    @FXML private TableView<Fornecedor> tabela;
    @FXML private TableColumn<Fornecedor, Number> colId;
    @FXML private TableColumn<Fornecedor, String> colRazao, colCnpj, colEmail, colTelefone, colStatus;
    @FXML private TextField busca, razao, cnpj, email, telefone;
    @FXML private Label lblTotal, lblAtivos, lblInativos, lblResultado, tituloFormulario;
    @FXML private Label lblPerfil, lblNomeUsuario, lblAvatar;
    @FXML private Button btnEditar, btnDesativar, btnTodos, btnAtivos, btnInativos, btnSalvar;
    @FXML private VBox formulario;

    @FXML private void initialize() {
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colRazao.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getRazaoSocial()));
        colCnpj.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getCnpj()));
        colEmail.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getEmail()));
        colTelefone.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getTelefone()));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtivo() ? "Ativo" : "Inativo"));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPlaceholder(new Label("Nenhum fornecedor encontrado."));
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, ant, atual) -> ajustarAcoes());
        busca.textProperty().addListener((obs, ant, atual) -> filtrar());
        destacarFiltro();
        ajustarAcoes();
    }

    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        this.sistema = Objects.requireNonNull(sistema, "SistemaFacade é obrigatório");
        this.usuario = Objects.requireNonNull(usuario, "Usuário autenticado é obrigatório");
        if (!(usuario instanceof Administrador || usuario instanceof Funcionario) || !usuario.isAtivo()) {
            throw new SecurityException("Somente administrador ou funcionário ativo pode gerenciar fornecedores.");
        }
        this.navegacao = Objects.requireNonNull(navegacao, "Callback de navegação é obrigatório");
        this.sair = Objects.requireNonNull(sair, "Callback de logout é obrigatório");
        lblPerfil.setText(usuario.getTipo());
        lblNomeUsuario.setText(usuario.getNome());
        lblAvatar.setText(usuario.getNome().isBlank() ? "U" : usuario.getNome().substring(0, 1).toUpperCase(Locale.ROOT));
        atualizar();
    }

    private void exigirSessao() {
        if (sistema == null || usuario == null || !usuario.isAtivo()
                || !(usuario instanceof Administrador || usuario instanceof Funcionario)) {
            throw new SecurityException("Sessão ausente ou sem permissão para fornecedores.");
        }
    }

    @FXML private void atualizar() {
        executar("consultar fornecedores", () -> {
            exigirSessao();
            fornecedores = new ArrayList<>(sistema.listarFornecedores());
            lblTotal.setText(String.valueOf(fornecedores.size()));
            long ativos = fornecedores.stream().filter(Fornecedor::isAtivo).count();
            lblAtivos.setText(String.valueOf(ativos));
            lblInativos.setText(String.valueOf(fornecedores.size() - ativos));
            filtrar();
        });
    }

    private void filtrar() {
        String termo = busca.getText() == null ? "" : busca.getText().strip().toLowerCase(Locale.ROOT);
        List<Fornecedor> encontrados = fornecedores.stream()
                .filter(f -> filtro == Filtro.TODOS || (filtro == Filtro.ATIVOS) == f.isAtivo())
                .filter(f -> termo.isEmpty() || String.valueOf(f.getId()).equals(termo)
                        || texto(f.getRazaoSocial()).contains(termo) || texto(f.getCnpj()).contains(termo)
                        || texto(f.getEmail()).contains(termo) || texto(f.getTelefone()).contains(termo))
                .toList();
        tabela.setItems(FXCollections.observableArrayList(encontrados));
        lblResultado.setText("Exibindo " + encontrados.size() + " de " + fornecedores.size());
        ajustarAcoes();
    }

    private String texto(String s) { return s == null ? "" : s.toLowerCase(Locale.ROOT); }
    @FXML private void todos() { mudarFiltro(Filtro.TODOS); }
    @FXML private void ativos() { mudarFiltro(Filtro.ATIVOS); }
    @FXML private void inativos() { mudarFiltro(Filtro.INATIVOS); }
    private void mudarFiltro(Filtro novo) { filtro = novo; destacarFiltro(); filtrar(); }
    private void destacarFiltro() {
        destacar(btnTodos, filtro == Filtro.TODOS);
        destacar(btnAtivos, filtro == Filtro.ATIVOS);
        destacar(btnInativos, filtro == Filtro.INATIVOS);
    }
    private void destacar(Button botao, boolean ativo) {
        botao.getStyleClass().remove("filter-active");
        if (ativo) botao.getStyleClass().add("filter-active");
    }
    private void ajustarAcoes() {
        Fornecedor selecionado = tabela.getSelectionModel().getSelectedItem();
        btnEditar.setDisable(selecionado == null);
        btnDesativar.setDisable(selecionado == null || !selecionado.isAtivo());
    }

    @FXML private void novo() {
        executar("abrir cadastro", () -> {
            exigirSessao();
            idEmEdicao = null;
            tituloFormulario.setText("Cadastrar fornecedor");
            razao.clear(); cnpj.clear(); email.clear(); telefone.clear();
            cnpj.setDisable(false);
            btnSalvar.setText("Cadastrar fornecedor");
            exibirFormulario(true);
        });
    }
    @FXML private void editar() {
        executar("editar fornecedor", () -> {
            exigirSessao();
            Fornecedor f = selecionado();
            idEmEdicao = f.getId();
            tituloFormulario.setText("Editar fornecedor #" + f.getId());
            razao.setText(f.getRazaoSocial()); cnpj.setText(f.getCnpj());
            email.setText(f.getEmail()); telefone.setText(f.getTelefone());
            cnpj.setDisable(true); // como no MenuFornecedores, não é editável
            btnSalvar.setText("Salvar alterações");
            exibirFormulario(true);
        });
    }
    @FXML private void cancelar() { idEmEdicao = null; exibirFormulario(false); }
    private void exibirFormulario(boolean visivel) {
        formulario.setVisible(visivel); formulario.setManaged(visivel);
        if (visivel) razao.requestFocus();
    }

    @FXML private void salvar() {
        executar("salvar fornecedor", () -> {
            exigirSessao();
            String nome = validarTexto(razao.getText(), "Razão social");
            String mail = validarTexto(email.getText(), "E-mail");
            String phone = validarTexto(telefone.getText(), "Telefone");
            // ValidaEntrada.lerEmail exige @loja.com mesmo para fornecedores
            if (!mail.toLowerCase(Locale.ROOT).endsWith("@loja.com"))
                throw new IllegalArgumentException("O sistema atual exige e-mail @loja.com.");
            if (!(CELULAR.matcher(phone).matches() || FIXO.matcher(phone).matches()))
                throw new IllegalArgumentException("Telefone: use (81) 9 9999-9999 ou (81) 9999-9999.");

            final boolean sucesso;
            if (idEmEdicao == null) {
                String documento = validarTexto(cnpj.getText(), "CNPJ").toUpperCase(Locale.ROOT);
                if (!CNPJ.matcher(documento).matches())
                    throw new IllegalArgumentException("CNPJ: use XX.XXX.XXX/XXXX-XX.");
                Fornecedor novo = new Fornecedor(sistema.gerarProximoIdFornecedor(), nome, documento, mail, phone);
                sucesso = sistema.cadastrarFornecedor(novo);
            } else {
                Fornecedor atual = sistema.buscarFornecedor(idEmEdicao);
                if (atual == null) throw new IllegalArgumentException("Fornecedor não encontrado.");
                Fornecedor modificado = new Fornecedor(atual.getId(), nome, atual.getCnpj(), mail, phone);
                modificado.setAtivo(atual.isAtivo()); // não reativar fornecedor inativo ao editar
                sucesso = sistema.atualizarFornecedor(modificado);
            }
            if (!sucesso) throw new IllegalArgumentException("Operação não realizada. Verifique ID/CNPJ duplicado.");
            cancelar();
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor salvo com sucesso.");
        });
    }

    @FXML private void desativar() {
        executar("desativar fornecedor", () -> {
            exigirSessao();
            Fornecedor f = selecionado();
            if (!f.isAtivo()) throw new IllegalArgumentException("Fornecedor já está inativo.");
            if (!confirmar("Desativar fornecedor", "Confirma a desativação de " + f.getRazaoSocial() + "?")) return;
            if (!sistema.desativarFornecedor(f.getId()))
                throw new IllegalArgumentException("Não foi possível desativar o fornecedor.");
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Fornecedor desativado.");
        });
    }

    private Fornecedor selecionado() {
        Fornecedor f = tabela.getSelectionModel().getSelectedItem();
        if (f == null) throw new IllegalArgumentException("Selecione um fornecedor na tabela.");
        return f;
    }
    private String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(nome + " é obrigatório(a).");
        String limpo = valor.strip();
        if (limpo.contains(";") || limpo.contains("\n") || limpo.contains("\r"))
            throw new IllegalArgumentException(nome + " não pode conter ponto e vírgula ou quebras de linha (CSV).");
        return limpo;
    }
    @FXML private void abrirFornecedores() { atualizar(); }
    @FXML private void abrirMultas() { executar("navegar para multas", () -> { exigirSessao(); navegacao.accept("multas"); }); }
    @FXML private void encerrarSessao() { executar("encerrar sessão", () -> { exigirSessao(); sair.run(); }); }

    private void executar(String acao, Runnable operacao) {
        try { operacao.run(); }
        catch (IllegalArgumentException | SecurityException e) { alerta(Alert.AlertType.WARNING, "Atenção", e.getMessage()); }
        catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Falha ao " + acao, e);
            alerta(Alert.AlertType.ERROR, "Erro", "Não foi possível " + acao + ". Consulte o log.");
        }
    }
    private boolean confirmar(String titulo, String mensagem) {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        dialogo.setTitle(titulo); dialogo.setHeaderText(null);
        Optional<ButtonType> resposta = dialogo.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }
    private void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo, texto, ButtonType.OK);
        alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.showAndWait();
    }
}