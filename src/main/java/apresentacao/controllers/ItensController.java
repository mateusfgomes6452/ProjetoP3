package apresentacao.controllers;

import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;
import facade.SistemaFacade;
import facade.SistemaJavaFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ItensController extends BaseControllersCRUD<Item> {

    @FXML private TextField campoNome;
    @FXML private TextField campoTaxa;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoReposicao;
    @FXML private ComboBox<String> comboEstado;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private ComboBox<Fornecedor> comboFornecedor;

    @FXML private TableColumn<Item, String> colId;
    @FXML private TableColumn<Item, String> colNome;
    @FXML private TableColumn<Item, String> colDescricao;
    @FXML private TableColumn<Item, String> colTaxa;
    @FXML private TableColumn<Item, String> colEstado;
    @FXML private TableColumn<Item, String> colValorRep;
    @FXML private TableColumn<Item, String> colStatus;

    private final SistemaFacade sistema = SistemaJavaFX.getSistema();
    private Item itemEmEdicao;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getId())));
        colNome.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getNome()));
        colDescricao.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getDescricao()));
        colTaxa.setCellValueFactory(l -> new SimpleStringProperty(String.format("R$ %.2f", l.getValue().getTaxaDiaria())));
        colEstado.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getEstadoConservacao()));
        colValorRep.setCellValueFactory(l -> new SimpleStringProperty(String.format("R$ %.2f", l.getValue().getValorReposicao())));
        colStatus.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().isAtivo() ? "Ativo" : "Inativo"));

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

    @Override
    protected List<Item> listar() {
        return sistema.listarItens();
    }

    @Override
    protected boolean estaAtivo(Item i) {
        return i.isAtivo();
    }

    @Override
    protected boolean combinaComBusca(Item i, String texto) {
        return String.valueOf(i.getId()).equals(texto) || i.getNome().toLowerCase().contains(texto);
    }

    @Override
    protected String nomeDe(Item i) {
        return "o item " + i.getNome();
    }

    @Override
    protected void desativarRegistro(Item i) {
        sistema.excluirItem(i.getId());
    }

    @FXML
    private void novo() {
        itemEmEdicao = null;
        limparFormulario();
        carregarCombos();
        abrirFormulario("Cadastrar item", "Salvar item");
    }

    @FXML
    private void editar() {
        Item selecionado = selecionado("Selecione um item na tabela.");
        if (selecionado == null) {
            return;
        }
        itemEmEdicao = selecionado;
        carregarCombos();

        campoNome.setText(selecionado.getNome());
        campoDescricao.setText(selecionado.getDescricao());
        campoTaxa.setText(String.valueOf(selecionado.getTaxaDiaria()));
        campoReposicao.setText(String.valueOf(selecionado.getValorReposicao()));
        comboEstado.setValue(selecionado.getEstadoConservacao());
        comboCategoria.setValue(selecionado.getCategoria());
        comboFornecedor.setValue(selecionado.getFornecedor());
        abrirFormulario("Editar item", "Salvar alterações");
    }

    @FXML
    private void salvar() {
        String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText().trim();
        String estado = comboEstado.getValue();
        Categoria categoria = comboCategoria.getValue();
        Fornecedor fornecedor = comboFornecedor.getValue();

        if (nome.isEmpty() || descricao.isEmpty() || estado == null
                || categoria == null || fornecedor == null) {
            aviso("Preencha todos os campos.");
            return;
        }

        double taxa;
        double reposicao;
        try {
            taxa = Double.parseDouble(campoTaxa.getText().trim().replace(",", "."));
            reposicao = Double.parseDouble(campoReposicao.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            aviso("Taxa diária e valor de reposição devem ser números.");
            return;
        }
        if (taxa <= 0 || reposicao <= 0) {
            aviso("Os valores devem ser maiores que zero.");
            return;
        }

        boolean ok;
        if (itemEmEdicao == null) {
            ok = sistema.cadastrarItem(new Item(sistema.gerarProximoIdItem(), nome, descricao,
                    taxa, estado, reposicao, categoria, fornecedor));
        } else {
            itemEmEdicao.setNome(nome);
            itemEmEdicao.setDescricao(descricao);
            itemEmEdicao.setTaxaDiaria(taxa);
            itemEmEdicao.setEstadoConservacao(estado);
            itemEmEdicao.setValorReposicao(reposicao);
            itemEmEdicao.setCategoria(categoria);
            itemEmEdicao.setFornecedor(fornecedor);
            ok = sistema.atualizarItem(itemEmEdicao);
        }

        if (!ok) {
            aviso("Não foi possível salvar o item.");
            return;
        }
        voltarParaLista();
    }

    @FXML
    private void abrirItens() {
        voltarParaLista();
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