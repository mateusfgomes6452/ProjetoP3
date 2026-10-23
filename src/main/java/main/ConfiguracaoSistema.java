package main;

import business.cruds.GerenciamentoCategoria;
import business.cruds.GerenciamentoContratos;
import business.cruds.GerenciamentoFornecedor;
import business.cruds.GerenciamentoItens;
import business.cruds.GerenciamentoMultas;
import business.cruds.GerenciamentoUsuarios;
import business.interfaces.IGerenciamentoContratos;
import business.relatorios.GeradorRelatorios;
import facade.SistemaFacade;
import repositories.CategoriaRepositorio;
import repositories.ContratoRepositorio;
import repositories.FornecedorRepositorio;
import repositories.ItemRepositorio;
import repositories.MultaRepositorio;
import repositories.UsuarioRepositorio;

/**
 * Monta repositórios, regras de negócio e Facade (a mesma ligação que {@link Main} faz no console),
 * para as telas JavaFX receberem um {@link SistemaFacade} pronto.
 */
public final class ConfiguracaoSistema {

    private static SistemaFacade sistema;

    private ConfiguracaoSistema() {
    }

    /** Facade único das telas JavaFX (criado na primeira chamada e reaproveitado depois). */
    public static synchronized SistemaFacade getSistema() {
        if (sistema == null) {
            sistema = criarSistema();
        }
        return sistema;
    }

    public static SistemaFacade criarSistema() {

        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        CategoriaRepositorio categoriaRepositorio = new CategoriaRepositorio();
        FornecedorRepositorio fornecedorRepositorio = new FornecedorRepositorio();
        ItemRepositorio itemRepositorio = new ItemRepositorio(categoriaRepositorio, fornecedorRepositorio);
        ContratoRepositorio contratoRepositorio = new ContratoRepositorio(usuarioRepositorio, itemRepositorio);
        MultaRepositorio multaRepositorio = new MultaRepositorio();

        IGerenciamentoContratos contratos = new GerenciamentoContratos(contratoRepositorio);
        GerenciamentoUsuarios usuarios = new GerenciamentoUsuarios(usuarioRepositorio, contratos);
        GerenciamentoCategoria categorias = new GerenciamentoCategoria(categoriaRepositorio);
        GerenciamentoFornecedor fornecedores = new GerenciamentoFornecedor(fornecedorRepositorio);
        GerenciamentoItens itens = new GerenciamentoItens(itemRepositorio);
        GerenciamentoMultas multas = new GerenciamentoMultas(multaRepositorio);

        GeradorRelatorios relatorios = new GeradorRelatorios(itens, contratos, usuarios);

        return new SistemaFacade(usuarios, contratos, itens, categorias, fornecedores, multas, relatorios);
    }
}
