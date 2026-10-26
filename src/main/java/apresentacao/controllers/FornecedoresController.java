package apresentacao.controllers;

import entidades.Fornecedor;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class FornecedoresController extends BaseControllers<Fornecedor> {

    private static final Pattern CNPJ = Pattern.compile("[A-Z0-9]{2}\\.[A-Z0-9]{3}\\.[A-Z0-9]{3}/[A-Z0-9]{4}-[A-Z0-9]{2}");
    private static final Pattern CELULAR = Pattern.compile("\\(\\d{2}\\) 9 \\d{4}-\\d{4}");
    private static final Pattern FIXO = Pattern.compile("\\(\\d{2}\\) \\d{4}-\\d{4}");

    @FXML private TableColumn<Fornecedor, Number> colId;
    @FXML private TableColumn<Fornecedor, String> colRazao, colCnpj, colEmail, colTelefone, colStatus;
    @FXML private TextField razao, cnpj, email, telefone;

    @FXML private TableColumn<Fornecedor, String> colRazao;
    @FXML private TableColumn<Fornecedor, String> colCnpj;
    @FXML private TableColumn<Fornecedor, String> colEmail;
    @FXML private TableColumn<Fornecedor, String> colTelefone;
    @FXML private TableColumn<Fornecedor, String> colStatus;
    @FXML private TextField busca;
    @FXML private TextField razao;
    @FXML private TextField cnpj;
    @FXML private TextField email;
    @FXML private TextField telefone;
    @FXML private Label lblTotal;
    @FXML private Label lblAtivos;
    @FXML private Label lblInativos;
    @FXML private Label lblResultado;
    @FXML private Label tituloFormulario;
    @FXML private Label lblPerfil;
    @FXML private Label lblNomeUsuario;
    @FXML private Label lblAvatar;
    @FXML private Button btnEditar;
    @FXML private Button btnDesativar;
    @FXML private Button btnTodos;
    @FXML private Button btnAtivos;
    @FXML private Button btnInativos;
    @FXML private Button btnSalvar;
    @FXML private VBox formulario;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colRazao.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getRazaoSocial()));
        colCnpj.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getCnpj()));
        colEmail.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getEmail()));
        colTelefone.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getTelefone()));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtivo() ? "Ativo" : "Inativo"));
        iniciarBase();
    }

    @Override protected List<Fornecedor> listarRegistros() { return sistema.listarFornecedores(); }
    @Override protected boolean estaAtivo(Fornecedor f) { return f.isAtivo(); }
    @Override protected int idDe(Fornecedor f) { return f.getId(); }
    @Override protected String nomeDe(Fornecedor f) { return f.getRazaoSocial(); }
    @Override protected String descricaoDe(Fornecedor f) { return f.getCnpj() + " " + f.getEmail() + " " + f.getTelefone(); }
    @Override protected boolean desativarRegistro(Fornecedor f) { return sistema.desativarFornecedor(f.getId()); }
    @Override protected String nomeEntidade() { return "fornecedor"; }

    @FXML
    private void novo() {
        executar("abrir cadastro", () -> {
            exigirSessao();
            idEmEdicao = null;
            tituloFormulario.setText("Cadastrar fornecedor");
            razao.clear(); cnpj.clear(); email.clear(); telefone.clear();
            cnpj.setDisable(false);
            btnSalvar.setText("Cadastrar fornecedor");
            exibirFormulario(true);
            razao.requestFocus();
        });
    }

    @FXML
    private void editar() {
        executar("editar fornecedor", () -> {
            exigirSessao();
            Fornecedor f = selecionado();
            idEmEdicao = f.getId();
            tituloFormulario.setText("Editar fornecedor #" + f.getId());
            razao.setText(f.getRazaoSocial()); cnpj.setText(f.getCnpj());
            email.setText(f.getEmail()); telefone.setText(f.getTelefone());
            cnpj.setDisable(true);
            btnSalvar.setText("Salvar alterações");
            exibirFormulario(true);
            razao.requestFocus();
        });
    }

    @FXML
    private void salvar() {
        executar("salvar fornecedor", () -> {
            exigirSessao();
            String nome = validarTexto(razao.getText(), "Razão social");
            String mail = validarTexto(email.getText(), "E-mail");
            String phone = validarTexto(telefone.getText(), "Telefone");
            if (!mail.toLowerCase(Locale.ROOT).endsWith("@loja.com"))
                throw new IllegalArgumentException("O sistema atual exige e-mail @loja.com.");
            if (!(CELULAR.matcher(phone).matches() || FIXO.matcher(phone).matches()))
                throw new IllegalArgumentException("Telefone: use (81) 9 9999-9999 ou (81) 9999-9999.");

            final boolean sucesso;
            if (idEmEdicao == null) {
                String documento = validarTexto(cnpj.getText(), "CNPJ").toUpperCase(Locale.ROOT);
                if (!CNPJ.matcher(documento).matches())
                    throw new IllegalArgumentException("CNPJ: use XX.XXX.XXX/XXXX-XX.");
                sucesso = sistema.cadastrarFornecedor(
                        new Fornecedor(sistema.gerarProximoIdFornecedor(), nome, documento, mail, phone));
            } else {
                Fornecedor atual = sistema.buscarFornecedor(idEmEdicao);
                if (atual == null) throw new IllegalArgumentException("Fornecedor não encontrado.");
                Fornecedor modificado = new Fornecedor(atual.getId(), nome, atual.getCnpj(), mail, phone);
                modificado.setAtivo(atual.isAtivo());
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
    @FXML private void abrirCategorias() { executar("navegar para categorias", () -> { exigirSessao(); navegacao.accept("categorias"); }); }
    @FXML private void abrirItens() { 
        executar("navegar para fornecedores", () -> { 
            exigirSessao(); 
            if (usuario instanceof entidades.Administrador) {
                navegacao.accept("ItensAdministrador"); 
            } else {
                navegacao.accept("ItensFuncionario"); 
            }
        }); 
    }
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
