package business.relatorios;

import java.time.LocalDate;
import java.time.ZoneId;
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
import repositories.SalvaRelatorioPdf;

@SuppressWarnings("java:S106")
public class GeradorRelatorios implements IRelatorios {

    private final IGerenciamentoItens gerenciamentoItens;
    private final IGerenciamentoContratos gerenciamentoContratos;
    private final IGerenciamentoUsuarios gerenciamentoUsuarios;

    public GeradorRelatorios(IGerenciamentoItens gerenciamentoItens, IGerenciamentoContratos gerenciamentoContratos, IGerenciamentoUsuarios gerenciamentoUsuarios) {
        this.gerenciamentoItens = gerenciamentoItens;
        this.gerenciamentoContratos = gerenciamentoContratos;
        this.gerenciamentoUsuarios = gerenciamentoUsuarios;

    }

    @Override
    public void gerarRelatorioItensDisponiveis() {

        List<Item> itensDisponiveis = listarItensDisponiveis();

        System.out.println("\n===== RELATÓRIO DE ITENS DISPONÍVEIS =====");

            System.out.printf("%-20s %-5s %-25s %-10s%n","CATEGORIA", "ID", "NOME", "TAXA");

            for (Item item : itensDisponiveis) {
                System.out.printf("%-20s %-5d %-25s R$ %.2f%n",item.getCategoria() == null ? "Sem categoria" : item.getCategoria().getNome(),item.getId(), item.getNome(), item.getTaxaDiaria());

            }

        System.out.println("\nArquivo CSV gerado na pasta relatorios");

        exportarItensDisponiveis(itensDisponiveis);

    }

    @Override
    public void gerarHistoricoCliente(int idCliente) {

        Usuario usuario = gerenciamentoUsuarios.buscarUsuario(idCliente);

        if (usuario == null) {

            System.out.println("Cliente não encontrado");
        } else {
            
            List<ContratoAluguel> historico = listarHistoricoCliente(idCliente);

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
                exportarHistoricoCliente(historico);

            }

        }
    }

    @Override
    public void gerarRelatorioItensAlugados() {

        List<ContratoAluguel> contratosAtivos = listarContratosAtivos();

        if (contratosAtivos.isEmpty()) {

            System.out.println("Não existem contratos ativos.");
        } else {

            System.out.println("\n===== ITENS ALUGADOS ATUALMENTE =====");

            System.out.printf("%-5s %-20s %-20s %-15s %-10s%n","ID","CLIENTE","ITEM","DEVOLUÇÃO","SITUAÇÃO");

            for (ContratoAluguel contrato : contratosAtivos) {

                String situacao;

                if (estaAtrasado(contrato)) {

                    situacao = "ATRASADO";

                } else {

                    situacao = "EM DIA";

                }

                System.out.printf("%-5d %-20s %-20s %-15s %-10s%n",contrato.getId(),contrato.getCliente().getNome(),contrato.getItem().getNome(),contrato.getDataDevolucaoPrevista(),situacao);
            }

            exportarItensAlugados(contratosAtivos);
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

        List<ContratoAluguel> contratosPeriodo = listarContratosPorPeriodo(inicio, fim);

        double totalAlugueis = 0;
        double totalMultas = 0;

        for (ContratoAluguel contrato : contratosPeriodo) {
            totalAlugueis += contrato.getValorTotal();
            totalMultas += contrato.getValorMulta();
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

            exportarFaturamento(contratosPeriodo, dataInicial, dataFinal);
        }

    }

    // =========================================================================
    // DADOS DOS RELATÓRIOS (usados pelas telas JavaFX)
    // =========================================================================

    @Override
    public List<Item> listarItensDisponiveis() {

        List<Item> itensDisponiveis = new ArrayList<>();

        for (Item item : gerenciamentoItens.listarItens()) {

            if (item.estaDisponivel()) {
                itensDisponiveis.add(item);
            }
        }

        return itensDisponiveis;
    }

    @Override
    public List<ContratoAluguel> listarHistoricoCliente(int idCliente) {

        List<ContratoAluguel> historico = new ArrayList<>();

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            if (contrato.getCliente().getId() == idCliente) {
                historico.add(contrato);
            }
        }

        return historico;
    }

    @Override
    public List<ContratoAluguel> listarContratosAtivos() {

        List<ContratoAluguel> contratosAtivos = new ArrayList<>();

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            if (contrato.estaAtivo()) {
                contratosAtivos.add(contrato);
            }
        }

        return contratosAtivos;
    }

    @Override
    public List<ContratoAluguel> listarContratosPorPeriodo(LocalDate inicio, LocalDate fim) {

        List<ContratoAluguel> contratosPeriodo = new ArrayList<>();

        for (ContratoAluguel contrato : gerenciamentoContratos.listarContratos()) {

            LocalDate dataContrato = LocalDate.parse(contrato.getDataRetirada());

            if (!dataContrato.isBefore(inicio) && !dataContrato.isAfter(fim)) {
                contratosPeriodo.add(contrato);
            }
        }

        return contratosPeriodo;
    }

    @Override
    public boolean estaAtrasado(ContratoAluguel contrato) {

        if (!contrato.estaAtivo()) {
            return false;
        }

        return LocalDate.now(ZoneId.systemDefault()).isAfter(LocalDate.parse(contrato.getDataDevolucaoPrevista()));
    }

    // =========================================================================
    // EXPORTAÇÃO PARA CSV
    // =========================================================================

    @Override
    public void exportarItensDisponiveis(List<Item> itens) {
        new SalvaRelatorioItensDisponiveis().salvar(new ArrayList<>(itens));
    }

    @Override
    public void exportarHistoricoCliente(List<ContratoAluguel> historico) {
        new SalvaRelatorioHistoricoCliente().salvar(historico);
    }

    @Override
    public void exportarItensAlugados(List<ContratoAluguel> contratosAtivos) {
        new SalvaRelatorioItensAlugados().salvar(contratosAtivos);
    }

    @Override
    public void exportarFaturamento(List<ContratoAluguel> contratos, String dataInicial, String dataFinal) {
        new SalvaRelatorioFaturamento().salvar(contratos, dataInicial, dataFinal);
    }

    // ---- Exportação para PDF ----

    @Override
    public void exportarPdfItensDisponiveis(List<Item> itens) {
        new SalvaRelatorioPdf().salvarItensDisponiveis(new ArrayList<>(itens));
    }

    @Override
    public void exportarPdfHistoricoCliente(List<ContratoAluguel> historico) {
        new SalvaRelatorioPdf().salvarHistoricoCliente(new ArrayList<>(historico));
    }

    @Override
    public void exportarPdfItensAlugados(List<ContratoAluguel> contratosAtivos) {
        new SalvaRelatorioPdf().salvarItensAlugados(new ArrayList<>(contratosAtivos));
    }

    @Override
    public void exportarPdfFaturamento(List<ContratoAluguel> contratos, String dataInicial, String dataFinal) {
        new SalvaRelatorioPdf().salvarFaturamento(new ArrayList<>(contratos), dataInicial, dataFinal);
    }

}
