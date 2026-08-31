package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;
import entidades.Fornecedor;
import entidades.Item;

public class ItemRepositorio implements IItemRepositorio {

    private String arquivo = "dados/itens.csv";

    private List<Item> itens;
    private ICategoriaRepositorio categoriaRepositorio;
    private IFornecedorRepositorio fornecedorRepositorio;
    private ManipulaArquivo manipulaArquivo;

    public ItemRepositorio(ICategoriaRepositorio categoriaRepositorio, IFornecedorRepositorio fornecedorRepositorio) {

        this.categoriaRepositorio = categoriaRepositorio;
        this.fornecedorRepositorio = fornecedorRepositorio;

        manipulaArquivo = new ManipulaArquivo(arquivo);

        itens = carregar();

    }

    public List<Item> carregar() {

        List<Item> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 9) {

                int id = Integer.parseInt(campos[0].trim());
                String nome = campos[1].trim();
                String descricao = campos[2].trim();
                double taxaDiaria = Double.parseDouble(campos[3].trim());
                String estado = campos[4].trim();
                double valorReposicao = Double.parseDouble(campos[5].trim());
                String status = campos[6].trim();
                boolean ativo = Boolean.parseBoolean(campos[7].trim());

                int idCategoria = Integer.parseInt(campos[8].trim());

                int idFornecedor = 0;

                if (campos.length > 9) {

                    idFornecedor = Integer.parseInt(campos[9].trim());

                }

                Categoria categoria = null;

                if (idCategoria > 0) {

                    categoria = categoriaRepositorio.buscarPorId(idCategoria);

                }

                Fornecedor fornecedor = null;

                if (idFornecedor > 0) {

                    fornecedor = fornecedorRepositorio.buscarPorId(idFornecedor);

                }

                Item item = new Item(id, nome, descricao, taxaDiaria, estado, valorReposicao, categoria, fornecedor);

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
    public Item buscarPorId(int id) {

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

    @Override
    public int gerarProximoId() {

        int maiorId = 0;

        for (Item item : itens) {

            if (item.getId() > maiorId) {

                maiorId = item.getId();

            }

        }

        return maiorId + 1;

    }

}