package main;

import java.io.IOException;

import business.cruds.GerenciamentoCategoria;
import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoFornecedor;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoMultas;
import business.cruds.GerenciamentoUsuarios;
import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoUsuarios;
import business.relatorios.GeradorRelatorios;
import entidades.Administrador;
import facade.SistemaFacade;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repositories.CategoriaRepositorio;
import repositories.ContratoRepositorio;
import repositories.FornecedorRepositorio;
import repositories.ItemRepositorio;
import repositories.MultaRepositorio;
import repositories.UsuarioRepositorio;

public class Main extends Application {

    private static final String EMAIL_PADRAO = "jacksonmepassaporfavor@loja.com";

    @Override
    public void start(Stage palco) throws IOException {
        criarSistema();

        Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        palco.setTitle("Loja que Aluga de Tudo");
        palco.setScene(new Scene(raiz));
        palco.show();
    }

    static SistemaFacade criarSistema() {
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        CategoriaRepositorio categorias = new CategoriaRepositorio();
        FornecedorRepositorio fornecedores = new FornecedorRepositorio();
        ItemRepositorio itens = new ItemRepositorio(categorias, fornecedores);
        ContratoRepositorio contratos = new ContratoRepositorio(usuarios, itens);
        MultaRepositorio multas = new MultaRepositorio();

        IGerenciamentoContratos gContratos = new GerenciamentoContratos(contratos);
        IGerenciamentoUsuarios gUsuarios = new GerenciamentoUsuarios(usuarios, gContratos);
        IGerenciamentoItens gItens = new GerenciamentoItens(itens);

        SistemaFacade sistema = new SistemaFacade(gUsuarios, gContratos, gItens,
                new GerenciamentoCategoria(categorias),
                new GerenciamentoFornecedor(fornecedores),
                new GerenciamentoMultas(multas),
                new GeradorRelatorios(gItens, gContratos, gUsuarios));
        SistemaFacade.registrar(sistema);

        if (sistema.buscarUsuarioPorEmail(EMAIL_PADRAO) == null) {
            sistema.cadastrarUsuario(new Administrador(sistema.gerarProximoIdUsuario(),
                    "Jackson-Raniel", EMAIL_PADRAO, "000.000.000-00", "obrigadojackson"));
        }
        return sistema;
    }

    public static void main(String[] args) {
        launch(args);
    }
}