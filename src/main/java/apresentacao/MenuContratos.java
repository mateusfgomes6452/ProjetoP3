package apresentacao;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

import entidades.Cliente;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import facade.SistemaFacade;


public class MenuContratos {

    private SistemaFacade sistema;
    private Scanner scanner;

    public MenuContratos(SistemaFacade sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }

    public void exibir() {

        int opcao;

        do {

            System.out.println("\n===== GERENCIAR CONTRATOS =====");
            System.out.println("1 - Registrar Aluguel");
            System.out.println("2 - Buscar Contrato por ID");
            System.out.println("3 - Listar todos os Contratos");
            System.out.println("4 - Finalizar Contrato (devolução)");
            System.out.println("5 - Cancelar Contrato");
            System.out.println("0 - Voltar");
            System.out.print("Opção: ");

            opcao = ValidaEntrada.lerInteiro(scanner);

            switch (opcao) {

                case 1:
                    registrarAluguel();
                    break;

                case 2:
                    buscarContrato();
                    break;

                case 3:
                    listarContratos();
                    break;

                case 4:
                    finalizarContrato();
                    break;

                case 5:
                    cancelarContrato();
                    break;

                case 0:
                    System.out.println("Retornando...");
                    break;

                default:
                    System.out.println("Opção inválida");
            }

        } while (opcao != 0);
    }

    public void registrarAluguel() {

        System.out.println("\n=== REGISTRAR ALUGUEL ===");

        System.out.print("ID do cliente: ");
        int idCliente = ValidaEntrada.lerInteiro(scanner);

        Usuario usuario = sistema.buscarUsuario(idCliente);

        if (!(usuario instanceof Cliente)) {
            System.out.println("Cliente não encontrado");
            
        } else{

            Cliente cliente = (Cliente) usuario;

            if (sistema.clientePossuiMultaPendente(cliente.getId())) {
                System.out.println("Cliente possui multa pendente e não pode realizar novos aluguéis");
                
            } else{

                System.out.print("ID do item: ");
                int idItem = ValidaEntrada.lerInteiro(scanner);

                Item item = sistema.buscarItem(idItem);

                if (item == null) {
                    System.out.println("Item não encontrado");
                   
                } else{

                    if (!item.estaDisponivel()) {
                        System.out.println("Item indisponível para aluguel. Status atual: " + item.getStatus());
                    } else{

                        System.out.print("Data de retirada (AAAA-MM-DD): ");
                        String dataRetirada = ValidaEntrada.lerData(scanner);

                        System.out.print("Data de devolução prevista (AAAA-MM-DD): ");
                        String dataDevolucao = ValidaEntrada.lerData(scanner);

                        LocalDate retirada = LocalDate.parse(dataRetirada);
                        LocalDate devolucao = LocalDate.parse(dataDevolucao);

                        long quantidadeDias = ChronoUnit.DAYS.between(retirada, devolucao);

                        if (quantidadeDias <= 0) {
                            System.out.println("A data de devolução deve ser posterior à data de retirada");
                        
                        } else {

                            double valorTotal = quantidadeDias * item.getTaxaDiaria();
                            int idContrato = sistema.gerarProximoIdContrato();

                            ContratoAluguel contrato = new ContratoAluguel(idContrato, cliente, item, dataRetirada, dataDevolucao, valorTotal);

                            if (sistema.cadastrarContrato(contrato)) {

                                item.alugar();

                                System.out.println("\n=== ALUGUEL REGISTRADO COM SUCESSO ===");
                                System.out.println("Contrato ID:   " + idContrato);
                                System.out.println("Cliente:       " + cliente.getNome());
                                System.out.println("Item:          " + item.getNome());
                                System.out.printf("Período:       %d dia(s)%n", quantidadeDias);
                                System.out.printf("Taxa diária:   R$ %.2f%n", item.getTaxaDiaria());
                                System.out.printf("Valor total:   R$ %.2f%n", valorTotal);

                            } else {
                                System.out.println("Erro ao registrar aluguel");
                            }
                        }
                    }
                }
            }
        }
    }

