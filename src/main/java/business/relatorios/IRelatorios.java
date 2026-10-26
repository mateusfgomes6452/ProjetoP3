package business.relatorios;

import java.time.LocalDate;
import java.util.List;

import entidades.ContratoAluguel;
import entidades.Item;

public interface IRelatorios {

    // ---- Relatórios no console (menu em texto) ----

    public abstract void gerarRelatorioItensDisponiveis();

    public abstract void gerarHistoricoCliente(int idCliente);

    public abstract void gerarRelatorioItensAlugados();

    public abstract void gerarRelatorioFaturamento(String dataInicial, String dataFinal);

    // ---- Dados dos relatórios (usados pelas telas JavaFX) ----

    /** Itens com status DISPONIVEL. */
    public abstract List<Item> listarItensDisponiveis();

    /** Todos os contratos do cliente (lista vazia se não houver). */
    public abstract List<ContratoAluguel> listarHistoricoCliente(int idCliente);

    /** Contratos com status ATIVO (itens alugados no momento). */
    public abstract List<ContratoAluguel> listarContratosAtivos();

    /** Contratos cuja data de retirada está entre inicio e fim (inclusive). */
    public abstract List<ContratoAluguel> listarContratosPorPeriodo(LocalDate inicio, LocalDate fim);

    /** true se o contrato está ATIVO e a data prevista de devolução já passou. */
    public abstract boolean estaAtrasado(ContratoAluguel contrato);

    // ---- Exportação para CSV (pasta relatorios) ----

    public abstract void exportarItensDisponiveis(List<Item> itens);

    public abstract void exportarHistoricoCliente(List<ContratoAluguel> historico);

    public abstract void exportarItensAlugados(List<ContratoAluguel> contratosAtivos);

    public abstract void exportarFaturamento(List<ContratoAluguel> contratos, String dataInicial, String dataFinal);

}
