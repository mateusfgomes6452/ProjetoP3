package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoCategoria;
import entidades.Categoria;
import repositories.ICategoriaRepositorio;

public class GerenciamentoCategoria implements IGerenciamentoCategoria {

    private final ICategoriaRepositorio repositorio;

    public GerenciamentoCategoria(ICategoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarCategoria(Categoria categoria) {

        if (categoria.getId() != null && repositorio.buscarPorId(categoria.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorNome(categoria.getNome()) != null) {
            return false;
        }

        repositorio.adicionar(categoria); 

        return true;
    }

    @Override
    public Categoria buscarCategoria(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Categoria> listarCategorias() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarCategoria(Categoria categoriaNova) {

        if (categoriaNova.getId() == null || repositorio.buscarPorId(categoriaNova.getId()) == null) {
        	return false;
        }
        repositorio.atualizar(categoriaNova);
        return true;
    }

    @Override
    public boolean desativarCategoria(Long id) {

        Categoria categoria = repositorio.buscarPorId(id);

        if (categoria == null) {
            return false;
        }

        categoria.desativar();

        repositorio.atualizar(categoria);

        return true;
    }
}
