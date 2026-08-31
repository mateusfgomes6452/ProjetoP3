package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Administrador;
import entidades.Cliente;
import entidades.Funcionario;
import entidades.Usuario;
import facade.SistemaFacade;

public class MenuUsuarios {

    private SistemaFacade sistema;
    private Scanner scanner;
    private boolean acessoTotal;

    public MenuUsuarios(SistemaFacade sistema, Scanner scanner, boolean acessoTotal) {
        this.sistema = sistema;
        this.scanner = scanner;
        this.acessoTotal = acessoTotal;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR USUÁRIOS =====");
            System.out.println("1 - Cadastrar Cliente");

            if (acessoTotal) {
                System.out.println("2 - Cadastrar Funcionário");
                System.out.println("3 - Cadastrar Administrador");
            }

            System.out.println("4 - Buscar Usuário por ID");
            System.out.println("5 - Listar todos os Usuários");

            if (acessoTotal) {
                System.out.println("6 - Atualizar Usuário");
                System.out.println("7 - Desativar Usuário");
            }

            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    cadastrarCliente();
                    break;

                case 2:
                    if (acessoTotal) cadastrarFuncionario();
                    else System.out.println("Acesso negado.");
                    break;

                case 3:
                    if (acessoTotal) cadastrarAdministrador();
                    else System.out.println("Acesso negado.");
                    break;

                case 4:
                    buscarUsuario();
                    break;

                case 5:
                    listarUsuarios();
                    break;

                case 6:
                    if (acessoTotal) atualizarUsuario();
                    else System.out.println("Acesso negado");
                    break;

                case 7:
                    if (acessoTotal) desativarUsuario();
                    else System.out.println("Acesso negado");
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    public void cadastrarCliente() {

        System.out.println("\n=== CADASTRAR CLIENTE ===");

        int id = sistema.gerarProximoIdUsuario();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = ValidaEntrada.lerEmail(scanner);

        System.out.print("CPF: ");
        String cpf = ValidaEntrada.lerCpf(scanner);

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Cliente cliente = new Cliente(id, nome, email, cpf, senha);

        if (sistema.cadastrarUsuario(cliente)) {
            System.out.println("Cliente cadastrado com sucesso. ID: " + id);
        } else {
            System.out.println("Erro: CPF ou e-mail já cadastrado");
        }
    }

    public void cadastrarFuncionario() {

        System.out.println("\n=== CADASTRAR FUNCIONÁRIO ===");

        int id = sistema.gerarProximoIdUsuario();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = ValidaEntrada.lerEmail(scanner);

        System.out.print("CPF: ");
        String cpf = ValidaEntrada.lerCpf(scanner);

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Funcionario funcionario = new Funcionario(id, nome, email, cpf, senha);

        if (sistema.cadastrarUsuario(funcionario)) {
            System.out.println("Funcionário cadastrado com sucesso. ID: " + id);
        } else {
            System.out.println("Erro: CPF ou e-mail já cadastrado");
        }
    }

    public void cadastrarAdministrador() {

        System.out.println("\n=== CADASTRAR ADMINISTRADOR ===");

        int id = sistema.gerarProximoIdUsuario();

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = ValidaEntrada.lerEmail(scanner);

        System.out.print("CPF: ");
        String cpf = ValidaEntrada.lerCpf(scanner);

        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        Administrador administrador = new Administrador(id, nome, email, cpf, senha);

        if (sistema.cadastrarUsuario(administrador)) {
            System.out.println("Administrador cadastrado com sucesso. ID: " + id);
        } else {
            System.out.println("Erro: CPF ou e-mail já cadastrado");
        }
    }

    public void buscarUsuario() {

        System.out.print("ID do usuário: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Usuario usuario = sistema.buscarUsuario(id);

        if (usuario == null) {
            System.out.println("Usuário não encontrado");
        } else {

            System.out.println("\n===== DADOS DO USUÁRIO =====");
            System.out.println("ID:     " + usuario.getId());
            System.out.println("Nome:   " + usuario.getNome());
            System.out.println("Email:  " + usuario.getEmail());
            System.out.println("CPF:    " + usuario.getCpf());
            System.out.println("Perfil: " + usuario.getClass().getSimpleName());
            System.out.println("Ativo:  " + usuario.isAtivo());
        }
    }

    public void listarUsuarios() {

        List<Usuario> usuarios = sistema.listarUsuarios();

        System.out.println("\n===== USUÁRIOS CADASTRADOS =====");

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado");
            
        } else {

            System.out.printf("%-5s %-25s %-30s %-15s %-6s%n","ID", "NOME", "EMAIL", "PERFIL", "ATIVO");
            System.out.println("-".repeat(85));

            for (Usuario usuario : usuarios) {
                System.out.printf("%-5d %-25s %-30s %-15s %-6s%n",usuario.getId(),usuario.getNome(),usuario.getEmail(),usuario.getClass().getSimpleName(),usuario.isAtivo());
            }
        }
    }

    public void atualizarUsuario() {

        System.out.print("ID do usuário: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Usuario usuario = sistema.buscarUsuario(id);

        if (usuario == null) {
            System.out.println("Usuário não encontrado");
            
        } else{

            System.out.println("Deixe em branco para manter o valor atual");

            System.out.print("Novo nome [" + usuario.getNome() + "]: ");
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) nome = usuario.getNome();

            System.out.print("Novo email [" + usuario.getEmail() + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = usuario.getEmail();

            System.out.print("Nova senha: ");
            String senha = scanner.nextLine().trim();
            if (senha.isEmpty()) senha = usuario.getSenha();

            if (sistema.atualizarUsuario(id, nome, email, senha)) {
                System.out.println("Usuário atualizado com sucesso");
            } else {
                System.out.println("Erro ao atualizar usuário");
            }
        }
    }

    public void desativarUsuario() {

        System.out.print("ID do usuário: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        if (sistema.desativaUsuario(id)) {
            System.out.println("Usuário desativado com sucesso");
        } else {
            System.out.println("Não foi possível desativar. Verifique se o usuário existe ou possui multas pendentes");
        }
    }
}