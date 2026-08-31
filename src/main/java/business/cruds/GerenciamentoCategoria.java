package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoCategoria;
import entidades.Categoria;
import repositories.ICategoriaRepositorio;

public class GerenciamentoCategoria implements IGerenciamentoCategoria {

    private ICategoriaRepositorio repositorio;

    public GerenciamentoCategoria(ICategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarCategoria(Categoria categoria) {

        if (repositorio.buscarPorId(categoria.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorNome(categoria.getNome()) != null) {
            return false;
        }

        repositorio.adicionar(categoria); 

        return true;
    }

    @Override
    public Categoria buscarCategoria(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarCategoria(Categoria categoriaNova) {

        List<Categoria> categorias = repositorio.listarTodos();

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == categoriaNova.getId()) {
                categorias.set(i, categoriaNova);
                repositorio.salvar();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean desativarCategoria(int id) {

        Categoria categoria = repositorio.buscarPorId(id);

        if (categoria == null) {
            return false;
        }

        categoria.desativar();

        repositorio.salvar();

        return true;
    }

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }
}