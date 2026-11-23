package repositories;
import java.util.List;

import entidades.ContratoAluguel;

public interface IContratoRepositorio {

    public abstract void adicionar(ContratoAluguel contrato);

    public abstract List<ContratoAluguel> listarTodos();

    public abstract ContratoAluguel buscarPorId(Long id);

    public abstract List<ContratoAluguel> buscarPorCliente(Long idCliente);

    public abstract ContratoAluguel buscarContratoAtivoPorItem(Long idItem);

    public abstract void remover(ContratoAluguel contrato);
    
    public abstract void salvar();
}