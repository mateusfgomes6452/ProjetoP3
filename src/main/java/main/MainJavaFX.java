package main;

import java.io.IOException;

import business.cruds.GerenciamentoCategoria;
import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoFornecedor;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoMultas;
import business.cruds.GerenciamentoUsuarios;
import business.interfaces.IGerenciamentoCategoria;
import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoFornecedor;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoMultas;
import business.interfaces.IGerenciamentoUsuarios;
import business.relatorios.GeradorRelatorios;
import business.relatorios.IRelatorios;
import entidades.Administrador;
import facade.SistemaFacade;
import repositories.CategoriaRepositorio;
import repositories.ContratoRepositorio;
import repositories.FornecedorRepositorio;
import repositories.ItemRepositorio;
import repositories.MultaRepositorio;
import repositories.UsuarioRepositorio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainJavaFX extends Application {

    @Override
    public void start(Stage palco) throws IOException {
        UsuarioRepositorio    usuarioRepositorio    = new UsuarioRepositorio();
        CategoriaRepositorio  categoriaRepositorio  = new CategoriaRepositorio();
        FornecedorRepositorio fornecedorRepositorio = new FornecedorRepositorio();
        ItemRepositorio       itemRepositorio       = new ItemRepositorio(categoriaRepositorio, fornecedorRepositorio);
        ContratoRepositorio   contratoRepositorio   = new ContratoRepositorio(usuarioRepositorio, itemRepositorio);
        MultaRepositorio      multaRepositorio      = new MultaRepositorio();

        IGerenciamentoContratos  gerenciamentoContratos  = new GerenciamentoContratos(contratoRepositorio);
        IGerenciamentoUsuarios   gerenciamentoUsuarios   = new GerenciamentoUsuarios(usuarioRepositorio, gerenciamentoContratos);
        IGerenciamentoCategoria  gerenciamentoCategoria  = new GerenciamentoCategoria(categoriaRepositorio);
        IGerenciamentoFornecedor gerenciamentoFornecedor = new GerenciamentoFornecedor(fornecedorRepositorio);
        IGerenciamentoItens      gerenciamentoItens      = new GerenciamentoItens(itemRepositorio);
        IGerenciamentoMultas     gerenciamentoMultas     = new GerenciamentoMultas(multaRepositorio);

        IRelatorios geradorRelatorios = new GeradorRelatorios(gerenciamentoItens, gerenciamentoContratos, gerenciamentoUsuarios);

        SistemaFacade sistema = new SistemaFacade(
            gerenciamentoUsuarios, 
            gerenciamentoContratos, 
            gerenciamentoItens, 
            gerenciamentoCategoria, 
            gerenciamentoFornecedor, 
            gerenciamentoMultas, 
            geradorRelatorios
        );

        if (sistema.buscarUsuarioPorEmail("jacksonmepassaporfavor@loja.com") == null) {
            Administrador adminPadrao = new Administrador(
                sistema.gerarProximoIdUsuario(),
                "Jackson-Raniel",
                "jacksonmepassaporfavor@loja.com",
                "000.000.000-00",
                "obrigadojackson"
            );
            sistema.cadastrarUsuario(adminPadrao);
        }

        Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        palco.setTitle("Loja que Aluga de Tudo");
        palco.setScene(new Scene(raiz));
        palco.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}