package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Fornecedor;
import facade.SistemaFacade;

public class MenuFornecedores {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuFornecedores(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR FORNECEDORES =====");
            System.out.println("1 - Cadastrar Fornecedor");
            System.out.println("2 - Buscar Fornecedor por ID");
            System.out.println("3 - Listar todos os Fornecedores");
            System.out.println("4 - Atualizar Fornecedor");
            System.out.println("5 - Desativar Fornecedor");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    cadastrarFornecedor();
                    break;

                case 2:
                    buscarFornecedor();
                    break;

                case 3:
                    listarFornecedores();
                    break;

                case 4:
                    atualizarFornecedor();
                    break;

                case 5:
                    desativarFornecedor();
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    private void cadastrarFornecedor() {

        System.out.println("\n=== CADASTRAR FORNECEDOR ===");

        int id = sistema.gerarProximoIdFornecedor();

        System.out.print("Razão Social: ");
        String razaoSocial = scanner.nextLine().trim();

        System.out.print("CNPJ: ");
        String cnpj = ValidaEntrada.lerCnpj(scanner);

        System.out.print("Email: ");
        String email = ValidaEntrada.lerEmail(scanner);

        System.out.print("Telefone: ");
        String telefone = ValidaEntrada.lerTelefone(scanner);

        Fornecedor fornecedor = new Fornecedor(id, razaoSocial, cnpj, email, telefone);

        if (sistema.cadastrarFornecedor(fornecedor)) {
            System.out.println("Fornecedor cadastrado com sucesso. ID: " + id);
        } else {
            System.out.println("Erro: já existe um fornecedor com esse CNPJ");
        }
    }

    public void buscarFornecedor() {

        System.out.print("ID do fornecedor: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Fornecedor fornecedor = sistema.buscarFornecedor(id);

        if (fornecedor == null) {
            System.out.println("Fornecedor não encontrado");
        } else{

            exibirFornecedor(fornecedor);
        }
    }

    public void listarFornecedores() {

        List<Fornecedor> fornecedores = sistema.listarFornecedores();

        System.out.println("\n===== FORNECEDORES CADASTRADOS =====");

        if (fornecedores.isEmpty()) {
            System.out.println("Nenhum fornecedor cadastrado");
        } else {

            System.out.printf("%-5s %-30s %-20s %-25s %-15s %-6s%n","ID", "RAZÃO SOCIAL", "CNPJ", "EMAIL", "TELEFONE", "ATIVO");
            System.out.println("------------------------------------------------------------------------------------------------------------------");

            for (Fornecedor fornecedor : fornecedores) {
                System.out.printf("%-5d %-30s %-20s %-25s %-15s %-6s%n",fornecedor.getId(),fornecedor.getRazaoSocial(),fornecedor.getCnpj(),fornecedor.getEmail(),fornecedor.getTelefone(),fornecedor.isAtivo());
            }
        }
    }

    public void atualizarFornecedor() {

        System.out.print("ID do fornecedor: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Fornecedor fornecedor = sistema.buscarFornecedor(id);

        if (fornecedor == null) {
            System.out.println("Fornecedor não encontrado");
        } else{

            System.out.println("Deixe em branco para manter o valor atual");

            System.out.print("Razão Social [" + fornecedor.getRazaoSocial() + "]: ");
            String razaoSocial = scanner.nextLine().trim();
            if (razaoSocial.isEmpty()) razaoSocial = fornecedor.getRazaoSocial();

            System.out.print("Email [" + fornecedor.getEmail() + "]: ");
            String email = ValidaEntrada.lerEmail(scanner);
            if (email.isEmpty()) email = fornecedor.getEmail();

            System.out.print("Telefone [" + fornecedor.getTelefone() + "]: ");
            String telefone = ValidaEntrada.lerTelefone(scanner);
            if (telefone.isEmpty()) telefone = fornecedor.getTelefone();

            Fornecedor fornecedorNovo = new Fornecedor(id, razaoSocial, fornecedor.getCnpj(), email, telefone);
            if (sistema.atualizarFornecedor(fornecedorNovo)) {
                System.out.println("Fornecedor atualizado com sucesso");
            } else {
                System.out.println("Erro ao atualizar fornecedor");
            }
        }
    }

    private void desativarFornecedor() {

        System.out.print("ID do fornecedor: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Fornecedor fornecedor = sistema.buscarFornecedor(id);

        if (fornecedor == null) {
            System.out.println("Fornecedor não encontrado");
        } else {

            System.out.println("Tem certeza que deseja desativar \"" + fornecedor.getRazaoSocial() + "\"?");
            System.out.print("Digite 1 para confirmar ou 0 para cancelar: ");
            int confirmacao = ValidaEntrada.lerInteiro(scanner);

            if (confirmacao != 1) {
                System.out.println("Operação cancelada");
                
            } else {

                if (sistema.desativarFornecedor(id)) {
                    System.out.println("Fornecedor desativado com sucesso");
                } else {
                    System.out.println("Erro ao desativar fornecedor");
                }
            }
        }
    }

    private void exibirFornecedor(Fornecedor fornecedor) {

        System.out.println("\n===== DADOS DO FORNECEDOR =====");
        System.out.println("ID:           " + fornecedor.getId());
        System.out.println("Razão Social: " + fornecedor.getRazaoSocial());
        System.out.println("CNPJ:         " + fornecedor.getCnpj());
        System.out.println("Email:        " + fornecedor.getEmail());
        System.out.println("Telefone:     " + fornecedor.getTelefone());
        System.out.println("Ativo:        " + fornecedor.isAtivo());
    }
}