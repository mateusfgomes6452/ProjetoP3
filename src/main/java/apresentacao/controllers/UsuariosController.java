package apresentacao.controllers;

import apresentacao.base.*;
import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class UsuariosController extends BaseControllers<Usuario> {

    private static final String ADMIN = "Administrador";
    private static final String FUNCIONARIO = "Funcionário";
    private static final String CLIENTE = "Cliente";
    private static final Pattern EMAIL_LOJA = Pattern.compile(
            "[A-Za-z0-9._%+-]+@loja\\.com", Pattern.CASE_INSENSITIVE);
    private static final Pattern FORMATO_CPF = Pattern.compile(
            "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

    @FXML private TableColumn<Usuario, Number> colId;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colCPF;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TableColumn<Usuario, String> colStatus;

    @FXML private TextField campoNome;
    @FXML private TextField campoCPF;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> comboTipo;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(x ->
                new ReadOnlyIntegerWrapper(x.getValue().getId()));

        colNome.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(x.getValue().getNome()));

        colCPF.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(
                        String.valueOf(x.getValue().getCpf())));

        colEmail.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(x.getValue().getEmail()));

        colTipo.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(x.getValue().getTipo()));

        colStatus.setCellValueFactory(x ->
                new ReadOnlyStringWrapper(
                        x.getValue().isAtivo() ? "Ativo" : "Inativo"));

        iniciarBase();
    }

    @Override
    public void configurar(
            SistemaFacade sistema,
            Usuario usuario,
            Consumer<String> navegacao,
            Runnable sair) {

        super.configurar(sistema, usuario, navegacao, sair);

        if (usuario instanceof Administrador) {
            comboTipo.setItems(
                    FXCollections.observableArrayList(
                            ADMIN, FUNCIONARIO, CLIENTE));
        } else {
            comboTipo.setItems(
                    FXCollections.observableArrayList(CLIENTE));
            comboTipo.setValue(CLIENTE);
            comboTipo.setDisable(true);
        }
    }

    @Override protected List<Usuario> listarRegistros() {
        return sistema.listarUsuarios().stream().filter(this::podeGerenciar).toList();
    }
    @Override protected boolean estaAtivo(Usuario u) { return u.isAtivo(); }
    @Override protected int idDe(Usuario u) { return u.getId(); }
    @Override protected String nomeDe(Usuario u) { return u.getNome(); }
    @Override protected String descricaoDe(Usuario u) { return u.getCpf() + " " + u.getEmail(); }
    @Override protected boolean desativarRegistro(Usuario u) { return sistema.desativaUsuario(u.getId()); }
    @Override protected String nomeEntidade() { return "usuario"; }

    @FXML
    private void novo() {
        iniciarNovo("usuário", this::limparFormulario);
    }

    @FXML
    private void editar() {
        iniciarEdicao("usuário", u -> {
            campoNome.setText(u.getNome());
            campoCPF.setText(String.valueOf(u.getCpf()));
            campoEmail.setText(u.getEmail());
            campoSenha.setText(u.getSenha());
            campoCPF.setDisable(true);
            comboTipo.setDisable(true);
            if (u instanceof Administrador) comboTipo.setValue(ADMIN);
            else if (u instanceof Funcionario) comboTipo.setValue(FUNCIONARIO);
            else comboTipo.setValue(CLIENTE);
        });
    }

    @FXML
    private void salvar() {
        executar("salvar usuario", () -> {
            exigirSessao();

            String nome = validarTexto(campoNome.getText(), "Nome");
            String cpf = validarTexto(campoCPF.getText(), "CPF");
            String email = validarEmailLoja(validarTexto(campoEmail.getText(), "Email"));
            String senha = validarTexto(campoSenha.getText(), "Senha");
            String tipo = comboTipo.getValue();

            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException(
                        "Selecione o tipo de usuário.");
            }

            boolean ok = idEmEdicao == null
                    ? cadastrarNovoUsuario(nome, cpf, email, senha, tipo)
                    : atualizarUsuarioExistente(nome, email, senha);

            concluirSalvamento(ok, "Usuário salvo com sucesso.");
        });
    }

    private boolean cadastrarNovoUsuario(
            String nome,
            String cpf,
            String email,
            String senha,
            String tipo) {

        validarCpf(cpf);

        Usuario novoUsuario;
        int novoId = sistema.gerarProximoIdUsuario();

        if (tipo.equals(ADMIN)) {
            novoUsuario = new Administrador(
                    novoId, nome, email, cpf, senha);

        } else if (tipo.equals(FUNCIONARIO)) {
            novoUsuario = new Funcionario(
                    novoId, nome, email, cpf, senha);

        } else {
            novoUsuario = new Cliente(
                    novoId, nome, email, cpf, senha);
        }

        return sistema.cadastrarUsuario(novoUsuario);
    }

    private boolean atualizarUsuarioExistente(
            String nome,
            String email,
            String senha) {

        Usuario atual = sistema.buscarUsuario(idEmEdicao);

        if (atual == null) {
            throw new IllegalArgumentException(
                    "Usuário não encontrado.");
        }

        if (!podeGerenciar(atual)) {
            throw new SecurityException(
                    "Funcionário só pode alterar clientes.");
        }

        Usuario donoDoEmail =
                sistema.buscarUsuarioPorEmail(email);

        if (donoDoEmail != null
                && donoDoEmail.getId() != atual.getId()) {
            throw new IllegalArgumentException(
                    "Este e-mail já é usado por outro usuário.");
        }

        return sistema.atualizarUsuario(
                idEmEdicao, nome, email, senha);
    }

    private String validarEmailLoja(String email) {
        if (!EMAIL_LOJA.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "E-mail inválido. Use um endereço @loja.com.");
        }
        return email;
    }

    private void validarCpf(String cpf) {
        if (!FORMATO_CPF.matcher(cpf).matches()) {
            throw new IllegalArgumentException(
                    "CPF inválido. Use o formato 000.000.000-00.");
        }

        String digitos = cpf.replace(".", "").replace("-", "");
        if (digitos.chars().distinct().count() == 1
                || !digitoVerificadorValido(digitos, 9)
                || !digitoVerificadorValido(digitos, 10)) {
            throw new IllegalArgumentException(
                    "CPF inválido. Confira os dígitos verificadores.");
        }
    }

    private boolean digitoVerificadorValido(String digitos, int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += (digitos.charAt(i) - '0') * (posicao + 1 - i);
        }
        int esperado = (soma * 10) % 11;
        if (esperado == 10) {
            esperado = 0;
        }
        return esperado == digitos.charAt(posicao) - '0';
    }

    private void limparFormulario() {
        campoNome.clear();
        campoCPF.clear();
        campoEmail.clear();
        campoSenha.clear();
        campoCPF.setDisable(false);
        if (usuario instanceof Administrador) {
            comboTipo.setValue(null);
            comboTipo.setDisable(false);
        }
    }

    private boolean podeGerenciar(Usuario alvo) {
        return usuario instanceof Administrador || alvo instanceof Cliente;
    }
}