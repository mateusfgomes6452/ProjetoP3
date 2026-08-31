package apresentacao;

import java.util.List;
import java.util.Scanner;

import entidades.Multa;
import facade.SistemaFacade;

public class MenuMultas {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuMultas(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR MULTAS =====");
            System.out.println("1 - Registrar Multa manual (avaria)");
            System.out.println("2 - Buscar Multa por ID");
            System.out.println("3 - Listar todas as Multas");
            System.out.println("4 - Listar Multas de um Contrato");
            System.out.println("5 - Listar Multas pendentes");
            System.out.println("6 - Quitar Multa");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    registrarMulta();
                    break;

                case 2:
                    buscarMulta();
                    break;

                case 3:
                    listarMultas();
                    break;

                case 4:
                    listarPorContrato();
                    break;

                case 5:
                    listarPendentes();
                    break;

                case 6:
                    quitarMulta();
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    public void registrarMulta() {

        System.out.println("\n=== REGISTRAR MULTA ===");

        System.out.print("ID do contrato: ");
        int idContrato = ValidaEntrada.lerInteiro(scanner);

        if (sistema.buscarContrato(idContrato) == null) {
            System.out.println("Contrato não encontrado");
            
        } else {

            System.out.println("Tipo: 1 - AVARIA  2 - OUTRO");
            System.out.print("Opção: ");
            int tipoOpcao = ValidaEntrada.lerInteiro(scanner);
            String tipo = "";
            if (tipoOpcao == 1){
                tipo = "AVARIA";
            } else {
                tipo = "OUTRO";
            }

            System.out.print("Descrição: ");
            String descricao = scanner.nextLine().trim();

            System.out.print("Valor (R$): ");
            double valor = ValidaEntrada.lerDouble(scanner);

            String hoje = java.time.LocalDate.now().toString();

            int id = sistema.gerarProximoIdMulta();

            Multa multa = new Multa(id, idContrato, tipo, descricao, valor, hoje);

            if (sistema.registrarMulta(multa)) {
                System.out.println("Multa registrada com sucesso ID: " + id);
            } else {
                System.out.println("Erro ao registrar multa");
            }
        }
    }

    public void buscarMulta() {

        System.out.print("ID da multa: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        Multa multa = sistema.buscarMulta(id);

        if (multa == null) {
            System.out.println("Multa não encontrada.");
        } else{

            exibirMulta(multa);
        }
    }

    public void listarMultas() {

        List<Multa> multas = sistema.listarMultas();

        System.out.println("\n===== TODAS AS MULTAS =====");

        if (multas.isEmpty()) {
            System.out.println("Nenhuma multa registrada.");
        } else {

            imprimirCabecalho();

            for (Multa multa : multas) {
                imprimirLinha(multa);
            }
        }
    }

    private void listarPorContrato() {

        System.out.print("ID do contrato: ");
        int idContrato = ValidaEntrada.lerInteiro(scanner);

        List<Multa> multas = sistema.listarMultasPorContrato(idContrato);

        System.out.println("\n===== MULTAS DO CONTRATO " + idContrato + " =====");

        if (multas.isEmpty()) {
            System.out.println("Nenhuma multa encontrada para esse contrato");
            
        } else {

            imprimirCabecalho();

            for (Multa multa : multas) {
                imprimirLinha(multa);
            }
        }
    }

    private void listarPendentes() {

        List<Multa> multas = sistema.listarMultasPendentes();

        System.out.println("\n===== MULTAS PENDENTES =====");

        if (multas.isEmpty()) {
            System.out.println("Nenhuma multa pendente");
            
        } else {

            imprimirCabecalho();

            for (Multa multa : multas) {
                imprimirLinha(multa);
            }
        }
    }

    public void quitarMulta() {

        System.out.print("ID da multa: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        if (sistema.quitarMulta(id)) {
            System.out.println("Multa quitada com sucesso.");
        } else {
            System.out.println("Multa não encontrada ou já estava paga.");
        }
    }

    public void exibirMulta(Multa multa) {

        System.out.println("\n===== DADOS DA MULTA =====");
        System.out.println("ID:         " + multa.getId());
        System.out.println("Contrato:   " + multa.getIdContrato());
        System.out.println("Tipo:       " + multa.getTipo());
        System.out.println("Descrição:  " + multa.getDescricao());
        System.out.printf("Valor:      R$ %.2f%n", multa.getValor());
        System.out.println("Paga:       " + multa.isPaga());
        System.out.println("Criada em:  " + multa.getDataCriacao());
    }

    public void imprimirCabecalho() {
        System.out.printf("%-5s %-10s %-10s %-10s %-6s%n",
                "ID", "CONTRATO", "TIPO", "VALOR", "PAGA");
        System.out.println("---------------------------------------------------------");
    }

    public void imprimirLinha(Multa multa) {
        System.out.printf("%-5d %-10d %-10s R$%-8.2f %-6s%n",multa.getId(), multa.getIdContrato(), multa.getTipo(), multa.getValor(), multa.isPaga());
    }
}