package business.relatorios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import business.interfaces.IGerenciamentoContratos;
import business.interfaces.IGerenciamentoItens;
import business.interfaces.IGerenciamentoUsuarios;
import entidades.ContratoAluguel;
import entidades.Item;
import entidades.Usuario;
import repositories.SalvaRelatorioFaturamento;
import repositories.SalvaRelatorioHistoricoCliente;
import repositories.SalvaRelatorioItensAlugados;
import repositories.SalvaRelatorioItensDisponiveis;

public class GeradorRelatorios implements IRelatorios {

    private IGerenciamentoItens gerenciamentoItens;
    private IGerenciamentoContratos gerenciamentoContratos;
    private IGerenciamentoUsuarios gerenciamentoUsuarios;

    public GeradorRelatorios(IGerenciamentoItens gerenciamentoItens, IGerenciamentoContratos gerenciamentoContratos, IGerenciamentoUsuarios gerenciamentoUsuarios) {
        this.gerenciamentoItens = gerenciamentoItens;
        this.gerenciamentoContratos = gerenciamentoContratos;
        this.gerenciamentoUsuarios = gerenciamentoUsuarios;

    }

    @Override
    public void gerarRelatorioItensDisponiveis() {

        List<Item> itensDisponiveis = new ArrayList<>();

        for (Item item : gerenciamentoItens.listarItens()) {

            if (item.estaDisponivel()) {
                itensDisponiveis.add(item);

            }

        }
        System.out.println("\n===== RELATÓRIO DE ITENS DISPONÍVEIS =====");

            System.out.printf("%-20s %-5s %-25s %-10s%n","CATEGORIA", "ID", "NOME", "TAXA");

            for (Item item : itensDisponiveis) {
                System.out.printf("%-20s %-5d %-25s R$ %.2f%n",item.getCategoria().getNome(),item.getId(), item.getNome(), item.getTaxaDiaria());

            }

        System.out.println("\nArquivo CSV gerado na pasta relatorios");

        SalvaRelatorioItensDisponiveis salva = new SalvaRelatorioItensDisponiveis();

        salva.salvar(itensDisponiveis);

    }

    @Override
    public void gerarHistoricoCliente(int idCliente) {

        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(idCliente);

        if (usuario == null) {

            System.out.println("Cliente não encontrado");
        } else {
            
            List<ContratoAluguel> historico = new ArrayList<>();

            for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

                if (contrato.getCliente().getId() == idCliente) {
                    
                    historico.add(contrato);
                }

            }

            if (historico.isEmpty()) {

                System.out.println("O cliente não possui contratos");

            } else {
                System.out.println("\n===== HISTÓRICO DE ALUGUÉIS =====");

                System.out.println("Cliente: " + usuario.getNome());

                System.out.printf("%-5s %-20s %-12s %-12s %-12s %-10s %-10s%n","ID","ITEM","RETIRADA","PREVISTA","STATUS","VALOR","MULTA");
                
                for (ContratoAluguel contrato : historico) {

                    System.out.printf("%-5d %-20s %-12s %-12s %-12s %-10.2f %-10.2f%n", contrato.getId(), contrato.getItem().getNome(), contrato.getDataRetirada(),
                        contrato.getDataDevolucaoPrevista(), contrato.getStatus(), contrato.getValorTotal(), contrato.getValorMulta());
                    }

                System.out.println("\nArquivo CSV gerado na pasta relatorios");
                SalvaRelatorioHistoricoCliente salva = new SalvaRelatorioHistoricoCliente();
                
                salva.salvar(historico);

            }

        }
    }

    @Override
    public void gerarRelatorioItensAlugados() {

        List<ContratoAluguel> contratosAtivos = new ArrayList<>();

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            if (contrato.estaAtivo()) {
                contratosAtivos.add(contrato);

            }

        }

        if (contratosAtivos.isEmpty()) {

            System.out.println("Não existem contratos ativos.");
        } else {

            System.out.println("\n===== ITENS ALUGADOS ATUALMENTE =====");

            System.out.printf("%-5s %-20s %-20s %-15s %-10s%n","ID","CLIENTE","ITEM","DEVOLUÇÃO","SITUAÇÃO");

            LocalDate hoje = LocalDate.now();

            for (ContratoAluguel contrato : contratosAtivos) {

                LocalDate dataPrevista = LocalDate.parse(contrato.getDataDevolucaoPrevista());

                String situacao;

                if (hoje.isAfter(dataPrevista)) {

                    situacao = "ATRASADO";

                } else {

                    situacao = "EM DIA";

                }

                System.out.printf("%-5d %-20s %-20s %-15s %-10s%n",contrato.getId(),contrato.getCliente().getNome(),contrato.getItem().getNome(),contrato.getDataDevolucaoPrevista(),situacao);
            }

            SalvaRelatorioItensAlugados salva = new SalvaRelatorioItensAlugados();

            salva.salvar(contratosAtivos);
        }

    }

    @Override
    public void gerarRelatorioFaturamento(String dataInicial, String dataFinal) {
        LocalDate inicio;
        LocalDate fim;

        try {

            inicio = LocalDate.parse(dataInicial);
            fim = LocalDate.parse(dataFinal);

        } catch (Exception e) {

            System.out.println(
                    "Formato de data inválido.");

            return; //perguntar a jackson se pode usar isso

        }

        List<ContratoAluguel> contratosPeriodo = new ArrayList<>();

        double totalAlugueis = 0;
        double totalMultas = 0;

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            LocalDate dataContrato = LocalDate.parse(contrato.getDataRetirada());

            if (!dataContrato.isBefore(inicio) && !dataContrato.isAfter(fim)) {

                contratosPeriodo.add(contrato);

                totalAlugueis += contrato.getValorTotal();

                totalMultas += contrato.getValorMulta();

            }

        }

        if (contratosPeriodo.isEmpty()) {
            System.out.println( "Nenhum contrato encontrado no período.");

        } else{

            System.out.println("\n===== RELATÓRIO DE FATURAMENTO =====");

            System.out.println("Período: " + dataInicial + " até "  + dataFinal);

            System.out.println();

            System.out.printf( "%-5s %-20s %-12s %-12s%n","ID", "CLIENTE", "ALUGUEL",  "MULTA");

            for (ContratoAluguel contrato : contratosPeriodo) {

                System.out.printf("%-5d %-20s %-12.2f %-12.2f%n", contrato.getId(), contrato.getCliente().getNome(), contrato.getValorTotal(), contrato.getValorMulta());

            }

            System.out.println();

            System.out.println("Quantidade de contratos: " + contratosPeriodo.size());

            System.out.printf("Total de aluguéis: R$ %.2f%n", totalAlugueis);

            System.out.printf("Total de multas: R$ %.2f%n",totalMultas);

            System.out.printf("Total geral: R$ %.2f%n", totalAlugueis + totalMultas);

            SalvaRelatorioFaturamento salva = new SalvaRelatorioFaturamento();

            salva.salvar(contratosPeriodo, dataInicial, dataFinal);
        }

    }

}