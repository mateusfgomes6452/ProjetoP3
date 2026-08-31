package support;

import java.util.ArrayList;
import java.util.List;

import entidades.Usuario;
import repositories.IUsuarioRepositorio;

public class FakeUsuarioRepositorio implements IUsuarioRepositorio {

    private final List<Usuario> usuarios = new ArrayList<>();
    private int chamadasSalvar = 0;

    @Override
    public void adicionar(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarios;
    }

    @Override
    public void remover(Usuario usuario) {
        usuarios.remove(usuario);
    }

    @Override
    public Usuario buscarPorCpf(String cpf) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCpf().equals(cpf)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorId(int id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return usuario;
            }
        }
        return null;
    }

    @Override
    public int gerarProximoId() {
        int maiorId = 0;
        for (Usuario usuario : usuarios) {
            if (usuario.getId() > maiorId) {
                maiorId = usuario.getId();
            }
        }
        return maiorId + 1;
    }
 
    @Override
    public void salvar() {
        chamadasSalvar++;
    }

    public int getChamadasSalvar() {
        return chamadasSalvar;
    }
}
