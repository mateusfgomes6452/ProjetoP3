package main;

import java.util.Scanner;

import apresentacao.MenuLogin;
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
import repositories.CategoriaRepositorio;
import repositories.ContratoRepositorio;
import repositories.FornecedorRepositorio;
import repositories.ItemRepositorio;
import repositories.MultaRepositorio;
import repositories.UsuarioRepositorio;

public class Main {

    private static final String EMAIL_PADRAO = "jacksonmepassaporfavor@loja.com";

    public static SistemaFacade criarSistema() {
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
        try (Scanner scanner = new Scanner(System.in)) {
            new MenuLogin(criarSistema(), scanner).exibir();
        }
    }
}