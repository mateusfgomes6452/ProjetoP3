package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.Usuario;
import repositories.IUsuarioRepositorio;

public class GerenciamentoUsuarios implements IGerenciamentoUsuarios {

    private final IUsuarioRepositorio repositorio;
    private final IGerenciamentoContratos gerenciamentoContratos;

    public GerenciamentoUsuarios(IUsuarioRepositorio repositorio,
                                  IGerenciamentoContratos gerenciamentoContratos) {
        this.repositorio            = repositorio;
        this.gerenciamentoContratos = gerenciamentoContratos;
    }

    @Override
    public boolean cadastrarUsuario(Usuario usuario) {
        if (usuario.getId() != null && repositorio.buscarPorId(usuario.getId()) != null) {
            return false;
        }

        if (repositorio.buscarPorCpf(usuario.getCpf()) != null) {
            return false;
        }

        if (repositorio.buscarPorEmail(usuario.getEmail()) != null) {
            return false;
        }

        repositorio.adicionar(usuario);

        return true;
    }

    @Override
    public Usuario buscarUsuario(Long id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarUsuario(Long id, String nome, String email, String senha) {
        Usuario usuario = repositorio.buscarPorId(id);
        if (usuario == null) {
            return false;
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        repositorio.atualizar(usuario);

        return true;
    }

    @Override
    public boolean desativaUsuario(Long id) {
        Usuario usuario = repositorio.buscarPorId(id);

        if (usuario == null) {
            return false;
        }

        if (gerenciamentoContratos.clientePossuiMultaPendente(id)) {
            return false;
        }

        usuario.setAtivo(false);

        repositorio.atualizar(usuario);

        return true;
    }

    @Override
    public Usuario realizarLogin(String email, String senha) {
        Usuario usuario = repositorio.buscarPorEmail(email);

        if (usuario == null || !usuario.isAtivo()) {
            return null;
        }

        if (!usuario.getSenha().equals(senha)) {
            return null;
        }

        return usuario;
    }
}
