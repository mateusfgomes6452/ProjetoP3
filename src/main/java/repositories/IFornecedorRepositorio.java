package repositories;
import java.util.List;

import entidades.Fornecedor;

public interface IFornecedorRepositorio {

    public abstract void adicionar(Fornecedor fornecedor);

    public abstract List<Fornecedor> listarTodos();

    public abstract void desativar(Long id);

    public abstract Fornecedor buscarPorCnpj(String cnpj);

    public abstract Fornecedor buscarPorId(Long id);

    public abstract void salvar();
}
