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

    @Override
    @FXML
    protected void abrirFornecedores() { atualizar(); }

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
}