package business.interfaces;
import java.util.List;

import entidades.Usuario;

public interface IGerenciamentoUsuarios {

    public abstract boolean cadastrarUsuario(Usuario usuario);

    public abstract Usuario buscarUsuario(Long id);

    public abstract List<Usuario> listarUsuarios();

    public abstract boolean atualizarUsuario(Long id, String razaoSocial, String email, String telefone);

    public abstract boolean desativaUsuario(Long id);

    public abstract Usuario realizarLogin(String email, String senha);
    
    public abstract int gerarProximoId();

}