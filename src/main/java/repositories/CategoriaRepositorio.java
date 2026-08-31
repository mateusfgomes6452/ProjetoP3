package repositories;

import java.util.ArrayList;
import java.util.List;

import entidades.Categoria;

public class CategoriaRepositorio implements ICategoriaRepositorio {

    private String arquivo = "dados/categorias.csv";

    private List<Categoria> categorias;
    private ManipulaArquivo manipulaArquivo;

    public CategoriaRepositorio() {

        manipulaArquivo = new ManipulaArquivo(arquivo);
        categorias = carregar();

    }

    public List<Categoria> carregar() {

        List<Categoria> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 4) {

                int id = Integer.parseInt(campos[0].trim());
                String nome = campos[1].trim();
                String descricao = campos[2].trim();
                boolean ativa = Boolean.parseBoolean(campos[3].trim());

                Categoria categoria = new Categoria(id, nome, descricao);

                categoria.setAtiva(ativa);

                lista.add(categoria);

            }

        }

        return lista;

    }
    @Override
    public void salvar() {

        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id", "nome", "descricao", "ativa"});

        for (Categoria categoria : categorias) {

            linhas.add(new String[]{String.valueOf(categoria.getId()),categoria.getNome(),categoria.getDescricao(),String.valueOf(categoria.isAtiva())});

        }

        manipulaArquivo.salvar(linhas);

    }

    @Override
    public void adicionar(Categoria categoria) {

        categorias.add(categoria);
        salvar();

    }

    @Override
    public List<Categoria> listarTodos() {
        return categorias;

    }

    @Override
    public Categoria buscarPorId(int id) {

        for (Categoria categoria : categorias) {

            if (categoria.getId() == id) {

                return categoria;

            }

        }

        return null;

    }

    @Override
    public Categoria buscarPorNome(String nome) {

        for (Categoria categoria : categorias) {

            if (categoria.getNome().equalsIgnoreCase(nome)) {

                return categoria;

            }

        }

        return null;

    }

    @Override
    public int gerarProximoId() {

        int maiorId = 0;

        for (Categoria categoria : categorias) {

            if (categoria.getId() > maiorId) {

                maiorId = categoria.getId();

            }

        }

        return maiorId + 1;

    }

}