package repositories;
import java.util.List;

import entidades.Multa;

public interface IMultaRepositorio {

    public abstract void adicionar(Multa multa);

    public abstract List<Multa> listarTodos();

    public abstract Multa buscarPorId(int id);

    public abstract List<Multa> buscarPorContrato(int idContrato);

    public abstract List<Multa> buscarPendentes();

    public abstract void salvar();

    public abstract int gerarProximoId();
}
