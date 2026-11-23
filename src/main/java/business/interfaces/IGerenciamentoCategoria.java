package business.interfaces;

import java.util.List;
import entidades.Categoria;
 
public interface IGerenciamentoCategoria {
 
    public abstract boolean cadastrarCategoria(Categoria categoria);
 
    public abstract Categoria buscarCategoria(Long idEmEdicao);
 
    public abstract List<Categoria> listarCategorias();
 
    public abstract boolean atualizarCategoria(Categoria categoriaNova);
 
    public abstract boolean desativarCategoria(Long id);
}
