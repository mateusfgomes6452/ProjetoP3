package apresentacao.controllers;

import apresentacao.base.*;
import entidades.Categoria;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

import java.util.List;

public class CategoriasController extends BaseControllers<Categoria> {

    @FXML private TableColumn<Categoria, Number> colId;
    @FXML private TableColumn<Categoria, String> colNome;
    @FXML private TableColumn<Categoria, String> colDescricao;
    @FXML private TableColumn<Categoria, String> colStatus;

    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(x -> new ReadOnlyIntegerWrapper(x.getValue().getId()));
        colNome.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getNome()));
        colDescricao.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getDescricao()));
        colStatus.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().isAtiva() ? "Ativo" : "Inativo"));

        iniciarBase();
    }

    @Override protected List<Categoria> listarRegistros() { return sistema.listarCategorias(); }
    @Override protected boolean estaAtivo(Categoria c) { return c.isAtiva(); }
    @Override protected int idDe(Categoria c) { return c.getId(); }
    @Override protected String nomeDe(Categoria c) { return c.getNome(); }
    @Override protected String descricaoDe(Categoria c) { return c.getDescricao(); }
    @Override protected boolean desativarRegistro(Categoria c) {
        return sistema.desativarCategoria(c.getId());
    }
    @Override protected String nomeEntidade() { return "categoria"; }

    @FXML
    private void novo() {
        iniciarNovo("categoria", this::limparFormulario);
    }

    @FXML
    private void editar() {
        iniciarEdicao("categoria", c -> {
            campoNome.setText(c.getNome());
            campoDescricao.setText(c.getDescricao());
        });
    }

    @FXML
    private void salvar() {
        executar("salvar categoria", () -> {
            exigirSessao();
            String nome = validarTexto(campoNome.getText(), "Nome");
            String descricao = validarTexto(campoDescricao.getText(), "Descrição");

            boolean ok;
            if (idEmEdicao == null) {
                ok = sistema.cadastrarCategoria(new Categoria(sistema.gerarProximoIdCategoria(), nome, descricao));
            } else {
                Categoria categoria = sistema.buscarCategoria(idEmEdicao);
                if (categoria == null) throw new IllegalArgumentException("Categoria não encontrada.");
                categoria.setNome(nome);
                categoria.setDescricao(descricao);
                ok = sistema.atualizarCategoria(categoria);
            }
            concluirSalvamento(ok, "Categoria salva com sucesso.");
        });
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDescricao.clear();
    }

}