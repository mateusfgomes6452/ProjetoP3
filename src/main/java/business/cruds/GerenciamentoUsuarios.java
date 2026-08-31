package business.cruds;

import java.util.List;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.Usuario;
import repositories.IUsuarioRepositorio;

public class GerenciamentoUsuarios implements IGerenciamentoUsuarios {

    private IUsuarioRepositorio repositorio;
    private IGerenciamentoContratos gerenciamentoContratos;

    public GerenciamentoUsuarios(IUsuarioRepositorio repositorio,
                                  IGerenciamentoContratos gerenciamentoContratos) {
        this.repositorio            = repositorio;
        this.gerenciamentoContratos = gerenciamentoContratos;
    }

    @Override
    public boolean cadastrarUsuario(Usuario usuario) {

        if (repositorio.buscarPorId(usuario.getId()) != null) {
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
    public Usuario buscarUsuario(int id) {
        return repositorio.buscarPorId(id);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return repositorio.listarTodos();
    }

    @Override
    public boolean atualizarUsuario(int id, String nome, String email, String senha) {
        
        Usuario usuario = repositorio.buscarPorId(id);
        if (usuario == null) {
            return false;
        }
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        repositorio.salvar();

        return true;
    }

    @Override
    public boolean desativaUsuario(int id) {

        Usuario usuario = repositorio.buscarPorId(id);

        if (usuario == null) {
            return false;
        }

        if (gerenciamentoContratos.clientePossuiMultaPendente(id)) {
            System.out.println("Usuário possui multas pendentes e não pode ser desativado.");
            return false;
        }

        usuario.setAtivo(false);

        repositorio.salvar();

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

    @Override
    public int gerarProximoId() {
        return repositorio.gerarProximoId();
    }
}