package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoItens;
import entidades.Item;
import repositories.IItemRepositorio;

public class GerenciamentoItens implements IGerenciamentoItens {

    private IItemRepositorio repositorio;

    public GerenciamentoItens(IItemRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarItem(Item item) {

        if (repositorio.buscarPorId(item.getId()) != null) {
            return false;
        }

        repositorio.adicionar(item);

        return true;
    }

    @Override
    public Item buscarItem(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Item> listarItens() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarItem(Item itemNovo) {

        List<Item> items = repositorio.listarTodos();
        
        for (int i = 0; i < items.size(); i++){
            if (items.get(i).getId() == itemNovo.getId()){
                items.set(i, itemNovo);
                repositorio.salvar();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean excluirItem(int id) {

        Item item = repositorio.buscarPorId(id);

        if (item == null || item.getStatus().equals("ALUGADO")) {
            return false;
        }

        item.desativar();

        repositorio.salvar();

        return true;
    }

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }
}