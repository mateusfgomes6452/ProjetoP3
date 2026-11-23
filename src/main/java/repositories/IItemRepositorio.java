package repositories;

import java.util.List;

import entidades.Item;

public interface IItemRepositorio {

    public abstract void adicionar(Item item);

    public abstract List<Item> listarTodos();

    public abstract Item buscarPorId(Long id);

    public abstract void remover(Item item);

    public abstract void salvar();
}