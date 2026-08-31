package main;
import java.util.Scanner;

import apresentacao.MenuLogin;
import business.cruds.GerenciamentoCategoria;
import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoMultas;
import business.cruds.GerenciamentoUsuarios;
import business.cruds.GerenciamentoFornecedor;
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

public class Main {

    public static void main(String[] args) {

        //REPOSITÓRIOS 

        UsuarioRepositorio    usuarioRepositorio    = new UsuarioRepositorio();
        CategoriaRepositorio  categoriaRepositorio  = new CategoriaRepositorio();
        FornecedorRepositorio fornecedorRepositorio = new FornecedorRepositorio();
        ItemRepositorio       itemRepositorio       = new ItemRepositorio(categoriaRepositorio, fornecedorRepositorio);
        ContratoRepositorio   contratoRepositorio   = new ContratoRepositorio(usuarioRepositorio, itemRepositorio);
        MultaRepositorio      multaRepositorio      = new MultaRepositorio();

        //CAMADA DE NEGÓCIO

        IGerenciamentoContratos  gerenciamentoContratos  = new GerenciamentoContratos(contratoRepositorio);
        IGerenciamentoUsuarios   gerenciamentoUsuarios   = new GerenciamentoUsuarios(usuarioRepositorio, gerenciamentoContratos);
        IGerenciamentoCategoria  gerenciamentoCategoria  = new GerenciamentoCategoria(categoriaRepositorio);
        IGerenciamentoFornecedor gerenciamentoFornecedor = new GerenciamentoFornecedor(fornecedorRepositorio);
        IGerenciamentoItens      gerenciamentoItens      = new GerenciamentoItens(itemRepositorio);
        IGerenciamentoMultas     gerenciamentoMultas     = new GerenciamentoMultas(multaRepositorio);

        IRelatorios geradorRelatorios = new GeradorRelatorios(gerenciamentoItens, gerenciamentoContratos, gerenciamentoUsuarios);

        //FACADE

        SistemaFacade sistema = new SistemaFacade(gerenciamentoUsuarios,gerenciamentoContratos,gerenciamentoItens,gerenciamentoCategoria,gerenciamentoFornecedor,gerenciamentoMultas,geradorRelatorios
        );

        if (sistema.buscarUsuarioPorEmail("jacksonmepassaporfavor@loja.com") == null) {

            Administrador adminPadrao = new Administrador(sistema.gerarProximoIdUsuario(),"Jackson-Raniel","jacksonmepassaporfavor@loja.com","000.000.000-00","obrigadojackson");

            sistema.cadastrarUsuario(adminPadrao);

            
            System.out.println("Usuário padrão criado para testes");
            System.out.println("Email : jacksonmepassaporfavor@loja.com");
            System.out.println("Senha : obrigadojackson");
        }


        Scanner scanner = new Scanner(System.in);

        new MenuLogin(sistema, scanner).exibir();

        scanner.close();
    }
}