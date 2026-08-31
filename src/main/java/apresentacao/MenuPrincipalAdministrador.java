package apresentacao;

import java.util.Scanner;

import entidades.Administrador;
import facade.SistemaFacade;

public class MenuPrincipalAdministrador {

    private SistemaFacade sistema;
    private Administrador administrador;
    private Scanner scanner;

    public MenuPrincipalAdministrador(SistemaFacade sistema, Administrador administrador, Scanner scanner) {
        this.sistema = sistema;
        this.administrador = administrador;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===================================");
            System.out.println("       MENU ADMINISTRADOR         ");
            System.out.println("===================================");
            System.out.println("Bem vindo, " + administrador.getNome());
            System.out.println("1 - Usuários");
            System.out.println("2 - Itens");
            System.out.println("3 - Categorias");
            System.out.println("4 - Fornecedores");
            System.out.println("5 - Contratos / Aluguéis");
            System.out.println("6 - Multas");
            System.out.println("7 - Relatórios");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    new MenuUsuarios(sistema, scanner, true).exibir();
                    break;

                case 2:
                    new MenuItens(sistema, scanner).exibir();
                    break;

                case 3:
                    new MenuCategorias(sistema, scanner).exibir();
                    break;

                case 4:
                    new MenuFornecedores(sistema, scanner).exibir();
                    break;

                case 5:
                    new MenuContratos(sistema, scanner).exibir();
                    break;

                case 6:
                    new MenuMultas(sistema, scanner).exibir();
                    break;

                case 7:
                    new MenuRelatorios(sistema, scanner).exibir();
                    break;

                case 0:
                    System.out.println("Sessão encerrada.");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }
}