package repositories;
import java.util.List;

import entidades.Usuario;
public interface IUsuarioRepositorio {

    public abstract void adicionar(Usuario usuario);

    public abstract List<Usuario> listarTodos();

    public abstract void remover(Usuario usuario);

    public abstract Usuario buscarPorCpf(String cpf);

    public abstract Usuario buscarPorId(Long id);

    public abstract Usuario buscarPorEmail(String email);

    public abstract void atualizar(Usuario usuario);
}
