package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;

public class ItemRepositorio implements IItemRepositorio {

    private static final String ARQUIVO = "dados/itens.csv";

    private final List<Item> itens;
    private final ICategoriaRepositorio categoriaRepositorio;
    private final IFornecedorRepositorio fornecedorRepositorio;
    private final ManipulaArquivo manipulaArquivo;

    public ItemRepositorio(ICategoriaRepositorio categoriaRepositorio, IFornecedorRepositorio fornecedorRepositorio) {

        this.categoriaRepositorio = categoriaRepositorio;
        this.fornecedorRepositorio = fornecedorRepositorio;

        manipulaArquivo = new ManipulaArquivo(ARQUIVO);

        itens = carregar();

    }

    public List<Item> carregar() {
        List<Item> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 9) {
                String nome = campos[1].trim();
                String descricao = campos[2].trim();
                double taxaDiaria = Double.parseDouble(campos[3].trim());
                String estado = campos[4].trim();
                double valorReposicao = Double.parseDouble(campos[5].trim());
                String status = campos[6].trim();
                boolean ativo = Boolean.parseBoolean(campos[7].trim());

                Long idCategoria = Long.parseLong(campos[8].trim());

                Long idFornecedor = (long) 0;

                if (campos.length > 9) {
                    idFornecedor = Long.parseLong(campos[9].trim());
                }

                Categoria categoria = null;

                if (idCategoria > 0) {
                    categoria = categoriaRepositorio.buscarPorId(idCategoria);
                }

                Fornecedor fornecedor = null;

                if (idFornecedor > 0) {
                    fornecedor = fornecedorRepositorio.buscarPorId(idFornecedor);
                }

                Item item = new Item(new Item.Infos(nome, descricao, estado), new Item.ValoresItem(taxaDiaria, valorReposicao), categoria, fornecedor);

                item.setStatus(status);
                item.setAtivo(ativo);

                lista.add(item);
            }
        }
        return lista;
    }
    
    @Override
    public void salvar() {
        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id","nome","descricao","taxaDiaria","estadoConservacao","valorReposicao","status","ativo","idCategoria","idFornecedor"});

        for (Item item : itens) {

            linhas.add(new String[]{String.valueOf(item.getId()),item.getNome(), item.getDescricao(), String.valueOf(item.getTaxaDiaria()),item.getEstadoConservacao(),String.valueOf(item.getValorReposicao()),item.getStatus(),String.valueOf(item.isAtivo()),item.getCategoria() != null ? String.valueOf(item.getCategoria().getId()) : "0",item.getFornecedor() != null ? String.valueOf(item.getFornecedor().getId()) : "0"});

        }

        manipulaArquivo.salvar(linhas);
    }

    @Override
    public void adicionar(Item item) {
        itens.add(item);

        salvar();
    }

    @Override
    public List<Item> listarTodos() {
        return itens;

    }

    @Override
    public Item buscarPorId(Long id) {
        for (Item item : itens) {

            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void remover(Item item) {
        itens.remove(item);

        salvar();

    }
}