    public void buscarContrato() {

        System.out.print("ID do contrato: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        ContratoAluguel contrato = sistema.buscarContrato(id);

        if (contrato == null) {
            System.out.println("Contrato não encontrado");
        }else {

            exibirDetalhesContrato(contrato);
        }
    }

    public void listarContratos() {

        List<ContratoAluguel> contratos = sistema.listarContratos();

        System.out.println("\n===== CONTRATOS CADASTRADOS =====");

        if (contratos.isEmpty()) {
            System.out.println("Nenhum contrato cadastrado");
        } else {

            System.out.printf("%-5s %-22s %-22s %-12s %-10s%n", "ID", "CLIENTE", "ITEM", "STATUS", "VALOR");
            System.out.println("----------------------------------------------------------------------------------------");

            for (ContratoAluguel contrato : contratos) {
                System.out.printf("%-5d %-22s %-22s %-12s R$%.2f%n",contrato.getId(), contrato.getCliente().getNome(), contrato.getItem().getNome(), contrato.getStatus(),contrato.getValorTotal());
            }
        }
    }

    public void finalizarContrato() {

        System.out.print("ID do contrato: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        ContratoAluguel contrato = sistema.buscarContrato(id);

        if (contrato == null) {
            System.out.println("Contrato não encontrado");
            
        } else{

            if (!contrato.estaAtivo() || contrato.estaFinalizado()) {
                System.out.println("Este contrato não está ativo. Status: " + contrato.getStatus());
                
            } else{

                System.out.print("Data real da devolução (AAAA-MM-DD): ");
                String dataDevolucaoReal = ValidaEntrada.lerData(scanner);

                LocalDate dataPrevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());
                LocalDate dataReal = LocalDate.parse(dataDevolucaoReal);

                contrato.setDataDevolucaoReal(dataDevolucaoReal);

                long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, dataReal);

                if (diasAtraso > 0) {

                    double multaPercentual = sistema.calcularMulta(diasAtraso, (contrato.getItem().getTaxaDiaria()));
                    double multa = multaPercentual;

                    contrato.setValorMulta(multa);
                    contrato.setMultaPaga(false);

                    System.out.println("\n=== MULTA POR ATRASO ===");
                    System.out.println("Dias de atraso:     " + diasAtraso);
                    System.out.printf("Multa fixa:         R$ %.2f%n", 10.0);
                    System.out.printf("Multa percentual:   R$ %.2f%n", multaPercentual);
                    System.out.printf("Multa total:        R$ %.2f%n", multa);

                } else {
                    System.out.println("Devolução dentro do prazo");
                }

                contrato.getItem().devolver();

                if (sistema.finalizarContrato(id)) {
                    System.out.println("Contrato finalizado. Item devolvido ao estoque");
                } else {
                    System.out.println("Erro ao finalizar contrato");
                }
            }
        }
    }

    public void cancelarContrato() {

        System.out.print("ID do contrato: ");
        int id = ValidaEntrada.lerInteiro(scanner);

        ContratoAluguel contrato = sistema.buscarContrato(id);

        if (contrato == null) {
            System.out.println("Contrato não encontrado");
        } else{

            if (!(contrato.getStatus().equals("FINALIZADO"))) {
                sistema.cancelarContrato(id);
                contrato.getItem().devolver();
                System.out.println("Contrato cancelado. Item devolvido ao estoque");
            } else {
                System.out.println("Erro ao cancelar contrato");
            }
        }
    }

    public void exibirDetalhesContrato(ContratoAluguel contrato) {

        System.out.println("\n===== DETALHES DO CONTRATO =====");
        System.out.println("ID:                 " + contrato.getId());
        System.out.println("Cliente:            " + contrato.getCliente().getNome());
        System.out.println("Item:               " + contrato.getItem().getNome());
        System.out.println("Data de retirada:   " + contrato.getDataRetirada());
        System.out.println("Devolução prevista: " + contrato.getDataDevolucaoPrevista());
        System.out.println("Devolução real:     " + (contrato.getDataDevolucaoReal() != null ? contrato.getDataDevolucaoReal() : "-"));
        System.out.println("Status:             " + contrato.getStatus());
        System.out.printf("Valor total:        R$ %.2f%n", contrato.getValorTotal());
        System.out.printf("Valor multa:        R$ %.2f%n", contrato.getValorMulta());
        System.out.println("Multa paga:         " + contrato.isMultaPaga());
    }
}