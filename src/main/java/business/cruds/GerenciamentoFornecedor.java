package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoFornecedor;
import entidades.Fornecedor;
import repositories.IFornecedorRepositorio;

public class GerenciamentoFornecedor implements IGerenciamentoFornecedor {

    private final IFornecedorRepositorio repositorio;

    public GerenciamentoFornecedor(IFornecedorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean cadastrarFornecedor(Fornecedor fornecedor) {
        if (fornecedor.getId() != null && repositorio.buscarPorId(fornecedor.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorCnpj(fornecedor.getCnpj()) != null) {
            return false;
        }

        repositorio.adicionar(fornecedor);

        return true;
    }

    @Override
    public Fornecedor buscarFornecedor(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Fornecedor> listarFornecedores() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarFornecedor(Fornecedor fornecedorNovo) {
    	if (fornecedorNovo.getId() == null || repositorio.buscarPorId(fornecedorNovo.getId()) == null) {
            return false;
        }
    	repositorio.atualizar(fornecedorNovo);
    	return true;
    }

    @Override
    public boolean desativarFornecedor(Long id) {
        Fornecedor fornecedor = repositorio.buscarPorId(id);

        if (fornecedor == null) {
            return false;
        }

        repositorio.desativar(id); 

        return true;
    }
}
