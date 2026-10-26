package apresentacao.controllers;

import apresentacao.base.*;
import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;

public class ItensController extends BaseControllers<Item> {

    @FXML private TableColumn<Item, Number> colId;
    @FXML private TableColumn<Item, String> colNome;
    @FXML private TableColumn<Item, String> colDescricao;
    @FXML private TableColumn<Item, String> colTaxa;
    @FXML private TableColumn<Item, String> colEstado;
    @FXML private TableColumn<Item, String> colValorRep;
    @FXML private TableColumn<Item, String> colStatus;

    @FXML private TextField campoNome;
    @FXML private TextField campoTaxa;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoReposicao;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Fornecedor> comboFornecedor;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colNome.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getNome()));
        colDescricao.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getDescricao()));
        colTaxa.setCellValueFactory(x -> new ReadOnlyStringWrapper(String.format("R$ %.2f", x.getValue().getTaxaDiaria())));
        colEstado.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getEstadoConservacao()));
        colValorRep.setCellValueFactory(x -> new ReadOnlyStringWrapper(String.format("R$ %.2f", x.getValue().getValorReposicao())));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtivo() ? "Ativo" : "Inativo"));

        comboEstado.setItems(FXCollections.observableArrayList("Novo", "Bom", "Regular", "Ruim"));
        comboCategoria.setConverter(new StringConverter<>() {
            @Override public String toString(Categoria c) { return c == null ? "" : c.getNome(); }
            @Override public Categoria fromString(String s) { return null; }
        });
        comboFornecedor.setConverter(new StringConverter<>() {
            @Override public String toString(Fornecedor f) { return f == null ? "" : f.getRazaoSocial(); }
            @Override public Fornecedor fromString(String s) { return null; }
        });
        iniciarBase();
    }

    @Override protected List<Item> listarRegistros() { return sistema.listarItens(); }
    @Override protected boolean estaAtivo(Item i) { return i.isAtivo(); }
    @Override protected int idDe(Item i) { return i.getId(); }
    @Override protected String nomeDe(Item i) { return i.getNome(); }
    @Override protected String descricaoDe(Item i) { return i.getDescricao(); }
    @Override protected boolean desativarRegistro(Item i) { return sistema.excluirItem(i.getId()); }
    @Override protected String nomeEntidade() { return "item"; }

    @FXML
    private void novo() {
        executar("abrir cadastro de item", () -> {
            exigirSessao();
            idEmEdicao = null;
            tituloFormulario.setText("Cadastrar item");
            limparFormulario();
            carregarCombos();
            btnSalvar.setText("Cadastrar item");
            exibirFormulario(true);
        });
    }

    @FXML
    private void editar() {
        executar("editar item", () -> {
            exigirSessao();
            Item f = selecionado();
            idEmEdicao = f.getId();
            tituloFormulario.setText("Editar item #" + f.getId());
            carregarCombos();
            campoNome.setText(f.getNome());
            campoDescricao.setText(f.getDescricao());
            campoTaxa.setText(String.valueOf(f.getTaxaDiaria()));
            campoReposicao.setText(String.valueOf(f.getValorReposicao()));
            comboEstado.setValue(f.getEstadoConservacao());
            comboCategoria.setValue(f.getCategoria());
            comboFornecedor.setValue(f.getFornecedor());
            btnSalvar.setText("Salvar alterações");
            exibirFormulario(true);
        });
    }

    @FXML
    private void salvar() {
        executar("salvar item", () -> {
            exigirSessao();
            String nome = validarTexto(campoNome.getText(), "Nome");
            String descricao = validarTexto(campoDescricao.getText(), "Descrição");
            String estado = comboEstado.getValue();
            Categoria categoria = comboCategoria.getValue();
            Fornecedor fornecedor = comboFornecedor.getValue();

            if (estado == null || estado.isBlank())
                throw new IllegalArgumentException("Estado de conservação é obrigatório.");
            if (categoria == null) throw new IllegalArgumentException("Categoria é obrigatória.");
            if (fornecedor == null) throw new IllegalArgumentException("Fornecedor é obrigatório.");

            double taxa;
            double reposicao;
            try {
                taxa = Double.parseDouble(validarTexto(campoTaxa.getText(), "Taxa diária").replace(",", "."));
                reposicao = Double.parseDouble(validarTexto(campoReposicao.getText(), "Valor de reposição").replace(",", "."));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Taxa diária e valor de reposição devem ser números válidos.");
            }
            if (taxa <= 0 || reposicao <= 0)
                throw new IllegalArgumentException("Os valores de taxa e reposição devem ser maiores que zero.");

            final boolean sucesso;
            if (idEmEdicao == null) {
                sucesso = sistema.cadastrarItem(new Item(sistema.gerarProximoIdItem(), nome, descricao,
                        taxa, estado, reposicao, categoria, fornecedor));
            } else {
                Item atual = sistema.buscarItem(idEmEdicao);
                if (atual == null) throw new IllegalArgumentException("Item não encontrado.");
                atual.setNome(nome);
                atual.setDescricao(descricao);
                atual.setTaxaDiaria(taxa);
                atual.setEstadoConservacao(estado);
                atual.setValorReposicao(reposicao);
                atual.setCategoria(categoria);
                atual.setFornecedor(fornecedor);
                sucesso = sistema.atualizarItem(atual);
            }

            if (!sucesso) throw new IllegalArgumentException("Operação não realizada. Verifique dados duplicados.");
            cancelar();
            atualizar();
            alerta(Alert.AlertType.INFORMATION, "Sucesso", "Item salvo com sucesso.");
        });
    }

    private void carregarCombos() {
        comboCategoria.setItems(FXCollections.observableArrayList(sistema.listarCategorias()));
        comboFornecedor.setItems(FXCollections.observableArrayList(sistema.listarFornecedores()));
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDescricao.clear();
        campoTaxa.clear();
        campoReposicao.clear();
        comboEstado.setValue(null);
        comboCategoria.setValue(null);
        comboFornecedor.setValue(null);
    }
}