package repositories;
import java.util.List;

import entidades.Multa;

public interface IMultaRepositorio {

    public abstract void adicionar(Multa multa);

    public abstract List<Multa> listarTodos();

    public abstract Multa buscarPorId(Long id);

    public abstract List<Multa> buscarPorContrato(Long idContrato);

    public abstract List<Multa> buscarPendentes();

    public abstract void salvar();
}
