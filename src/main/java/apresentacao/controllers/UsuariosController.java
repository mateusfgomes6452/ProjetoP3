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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.function.Consumer;

public class UsuariosController extends BaseControllers<Usuario> {
	private Consumer<String> navegacao;
	
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
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colNome.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getNome()));
        colCPF.setCellValueFactory(x -> new ReadOnlyStringWrapper(String.valueOf(x.getValue().getCpf())));
        colEmail.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getEmail()));
        colTipo.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getTipo()));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtivo() ? "Ativo" : "Inativo"));
        
        iniciarBase();
    }

    @Override
    public void configurar(SistemaFacade sistema, Usuario usuario, Consumer<String> navegacao, Runnable sair) {
        super.configurar(sistema, usuario, navegacao, sair);

        if (usuario instanceof Administrador) {
            comboTipo.setItems(FXCollections.observableArrayList("Administrador", "Funcionário", "Cliente"));
        } else {
            comboTipo.setItems(FXCollections.observableArrayList("Cliente"));
            comboTipo.setValue("Cliente");
            comboTipo.setDisable(true);
        }
    }
    
    @Override protected List<Usuario> listarRegistros() { return sistema.listarUsuarios(); }
    @Override protected boolean estaAtivo(Usuario u) { return u.isAtivo(); }
    @Override protected int idDe(Usuario u) { return u.getId(); }
    @Override protected String nomeDe(Usuario u) { return u.getNome(); }
    @Override protected String descricaoDe(Usuario u) { return u.getCpf() + " " + u.getEmail(); }
    @Override protected boolean desativarRegistro(Usuario u) { return sistema.desativaUsuario(u.getId()); }
    @Override protected String nomeEntidade() { return "usuario"; }

    @FXML
    private void novo() {
        executar("abrir cadastro de usuario", () -> {
            exigirSessao();
            idEmEdicao = null;
            tituloFormulario.setText("Cadastrar usuário");
            limparFormulario();
            btnSalvar.setText("Cadastrar usuário");
            exibirFormulario(true);
        });
    }

    @FXML
    private void editar() {
        executar("editar usuário", () -> {
            exigirSessao();
            Usuario u = selecionado();
            idEmEdicao = u.getId();
            tituloFormulario.setText("Editar usuário #" + u.getId());
            campoNome.setText(u.getNome());
            campoCPF.setText(String.valueOf(u.getCpf()));
            campoEmail.setText(u.getEmail());
            campoSenha.setText(u.getSenha());
            
            if (u instanceof Administrador) comboTipo.setValue("Administrador");
            else if (u instanceof Funcionario) comboTipo.setValue("Funcionário");
            else comboTipo.setValue("Cliente");

            btnSalvar.setText("Salvar alterações");
            exibirFormulario(true);
        });
    }

    @FXML
    private void salvar() {
        executar("salvar usuario", () -> {
            exigirSessao();
            String nome = validarTexto(campoNome.getText(), "Nome");
            String cpf = validarTexto(campoCPF.getText(), "CPF");
            String email = validarTexto(campoEmail.getText(), "Email");
            String senha = validarTexto(campoSenha.getText(), "Senha");
            String tipo = comboTipo.getValue();

            if (tipo == null || tipo.isBlank()) {
                throw new IllegalArgumentException("Selecione o tipo de usuário.");
            }

            boolean ok;
            if (idEmEdicao == null) {
                Usuario novoUsuario;
                int novoId = sistema.gerarProximoIdUsuario(); 
                
                if (tipo.equals("Administrador")) {
                    novoUsuario = new Administrador(novoId, nome, cpf, email, senha);
                } else if (tipo.equals("Funcionário")) {
                    novoUsuario = new Funcionario(novoId, nome, cpf, email, senha);
                } else {
                    novoUsuario = new Cliente(novoId, nome, cpf, email, senha);
                }
                ok = sistema.cadastrarUsuario(novoUsuario);
            } else {
                Usuario atual = sistema.buscarUsuario(idEmEdicao);
                if (atual == null) throw new IllegalArgumentException("Usuário não encontrado.");
                
                ok = sistema.atualizarUsuario(idEmEdicao, nome, email, senha);
            }

            if (!ok) throw new IllegalArgumentException("Não foi possível salvar o usuário. Verifique dados duplicados.");

            cancelar();
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Usuário salvo com sucesso.");
        });
    }

    private void limparFormulario() {
        campoNome.clear();
        campoCPF.clear();
        campoEmail.clear();
        campoSenha.clear();
        if (usuario instanceof Administrador) {
            comboTipo.setValue(null);
        }
    }
}