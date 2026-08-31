package repositories;

import java.util.List;

import entidades.Categoria;

public interface ICategoriaRepositorio {

    public abstract void adicionar(Categoria categoria);

    public abstract List<Categoria> listarTodos();

    public abstract Categoria buscarPorId(int id);

    public abstract Categoria buscarPorNome(String nome);

    public abstract void salvar();

    public abstract int gerarProximoId();

}