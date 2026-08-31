package business.interfaces;
import java.util.List;

import entidades.Fornecedor;

public interface IGerenciamentoFornecedor{

    public abstract boolean cadastrarFornecedor(Fornecedor fornecedor);
    public abstract Fornecedor buscarFornecedor(int id);
    public abstract List<Fornecedor> listarFornecedores();
    public abstract boolean atualizarFornecedor(Fornecedor fornecedorNovo);
    public abstract boolean desativarFornecedor(int id);
    public abstract int gerarProximoId();
    

}