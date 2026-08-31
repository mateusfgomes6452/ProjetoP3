package apresentacao;

import java.util.Scanner;

import facade.SistemaFacade;


public class MenuRelatorios {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuRelatorios(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== RELATÓRIOS =====");
            System.out.println("1 - Itens disponíveis por categoria");
            System.out.println("2 - Histórico de aluguéis de um cliente");
            System.out.println("3 - Itens alugados no momento");
            System.out.println("4 - Relatório de faturamento por período");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    sistema.gerarRelatorioItensDisponiveis();
                    break;

                case 2:
                    System.out.print("ID do cliente: ");
                    int idCliente = ValidaEntrada.lerInteiro(scanner);
                    sistema.gerarHistoricoCliente(idCliente);
                    break;

                case 3:
                    sistema.gerarRelatorioItensAlugados();
                    break;

                case 4:
                    System.out.print("Data inicial (AAAA-MM-DD): ");
                    String dataInicial = ValidaEntrada.lerData(scanner);
                    System.out.print("Data final (AAAA-MM-DD): ");
                    String dataFinal = ValidaEntrada.lerData(scanner);
                    sistema.gerarRelatorioFaturamento(dataInicial, dataFinal);
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);
    }
}