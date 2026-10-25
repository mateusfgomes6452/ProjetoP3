package apresentacao.controllers;

import java.util.List;

import entidades.Categoria;
import facade.SistemaFacade;
import facade.SistemaJavaFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

public class CategoriasController extends BaseControllersCRUD<Categoria> {

    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;

    @FXML private TableColumn<Categoria, String> colId;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, String> colStatus;

    private final SistemaFacade sistema = SistemaJavaFX.getSistema();
    private Categoria categoriaEmEdicao;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(l -> new SimpleStringProperty(String.valueOf(l.getValue().getId())));
        colNome.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getNome()));
        colDescricao.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().getDescricao()));
        colStatus.setCellValueFactory(l -> new SimpleStringProperty(l.getValue().isAtiva() ? "Ativo" : "Inativo"));
        iniciarBase();
    }

    @Override
    protected List<Categoria> listar() {
        return sistema.listarCategorias();
    }

    @Override
    protected boolean estaAtivo(Categoria c) {
        return c.isAtiva();
    }

    @Override
    protected boolean combinaComBusca(Categoria c, String texto) {
        return String.valueOf(c.getId()).equals(texto) || c.getNome().toLowerCase().contains(texto);
    }

    @Override
    protected String nomeDe(Categoria c) {
        return "a categoria " + c.getNome();
    }

    @Override
    protected void desativarRegistro(Categoria c) {
        sistema.desativarCategoria(c.getId());
    }

    @FXML
    private void novo() {
        categoriaEmEdicao = null;
        campoNome.clear();
        campoDescricao.clear();
        abrirFormulario("Cadastrar categoria", "Salvar categoria");
    }

    @FXML
    private void editar() {
        Categoria selecionada = selecionado("Selecione uma categoria na tabela.");
        if (selecionada == null) {
            return;
        }
        categoriaEmEdicao = selecionada;
        campoNome.setText(selecionada.getNome());
        campoDescricao.setText(selecionada.getDescricao());
        abrirFormulario("Editar categoria", "Salvar alterações");
    }

    @FXML
    private void salvar() {
    	String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText().trim();

        boolean ok;
        if (categoriaEmEdicao == null) {
            ok = sistema.cadastrarCategoria(
                    new Categoria(sistema.gerarProximoIdCategoria(), nome, descricao));
        } else {
            categoriaEmEdicao.setNome(nome);
            categoriaEmEdicao.setDescricao(descricao);
            ok = sistema.atualizarCategoria(categoriaEmEdicao);
        }

        if (!ok) {
            aviso("Não foi possível salvar a categoria.");
            return;
        }
        
        if (nome.isEmpty()) {
            aviso("Informe o nome da categoria.");
            return;
        }
        
        if (descricao.isEmpty()) {
            aviso("Informe a descrição da categoria.");
            return;
        }
        voltarParaLista();
    }

    @FXML
    private void abrirCategorias() {
        voltarParaLista();
    }
}