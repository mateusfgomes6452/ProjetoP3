package repositories;

import java.util.ArrayList;


import java.util.List;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;

public class UsuarioRepositorio implements IUsuarioRepositorio {

    private String arquivo = "dados/usuarios.csv";

    private List<Usuario> usuarios;
    private ManipulaArquivo manipulaArquivo;

    public UsuarioRepositorio() {

        manipulaArquivo = new ManipulaArquivo(arquivo);
        usuarios = carregar();

    }


    public List<Usuario> carregar() {

        List<Usuario> lista = new ArrayList<>();

        for (String[] campos : manipulaArquivo.carregar()) {

            if (campos.length >= 7) {

                int id = Integer.parseInt(campos[0].trim());
                String tipo = campos[1].trim();
                String nome = campos[2].trim();
                String email = campos[3].trim();
                String cpf = campos[4].trim();
                String senha = campos[5].trim();
                boolean ativo = Boolean.parseBoolean(campos[6].trim());

                Usuario usuario;

                switch (tipo) {

                    case "Administrador":
                        usuario = new Administrador(id, nome, email, cpf, senha);
                        break;

                    case "Funcionario":
                        usuario = new Funcionario(id, nome, email, cpf, senha);
                        break;

                    default:
                        usuario = new Cliente(id, nome, email, cpf, senha);
                        break;

                }

                usuario.setAtivo(ativo);
                lista.add(usuario);

            }

        }

        return lista;

    }
    @Override
    public void salvar() {

        List<String[]> linhas = new ArrayList<>();

        linhas.add(new String[]{"id","tipo","nome","email","cpf","senha","ativo"});

        for (Usuario usuario : usuarios) {

            linhas.add(new String[]{String.valueOf(usuario.getId()),usuario.getClass().getSimpleName(),usuario.getNome(),usuario.getEmail(),usuario.getCpf(),usuario.getSenha(),String.valueOf(usuario.isAtivo())});

        }

        manipulaArquivo.salvar(linhas);

    }

    @Override
    public void adicionar(Usuario usuario) {

        usuarios.add(usuario);
        salvar();

    }

    @Override
    public List<Usuario> listarTodos() {

        return usuarios;

    }

    @Override
    public void remover(Usuario usuario) {

        usuarios.remove(usuario);
        salvar();

    }

    @Override
    public Usuario buscarPorEmail(String email) {

        for (Usuario usuario : usuarios) {

            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }

        }

        return null;

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
    public int gerarProximoId() {

        int maiorId = 0;

        for (Usuario usuario : usuarios) {

            if (usuario.getId() > maiorId) {
                maiorId = usuario.getId();
            }

        }

        return maiorId + 1;

    }

}