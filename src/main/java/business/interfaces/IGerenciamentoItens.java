package business.interfaces;

import java.util.List;

import entidades.Item;

public interface IGerenciamentoItens {

    public abstract boolean cadastrarItem(Item item);

    public abstract Item buscarItem(int id);

    public abstract List<Item> listarItens();

    public abstract boolean atualizarItem(Item itemNovo);

    public abstract boolean excluirItem(int id);

    public abstract int gerarProximoId();
}