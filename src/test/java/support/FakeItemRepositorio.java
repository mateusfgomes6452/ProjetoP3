package support;

import java.util.ArrayList;
import java.util.List;

import entidades.Item;
import repositories.IItemRepositorio;


public class FakeItemRepositorio implements IItemRepositorio {

    private final List<Item> itens = new ArrayList<>();
    private int chamadasSalvar = 0;

    @Override
    public void adicionar(Item item) {
        itens.add(item);
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
    }

    @Override
    public void salvar() {
        chamadasSalvar++;
    }

    public int getChamadasSalvar() {
        return chamadasSalvar;
    }
}
